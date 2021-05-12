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

package com.pyamsoft.splattrak.lobby.item

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.CheckResult
import androidx.core.view.updateLayoutParams
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.arch.UiRender
import com.pyamsoft.pydroid.arch.UiViewState
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.loader.Loaded
import com.pyamsoft.pydroid.util.asDp
import com.pyamsoft.splattrak.lobby.databinding.LobbyItemMapsBinding
import com.pyamsoft.splattrak.splatnet.api.SplatMap
import com.pyamsoft.splattrak.splatnet.api.SplatMatch

abstract class BaseLobbyStages<S : UiViewState> protected constructor(
    private val imageLoader: ImageLoader,
    parent: ViewGroup,
) : BaseUiView<S, Nothing, LobbyItemMapsBinding>(parent) {

    final override val viewBinding = LobbyItemMapsBinding::inflate

    final override val layoutRoot by boundView { lobbyItemStagesRoot }

    private var stageALoaded: Loaded? = null
    private var stageBLoaded: Loaded? = null

    init {
        doOnTeardown {
            clear()
        }

        doOnInflate {
            setChildWeights(isLarge())
        }
    }


    private fun setChildWeights(isLarge: Boolean) {
        val ctx = layoutRoot.context.applicationContext
        val orientation = if (isLarge) LinearLayout.VERTICAL else LinearLayout.HORIZONTAL
        layoutRoot.orientation = orientation

        binding.lobbyItemStageSpacer.updateLayoutParams {
            val size = 8.asDp(ctx)
            this.width = if (isLarge) LinearLayout.LayoutParams.MATCH_PARENT else size
            this.height = if (isLarge) size * 2 else LinearLayout.LayoutParams.MATCH_PARENT
        }

        binding.lobbyItemStageA.updateLayoutParams {
            this.width = if (isLarge) LinearLayout.LayoutParams.MATCH_PARENT else 0
            this.height = if (isLarge) 0 else LinearLayout.LayoutParams.MATCH_PARENT
        }

        binding.lobbyItemStageB.updateLayoutParams {
            this.width = if (isLarge) LinearLayout.LayoutParams.MATCH_PARENT else 0
            this.height = if (isLarge) 0 else LinearLayout.LayoutParams.MATCH_PARENT
        }
    }

    @CheckResult
    protected abstract fun isLarge(): Boolean

    @CheckResult
    protected abstract fun getMatch(state: S): SplatMatch

    private fun clear() {
        stageALoaded?.dispose()
        stageALoaded = null

        stageBLoaded?.dispose()
        stageBLoaded = null

        binding.lobbyItemStageAName.text = ""
        binding.lobbyItemStageBName.text = ""
    }

    final override fun onRender(state: UiRender<S>) {
        state
            .mapChanged { getMatch(it) }
            .mapChanged { it.stageA() }
            .render(viewScope) {
                stageALoaded = handleMapRotation(
                    stageALoaded,
                    it,
                    binding.lobbyItemStageAImage,
                    binding.lobbyItemStageAName
                )
            }

        state
            .mapChanged { getMatch(it) }
            .mapChanged { it.stageB() }
            .render(viewScope) {
                stageBLoaded = handleMapRotation(
                    stageBLoaded,
                    it,
                    binding.lobbyItemStageBImage,
                    binding.lobbyItemStageBName
                )
            }
    }

    @CheckResult
    private fun handleMapRotation(
        loaded: Loaded?,
        stage: SplatMap,
        image: ImageView,
        name: TextView,
    ): Loaded {
        loaded?.dispose()
        name.text = stage.name()
        return imageLoader.asDrawable().load(stage.imageUrl())
            .into(image)
    }

}
