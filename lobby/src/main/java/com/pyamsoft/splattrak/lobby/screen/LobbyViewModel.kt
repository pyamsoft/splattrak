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

import androidx.lifecycle.viewModelScope
import com.pyamsoft.highlander.highlander
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.arch.onActualError
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.splattrak.splatnet.SplatnetInteractor
import com.pyamsoft.splattrak.ui.BottomOffset
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class LobbyViewModel
@Inject
internal constructor(
    splatnetInteractor: SplatnetInteractor,
    private val bottomOffsetBus: EventBus<BottomOffset>,
) :
    UiViewModel<LobbyViewState, LobbyControllerEvent>(
        LobbyViewState(
            rawSchedule = null,
            schedule = emptyList(),
            loading = false,
            error = null,
            bottomOffset = 0)) {

  private val scheduleRunner =
      highlander<Unit> {
        setState(
            stateChange = { copy(loading = true) },
            andThen = {
              try {
                val schedule = splatnetInteractor.schedule()
                val groupings = mutableListOf<LobbyViewState.ScheduleGroupings>()
                for (entry in schedule.battles()) {
                  val currentMatch = entry.rotation()[0]
                  val nextMatch = entry.rotation()[1]
                  groupings.add(LobbyViewState.ScheduleGroupings(currentMatch, nextMatch, entry))
                }
                setState { copy(schedule = groupings, rawSchedule = schedule, loading = false) }
              } catch (error: Throwable) {
                error.onActualError { e ->
                  Timber.e(e, "Failed to load Splatoon2.ink lobby list")
                  setState { copy(error = e, loading = false) }
                }
              }
            })
      }

  init {

    viewModelScope.launch(context = Dispatchers.Default) {
      bottomOffsetBus.onEvent { setState { copy(bottomOffset = it.height) } }
    }

    performRefresh()
  }

  fun handleOpenBattle(index: Int) {
    viewModelScope.launch(context = Dispatchers.Default) {
      val schedule = state.schedule
      if (schedule.isNotEmpty()) {
        val entry = schedule[index]
        publish(LobbyControllerEvent.OpenBattleRotation(entry.battle))
      }
    }
  }

  fun handleRefresh() {
    performRefresh()
  }

  private fun performRefresh() {
    viewModelScope.launch(context = Dispatchers.Default) { scheduleRunner.call() }
  }
}
