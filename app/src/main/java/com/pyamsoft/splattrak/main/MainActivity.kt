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
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.appbar.AppBarLayout
import com.pyamsoft.pydroid.arch.StateSaver
import com.pyamsoft.pydroid.arch.UiController
import com.pyamsoft.pydroid.arch.createComponent
import com.pyamsoft.pydroid.arch.createSavedStateViewModelFactory
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.pydroid.ui.app.AppBarActivity
import com.pyamsoft.pydroid.ui.app.AppBarActivityProvider
import com.pyamsoft.pydroid.ui.arch.fromViewModelFactory
import com.pyamsoft.pydroid.ui.changelog.ChangeLogActivity
import com.pyamsoft.pydroid.ui.changelog.ChangeLogBuilder
import com.pyamsoft.pydroid.ui.changelog.buildChangeLog
import com.pyamsoft.pydroid.ui.databinding.LayoutCoordinatorBinding
import com.pyamsoft.pydroid.ui.util.commitNow
import com.pyamsoft.pydroid.util.doOnStart
import com.pyamsoft.pydroid.util.stableLayoutHideNavigation
import com.pyamsoft.splattrak.BuildConfig
import com.pyamsoft.splattrak.R
import com.pyamsoft.splattrak.SplatComponent
import com.pyamsoft.splattrak.lobby.LobbyFragment
import com.pyamsoft.splattrak.setting.SettingsFragment
import com.pyamsoft.splattrak.ui.SnackbarContainer
import timber.log.Timber
import javax.inject.Inject

internal class MainActivity :
    ChangeLogActivity(), AppBarActivity, AppBarActivityProvider, UiController<MainControllerEvent> {

  override val checkForUpdates = false

  override val applicationIcon = R.mipmap.ic_launcher

  override val changelog: ChangeLogBuilder = buildChangeLog {}

  override val versionName = BuildConfig.VERSION_NAME

  override val fragmentContainerId: Int
    get() = requireNotNull(container).id()

  override val snackbarRoot: ViewGroup
    get() {
      val fm = supportFragmentManager
      val fragment = fm.findFragmentById(fragmentContainerId)
      if (fragment is SnackbarContainer) {
        val container = fragment.container()
        if (container != null) {
          Timber.d("Return fragment snackbar container: $fragment $container")
          return container
        }
      }

      val fallbackContainer = requireNotNull(snackbar?.container())
      Timber.d("Return activity snackbar container: $fallbackContainer")
      return fallbackContainer
    }

  private var stateSaver: StateSaver? = null

  private var capturedAppBar: AppBarLayout? = null

  @JvmField @Inject internal var factory: MainViewModel.Factory? = null
  private val viewModel by fromViewModelFactory<MainViewModel> {
    createSavedStateViewModelFactory(factory)
  }

  @JvmField @Inject internal var toolbar: MainToolbar? = null

  @JvmField @Inject internal var navigation: MainNavigation? = null

  @JvmField @Inject internal var container: MainContainer? = null

  @JvmField @Inject internal var snackbar: MainSnackbar? = null

  override fun setAppBar(bar: AppBarLayout?) {
    capturedAppBar = bar
  }

  override fun requireAppBar(func: (AppBarLayout) -> Unit) {
    requireNotNull(capturedAppBar).let(func)
  }

  override fun withAppBar(func: (AppBarLayout) -> Unit) {
    capturedAppBar?.let(func)
  }

  override fun onControllerEvent(event: MainControllerEvent) {
    return when (event) {
      is MainControllerEvent.PushPage -> handleSelectPage(event.newPage, event.oldPage, event.force)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    setTheme(R.style.Theme_Splat)
    super.onCreate(savedInstanceState)
    val binding = LayoutCoordinatorBinding.inflate(layoutInflater)
    setContentView(binding.root)

    Injector.obtainFromApplication<SplatComponent>(this)
        .plusMainComponent()
        .create(this, this, this, binding.layoutCoordinator, this, this)
        .inject(this)

    stableLayoutHideNavigation()
    inflateComponents(savedInstanceState)

    val existingFragment = supportFragmentManager.findFragmentById(fragmentContainerId)
    if (savedInstanceState == null || existingFragment == null) {
      viewModel.handleLoadDefaultPage()
    }
  }

  private fun handleSelectPage(newPage: MainPage, oldPage: MainPage?, force: Boolean) {
    return when (newPage) {
      is MainPage.Lobby -> pushLobby(oldPage, force)
      is MainPage.Settings -> pushSettings(oldPage, force)
    }
  }

  private fun pushLobby(previousPage: MainPage?, force: Boolean) {
    commitPage(LobbyFragment.newInstance(), MainPage.Lobby, previousPage, LobbyFragment.TAG, force)
  }

  private fun pushSettings(previousPage: MainPage?, force: Boolean) {
    commitPage(
        SettingsFragment.newInstance(),
        MainPage.Settings,
        previousPage,
        SettingsFragment.TAG,
        force)
  }

  private fun commitPage(
      fragment: Fragment,
      newPage: MainPage,
      previousPage: MainPage?,
      tag: String,
      force: Boolean,
  ) {
    val fm = supportFragmentManager
    val container = fragmentContainerId

    val push =
        when {
          previousPage != null -> true
          fm.findFragmentById(container) == null -> true
          else -> false
        }

    if (push || force) {
      if (force) {
        Timber.d("Force commit fragment: $tag")
      } else {
        Timber.d("Commit fragment: $tag")
      }

      this.doOnStart {
        fm.commitNow(this) {
          decideAnimationForPage(previousPage, newPage)
          replace(container, fragment, tag)
        }
      }
    }
  }

  private fun FragmentTransaction.decideAnimationForPage(oldPage: MainPage?, newPage: MainPage) {
    val animations =
        when (newPage) {
          is MainPage.Lobby ->
              when (oldPage) {
                null -> R.anim.fragment_open_enter to R.anim.fragment_open_exit
                is MainPage.Settings -> R.anim.slide_in_left to R.anim.slide_out_right
                is MainPage.Lobby -> null
              }
          is MainPage.Settings ->
              when (oldPage) {
                null -> R.anim.fragment_open_enter to R.anim.fragment_open_exit
                is MainPage.Lobby -> R.anim.slide_in_right to R.anim.slide_out_left
                is MainPage.Settings -> null
              }
        }

    if (animations != null) {
      val (enter, exit) = animations
      setCustomAnimations(enter, exit, enter, exit)
    }
  }

  private fun inflateComponents(savedInstanceState: Bundle?) {
    val container = requireNotNull(container)
    val navigation = requireNotNull(navigation)
    val snackbar = requireNotNull(snackbar)
    val toolbar = requireNotNull(toolbar)

    stateSaver =
        createComponent(
            savedInstanceState, this, viewModel, this, container, toolbar, navigation, snackbar) {
          return@createComponent when (it) {
            is MainViewEvent.OpenLobby -> viewModel.handleSelectPage(MainPage.Lobby, force = false)
            is MainViewEvent.OpenSettings ->
                viewModel.handleSelectPage(MainPage.Settings, force = false)
            is MainViewEvent.BottomBarMeasured -> viewModel.handleConsumeBottomBarHeight(it.height)
          }
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

  override fun onSaveInstanceState(outState: Bundle) {
    stateSaver?.saveState(outState)
    super.onSaveInstanceState(outState)
  }

  override fun onDestroy() {
    super.onDestroy()
    stateSaver = null
    factory = null

    toolbar = null
    container = null
    navigation = null
    snackbar = null

    capturedAppBar = null
  }
}
