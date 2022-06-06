package me.lioironzello.yahtzee.ui.screen

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
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

data class PageInfo(val video: Int?, val topText: String, val bottomText: String)

object Tutorial {
    val pages = listOf<PageInfo>(
        PageInfo(null, "Test", "Test"),
        PageInfo(null, "Test1", "Test1"),
        PageInfo(null, "Test2", "Test2"),
        PageInfo(null, "Test3", "Test3"),
    )
}

@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
fun Tutorial() {
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()

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
            Tutorial.pages.size,
            state = pagerState,
            modifier = Modifier.weight(1f, true)
        ) { page ->
            Page(pageInfo = Tutorial.pages[page])
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
                .padding(16.dp)) {
            if (pagerState.currentPage == 0) {
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
                if (pagerState.currentPage == pagerState.pageCount - 1) {
                    ScreenRouter.navigateTo(Screens.Home)
                } else {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.pageCount - 1)
                    }
                }
            }, modifier = Modifier.weight(1f, true)) {
                Text(if (pagerState.currentPage == pagerState.pageCount - 1) "Close" else "Skip")
            }
            if (pagerState.currentPage == pagerState.pageCount - 1) {
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
    }
}

@Composable
private fun Page(pageInfo: PageInfo) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(pageInfo.topText)
        if (pageInfo.video != null) {
            // TODO(render video)
        }
        Text(pageInfo.bottomText)
    }
}
