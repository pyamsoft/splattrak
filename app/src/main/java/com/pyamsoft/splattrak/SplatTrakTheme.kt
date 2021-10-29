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

import androidx.annotation.CheckResult
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.pyamsoft.pydroid.ui.theme.ThemeProvider
import com.pyamsoft.pydroid.ui.theme.Theming

@Composable
@CheckResult
private fun themeColors(isDarkMode: Boolean): Colors {
  val primary = colorResource(R.color.colorPrimary)
  val onPrimary = colorResource(R.color.colorOnPrimary)
  val secondary = colorResource(R.color.colorSecondary)
  val onSecondary = colorResource(R.color.colorOnSecondary)
  return if (isDarkMode)
      darkColors(
          primary = primary,
          onPrimary = onPrimary,
          secondary = secondary,
          onSecondary = onSecondary,
      )
  else
      lightColors(
          primary = primary,
          onPrimary = onPrimary,
          secondary = secondary,
          onSecondary = onSecondary,
      )
}

@Composable
@CheckResult
private fun themeTypography(): Typography {
  return Typography(
      defaultFontFamily =
          FontFamily(
              Font(R.font.splat2, FontWeight.W100),
              Font(R.font.splat2, FontWeight.W200),
              Font(R.font.splat2, FontWeight.W300),
              Font(R.font.splat2, FontWeight.W400),
              Font(R.font.splat2, FontWeight.W500),
              Font(R.font.splat2, FontWeight.W600),
              Font(R.font.splat2, FontWeight.W700),
              Font(R.font.splat2, FontWeight.W800),
              Font(R.font.splat2, FontWeight.W900),
          ),
  )
}

@Composable
fun SplatTrakTheme(
    themeProvider: ThemeProvider,
    content: @Composable () -> Unit,
) {
  SplatTrakTheme(
      theme = if (themeProvider.isDarkTheme()) Theming.Mode.DARK else Theming.Mode.LIGHT,
      content = content,
  )
}

@Composable
fun SplatTrakTheme(
    theme: Theming.Mode,
    content: @Composable () -> Unit,
) {
  val isDarkMode =
      when (theme) {
        Theming.Mode.LIGHT -> false
        Theming.Mode.DARK -> true
        Theming.Mode.SYSTEM -> isSystemInDarkTheme()
      }
  MaterialTheme(
      colors = themeColors(isDarkMode),
      typography = themeTypography(),
      content = content,
  )
}

enum class ThemeType {
  LIGHT,
  DARK,
  SYSTEM
}
