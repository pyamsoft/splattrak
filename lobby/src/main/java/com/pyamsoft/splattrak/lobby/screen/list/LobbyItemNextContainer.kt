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
import androidx.core.view.updateLayoutParams
import com.pyamsoft.pydroid.util.asDp
import com.pyamsoft.splattrak.lobby.item.LobbyItemContainer
import javax.inject.Inject

class LobbyItemNextContainer @Inject internal constructor(
    parent: ViewGroup,
) : LobbyItemContainer<LobbyItemViewState>(parent) {

    init {
        doOnInflate {
            layoutRoot.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                this.topMargin = 16.asDp(layoutRoot.context.applicationContext)
            }
        }
    }

}
