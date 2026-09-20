import request from "@/utils/request.js"

var prefix = '/other-achievement'

export function getOtherPage(page, size, uid, status) {
    let url = `${prefix}?currentPage=${page}&pageSize=${size}`
    if (uid) url += `&userId=${uid}`
    if (status) url += `&status=${status}`
    return request.get(url)
}

export const saveOther = (data) => request.post(prefix, data)

export function auditOther(id, status, auditUid, remark) {
    let qs = new URLSearchParams()
    qs.append('status', status)
    if (auditUid) qs.append('auditUserId', auditUid)
    if (remark) qs.append('auditRemark', remark)
    return request.put(`${prefix}/${id}/audit?${qs}`)
}

export const deleteOther = id => request.delete(`${prefix}/${id}`)

