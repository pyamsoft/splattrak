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

package com.pyamsoft.splattrak.setting

import android.content.Context
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.preference.PreferenceViewHolder
import com.pyamsoft.pydroid.ui.preference.PreferenceCompat
import com.pyamsoft.splattrak.setting.databinding.PreferenceSpacerBinding

internal class PreferenceBottomSpace internal constructor(
    private val height: Int,
    context: Context,
) : PreferenceCompat(context) {

    init {
        layoutResource = R.layout.preference_spacer
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        height.let { h ->
            val view = holder.itemView
            val binding = PreferenceSpacerBinding.bind(view)
            binding.nintendoDisclaimer.setText(R.string.nintendo_disclaimer)
            if (h > 0) {
                view.updatePadding(bottom = h)
                binding.nintendoDisclaimer.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    this.bottomMargin = h
                }
            }
        }
    }
}
