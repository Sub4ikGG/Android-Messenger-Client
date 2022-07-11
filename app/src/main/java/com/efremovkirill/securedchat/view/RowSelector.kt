package com.efremovkirill.securedchat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RowSelector(modifier: Modifier, rowTitle: String, items: Array<String>, selected: MutableState<String>) {
    Column(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = rowTitle,
            style = TextStyle(
                color = Color(248, 248, 248),
                fontSize = 24.sp,
                fontFamily = FontFamily(Font(R.font.sfprodisplaylight)),
                textAlign = TextAlign.Center
            )
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items.forEach { item ->
                Card(modifier = Modifier, backgroundColor = Color(28, 28, 28)) {
                    Box(
                        modifier = Modifier.size(32.dp).clickable {
                            selected.value = item
                        },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item,
                            style = TextStyle(
                                color = if(selected.value != item) Color.Gray else Color.White,
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(R.font.sfprodisplaylight)),
                            )
                        )
                    }
                }
            }
        }
    }
}