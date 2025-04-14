package com.prai.te.view.call

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prai.te.R
import com.prai.te.common.MainColor
import com.prai.te.common.MainFont
import com.prai.te.common.MainTimeUtil
import com.prai.te.common.cleanClickable
import com.prai.te.common.textDp
import com.prai.te.model.MainEvent
import com.prai.te.retrofit.MainConversation
import com.prai.te.retrofit.MainConversationMeta
import com.prai.te.view.model.MainViewModel

@Composable
internal fun ConversationListView(model: MainViewModel = viewModel()) {
    val conversationList = model.chatList.collectAsStateWithLifecycle()
    val selectedId = model.selectedConversationId.collectAsStateWithLifecycle()
    BackHandler {
        model.closeChatList()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF000000))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .align(Alignment.TopCenter)
        ) {
            Image(
                painter = painterResource(R.drawable.main_button_back),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 20.dp)
                    .align(Alignment.CenterStart)
                    .size(24.dp)
                    .cleanClickable {
                        if (selectedId.value == null) {
                            model.closeChatList()
                        } else {
                            model.selectedConversationId.value = null
                        }
                    }
            )
            val title = if (selectedId.value != null) {
                val conversation = conversationList.value.find {
                    it.id == selectedId.value
                }
                if (conversation == null) {
                    "대화 기록"
                } else {
                    MainTimeUtil.isoToDateCustom(conversation.createdAt)
                }
            } else {
                "대화 기록"
            }
            Text(
                text = title,
                fontFamily = MainFont.Pretendard,
                fontSize = 18.textDp,
                lineHeight = 25.textDp,
                fontWeight = FontWeight(600),
                color = Color(0xFFFFFFFF),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        if (conversationList.value.isEmpty()) {
            EmptyScreen()
        } else {
            Crossfade(
                targetState = selectedId.value,
                label = "conversation_list_cross_fade",
                modifier = Modifier.align(Alignment.TopCenter)
            ) { selectedId ->
                if (selectedId == null) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .padding(top = 60.dp)
                            .align(Alignment.TopCenter)
                            .padding(20.dp)
                    ) {
                        items(conversationList.value.size) { index ->
                            ConversationTitleItem(conversationList.value[index]) {
                                val itemId = conversationList.value[index].id
                                model.dispatchEvent(MainEvent.ConversationOpen(itemId))
                                model.selectedConversationId.value = itemId
                            }
                        }
                    }
                } else {
                    ConversationRoomView(selectedId)
                }
            }
        }
    }
}

@Preview
@Composable
private fun EmptyScreen() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(top = 60.dp, bottom = 80.dp)
            .fillMaxSize()
            .background(color = Color(0xFF000000))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(R.drawable.main_icon_broken_egg),
                contentDescription = null,
                modifier = Modifier.padding(bottom = 30.dp)
            )
            Text(
                text = "첫 대화를 기다리고 있어요!",
                fontSize = 18.textDp,
                lineHeight = 23.textDp,
                fontFamily = MainFont.Pretendard,
                fontWeight = FontWeight(600),
                color = MainColor.Greyscale11WH,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "지금 PRAI와 영어로\n가볍게 통화해 보세요!",
                fontSize = 16.textDp,
                lineHeight = 22.textDp,
                fontFamily = MainFont.Pretendard,
                fontWeight = FontWeight(400),
                color = MainColor.Greyscale10BK,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Composable
private fun ConversationTitleItem(meta: MainConversationMeta, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .cleanClickable { onClick.invoke() }
            .border(
                width = 0.5.dp,
                color = Color(0xFF424242),
                shape = RoundedCornerShape(size = 16.dp)
            )
            .fillMaxWidth()
            .background(color = Color(0xFF191919), shape = RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Text(
            text = MainTimeUtil.isoToFullCustom(meta.createdAt),
            fontSize = 16.textDp,
            fontFamily = MainFont.Pretendard,
            lineHeight = 22.textDp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight(600),
            color = Color(0xFFFFFFFF)
        )
        Spacer(
            modifier = Modifier
                .padding(horizontal = 6.dp)
                .background(color = Color(0xFF727272))
                .size(3.dp)
                .clip(shape = CircleShape)
        )
        Text(
            text = MainTimeUtil.secondToMinuteString(meta.duration),
            fontSize = 16.textDp,
            fontFamily = MainFont.Pretendard,
            lineHeight = 22.textDp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight(600),
            color = Color(0xFFFFFFFF)
        )
    }
}

@Preview
@Composable
private fun ConversationRoomView(id: String = "test", model: MainViewModel = viewModel()) {
    val messages = model.getConversationFlow(id).collectAsStateWithLifecycle()
    BackHandler {
        model.selectedConversationId.value = null
    }

    LazyColumn(
        modifier = Modifier
            .padding(top = 60.dp)
            .fillMaxSize()
            .background(color = Color(0xFF000000))
            .padding(20.dp)
    ) {
        items(messages.value?.messages ?: emptyList()) { message ->
            if (message.speaker == "ai") {
                AiChatItem(message)
            } else if (message.speaker == "user") {
                UserChatItem(message)
            }
        }
    }
}

@Composable
private fun AiChatItem(message: MainConversation = MockConversation) {
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .padding(bottom = 26.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = message.text,
            fontSize = 16.sp,
            fontFamily = MainFont.Pretendard,
            lineHeight = 22.4.sp,
            fontWeight = FontWeight(400),
            color = Color(0xFFD2D2D2),

            textAlign = TextAlign.Start,
            modifier = Modifier
                .background(
                    color = Color(0xFF191919),
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 16.dp,
                        bottomStart = 16.dp,
                        bottomEnd = 16.dp
                    )
                )
                .weight(1f, fill = false)
                .widthIn(max = 276.dp)
                .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp)
        )
        Text(
            text = MainTimeUtil.isoToTimeCustom(message.timestamp),
            fontSize = 12.sp,
            fontFamily = MainFont.Pretendard,
            lineHeight = 16.8.sp,
            fontWeight = FontWeight(400),
            color = Color(0xFF868686),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(start = 6.dp)
        )
    }
}

@Composable
private fun UserChatItem(message: MainConversation = MockConversation) {
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .padding(bottom = 26.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = MainTimeUtil.isoToTimeCustom(message.timestamp),
            fontSize = 12.sp,
            lineHeight = 16.8.sp,
            fontFamily = MainFont.Pretendard,
            fontWeight = FontWeight(400),
            color = Color(0xFF868686),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(end = 6.dp)
        )
        Text(
            text = message.text.trim(),
            fontSize = 16.sp,
            fontFamily = MainFont.Pretendard,
            lineHeight = 22.4.sp,
            fontWeight = FontWeight(400),
            color = Color(0xFF000000),
            textAlign = TextAlign.Start,
            modifier = Modifier
                .background(
                    color = Color(0xFFB9B9B9),
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 0.dp,
                        bottomStart = 16.dp,
                        bottomEnd = 16.dp
                    )
                )
                .widthIn(max = 276.dp)
                .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp)
        )
    }
}

private val MockConversation =
    MainConversation("test", "2025-03-31T15:16:50.503000+09:00", "test", "test", "test")