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

import androidx.annotation.CheckResult
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberImagePainter
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.splattrak.lobby.R
import com.pyamsoft.splattrak.lobby.screen.list.LobbyItemViewState
import com.pyamsoft.splattrak.splatnet.api.SplatBattle
import com.pyamsoft.splattrak.splatnet.api.SplatGameMode
import com.pyamsoft.splattrak.splatnet.api.SplatMap
import com.pyamsoft.splattrak.splatnet.api.SplatMatch
import com.pyamsoft.splattrak.splatnet.api.SplatRuleset
import com.pyamsoft.splattrak.ui.createNewTestImageLoader
import java.time.LocalDateTime

@Composable
internal fun LobbyListItem(
    modifier: Modifier = Modifier,
    state: LobbyItemViewState,
    imageLoader: ImageLoader,
    onClick: () -> Unit,
) {
  val battle = state.data.requireNotNull().battle
  val name = battle.mode().name()
  val mode = battle.mode().mode()
  val rotation = battle.rotation()
  val backgroundColorResource = remember(mode) { decideBackgroundColor(mode) }
  val backgroundColor = colorResource(backgroundColorResource)

  Card(
      modifier = modifier.clickable { onClick() },
      backgroundColor = backgroundColor,
      contentColor = Color.White,
  ) {
    BackgroundStripeWrapper(
        modifier = Modifier.fillMaxWidth(),
        imageLoader = imageLoader,
    ) {
      Column(
          modifier = Modifier.fillMaxWidth(),
      ) {
        LobbyName(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            name = name,
        )
        CurrentBattle(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            imageLoader = imageLoader,
            rotation = rotation,
        )
        LobbyUpNext(
            modifier = Modifier.padding(horizontal = 8.dp),
        )
        NextBattle(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            imageLoader = imageLoader,
            rotation = rotation,
        )
      }
    }
  }
}

@Composable
private fun NextBattle(
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader,
    rotation: List<SplatMatch>,
) {
  val match = remember(rotation) { rotation[1] }
  Column(
      modifier = modifier,
  ) {
    BattleInfo(
        modifier = Modifier.fillMaxWidth(),
        match = match,
    )
    SmallBattleMaps(
        modifier = Modifier.fillMaxWidth(),
        match = match,
        imageLoader = imageLoader,
    )
  }
}

@Composable
private fun CurrentBattle(
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader,
    rotation: List<SplatMatch>,
) {
  val match = remember(rotation) { rotation[0] }
  BackgroundDarkWrapper(
      modifier = modifier,
      imageLoader = imageLoader,
  ) {
    Column(
        modifier = modifier,
    ) {
      BattleInfo(
          modifier = Modifier.fillMaxWidth(),
          match = match,
      )
      BigBattleMaps(
          modifier = Modifier.fillMaxWidth(),
          match = match,
          imageLoader = imageLoader,
      )
    }
  }
}

@Composable
private fun LobbyUpNext(
    modifier: Modifier = Modifier,
) {
  Row(
      modifier = modifier,
  ) {
    Surface(
        color = colorResource(R.color.splatNext),
        contentColor = Color.White,
        shape = MaterialTheme.shapes.medium,
    ) {
      Text(
          modifier = Modifier.padding(4.dp),
          text = stringResource(R.string.up_next),
          style = MaterialTheme.typography.body1,
          textAlign = TextAlign.Center,
      )
    }
    Spacer(
        modifier = Modifier.weight(1F),
    )
  }
}

@Composable
private fun LobbyName(
    modifier: Modifier = Modifier,
    name: String,
) {
  Box(
      modifier = modifier,
      contentAlignment = Alignment.Center,
  ) {
    Text(
        text = name,
        style = MaterialTheme.typography.h5,
        textAlign = TextAlign.Center,
    )
  }
}

@ColorRes
@CheckResult
private fun decideBackgroundColor(mode: SplatGameMode.Mode): Int {
  return when (mode) {
    SplatGameMode.Mode.REGULAR -> R.color.splatRegular
    SplatGameMode.Mode.LEAGUE -> R.color.splatLeague
    SplatGameMode.Mode.RANKED -> R.color.splatRanked
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
private fun BackgroundStripeWrapper(
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader,
    content: @Composable () -> Unit,
) {
  BackgroundWrapper(
      modifier = modifier,
      imageLoader = imageLoader,
      backgroundRes = R.drawable.repeating_stripes,
      contentScale = ContentScale.Crop,
      content = content,
  )
}

@Composable
private fun BackgroundDarkWrapper(
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

@Preview
@Composable
private fun PreviewLobbyListItem() {
  val context = LocalContext.current

  val currentMatch =
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
              return "Current"
            }

            override fun name(): String {
              return "Turf War"
            }
          }
        }
      }

  val nextMatch =
      object : SplatMatch {
        override fun id(): Long {
          return 2
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
              return "Next"
            }

            override fun name(): String {
              return "Turf War Again"
            }
          }
        }
      }

  LobbyListItem(
      state =
          LobbyItemViewState(
              isDisclaimer = false,
              data =
                  LobbyItemViewState.Data(
                      currentMatch = currentMatch,
                      nextMatch = nextMatch,
                      battle =
                          object : SplatBattle {
                            override fun mode(): SplatGameMode {
                              return object : SplatGameMode {
                                override fun key(): String {
                                  return "test"
                                }

                                override fun name(): String {
                                  return "TEST"
                                }

                                override fun mode(): SplatGameMode.Mode {
                                  return SplatGameMode.Mode.REGULAR
                                }
                              }
                            }

                            override fun rotation(): List<SplatMatch> {
                              return listOf(currentMatch, nextMatch)
                            }
                          },
                  )),
      imageLoader = createNewTestImageLoader(context),
      onClick = {},
  )
}
