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
import com.pyamsoft.pydroid.bus.EventConsumer
import com.pyamsoft.pydroid.core.ResultWrapper
import com.pyamsoft.splattrak.splatnet.SplatnetInteractor
import com.pyamsoft.splattrak.splatnet.api.SplatSchedule
import com.pyamsoft.splattrak.ui.BottomOffset
import com.pyamsoft.splattrak.ui.TopOffset
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class LobbyViewModel
@Inject
internal constructor(
    splatnetInteractor: SplatnetInteractor,
    bottomOffsetBus: EventConsumer<BottomOffset>,
    topOffsetBus: EventConsumer<TopOffset>,
) :
    UiViewModel<LobbyViewState, LobbyControllerEvent>(
        LobbyViewState(
            rawSchedule = null,
            schedule = emptyList(),
            loading = false,
            error = null,
            bottomOffset = 0,
            topOffset = 0,
        ),
    ) {

  private val scheduleRunner =
      highlander<ResultWrapper<LobbyData>> {
        splatnetInteractor.schedule().map { schedule ->
          val groupings = mutableListOf<LobbyViewState.ScheduleGroupings>()
          for (entry in schedule.battles()) {
            val currentMatch = entry.rotation()[0]
            val nextMatch = entry.rotation()[1]
            groupings.add(LobbyViewState.ScheduleGroupings(currentMatch, nextMatch, entry))
          }

          LobbyData(groupings = groupings, schedule = schedule)
        }
      }

  init {
    viewModelScope.launch(context = Dispatchers.Default) {
      bottomOffsetBus.onEvent { setState { copy(bottomOffset = it.height) } }
    }

    viewModelScope.launch(context = Dispatchers.Default) {
      topOffsetBus.onEvent { setState { copy(topOffset = it.height) } }
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
    viewModelScope.launch(context = Dispatchers.Default) {
      setState(
          stateChange = { copy(loading = true) },
          andThen = {
            scheduleRunner
                .call()
                .onSuccess { data ->
                  setState {
                    copy(schedule = data.groupings, rawSchedule = data.schedule, loading = false)
                  }
                }
                .onFailure { Timber.e(it, "Failed to load Splatoon2.ink lobby list") }
                .onFailure { setState { copy(error = it, loading = false) } }
          })
    }
  }

  private data class LobbyData(
      val groupings: List<LobbyViewState.ScheduleGroupings>,
      val schedule: SplatSchedule
  )
}
