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

import androidx.lifecycle.viewModelScope
import com.pyamsoft.highlander.highlander
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.core.ResultWrapper
import com.pyamsoft.splattrak.splatnet.SplatnetInteractor
import com.pyamsoft.splattrak.splatnet.api.SplatBattle
import com.pyamsoft.splattrak.splatnet.api.SplatGameMode
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class DrilldownViewModel
@Inject
internal constructor(
    splatnetInteractor: SplatnetInteractor,
    expectedMode: SplatGameMode.Mode,
) :
    UiViewModel<DrilldownViewState, Nothing>(
        DrilldownViewState(
            battle = null,
            loading = false,
            error = null,
        )) {

  private val scheduleRunner =
      highlander<ResultWrapper<LobbyData>> {
        splatnetInteractor.schedule().map { schedule ->
          for (entry in schedule.battles()) {
            if (entry.mode().mode() == expectedMode) {
              return@map LobbyData.Battle(entry)
            }
          }

          val missingBattleError = IllegalStateException("Missing battle: ${expectedMode.name}")
          Timber.e(missingBattleError, "Failed to find battle")
          LobbyData.Error(missingBattleError)
        }
      }

  init {
    performRefresh()
  }

  fun handleRefresh() {
    performRefresh()
  }

  private fun performRefresh() {
    viewModelScope.launch(context = Dispatchers.Default) {
      setState(
          stateChange = { copy(loading = true) },
          andThen = {
            scheduleRunner
                .call()
                .onSuccess { data ->
                  when (data) {
                    is LobbyData.Battle -> setState { copy(battle = data.battle, loading = false) }
                    is LobbyData.Error -> setState { copy(error = data.error, loading = false) }
                  }
                }
                .onFailure { Timber.e(it, "Failed to load Splatoon2.ink lobby list") }
                .onFailure { setState { copy(error = it, loading = false) } }
          })
    }
  }

  private sealed class LobbyData {
    data class Battle(val battle: SplatBattle) : LobbyData()
    data class Error(val error: Throwable) : LobbyData()
  }
}
