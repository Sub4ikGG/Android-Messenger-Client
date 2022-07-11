package com.efremovkirill.securedchat.view

import android.view.MotionEvent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.efremovkirill.securedchat.R

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ModButton(
    text: String,
    buttonColor: Color = Color.LightGray,
    shapeDp: Dp,
    textColor: Color = Color.Black,
    textSize: TextUnit,
    clicked: MutableState<Boolean>? = null,
    modifier: Modifier = Modifier
) {
    val selected = remember { mutableStateOf(false) }
    val pressScale = animateFloatAsState(if (selected.value) 0.9f else 1f)

    Card(
        modifier = modifier.scale(scale = pressScale.value),
        shape = RoundedCornerShape(shapeDp),
        elevation = 5.dp
    ) {
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInteropFilter {
                    when (it.action) {
                        MotionEvent.ACTION_DOWN -> {
                            clicked?.value = true
                            selected.value = true
                        }

                        MotionEvent.ACTION_UP -> {
                            //clicked?.value = false
                            selected.value = false
                        }
                    }
                    true
                },
            onClick = { },
            colors = ButtonDefaults.buttonColors(backgroundColor = buttonColor),
            shape = RoundedCornerShape(shapeDp),
            elevation = null
        ) {
            Text(
                text = text,
                fontFamily = FontFamily(Font(R.font.sfprodisplaylight)),
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontSize = textSize
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ModButtonPreview() {
    ModButton(
        text = "Какое-то действие",
        buttonColor = Color.Transparent,
        shapeDp = 15.dp,
        textSize = 16.sp,
    )
}