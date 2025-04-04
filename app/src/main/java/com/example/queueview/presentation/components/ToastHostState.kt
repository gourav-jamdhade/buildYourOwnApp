package com.example.queueview.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class ToastHostState {
    private val _message = mutableStateOf<String?>(null)
    val message: State<String?> get() = _message

    fun showToast(msg: String) {
        _message.value = msg
    }

    fun clear() {
        _message.value = null
    }
}

@Composable
fun ToastHost(toastHostState: ToastHostState) {
    val message = toastHostState.message.value

    if (message != null) {
        LaunchedEffect(message) {
            delay(5000)
            toastHostState.clear()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = message,
                    color = Color.Black,
                    fontSize = 16.sp
                )
            }
        }
    }
}
