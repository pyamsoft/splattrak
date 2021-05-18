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

import android.view.ViewGroup
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.splattrak.lobby.item.BaseLobbyStages
import com.pyamsoft.splattrak.splatnet.api.SplatMatch
import javax.inject.Inject

class DrilldownItemStages
@Inject
internal constructor(
    imageLoader: ImageLoader,
    parent: ViewGroup,
) : BaseLobbyStages<DrilldownItemViewState>(imageLoader, parent) {
  override fun isLarge(): Boolean {
    return false
  }

  override fun getMatch(state: DrilldownItemViewState): SplatMatch {
    return state.match
  }
}
