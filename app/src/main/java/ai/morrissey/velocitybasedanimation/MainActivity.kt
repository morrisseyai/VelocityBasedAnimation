package ai.morrissey.velocitybasedanimation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ai.morrissey.velocitybasedanimation.ui.theme.VelocityBasedAnimationTheme
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VelocityBasedAnimationTheme(darkTheme = true) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen(mainViewModel: MainViewModel = MainViewModel()) {
    val articles = mainViewModel.newsArticles.collectAsState().value
    val lazyListState = rememberLazyListState()
    val velocityTrackingFlingBehavior = rememberVelocityTrackingFlingBehavior()

    val normalizedVelocity by remember {
        derivedStateOf {
            normalizeVelocity(velocityTrackingFlingBehavior.currentVelocity.value)
        }
    }
    val getCurrentVelocity: () -> Float = { normalizedVelocity }

    Column(modifier = Modifier.fillMaxSize()) {
        val showInfoRow by remember { mutableStateOf(true) }
        if (showInfoRow) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val velocity =
                    remember { derivedStateOf { velocityTrackingFlingBehavior.currentVelocity.value } }
                Text(text = "Velocity: ${velocity.value}", color = Color.Red)
                val scrollInProgress =
                    remember { derivedStateOf { velocityTrackingFlingBehavior.currentVelocity.value != 0f } }
                if (scrollInProgress.value) {
                    Text(text = "Scroll in progress", color = Color.Green)
                }
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = lazyListState,
            flingBehavior = velocityTrackingFlingBehavior
        ) {
            items(articles) { newsArticle ->
                Article(newsArticle = newsArticle, getCurrentVelocity = getCurrentVelocity)
            }
        }
    }
}

@Preview
@Composable
fun DefaultPreview() {
    VelocityBasedAnimationTheme(darkTheme = true) {
        MainScreen()
    }
}

@Composable
fun Article(newsArticle: NewsArticle, getCurrentVelocity: () -> Float) {
    val animateDuration = velocityBasedFadeInDuration(getCurrentVelocity())
    val initialValue = 0f
    val animatedAlpha = remember { Animatable(initialValue = initialValue) }
    LaunchedEffect(Unit) {
        animatedAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(animateDuration)
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .graphicsLayer { alpha = animatedAlpha.value }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(start = 8.dp, end = 2.dp, top = 8.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = newsArticle.headline, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text(text = newsArticle.subhead, fontSize = 14.sp)
            }
            Box(modifier = Modifier
                .padding(start = 4.dp, end = 8.dp)
                .size(72.dp)
                .background(color = Color.DarkGray))
        }
    }
}

fun normalizeVelocity(velocity: Float): Float {
    return when (if (velocity < 0) velocity * -1 else velocity) {
        in 0f..5000f -> 0f
        in 5001f..10000f -> 1f
        in 10001f..15000f -> 2f
        in 15001f..20000f -> 3f
        in 20001f..30000f -> 4f
        in 30001f..40000f -> 5f
        in 40001f..50000f -> 6f
        in 50001f..60000f -> 7f
        in 60001f..70000f -> 8f
        in 70001f..80000f -> 9f
        else -> 10f
    }
}

fun velocityBasedFadeInDuration(normalizedScrollVelocity: Float): Int {
    return when (normalizedScrollVelocity) {
        0f -> 600
        1f -> 500
        2f -> 400
        3f -> 300
        4f -> 200
        5f -> 100
        6f -> 50
        7f -> 30
        8f -> 15
        9f -> 10
        else -> 5
    }
}
