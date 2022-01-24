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
import com.pyamsoft.pydroid.core.Enforcer
import com.pyamsoft.pydroid.core.ResultWrapper
import com.pyamsoft.pydroid.util.ifNotCancellation
import com.pyamsoft.splattrak.splatnet.api.*
import com.pyamsoft.splattrak.splatnet.data.*
import com.pyamsoft.splattrak.splatnet.network.NetworkSplatMatch
import com.pyamsoft.splattrak.splatnet.service.Splatnet
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

internal class SplatnetNetworkInteractor
@Inject
internal constructor(
    @InternalApi private val splatnet: Splatnet,
) : SplatnetInteractor {

  override suspend fun schedule(force: Boolean): ResultWrapper<SplatSchedule> =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()

        return@withContext try {
          val networkSchedule = splatnet.lobbySchedule()
          ResultWrapper.success(
              SplatScheduleImpl(
                  listOf(
                      assembleBattles(SplatGameMode.Mode.REGULAR, networkSchedule.regular),
                      assembleBattles(SplatGameMode.Mode.LEAGUE, networkSchedule.league),
                      assembleBattles(SplatGameMode.Mode.RANKED, networkSchedule.ranked),
                  ),
              ),
          )
        } catch (e: Throwable) {
          e.ifNotCancellation {
            Timber.e(e, "Failed to get Lobby schedule")
            ResultWrapper.failure(e)
          }
        }
      }

  companion object {

    @JvmStatic
    @CheckResult
    fun Long.toLocalDateTime(): LocalDateTime {
      return LocalDateTime.ofInstant(Instant.ofEpochSecond(this), ZoneId.systemDefault())
          .truncatedTo(ChronoUnit.MINUTES)
    }

    @JvmStatic
    @CheckResult
    private fun assembleBattles(
        gameMode: SplatGameMode.Mode,
        list: List<NetworkSplatMatch>,
    ): SplatBattle {
      val mode = list.first().gameMode
      val battleMode = SplatGameModeImpl(
          key = mode.key,
          name = mode.name,
          mode = gameMode,
      )

      return SplatBattleImpl(
          mode = battleMode,
          rotation =
              list.map { match ->
                SplatMatchImpl(
                    id = match.id,
                    startTime = match.startTime.toLocalDateTime(),
                    endTime = match.endTime.toLocalDateTime(),
                    stageA =
                        SplatMapImpl(
                            name = match.stageA.name,
                            image = match.stageA.image,
                        ),
                    stageB =
                        SplatMapImpl(
                            name = match.stageB.name,
                            image = match.stageB.image,
                        ),
                    gameMode =
                        SplatGameModeImpl(
                            key = match.gameMode.key,
                            name = match.gameMode.name,
                            mode = gameMode,
                        ),
                    rule =
                        SplatRulesetImpl(
                            key = match.rule.key,
                            name = match.rule.name,
                        ),
                )
              },
      )
    }
  }
}
