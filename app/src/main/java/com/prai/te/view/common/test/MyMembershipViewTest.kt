package com.prai.te.view.common.test

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prai.te.view.model.MainViewModel

@Preview(showBackground = true)
@Composable
internal fun MyMembershipViewTest(model: MainViewModel = viewModel()) {
    val itemList = model.billingItems.collectAsStateWithLifecycle()
    val state = model.billingState.collectAsStateWithLifecycle()
    val isPremiumUser = model.isPremiumUser.collectAsStateWithLifecycle()
    val expireAt = model.premiumExpiresTime.collectAsStateWithLifecycle()

    BackHandler {
        model.isBillingVisible.value = false
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF000000))
    ) {
        Text(
            "뒤로가기",
            color = Color(0xFF000000),
            modifier = Modifier
                .clickable {
                    model.isBillingVisible.value = false
                }
                .padding(bottom = 20.dp)
                .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(20.dp))
                .padding(10.dp)
        )
        Text(
            "GooglePlay 접속 상태 : ${state.value}",
            color = Color(0xFF000000),
            modifier = Modifier
                .padding(bottom = 20.dp)
                .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(20.dp))
                .padding(10.dp)
        )
        Text(
            "구독 취소하기",
            color = Color(0xFF000000),
            modifier = Modifier
                .padding(bottom = 20.dp)
                .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(20.dp))
                .padding(10.dp)
        )
        Text(
            "isPremium : ${isPremiumUser.value}",
            color = Color(0xFF000000),
            modifier = Modifier
                .padding(bottom = 20.dp)
                .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(20.dp))
                .padding(10.dp)
        )
        Text(
            "expirateAt : ${expireAt.value}",
            color = Color(0xFF000000),
            modifier = Modifier
                .padding(bottom = 20.dp)
                .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(20.dp))
                .padding(10.dp)
        )
        if (itemList.value.isEmpty()) {
            Text(
                "NO ITEM",
                color = Color(0xFF000000),
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(20.dp))
                    .padding(10.dp)
            )
        }
        LazyColumn {
            items(itemList.value) { item ->
                Text(
                    item,
                    color = Color(0xFF000000),
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .clickable {
//                            model.dispatchEvent(MainEvent.BillingItemClick(item))
                        }
                        .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(20.dp))
                        .padding(10.dp)
                )
            }
        }
    }
}