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

package com.pyamsoft.splattrak.lobby.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import coil.ImageLoader
import com.pyamsoft.splattrak.lobby.screen.BattleInfo
import com.pyamsoft.splattrak.lobby.screen.SmallBattleMaps
import com.pyamsoft.splattrak.lobby.test.TestData
import com.pyamsoft.splattrak.splatnet.api.SplatMatch
import com.pyamsoft.splattrak.ui.createNewTestImageLoader

@Composable
internal fun DrilldownListItem(
    modifier: Modifier = Modifier,
    match: SplatMatch,
    imageLoader: ImageLoader,
) {
  Column(
      modifier = modifier,
  ) {
    BattleInfo(
        match = match,
    )
    SmallBattleMaps(
        match = match,
        imageLoader = imageLoader,
    )
  }
}

@Preview
@Composable
private fun PreviewDrilldownListItem() {
  val context = LocalContext.current

  Surface {
    DrilldownListItem(
        match = TestData.currentMatch,
        imageLoader = createNewTestImageLoader(context),
    )
  }
}
