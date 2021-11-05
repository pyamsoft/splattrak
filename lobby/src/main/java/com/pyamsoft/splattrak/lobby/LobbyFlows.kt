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

import androidx.compose.runtime.Stable
import com.pyamsoft.pydroid.arch.UiControllerEvent
import com.pyamsoft.pydroid.arch.UiViewState
import com.pyamsoft.splattrak.splatnet.api.SplatBattle

@Stable
data class LobbyViewState
internal constructor(
    val schedule: List<SplatBattle>,
    val error: Throwable?,
    val loading: Boolean,
) : UiViewState

sealed class LobbyControllerEvent : UiControllerEvent {

  data class OpenBattleRotation
  internal constructor(
      val battle: SplatBattle,
  ) : LobbyControllerEvent()
}
