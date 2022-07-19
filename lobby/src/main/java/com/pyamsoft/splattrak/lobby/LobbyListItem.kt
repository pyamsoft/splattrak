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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.defaults.CardDefaults
import com.pyamsoft.splattrak.splatnet.api.SplatBattle
import com.pyamsoft.splattrak.splatnet.api.SplatMatch
import com.pyamsoft.splattrak.ui.SplatCountdownTimer
import com.pyamsoft.splattrak.ui.card.BackgroundDarkWrapper
import com.pyamsoft.splattrak.ui.card.BackgroundStripeWrapper
import com.pyamsoft.splattrak.ui.card.BattleInfo
import com.pyamsoft.splattrak.ui.card.BigBattleMaps
import com.pyamsoft.splattrak.ui.card.Label
import com.pyamsoft.splattrak.ui.card.SmallBattleMaps
import com.pyamsoft.splattrak.ui.card.decideBackgroundColor
import com.pyamsoft.splattrak.ui.test.TestData
import com.pyamsoft.splattrak.ui.test.createNewTestImageLoader
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
internal fun LobbyListItem(
    modifier: Modifier = Modifier,
    battle: SplatBattle,
    imageLoader: ImageLoader,
    onClick: (SplatBattle) -> Unit,
    onCountdownCompleted: (SplatBattle) -> Unit,
) {
  val b = battle.mode
  val name = b.name
  val mode = b.mode
  val rotation = battle.rotation
  val backgroundColorResource = remember(mode) { decideBackgroundColor(mode) }
  val backgroundColor = colorResource(backgroundColorResource)

  Card(
      modifier = modifier.clickable { onClick(battle) },
      backgroundColor = backgroundColor,
      contentColor = Color.White,
      elevation = CardDefaults.Elevation,
  ) {
    BackgroundStripeWrapper(
        modifier = Modifier.fillMaxWidth(),
        imageLoader = imageLoader,
    ) {
      Column(
          modifier = Modifier.fillMaxWidth(),
      ) {
        LobbyName(
            modifier = Modifier.fillMaxWidth().padding(top = MaterialTheme.keylines.baseline),
            name = name,
        )
        CurrentBattle(
            modifier = Modifier.fillMaxWidth().padding(MaterialTheme.keylines.baseline),
            imageLoader = imageLoader,
            rotation = rotation,
        )
        NextBattle(
            modifier = Modifier.fillMaxWidth().padding(MaterialTheme.keylines.baseline),
            imageLoader = imageLoader,
            rotation = rotation,
            onCountdownCompleted = { onCountdownCompleted(battle) },
        )
      }
    }
  }
}

@Composable
private fun NextBattle(
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader,
    rotation: List<SplatMatch>,
    onCountdownCompleted: () -> Unit,
) {
  val match = remember(rotation) { rotation[1] }
  LobbyUpNext(
      modifier = Modifier.padding(horizontal = MaterialTheme.keylines.baseline),
      match = match,
      onCountdownCompleted = onCountdownCompleted,
  )

  Column(
      modifier = modifier,
  ) {
    BattleInfo(
        modifier = Modifier.fillMaxWidth(),
        match = match,
    )
    SmallBattleMaps(
        modifier = Modifier.fillMaxWidth(),
        match = match,
        imageLoader = imageLoader,
    )
  }
}

@Composable
private fun CurrentBattle(
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader,
    rotation: List<SplatMatch>,
) {
  val match = remember(rotation) { rotation[0] }
  BackgroundDarkWrapper(
      modifier = modifier,
      imageLoader = imageLoader,
  ) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(MaterialTheme.keylines.content),
    ) {
      BattleInfo(
          modifier = Modifier.fillMaxWidth(),
          match = match,
      )
      BigBattleMaps(
          modifier = Modifier.fillMaxWidth(),
          match = match,
          imageLoader = imageLoader,
      )
    }
  }
}

@Composable
private fun LobbyUpNext(
    modifier: Modifier = Modifier,
    match: SplatMatch,
    onCountdownCompleted: () -> Unit
) {
  Row(
      modifier = modifier,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Label(
        text = stringResource(R.string.up_next),
    )
    Spacer(
        modifier = Modifier.weight(1F),
    )
    Countdown(
        match = match,
        onCountdownCompleted = onCountdownCompleted,
    )
  }
}

@Composable
private fun Countdown(
    modifier: Modifier = Modifier,
    match: SplatMatch,
    onCountdownCompleted: () -> Unit
) {
  val scope = rememberCoroutineScope()

  // Countdown text
  val (text, setText) = remember { mutableStateOf("") }

  // Hold callback here to avoid recompose
  val handleCountdownCompleted by rememberUpdatedState(onCountdownCompleted)

  DisposableEffect(
      match,
      setText,
      handleCountdownCompleted,
      scope,
  ) {
    val nextStartTime = match.start
    val timeUntilStart = LocalDateTime.now().until(nextStartTime, ChronoUnit.SECONDS)
    val countdown =
        SplatCountdownTimer(timeUntilStart) { display, isComplete ->
          setText(display)
          if (isComplete) {
            handleCountdownCompleted()
          }
        }

    scope.launch(context = Dispatchers.IO) { countdown.start() }
    return@DisposableEffect onDispose { countdown.cancel() }
  }

  Text(
      modifier = modifier.width(96.dp),
      text = text,
      style = MaterialTheme.typography.body1,
  )
}

@Composable
private fun LobbyName(
    modifier: Modifier = Modifier,
    name: String,
) {
  Box(
      modifier = modifier,
      contentAlignment = Alignment.Center,
  ) {
    Text(
        text = name,
        style = MaterialTheme.typography.h5,
        textAlign = TextAlign.Center,
    )
  }
}

@Preview
@Composable
private fun PreviewLobbyListItem() {
  LobbyListItem(
      battle = TestData.battle,
      imageLoader = createNewTestImageLoader(),
      onClick = {},
      onCountdownCompleted = {},
  )
}
