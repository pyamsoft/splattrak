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

package com.pyamsoft.splattrak.lobby.item

import android.text.format.DateFormat
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.CheckResult
import androidx.core.view.updatePadding
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.arch.UiRender
import com.pyamsoft.pydroid.arch.UiViewState
import com.pyamsoft.pydroid.util.asDp
import com.pyamsoft.splattrak.lobby.databinding.LobbyItemInfoBinding
import com.pyamsoft.splattrak.splatnet.api.SplatMatch
import com.pyamsoft.splattrak.splatnet.api.SplatRuleset
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

abstract class BaseLobbyInfo<S : UiViewState>
protected constructor(
    parent: ViewGroup,
) : BaseUiView<S, Nothing, LobbyItemInfoBinding>(parent) {

  private val formatter12 by lazy(LazyThreadSafetyMode.NONE) {
    DateTimeFormatter.ofPattern("hh:mm a")
  }

  private val formatter24 by lazy(LazyThreadSafetyMode.NONE) {
    DateTimeFormatter.ofPattern("kk:mm")
  }

  final override val layoutRoot by boundView { lobbyItemCurrentInfo }

  final override val viewBinding = LobbyItemInfoBinding::inflate

  init {
    doOnTeardown {
      binding.lobbyItemCurrentGameType.text = ""
      binding.lobbyItemCurrentGameStart.text = ""
      binding.lobbyItemCurrentGameEnd.text = ""
    }

    doOnInflate {
      val size = 8.asDp(layoutRoot.context.applicationContext)
      layoutRoot.updatePadding(bottom = if (isLarge()) size * 2 else size)
    }
  }

  @CheckResult protected abstract fun isLarge(): Boolean

  @CheckResult protected abstract fun getMatch(state: S): SplatMatch

  @CheckResult
  private fun getDateFormatter(): DateTimeFormatter {
    val ctx = layoutRoot.context.applicationContext
    return if (DateFormat.is24HourFormat(ctx)) formatter24 else formatter12
  }

  final override fun onRender(state: UiRender<S>) {
    state.mapChanged { getMatch(it) }.mapChanged { it.rules() }.render(viewScope) {
      handleRuleset(it)
    }

    state.mapChanged { getMatch(it) }.mapChanged { it.start() }.render(viewScope) {
      handleTime(it, binding.lobbyItemCurrentGameStart)
    }

    state.mapChanged { getMatch(it) }.mapChanged { it.end() }.render(viewScope) {
      handleTime(it, binding.lobbyItemCurrentGameEnd)
    }
  }

  private fun handleTime(time: LocalDateTime, textView: TextView) {
    textView.text = time.format(getDateFormatter()).toLowerCase(Locale.getDefault())
  }

  private fun handleRuleset(rules: SplatRuleset) {
    binding.lobbyItemCurrentGameType.text = rules.name()
  }
}
