export function isAdmin() {
  try {
    var info = JSON.parse(localStorage.getItem('userInfo'))
    return info && info.role === 'admin'
  } catch(e) {
    return false
  }
}

export function getUserId() {
  return localStorage.getItem('userId')
}

export function getUserInfo() {
  try {
    return JSON.parse(localStorage.getItem('userInfo'))
  } catch(_) {
    return null
  }
}

