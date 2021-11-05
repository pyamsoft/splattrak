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

package com.pyamsoft.splattrak.lobby.test

import com.pyamsoft.splattrak.splatnet.api.SplatBattle
import com.pyamsoft.splattrak.splatnet.api.SplatGameMode
import com.pyamsoft.splattrak.splatnet.api.SplatMap
import com.pyamsoft.splattrak.splatnet.api.SplatMatch
import com.pyamsoft.splattrak.splatnet.api.SplatRuleset
import java.time.LocalDateTime

/**
 * For tests/previews only
 */
internal object TestData {

  val currentMatch =
    object : SplatMatch {
      override fun id(): Long {
        return 1
      }

      override fun start(): LocalDateTime {
        return LocalDateTime.now()
      }

      override fun end(): LocalDateTime {
        return LocalDateTime.now().plusHours(1)
      }

      override fun stageA(): SplatMap {
        return object : SplatMap {
          override fun id(): String {
            return "A"
          }

          override fun name(): String {
            return "Stage A"
          }

          override fun imageUrl(): String {
            return ""
          }
        }
      }

      override fun stageB(): SplatMap {
        return object : SplatMap {
          override fun id(): String {
            return "B"
          }

          override fun name(): String {
            return "Stage B"
          }

          override fun imageUrl(): String {
            return ""
          }
        }
      }

      override fun rules(): SplatRuleset {
        return object : SplatRuleset {
          override fun key(): String {
            return "Current"
          }

          override fun name(): String {
            return "Turf War"
          }
        }
      }
    }

  val nextMatch =
    object : SplatMatch {
      override fun id(): Long {
        return 2
      }

      override fun start(): LocalDateTime {
        return LocalDateTime.now()
      }

      override fun end(): LocalDateTime {
        return LocalDateTime.now().plusHours(1)
      }

      override fun stageA(): SplatMap {
        return object : SplatMap {
          override fun id(): String {
            return "A"
          }

          override fun name(): String {
            return "Stage A"
          }

          override fun imageUrl(): String {
            return ""
          }
        }
      }

      override fun stageB(): SplatMap {
        return object : SplatMap {
          override fun id(): String {
            return "B"
          }

          override fun name(): String {
            return "Stage B"
          }

          override fun imageUrl(): String {
            return ""
          }
        }
      }

      override fun rules(): SplatRuleset {
        return object : SplatRuleset {
          override fun key(): String {
            return "Next"
          }

          override fun name(): String {
            return "Turf War Again"
          }
        }
      }
    }

  val battle =
    object : SplatBattle {
      override fun mode(): SplatGameMode {
        return object : SplatGameMode {
          override fun key(): String {
            return "test"
            }

            override fun name(): String {
              return "TEST"
            }

            override fun mode(): SplatGameMode.Mode {
              return SplatGameMode.Mode.REGULAR
            }
          }
        }

        override fun rotation(): List<SplatMatch> {
          return listOf(currentMatch, nextMatch)
        }
      }
}
