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

package com.pyamsoft.splattrak.splatnet.data

import com.pyamsoft.splattrak.splatnet.SPLATNET_ASSET_URL
import com.pyamsoft.splattrak.splatnet.api.SplatCoopSession

internal data class SplatCoopWeaponImpl
internal constructor(
    private val name: String?,
    private val image: String?,
) : SplatCoopSession.Map.Weapon {

    private val weaponName = name ?: "Mystery"
    private val imageUrl = "$SPLATNET_ASSET_URL${image ?: MYSTERY_WEAPON}"

    override fun name(): String {
        return weaponName
    }

    override fun imageUrl(): String {
        return imageUrl
    }

    companion object {

        private const val MYSTERY_WEAPON =
            "/images/coop_weapons/7076c8181ab5c49d2ac91e43a2d945a46a99c17d.png"
    }
}
