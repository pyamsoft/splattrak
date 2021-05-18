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

import android.view.ViewGroup
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.arch.UiRender
import com.pyamsoft.pydroid.arch.UiViewState
import com.pyamsoft.splattrak.lobby.databinding.LobbyItemNameBinding
import com.pyamsoft.splattrak.splatnet.api.SplatBattle

abstract class BaseLobbyName<S : UiViewState>
protected constructor(
    parent: ViewGroup,
) : BaseUiView<S, Nothing, LobbyItemNameBinding>(parent) {

  final override val layoutRoot by boundView { lobbyItemNameRoot }

  final override val viewBinding = LobbyItemNameBinding::inflate

  init {
    doOnTeardown { binding.lobbyItemName.text = "" }
  }

  @CheckResult protected abstract fun getBattle(state: S): SplatBattle

  final override fun onRender(state: UiRender<S>) {
    state.mapChanged { getBattle(it) }.mapChanged { it.mode() }.mapChanged { it.name() }.render(
        viewScope) { handleName(it) }
  }

  private fun handleName(name: String) {
    binding.lobbyItemName.text = name
  }
}
