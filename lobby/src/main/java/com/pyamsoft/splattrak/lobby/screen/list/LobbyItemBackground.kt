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

package com.pyamsoft.splattrak.lobby.screen.list

import android.view.ViewGroup
import com.pyamsoft.pydroid.arch.UiRender
import com.pyamsoft.pydroid.arch.UiView
import com.pyamsoft.splattrak.lobby.R
import com.pyamsoft.splattrak.splatnet.api.SplatGameMode
import javax.inject.Inject

class LobbyItemBackground
@Inject
internal constructor(
    private val parent: ViewGroup,
) : UiView<LobbyItemViewState.Data, LobbyItemViewEvent>() {

  init {
    doOnTeardown { parent.background = null }
  }

  override fun render(state: UiRender<LobbyItemViewState.Data>) {
    state.mapChanged { it.battle }.mapChanged { it.mode() }.render(viewScope) {
      handleBackground(it)
    }
  }

  private fun handleBackground(mode: SplatGameMode) {
    parent.setBackgroundResource(
        when (mode.mode()) {
          SplatGameMode.Mode.REGULAR -> R.color.splatRegular
          SplatGameMode.Mode.LEAGUE -> R.color.splatLeague
          SplatGameMode.Mode.RANKED -> R.color.splatRanked
        })
  }
}
