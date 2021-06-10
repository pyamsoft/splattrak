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

package com.pyamsoft.splattrak.lobby.screen.list

import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.arch.UiRender
import com.pyamsoft.splattrak.lobby.databinding.LobbyItemCountdownBinding
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class LobbyItemNextCountdown
@Inject
internal constructor(
    owner: LifecycleOwner,
    parent: ViewGroup,
) :
    BaseUiView<LobbyItemViewState.Data, LobbyItemViewEvent, LobbyItemCountdownBinding>(
        parent,
    ) {

  override val layoutRoot by boundView { this.lobbyItemNextCountdown }

  override val viewBinding = LobbyItemCountdownBinding::inflate

  private var nextStartTime: LocalDateTime? = null
  private var timer: SplatCountdownTimer? = null

  init {
    doOnTeardown { pauseTimer() }

    doOnTeardown { nextStartTime = null }

    val observer =
        object : LifecycleObserver {

          @Suppress("unused")
          @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
          fun onResume() {
            resumeTimer()
          }

          @Suppress("unused")
          @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
          fun onPause() {
            pauseTimer()
          }

          @Suppress("unused")
          @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
          fun onDestroy() {
            owner.lifecycle.removeObserver(this)
          }
        }

    owner.lifecycle.addObserver(observer)
  }

  override fun onRender(state: UiRender<LobbyItemViewState.Data>) {
    state.mapChanged { it.nextMatch }.mapChanged { it.start() }.render(viewScope) {
      handleNextStartTime(it)
    }
  }

  private fun handleNextStartTime(time: LocalDateTime) {
    nextStartTime = time
    resumeTimer()
  }

  private fun pauseTimer() {
    timer?.cancel()
    timer = null
  }

  private fun resumeTimer() {
    val time = nextStartTime ?: return
    val now = LocalDateTime.now()
    val timeUntilStart = now.until(time, ChronoUnit.SECONDS)
    pauseTimer()
    timer =
        SplatCountdownTimer(viewScope, timeUntilStart) { display, isComplete ->
          binding.lobbyItemNextCountdownText.text = display
          if (isComplete) {
            publish(LobbyItemViewEvent.OnCountdown)
          }
        }
            .apply { start() }
  }
}
