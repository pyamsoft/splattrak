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
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pyamsoft.pydroid.ui.util.teardownAdapter
import com.pyamsoft.splattrak.lobby.databinding.LobbyListItemHolderBinding
import com.pyamsoft.splattrak.lobby.screen.list.LobbyItemComponent
import com.pyamsoft.splattrak.lobby.screen.list.LobbyItemViewHolder
import com.pyamsoft.splattrak.lobby.screen.list.LobbyItemViewState
import me.zhanghai.android.fastscroll.PopupTextProvider

class LobbyListAdapter internal constructor(
    private val factory: LobbyItemComponent.Factory,
    private val callback: Callback
) : ListAdapter<LobbyItemViewState, LobbyItemViewHolder>(DIFFER), PopupTextProvider {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).battle.mode().key().hashCode().toLong()
    }

    override fun getPopupText(position: Int): String {
        val item = getItem(position)
        return item.battle.mode().name()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LobbyItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LobbyListItemHolderBinding.inflate(inflater, parent, false)
        return LobbyItemViewHolder(binding, factory, callback)
    }

    override fun onBindViewHolder(holder: LobbyItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bindState(item)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        teardownAdapter(recyclerView)
    }

    interface Callback {

        fun onClick(index: Int)

    }

    companion object {

        private val DIFFER = object : DiffUtil.ItemCallback<LobbyItemViewState>() {

            override fun areItemsTheSame(
                oldItem: LobbyItemViewState,
                newItem: LobbyItemViewState
            ): Boolean {
                return oldItem.battle.mode().key() == newItem.battle.mode().key()
            }

            override fun areContentsTheSame(
                oldItem: LobbyItemViewState,
                newItem: LobbyItemViewState
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

}
