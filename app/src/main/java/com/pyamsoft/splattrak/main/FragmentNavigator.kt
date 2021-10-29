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

import androidx.annotation.CheckResult
import androidx.annotation.IdRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.pyamsoft.pydroid.ui.util.commitNow
import com.pyamsoft.splattrak.R
import com.pyamsoft.splattrak.lobby.LobbyFragment
import com.pyamsoft.splattrak.setting.AppSettings
import timber.log.Timber

class FragmentNavigator(
    private val activity: FragmentActivity,
    @IdRes private val fragmentContainerId: Int,
) {

  private val fragmentManager = activity.supportFragmentManager

  private var currentPage: MainPage by mutableStateOf(DEFAULT_PAGE)

  @CheckResult
  private fun getExistingFragment(): Fragment? {
    return fragmentManager.findFragmentById(fragmentContainerId)
  }

  @Composable
  @CheckResult
  fun currentPage(): State<MainPage> {
    return remember { mutableStateOf(currentPage) }
  }

  fun restore() {
    if (getExistingFragment() != null) {
      handleSelectPage(DEFAULT_PAGE, force = true)
    }
  }

  @JvmOverloads
  fun handleSelectPage(newPage: MainPage, force: Boolean = false) {
    val existing = getExistingFragment()
    val oldPage =
        if (existing == null) null
        else {
          when (existing.tag) {
            LobbyFragment.TAG -> MainPage.Lobby
            AppSettings.TAG -> MainPage.Settings
            else -> null
          }
        }

    return when (newPage) {
      is MainPage.Lobby -> pushLobby(oldPage, force)
      is MainPage.Settings -> pushSettings(oldPage, force)
    }
  }

  private fun pushLobby(previousPage: MainPage?, force: Boolean) {
    commitPage(MainPage.Lobby, previousPage, force)
  }

  private fun pushSettings(previousPage: MainPage?, force: Boolean) {
    commitPage(MainPage.Settings, previousPage, force)
  }

  private fun commitPage(
      newPage: MainPage,
      previousPage: MainPage?,
      force: Boolean,
  ) {
    val push =
        when {
          previousPage != null -> true
          getExistingFragment() == null -> true
          else -> false
        }

    val fragment: Fragment
    val tag: String
    when (newPage) {
      is MainPage.Lobby -> {
        fragment = LobbyFragment.newInstance()
        tag = LobbyFragment.TAG
      }
      is MainPage.Settings -> {
        fragment = AppSettings.newInstance()
        tag = AppSettings.TAG
      }
    }

    if (push || force) {
      if (force) {
        Timber.d("Force commit fragment: $tag")
      } else {
        Timber.d("Commit fragment: $tag")
      }

      currentPage = newPage
      fragmentManager.commitNow(activity) {
        decideAnimationForPage(previousPage, newPage)
        replace(fragmentContainerId, fragment, tag)
      }
    }
  }

  companion object {

    private val DEFAULT_PAGE = MainPage.Lobby

    @JvmStatic
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
  }
}
