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

import android.os.CountDownTimer
import android.view.ViewGroup
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.arch.UiRender
import com.pyamsoft.splattrak.lobby.databinding.LobbyItemCountdownBinding
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class LobbyItemNextCountdown @Inject internal constructor(
    parent: ViewGroup,
) : BaseUiView<LobbyItemViewState.Data, LobbyItemViewEvent, LobbyItemCountdownBinding>(
    parent,
) {

    override val layoutRoot by boundView { this.lobbyItemNextCountdown }

    override val viewBinding = LobbyItemCountdownBinding::inflate

    private var timer: CountDownTimer? = null

    init {
        doOnInflate {
            timer?.cancel()
            timer = null
        }
    }

    override fun onRender(state: UiRender<LobbyItemViewState.Data>) {
        state
            .mapChanged { it.nextMatch }
            .mapChanged { it.start() }
            .render(viewScope) { handleNextStartTime(it) }
    }

    private fun handleNextStartTime(time: LocalDateTime) {
        val now = LocalDateTime.now()
        val timeUntilStart = now.until(time, ChronoUnit.MILLIS);
        timer?.cancel()
        timer = object : CountDownTimer(timeUntilStart, 1000L) {

            override fun onTick(millisUntilFinished: Long) {
                val timeTo = Duration.ofMillis(millisUntilFinished)
                val totalSeconds = timeTo.seconds
                val hours = totalSeconds / 3600
                val minutes = (totalSeconds % 3600) / 60
                val seconds = totalSeconds % 60
                val formattedString = "%d:%02d:%02d".format(hours, minutes, seconds)
                binding.lobbyItemNextCountdownText.text = "in $formattedString"
            }

            override fun onFinish() {
                binding.lobbyItemNextCountdownText.text = "Starting Now!"
                publish(LobbyItemViewEvent.OnCountdown)
            }
        }.apply {
            start()
        }
    }

}
