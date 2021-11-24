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

import com.pyamsoft.highlander.highlander
import com.pyamsoft.pydroid.arch.AbstractViewModeler
import com.pyamsoft.pydroid.core.ResultWrapper
import com.pyamsoft.splattrak.splatnet.SplatnetInteractor
import com.pyamsoft.splattrak.splatnet.api.SplatBattle
import com.pyamsoft.splattrak.splatnet.api.SplatGameMode
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class DrilldownViewModeler
@Inject
internal constructor(
    private val state: MutableDrilldownViewState,
    splatnetInteractor: SplatnetInteractor,
    expectedMode: SplatGameMode.Mode,
) : AbstractViewModeler<DrilldownViewState>(state) {

  private val scheduleRunner =
      highlander<ResultWrapper<LobbyData>, Boolean> { force ->
        splatnetInteractor.schedule(force).map { schedule ->
          val entry = schedule.battles().find { it.mode().mode() == expectedMode }
          return@map if (entry != null) {
            LobbyData.Battle(entry)
          } else {
            val missingBattleError = IllegalStateException("Missing battle: ${expectedMode.name}")
            Timber.e(missingBattleError, "Failed to find battle")
            LobbyData.Error(missingBattleError)
          }
        }
      }

  fun handleRefresh(scope: CoroutineScope, force: Boolean) {
    scope.launch(context = Dispatchers.Main) {
      state.loading = true
      scheduleRunner
          .call(force)
          .onSuccess { data ->
            when (data) {
              is LobbyData.Battle -> {
                Timber.d("Loaded drilldown for battle: ${data.battle}")
                state.apply {
                  battle = data.battle
                  error = null
                }
              }
              is LobbyData.Error -> {
                Timber.w(data.error, "Error loading battle info")
                state.apply {
                  battle = null
                  error = data.error
                }
              }
            }
          }
          .onFailure { Timber.e(it, "Failed to load Splatoon2.ink lobby list") }
          .onFailure { state.error = it }
          .onFinally { state.loading = false }
    }
  }

  private sealed class LobbyData {
    data class Battle(val battle: SplatBattle) : LobbyData()
    data class Error(val error: Throwable) : LobbyData()
  }
}
