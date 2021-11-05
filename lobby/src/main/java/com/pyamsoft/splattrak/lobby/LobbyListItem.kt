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
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.pyamsoft.splattrak.lobby.common.BackgroundDarkWrapper
import com.pyamsoft.splattrak.lobby.common.BackgroundStripeWrapper
import com.pyamsoft.splattrak.lobby.common.BattleInfo
import com.pyamsoft.splattrak.lobby.common.BigBattleMaps
import com.pyamsoft.splattrak.lobby.common.SmallBattleMaps
import com.pyamsoft.splattrak.lobby.common.decideBackgroundColor
import com.pyamsoft.splattrak.lobby.test.TestData
import com.pyamsoft.splattrak.splatnet.api.SplatBattle
import com.pyamsoft.splattrak.splatnet.api.SplatMatch
import com.pyamsoft.splattrak.ui.test.createNewTestImageLoader
import com.pyamsoft.splattrak.ui.R as R2
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
internal fun LobbyListItem(
    modifier: Modifier = Modifier,
    battle: SplatBattle,
    imageLoader: ImageLoader,
    onClick: () -> Unit,
    onCountdownCompleted: () -> Unit,
) {
  val name = battle.mode().name()
  val mode = battle.mode().mode()
  val rotation = battle.rotation()
  val backgroundColorResource = remember(mode) { decideBackgroundColor(mode) }
  val backgroundColor = colorResource(backgroundColorResource)

  Card(
      modifier = modifier.clickable { onClick() },
      backgroundColor = backgroundColor,
      contentColor = Color.White,
  ) {
    BackgroundStripeWrapper(
        modifier = Modifier.fillMaxWidth(),
        imageLoader = imageLoader,
    ) {
      Column(
          modifier = Modifier.fillMaxWidth(),
      ) {
        LobbyName(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            name = name,
        )
        CurrentBattle(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            imageLoader = imageLoader,
            rotation = rotation,
        )
        NextBattle(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            imageLoader = imageLoader,
            rotation = rotation,
            onCountdownCompleted = onCountdownCompleted,
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
      modifier = Modifier.padding(horizontal = 8.dp),
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
        modifier = Modifier.fillMaxWidth().padding(16.dp),
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
    Surface(
        color = colorResource(R2.color.splatNext),
        contentColor = Color.White,
        shape = MaterialTheme.shapes.medium,
    ) {
      Text(
          modifier = Modifier.padding(8.dp),
          text = stringResource(R.string.up_next),
          style = MaterialTheme.typography.body1,
          textAlign = TextAlign.Center,
      )
    }
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
  var text by remember(match.id()) { mutableStateOf("") }

  DisposableEffect(match.id()) {
    val nextStartTime = match.start()
    val timeUntilStart = LocalDateTime.now().until(nextStartTime, ChronoUnit.SECONDS)
    val countdown =
        SplatCountdownTimer(timeUntilStart) { display, isComplete ->
          text = display
          if (isComplete) {
            onCountdownCompleted()
          }
        }

    scope.launch(context = Dispatchers.IO) { countdown.start() }
    return@DisposableEffect onDispose { countdown.cancel() }
  }

  Text(
      modifier = modifier,
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
  val context = LocalContext.current

  LobbyListItem(
      battle = TestData.battle,
      imageLoader = createNewTestImageLoader(context),
      onClick = {},
      onCountdownCompleted = {},
  )
}
