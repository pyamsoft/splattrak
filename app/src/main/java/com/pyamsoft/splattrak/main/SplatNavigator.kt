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

import android.os.Bundle
import androidx.annotation.CheckResult
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.pyamsoft.pydroid.ui.navigator.FragmentNavigator
import com.pyamsoft.pydroid.ui.navigator.Navigator
import com.pyamsoft.splattrak.R
import com.pyamsoft.splattrak.lobby.LobbyFragment
import com.pyamsoft.splattrak.setting.AppSettings
import javax.inject.Inject

class SplatNavigator
@Inject
internal constructor(
    activity: FragmentActivity,
    @IdRes fragmentContainerId: Int,
) : FragmentNavigator<MainPage>(activity, fragmentContainerId) {

  override val defaultScreen: Navigator.Screen<MainPage> = MainPage.Lobby.asScreen()

  override fun performFragmentTransaction(
      container: Int,
      data: FragmentTag,
      newScreen: Navigator.Screen<MainPage>,
      previousScreen: MainPage?,
  ) {
    commitNow {
      decideAnimationForPage(newScreen.screen, previousScreen)
      replace(container, data.fragment(newScreen.arguments), data.tag)
    }
  }

  override fun provideFragmentTagMap(): Map<MainPage, FragmentTag> {
    return mapOf(
        MainPage.Lobby to createFragmentTag(LobbyFragment.TAG) { LobbyFragment.newInstance() },
        MainPage.Settings to createFragmentTag(AppSettings.TAG) { AppSettings.newInstance() },
    )
  }

  companion object {

    @JvmStatic
    @CheckResult
    private fun createFragmentTag(
        tag: String,
        fragment: (arguments: Bundle?) -> Fragment,
    ): FragmentTag {
      return object : FragmentTag {
        override val fragment: (arguments: Bundle?) -> Fragment = fragment
        override val tag: String = tag
      }
    }

    @JvmStatic
    private fun FragmentTransaction.decideAnimationForPage(
        newPage: MainPage,
        oldPage: MainPage?,
    ) {
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
  }
}
