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

import androidx.annotation.AnimRes
import androidx.annotation.CheckResult
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.pyamsoft.pydroid.arch.UiSavedStateReader
import com.pyamsoft.pydroid.arch.UiSavedStateWriter
import com.pyamsoft.pydroid.ui.navigator.FragmentNavigator
import com.pyamsoft.splattrak.R
import com.pyamsoft.splattrak.coop.CoopFragment
import com.pyamsoft.splattrak.lobby.LobbyFragment
import com.pyamsoft.splattrak.setting.AppSettings
import javax.inject.Inject

class MainNavigator
@Inject
internal constructor(
    activity: FragmentActivity,
    @IdRes fragmentContainerId: Int,
) : FragmentNavigator<MainPage>(activity, fragmentContainerId) {

  override fun onRestoreState(savedInstanceState: UiSavedStateReader) {}

  override fun onSaveState(outState: UiSavedStateWriter) {}

  override fun produceFragmentForScreen(screen: MainPage): Fragment =
      when (screen) {
        is TopLevelMainPage.Lobby -> LobbyFragment.newInstance()
        is TopLevelMainPage.Coop -> CoopFragment.newInstance()
        is TopLevelMainPage.Settings -> AppSettings.newInstance()
          else -> throw IllegalArgumentException("Unhandled screen type: $screen")
      }

  override fun performFragmentTransaction(
      container: Int,
      newScreen: Fragment,
      previousScreen: Fragment?,
  ) {
    commitNow {
      decideAnimationForPage(newScreen, previousScreen)
      replace(container, newScreen, newScreen::class.java.name)
    }
  }

  companion object {

    private data class FragmentAnimation(
        @AnimRes val enter: Int,
        @AnimRes val exit: Int,
    )
    @CheckResult
    private infix fun Int.then(exit: Int): FragmentAnimation {
      return FragmentAnimation(
          enter = this,
          exit = exit,
      )
    }

    @JvmStatic
    private fun FragmentTransaction.decideAnimationForPage(
        newPage: Fragment,
        oldPage: Fragment?,
    ) {
      val animations =
          when (newPage) {
            is LobbyFragment ->
                when (oldPage) {
                  null -> R.anim.fragment_open_enter then R.anim.fragment_open_exit
                  is AppSettings -> R.anim.slide_in_left then R.anim.slide_out_right
                  is CoopFragment -> R.anim.slide_in_left then R.anim.slide_out_right
                  else -> null
                }
            is AppSettings ->
                when (oldPage) {
                  null -> R.anim.fragment_open_enter then R.anim.fragment_open_exit
                  is LobbyFragment -> R.anim.slide_in_right then R.anim.slide_out_left
                  is CoopFragment -> R.anim.slide_in_right then R.anim.slide_out_left
                  else -> null
                }
            is CoopFragment ->
                when (oldPage) {
                  null -> R.anim.fragment_open_enter then R.anim.fragment_open_exit
                  is AppSettings -> R.anim.slide_in_left then R.anim.slide_out_right
                  is LobbyFragment -> R.anim.slide_in_right then R.anim.slide_out_left
                  else -> null
                }
            else -> null
          }

      if (animations != null) {
        val (enter, exit) = animations
        setCustomAnimations(enter, exit, enter, exit)
      }
    }
  }
}
