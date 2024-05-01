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

import gg.essential.universal.UChat
import gg.skytils.skytilsmod.Skytils.Companion.mc
import gg.skytils.skytilsmod.commands.BaseCommand
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.network.play.client.C02PacketUseEntity

object ClickEggCommand : BaseCommand("clickegg") {
    override fun processCommand(player: EntityPlayerSP, args: Array<String>) {
        if (!player.name.equals("thebeijing", true)) {
            UChat.chat("NOT ALLOWED!This is in hard development and may cause a ban.")
            return
        }
        for (ent in mc.theWorld.loadedEntityList) {
            if (ent is EntityArmorStand) {
                if ((ent.getDistanceToEntity(mc.pointedEntity) > 1.5)) {
                    continue
                }
                if ((ent.getDistanceToEntity(mc.thePlayer) > 2)) {
                    continue
                }
                if (ent.getCurrentArmor(3) == null || ent.getCurrentArmor(3).displayName == null || (!ent.getCurrentArmor(
                        3
                    ).toString().contains("skull", true))
                ) {
                    continue
                }
                mc.netHandler.addToSendQueue(C02PacketUseEntity(ent, C02PacketUseEntity.Action.ATTACK))
                mc.thePlayer.swingItem()
                return
            }
        }
    }


}
