import request from "@/utils/request.js"

export function getEssayList(page, size, uid, status) {
    let url = '/essay?currentPage=' + page + '&pageSize=' + size
    if (uid) url += '&userId=' + uid
    if (status) url += '&status=' + status
    return request.get(url)
}

export const addEssay = (data) => request.post('/essay', data)

export function auditEssay(id, status, auditUid, remark) {
    var p = new URLSearchParams()
    p.append('status', status)
    if (auditUid) p.append('auditUserId', auditUid)
    if (remark) p.append('auditRemark', remark)
    return request.put('/essay/' + id + '/audit?' + p.toString())
}

export const removeEssay = (id) => request.delete(`/essay/${id}`)

