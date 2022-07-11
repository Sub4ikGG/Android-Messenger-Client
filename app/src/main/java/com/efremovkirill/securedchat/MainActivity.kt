package com.efremovkirill.securedchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.efremovkirill.securedchat.navigation.SetupNavGraph
import com.efremovkirill.securedchat.ui.theme.SecuredChatTheme

class MainActivity : ComponentActivity() {
    lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SecuredChatTheme {
                navController = rememberNavController()

                Surface(color = MaterialTheme.colors.background) {
                    SetupNavGraph(navController)
                }
            }
        }
    }
}