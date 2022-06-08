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
import androidx.compose.ui.platform.LocalContext
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

data class PageInfo(val video: Int?, val text: String)

object Tutorial {
    val pages = listOf(
        PageInfo(
            null, """
Segna più punti possibili tirando i dadi per realizzare le 13 combinazioni prefissate nel gioco

Una partita consiste in turni durante i quali ogni giocatore sceglie quale combinazione di punteggio deve essere utilizzata

Una volta che una combinazione è stata usata, non può essere usata nuovamente

La partita termina quando tutte le categorie sono state messe a segno

Il vincitore è il giocatore che realizza più punti
            """
        ),
        PageInfo(R.drawable.clip1, "I dadi possono essere lanciati 3 volte in un turno"),
        PageInfo(R.drawable.clip2, "Puoi bloccare i dadi dopo il tuo primo o secondo lancio"),
        PageInfo(
            R.drawable.clip3,
            "Entro il terzo lancio devi selezionare una combinazione sul tabellone"
        ),
        PageInfo(
            null, """
Combinazioni

[1-6]: Ottieni più dadi possibili con questa faccia. Il punteggio è la somma di questi dadi specifici

Tris e Poker: 3 e 4 dadi con la stessa faccia. Il punteggio è la somma di tutti i dadi

Full: 3 dadi di un tipo e 2 di un altro. Il punteggio è 25 punti

Piccola Scala: 4 dadi sono ordinati in modo crescente. Il punteggio è 30 punti

Grande Scalaa: 5 dadi sono ordinati in modo crescente. Il punteggio è 40 punti

Yahtzee: 5 dadi uguali. Il punteggio è 50. Se Yahtzee viene ripetuto può essere inserito solo in un'altra combinazione libera con il relativo punteggio

Chance: qualsiasi combinazione ottenuta. Il punteggio è la somma di tutti i dadi

Bonus: si ottiene quando la somma dei punteggi per le 6 combinazioni precedenti supera o raggiunge 63. Il punteggio è 35 punti
        """.trimIndent()
        )
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
                .padding(16.dp)
        ) {
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
    val context = LocalContext.current

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(pageInfo.text, textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
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
