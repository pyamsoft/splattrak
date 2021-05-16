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

package com.pyamsoft.splattrak.lobby.drilldown

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import com.pyamsoft.pydroid.arch.StateSaver
import com.pyamsoft.pydroid.arch.UiController
import com.pyamsoft.pydroid.arch.createComponent
import com.pyamsoft.pydroid.ui.Injector
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.makeFullscreen
import com.pyamsoft.pydroid.ui.arch.fromViewModelFactory
import com.pyamsoft.pydroid.ui.databinding.LayoutFrameBinding
import com.pyamsoft.splattrak.SplatComponent
import com.pyamsoft.splattrak.core.SplatViewModelFactory
import com.pyamsoft.splattrak.lobby.dialog.DrilldownBackgroundContainer
import com.pyamsoft.splattrak.lobby.dialog.DrilldownList
import com.pyamsoft.splattrak.lobby.dialog.DrilldownViewEvent
import com.pyamsoft.splattrak.lobby.dialog.DrilldownViewModel
import com.pyamsoft.splattrak.splatnet.api.SplatGameMode
import timber.log.Timber
import javax.inject.Inject
import com.pyamsoft.splattrak.ui.R as R2

internal class DrilldownDialog : AppCompatDialogFragment(), UiController<Nothing> {

    @JvmField
    @Inject
    internal var factory: SplatViewModelFactory? = null
    private val viewModel by fromViewModelFactory<DrilldownViewModel> { factory?.create(this) }

    private var stateSaver: StateSaver? = null

    @JvmField
    @Inject
    internal var backgroundContainer: DrilldownBackgroundContainer? = null

    // Nested in container
    @JvmField
    @Inject
    internal var nestedList: DrilldownList? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.layout_frame, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        makeFullscreen()

        val rawGameMode = requireArguments().getString(KEY_GAME_MODE)
            ?: throw IllegalStateException("Missing required GAME_MODE")
        val gameMode = SplatGameMode.Mode.valueOf(rawGameMode)

        val binding = LayoutFrameBinding.bind(view)
        Injector.obtainFromApplication<SplatComponent>(view.context)
            .plusDrilldownComponent()
            .create(binding.layoutFrame, gameMode)
            .inject(this)

        val backgroundContainer = requireNotNull(backgroundContainer)
        backgroundContainer.nest(requireNotNull(nestedList))

        stateSaver = createComponent(
            savedInstanceState,
            viewLifecycleOwner,
            viewModel,
            this,
            backgroundContainer
        ) {
            return@createComponent when (it) {
                is DrilldownViewEvent.ForceRefresh -> viewModel.handleRefresh()
            }
        }

        handleBackground(view, gameMode)
    }

    private fun handleBackground(view: View, mode: SplatGameMode.Mode) {
        view.setBackgroundResource(
            when (mode) {
                SplatGameMode.Mode.REGULAR -> R2.color.splatRegular
                SplatGameMode.Mode.LEAGUE -> R2.color.splatLeague
                SplatGameMode.Mode.RANKED -> R2.color.splatRanked
            }
        )
    }

    override fun onControllerEvent(event: Nothing) {
        Timber.w("There should not be any ControllerEvents here!")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        stateSaver?.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stateSaver = null
        factory = null

        backgroundContainer = null
        nestedList = null
    }

    companion object {

        const val TAG = "DrilldownDialog"
        private const val KEY_GAME_MODE = "key_game_mode"

        @JvmStatic
        @CheckResult
        fun newInstance(mode: SplatGameMode): DialogFragment {
            return DrilldownDialog().apply {
                arguments = Bundle().apply {
                    putString(KEY_GAME_MODE, mode.mode().name)
                }
            }
        }
    }
}
