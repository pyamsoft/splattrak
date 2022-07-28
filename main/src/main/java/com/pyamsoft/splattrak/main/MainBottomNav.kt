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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.splattrak.ui.icons.Group
import com.pyamsoft.splattrak.ui.icons.MeetingRoom

private val ALL_TOP_LEVEL_PAGES =
    listOf(
        TopLevelMainPage.Lobby,
        TopLevelMainPage.Coop,
        TopLevelMainPage.Settings,
    )

@Composable
fun MainBottomNav(
    modifier: Modifier = Modifier,
    page: TopLevelMainPage,
    onHeightMeasured: (Int) -> Unit,
    onLoadPage: (TopLevelMainPage) -> Unit,
) {
  // Can't use BottomAppBar since we can't modify its Shape
  Surface(
      modifier =
          modifier
              .padding(vertical = MaterialTheme.keylines.content, horizontal = 64.dp)
              .navigationBarsPadding()
              .onSizeChanged { onHeightMeasured(it.height) },
      shape = RoundedCornerShape(MaterialTheme.keylines.baseline),
      color = MaterialTheme.colors.primary,
      contentColor = Color.White,
      elevation = AppBarDefaults.BottomAppBarElevation,
  ) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = Color.White,
    ) {
      for (p in ALL_TOP_LEVEL_PAGES) {
        Item(
            current = page,
            target = p,
            onClick = { onLoadPage(p) },
        )
      }
    }
  }
}

@Composable
private fun RowScope.Item(
    current: TopLevelMainPage,
    target: TopLevelMainPage,
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
              imageVector =
                  when (target) {
                    TopLevelMainPage.Lobby -> Icons.Filled.MeetingRoom
                    TopLevelMainPage.Coop -> Icons.Filled.Group
                    TopLevelMainPage.Settings -> Icons.Filled.Settings
                  },
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
        page = TopLevelMainPage.Lobby,
        onHeightMeasured = {},
        onLoadPage = {},
    )
  }
}
