import request from "@/utils/request.js";

export function getMeetingPage(page, size, uid, status) {
    var url = `/meeting?currentPage=${page}&pageSize=${size}`
    if (uid) url += '&userId=' + uid;
    if (status) url += '&status=' + status;
    return request.get(url);
}

export const addMeeting = (data) => request.post('/meeting', data)

// 审核
export const auditMeeting = (id, st, auditUid, remark) => {
    let params = new URLSearchParams();
    params.append('status', st);
    if (auditUid) params.append('auditUserId', auditUid);
    if (remark) params.append('auditRemark', remark);
    return request.put(`/meeting/${id}/audit?${params.toString()}`);
}


export function removeMeeting(id) {
    return request.delete('/meeting/' + id)
}

