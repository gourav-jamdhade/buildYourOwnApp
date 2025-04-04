package com.example.queueview.presentation.screens

import android.util.Log
import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import com.example.queueview.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun SplashScreen(onNavigate: () -> Unit) {
    //Animating the logo

    val scale = remember {
        Animatable(0f)
    }

    LaunchedEffect(key1 = true) {
        Log.d("SplashScreen", "Animation started")
        scale.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 1000, easing = {
            OvershootInterpolator(2f).getInterpolation(it)
        }))
        Log.d("SplashScreen", "Animation completed")
        delay(3000L)
        Log.d("SplashScreen", "Delay completed")
        try {
            withContext(Dispatchers.Main) {
                onNavigate()
            }
            Log.d("SplashScreen", "onNavigate() called successfully")
        } catch (e: Exception) {
            Log.e("Navigation Error", "Error navigating to MainScreen From Splash: ${e.message}")
        }

    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.scale(scale.value)
        )

//        Text(
//            buildAnnotatedString {
//                withStyle(
//                    style = SpanStyle(
//                        Color(0xfff57f4d),
//                        fontSize = 55.sp
//                    )
//                ) {
//                    append("Q")
//                }
//                append("ueue")
//                withStyle(
//                    style = SpanStyle(
//                        Color(0xfff57f4d),
//                        fontSize = 55.sp
//                    )
//                ) {
//                    append("V")
//                }
//                append("ue")
//            },
//            modifier = Modifier.scale(scale.value),
//            fontFamily = FontFamily.Cursive,
////            fontStyle = FontStyle.Italic,
//            fontWeight = FontWeight.ExtraBold,
//            fontSize = 45.sp,
//            color = Color(0xff4f84d3)
//        )
    }
}
