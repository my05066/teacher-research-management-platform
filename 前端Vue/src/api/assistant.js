import request from "@/utils/request.js"


export function askAssistant(q) {
  return request.post("/ai/ask", { question: q })
}

export const getChatHistory = () => request.get("/ai/chat-history")

export const clearChatHistory = () => request.delete("/ai/chat-history")