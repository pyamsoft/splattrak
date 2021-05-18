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

import android.content.res.ColorStateList
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.core.content.ContextCompat
import androidx.core.view.ViewPropertyAnimatorCompat
import androidx.core.view.doOnLayout
import androidx.core.view.marginBottom
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.shape.ShapeAppearanceModel
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.arch.UiRender
import com.pyamsoft.pydroid.util.asDp
import com.pyamsoft.pydroid.util.doOnApplyWindowInsets
import com.pyamsoft.splattrak.main.databinding.MainNavigationBinding
import com.pyamsoft.splattrak.ui.R as R2
import com.pyamsoft.splattrak.ui.appbar.animatePopInFromBottom
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

  private val handler = Handler(Looper.getMainLooper())
  private var animator: ViewPropertyAnimatorCompat? = null

  init {
    doOnInflate {
      correctBackground()
      animateIn()
    }

    doOnInflate {
      layoutRoot.doOnLayout { view ->
        val initialBottomMargin = view.marginBottom
        view.doOnApplyWindowInsets(owner) { v, insets, _ ->
          v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            this.bottomMargin = initialBottomMargin + insets.systemWindowInsetBottom
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
    }
  }

  /**
   * Default MaterialShapeBackground makes a weird shadow thing, disable it since it looks funny
   * through the transparent bar
   */
  private fun correctBackground() {
    val context = layoutRoot.context
    val cornerSize = 16.asDp(layoutRoot.context).toFloat()

    val shapeModel =
        ShapeAppearanceModel.Builder()
            .apply {
              setAllCorners(RoundedCornerTreatment())
              setAllCornerSizes(cornerSize)
            }
            .build()

    // Create background
    val color = ContextCompat.getColor(layoutRoot.context, R2.color.colorPrimarySeeThrough)
    val materialShapeDrawable = MaterialShapeDrawable(shapeModel)
    materialShapeDrawable.initializeElevationOverlay(context)
    materialShapeDrawable.shadowCompatibilityMode = MaterialShapeDrawable.SHADOW_COMPAT_MODE_ALWAYS
    materialShapeDrawable.fillColor = ColorStateList.valueOf(color)
    materialShapeDrawable.elevation = 0F

    binding.mainBottomNavigationMenu.apply {
      elevation = 8.asDp(context).toFloat()
      background = materialShapeDrawable
    }
  }

  private fun animateIn() {
    if (animator == null) {
      animator = animatePopInFromBottom(binding.mainBottomNavigationMenu, 600, false)
    }
  }

  override fun onRender(state: UiRender<MainViewState>) {
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
