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

package com.pyamsoft.splattrak.splatnet.data

import com.pyamsoft.splattrak.splatnet.api.SplatGameMode
import com.pyamsoft.splattrak.splatnet.api.SplatMap
import com.pyamsoft.splattrak.splatnet.api.SplatMatch
import com.pyamsoft.splattrak.splatnet.api.SplatRuleset
import java.time.LocalDateTime

internal data class SplatMatchImpl internal constructor(
    private val id: Long,
    private val startTime: LocalDateTime,
    private val endTime: LocalDateTime,
    private val stageA: SplatMap,
    private val stageB: SplatMap,
    private val gameMode: SplatGameMode,
    private val rule: SplatRuleset,
) : SplatMatch {

    override fun id(): Long {
        return id
    }

    override fun start(): LocalDateTime {
        return startTime
    }

    override fun end(): LocalDateTime {
        return endTime
    }

    override fun stageA(): SplatMap {
        return stageA
    }

    override fun stageB(): SplatMap {
        return stageB
    }

    override fun rules(): SplatRuleset {
        return rule
    }
}