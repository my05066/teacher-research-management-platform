import request from "@/utils/request.js";

export const searchApi = (page, size, userId, status) => {
    let url = `/program?currentPage=${page}&pageSize=${size}`
    if (userId) url += `&userId=${userId}`
    if (status) url += `&status=${status}`
    return request.get(url)
};

export const addApi = (program) => request.post('/program', program);

export function auditProgram(id, status, auditUserId, auditRemark) {
    var params = new URLSearchParams();
    params.append('status', status);
    if (auditUserId) params.append('auditUserId', auditUserId);
    if (auditRemark) params.append('auditRemark', auditRemark);
    return request.put(`/program/${id}/audit?${params.toString()}`);
}

// 结题
export const submitCompletionApi = (id, docPath, docName, docSize) =>
    request.put(`/program/${id}/completion?completionDocPath=${docPath}&completionDocName=${docName}&completionDocSize=${docSize}`);

export const deleteApi = (id) => request.delete(`/program/${id}`);

