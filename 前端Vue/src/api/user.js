import request from "@/utils/request.js"

export const getUserInfoApi = (id) => request.get(`/user/${id}`)

export function changePwd(id, oldPwd, newPwd) {
    return request.put(`/user/${id}/password`, {
        oldPassword: oldPwd,
        newPassword: newPwd
    })
}

export const updateUserInfo = (id, info) => request.put('/user/' + id, info)

