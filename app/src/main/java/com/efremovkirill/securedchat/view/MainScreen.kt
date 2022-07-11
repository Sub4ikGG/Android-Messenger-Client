package com.efremovkirill.securedchat.view

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.efremovkirill.securedchat.R
import com.efremovkirill.securedchat.connection.ClientConnection
import com.efremovkirill.securedchat.data.ChatOptions
import com.efremovkirill.securedchat.data.ConnectionData
import com.efremovkirill.securedchat.navigation.Screen
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import java.nio.charset.StandardCharsets

private var clientConnection: ClientConnection? = null

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val chatId = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    val init = remember { mutableStateOf(false) }
    val receiving = remember { mutableStateOf(false) }

    if (!init.value) {
        println("Receiving messages from MainScreen")
        init.value = true

        CoroutineScope(IO).launch {
            delay(1500)
            clientConnection = ClientConnection.getClientConnection()
            receiving.value = true

            val receiveChannel = clientConnection?.getReceiveChannel()
            delay(1000)
            while (receiving.value) {
                delay(500)
                receiveChannel?.readAvailable {
                    val receivedMessage = StandardCharsets.UTF_8.decode(it).toString()
                    println(receivedMessage)
                    launch(Main) {
                        if (receivedMessage.contains("cn:")) {
                            val data = receivedMessage.split(" ", limit = 1)
                            val chatNickname = data[0].replace("cn:", "")

                            navController.navigate(
                                Screen.Chat.passData(
                                    chatId = chatId.value,
                                    chatNickname = chatNickname,
                                    jsonChatOptions = data[1]
                                )
                            )
                            receiving.value = false
                        }
                        else if(receivedMessage == "kk") {

                        }
                        else {
                            Toast.makeText(
                                context,
                                "Cannot connect to this chat.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black)
    ) {
        Row(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = "Secured chat",
                style = TextStyle(
                    color = Color(248, 248, 248),
                    fontSize = 42.sp,
                    fontFamily = FontFamily(Font(R.font.sfprodisplaylight))
                )
            )
            if(!receiving.value) {
                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp), color = Color.White)
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f).padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = chatId.value,
                    onValueChange = {
                        chatId.value = if(it.length < 16) it else chatId.value
                    },
                    label = {
                        Text(
                            "Please, enter chat-ID for connection",
                            fontFamily = FontFamily(Font(R.font.sfprodisplaylight))
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color(28, 28, 28), cursorColor = Color(248, 248, 248),
                        unfocusedIndicatorColor = Color.Transparent, focusedIndicatorColor = Color.Transparent,
                        focusedLabelColor = Color(139, 139, 141), unfocusedLabelColor = Color.Gray
                    ),
                    textStyle = TextStyle(
                        color = Color(218, 218, 218),
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.sfprodisplaylight))
                    )
                )
                TextField(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    value = password.value,
                    onValueChange = {
                        password.value = it
                    },
                    label = {
                        Text(
                            "Chat-ID password (optional)",
                            fontFamily = FontFamily(Font(R.font.sfprodisplaylight))
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color(28, 28, 28), cursorColor = Color(248, 248, 248),
                        unfocusedIndicatorColor = Color.Transparent, focusedIndicatorColor = Color.Transparent,
                        focusedLabelColor = Color(139, 139, 141), unfocusedLabelColor = Color.Gray
                    ),
                    textStyle = TextStyle(
                        color = Color(218, 218, 218),
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.sfprodisplaylight))
                    )
                )
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                ) {
                    val createChatClicked = remember { mutableStateOf(false) }
                    if (createChatClicked.value) {
                        createChatClicked.value = false
                        navController.navigate(Screen.CreateChat.route)
                        receiving.value = false
                    }

                    ModButton(
                        text = "Create new chat",
                        buttonColor = Color(28, 28, 28),
                        shapeDp = 10.dp,
                        textSize = 16.sp,
                        textColor = Color(218, 218, 218),
                        clicked = createChatClicked
                    )
                    AnimatedVisibility(chatId.value.length >= 4) {
                        val connectClicked = remember { mutableStateOf(false) }
                        if (connectClicked.value) {
                            connectClicked.value = false

                            val gson = Gson()
                            val connectionData = ConnectionData(chatId.value, password.value)
                            CoroutineScope(IO).launch {
                                clientConnection?.write("/connect ${gson.toJson(connectionData)}")
                            }
                        }

                        ModButton(
                            modifier = Modifier.padding(top = 4.dp),
                            text = "Connect to ${chatId.value}",
                            buttonColor = Color(28, 28, 28),
                            shapeDp = 10.dp,
                            textSize = 16.sp,
                            textColor = Color(218, 218, 218),
                            clicked = connectClicked
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = "https://github.com/Sub4ikGG",
                style = TextStyle(
                    color = Color(139, 139, 141),
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.sfprodisplaylight)),
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen(rememberNavController())
}