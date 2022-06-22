package me.lioironzello.yahtzee.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowLeft
import androidx.compose.material.icons.outlined.ArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import me.lioironzello.yahtzee.R

// Utility class for code reusability
data class PageInfo(val video: Int?, val text: List<Int>)

@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
fun Tutorial() {
    val pagerState = rememberPagerState() // State to control the current page in the Pager
    val scope = rememberCoroutineScope()

    // List of PageInfo
    val pages = listOf(
        PageInfo(null, listOf(R.string.tutorial_one)),
        PageInfo(R.drawable.clip1, listOf(R.string.tutorial_two)),
        PageInfo(R.drawable.clip2, listOf(R.string.tutorial_three)),
        PageInfo(
            R.drawable.clip3,
            listOf(R.string.tutorial_four)
        ),
        PageInfo(
            null, listOf(
                R.string.tutorial_categories,
                R.string.tutorial_one_to_six,
                R.string.tutorial_tris_poker,
                R.string.tutorial_full,
                R.string.tutorial_small_straight,
                R.string.tutorial_large_straight,
                R.string.tutorial_yahtzee,
                R.string.tutorial_chance,
                R.string.tutorial_bonus
            )
        )
    )

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Tutorial") },
            navigationIcon = {
                IconButton(onClick = { ScreenRouter.navigateTo(Screens.Home) }) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
                }
            },
            backgroundColor = MaterialTheme.colors.background,
            elevation = 0.dp
        )
        HorizontalPager(
            pages.size,
            state = pagerState,
            modifier = Modifier.weight(1f, true)
        ) { page ->
            Page(pageInfo = pages[page])
        }
        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        )
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (pagerState.currentPage == 0) { // Don't show the button if we're on the first page
                Spacer(Modifier.weight(1f, true))
            } else {
                AnimatedVisibility(
                    visible = pagerState.currentPage != 0,
                    enter = scaleIn(),
                    exit = scaleOut(),
                    modifier = Modifier.weight(1f, true)
                ) {
                    IconButton(onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }) {
                        Icon(Icons.Outlined.ArrowLeft, contentDescription = "Previous")
                    }
                }
            }
            Button(onClick = {
                if (pagerState.currentPage == pagerState.pageCount - 1) { // Go to home if we're on the last page
                    ScreenRouter.navigateTo(Screens.Home)
                } else { // Scroll to the last page of the tutorial
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.pageCount - 1)
                    }
                }
            }, modifier = Modifier.weight(1f, true)) {
                // Text is chosen dynamically based on the current page
                Text(
                    if (pagerState.currentPage == pagerState.pageCount - 1)
                        stringResource(R.string.close)
                    else stringResource(R.string.skip)
                )
            }
            if (pagerState.currentPage == pagerState.pageCount - 1) { // Don't show the button if we're on the last page
                Spacer(Modifier.weight(1f, true))
            } else {
                AnimatedVisibility(
                    visible = pagerState.currentPage != pagerState.pageCount - 1,
                    enter = scaleIn(),
                    exit = scaleOut(),
                    modifier = Modifier.weight(1f, true)
                ) {
                    IconButton(onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }) {
                        Icon(Icons.Outlined.ArrowRight, contentDescription = "Next")
                    }
                }
            }
        }
        BackHandler { ScreenRouter.navigateTo(Screens.Home) }
    }
}

// Utility function for code reusability
@Composable
private fun Page(pageInfo: PageInfo) {
    val context = LocalContext.current

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            pageInfo.text.map { stringResource(it) }.joinToString("\n\n"),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
        if (pageInfo.video != null) {
            AsyncImage(
                modifier = Modifier.padding(16.dp),
                model = ImageRequest.Builder(context).data(pageInfo.video).build(),
                contentDescription = "Clip",
                imageLoader = ImageLoader.Builder(context).components {
                    add(ImageDecoderDecoder.Factory())
                }.build()
            )
        }
    }
}
