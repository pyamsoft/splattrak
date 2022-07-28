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

package com.pyamsoft.splattrak.coop

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import coil.ImageLoader
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.splattrak.main.MainViewState
import com.pyamsoft.splattrak.main.MutableMainViewState
import com.pyamsoft.splattrak.splatnet.api.SplatCoop
import com.pyamsoft.splattrak.ui.NotNintendo
import com.pyamsoft.splattrak.ui.test.createNewTestImageLoader

@Composable
fun CoopScreen(
    modifier: Modifier = Modifier,
    mainState: MainViewState,
    state: CoopViewState,
    imageLoader: ImageLoader,
    onRefresh: () -> Unit,
) {
  val isLoading = state.loading
  val error = state.error

  SwipeRefresh(
      modifier = modifier,
      state = rememberSwipeRefreshState(isRefreshing = isLoading),
      onRefresh = onRefresh,
  ) {
    Crossfade(
        targetState = error,
    ) { err ->
      if (err == null) {
        CoopList(
            mainState = mainState,
            state = state,
            imageLoader = imageLoader,
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

@Composable
private fun Error(
    modifier: Modifier = Modifier,
    error: Throwable,
    onRefresh: () -> Unit,
) {
  Column(
      modifier = modifier.padding(MaterialTheme.keylines.content),
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
        modifier = Modifier.padding(top = MaterialTheme.keylines.content),
        textAlign = TextAlign.Center,
        text = "Please try again later.",
        style = MaterialTheme.typography.body2,
    )

    Button(
        modifier = Modifier.padding(top = MaterialTheme.keylines.content),
        onClick = onRefresh,
    ) {
      Text(
          text = "Refresh",
      )
    }
  }
}

@Composable
private fun CoopList(
    mainState: MainViewState,
    state: CoopViewState,
    imageLoader: ImageLoader,
    onItemCountdownCompleted: (SplatCoop) -> Unit,
) {
  val coop = state.coop
  val bottomNavHeight = mainState.bottomNavHeight
  val density = LocalDensity.current
  val bottomNavHeightDp = remember(bottomNavHeight) { density.run { bottomNavHeight.toDp() } }

  LazyColumn(
      modifier = Modifier.padding(horizontal = MaterialTheme.keylines.content),
      verticalArrangement = Arrangement.spacedBy(MaterialTheme.keylines.content),
  ) {
    item {
      Spacer(
          modifier = Modifier.fillMaxWidth().statusBarsPadding(),
      )
    }

    if (coop != null) {
      item {
        CoopListItem(
            coop = coop,
            imageLoader = imageLoader,
            onCountdownCompleted = onItemCountdownCompleted,
        )
      }
    }

    item {
      NotNintendo(
          modifier = Modifier.fillMaxWidth().padding(top = MaterialTheme.keylines.content),
      )
    }

    // Space to float the bottom nav
    item {
      Spacer(
          modifier =
              Modifier.fillMaxWidth().height(bottomNavHeightDp + MaterialTheme.keylines.baseline),
      )
    }

    item {
      Spacer(
          modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
      )
    }
  }
}

@Preview
@Composable
private fun PreviewCoopScreen() {
  CoopScreen(
      state =
          MutableCoopViewState().apply {
            coop = null
            error = null
            loading = false
          },
      mainState =
          MutableMainViewState().apply {
            theme = Theming.Mode.SYSTEM
            bottomNavHeight = 0
          },
      imageLoader = createNewTestImageLoader(),
      onRefresh = {},
  )
}
