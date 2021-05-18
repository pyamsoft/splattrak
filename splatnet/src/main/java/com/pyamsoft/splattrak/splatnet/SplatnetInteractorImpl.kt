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

package com.pyamsoft.splattrak.splatnet

import androidx.annotation.CheckResult
import com.pyamsoft.cachify.MemoryCacheStorage
import com.pyamsoft.cachify.cachify
import com.pyamsoft.pydroid.core.Enforcer
import com.pyamsoft.splattrak.splatnet.api.*
import com.pyamsoft.splattrak.splatnet.data.*
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
internal class SplatnetInteractorImpl
@Inject
internal constructor(
    @InternalApi private val interactor: SplatnetInteractor,
) : SplatnetInteractor {

  private val scheduleCache =
      cachify<SplatSchedule>(storage = { listOf(MemoryCacheStorage.create(24, TimeUnit.HOURS)) }) {
        interactor.schedule()
      }

  override suspend fun schedule(): SplatSchedule =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()

        val schedule = scheduleCache.call()
        val now = LocalDateTime.now()
        val validRotations =
            schedule.battles().map { entry ->
              SplatBattleImpl(
                  mode = entry.mode(),
                  rotation =
                      entry
                          .rotation()
                          .asSequence()
                          .filter { filterPastMatches(it, now) }
                          .sortedWith(SORTER)
                          .toList())
            }

        return@withContext SplatScheduleImpl(validRotations)
      }

  companion object {

    private val SORTER = Comparator<SplatMatch> { o1, o2 -> o1.start().compareTo(o2.start()) }

    @CheckResult
    private fun filterPastMatches(match: SplatMatch, now: LocalDateTime): Boolean {
      val time = match.end()
      return time.isAfter(now) || time.isEqual(now)
    }
  }
}
