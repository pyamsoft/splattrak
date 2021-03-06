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

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.pydroid.ui.app.PYDroidActivity
import com.pyamsoft.pydroid.ui.changelog.ChangeLogBuilder
import com.pyamsoft.pydroid.ui.changelog.buildChangeLog
import com.pyamsoft.pydroid.ui.navigator.Navigator
import com.pyamsoft.pydroid.ui.util.dispose
import com.pyamsoft.pydroid.ui.util.recompose
import com.pyamsoft.pydroid.util.stableLayoutHideNavigation
import com.pyamsoft.splattrak.R
import com.pyamsoft.splattrak.SplatComponent
import com.pyamsoft.splattrak.SplatTrakTheme
import com.pyamsoft.splattrak.databinding.ActivityMainBinding
import javax.inject.Inject

internal class MainActivity : PYDroidActivity() {

  override val applicationIcon = R.mipmap.ic_launcher

  override val changelog: ChangeLogBuilder = buildChangeLog {
    bugfix("Improve UI performance")
    bugfix("Fix rotation causing incorrect layouts")
  }

  private var viewBinding: ActivityMainBinding? = null

  private var injector: MainComponent? = null

  @JvmField @Inject internal var viewModel: MainViewModeler? = null
  @JvmField @Inject internal var navigator: Navigator<MainPage>? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    // NOTE(Peter):
    // Not full Compose yet
    // Compose has an issue handling Fragments.
    //
    // We need an AndroidView to handle a Fragment, but a Fragment outlives the Activity via the
    // FragmentManager keeping state. The Compose render does not, so when an activity dies from
    // configuration change, the Fragment is headless somewhere in the great beyond. This leads to
    // memory leaks and other issues like Disposable hooks not being called on DisposeEffect blocks.
    // To avoid these growing pains, we use an Activity layout file and then host the ComposeViews
    // from it that are then used to render Activity level views. Fragment transactions happen as
    // normal and then Fragments host ComposeViews too.
    val binding = ActivityMainBinding.inflate(layoutInflater).apply { viewBinding = this }
    setContentView(binding.root)

    injector =
        Injector.obtainFromApplication<SplatComponent>(this)
            .plusMainComponent()
            .create(
                this,
                binding.mainFragmentContainerView.id,
            )
            .also { c -> c.inject(this) }

    setTheme(R.style.Theme_Splat)
    super.onCreate(savedInstanceState)
    stableLayoutHideNavigation()

    val vm = viewModel.requireNotNull()
    val navi = navigator.requireNotNull()

    vm.restoreState(savedInstanceState)

    // Snackbar respects window offsets and hosts snackbar composables
    // Because these are not in a nice Scaffold, we cannot take advantage of Coordinator style
    // actions (a FAB will not move out of the way for example)
    binding.mainComposeBottom.setContent {
      val screen by navi.currentScreenState()
      val page = remember(screen) { screen as? TopLevelMainPage }

      vm.Render { state ->
        val theme = state.theme

        SystemBars(theme)
        SplatTrakTheme(theme) {
          // Need to box or else snackbar pushes bottom nav
          Box(
              contentAlignment = Alignment.BottomCenter,
          ) {
            page?.let { p ->
              MainBottomNav(
                  page = p,
                  onLoadPage = { navi.navigateTo(it) },
                  onHeightMeasured = { vm.handleMeasureBottomNavHeight(it) },
              )
            }
          }
        }
      }
    }

    vm.handleSyncDarkTheme(this)

    navi.restoreState(savedInstanceState)
    navi.loadIfEmpty { TopLevelMainPage.Lobby }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
  }

  override fun onBackPressed() {
    onBackPressedDispatcher.also { dispatcher ->
      if (dispatcher.hasEnabledCallbacks()) {
        dispatcher.onBackPressed()
      } else {
        super.onBackPressed()
      }
    }
  }

  override fun getSystemService(name: String): Any? {
    return when (name) {
      // Must have super.onCreate() come after defining injector or this will throw
      MainComponent::class.java.name -> injector.requireNotNull()
      else -> super.getSystemService(name)
    }
  }

  override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    viewModel?.handleSyncDarkTheme(this)
    viewBinding?.apply { this.mainComposeBottom.recompose() }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    viewModel?.saveState(outState)
    navigator?.saveState(outState)
  }

  override fun onDestroy() {
    super.onDestroy()
    viewBinding?.apply { this.mainComposeBottom.dispose() }
    viewBinding = null
    navigator = null

    injector = null
    viewModel = null
  }
}
