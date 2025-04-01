package com.prai.te.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prai.te.common.cleanClickable
import com.prai.te.model.MainEvent
import com.prai.te.view.model.MainViewModel
import kotlinx.coroutines.launch

@Composable
internal fun RecordView(model: MainViewModel = viewModel()) {
    var isRecording by remember { mutableStateOf(false) }

    val segmentItems = model.segmentItemList.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val targetEvent = if (isRecording) MainEvent.RecordStop else MainEvent.RecordStart
    val text = if (isRecording) "전송하기" else "녹음하기"

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .padding(vertical = 200.dp, horizontal = 50.dp)
                .fillMaxWidth()
                .height(500.dp)
                .align(Alignment.Center)
                .background(color = Color(0xCCFFFFFF), shape = RoundedCornerShape(15.dp))
                .padding(10.dp)
        ) {
            items(segmentItems.value) { item ->
                Text(
                    item.text,
                    color = Color(0xFF3322FF),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Start,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(horizontal = 5.dp, vertical = 5.dp)
                        .background(color = Color(0xFFAAAAAA), shape = RoundedCornerShape(10.dp))
                        .cleanClickable {
                            scope.launch { model.event.emit(MainEvent.PlayStart(item.path)) }
                        }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                )
            }
        }
        Text(
            text,
            color = Color.White,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(bottom = 100.dp)
                .align(Alignment.BottomCenter)
                .background(color = Color(0xFFAAAAAA), shape = RoundedCornerShape(10.dp))
                .cleanClickable {
                    isRecording = isRecording.not()
                    scope.launch { model.event.emit(targetEvent) }
                }
                .padding(horizontal = 20.dp, vertical = 10.dp)
        )
        Text(
            "구글로그인",
            color = Color.White,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(bottom = 40.dp)
                .align(Alignment.BottomCenter)
                .background(color = Color(0xFFAAAAAA), shape = RoundedCornerShape(10.dp))
                .cleanClickable {
                    scope.launch {
                        model.event.emit(MainEvent.GoogleLoginRequest)
                    }
                }
                .padding(horizontal = 20.dp, vertical = 10.dp)
        )

        Text(
            "로그아웃",
            color = Color.White,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(bottom = 300.dp)
                .align(Alignment.BottomCenter)
                .background(color = Color(0xFFAAAAAA), shape = RoundedCornerShape(10.dp))
                .cleanClickable {
                    scope.launch {
                        model.event.emit(MainEvent.LogoutRequest)
                    }
                }
                .padding(horizontal = 20.dp, vertical = 10.dp)
        )
    }
}



@Preview
@Composable
private fun RecordPreview() {
    RecordView()
}