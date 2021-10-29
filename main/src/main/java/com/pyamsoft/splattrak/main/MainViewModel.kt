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

import android.app.Activity
import androidx.lifecycle.viewModelScope
import com.pyamsoft.pydroid.arch.UiSavedState
import com.pyamsoft.pydroid.arch.UiSavedStateViewModel
import com.pyamsoft.pydroid.arch.UiSavedStateViewModelProvider
import com.pyamsoft.pydroid.arch.UnitControllerEvent
import com.pyamsoft.pydroid.ui.theme.Theming
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel
@AssistedInject
internal constructor(
    @Assisted savedState: UiSavedState,
    private val theming: Theming,
) :
    UiSavedStateViewModel<MainViewState, UnitControllerEvent>(
        savedState,
        MainViewState(
            theme = Theming.Mode.SYSTEM,
        ),
    ) {

  init {
    viewModelScope.launch(context = Dispatchers.Default) {
      val theme = restoreSavedState(KEY_THEME) { Theming.Mode.SYSTEM }
      setState { copy(theme = theme) }
    }
  }

  fun handleSyncDarkTheme(activity: Activity) {
    setState(
        stateChange = {
          val isDark = theming.isDarkTheme(activity)
          return@setState copy(
              theme = if (isDark) Theming.Mode.DARK else Theming.Mode.LIGHT,
          )
        },
        andThen = { newState -> putSavedState(KEY_THEME, newState.theme.name) },
    )
  }

  companion object {

    private const val KEY_THEME = "theme"
  }

  @AssistedFactory
  interface Factory : UiSavedStateViewModelProvider<MainViewModel> {
    override fun create(savedState: UiSavedState): MainViewModel
  }
}
