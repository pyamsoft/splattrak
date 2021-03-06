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
import com.pyamsoft.pydroid.core.ResultWrapper
import com.pyamsoft.splattrak.splatnet.api.SplatCoop
import com.pyamsoft.splattrak.splatnet.api.SplatCoopSession
import com.pyamsoft.splattrak.splatnet.api.SplatMatch
import com.pyamsoft.splattrak.splatnet.api.SplatSchedule
import com.pyamsoft.splattrak.splatnet.data.SplatBattleImpl
import com.pyamsoft.splattrak.splatnet.data.SplatCoopImpl
import com.pyamsoft.splattrak.splatnet.data.SplatScheduleImpl
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

@Singleton
internal class SplatnetInteractorImpl
@Inject
internal constructor(
    @InternalApi private val interactor: SplatnetInteractor,
) : SplatnetInteractor {

  private val scheduleCache =
      cachify<ResultWrapper<SplatSchedule>>(
          storage = { listOf(MemoryCacheStorage.create(24, TimeUnit.HOURS)) }) {
        interactor.schedule(true)
      }

  private val coopCache =
      cachify<ResultWrapper<SplatCoop>>(
          storage = { listOf(MemoryCacheStorage.create(24, TimeUnit.HOURS)) }) {
        interactor.coopSchedule(true)
      }

  override suspend fun schedule(force: Boolean): ResultWrapper<SplatSchedule> =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()

        if (force) {
          scheduleCache.clear()
        }

        return@withContext scheduleCache
            .call()
            .onFailure { scheduleCache.clear() }
            .onFailure { Timber.e(it, "Error fetching splat lobby schedule") }
            .map<SplatSchedule> { s ->
              val now = LocalDateTime.now()
              val validRotations =
                  s.battles.map { entry ->
                    SplatBattleImpl(
                        mode = entry.mode,
                        rotation =
                            entry
                                .rotation
                                .asSequence()
                                .filter { filterPastMatches(it, now) }
                                .sortedWith(SCHEDULE_SORTER)
                                .toList(),
                    )
                  }

              return@map SplatScheduleImpl(validRotations)
            }
      }

  override suspend fun coopSchedule(force: Boolean): ResultWrapper<SplatCoop> =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()

        if (force) {
          coopCache.clear()
        }

        return@withContext coopCache
            .call()
            .onFailure { coopCache.clear() }
            .onFailure { Timber.e(it, "Error fetching splat coop schedule") }
            .map<SplatCoop> { coop ->
              return@map SplatCoopImpl(
                  name = coop.name,
                  sessions = coop.sessions.sortedWith(COOP_SORTER),
              )
            }
      }

  companion object {

    private val SCHEDULE_SORTER = Comparator<SplatMatch> { o1, o2 -> o1.start.compareTo(o2.start) }

    private val COOP_SORTER =
        Comparator<SplatCoopSession> { o1, o2 -> o1.start.compareTo(o2.start) }

    @CheckResult
    private fun filterPastMatches(match: SplatMatch, now: LocalDateTime): Boolean {
      val time = match.end
      return time.isAfter(now) || time.isEqual(now)
    }
  }
}
