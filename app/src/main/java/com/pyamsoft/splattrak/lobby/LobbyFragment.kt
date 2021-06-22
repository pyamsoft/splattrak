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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.fragment.app.Fragment
import com.pyamsoft.pydroid.arch.StateSaver
import com.pyamsoft.pydroid.arch.UiController
import com.pyamsoft.pydroid.arch.createComponent
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.requireAppBarActivity
import com.pyamsoft.pydroid.ui.app.requireToolbarActivity
import com.pyamsoft.pydroid.ui.arch.fromViewModelFactory
import com.pyamsoft.pydroid.ui.databinding.LayoutCoordinatorBinding
import com.pyamsoft.pydroid.ui.util.show
import com.pyamsoft.splattrak.SplatComponent
import com.pyamsoft.splattrak.core.SplatViewModelFactory
import com.pyamsoft.splattrak.lobby.drilldown.DrilldownDialog
import com.pyamsoft.splattrak.lobby.screen.LobbyAppBarSpacer
import com.pyamsoft.splattrak.lobby.screen.LobbyContainer
import com.pyamsoft.splattrak.lobby.screen.LobbyControllerEvent
import com.pyamsoft.splattrak.lobby.screen.LobbyList
import com.pyamsoft.splattrak.lobby.screen.LobbyViewEvent
import com.pyamsoft.splattrak.lobby.screen.LobbyViewModel
import javax.inject.Inject

internal class LobbyFragment : Fragment(), UiController<LobbyControllerEvent> {

  @JvmField @Inject internal var factory: SplatViewModelFactory? = null
  private val viewModel by fromViewModelFactory<LobbyViewModel>(activity = true) {
    factory?.create(requireActivity())
  }

  private var stateSaver: StateSaver? = null

  @JvmField @Inject internal var spacer: LobbyAppBarSpacer? = null

  @JvmField @Inject internal var container: LobbyContainer? = null

  // Nested in container
  @JvmField @Inject internal var nestedList: LobbyList? = null

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?,
  ): View? {
    return inflater.inflate(R.layout.layout_coordinator, container, false)
  }

  override fun onViewCreated(
      view: View,
      savedInstanceState: Bundle?,
  ) {
    super.onViewCreated(view, savedInstanceState)

    val binding = LayoutCoordinatorBinding.bind(view)
    Injector.obtainFromApplication<SplatComponent>(view.context)
        .plusLobbyComponent()
        .create(
            requireAppBarActivity(),
            requireToolbarActivity(),
            requireActivity(),
            viewLifecycleOwner,
            binding.layoutCoordinator)
        .inject(this)

    val container = requireNotNull(container)
    val nestedList = requireNotNull(nestedList)
    container.nest(nestedList)

    stateSaver =
        createComponent(
            savedInstanceState,
            viewLifecycleOwner,
            viewModel,
            this,
            requireNotNull(spacer),
            container) {
          return@createComponent when (it) {
            is LobbyViewEvent.ViewBattleRotation -> viewModel.handleOpenBattle(it.index)
            is LobbyViewEvent.ForceRefresh -> viewModel.handleRefresh()
          }
        }
  }

  override fun onControllerEvent(event: LobbyControllerEvent) {
    return when (event) {
      is LobbyControllerEvent.OpenBattleRotation ->
          DrilldownDialog.newInstance(event.battle.mode())
              .show(requireActivity(), DrilldownDialog.TAG)
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    stateSaver?.saveState(outState)
    super.onSaveInstanceState(outState)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    stateSaver = null
    factory = null

    container = null
    spacer = null

    nestedList = null
  }

  companion object {

    const val TAG = "LobbyFragment"

    @JvmStatic
    @CheckResult
    fun newInstance(): Fragment {
      return LobbyFragment().apply { arguments = Bundle().apply {} }
    }
  }
}
