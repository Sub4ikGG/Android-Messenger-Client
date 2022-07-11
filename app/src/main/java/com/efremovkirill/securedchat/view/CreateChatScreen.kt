package com.efremovkirill.securedchat.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.efremovkirill.securedchat.R
import com.efremovkirill.securedchat.RowSelector
import com.efremovkirill.securedchat.connection.ClientConnection
import com.efremovkirill.securedchat.data.ChatOptions
import com.efremovkirill.securedchat.navigation.Screen
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

private var clientConnection: ClientConnection? = null

@Composable
fun CreateChatScreen(navController: NavController) {
    val id = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val capacity = remember { mutableStateOf("2") }
    val lifetime = remember { mutableStateOf("~") }

    val saveAfterClearSwitch = remember { mutableStateOf(true) }
    val screenshotsSwitch = remember { mutableStateOf(true) }
    val copySwitch = remember { mutableStateOf(true) }

    if (clientConnection == null)
        CoroutineScope(Dispatchers.IO).launch {
            clientConnection = ClientConnection.getClientConnection()
        }

    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.padding(start = 8.dp).size(36.dp).clickable {
                    navController.popBackStack()
                },
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back button",
                tint = Color.White
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = "Create chat",
                style = TextStyle(
                    color = Color(248, 248, 248),
                    fontSize = 42.sp,
                    fontFamily = FontFamily(Font(R.font.sfprodisplaylight))
                )
            )
        }
        Box(
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = id.value.trim(),
                    onValueChange = {
                        id.value = it
                    },
                    label = {
                        Text(
                            "Enter here name of your chat",
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
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    value = password.value.trim(),
                    onValueChange = {
                        password.value = it
                    },
                    label = {
                        Text(
                            "Password for your chat (optional)",
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
                RowSelector(
                    modifier = Modifier.padding(top = 16.dp),
                    rowTitle = "Capacity",
                    items = arrayOf("2", "3", "4", "5", "6"),
                    selected = capacity
                )
                RowSelector(
                    modifier = Modifier.padding(top = 8.dp),
                    rowTitle = "Lifetime",
                    items = arrayOf("15m", "1h", "6h", "24h", "~"),
                    selected = lifetime
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier,
                        text = "Allow to save chat",
                        style = TextStyle(
                            color = Color(248, 248, 248),
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.sfprodisplaylight))
                        )
                    )
                    Switch(
                        checked = saveAfterClearSwitch.value,
                        onCheckedChange = { saveAfterClearSwitch.value = it }
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        modifier = Modifier,
                        text = "Allow message copy",
                        style = TextStyle(
                            color = Color(248, 248, 248),
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.sfprodisplaylight))
                        )
                    )
                    Switch(
                        checked = copySwitch.value,
                        onCheckedChange = { copySwitch.value = it })
                }

                val clicked = remember { mutableStateOf(false) }
                if (clicked.value) {
                    clicked.value = false

                    if (id.value.length < 5) {
                        Toast.makeText(
                            context,
                            "Chat-id length must be more than 5 characters",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (password.value.length in 1..5) {
                        Toast.makeText(
                            context,
                            "Password length must be more than 5 characters",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val chatOptions = ChatOptions(
                            id = id.value,
                            password = password.value.ifEmpty { "-1" },
                            capacity = capacity.value,
                            timer = if (lifetime.value == "~") "-1" else lifetime.value,
                            onlyInvite = false,
                            saveAfterClear = saveAfterClearSwitch.value,
                            screenshotsAllowed = screenshotsSwitch.value,
                            copyAllowed = copySwitch.value
                        )

                        val gson = Gson()
                        try {
                            CoroutineScope(Dispatchers.IO).launch {
                                clientConnection?.write("/createchat ${gson.toJson(chatOptions)}")
                                val answer = clientConnection?.receive()

                                println(answer)
                                launch(Main) {
                                    if (answer?.contains("cn:") == true) {
                                        navController.popBackStack()

                                        val data = answer.split(" ")
                                        val chatNickname = data[0].replace("cn:", "")

                                        navController.navigate(
                                            Screen.Chat.passData(
                                                chatId = id.value,
                                                chatNickname = chatNickname,
                                                jsonChatOptions = data[1]
                                            )
                                        )
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Server connection error.\nPlease try again.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Error: ${e.message}\nPlease try again..",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                ModButton(
                    modifier = Modifier.padding(top = 32.dp),
                    text = "Continue",
                    buttonColor = Color(248, 248, 248),
                    shapeDp = 10.dp,
                    textSize = 16.sp,
                    clicked = clicked
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateChatScreenPreview() {
    CreateChatScreen(rememberNavController())
}