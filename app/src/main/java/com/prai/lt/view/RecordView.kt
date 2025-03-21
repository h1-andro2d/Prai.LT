package com.prai.lt.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import com.prai.lt.common.cleanClickable
import com.prai.lt.model.MainEvent
import com.prai.lt.view.model.MainViewModel
import kotlinx.coroutines.launch

@Composable
internal fun RecordView(model: MainViewModel = viewModel()) {
    var isRecording by remember { mutableStateOf(false) }

    val recordPath = model.recordPathList.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val targetEvent = if (isRecording) MainEvent.RecordStop else MainEvent.RecordStart
    val text = if (isRecording) "중지하기" else "녹음하기"

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text,
            color = Color.White,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .background(color = Color(0xFFAAAAAA), shape = RoundedCornerShape(10.dp))
                .cleanClickable {
                    isRecording = isRecording.not()
                    scope.launch { model.event.emit(targetEvent) }
                }
                .padding(horizontal = 20.dp, vertical = 10.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(recordPath.value) { path ->
                Text(
                    path.takeLast(30),
                    color = Color(0xFF3322FF),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(horizontal = 30.dp, vertical = 5.dp)
                        .background(color = Color(0xFFAAAAAA), shape = RoundedCornerShape(10.dp))
                        .cleanClickable {
                            isRecording = isRecording.not()
                            scope.launch { model.event.emit(MainEvent.PlayStart(path)) }
                        }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun RecordPreview() {
    RecordView()
}