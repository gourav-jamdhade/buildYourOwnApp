package com.example.queueview.presentation.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.queueview.data.model.ObservableQueueData
import com.example.queueview.presentation.components.SearchWithLocationAwareSuggestions
import com.example.queueview.presentation.viemodel.MainViewModel
import com.example.queueview.utils.FontUtils
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun NearbyLocationScreen(
    viewModel: MainViewModel,
    onAddDataClick: () -> Unit
) {
//    val context = LocalContext.current
//    DisposableEffect(Unit) {
//        onDispose {
//            WorkManagerHelper.stopPeriodicRefresh(context)
//        }
//    }

    LaunchedEffect(Unit) {
        viewModel.fetchAllQueues()
    }
    // Observe changes


    val queues by viewModel.queues.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var isRefreshing by remember { mutableStateOf(false) }
    val state = rememberPullToRefreshState()
    val onRefresh: () -> Unit = {
        isRefreshing = true
        scope.launch {
            // fetch something
            delay(5000)
            viewModel.fetchAllQueues()
            isRefreshing = false
        }
    }



    Scaffold(

        topBar = {
            SearchWithLocationAwareSuggestions(koinViewModel())
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddDataClick, containerColor = Color(0xFF14C18B),
            ) {
                Row(
                    modifier = Modifier
                        .wrapContentSize(align = Alignment.Center)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Data",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Contribute", color = Color.White, fontSize = 16.sp)

                }

            }
        })
    { padding ->

        if (isLoading && !isRefreshing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .wrapContentSize()
            ) {
                CircularProgressIndicator()
            }
        } else {
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier.wrapContentSize(),
                state = state,
                content = {
                    LazyColumn(
                        modifier = Modifier
                            .padding(padding),
                        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        if (queues.isEmpty()) {
                            items(viewModel.dummyQueueList.size) {
                                QueueCard(observableQueue = ObservableQueueData(viewModel.dummyQueueList[it]))
                            }
                        } else {
                            items(queues,
                                key = { it.queueData.id }) { observableQueue ->
                                QueueCard(observableQueue = observableQueue,
                                    onRemove = {
                                        scope.launch {
                                            viewModel.removeQueue(observableQueue.queueData.id)
                                            snackbarHostState.showSnackbar(
                                                "${observableQueue.queueData.placeName} removed"
                                            )
                                        }
                                    })
                            }
                        }

                    }
                }
            )
        }
    }

}


@Composable

fun QueueCard(observableQueue: ObservableQueueData, onRemove: () -> Unit = {}) {
    val currentWaitTime by remember {
        derivedStateOf { observableQueue.currentWaitTime }
    }


    Log.d("QueueCard", "currentWaitTime: ${observableQueue.queueData.lastUpdated.seconds}")

    var isVisible by remember { mutableStateOf(true) }

    AnimatedVisibility(
        visible = isVisible && currentWaitTime > 0,
        exit = fadeOut() + shrinkVertically()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            border = BorderStroke(
                width = 2.dp, color = (if (currentWaitTime <= 5) {
                    Color(0xFFD21E1E)
                } else if (currentWaitTime in 5..30) {
                    Color(0xFF24BF28)
                } else {
                    Color(0xFF1470C1)
                })
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.CenterStart
            ) {
                Column(
                    Modifier
                        .fillMaxWidth(0.7f)
                        .fillMaxHeight()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        observableQueue.queueData.placeName,
                        style = TextStyle(
                            fontFamily = FontUtils().getFontFamily(),
                            fontSize = 20.sp
                        ),
                        fontWeight = FontWeight.SemiBold,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Type: ${observableQueue.queueData.placeType}",
                        style = TextStyle(
                            fontFamily = FontUtils().getFontFamily(),
                            fontSize = 16.sp
                        ),
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )

                }

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.3f)
                        .background(
                            color = (if (currentWaitTime <= 5) {
                                Color(0xFFD21E1E)
                            } else if (currentWaitTime in 5..30) {
                                Color(0xFF24BF28)
                            } else {
                                Color(0xFF1470C1)
                            })
                        )
                        .align(Alignment.CenterEnd),
                    contentAlignment = Alignment.Center,

                    ) {
                    Text(
                        "$currentWaitTime min",
                        style = TextStyle(
                            fontFamily = FontUtils().getFontFamily(),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 24.sp,
                            color = Color.White
                        ),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(8.dp)
                    )
                }

            }
        }

    }


    LaunchedEffect(currentWaitTime) {
        if (currentWaitTime <= 0) {
            isVisible = false
            delay(300)
            onRemove()
        }
    }
}