package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.entity.MedicalFile;
import com.example.hello.service.MedicalFileService;
import com.example.hello.utils.OSSUtils;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/medical-files")
public class MedicalFileController {
    
    @Autowired
    private MedicalFileService medicalFileService;
    
    @Autowired
    private OSSUtils ossUtils;
    
    @GetMapping("/all")
    public Result<List<MedicalFile>> getAllFiles() {
        return Result.success(medicalFileService.getAllFiles());
    }
    
    @GetMapping
    public Result<PageInfo<MedicalFile>> getFilesByPage(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer reportId,
            @RequestParam(required = false) String moduleType,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return Result.success(medicalFileService.getFilesByPage(userId, reportId, moduleType, page, pageSize));
    }
    
    @GetMapping("/{fileId}")
    public Result<MedicalFile> getFileById(@PathVariable Integer fileId) {
        return Result.success(medicalFileService.getFileById(fileId));
    }
    
    @PostMapping
    public Result<MedicalFile> addFile(@RequestParam("file") MultipartFile file,
                                     @RequestParam("userId") Integer userId,
                                     @RequestParam("reportId") Integer reportId,
                                     @RequestParam("moduleType") String moduleType) {
        try {
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }
            
            // 上传文件到阿里云OSS
            String fileUrl = ossUtils.uploadFile(file);
            
            // 创建文件记录
            MedicalFile medicalFile = new MedicalFile();
            medicalFile.setUserId(userId);
            medicalFile.setReportId(reportId);
            medicalFile.setModuleType(moduleType);
            medicalFile.setFileUrl(fileUrl);
            
            medicalFileService.addFile(medicalFile);
            
            return Result.success(medicalFile);
            
        } catch (Exception e) {
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }
    
    @PutMapping("/{fileId}")
    public Result<?> updateFile(@PathVariable Integer fileId, @RequestBody MedicalFile file) {
        file.setFileId(fileId);
        medicalFileService.updateFile(file);
        return Result.success(null);
    }
    
    @DeleteMapping("/{fileId}")
    public Result<?> deleteFile(@PathVariable Integer fileId) {
        medicalFileService.deleteFile(fileId);
        return Result.success(null);
    }
    
    @GetMapping("/preview/{fileId}")
    public Result<String> previewFile(@PathVariable Integer fileId) {
        MedicalFile medicalFile = medicalFileService.getFileById(fileId);
        if (medicalFile == null) {
            return Result.error("文件未找到");
        }
        
        // 获取文件的可访问 URL
        String fileUrl = medicalFile.getFileUrl();
        
        // 返回标准Result格式的响应
        return Result.success(fileUrl);
    }
}