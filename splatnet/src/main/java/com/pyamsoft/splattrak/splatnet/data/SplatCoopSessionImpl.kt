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

package com.pyamsoft.splattrak.splatnet.data

import com.pyamsoft.splattrak.splatnet.api.SplatCoopSession
import java.time.LocalDateTime

internal data class SplatCoopSessionImpl
internal constructor(
    override val start: LocalDateTime,
    override val end: LocalDateTime,
    override val map: SplatCoopSession.Map?
) : SplatCoopSession
