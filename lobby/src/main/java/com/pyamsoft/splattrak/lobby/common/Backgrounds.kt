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

package com.pyamsoft.splattrak.lobby.common

import androidx.annotation.CheckResult
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.ImageLoader
import coil.compose.rememberImagePainter
import com.pyamsoft.splattrak.lobby.R
import com.pyamsoft.splattrak.ui.R as R2
import com.pyamsoft.splattrak.splatnet.api.SplatGameMode

@ColorRes
@CheckResult
internal fun decideBackgroundColor(mode: SplatGameMode.Mode): Int {
  return when (mode) {
    SplatGameMode.Mode.REGULAR -> R2.color.splatRegular
    SplatGameMode.Mode.LEAGUE -> R2.color.splatLeague
    SplatGameMode.Mode.RANKED -> R2.color.splatRanked
  }
}

@Composable
private fun BackgroundWrapper(
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader,
    @DrawableRes backgroundRes: Int,
    contentScale: ContentScale = ContentScale.Fit,
    content: @Composable () -> Unit,
) {
  Box(
      modifier = modifier,
  ) {
    Surface(
        color = Color.Transparent,
        contentColor = Color.White,
        shape = MaterialTheme.shapes.medium,
    ) {
      Image(
          modifier = Modifier.matchParentSize(),
          contentScale = contentScale,
          painter =
              rememberImagePainter(
                  data = backgroundRes,
                  imageLoader = imageLoader,
              ),
          contentDescription = null,
      )

      content()
    }
  }
}

@Composable
internal fun BackgroundStripeWrapper(
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader,
    content: @Composable () -> Unit,
) {
  BackgroundWrapper(
      modifier = modifier,
      imageLoader = imageLoader,
      backgroundRes = R2.drawable.repeating_stripes,
      contentScale = ContentScale.Crop,
      content = content,
  )
}

@Composable
internal fun BackgroundDarkWrapper(
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader,
    content: @Composable () -> Unit,
) {
  BackgroundWrapper(
      modifier = modifier,
      imageLoader = imageLoader,
      backgroundRes = R.drawable.current_container_background,
      content = content,
  )
}
