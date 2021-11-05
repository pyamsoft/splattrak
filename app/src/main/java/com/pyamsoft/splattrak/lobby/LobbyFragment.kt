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

package com.pyamsoft.splattrak.lobby

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import coil.ImageLoader
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.ViewWindowInsetObserver
import com.pyamsoft.pydroid.arch.asFactory
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.pydroid.ui.theme.ThemeProvider
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.splattrak.R
import com.pyamsoft.splattrak.SplatComponent
import com.pyamsoft.splattrak.SplatTrakTheme
import com.pyamsoft.splattrak.core.SplatViewModelFactory
import com.pyamsoft.splattrak.lobby.drilldown.DrilldownDialog
import com.pyamsoft.splattrak.main.MainViewModel
import javax.inject.Inject

internal class LobbyFragment : Fragment() {

  @JvmField @Inject internal var factory: SplatViewModelFactory? = null
  private val viewModel by
      activityViewModels<LobbyViewModel> { factory.requireNotNull().create(requireActivity()) }

  @JvmField @Inject internal var mainFactory: MainViewModel.Factory? = null
  private val mainViewModel by
      activityViewModels<MainViewModel> {
        mainFactory.requireNotNull().asFactory(requireActivity())
      }

  @JvmField @Inject internal var imageLoader: ImageLoader? = null
  @JvmField @Inject internal var theming: Theming? = null

  // Watches the window insets
  private var windowInsetObserver: ViewWindowInsetObserver? = null

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?,
  ): View {
    val act = requireActivity()
    Injector.obtainFromApplication<SplatComponent>(act).plusLobbyComponent().create().inject(this)

    val themeProvider = ThemeProvider { theming.requireNotNull().isDarkTheme(act) }
    return ComposeView(act).apply {
      id = R.id.screen_lobby

      val observer = ViewWindowInsetObserver(this)
      val windowInsets = observer.start()
      windowInsetObserver = observer

      setContent {
        val state by viewModel.compose()
        val mainState by mainViewModel.compose()

        SplatTrakTheme(themeProvider) {
          CompositionLocalProvider(LocalWindowInsets provides windowInsets) {
            LobbyScreen(
                modifier = Modifier.fillMaxSize(),
                state = state,
                mainState = mainState,
                imageLoader = imageLoader.requireNotNull(),
                onItemClicked = { viewModel.handleOpenBattle(it) },
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
    viewModel.bindController(viewLifecycleOwner) { event ->
      return@bindController when (event) {
        is LobbyControllerEvent.OpenBattleRotation ->
            DrilldownDialog.show(requireActivity(), event.battle.mode())
      }
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    (view as? ComposeView)?.disposeComposition()

    windowInsetObserver?.stop()
    windowInsetObserver = null

    mainFactory = null
    factory = null
    imageLoader = null
    theming = null
  }

  companion object {

    const val TAG = "LobbyFragment"

    @JvmStatic
    @CheckResult
    fun newInstance(): Fragment {
      return LobbyFragment().apply { arguments = Bundle().apply {} }
    }
  }
}
