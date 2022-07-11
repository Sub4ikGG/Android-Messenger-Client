package com.efremovkirill.securedchat.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.efremovkirill.securedchat.R
import com.efremovkirill.securedchat.data.Message

@Composable
fun MessageCloud(message: Message) {
    Card(
        modifier = Modifier.widthIn(64.dp, 256.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            //horizontalAlignment = Alignment.End
        ) {
            Text(
                modifier = Modifier.padding(top = 4.dp, start = 4.dp).fillMaxWidth(),
                text = message.sender + ":",
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontFamily = FontFamily(Font(R.font.sfprodisplaylight)),
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                modifier = Modifier.padding(start = 4.dp).fillMaxWidth(),
                text = message.content,
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.sfprodisplaylight)),
                    textAlign = TextAlign.Start
                )
            )
            Text(
                modifier = Modifier.padding(top = 8.dp, end = 4.dp, bottom = 4.dp)
                    .fillMaxWidth(),
                text = getDateTime(message.time.toString()),
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.sfprodisplaylight)),
                    textAlign = TextAlign.End
                )
            )
        }
    }
}

@Preview
@Composable
fun MessageCloudPreview() {
    MessageCloud(Message("Johny Cage", "Hello world!", 0))
}