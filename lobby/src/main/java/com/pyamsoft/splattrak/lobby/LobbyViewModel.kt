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

import androidx.lifecycle.viewModelScope
import com.pyamsoft.highlander.highlander
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.core.ResultWrapper
import com.pyamsoft.splattrak.splatnet.SplatnetInteractor
import com.pyamsoft.splattrak.splatnet.api.SplatBattle
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class LobbyViewModel
@Inject
internal constructor(
    splatnetInteractor: SplatnetInteractor,
) :
    UiViewModel<LobbyViewState, LobbyControllerEvent>(
        LobbyViewState(
            schedule = emptyList(),
            loading = false,
            error = null,
        ),
    ) {

  private val scheduleRunner =
      highlander<ResultWrapper<List<SplatBattle>>, Boolean> { force ->
        splatnetInteractor.schedule(force).map { it.battles() }
      }

  init {
    performRefresh(false)
  }

  fun handleOpenBattle(index: Int) {
    viewModelScope.launch(context = Dispatchers.Default) {
      val schedule = state.schedule
      if (schedule.isNotEmpty()) {
        val battle = schedule[index]
        publish(LobbyControllerEvent.OpenBattleRotation(battle))
      }
    }
  }

  fun handleRefresh() {
    performRefresh(true)
  }

  private fun performRefresh(force: Boolean) {
    viewModelScope.launch(context = Dispatchers.Default) {
      setState(
          stateChange = {
            copy(
                loading = true,
                error = null,
                schedule = emptyList(),
            )
          },
          andThen = {
            scheduleRunner
                .call(force)
                .onSuccess { data -> setState { copy(schedule = data) } }
                .onFailure { Timber.e(it, "Failed to load Splatoon2.ink lobby list") }
                .onFailure { setState { copy(error = it) } }
                .onFinally { setState { copy(loading = false) } }
          },
      )
    }
  }
}
