package com.example.composechatapp.Screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.example.composechatapp.ChatViewModel
import com.example.composechatapp.CommonProfileImage
import com.example.composechatapp.R
import java.util.Calendar
import java.util.concurrent.TimeUnit

enum class State {
    INITIAL, ACTIVE, COMPLETED
}

@Composable
fun SingleStatusScreen(navController: NavController, viewModel: ChatViewModel, userId: String) {
    val statusList = viewModel.statusList.value.filter {
        it.user.userId == userId && withinLast24Hours(it.timeStamp ?: 0L)
    }

    if (statusList.isNotEmpty()) {
        val currentStatus = remember { mutableIntStateOf(0) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {

            StatusImage(
                imageUrl = statusList[currentStatus.intValue].imageUrl,
                placeholderResId = R.drawable.placeholder,
                errorResId = R.drawable.placeholder,
                modifier = Modifier.fillMaxSize(), // Optional
                contentDescription = "Image Description", // Optional
                contentScale = ContentScale.Fit // Optional
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                statusList.forEachIndexed { index, status ->


                    CustomProgressIndicator(
                        modifier = Modifier
                            .weight(1f)
                            .height(7.dp)
                            .padding(1.dp),
                        state = if (currentStatus.intValue < index) State.INITIAL else if (currentStatus.intValue == index) State.ACTIVE else State.COMPLETED
                    ) {
                        if (currentStatus.intValue < statusList.size - 1) {
                            currentStatus.intValue++
                        } else {
                            navController.popBackStack()
                        }
                    }


                }


            }


        }
    }
}

@Composable
fun CustomProgressIndicator(modifier: Modifier, state: State, onComplete: () -> Unit) {
    var progress = if (state == State.INITIAL) 0f else 1f
    if (state == State.ACTIVE) {
        val toggleState = remember { mutableStateOf(false) }

        LaunchedEffect(toggleState) {
            toggleState.value = true
        }

        val p: Float by animateFloatAsState(
            targetValue = if (toggleState.value) 1f else 0f,
            animationSpec = tween(5000),
            finishedListener = { onComplete.invoke() }, label = ""
        )
        progress = p

    }

    LinearProgressIndicator(
        modifier = modifier,
        color = Color.Red,
        progress = progress
    )

}

private fun withinLast24Hours(timeStamp: Long): Boolean {
    val currentTime = Calendar.getInstance().timeInMillis
    val difference = currentTime - timeStamp
    val differenceHours = TimeUnit.MILLISECONDS.toHours(difference)
    return differenceHours <= 24
}


@Composable
fun StatusImage(
    imageUrl: String?,
    placeholderResId: Int,
    errorResId: Int,
    modifier: Modifier = Modifier,
    contentDescription: String?,
    contentScale: ContentScale = ContentScale.Fit
) {
    Box(
        modifier = modifier
    ) {
        if (imageUrl != null) {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current).data(data = imageUrl)
                        .apply(block = fun ImageRequest.Builder.() {
                            placeholder(placeholderResId)
                            error(errorResId)
                        }).build()
                ),
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                painter = painterResource(id = placeholderResId),
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                tint = Color.Gray
            )
        }
    }
}



