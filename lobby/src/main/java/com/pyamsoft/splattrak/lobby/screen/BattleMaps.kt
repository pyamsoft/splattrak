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

package com.pyamsoft.splattrak.lobby.screen

import androidx.compose.foundation.Image
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberImagePainter
import com.pyamsoft.splattrak.lobby.R
import com.pyamsoft.splattrak.splatnet.api.SplatMap
import com.pyamsoft.splattrak.splatnet.api.SplatMatch
import com.pyamsoft.splattrak.splatnet.api.SplatRuleset
import com.pyamsoft.splattrak.ui.createNewTestImageLoader
import java.time.LocalDateTime

@Composable
internal fun BigBattleMaps(
    modifier: Modifier = Modifier,
    match: SplatMatch,
    imageLoader: ImageLoader,
) {
  Column(
      modifier = modifier.height(320.dp),
  ) {
    BattleMaps(
        modifier = Modifier.weight(1F),
        match = match,
        imageLoader = imageLoader,
    ) {
      Spacer(
          modifier = Modifier.height(8.dp).fillMaxWidth(),
      )
    }
  }
}

@Composable
internal fun SmallBattleMaps(
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
          modifier = Modifier.width(8.dp).fillMaxHeight(),
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
  val mapA = match.stageA()
  val mapB = match.stageB()

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
private fun BattleMap(
    modifier: Modifier = Modifier,
    map: SplatMap,
    imageLoader: ImageLoader,
) {
  val name = map.name()
  val imageUrl = map.imageUrl()
  Box(
      modifier = modifier,
      contentAlignment = Alignment.BottomEnd,
  ) {
    Surface(
        color = Color.Transparent,
        contentColor = Color.White,
        shape = MaterialTheme.shapes.medium,
    ) {
      Image(
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize(),
          painter =
              rememberImagePainter(
                  data = imageUrl,
                  imageLoader = imageLoader,
              ),
          contentDescription = name,
      )
    }

    Box(
        modifier = Modifier.padding(8.dp),
    ) {
      Surface(
          color = Color.Transparent,
          contentColor = Color.White,
          shape = MaterialTheme.shapes.medium,
      ) {
        Image(
            modifier = Modifier.matchParentSize(),
            painter =
                rememberImagePainter(
                    data = R.drawable.map_name_chip,
                    imageLoader = imageLoader,
                ),
            contentDescription = name,
        )
        Text(
            modifier = Modifier.padding(4.dp),
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

private val fakeMatch =
    object : SplatMatch {
      override fun id(): Long {
        return 1
      }

      override fun start(): LocalDateTime {
        return LocalDateTime.now()
      }

      override fun end(): LocalDateTime {
        return LocalDateTime.now().plusHours(1)
      }

      override fun stageA(): SplatMap {
        return object : SplatMap {
          override fun id(): String {
            return "A"
          }

          override fun name(): String {
            return "Stage A"
          }

          override fun imageUrl(): String {
            return ""
          }
        }
      }

      override fun stageB(): SplatMap {
        return object : SplatMap {
          override fun id(): String {
            return "B"
          }

          override fun name(): String {
            return "Stage B"
          }

          override fun imageUrl(): String {
            return ""
          }
        }
      }

      override fun rules(): SplatRuleset {
        return object : SplatRuleset {
          override fun key(): String {
            return "test"
          }

          override fun name(): String {
            return "Turf War"
          }
        }
      }
    }

@Preview
@Composable
private fun PreviewSmallBattleMaps() {
  val context = LocalContext.current
  Surface {
    SmallBattleMaps(
        match = fakeMatch,
        imageLoader = createNewTestImageLoader(context),
    )
  }
}
