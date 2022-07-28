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

package com.pyamsoft.splattrak.ui.test

import com.pyamsoft.splattrak.splatnet.api.SplatBattle
import com.pyamsoft.splattrak.splatnet.api.SplatCoop
import com.pyamsoft.splattrak.splatnet.api.SplatCoopSession
import com.pyamsoft.splattrak.splatnet.api.SplatGameMode
import com.pyamsoft.splattrak.splatnet.api.SplatMap
import com.pyamsoft.splattrak.splatnet.api.SplatMatch
import com.pyamsoft.splattrak.splatnet.api.SplatRuleset
import java.time.LocalDateTime

/** For tests/previews only */
object TestData {

  val currentMatch =
      object : SplatMatch {
        override val id: Long = 1
        override val key: String = "1"
        override val start: LocalDateTime = LocalDateTime.now()
        override val end: LocalDateTime = LocalDateTime.now().plusMinutes(1)
        override val stageA: SplatMap =
            object : SplatMap {
              override val name: String = "Map A"
              override val imageUrl: String = ""
            }
        override val stageB: SplatMap =
            object : SplatMap {
              override val name: String = "Map B"
              override val imageUrl: String = ""
            }
        override val rules: SplatRuleset =
            object : SplatRuleset {
              override val key: String = "Now"
              override val name: String = "Turf War"
            }
      }

  val nextMatch =
      object : SplatMatch {
        override val id: Long = 2
        override val key: String = "2"
        override val start: LocalDateTime = LocalDateTime.now()
        override val end: LocalDateTime = LocalDateTime.now().plusMinutes(1)
        override val stageA: SplatMap =
            object : SplatMap {
              override val name: String = "Map C"
              override val imageUrl: String = ""
            }
        override val stageB: SplatMap =
            object : SplatMap {
              override val name: String = "Map D"
              override val imageUrl: String = ""
            }
        override val rules: SplatRuleset =
            object : SplatRuleset {
              override val key: String = "Next"
              override val name: String = "Turf War Again"
            }
      }

  val coop =
      object : SplatCoop {
        override val name: String = "Salmon Run"
        override val sessions: List<SplatCoopSession> = emptyList()
      }

  val battle =
      object : SplatBattle {
        override val mode: SplatGameMode =
            object : SplatGameMode {
              override val key: String = "mode"
              override val name: String = "Battle"
              override val mode: SplatGameMode.Mode = SplatGameMode.Mode.REGULAR
            }
        override val rotation: List<SplatMatch> = listOf(currentMatch, nextMatch)
      }
}
