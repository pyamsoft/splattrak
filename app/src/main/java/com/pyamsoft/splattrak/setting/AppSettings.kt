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

package com.pyamsoft.splattrak.setting

import android.os.Bundle
import android.view.View
import androidx.annotation.CheckResult
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.pyamsoft.pydroid.arch.asFactory
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.pydroid.ui.preference.Preferences
import com.pyamsoft.pydroid.ui.preference.customPreference
import com.pyamsoft.pydroid.ui.settings.SettingsFragment
import com.pyamsoft.splattrak.SplatComponent
import com.pyamsoft.splattrak.main.MainViewModel
import com.pyamsoft.splattrak.ui.NotNintendo
import javax.inject.Inject

internal class AppSettings : SettingsFragment() {

  override val hideClearAll: Boolean = false

  override val hideUpgradeInformation: Boolean = false

  @JvmField @Inject internal var factory: MainViewModel.Factory? = null
  private val mainViewModel by
      activityViewModels<MainViewModel> { factory.requireNotNull().asFactory(requireActivity()) }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    Injector.obtainFromApplication<SplatComponent>(view.context)
        .plusSettingsComponent()
        .create()
        .inject(this)
  }

  @Composable
  override fun customTopItemMargin(): Dp {
    return 0.dp
  }

  @Composable
  override fun customBottomItemMargin(): Dp {
    // Additional top padding based on the size of the measured Bottom App Bar
    val state by mainViewModel.compose()

    val density = LocalDensity.current
    val bottomNavHeight = state.bottomNavHeight
    return remember(bottomNavHeight) { density.run { bottomNavHeight.toDp() } + 16.dp }
  }

  @Composable
  override fun customPrePreferences(): List<Preferences> {
    return emptyList()
  }

  @Composable
  override fun customPostPreferences(): List<Preferences> {
    return listOf(
        customPreference {
          NotNintendo(
              modifier = Modifier.fillMaxWidth().padding(16.dp),
          )
        },
    )
  }

  companion object {

    const val TAG = "SettingsFragment"

    @JvmStatic
    @CheckResult
    fun newInstance(): Fragment {
      return AppSettings().apply { arguments = Bundle().apply {} }
    }
  }
}