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
import coil.ImageLoader
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.splattrak.lobby.drilldown.DrilldownComponent
import com.pyamsoft.splattrak.main.MainComponent
import com.pyamsoft.splattrak.splatnet.SplatnetModule
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Component(modules = [SplatComponent.SplatProvider::class, SplatnetModule::class])
internal interface SplatComponent {

  @CheckResult fun plusMainComponent(): MainComponent.Factory

  @CheckResult fun plusDrilldownComponent(): DrilldownComponent.Factory

  @Component.Factory
  interface Factory {

    @CheckResult
    fun create(
        @BindsInstance application: Application,
        @Named("debug") @BindsInstance debug: Boolean,
        @BindsInstance theming: Theming,
        @BindsInstance imageLoader: () -> ImageLoader,
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
      internal fun provideCoilImageLoader(lazyImageLoader: () -> ImageLoader): ImageLoader {
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
