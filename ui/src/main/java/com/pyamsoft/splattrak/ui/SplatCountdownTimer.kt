/*
 * Copyright 2022 Peter Kenji Yamanaka
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

package com.pyamsoft.splattrak.ui

import androidx.annotation.CheckResult
import java.time.Duration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplatCountdownTimer(
    private val totalSeconds: Long,
    onUpdate: (String, Boolean) -> Unit,
) {

  private var updater: ((String, Boolean) -> Unit)? = onUpdate
  private var timer: Job? = null

  suspend fun start() =
      withContext(context = Dispatchers.IO) {
        timer?.cancel()
        timer =
            launch(context = Dispatchers.IO) {
              createTimer(totalSeconds).collect { remainingSeconds ->
                withContext(context = Dispatchers.Main) {
                  if (remainingSeconds <= 0) {
                    updater?.invoke("Starting Now!", true)
                  } else {
                    val timeTo = Duration.ofSeconds(remainingSeconds)
                    val totalSeconds = timeTo.seconds
                    val hours = totalSeconds / 3600
                    val minutes = (totalSeconds % 3600) / 60
                    val seconds = totalSeconds % 60
                    val formattedString = "%d:%02d:%02d".format(hours, minutes, seconds)
                    updater?.invoke(formattedString, false)
                  }
                }
              }
            }
      }

  fun cancel() {
    timer?.cancel()
    timer = null

    updater = null
  }

  companion object {

    @JvmStatic
    @CheckResult
    private fun createTimer(totalSeconds: Long): Flow<Long> {
      val range = totalSeconds - 1 downTo 0
      return range
          .asFlow()
          .onEach { delay(1000L) }
          .onStart { emit(totalSeconds) }
          .distinctUntilChanged()
    }
  }
}
