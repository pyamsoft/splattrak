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
import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import coil.ImageLoader
import com.google.accompanist.insets.ProvideWindowInsets
import com.pyamsoft.pydroid.arch.asFactory
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.pydroid.ui.app.PYDroidActivity
import com.pyamsoft.pydroid.ui.changelog.ChangeLogBuilder
import com.pyamsoft.pydroid.ui.changelog.buildChangeLog
import com.pyamsoft.pydroid.ui.navigator.Navigator
import com.pyamsoft.pydroid.util.stableLayoutHideNavigation
import com.pyamsoft.splattrak.R
import com.pyamsoft.splattrak.SplatComponent
import com.pyamsoft.splattrak.SplatTrakTheme
import com.pyamsoft.splattrak.databinding.ActivityMainBinding
import javax.inject.Inject
import timber.log.Timber

internal class MainActivity : PYDroidActivity() {

  override val applicationIcon = R.mipmap.ic_launcher

  override val changelog: ChangeLogBuilder = buildChangeLog {
    feature("Convert to Jetpack Compose")
  }

  private var viewBinding: ActivityMainBinding? = null

  @JvmField @Inject internal var imageLoader: ImageLoader? = null

  @JvmField @Inject internal var factory: MainViewModel.Factory? = null
  private val viewModel by viewModels<MainViewModel> { factory.requireNotNull().asFactory(this) }

  @JvmField @Inject internal var navigator: Navigator<MainPage>? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    setTheme(R.style.Theme_Splat)
    super.onCreate(savedInstanceState)
    stableLayoutHideNavigation()

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

    Injector.obtainFromApplication<SplatComponent>(this)
        .plusMainComponent()
        .create(
            this,
            binding.mainFragmentContainerView.id,
        )
        .inject(this)

    // Snackbar respects window offsets and hosts snackbar composables
    // Because these are not in a nice Scaffold, we cannot take advantage of Coordinator style
    // actions (a FAB will not move out of the way for example)
    binding.mainComposeBottom.setContent {
      val state by viewModel.compose()
      val page by navigator.requireNotNull().currentScreenState()

      val snackbarHostState = remember { SnackbarHostState() }

      val theme = state.theme
      SplatTrakTheme(
          theme = theme,
      ) {
        ProvideWindowInsets {
          MainBottomNav(
              page = page,
              imageLoader = imageLoader.requireNotNull(),
              onLoadLobby = { navigate(MainPage.Lobby) },
              onLoadSettings = { navigate(MainPage.Settings) },
              onHeightMeasured = { viewModel.handleMeasureBottomNavHeight(it) },
          )
          RatingScreen(
              snackbarHostState = snackbarHostState,
          )
          VersionCheckScreen(
              snackbarHostState = snackbarHostState,
          )
        }
      }
    }

    viewModel.handleSyncDarkTheme(this)

    navigator.requireNotNull().restore(savedInstanceState)
  }

  private fun navigate(page: MainPage) {
    if (navigator.requireNotNull().select(page.asScreen())) {
      Timber.d("Navigated to $page")
    } else {
      Timber.w("Did not navigate to $page")
    }
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

  override fun onDestroy() {
    super.onDestroy()
    viewBinding?.apply { this.mainComposeBottom.disposeComposition() }
    viewBinding = null
    factory = null
    imageLoader = null
    navigator = null
  }
}
