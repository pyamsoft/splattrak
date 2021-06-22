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

import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.core.view.ViewPropertyAnimatorCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnLayout
import androidx.core.view.marginBottom
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.arch.UiRender
import com.pyamsoft.pydroid.util.asDp
import com.pyamsoft.pydroid.util.doOnApplyWindowInsets
import com.pyamsoft.splattrak.main.databinding.MainNavigationBinding
import com.pyamsoft.splattrak.ui.animatePopInFromBottom
import com.pyamsoft.splattrak.ui.createRoundedBackground
import javax.inject.Inject
import timber.log.Timber

class MainNavigation
@Inject
internal constructor(
    owner: LifecycleOwner,
    parent: ViewGroup,
) : BaseUiView<MainViewState, MainViewEvent, MainNavigationBinding>(parent) {

  override val viewBinding = MainNavigationBinding::inflate

  override val layoutRoot by boundView { mainBottomNavigationMenu }

  private var backgroundDrawable: Drawable? = null

  private val handler = Handler(Looper.getMainLooper())
  private var animator: ViewPropertyAnimatorCompat? = null

  init {
    doOnInflate {
      backgroundDrawable =
          createRoundedBackground(
              layoutRoot.context, R.color.colorPrimarySeeThrough, applyAllCorners = true)
      correctBackground()
      animateIn()
    }

    doOnInflate {
      layoutRoot.doOnLayout { view ->
        val initialBottomMargin = view.marginBottom
        view.doOnApplyWindowInsets(owner) { v, insets, _ ->
          v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            this.bottomMargin =
                initialBottomMargin + insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
          }
          v.updatePadding(left = 0, right = 0, top = 0, bottom = 0)

          // Publish the measured height
          publish(MainViewEvent.BottomBarMeasured(v.height))
        }
      }
    }

    doOnInflate {
      binding.mainBottomNavigationMenu.setOnNavigationItemSelectedListener { item ->
        Timber.d("Click nav item: $item")
        return@setOnNavigationItemSelectedListener when (item.itemId) {
          R.id.menu_item_nav_lobby -> select(MainViewEvent.OpenLobby)
          R.id.menu_item_nav_settings -> select(MainViewEvent.OpenSettings)
          else -> false
        }
      }
    }

    doOnTeardown {
      binding.mainBottomNavigationMenu.setOnNavigationItemSelectedListener(null)
      binding.mainBottomNavigationMenu.removeBadge(R.id.menu_item_nav_lobby)
    }

    doOnTeardown { handler.removeCallbacksAndMessages(null) }

    doOnTeardown {
      animator?.cancel()
      animator = null

      backgroundDrawable = null
    }
  }

  /**
   * Default MaterialShapeBackground makes a weird shadow thing, disable it since it looks funny
   * through the transparent bar
   */
  private fun correctBackground() {
    binding.mainBottomNavigationMenu.apply {
      background = requireNotNull(backgroundDrawable)
      elevation = 8.asDp(context).toFloat()
    }
  }

  private fun animateIn() {
    if (animator == null) {
      animator = animatePopInFromBottom(binding.mainBottomNavigationMenu, 600, false)
    }
  }

  override fun onRender(state: UiRender<MainViewState>) {
    correctBackground()
    state.mapChanged { it.page }.render(viewScope) { handlePage(it) }
  }

  private fun handlePage(page: MainPage?) {
    Timber.d("Handle page: $page")
    val pageId = getIdForPage(page)
    if (pageId != 0) {
      Timber.d("Mark page selected: $page $pageId")
      // Don't mark it selected since this will re-fire the click event
      // binding.mainBottomNavigationMenu.selectedItemId = pageId
      val item = binding.mainBottomNavigationMenu.menu.findItem(pageId)
      if (item != null) {
        handler.removeCallbacksAndMessages(null)
        handler.post { item.isChecked = true }
      }
    }
  }

  @CheckResult
  private fun getIdForPage(page: MainPage?): Int {
    return if (page == null) 0
    else {
      when (page) {
        is MainPage.Lobby -> R.id.menu_item_nav_lobby
        is MainPage.Settings -> R.id.menu_item_nav_settings
      }
    }
  }

  @CheckResult
  private fun select(viewEvent: MainViewEvent): Boolean {
    publish(viewEvent)
    return false
  }
}
