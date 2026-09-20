import request from "@/utils/request.js"



export function getDashboardStats(uid) {
    var url = '/statistics/dashboard'
    if (uid) {
        var id = parseInt(uid)
        if (!isNaN(id)) url += '?userId=' + id
    }
    return request.get(url)
}

