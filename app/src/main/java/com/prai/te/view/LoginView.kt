package com.prai.te.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun LoginView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFFFFFFF))
    ) {
        Text(
            text = "로그인하기",
            fontSize = 20.sp,
            color = Color(0xFFFFFFFF),
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.Center)
                .background(color = Color(0xFF003322), shape = RoundedCornerShape(10.dp))
                .padding(horizontal = 20.dp, vertical = 10.dp)
        )
    }
}

@Preview
@Composable
private fun LoginPreview() {
    LoginView()
}