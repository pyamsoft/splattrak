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

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.arch.UiViewEvent
import com.pyamsoft.pydroid.arch.UiViewState
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.loader.ImageTarget
import com.pyamsoft.splattrak.lobby.databinding.BackgroundContainerBinding

abstract class BackgroundContainer<S : UiViewState, V : UiViewEvent>
protected constructor(
    private val imageLoader: ImageLoader,
    parent: ViewGroup,
) : BaseUiView<S, V, BackgroundContainerBinding>(parent) {

  final override val layoutRoot by boundView { backgroundContainer }

  final override val viewBinding = BackgroundContainerBinding::inflate

  init {
    doOnInflate {
      val disposable =
          imageLoader
              .asDrawable()
              .load(R.drawable.repeating_stripes)
              .into(
                  object : ImageTarget<Drawable> {
                    override fun clear() {
                      layoutRoot.background = null
                    }

                    override fun setImage(image: Drawable) {
                      layoutRoot.background = image
                    }
                  })

      doOnTeardown { disposable.dispose() }
    }
  }
}
