package com.efremovkirill.securedchat.navigation

import com.efremovkirill.securedchat.data.ChatOptions

sealed class Screen(val route: String) {
    object Main: Screen(route = "main_screen")
    object CreateChat: Screen(route = "create_chat_screen")
    object Chat: Screen(route = "chat_screen/{chat_id}/{chat_nickname}/{jsonChatOptions}") {
        fun passData(chatId: String, chatNickname: String, jsonChatOptions: String): String {
            return "chat_screen/$chatId/$chatNickname/$jsonChatOptions"
        }
    }
}
