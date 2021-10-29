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

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberImagePainter
import com.google.accompanist.insets.navigationBarsPadding

@Composable
fun MainBottomNav(
    modifier: Modifier = Modifier,
    page: MainPage,
    imageLoader: ImageLoader,
    onLoadLobby: () -> Unit,
    onLoadSettings: () -> Unit,
) {
  // Can't use BottomAppBar since we can't modify its Shape
  Surface(
      modifier = modifier.padding(16.dp).navigationBarsPadding(bottom = true),
      shape = RoundedCornerShape(4.dp),
      color = MaterialTheme.colors.primary,
      contentColor = Color.White,
  ) {
    BottomNavigation {
      Item(
          current = page,
          target = MainPage.Lobby,
          imageLoader = imageLoader,
          onClick = onLoadLobby,
      )
      Item(
          current = page,
          target = MainPage.Settings,
          imageLoader = imageLoader,
          onClick = onLoadSettings,
      )
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
        Icon(
            painter =
                rememberImagePainter(
                    data =
                        when (target) {
                          is MainPage.Lobby -> R.drawable.ic_lobby_24dp
                          is MainPage.Settings -> R.drawable.ic_settings_24dp
                        },
                    imageLoader = imageLoader,
                ),
            contentDescription = target.name,
        )
      },
  )
}
