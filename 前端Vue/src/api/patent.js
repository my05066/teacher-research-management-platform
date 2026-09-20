import request from "@/utils/request.js"

export function queryPatentList(page, size, uid, status) {
    var url = '/patent?currentPage=' + page + '&pageSize=' + size
    if (uid) url += '&userId=' + uid
    if (status) url += '&status=' + status
    return request.get(url)
}

export const addPatent = (data) => request.post('/patent', data)

export const auditPatent = (id, status, auditUserId, remark) => {
    let p = new URLSearchParams()
    p.append('status', status)
    if (auditUserId) p.append('auditUserId', auditUserId)
    if (remark) p.append('auditRemark', remark)
    return request.put(`/patent/${id}/audit?` + p.toString())
}

export function delPatent(id) { return request.delete('/patent/' + id) }

