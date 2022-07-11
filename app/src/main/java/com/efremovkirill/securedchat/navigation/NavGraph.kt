package com.efremovkirill.securedchat.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.efremovkirill.securedchat.view.CreateChatScreen
import com.efremovkirill.securedchat.data.ChatOptions
import com.efremovkirill.securedchat.view.ChatScreen
import com.efremovkirill.securedchat.view.MainScreen
import com.google.gson.Gson

@Composable
fun SetupNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(
            route = Screen.Main.route
        ) {
            MainScreen(navController)
        }
        composable(
            route = Screen.CreateChat.route
        ) {
            CreateChatScreen(navController)
        }
        composable(
            route = Screen.Chat.route,
            arguments = listOf(
                navArgument("chat_id") {
                    type = NavType.StringType
                },
                navArgument("chat_nickname") {
                    type = NavType.StringType
                },
                navArgument("jsonChatOptions") {
                    type = NavType.StringType
                }
            )
        ) {
            val gson = Gson()
            val chatId = it.arguments?.getString("chat_id")
            val chatNickname = it.arguments?.getString("chat_nickname")
            val jsonChatOptions = it.arguments?.getString("jsonChatOptions")

            ChatScreen(chatId = chatId!!, chatNickname = chatNickname!!, jsonChatOptions = gson.fromJson(jsonChatOptions, ChatOptions::class.java), navController = navController)
        }
    }
}