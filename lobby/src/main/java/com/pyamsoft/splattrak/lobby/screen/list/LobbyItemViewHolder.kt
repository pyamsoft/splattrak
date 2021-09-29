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

import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.arch.ViewBinder
import com.pyamsoft.pydroid.arch.createViewBinder
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.util.doOnDestroy
import com.pyamsoft.splattrak.lobby.databinding.LobbyListItemHolderBinding
import com.pyamsoft.splattrak.lobby.screen.LobbyListAdapter
import javax.inject.Inject

class LobbyItemViewHolder
internal constructor(
    binding: LobbyListItemHolderBinding,
    owner: LifecycleOwner,
    factory: LobbyItemComponent.Factory,
    callback: LobbyListAdapter.Callback,
) : BaseLobbyViewHolder(binding.root) {

  @Inject @JvmField internal var clickHandler: LobbyItemClickHandler? = null

  @Inject @JvmField internal var background: LobbyItemBackground? = null

  @Inject @JvmField internal var backgroundContainer: LobbyItemBackgroundContainer? = null

  private val viewBinder: ViewBinder<LobbyItemViewState.Data>

  init {
    factory.create(owner, binding.lobbyListItem).inject(this)

    viewBinder =
        createViewBinder(
            clickHandler.requireNotNull(),
            background.requireNotNull(),
            backgroundContainer.requireNotNull(),
        ) {
          return@createViewBinder when (it) {
            is LobbyItemViewEvent.OnClick -> callback.onClick(bindingAdapterPosition)
            is LobbyItemViewEvent.OnCountdown -> callback.onCountdown(bindingAdapterPosition)
          }
        }

    owner.doOnDestroy { teardown() }
  }

  override fun bindState(state: LobbyItemViewState) {
    state.data?.also { viewBinder.bindState(it) }
  }

  override fun teardown() {
    viewBinder.teardown()

    clickHandler = null
    background = null
    backgroundContainer = null
  }
}
