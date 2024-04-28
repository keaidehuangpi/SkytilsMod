/*
 * Skytils - Hypixel Skyblock Quality of Life Mod
 * Copyright (C) 2020-2023 Skytils
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package gg.skytils.skytilsmod.commands.impl

import gg.skytils.skytilsmod.Skytils.Companion.mc
import gg.skytils.skytilsmod.commands.BaseCommand
import gg.skytils.skytilsmod.utils.cheats.ColorUtils.stripColor
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.network.NetworkPlayerInfo

object RemoveALLPlayerCommand : BaseCommand("fuckallplayers") {
    override fun processCommand(player: EntityPlayerSP, args: Array<String>) {
        for (entity in mc.theWorld.loadedEntityList) {
            if (entity !is EntityPlayerSP) {
                continue
            }
            if (entity.uniqueID.equals(mc.thePlayer.uniqueID)) {
                continue
            }
            val targetName = stripColor(entity.displayName.formattedText)

            for (networkPlayerInfo in mc.netHandler.playerInfoMap) {
                val networkName = stripColor(networkPlayerInfo.getFullName())

                if (targetName.contains(networkName)) {
                    continue
                }
            }
            mc.theWorld.removeEntity(entity)
        }
    }

    fun NetworkPlayerInfo.getFullName(): String {
        if (displayName != null) {
            return displayName!!.formattedText
        }

        val team = playerTeam
        val name = gameProfile.name
        return team?.formatString(name) ?: name
    }
}
