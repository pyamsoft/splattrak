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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberImagePainter
import com.google.accompanist.insets.navigationBarsPadding
import com.pyamsoft.splattrak.ui.test.createNewTestImageLoader

@Composable
fun MainBottomNav(
    modifier: Modifier = Modifier,
    page: MainPage,
    imageLoader: ImageLoader,
    onHeightMeasured: (Int) -> Unit,
    onLoadPage: (MainPage) -> Unit,
) {
  // Can't use BottomAppBar since we can't modify its Shape
  Surface(
      modifier =
          modifier
              .padding(vertical = 16.dp, horizontal = 64.dp)
              .navigationBarsPadding(bottom = true)
              .onSizeChanged { onHeightMeasured(it.height) },
      shape = RoundedCornerShape(8.dp),
      color = MaterialTheme.colors.primary,
      contentColor = Color.White,
  ) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = Color.White,
    ) {
      for (p in MainPage.values()) {
        Item(
            current = page,
            target = p,
            imageLoader = imageLoader,
            onClick = { onLoadPage(p) },
        )
      }
    }
  }
}

@Composable
private fun RowScope.Item(
    current: MainPage,
    target: MainPage,
    imageLoader: ImageLoader,
    onClick: () -> Unit
) {
  BottomNavigationItem(
      selected = current == target,
      onClick = onClick,
      icon = {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Icon(
              painter =
                  rememberImagePainter(
                      data =
                          when (target) {
                            MainPage.LOBBY -> R.drawable.ic_lobby_24dp
                            MainPage.COOP -> R.drawable.ic_lobby_24dp
                            MainPage.SETTINGS -> R.drawable.ic_settings_24dp
                          },
                      imageLoader = imageLoader,
                  ),
              contentDescription = target.display,
          )
          Text(
              text = target.display,
              style = MaterialTheme.typography.body2,
          )
        }
      },
  )
}

@Preview
@Composable
private fun PreviewMainBottomNav() {
  Surface {
    MainBottomNav(
        page = MainPage.LOBBY,
        imageLoader = createNewTestImageLoader(),
        onHeightMeasured = {},
        onLoadPage = {},
    )
  }
}
