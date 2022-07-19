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
import com.pyamsoft.splattrak.splatnet.api.SplatBattle
import com.pyamsoft.splattrak.splatnet.api.SplatCoop
import com.pyamsoft.splattrak.splatnet.api.SplatCoopSession
import com.pyamsoft.splattrak.splatnet.api.SplatGameMode
import com.pyamsoft.splattrak.splatnet.api.SplatSchedule
import com.pyamsoft.splattrak.splatnet.data.SplatBattleImpl
import com.pyamsoft.splattrak.splatnet.data.SplatCoopImpl
import com.pyamsoft.splattrak.splatnet.data.SplatCoopMapImpl
import com.pyamsoft.splattrak.splatnet.data.SplatCoopSessionImpl
import com.pyamsoft.splattrak.splatnet.data.SplatCoopWeaponImpl
import com.pyamsoft.splattrak.splatnet.data.SplatGameModeImpl
import com.pyamsoft.splattrak.splatnet.data.SplatMapImpl
import com.pyamsoft.splattrak.splatnet.data.SplatMatchImpl
import com.pyamsoft.splattrak.splatnet.data.SplatRulesetImpl
import com.pyamsoft.splattrak.splatnet.data.SplatScheduleImpl
import com.pyamsoft.splattrak.splatnet.network.NetworkCoopSession
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

  override suspend fun coopSchedule(force: Boolean): ResultWrapper<SplatCoop> =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()

        return@withContext try {
          val networkSchedule = splatnet.coopSchedule()

          ResultWrapper.success(
              SplatCoopImpl(
                  name = "Salmon Run",
                  sessions =
                      networkSchedule.schedules.map { sched ->
                        val matchingDetails =
                            networkSchedule.details.firstOrNull {
                              it.startTime == sched.startTime && it.endTime == sched.endTime
                            }

                        val map =
                            if (matchingDetails == null) null
                            else createCoopSessionMap(matchingDetails)
                        return@map SplatCoopSessionImpl(
                            start = sched.startTime.toLocalDateTime(),
                            end = sched.endTime.toLocalDateTime(),
                            map = map,
                        )
                      },
              ))
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
    private fun createCoopSessionMap(details: NetworkCoopSession.Details): SplatCoopSession.Map {
      return SplatCoopMapImpl(
          map =
              SplatMapImpl(
                  name = details.stage.name,
                  image = details.stage.image,
              ),
          weapons =
              details.weapons.map { w ->
                SplatCoopWeaponImpl(
                    weaponName = w.weapon?.name,
                    weaponImage = w.weapon?.image,
                )
              },
      )
    }

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
      val battleMode =
          SplatGameModeImpl(
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
                    start = match.startTime.toLocalDateTime(),
                    end = match.endTime.toLocalDateTime(),
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
                    rules =
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
