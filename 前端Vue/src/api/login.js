import request from "@/utils/request.js"

export const loginApi = (data) => request.post('/user/login', data)


export function doRegister(data) {
  return request.post('/user/register', data)
}
