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

import android.view.ViewGroup
import com.pyamsoft.splattrak.lobby.item.BaseLobbyInfo
import com.pyamsoft.splattrak.lobby.item.LobbyItemViewState
import com.pyamsoft.splattrak.splatnet.api.SplatMatch

abstract class GenericLobbyItemInfo protected constructor(
    parent: ViewGroup,
    private val isLarge: Boolean,
    private val matchResolver: () -> Int,
) : BaseLobbyInfo<LobbyItemViewState>(parent) {

    final override fun isLarge(): Boolean {
        return isLarge
    }

    final override fun getMatch(state: LobbyItemViewState): SplatMatch {
        val index = matchResolver()
        val battles = state.battle.rotation()
        return battles[index]
    }
}
