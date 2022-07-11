package com.efremovkirill.securedchat.view

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.efremovkirill.securedchat.connection.ClientConnection
import com.efremovkirill.securedchat.data.Message
import com.efremovkirill.securedchat.R
import com.efremovkirill.securedchat.data.ChatOptions
import kotlinx.coroutines.*
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

private var clientConnection: ClientConnection? = null

@Composable
fun ChatScreen(
    chatId: String,
    chatNickname: String,
    jsonChatOptions: ChatOptions?,
    navController: NavController
) {
    val scaffoldState = rememberScaffoldState()
    val messages = remember {
        mutableStateListOf(Message("Mr. S.", "Welcome to the $chatId, $chatNickname!", System.currentTimeMillis() / 1000L))
    }

    val receiving = remember { mutableStateOf(false) }
    val init = remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (!init.value) {
        println("Receiving messages from ChatScreen")
        init.value = true
        receiving.value = true

        CoroutineScope(Dispatchers.IO).launch {
            clientConnection = ClientConnection.getClientConnection()
            delay(500)
            while(receiving.value) {
                delay(500)
                clientConnection?.getReceiveChannel()?.readAvailable {
                    val receivedMessage = StandardCharsets.UTF_8.decode(it).toString()
                    println("Received message 1: $receivedMessage")
                    if (receivedMessage.contains(":") && !receivedMessage.contains("cn:")) {
                        val sender = receivedMessage.split(": ")[0]
                        val content = receivedMessage.split(": ")[1]

                        val message = Message(sender, content, System.currentTimeMillis())
                        messages.add(message)
                    } else if (receivedMessage == "removed") {
                        launch(Dispatchers.Main) {
                            Toast.makeText(context, "Chat destroyed.", Toast.LENGTH_SHORT).show()
                            receiving.value = false
                            navController.popBackStack()
                        }
                    } else if (receivedMessage.isEmpty() || receivedMessage == "leaved") receiving.value = false
                }
            }
        }
    }
    Scaffold(
        scaffoldState = scaffoldState,

        topBar = {
            TopAppBar(
                backgroundColor = Color(28, 28, 28)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.padding().size(32.dp).clickable {
                            receiving.value = false
                            CoroutineScope(Dispatchers.IO).launch {
                                clientConnection?.write("leaved")
                            }
                            navController.popBackStack()
                        },
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back button",
                        tint = Color.LightGray
                    )
                    Text(
                        text = chatId,
                        style = TextStyle(
                            color = Color(248, 248, 248),
                            fontSize = 20.sp,
                            fontFamily = FontFamily(Font(R.font.sfprodisplaylight)),
                        )
                    )
                    Icon(
                        modifier = Modifier.padding().size(32.dp),
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Chat information button",
                        tint = Color.LightGray
                    )
                }
            }
        },

        content = {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black)
                    .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 65.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    reverseLayout = true
                ) {
                    items(messages.reversed()) { message ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (message.sender == chatNickname) Arrangement.End else Arrangement.Start,
                        ) {
                            MessageCloud(message)
                        }
                    }
                }
            }
        },

        bottomBar = {
            val message = remember { mutableStateOf("") }
            Card(
                modifier = Modifier.padding(8.dp).height(53.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row {
                    TextField(
                        modifier = Modifier.fillMaxWidth(0.85f).background(Color.Transparent),
                        value = message.value,
                        onValueChange = {
                            message.value = it
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Transparent, cursorColor = Color(248, 248, 248),
                            unfocusedIndicatorColor = Color.Transparent, focusedIndicatorColor = Color.Transparent,
                            focusedLabelColor = Color.Black, unfocusedLabelColor = Color.Gray
                        ),
                        textStyle = TextStyle(
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.sfprodisplaylight))
                        ),
                        placeholder = {
                            Text(
                                text = "Write your message here",
                                style = TextStyle(
                                    color = Color.Black,
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(Font(R.font.sfprodisplaylight))
                                )
                            )
                        }
                    )
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            modifier = Modifier.size(32.dp).clickable {
                                CoroutineScope(Dispatchers.IO).launch {
                                    if (message.value.isNotEmpty()) {
                                        messages.add(Message(chatNickname, message.value, System.currentTimeMillis()))

                                        clientConnection?.write("/sendmessage ${message.value}")
                                        message.value = ""
                                    }
                                }
                            },
                            imageVector = Icons.Default.Send,
                            tint = Color.Black,
                            contentDescription = "Send message button"
                        )
                    }
                }
            }
        }
    )

    BackHandler {
        receiving.value = false
        CoroutineScope(Dispatchers.IO).launch {
            clientConnection?.write("leaved")
        }
        navController.popBackStack()
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    ChatScreen("Chat", "Nickname", null, rememberNavController())
}

@SuppressLint("SimpleDateFormat")
fun getDateTime(s: String): String {
    val simpleDate = SimpleDateFormat("hh:mm", Locale.KOREA)

    return simpleDate.format(Date(s.toLong() * 1000))
}