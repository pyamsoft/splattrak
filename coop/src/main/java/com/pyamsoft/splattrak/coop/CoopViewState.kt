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

package com.pyamsoft.splattrak.coop

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.pyamsoft.pydroid.arch.UiViewState
import com.pyamsoft.splattrak.splatnet.api.SplatCoop
import javax.inject.Inject

interface CoopViewState : UiViewState {
  val coop: SplatCoop?
  val error: Throwable?
  val loading: Boolean
}

internal class MutableCoopViewState @Inject internal constructor() : CoopViewState {
  override var coop by mutableStateOf<SplatCoop?>(null)
  override var error by mutableStateOf<Throwable?>(null)
  override var loading by mutableStateOf(false)
}
