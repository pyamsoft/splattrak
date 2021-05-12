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

import androidx.annotation.CheckResult
import androidx.lifecycle.viewModelScope
import com.pyamsoft.highlander.highlander
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.arch.onActualError
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.splattrak.splatnet.SplatnetInteractor
import com.pyamsoft.splattrak.splatnet.api.SplatMatch
import com.pyamsoft.splattrak.ui.appbar.BottomOffset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject

class LobbyViewModel @Inject internal constructor(
    splatnetInteractor: SplatnetInteractor,
    private val bottomOffsetBus: EventBus<BottomOffset>,
) : UiViewModel<LobbyViewState, LobbyControllerEvent>(
    LobbyViewState(
        rawSchedule = null,
        schedule = emptyList(),
        loading = false,
        error = null,
        bottomOffset = 0
    )
) {

    private val scheduleRunner = highlander<Unit> {
        setState(stateChange = { copy(loading = true) }, andThen = { newState ->
            try {
                val now = LocalDateTime.now()
                val forceRefresh = decideIfWeNeedToRefresh(newState, now)
                val schedule = splatnetInteractor.schedule(forceRefresh)
                val groupings = mutableListOf<LobbyViewState.ScheduleGroupings>()
                for (entry in schedule.battles()) {
                    val futureRotation = entry.rotation()
                        .asSequence()
                        .filter { filterPastMatches(it, now) }
                        .sortedWith(SORTER)
                        .toList()
                    val currentMatch = futureRotation[0]
                    val nextMatch = futureRotation[1]
                    groupings.add(
                        LobbyViewState.ScheduleGroupings(
                            currentMatch,
                            nextMatch,
                            entry
                        )
                    )
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

    @CheckResult
    private fun decideIfWeNeedToRefresh(state: LobbyViewState, now: LocalDateTime): Boolean {
        val schedule = state.rawSchedule
        if (schedule == null) {
            Timber.d("Missing rawSchedule, need to refresh")
            return true
        }

        // If we have at least the "current" and "upcoming" in the list, we don't need to refresh
        for (battle in schedule.battles()) {
            val rotation = battle.rotation()
            if (rotation.size < REQUIRED_AMOUNT_OF_MATCHES) {
                val mode = battle.mode().mode()
                Timber.d("Rotation: $mode missing number of matches, need to refresh")
                return true
            }

            val currentAndFutureMatches = rotation.filter { filterPastMatches(it, now) }
            if (currentAndFutureMatches.size < REQUIRED_AMOUNT_OF_MATCHES) {
                val mode = battle.mode().mode()
                Timber.d("Rotation: $mode missing number of future matches, need to refresh")
                return true
            }
        }

        Timber.d("Ignore refresh request since current data is valid.")
        return false
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
            scheduleRunner.call()
        }
    }

    companion object {

        private const val REQUIRED_AMOUNT_OF_MATCHES = 6
        private val SORTER = Comparator<SplatMatch> { o1, o2 -> o1.start().compareTo(o2.start()) }

        @CheckResult
        private fun filterPastMatches(match: SplatMatch, now: LocalDateTime): Boolean {
            val time = match.end()
            return time.isAfter(now) || time.isEqual(now)
        }
    }
}
