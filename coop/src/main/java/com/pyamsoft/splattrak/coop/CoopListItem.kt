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

import android.text.format.DateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.Divider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.defaults.CardDefaults
import com.pyamsoft.splattrak.splatnet.api.SplatCoop
import com.pyamsoft.splattrak.splatnet.api.SplatCoopSession
import com.pyamsoft.splattrak.ui.R as R2
import com.pyamsoft.splattrak.ui.SplatCountdownTimer
import com.pyamsoft.splattrak.ui.card.BackgroundDarkWrapper
import com.pyamsoft.splattrak.ui.card.BackgroundStripeWrapper
import com.pyamsoft.splattrak.ui.card.BattleMap
import com.pyamsoft.splattrak.ui.card.Label
import com.pyamsoft.splattrak.ui.test.TestData
import com.pyamsoft.splattrak.ui.test.createNewTestImageLoader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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

  val context = LocalContext.current
  val is24Hour = DateFormat.is24HourFormat(context)
  val localFormatter = remember(is24Hour) { if (is24Hour) formatter24 else formatter12 }
  val formatter = localFormatter.get().requireNotNull()

  Card(
      modifier = modifier,
      backgroundColor = backgroundColor,
      contentColor = Color.White,
      elevation = CardDefaults.Elevation,
  ) {
    BackgroundStripeWrapper(
        modifier = Modifier.fillMaxWidth(),
        imageLoader = imageLoader,
    ) {
      Column {
        LobbyName(
            modifier = Modifier.fillMaxWidth().padding(top = MaterialTheme.keylines.baseline),
            coop = coop,
        )

        val sessions = coop.sessions()
        val current = remember(sessions) { sessions.getOrNull(0) }

        // If there is no first session, there are no remaining sessions either
        if (current != null) {

          BackgroundDarkWrapper(
              modifier = Modifier.fillMaxWidth().padding(MaterialTheme.keylines.baseline),
              imageLoader = imageLoader,
          ) {
            CurrentBattle(
                modifier = Modifier.fillMaxWidth().padding(MaterialTheme.keylines.baseline),
                formatter = formatter,
                imageLoader = imageLoader,
                coop = current,
                onCountdownCompleted = { onCountdownCompleted(coop) },
            )
          }

          val remaining = remember(sessions) { sessions.subList(1, sessions.size) }
          Column(
              modifier = Modifier.fillMaxWidth(),
          ) {
            for (index in remaining.indices) {
              val isFirst = remember(index) { index <= 0 }
              NextBattle(
                  modifier = Modifier.fillMaxWidth().padding(MaterialTheme.keylines.baseline),
                  formatter = formatter,
                  imageLoader = imageLoader,
                  coop = remaining[index],
                  isFirst = isFirst,
              )

              Divider(
                  // DividerAlpha is a private const so we copy it here
                  // DividerAlpha is a little too light, use hardcoded instead
                  color = Color.Black.copy(alpha = 0.2F),
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
    formatter: DateTimeFormatter,
    imageLoader: ImageLoader,
    coop: SplatCoopSession,
    isFirst: Boolean,
) {
  val start = remember(coop.start()) { coop.start().format(formatter) }
  val end = remember(coop.end()) { coop.end().format(formatter) }
  val map = coop.map()

  Column(
      modifier = modifier,
  ) {
    if (isFirst) {
      Label(
          modifier = Modifier.padding(bottom = MaterialTheme.keylines.baseline),
          text = "Soon!",
      )
    }

    Text(
        text = "$start - $end",
        style = MaterialTheme.typography.body1,
    )
    if (map != null) {
      Row(
          modifier = Modifier.height(100.dp).padding(top = MaterialTheme.keylines.baseline),
          verticalAlignment = Alignment.CenterVertically,
      ) {
        BattleMap(
            modifier = Modifier.weight(1F),
            map = map.map(),
            imageLoader = imageLoader,
        )
        Weapons(
            modifier = Modifier.weight(1F),
            weapons = map.weapons(),
            imageLoader = imageLoader,
            small = true,
        )
      }
    }
  }
}

@Composable
private fun CurrentCountdown(
    modifier: Modifier = Modifier,
    coop: SplatCoopSession,
    onCountdownCompleted: () -> Unit,
) {
  Row(
      modifier = modifier,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Countdown(
        coop = coop,
        onCountdownCompleted = onCountdownCompleted,
        style = MaterialTheme.typography.body1,
    )
    Text(
        modifier = Modifier.padding(start = MaterialTheme.keylines.baseline),
        text = "remaining",
        style = MaterialTheme.typography.body1,
    )
  }
}

private val formatter24 =
    object : ThreadLocal<DateTimeFormatter>() {

      private val formatter by
          lazy(LazyThreadSafetyMode.NONE) { DateTimeFormatter.ofPattern("EE M/dd, kk:mm") }

      override fun get(): DateTimeFormatter {
        return formatter
      }
    }

private val formatter12 =
    object : ThreadLocal<DateTimeFormatter>() {

      private val formatter by
          lazy(LazyThreadSafetyMode.NONE) { DateTimeFormatter.ofPattern("EE M/dd, hh:mm a") }

      override fun get(): DateTimeFormatter {
        return formatter
      }
    }

@Composable
private fun CurrentBattle(
    modifier: Modifier = Modifier,
    formatter: DateTimeFormatter,
    imageLoader: ImageLoader,
    coop: SplatCoopSession,
    onCountdownCompleted: () -> Unit
) {
  val s = coop.start()
  val e = coop.end()

  val isOpen =
      remember(s, e) {
        val now = LocalDateTime.now()
        val isAfter = now.isAfter(s) || now == s
        val isBefore = now.isBefore(e) || now == e
        return@remember isAfter && isBefore
      }

  val start = remember(s) { s.format(formatter) }
  val end = remember(e) { e.format(formatter) }
  val map = coop.map()

  Column(
      modifier = modifier,
  ) {
    Label(
        modifier = Modifier.padding(bottom = MaterialTheme.keylines.baseline),
        text = if (isOpen) "Now Open!" else "Closed",
    )
    Text(
        modifier = Modifier.padding(bottom = MaterialTheme.keylines.baseline),
        text = "$start - $end",
        style = MaterialTheme.typography.body1,
    )
    if (isOpen) {
      CurrentCountdown(
          modifier = Modifier.padding(bottom = MaterialTheme.keylines.baseline),
          coop = coop,
          onCountdownCompleted = onCountdownCompleted,
      )
    }
    if (map != null) {
      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        BattleMap(
            modifier = Modifier.height(160.dp),
            map = map.map(),
            imageLoader = imageLoader,
        )

        Column(
            modifier = Modifier.height(100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
          Text(
              text = "Supplied Weapons",
              style = MaterialTheme.typography.body1,
          )

          Weapons(
              modifier = Modifier.fillMaxWidth(),
              weapons = map.weapons(),
              imageLoader = imageLoader,
              small = false,
          )
        }
      }
    }
  }
}

@Composable
private fun Weapons(
    modifier: Modifier = Modifier,
    weapons: List<SplatCoopSession.Map.Weapon>,
    imageLoader: ImageLoader,
    small: Boolean,
) {
  val weaponList =
      remember(small, weapons) {
        if (small) {
          val middle = weapons.size / 2
          listOf(
              weapons.subList(0, middle),
              weapons.subList(middle, weapons.size),
          )
        } else {
          listOf(weapons)
        }
      }

  val weaponSize = remember(small) { if (small) 40.dp else 64.dp }

  Column(
      modifier = modifier,
  ) {
    for (group in weaponList) {
      Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.CenterVertically,
      ) {
        for (weapon in group) {
          val image = weapon.imageUrl()
          val name = weapon.name()
          Box(
              modifier = Modifier.width(weaponSize),
              contentAlignment = Alignment.BottomEnd,
          ) {
            AsyncImage(
                model = image,
                imageLoader = imageLoader,
                contentDescription = name,
            )
          }
        }
      }
    }
  }
}

@Composable
private fun Countdown(
    modifier: Modifier = Modifier,
    style: TextStyle,
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
      modifier = modifier.width(96.dp),
      text = text,
      style = style,
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
