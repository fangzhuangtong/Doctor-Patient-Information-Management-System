// 解析JWT令牌
function parseJwt(token) {
  try {
    // 获取载荷部分（payload）
    const base64Url = token.split('.')[1]
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
    }).join(''))

    return JSON.parse(jsonPayload)
  } catch (error) {
    console.error('解析JWT令牌失败:', error)
    return null
  }
}

// 保存令牌到本地存储
export function setToken(token) {
  localStorage.setItem('token', token)
  
  // 解析并保存用户信息
  const payload = parseJwt(token)
  if (payload) {
    localStorage.setItem('username', payload.sub)
    localStorage.setItem('role', payload.role)
    
    // 根据角色保存对应的ID
    switch (payload.role) {
      case 'doctor':
        localStorage.setItem('doctorId', payload.doctorId)
        break
      case 'patient':
        localStorage.setItem('userId', payload.userId)
        break
      case 'admin':
        localStorage.setItem('adminId', payload.adminId)
        break
    }
  }
}

// 从本地存储获取令牌
export function getToken() {
  return localStorage.getItem('token')
}

// 从本地存储获取用户名
export function getUsername() {
  return localStorage.getItem('username')
}

// 从本地存储获取用户ID
export function getUserId() {
  const role = localStorage.getItem('role')
  switch (role) {
    case 'doctor':
      return localStorage.getItem('doctorId')
    case 'patient':
      return localStorage.getItem('userId')
    case 'admin':
      return localStorage.getItem('adminId')
    default:
      return null
  }
}

// 从本地存储获取用户角色
export function getRole() {
  return localStorage.getItem('role')
}

// 清除所有认证信息
export function removeToken() {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  localStorage.removeItem('role')
  localStorage.removeItem('doctorId')
  localStorage.removeItem('userId')
  localStorage.removeItem('adminId')
} 
 