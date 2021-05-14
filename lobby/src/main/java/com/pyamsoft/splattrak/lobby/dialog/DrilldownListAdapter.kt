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

package com.pyamsoft.splattrak.lobby.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pyamsoft.pydroid.ui.util.teardownAdapter
import com.pyamsoft.splattrak.lobby.databinding.DrilldownListItemHolderBinding
import com.pyamsoft.splattrak.lobby.databinding.LobbyListItemHolderBinding
import com.pyamsoft.splattrak.lobby.dialog.list.DrilldownItemComponent
import com.pyamsoft.splattrak.lobby.dialog.list.DrilldownItemViewHolder
import com.pyamsoft.splattrak.lobby.dialog.list.DrilldownItemViewState

class DrilldownListAdapter internal constructor(
    private val factory: DrilldownItemComponent.Factory,
) : ListAdapter<DrilldownItemViewState, DrilldownItemViewHolder>(DIFFER) {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).match.id()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrilldownItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DrilldownListItemHolderBinding.inflate(inflater, parent, false)
        return DrilldownItemViewHolder(binding, factory)
    }

    override fun onBindViewHolder(holder: DrilldownItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bindState(item)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        teardownAdapter(recyclerView)
    }

    companion object {

        private val DIFFER = object : DiffUtil.ItemCallback<DrilldownItemViewState>() {

            override fun areItemsTheSame(
                oldItem: DrilldownItemViewState,
                newItem: DrilldownItemViewState,
            ): Boolean {
                return oldItem.match.id() == newItem.match.id()
            }

            override fun areContentsTheSame(
                oldItem: DrilldownItemViewState,
                newItem: DrilldownItemViewState,
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

}
