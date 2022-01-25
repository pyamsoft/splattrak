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

package com.pyamsoft.splattrak.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

// Copied from material-icons-extended
@Suppress("unused")
val Icons.Filled.MeetingRoom: ImageVector
    get() {
        if (_meetingRoom != null) {
            return _meetingRoom!!
        }
        _meetingRoom = materialIcon(name = "Filled.MeetingRoom") {
            materialPath {
                moveTo(14.0f, 6.0f)
                verticalLineToRelative(15.0f)
                lineTo(3.0f, 21.0f)
                verticalLineToRelative(-2.0f)
                horizontalLineToRelative(2.0f)
                lineTo(5.0f, 3.0f)
                horizontalLineToRelative(9.0f)
                verticalLineToRelative(1.0f)
                horizontalLineToRelative(5.0f)
                verticalLineToRelative(15.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(-4.0f)
                lineTo(17.0f, 6.0f)
                horizontalLineToRelative(-3.0f)
                close()
                moveTo(10.0f, 11.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(-2.0f)
                horizontalLineToRelative(-2.0f)
                close()
            }
        }
        return _meetingRoom!!
    }

@Suppress("ObjectPropertyName")
private var _meetingRoom: ImageVector? = null
