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
import androidx.annotation.CheckResult
import coil.ImageLoader
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibraries
import com.pyamsoft.pydroid.ui.ModuleProvider
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.util.isDebugMode
import com.pyamsoft.splattrak.core.PRIVACY_POLICY_URL
import com.pyamsoft.splattrak.core.TERMS_CONDITIONS_URL
import timber.log.Timber

class SplatTrak : Application() {

    private val component by lazy {
        val url = "https://github.com/pyamsoft/splattrak"
        val lazyImageLoader = lazy(LazyThreadSafetyMode.NONE) { ImageLoader(this) }
        val parameters =
            PYDroid.Parameters(
                // Must be lazy since Coil calls getSystemService() internally, leading to SO exception
                lazyImageLoader = lazyImageLoader,
                viewSourceUrl = url,
                bugReportUrl = "$url/issues",
                privacyPolicyUrl = PRIVACY_POLICY_URL,
                termsConditionsUrl = TERMS_CONDITIONS_URL,
                version = BuildConfig.VERSION_CODE,
                logger = createLogger(),
                theme = { activity, themeProvider, content ->
                    activity.SplatTrakTheme(
                        themeProvider = themeProvider,
                        content = content,
                    )
                },
            )

        return@lazy createComponent(
            PYDroid.init(
                this,
                parameters,
            ),
            lazyImageLoader,
        )
    }

    @CheckResult
    private fun createComponent(
        provider: ModuleProvider,
        imageLoader: Lazy<ImageLoader>,
    ): SplatComponent {
        return DaggerSplatComponent.factory()
            .create(
                application = this,
                debug = isDebugMode(),
                theming = provider.get().theming(),
                imageLoader = imageLoader,
            )
            .also { addLibraries() }
    }

    override fun onCreate() {
        super.onCreate()
        component.also { Timber.d("Component injected: $it") }
    }

    override fun getSystemService(name: String): Any? {
        // Use component here in a weird way to guarantee the lazy is initialized.
        return component.run { PYDroid.getSystemService(name) } ?: fallbackGetSystemService(name)
    }

    @CheckResult
    private fun fallbackGetSystemService(name: String): Any? {
        return if (name == SplatComponent::class.java.name) component
        else {
            super.getSystemService(name)
        }
    }

    companion object {

        @JvmStatic
        private fun addLibraries() {
            // We are using pydroid-notify
            OssLibraries.usingNotify = true

            // We are using pydroid-autopsy
            OssLibraries.usingAutopsy = true

            OssLibraries.add(
                "Dagger",
                "https://github.com/google/dagger",
                "A fast dependency injector for Android and Java."
            )
        }
    }
}
