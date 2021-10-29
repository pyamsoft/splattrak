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

package com.pyamsoft.splattrak.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

private const val linkText = "Splatoon2.ink"
private const val uriTag = "splatoon2-ink"

private inline fun AnnotatedString.Builder.withStringAnnotation(
    tag: String,
    annotation: String,
    content: () -> Unit
) {
  pushStringAnnotation(tag = tag, annotation = annotation)
  content()
  pop()
}

@Composable
fun NotNintendo(
    modifier: Modifier = Modifier,
) {
  Box(modifier = modifier) {
    val text = buildAnnotatedString {
      append(
          "This Application is not affiliated with Nintendo. All product names, logos, and brands are property of their respective owners.")
      append("\n\n")
      append("Map rotation data provided by ")

      withStringAnnotation(tag = uriTag, annotation = "https://splatoon2.ink") {
        withStyle(
            style =
                SpanStyle(
                    textDecoration = TextDecoration.Underline,
                    color = MaterialTheme.colors.primary,
                ),
        ) { append(linkText) }
      }
    }

    val uriHandler = LocalUriHandler.current
    ClickableText(
        text = text,
        style =
            MaterialTheme.typography.caption.copy(
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.onSurface,
            ),
        onClick = {
          onTextClicked(
              text = text,
              uriHandler = uriHandler,
              start = it,
          )
        },
    )
  }
}

private fun onTextClicked(text: AnnotatedString, uriHandler: UriHandler, start: Int) {
  text
      .getStringAnnotations(
          tag = uriTag,
          start = start,
          end = start + linkText.length,
      )
      .firstOrNull()
      ?.also { uriHandler.openUri(it.item) }
}

@Preview
@Composable
private fun PreviewNotNintendo() {
  Surface { NotNintendo() }
}
