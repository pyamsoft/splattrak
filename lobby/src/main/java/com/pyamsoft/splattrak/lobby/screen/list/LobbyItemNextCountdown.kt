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
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.arch.UiRender
import com.pyamsoft.splattrak.lobby.databinding.LobbyItemCountdownBinding
import timber.log.Timber
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

    private var timer: SplatCountdownTimer? = null

    init {
        doOnTeardown {
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
        val timeUntilStart = now.until(time, ChronoUnit.SECONDS)
        timer?.cancel()
        timer = SplatCountdownTimer(viewScope, timeUntilStart) { display, isComplete ->
            binding.lobbyItemNextCountdownText.text = display
            if (isComplete) {
                publish(LobbyItemViewEvent.OnCountdown)
            }
        }.apply {
            start()
        }
    }

}
