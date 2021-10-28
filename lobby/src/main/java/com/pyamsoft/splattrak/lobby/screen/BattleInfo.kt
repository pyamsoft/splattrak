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

package com.pyamsoft.splattrak.lobby.screen

import android.text.format.DateFormat
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.splattrak.splatnet.api.SplatMatch
import com.pyamsoft.splattrak.ui.getCurrentLocale
import java.time.format.DateTimeFormatter

private val formatter24 =
    object : ThreadLocal<DateTimeFormatter>() {

      private val formatter by
          lazy(LazyThreadSafetyMode.NONE) { DateTimeFormatter.ofPattern("kk:mm") }

      override fun get(): DateTimeFormatter {
        return formatter
      }
    }

private val formatter12 =
    object : ThreadLocal<DateTimeFormatter>() {

      private val formatter by
          lazy(LazyThreadSafetyMode.NONE) { DateTimeFormatter.ofPattern("hh:mm a") }

      override fun get(): DateTimeFormatter {
        return formatter
      }
    }

@Composable
internal fun BattleInfo(
    modifier: Modifier = Modifier,
    match: SplatMatch,
) {
  val name = match.rules().name()
  val start = match.start()
  val end = match.end()

  val locale = getCurrentLocale()
  val context = LocalContext.current
  val is24Hour = DateFormat.is24HourFormat(context)
  val localFormatter = remember(is24Hour) { if (is24Hour) formatter24 else formatter12 }
  val formatter = localFormatter.get().requireNotNull()

  Row(
      modifier = modifier.padding(bottom = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
        text = name,
        style = MaterialTheme.typography.body1,
    )
    Spacer(
        modifier = Modifier.weight(1F),
    )
    Text(
        text = start.format(formatter).lowercase(locale),
        style = MaterialTheme.typography.body1,
    )
    Text(
        modifier =
            Modifier.padding(
                start = 4.dp,
                end = 4.dp,
            ),
        text = "-",
        style = MaterialTheme.typography.body1,
    )
    Text(
        text = end.format(formatter).lowercase(locale),
        style = MaterialTheme.typography.body1,
    )
  }
}
