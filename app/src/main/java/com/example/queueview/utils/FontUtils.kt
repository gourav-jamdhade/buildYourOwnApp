package com.example.queueview.utils

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.queueview.R


class FontUtils{
    private val fontFamily = FontFamily(
        Font(R.font.lexend_deca, FontWeight.Normal, FontStyle.Normal),
        Font(R.font.lexend_deca_thin, FontWeight.Thin, FontStyle.Normal),
        Font(R.font.lexend_deca_light, FontWeight.Light, FontStyle.Normal),
        Font(R.font.lexend_deca_medium, FontWeight.Medium, FontStyle.Normal),
        Font(R.font.lexend_deca_semibold, FontWeight.SemiBold, FontStyle.Normal)
    )


    fun getFontFamily(): FontFamily {
        return fontFamily
    }
}
