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

package com.pyamsoft.splattrak.lobby.drilldown

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import coil.ImageLoader
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.ViewWindowInsetObserver
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.pydroid.ui.app.makeFullscreen
import com.pyamsoft.pydroid.ui.theme.ThemeProvider
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.util.show
import com.pyamsoft.splattrak.SplatComponent
import com.pyamsoft.splattrak.SplatTrakTheme
import com.pyamsoft.splattrak.core.SplatViewModelFactory
import com.pyamsoft.splattrak.lobby.dialog.DrilldownScreen
import com.pyamsoft.splattrak.lobby.dialog.DrilldownViewModel
import com.pyamsoft.splattrak.splatnet.api.SplatGameMode
import javax.inject.Inject

internal class DrilldownDialog : AppCompatDialogFragment() {

  @JvmField @Inject internal var factory: SplatViewModelFactory? = null
  private val viewModel by viewModels<DrilldownViewModel> { factory.requireNotNull().create(this) }

  @JvmField @Inject internal var imageLoader: ImageLoader? = null
  @JvmField @Inject internal var theming: Theming? = null

  // Watches the window insets
  private var windowInsetObserver: ViewWindowInsetObserver? = null

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?,
  ): View {
    val rawGameMode =
        requireArguments().getString(KEY_GAME_MODE)
            ?: throw IllegalStateException("Missing required GAME_MODE")
    val gameMode = SplatGameMode.Mode.valueOf(rawGameMode)

    val act = requireActivity()
    Injector.obtainFromApplication<SplatComponent>(act)
        .plusDrilldownComponent()
        .create(gameMode)
        .inject(this)

    val themeProvider = ThemeProvider { theming.requireNotNull().isDarkTheme(act) }
    return ComposeView(act).apply {
      id = com.pyamsoft.splattrak.R.id.screen_lobby

      val observer = ViewWindowInsetObserver(this)
      val windowInsets = observer.start()
      windowInsetObserver = observer

      setContent {
        val state by viewModel.compose()

        SplatTrakTheme(themeProvider) {
          CompositionLocalProvider(LocalWindowInsets provides windowInsets) {
            DrilldownScreen(
                modifier = Modifier.fillMaxSize(),
                state = state,
                imageLoader = imageLoader.requireNotNull(),
                onRefresh = { viewModel.handleRefresh() },
            )
          }
        }
      }
    }
  }

  override fun onViewCreated(
      view: View,
      savedInstanceState: Bundle?,
  ) {
    super.onViewCreated(view, savedInstanceState)
    makeFullscreen()

    viewModel.bindController(viewLifecycleOwner) { event ->
      // TODO
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    (view as? ComposeView)?.disposeComposition()

    windowInsetObserver?.stop()
    windowInsetObserver = null

    factory = null
    imageLoader = null
    theming = null
  }

  companion object {

    private const val TAG = "DrilldownDialog"
    private const val KEY_GAME_MODE = "key_game_mode"

    @JvmStatic
    @CheckResult
    private fun newInstance(mode: SplatGameMode): DialogFragment {
      return DrilldownDialog().apply {
        arguments = Bundle().apply { putString(KEY_GAME_MODE, mode.mode().name) }
      }
    }

    @JvmStatic
    fun show(activity: FragmentActivity, mode: SplatGameMode) {
      newInstance(mode).show(activity, TAG)
    }
  }
}
