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

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.pyamsoft.pydroid.ui.databinding.ListitemFrameBinding
import com.pyamsoft.splattrak.lobby.databinding.LobbyListItemHolderBinding
import com.pyamsoft.splattrak.lobby.screen.list.BaseLobbyViewHolder
import com.pyamsoft.splattrak.lobby.screen.list.LobbyDisclaimerViewHolder
import com.pyamsoft.splattrak.lobby.screen.list.LobbyItemComponent
import com.pyamsoft.splattrak.lobby.screen.list.LobbyItemViewHolder
import com.pyamsoft.splattrak.lobby.screen.list.LobbyItemViewState

class LobbyListAdapter
internal constructor(
    private val owner: LifecycleOwner,
    private val factory: LobbyItemComponent.Factory,
    private val callback: Callback,
) : ListAdapter<LobbyItemViewState, BaseLobbyViewHolder>(DIFFER) {

  init {
    setHasStableIds(true)
  }

  override fun getItemViewType(position: Int): Int {
    return if (getItem(position).isDisclaimer) VIEW_TYPE_DISCLAIMER else VIEW_TYPE_ITEM
  }

  override fun getItemId(position: Int): Long {
    val item = getItem(position)
    return if (item.isDisclaimer) 0
    else requireNotNull(item.data).battle.mode().key().hashCode().toLong()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseLobbyViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    return if (viewType == VIEW_TYPE_ITEM) {
      val binding = LobbyListItemHolderBinding.inflate(inflater, parent, false)
      LobbyItemViewHolder(binding, owner, factory, callback)
    } else {
      val binding = ListitemFrameBinding.inflate(inflater, parent, false)
      LobbyDisclaimerViewHolder(binding, owner, factory)
    }
  }

  override fun onBindViewHolder(holder: BaseLobbyViewHolder, position: Int) {
    val item = getItem(position)
    holder.bindState(item)
  }

  interface Callback {

    fun onClick(index: Int)

    fun onCountdown(index: Int)
  }

  companion object {

    private const val VIEW_TYPE_ITEM = 0
    private const val VIEW_TYPE_DISCLAIMER = 1

    private val DIFFER =
        object : DiffUtil.ItemCallback<LobbyItemViewState>() {

          override fun areItemsTheSame(
              oldItem: LobbyItemViewState,
              newItem: LobbyItemViewState,
          ): Boolean {
            if (oldItem.isDisclaimer == newItem.isDisclaimer) {
              return true
            }

            if (!oldItem.isDisclaimer != newItem.isDisclaimer) {
              return false
            }

            val oldKey = requireNotNull(oldItem.data).battle.mode().key()
            val newKey = requireNotNull(newItem.data).battle.mode().key()
            return oldKey == newKey
          }

          override fun areContentsTheSame(
              oldItem: LobbyItemViewState,
              newItem: LobbyItemViewState,
          ): Boolean {
            return oldItem == newItem
          }
        }
  }
}
