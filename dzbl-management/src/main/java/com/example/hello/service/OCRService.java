package com.example.hello.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class OCRService {
    
    @Value("${baidu.ocr.api-key}")
    private String apiKey;
    
    @Value("${baidu.ocr.secret-key}")
    private String secretKey;
    
    // 缓存access token
    private String accessToken;
    private long tokenExpireTime;
    
    private synchronized String getAccessToken() throws Exception {
        // 如果token未过期，直接返回缓存的token
        if (accessToken != null && System.currentTimeMillis() < tokenExpireTime) {
            return accessToken;
        }
        
        String url = "https://aip.baidubce.com/oauth/2.0/token";
        // 使用URLEncoder对参数进行编码
        String param = String.format("grant_type=client_credentials&client_id=%s&client_secret=%s",
            URLEncoder.encode(apiKey.trim(), StandardCharsets.UTF_8),
            URLEncoder.encode(secretKey.trim(), StandardCharsets.UTF_8));
        
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url + "?" + param);
            try (CloseableHttpResponse response = client.execute(post)) {
                String result = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = JSON.parseObject(result);
                
                // 更新token和过期时间
                accessToken = jsonObject.getString("access_token");
                // token有效期默认30天，这里设置为29天以确保安全
                tokenExpireTime = System.currentTimeMillis() + 29L * 24 * 60 * 60 * 1000;
                
                return accessToken;
            }
        }
    }
    
    public Map<String, Object> extractText(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return createErrorResponse("请提供有效的文件");
        }

        try {
            String fileName = file.getOriginalFilename();
            if (fileName == null) {
                return createErrorResponse("文件名不能为空");
            }

            // 检查文件类型
            if (isPDF(fileName)) {
                return extractPDFText(file);
            } else if (isImage(fileName)) {
                return extractImageText(file);
            } else {
                return createErrorResponse("不支持的文件格式，仅支持PDF和图片格式(PNG、JPG、BMP)");
            }
        } catch (Exception e) {
            return createErrorResponse("文件处理失败: " + e.getMessage());
        }
    }

    private Map<String, Object> extractPDFText(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            List<Map<String, Object>> allResults = new ArrayList<>();
            int totalWords = 0;

            // 遍历每一页PDF
            for (int page = 0; page < document.getNumberOfPages(); page++) {
                // 将PDF页面渲染为图片
                BufferedImage image = pdfRenderer.renderImageWithDPI(page, 300); // 300 DPI for good quality
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "png", baos);
                byte[] imageBytes = baos.toByteArray();

                // 对图片进行OCR识别
                Map<String, Object> pageResult = extractImageText(imageBytes);
                if ((Boolean) pageResult.get("success")) {
                    allResults.add(pageResult);
                    totalWords += (Integer) pageResult.get("words_result_num");
                }
            }

            // 合并所有页面的结果
            Map<String, Object> finalResult = new HashMap<>();
            finalResult.put("success", true);
            finalResult.put("total_pages", document.getNumberOfPages());
            finalResult.put("total_words", totalWords);
            finalResult.put("page_results", allResults);
            return finalResult;
        }
    }

    private Map<String, Object> extractImageText(MultipartFile file) throws IOException {
        return extractImageText(file.getBytes());
    }

    private Map<String, Object> extractImageText(byte[] imageBytes) {
        try {
            String accessToken = getAccessToken();
            String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic?access_token=" + accessToken;
            
            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            String encodedImage = URLEncoder.encode(base64, StandardCharsets.UTF_8);
            
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpPost post = new HttpPost(url);
                post.setHeader("Content-Type", "application/x-www-form-urlencoded");
                post.setEntity(new StringEntity("image=" + encodedImage));
                
                try (CloseableHttpResponse response = client.execute(post)) {
                    String result = EntityUtils.toString(response.getEntity());
                    JSONObject jsonResult = JSON.parseObject(result);
                    
                    if (jsonResult.containsKey("error_code")) {
                        return createErrorResponse("OCR识别失败：" + jsonResult.getString("error_msg"));
                    }
                    
                    Map<String, Object> responseMap = new HashMap<>();
                    responseMap.put("success", true);
                    responseMap.put("words_result", jsonResult.getJSONArray("words_result"));
                    responseMap.put("words_result_num", jsonResult.getInteger("words_result_num"));
                    return responseMap;
                }
            }
        } catch (Exception e) {
            return createErrorResponse("OCR处理失败: " + e.getMessage());
        }
    }

    private boolean isPDF(String fileName) {
        return fileName.toLowerCase().endsWith(".pdf");
    }

    private boolean isImage(String fileName) {
        String lower = fileName.toLowerCase();
        return lower.endsWith(".png") || lower.endsWith(".jpg") || 
               lower.endsWith(".jpeg") || lower.endsWith(".bmp");
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", message);
        return response;
    }
} 