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

import com.pyamsoft.highlander.highlander
import com.pyamsoft.pydroid.arch.AbstractViewModeler
import com.pyamsoft.pydroid.core.ResultWrapper
import com.pyamsoft.splattrak.splatnet.SplatnetInteractor
import com.pyamsoft.splattrak.splatnet.api.SplatBattle
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class LobbyViewModeler
@Inject
internal constructor(
    private val state: MutableLobbyViewState,
    splatnetInteractor: SplatnetInteractor,
) : AbstractViewModeler<LobbyViewState>(state) {

  private val scheduleRunner =
      highlander<ResultWrapper<List<SplatBattle>>, Boolean> { force ->
        splatnetInteractor.schedule(force).map { it.battles }
      }

  fun handleRefresh(scope: CoroutineScope, force: Boolean) {
    scope.launch(context = Dispatchers.Main) {
      state.loading = true
      scheduleRunner
          .call(force)
          .onSuccess {
            state.apply {
              error = null
              schedule = it
            }
          }
          .onFailure { Timber.e(it, "Failed to load Splatoon2.ink lobby list") }
          .onFailure {
            state.apply {
              error = it
              schedule = emptyList()
            }
          }
          .onFinally { state.loading = false }
    }
  }
}
