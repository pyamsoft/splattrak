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

package com.pyamsoft.splattrak.lobby.dialog.list

import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.pyamsoft.pydroid.arch.ViewBinder
import com.pyamsoft.pydroid.arch.createViewBinder
import com.pyamsoft.pydroid.util.doOnDestroy
import com.pyamsoft.splattrak.lobby.databinding.DrilldownListItemHolderBinding
import javax.inject.Inject

class DrilldownItemViewHolder
internal constructor(
    binding: DrilldownListItemHolderBinding,
    owner: LifecycleOwner,
    factory: DrilldownItemComponent.Factory,
) : RecyclerView.ViewHolder(binding.root), ViewBinder<DrilldownItemViewState> {

  @Inject @JvmField internal var container: DrilldownItemContainer? = null

  @Inject @JvmField internal var info: DrilldownItemInfo? = null

  @Inject @JvmField internal var stages: DrilldownItemStages? = null

  private val viewBinder: ViewBinder<DrilldownItemViewState>

  init {
    factory.create(binding.drilldownListItem).inject(this)

    val nextContainer = requireNotNull(container)
    nextContainer.nest(requireNotNull(info), requireNotNull(stages))

    viewBinder = createViewBinder(nextContainer) {}

    owner.doOnDestroy { teardown() }
  }

  override fun bindState(state: DrilldownItemViewState) {
    viewBinder.bindState(state)
  }

  override fun teardown() {
    viewBinder.teardown()

    container = null
    info = null
    stages = null
  }
}
