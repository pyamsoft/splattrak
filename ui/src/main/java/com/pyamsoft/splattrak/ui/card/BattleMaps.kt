/*
 * Copyright 2022 Peter Kenji Yamanaka
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

package com.pyamsoft.splattrak.ui.card

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.splattrak.splatnet.api.SplatMap
import com.pyamsoft.splattrak.splatnet.api.SplatMatch
import com.pyamsoft.splattrak.ui.R
import com.pyamsoft.splattrak.ui.test.TestData
import com.pyamsoft.splattrak.ui.test.createNewTestImageLoader

@Composable
fun BigBattleMaps(
    modifier: Modifier = Modifier,
    match: SplatMatch,
    imageLoader: ImageLoader,
) {
  val configuration = LocalConfiguration.current
  val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

  val content =
      @Composable
      { m: Modifier ->
        BattleMaps(
            modifier = m,
            match = match,
            imageLoader = imageLoader,
        ) {
          Spacer(
              modifier =
                  Modifier.run {
                    if (isPortrait) {
                      height(MaterialTheme.keylines.content).fillMaxWidth()
                    } else {
                      width(MaterialTheme.keylines.content).fillMaxHeight()
                    }
                  },
          )
        }
      }

  if (isPortrait) {
    Column(
        modifier = modifier.height(320.dp),
    ) { content(Modifier.weight(1F)) }
  } else {
    Row(
        modifier = modifier.height(200.dp),
    ) { content(Modifier.weight(1F)) }
  }
}

@Composable
fun SmallBattleMaps(
    modifier: Modifier = Modifier,
    match: SplatMatch,
    imageLoader: ImageLoader,
) {
  Row(
      modifier = modifier.height(100.dp),
      verticalAlignment = Alignment.CenterVertically,
  ) {
    BattleMaps(
        modifier = Modifier.weight(1F),
        match = match,
        imageLoader = imageLoader,
    ) {
      Spacer(
          modifier = Modifier.width(MaterialTheme.keylines.baseline).fillMaxHeight(),
      )
    }
  }
}

@Composable
private fun BattleMaps(
    modifier: Modifier = Modifier,
    match: SplatMatch,
    imageLoader: ImageLoader,
    spacer: @Composable () -> Unit,
) {
  val mapA = match.stageA
  val mapB = match.stageB

  BattleMap(
      modifier = modifier,
      map = mapA,
      imageLoader = imageLoader,
  )
  spacer()
  BattleMap(
      modifier = modifier,
      map = mapB,
      imageLoader = imageLoader,
  )
}

@Composable
fun BattleMap(
    modifier: Modifier = Modifier,
    map: SplatMap,
    imageLoader: ImageLoader,
) {
  val name = map.name
  val imageUrl = map.imageUrl
  Box(
      modifier = modifier,
      contentAlignment = Alignment.BottomEnd,
  ) {
    Surface(
        color = Color.Transparent,
        contentColor = Color.White,
        shape = MaterialTheme.shapes.medium,
    ) {
      AsyncImage(
          model = imageUrl,
          imageLoader = imageLoader,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize(),
          contentDescription = name,
      )
    }

    Box(
        modifier = Modifier.padding(MaterialTheme.keylines.baseline),
    ) {
      Surface(
          color = Color.Transparent,
          contentColor = Color.White,
          shape = MaterialTheme.shapes.small,
      ) {
        AsyncImage(
            modifier = Modifier.matchParentSize(),
            model = R.drawable.map_name_chip,
            imageLoader = imageLoader,
            contentDescription = name,
        )
        Text(
            modifier = Modifier.padding(MaterialTheme.keylines.typography),
            text = name,
            style = MaterialTheme.typography.body2,
            color = Color.White,
            overflow = TextOverflow.Visible,
            maxLines = 1,
        )
      }
    }
  }
}

@Preview
@Composable
private fun PreviewBigBattleMaps() {
  Surface {
    BigBattleMaps(
        match = TestData.currentMatch,
        imageLoader = createNewTestImageLoader(),
    )
  }
}

@Preview
@Composable
private fun PreviewSmallBattleMaps() {
  Surface {
    SmallBattleMaps(
        match = TestData.currentMatch,
        imageLoader = createNewTestImageLoader(),
    )
  }
}
