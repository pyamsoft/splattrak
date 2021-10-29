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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.splattrak.main.MainViewState
import com.pyamsoft.splattrak.splatnet.api.SplatBattle
import com.pyamsoft.splattrak.ui.NotNintendo
import com.pyamsoft.splattrak.ui.createNewTestImageLoader

@Composable
fun LobbyScreen(
    modifier: Modifier = Modifier,
    mainState: MainViewState,
    state: LobbyViewState,
    imageLoader: ImageLoader,
    onRefresh: () -> Unit,
    onItemClicked: (Int) -> Unit,
    onItemCountdownCompleted: (Int) -> Unit,
) {
  val isLoading = state.loading
  val schedule = state.schedule

  Surface(
      modifier = modifier,
  ) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = isLoading),
        onRefresh = onRefresh,
    ) {
      BattleList(
          mainState = mainState,
          schedule = schedule,
          imageLoader = imageLoader,
          onItemClicked = onItemClicked,
          onItemCountdownCompleted = onItemCountdownCompleted,
      )
    }
  }
}

@Composable
private fun BattleList(
    mainState: MainViewState,
    schedule: List<SplatBattle>,
    imageLoader: ImageLoader,
    onItemClicked: (Int) -> Unit,
    onItemCountdownCompleted: (Int) -> Unit,
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

    itemsIndexed(
        items = schedule,
        key = { _, item -> item.mode().key() },
    ) { index, item ->
      LobbyListItem(
          battle = item,
          imageLoader = imageLoader,
          onClick = { onItemClicked(index) },
          onCountdownCompleted = { onItemCountdownCompleted(index) },
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
          LobbyViewState(
              schedule = emptyList(),
              error = null,
              loading = false,
          ),
      mainState =
          MainViewState(
              theme = Theming.Mode.SYSTEM,
              bottomNavHeight = 0,
          ),
      imageLoader = createNewTestImageLoader(context),
      onRefresh = {},
      onItemClicked = {},
      onItemCountdownCompleted = {},
  )
}
