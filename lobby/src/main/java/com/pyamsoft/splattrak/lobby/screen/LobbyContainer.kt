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

import android.view.ViewGroup
import androidx.core.view.updatePadding
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.arch.UiRender
import com.pyamsoft.splattrak.lobby.databinding.LobbyContainerBinding
import javax.inject.Inject

class LobbyContainer
@Inject
internal constructor(
    list: LobbyList,
    parent: ViewGroup,
) : BaseUiView<LobbyViewState, LobbyViewEvent, LobbyContainerBinding>(parent) {

  override val viewBinding = LobbyContainerBinding::inflate

  override val layoutRoot by boundView { lobbyContainer }

  init {
    nest(list)
  }

  override fun onRender(state: UiRender<LobbyViewState>) {
    state.mapChanged { it.bottomOffset }.render(viewScope) { handleBottomBarHeight(it) }
  }

  private fun handleBottomBarHeight(height: Int) {
    layoutRoot.updatePadding(bottom = height)
  }
}
