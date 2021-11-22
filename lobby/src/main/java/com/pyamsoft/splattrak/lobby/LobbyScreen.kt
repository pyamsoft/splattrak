/*
 * Copyright 2021 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.splattrak.lobby

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.splattrak.main.MainViewState
import com.pyamsoft.splattrak.main.MutableMainViewState
import com.pyamsoft.splattrak.splatnet.api.SplatBattle
import com.pyamsoft.splattrak.ui.NotNintendo
import com.pyamsoft.splattrak.ui.test.createNewTestImageLoader

@Composable
fun LobbyScreen(
    modifier: Modifier = Modifier,
    mainState: MainViewState,
    state: LobbyViewState,
    imageLoader: ImageLoader,
    onRefresh: () -> Unit,
    onItemClicked: (SplatBattle) -> Unit,
) {
  val isLoading = state.loading
  val schedule = state.schedule
  val error = state.error

  Surface(
      modifier = modifier,
  ) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = isLoading),
        onRefresh = onRefresh,
    ) {
      Crossfade(
          targetState = error,
      ) { err ->
        if (err == null) {
          BattleList(
              mainState = mainState,
              schedule = schedule,
              imageLoader = imageLoader,
              onItemClicked = onItemClicked,
              onItemCountdownCompleted = { onRefresh() },
          )
        } else {
          Error(
              modifier = Modifier.fillMaxSize().verticalScroll(state = rememberScrollState()),
              error = err,
              onRefresh = onRefresh,
          )
        }
      }
    }
  }
}

@Composable
private fun Error(
    modifier: Modifier = Modifier,
    error: Throwable,
    onRefresh: () -> Unit,
) {
  Column(
      modifier = modifier.padding(16.dp),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(
        textAlign = TextAlign.Center,
        text = error.message ?: "An unexpected error occurred",
        style =
            MaterialTheme.typography.body1.copy(
                color = MaterialTheme.colors.error,
            ),
    )

    Text(
        modifier = Modifier.padding(top = 16.dp),
        textAlign = TextAlign.Center,
        text = "Please try again later.",
        style = MaterialTheme.typography.body2,
    )

    Button(
        modifier = Modifier.padding(top = 16.dp),
        onClick = onRefresh,
    ) {
      Text(
          text = "Refresh",
      )
    }
  }
}

@Composable
private fun BattleList(
    mainState: MainViewState,
    schedule: List<SplatBattle>,
    imageLoader: ImageLoader,
    onItemClicked: (SplatBattle) -> Unit,
    onItemCountdownCompleted: (SplatBattle) -> Unit,
) {
  val bottomNavHeight = mainState.bottomNavHeight
  val density = LocalDensity.current
  val bottomNavHeightDp = remember(bottomNavHeight) { density.run { bottomNavHeight.toDp() } }

  LazyColumn(
      modifier = Modifier.padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    item {
      Spacer(
          modifier = Modifier.fillMaxWidth().statusBarsHeight(),
      )
    }

    items(
        items = schedule,
        key = { it.mode().key() },
    ) { item ->
      LobbyListItem(
          battle = item,
          imageLoader = imageLoader,
          onClick = onItemClicked,
          onCountdownCompleted = onItemCountdownCompleted,
      )
    }

    item {
      NotNintendo(
          modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
      )
    }

    // Space to float the bottom nav
    item {
      Spacer(
          modifier = Modifier.fillMaxWidth().height(bottomNavHeightDp + 8.dp),
      )
    }

    item {
      Spacer(
          modifier = Modifier.fillMaxWidth().navigationBarsHeight(),
      )
    }
  }
}

@Preview
@Composable
private fun PreviewLobbyScreen() {
  val context = LocalContext.current

  LobbyScreen(
      state =
          MutableLobbyViewState().apply {
            schedule = emptyList()
            error = null
            loading = false
          },
      mainState =
          MutableMainViewState().apply {
            theme = Theming.Mode.SYSTEM
            bottomNavHeight = 0
          },
      imageLoader = createNewTestImageLoader(context),
      onRefresh = {},
      onItemClicked = {},
  )
}
