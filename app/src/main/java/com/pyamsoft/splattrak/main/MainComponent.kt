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
import androidx.fragment.app.FragmentActivity
import com.pyamsoft.pydroid.ui.navigator.Navigator
import com.pyamsoft.splattrak.coop.CoopComponent
import com.pyamsoft.splattrak.core.ActivityScope
import com.pyamsoft.splattrak.lobby.LobbyComponent
import com.pyamsoft.splattrak.setting.SettingsComponent
import dagger.Binds
import dagger.BindsInstance
import dagger.Module
import dagger.Subcomponent

@ActivityScope
@Subcomponent(modules = [MainComponent.MainModule::class])
internal interface MainComponent {

  fun inject(activity: MainActivity)

  @CheckResult fun plusSettingsComponent(): SettingsComponent.Factory

  @CheckResult fun plusLobbyComponent(): LobbyComponent.Factory

  @CheckResult fun plusCoopComponent(): CoopComponent.Factory

  @Subcomponent.Factory
  interface Factory {

    @CheckResult
    fun create(
        @BindsInstance activity: FragmentActivity,
        @BindsInstance @IdRes fragmentContainerId: Int,
    ): MainComponent
  }

  @Module
  abstract class MainModule {

    @Binds
    @CheckResult
    internal abstract fun bindNavigator(impl: MainNavigator): Navigator<MainPage>
  }
}
