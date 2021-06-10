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
import com.pyamsoft.splattrak.lobby.dialog.list.DrilldownItemComponent
import com.pyamsoft.splattrak.lobby.dialog.list.DrilldownItemViewState
import com.pyamsoft.splattrak.splatnet.api.SplatBattle
import com.pyamsoft.splattrak.splatnet.api.SplatMatch
import io.cabriole.decorator.LinearMarginDecoration
import javax.inject.Inject
import timber.log.Timber

class DrilldownList
@Inject
internal constructor(
    private val factory: DrilldownItemComponent.Factory,
    parent: ViewGroup,
) :
    BaseUiView<DrilldownViewState, DrilldownViewEvent, LobbyListBinding>(parent),
    SwipeRefreshLayout.OnRefreshListener {

  override val viewBinding = LobbyListBinding::inflate

  override val layoutRoot by boundView { lobbyListRoot }

  private var modelAdapter: DrilldownListAdapter? = null

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
      modelAdapter = DrilldownListAdapter(factory)
      binding.lobbyList.adapter = modelAdapter
    }

    doOnInflate { binding.lobbySwipeRefresh.setOnRefreshListener(this) }

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

    doOnTeardown { binding.lobbyList.removeAllItemDecorations() }

    doOnTeardown {
      binding.lobbyList.adapter = null

      binding.lobbySwipeRefresh.setOnRefreshListener(null)

      modelAdapter = null
    }
  }

  @CheckResult
  private fun usingAdapter(): DrilldownListAdapter {
    return requireNotNull(modelAdapter)
  }

  override fun onRefresh() {
    publish(DrilldownViewEvent.ForceRefresh)
  }

  override fun onRender(state: UiRender<DrilldownViewState>) {
    state.mapChanged { it.battle }.render(viewScope) { handleList(it) }
    state.mapChanged { it.loading }.render(viewScope) { handleLoading(it) }
  }

  private fun setList(matches: List<SplatMatch>) {
    val data = matches.map { DrilldownItemViewState(it) }
    usingAdapter().submitList(data)
  }

  private fun clearList() {
    usingAdapter().submitList(null)
  }

  private fun handleLoading(loading: Boolean) {
    binding.lobbySwipeRefresh.isRefreshing = loading
  }

  private fun handleList(battle: SplatBattle?) {
    if (battle == null) {
      clearList()
    } else {
      setList(battle.rotation())
    }
  }

  companion object {
    private const val LAST_SCROLL_POSITION = "drilldown_last_scroll_position"
  }
}
