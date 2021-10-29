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

import androidx.annotation.CheckResult
import androidx.lifecycle.ViewModel
import com.pyamsoft.splattrak.core.ViewModelFactoryModule
import com.pyamsoft.splattrak.ui.ThemeProviderModule
import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Subcomponent(
    modules =
        [
            LobbyComponent.ComponentModule::class,
            ViewModelFactoryModule::class,
            ThemeProviderModule::class,
        ])
internal interface LobbyComponent {

  fun inject(fragment: LobbyFragment)

  @Subcomponent.Factory
  interface Factory {

    @CheckResult fun create(): LobbyComponent
  }

  @Module
  abstract class ComponentModule {

    @Binds
    @IntoMap
    @ClassKey(LobbyViewModel::class)
    internal abstract fun bindViewModel(impl: LobbyViewModel): ViewModel
  }
}
