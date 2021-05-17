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

import com.pyamsoft.pydroid.arch.UnitViewState
import com.pyamsoft.pydroid.arch.ViewBinder
import com.pyamsoft.pydroid.arch.createViewBinder
import com.pyamsoft.pydroid.ui.databinding.ListitemFrameBinding
import com.pyamsoft.splattrak.ui.NintendoDisclaimer
import javax.inject.Inject

class LobbyDisclaimerViewHolder internal constructor(
    binding: ListitemFrameBinding,
    factory: LobbyItemComponent.Factory,
) : BaseLobbyViewHolder(binding.root), ViewBinder<LobbyItemViewState> {

    @Inject
    @JvmField
    internal var disclaimer: NintendoDisclaimer? = null


    private val viewBinder: ViewBinder<UnitViewState>

    init {
        factory.create(binding.listitemFrame).inject(this)

        viewBinder = createViewBinder(requireNotNull(disclaimer)) {}
    }

    override fun bindState(state: LobbyItemViewState) {
        viewBinder.bindState(UnitViewState)
    }

    override fun teardown() {
        viewBinder.teardown()
    }

}
