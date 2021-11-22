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

package com.pyamsoft.splattrak.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.pyamsoft.pydroid.arch.UiViewState
import com.pyamsoft.pydroid.ui.theme.Theming
import javax.inject.Inject

interface MainViewState : UiViewState {
  val theme: Theming.Mode
  val bottomNavHeight: Int
}

class MutableMainViewState @Inject constructor() : MainViewState {
  override var theme by mutableStateOf(Theming.Mode.SYSTEM)
  override var bottomNavHeight by mutableStateOf(0)
}