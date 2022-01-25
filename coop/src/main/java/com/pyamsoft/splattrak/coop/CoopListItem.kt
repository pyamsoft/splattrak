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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.pyamsoft.splattrak.splatnet.api.SplatCoop
import com.pyamsoft.splattrak.splatnet.api.SplatCoopSession
import com.pyamsoft.splattrak.ui.R as R2
import com.pyamsoft.splattrak.ui.SplatCountdownTimer
import com.pyamsoft.splattrak.ui.card.BackgroundStripeWrapper
import com.pyamsoft.splattrak.ui.test.TestData
import com.pyamsoft.splattrak.ui.test.createNewTestImageLoader
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
internal fun CoopListItem(
    modifier: Modifier = Modifier,
    coop: SplatCoop,
    imageLoader: ImageLoader,
    onCountdownCompleted: (SplatCoop) -> Unit,
) {
  val backgroundColor = colorResource(R2.color.splatRanked)

  Card(
      modifier = modifier,
      backgroundColor = backgroundColor,
      contentColor = Color.White,
  ) {
    BackgroundStripeWrapper(
        modifier = Modifier.fillMaxWidth(),
        imageLoader = imageLoader,
    ) {
      Column {
        LobbyName(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            coop = coop,
        )
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
          itemsIndexed(
              items = coop.sessions(),
              key = { _, item -> item.start().toInstant(ZoneOffset.UTC).epochSecond },
          ) { index, item ->
            if (index <= 0) {
              CurrentBattle(
                  modifier = Modifier.fillMaxWidth().padding(8.dp),
                  imageLoader = imageLoader,
                  coop = item,
                  onCountdownCompleted = { onCountdownCompleted(coop) },
              )
            } else {
              NextBattle(
                  modifier = Modifier.fillMaxWidth().padding(8.dp),
                  imageLoader = imageLoader,
                  coop = item,
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun NextBattle(
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader,
    coop: SplatCoopSession,
) {}

@Composable
private fun CurrentBattle(
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader,
    coop: SplatCoopSession,
    onCountdownCompleted: () -> Unit
) {
  val start =
      remember(coop.start()) {
        coop.start().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))
      }
  val end =
      remember(coop.end()) {
        coop.end().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))
      }

  Column(
      modifier = modifier,
  ) {
    Text(
        text = "$start - $end",
        style = MaterialTheme.typography.body1,
    )
    Countdown(
        coop = coop,
        onCountdownCompleted = onCountdownCompleted,
    )
  }
}

@Composable
private fun Countdown(
    modifier: Modifier = Modifier,
    coop: SplatCoopSession,
    onCountdownCompleted: () -> Unit
) {
  val scope = rememberCoroutineScope()

  // Countdown text
  var text by remember(coop.end()) { mutableStateOf("") }

  DisposableEffect(coop.end()) {
    val nextStartTime = coop.end()
    val timeUntilEnd = LocalDateTime.now().until(nextStartTime, ChronoUnit.SECONDS)
    val countdown =
        SplatCountdownTimer(timeUntilEnd) { display, isComplete ->
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
    coop: SplatCoop,
) {
  val name = coop.name()

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
private fun PreviewCoopListItem() {
  CoopListItem(
      coop = TestData.coop,
      imageLoader = createNewTestImageLoader(),
      onCountdownCompleted = {},
  )
}
