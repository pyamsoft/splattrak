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

package com.pyamsoft.splattrak.splatnet.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class NetworkSplatMatch internal constructor(
    internal val id: Long,

    @Json(name = "start_time")
    internal val startTime: Long,

    @Json(name = "end_time")
    internal val endTime: Long,

    @Json(name = "stage_a")
    internal val stageA: NetworkSplatMap,

    @Json(name = "stage_b")
    internal val stageB: NetworkSplatMap,

    @Json(name = "game_mode")
    internal val gameMode: NetworkSplatGameMode,

    internal val rule: NetworkSplatRuleset,
)