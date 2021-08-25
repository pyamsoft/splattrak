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

package com.pyamsoft.splattrak.setting

import androidx.recyclerview.widget.RecyclerView
import com.pyamsoft.pydroid.arch.UiRender
import com.pyamsoft.pydroid.arch.UiView
import com.pyamsoft.pydroid.arch.UnitViewEvent
import com.pyamsoft.pydroid.ui.util.removeAllItemDecorations
import io.cabriole.decorator.LinearBoundsMarginDecoration
import javax.inject.Inject

class SettingsTopSpacer
@Inject
internal constructor(
    private val listView: RecyclerView,
) : UiView<SettingsViewState, UnitViewEvent>() {

  private val topDecoration = LinearBoundsMarginDecoration(topMargin = 0)

  init {
    doOnInflate { listView.addItemDecoration(topDecoration) }

    doOnTeardown { listView.removeAllItemDecorations() }
  }

  override fun render(state: UiRender<SettingsViewState>) {
    state.mapChanged { it.topOffset }.render(viewScope) { handleTopOffset(it) }
  }

  private fun handleTopOffset(height: Int) {
    topDecoration.setMargin(top = height)
    listView.invalidateItemDecorations()
  }
}
