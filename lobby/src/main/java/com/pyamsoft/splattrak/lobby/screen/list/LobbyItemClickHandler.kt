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

import android.view.ViewGroup
import com.pyamsoft.pydroid.arch.UiRender
import com.pyamsoft.pydroid.arch.UiView
import com.pyamsoft.pydroid.ui.util.setOnDebouncedClickListener
import javax.inject.Inject

class LobbyItemClickHandler @Inject internal constructor(
    private val parent: ViewGroup,
) : UiView<LobbyItemViewState.Data, LobbyItemViewEvent>() {

    init {
        doOnInflate {
            parent.setOnDebouncedClickListener {
                publish(LobbyItemViewEvent.OnClick)
            }
        }

        doOnTeardown {
            parent.setOnDebouncedClickListener(null)
        }
    }

    override fun render(state: UiRender<LobbyItemViewState.Data>) {
    }

}