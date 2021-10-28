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

package com.pyamsoft.splattrak.ui

import android.content.res.Configuration
import android.os.Build
import androidx.annotation.CheckResult
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import java.util.Locale

/** Gets the current locale */
@Composable
fun getCurrentLocale(): Locale {
  return LocalConfiguration.current.run {
    if (Build.VERSION.SDK_INT >= 24) {
      newGetCurrentLocal(this)
    } else {
      oldGetCurrentLocal(this)
    }
  }
}

@CheckResult
@RequiresApi(Build.VERSION_CODES.N)
private fun newGetCurrentLocal(configuration: Configuration): Locale {
  return configuration.locales[0]
}

@CheckResult
@Suppress("DEPRECATION")
private fun oldGetCurrentLocal(configuration: Configuration): Locale {
  return configuration.locale
}
