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

package com.pyamsoft.splattrak

import android.app.Application
import android.content.Context
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.splattrak.lobby.LobbyComponent
import com.pyamsoft.splattrak.lobby.dialog.list.DrilldownItemComponent
import com.pyamsoft.splattrak.lobby.drilldown.DrilldownComponent
import com.pyamsoft.splattrak.main.MainComponent
import com.pyamsoft.splattrak.setting.SettingsComponent
import com.pyamsoft.splattrak.splatnet.SplatnetModule
import com.pyamsoft.splattrak.ui.UiModule
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Component(modules = [SplatComponent.SplatProvider::class, SplatnetModule::class, UiModule::class])
internal interface SplatComponent {

  /** Not actually used, just here so graph can compile */
  @CheckResult
  @Suppress("FunctionName")
  fun `$$daggerRequiredDrilldownItemComponent`(): DrilldownItemComponent.Factory

  @CheckResult fun plusMainComponent(): MainComponent.Factory

  @CheckResult fun plusLobbyComponent(): LobbyComponent.Factory

  @CheckResult fun plusDrilldownComponent(): DrilldownComponent.Factory

  @CheckResult fun plusSettingsComponent(): SettingsComponent.Factory

  @Component.Factory
  interface Factory {

    @CheckResult
    fun create(
        @BindsInstance application: Application,
        @Named("debug") @BindsInstance debug: Boolean,
        @BindsInstance theming: Theming,
        @BindsInstance imageLoader: ImageLoader,
        @BindsInstance coilImageLoader: () -> coil.ImageLoader,
    ): SplatComponent
  }

  @Module
  abstract class SplatProvider {

    @Module
    companion object {

      @Provides
      @JvmStatic
      internal fun provideContext(application: Application): Context {
        return application
      }

      @Provides
      @JvmStatic
      @Singleton
      internal fun provideCoilImageLoader(
          lazyImageLoader: () -> coil.ImageLoader
      ): coil.ImageLoader {
        return lazyImageLoader()
      }

      @Provides
      @JvmStatic
      @Named("app_name")
      internal fun provideAppNameRes(): Int {
        return R.string.app_name
      }
    }
  }
}
