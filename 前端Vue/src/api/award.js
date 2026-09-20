import request from "@/utils/request.js"

export const fetchAwardPage = (page, size, userId, status) => {
    let url = `/award?currentPage=${page}&pageSize=${size}`;
    if (userId) url += `&userId=${userId}`
    if (status) url += `&status=${status}`
    return request.get(url)
}


export const addAward = (award) => request.post('/award', award)

export function doAuditAward(id, status, auditUid, remark) {
    var qs = new URLSearchParams()
    qs.append('status', status)
    if (auditUid) qs.append('auditUserId', auditUid)
    if (remark) qs.append('auditRemark', remark)
    return request.put('/award/' + id + '/audit?' + qs.toString())
}

export const deleteAward = (id) => request.delete(`/award/${id}`)

