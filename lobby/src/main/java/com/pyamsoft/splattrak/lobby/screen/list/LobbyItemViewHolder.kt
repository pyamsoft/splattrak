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
import coil.ImageLoader
import com.pyamsoft.pydroid.util.doOnDestroy
import com.pyamsoft.splattrak.lobby.databinding.ComposeListItemBinding
import com.pyamsoft.splattrak.lobby.screen.LobbyListAdapter
import com.pyamsoft.splattrak.lobby.screen.LobbyListItem

class LobbyItemViewHolder
internal constructor(
    private val binding: ComposeListItemBinding,
    owner: LifecycleOwner,
    private val imageLoader: ImageLoader,
    private val callback: LobbyListAdapter.Callback,
) : BaseLobbyViewHolder(binding.root) {

  init {
    owner.doOnDestroy { teardown() }
  }

  override fun bindState(state: LobbyItemViewState) {
    binding.composeListItem.setContent {
      LobbyListItem(
          state = state,
          imageLoader = imageLoader,
          onClick = { callback.onClick(bindingAdapterPosition) },
          onCountdownCompleted = { callback.onCountdown(bindingAdapterPosition) },
      )
    }
  }

  override fun teardown() {
    binding.composeListItem.disposeComposition()
  }
}
