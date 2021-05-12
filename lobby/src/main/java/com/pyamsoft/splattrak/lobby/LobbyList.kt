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

package com.pyamsoft.splattrak.lobby

import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.arch.UiRender
import com.pyamsoft.pydroid.ui.util.removeAllItemDecorations
import com.pyamsoft.pydroid.util.asDp
import com.pyamsoft.splattrak.lobby.databinding.LobbyListBinding
import com.pyamsoft.splattrak.lobby.screen.LobbyItemComponent
import com.pyamsoft.splattrak.lobby.item.LobbyItemViewState
import io.cabriole.decorator.LinearMarginDecoration
import me.zhanghai.android.fastscroll.FastScrollerBuilder
import timber.log.Timber
import javax.inject.Inject

class LobbyList @Inject internal constructor(
    private val factory: LobbyItemComponent.Factory,
    parent: ViewGroup,
) : BaseUiView<LobbyViewState, LobbyViewEvent, LobbyListBinding>(parent),
    SwipeRefreshLayout.OnRefreshListener,
    LobbyListAdapter.Callback {

    override val viewBinding = LobbyListBinding::inflate

    override val layoutRoot by boundView { lobbyListRoot }

    private var modelAdapter: LobbyListAdapter? = null

    private var lastScrollPosition = 0

    init {
        doOnInflate {
            binding.lobbyList.layoutManager =
                LinearLayoutManager(binding.lobbyList.context).apply {
                    isItemPrefetchEnabled = true
                    initialPrefetchItemCount = 3
                }
        }

        doOnInflate {
            modelAdapter = LobbyListAdapter(factory, callback = this)
            binding.lobbyList.adapter = modelAdapter
        }

        doOnInflate {
            // Fast Scroll
            FastScrollerBuilder(binding.lobbyList)
                .useMd2Style()
                .setPopupTextProvider(usingAdapter())
                .build()
        }

        doOnInflate {
            binding.lobbySwipeRefresh.setOnRefreshListener(this)
        }

        doOnInflate { savedInstanceState ->
            val position = savedInstanceState.get(LAST_SCROLL_POSITION) ?: -1
            if (position >= 0) {
                Timber.d("Last scroll position saved at: $position")
                lastScrollPosition = position
            }
        }

        doOnSaveState { outState ->
            val manager = binding.lobbyList.layoutManager
            if (manager is GridLayoutManager) {
                val position = manager.findFirstVisibleItemPosition()
                if (position > 0) {
                    outState.put(LAST_SCROLL_POSITION, position)
                    return@doOnSaveState
                }
            }

            outState.remove<Nothing>(LAST_SCROLL_POSITION)
        }

        doOnInflate {
            val margin = 16.asDp(binding.lobbyList.context)

            // Standard margin on all items
            // For some reason, the margin registers only half as large as it needs to
            // be, so we must double it.
            LinearMarginDecoration.create(margin = margin).apply {
                binding.lobbyList.addItemDecoration(this)
            }
        }

        doOnTeardown {
            binding.lobbyList.removeAllItemDecorations()
        }

        doOnTeardown {
            clearList()
            binding.lobbyList.adapter = null

            binding.lobbySwipeRefresh.setOnRefreshListener(null)

            modelAdapter = null
        }
    }


    @CheckResult
    private fun usingAdapter(): LobbyListAdapter {
        return requireNotNull(modelAdapter)
    }

    override fun onRefresh() {
        publish(LobbyViewEvent.ForceRefresh)
    }

    override fun onClick(index: Int) {
        publish(LobbyViewEvent.ViewBattleRotation(index))
    }

    override fun onRender(state: UiRender<LobbyViewState>) {
        state.mapChanged { it.schedule }.render(viewScope) { handleList(it) }
        state.mapChanged { it.loading }.render(viewScope) { handleLoading(it) }
    }

    private fun setList(groupings: List<LobbyViewState.ScheduleGroupings>) {
        val data = groupings.map { LobbyItemViewState(it.currentMatch, it.nextMatch, it.battle) }
        Timber.d("Submit data list: $data")
        usingAdapter().submitList(data)
    }

    private fun clearList() {
        usingAdapter().submitList(null)
    }

    private fun handleLoading(loading: Boolean) {
        binding.lobbySwipeRefresh.isRefreshing = loading
    }

    private fun handleList(schedule: List<LobbyViewState.ScheduleGroupings>) {
        if (schedule.isEmpty()) {
            clearList()
        } else {
            setList(schedule)
        }
    }

    companion object {
        private const val LAST_SCROLL_POSITION = "lobby_last_scroll_position"
    }

}
