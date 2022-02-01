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
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsPadding
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.splattrak.ui.icons.Group
import com.pyamsoft.splattrak.ui.icons.MeetingRoom

@Composable
fun MainBottomNav(
    modifier: Modifier = Modifier,
    page: MainPage,
    onHeightMeasured: (Int) -> Unit,
    onLoadPage: (MainPage) -> Unit,
) {
    // Can't use BottomAppBar since we can't modify its Shape
    Surface(
        modifier =
        modifier
            .padding(vertical = MaterialTheme.keylines.content, horizontal = 64.dp)
            .navigationBarsPadding(bottom = true)
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
            for (p in MainPage.values()) {
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
private fun RowScope.Item(current: MainPage, target: MainPage, onClick: () -> Unit) {
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
                        MainPage.LOBBY -> Icons.Filled.MeetingRoom
                        MainPage.COOP -> Icons.Filled.Group
                        MainPage.SETTINGS -> Icons.Filled.Settings
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
            page = MainPage.LOBBY,
            onHeightMeasured = {},
            onLoadPage = {},
        )
    }
}
