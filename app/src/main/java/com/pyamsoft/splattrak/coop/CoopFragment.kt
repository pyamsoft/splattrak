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

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.ViewWindowInsetObserver
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.pydroid.ui.theme.ThemeProvider
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.util.dispose
import com.pyamsoft.pydroid.ui.util.recompose
import com.pyamsoft.splattrak.R
import com.pyamsoft.splattrak.SplatTrakTheme
import com.pyamsoft.splattrak.main.MainComponent
import com.pyamsoft.splattrak.main.MainViewModeler
import javax.inject.Inject

internal class CoopFragment : Fragment() {

  @JvmField @Inject internal var viewModel: CoopViewModeler? = null
  @JvmField @Inject internal var mainViewModel: MainViewModeler? = null
  @JvmField @Inject internal var imageLoader: ImageLoader? = null
  @JvmField @Inject internal var theming: Theming? = null

  // Watches the window insets
  private var windowInsetObserver: ViewWindowInsetObserver? = null

  private fun handleRefresh() {
    viewModel
        .requireNotNull()
        .handleRefresh(
            scope = viewLifecycleOwner.lifecycleScope,
            force = true,
        )
  }

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?,
  ): View {
    val act = requireActivity()
    Injector.obtainFromActivity<MainComponent>(act).plusCoopComponent().create().inject(this)

    val mainVM = mainViewModel.requireNotNull()
    val vm = viewModel.requireNotNull()

    val themeProvider = ThemeProvider { theming.requireNotNull().isDarkTheme(act) }
    return ComposeView(act).apply {
      id = R.id.screen_lobby

      val observer = ViewWindowInsetObserver(this)
      val windowInsets = observer.start()
      windowInsetObserver = observer

      setContent {
        vm.Render { state ->
          mainVM.Render { mainState ->
            SplatTrakTheme(themeProvider) {
              CompositionLocalProvider(LocalWindowInsets provides windowInsets) {
                CoopScreen(
                    modifier = Modifier.fillMaxSize(),
                    state = state,
                    mainState = mainState,
                    imageLoader = imageLoader.requireNotNull(),
                    onRefresh = { handleRefresh() },
                )
              }
            }
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
    mainViewModel.requireNotNull().restoreState(savedInstanceState)
    viewModel.requireNotNull().also { vm ->
      vm.restoreState(savedInstanceState)
      vm.handleRefresh(
          scope = viewLifecycleOwner.lifecycleScope,
          force = false,
      )
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    viewModel?.saveState(outState)
    mainViewModel?.saveState(outState)
  }

  override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    recompose()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    dispose()

    windowInsetObserver?.stop()
    windowInsetObserver = null

    imageLoader = null
    theming = null

    viewModel = null
    mainViewModel = null
  }

  companion object {

    @JvmStatic
    @CheckResult
    fun newInstance(): Fragment {
      return CoopFragment().apply { arguments = Bundle().apply {} }
    }
  }
}
