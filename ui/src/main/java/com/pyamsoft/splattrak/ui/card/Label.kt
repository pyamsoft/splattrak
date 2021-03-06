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

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.splattrak.ui.R

@Composable
fun Label(
    modifier: Modifier = Modifier,
    text: String,
) {
  Surface(
      modifier = modifier,
      color = colorResource(R.color.splatNext),
      contentColor = Color.White,
      shape = MaterialTheme.shapes.medium,
  ) {
    Text(
        modifier = Modifier.padding(MaterialTheme.keylines.baseline),
        text = text,
        style = MaterialTheme.typography.body1,
        textAlign = TextAlign.Center,
    )
  }
}
