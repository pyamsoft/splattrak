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

package com.pyamsoft.splattrak.lobby.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.pyamsoft.splattrak.splatnet.api.SplatMatch
import com.pyamsoft.splattrak.splatnet.api.key
import com.pyamsoft.splattrak.ui.card.BackgroundStripeWrapper
import com.pyamsoft.splattrak.ui.card.decideBackgroundColor
import com.pyamsoft.splattrak.ui.test.TestData
import com.pyamsoft.splattrak.ui.test.createNewTestImageLoader

@Composable
fun DrilldownScreen(
    modifier: Modifier = Modifier,
    state: DrilldownViewState,
    imageLoader: ImageLoader,
    onRefresh: () -> Unit,
) {
  val isLoading = state.loading
  val battle = state.battle
  val error = state.error

  val mode = battle?.mode()?.mode()
  val backgroundColor =
      if (mode != null) {
        val backgroundColorResource = remember(mode) { decideBackgroundColor(mode) }
        colorResource(backgroundColorResource)
      } else {
        Color.Unspecified
      }

  Card(
      modifier = modifier,
      backgroundColor = backgroundColor,
      contentColor = Color.White,
      elevation = 16.dp,
  ) {
    BackgroundStripeWrapper(
        imageLoader = imageLoader,
    ) {
      SwipeRefresh(
          state = rememberSwipeRefreshState(isRefreshing = isLoading),
          onRefresh = onRefresh,
      ) {
        if (battle == null) {
          EmptyState(
              error = error,
          )
        } else {
          BattleList(
              rotation = battle.rotation(),
              imageLoader = imageLoader,
          )
        }
      }
    }
  }
}

@Composable
private fun EmptyState(error: Throwable?) {
  Box(
      modifier =
          Modifier.verticalScroll(
              state = rememberScrollState(),
          ),
      contentAlignment = Alignment.Center,
  ) {
    Text(
        text = if (error == null) "Loading" else error.message ?: "An unexpected error occurred.",
        style = MaterialTheme.typography.body1,
        color = if (error == null) Color.Unspecified else MaterialTheme.colors.error,
    )
  }
}

@Composable
private fun BattleList(
    rotation: List<SplatMatch>,
    imageLoader: ImageLoader,
) {
  LazyColumn(
      modifier = Modifier.padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    item {
      Spacer(
          modifier = Modifier.fillMaxWidth().height(0.dp),
      )
    }

    items(
        items = rotation,
        key = { it.key() },
    ) { item ->
      DrilldownListItem(
          match = item,
          imageLoader = imageLoader,
      )
    }

    item {
      Spacer(
          modifier = Modifier.fillMaxWidth().height(0.dp),
      )
    }
  }
}

@Preview
@Composable
private fun PreviewDrilldownScreen() {
  Surface {
    DrilldownScreen(
        state =
            MutableDrilldownViewState().apply {
              battle = TestData.battle
              loading = false
              error = null
            },
        imageLoader = createNewTestImageLoader(),
        onRefresh = {},
    )
  }
}
