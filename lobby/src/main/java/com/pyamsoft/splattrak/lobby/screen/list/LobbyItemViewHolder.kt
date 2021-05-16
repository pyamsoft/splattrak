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

import androidx.recyclerview.widget.RecyclerView
import com.pyamsoft.pydroid.arch.ViewBinder
import com.pyamsoft.pydroid.arch.createViewBinder
import com.pyamsoft.splattrak.lobby.databinding.LobbyListItemHolderBinding
import com.pyamsoft.splattrak.lobby.screen.LobbyListAdapter
import javax.inject.Inject

class LobbyItemViewHolder internal constructor(
    binding: LobbyListItemHolderBinding,
    factory: LobbyItemComponent.Factory,
    callback: LobbyListAdapter.Callback,
) : RecyclerView.ViewHolder(binding.root), ViewBinder<LobbyItemViewState> {

    @Inject
    @JvmField
    internal var clickHandler: LobbyItemClickHandler? = null

    @Inject
    @JvmField
    internal var background: LobbyItemBackground? = null

    @Inject
    @JvmField
    internal var backgroundContainer: LobbyItemBackgroundContainer? = null

    @Inject
    @JvmField
    internal var name: LobbyItemName? = null

    @Inject
    @JvmField
    internal var currentContainer: LobbyItemLargeContainer? = null

    @Inject
    @JvmField
    internal var currentInfo: LobbyItemCurrentInfo? = null

    @Inject
    @JvmField
    internal var currentStages: LobbyItemCurrentStages? = null

    @Inject
    @JvmField
    internal var nextContainer: LobbyItemNextContainer? = null

    @Inject
    @JvmField
    internal var nextCountdown: LobbyItemNextCountdown? = null

    @Inject
    @JvmField
    internal var nextInfo: LobbyItemNextInfo? = null

    @Inject
    @JvmField
    internal var nextStages: LobbyItemNextStages? = null

    private val viewBinder: ViewBinder<LobbyItemViewState>

    init {
        factory.create(binding.lobbyListItem).inject(this)

        val currentContainer = requireNotNull(currentContainer)
        currentContainer.nest(requireNotNull(currentInfo), requireNotNull(currentStages))

        val nextContainer = requireNotNull(nextContainer)
        nextContainer.nest(requireNotNull(nextInfo), requireNotNull(nextStages))

        val backgroundContainer = requireNotNull(backgroundContainer)
        backgroundContainer.nest(
            requireNotNull(name),
            currentContainer,
            requireNotNull(nextCountdown),
            nextContainer
        )

        viewBinder = createViewBinder(
            requireNotNull(clickHandler),
            requireNotNull(background),
            backgroundContainer,
        ) {
            return@createViewBinder when (it) {
                is LobbyItemViewEvent.OnClick -> callback.onClick(bindingAdapterPosition)
                is LobbyItemViewEvent.OnCountdown -> callback.onCountdown(bindingAdapterPosition)
            }
        }
    }

    override fun bindState(state: LobbyItemViewState) {
        viewBinder.bindState(state)
    }

    override fun teardown() {
        viewBinder.teardown()

        background = null
        name = null

        currentContainer = null
        currentInfo = null
        currentStages = null

        nextContainer = null
        nextInfo = null
        nextStages = null
    }

}
