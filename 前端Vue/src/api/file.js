import request from "@/utils/request.js"


export function uploadApi(file) {
    var fd = new FormData()
    fd.append('file', file)
    return request.post('/file/upload', fd)
}

// 快速录入（OCR识别）
export function quickEntryApi(file) {
    var fd = new FormData()
    fd.append('file', file)
    return request.post('/upload/quick-entry', fd, {
        headers: { 'Content-Type': 'multipart/form-data' },
        timeout: 120000
    })
}

