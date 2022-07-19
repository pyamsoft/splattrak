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

package com.pyamsoft.splattrak.splatnet.api

import androidx.annotation.CheckResult
import java.time.LocalDateTime

interface SplatMatch {

  @get:CheckResult val id: Long

  @get:CheckResult val key: String

  @get:CheckResult val start: LocalDateTime

  @get:CheckResult val end: LocalDateTime

  @get:CheckResult val stageA: SplatMap

  @get:CheckResult val stageB: SplatMap

  @get:CheckResult val rules: SplatRuleset
}
