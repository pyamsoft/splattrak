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
import com.pyamsoft.pydroid.arch.onActualError
import com.pyamsoft.splattrak.splatnet.SplatnetInteractor
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
      highlander<Unit> {
        setState(
            stateChange = { copy(loading = true) },
            andThen = {
              try {
                val schedule = splatnetInteractor.schedule()
                for (entry in schedule.battles()) {
                  if (entry.mode().mode() == expectedMode) {
                    return@setState setState { copy(battle = entry, loading = false) }
                  }
                }

                val missingBattleError =
                    IllegalStateException("Missing battle: ${expectedMode.name}")
                Timber.e(missingBattleError, "Failed to find battle")
                setState { copy(error = missingBattleError, loading = false) }
              } catch (error: Throwable) {
                error.onActualError { e ->
                  Timber.e(e, "Failed to load Splatoon2.ink lobby list")
                  setState { copy(error = e, loading = false) }
                }
              }
            })
      }

  init {
    performRefresh()
  }

  fun handleRefresh() {
    performRefresh()
  }

  private fun performRefresh() {
    viewModelScope.launch(context = Dispatchers.Default) { scheduleRunner.call() }
  }
}
