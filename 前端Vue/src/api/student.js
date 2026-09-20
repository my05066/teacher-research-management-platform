import request from "@/utils/request.js"

const BASE = '/student-award'

export function listStudentAward(page, size, uid, status) {
    var url = BASE + '?currentPage=' + page + '&pageSize=' + size
    if (uid) url += '&userId=' + uid
    if (status) url += '&status=' + status
    return request.get(url)
}

export const addStudentAward = (data) => request.post(BASE, data)

export const auditStudentAward = (id, status, auditUid, remark) => {
    var p = new URLSearchParams()
    p.append('status', status)
    if (auditUid) p.append('auditUserId', auditUid)
    if (remark) p.append('auditRemark', remark)
    return request.put(BASE + '/' + id + '/audit?' + p.toString())
}


export const delStudentAward = (id) => request.delete(BASE + '/' + id)

