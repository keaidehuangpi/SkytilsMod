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
import gg.essential.universal.utils.MCClickEventAction
import gg.essential.universal.wrappers.message.UMessage
import gg.essential.universal.wrappers.message.UTextComponent
import gg.skytils.skytilsmod.Skytils
import gg.skytils.skytilsmod.cape.GuiCapeManager
import gg.skytils.skytilsmod.commands.BaseCommand
import gg.skytils.skytilsmod.utils.setHoverText
import net.minecraft.client.entity.EntityPlayerSP

object CapeManagerCommand : BaseCommand("managecapes") {


    override fun getCommandUsage(player: EntityPlayerSP): String =
        "/${commandName}"

    override fun processCommand(player: EntityPlayerSP, args: Array<String>) {


        if (args.getOrNull(0) != null && args[0].equals("select", true) && (args.getOrNull(1) != null)
        ) {
            for (cape in GuiCapeManager.capeList) {
                if (args[1].equals(cape.name, true)) {
                    GuiCapeManager.nowCape = cape;
                    UChat.chat("Successfully changed your cape to ${cape.name}")
                    return
                }
            }
            UChat.chat("Cannot find which cape you mean!")
        } else if (args.getOrNull(0) != null && args[0].equals("refresh", true)) {
            GuiCapeManager.load()
            UChat.chat("§bSuccessfully reloaded!")
        } else {
            UMessage("${Skytils.prefix} §bSelect a Cape!\n").apply {
                for (cape in GuiCapeManager.capeList) {
                    addTextComponent(
                        UTextComponent("§7[§a${cape.name}§7]   ").setClick(
                            MCClickEventAction.RUN_COMMAND,
                            "/${commandName} select ${cape.name}"
                        ).setHoverText("§c§lSELECT §r§a${cape.name}")
                    )
                }
                addTextComponent(
                    UTextComponent("\n\n§c§l----CLICK ME TO REFRESH!----").setClick(
                        MCClickEventAction.RUN_COMMAND,
                        "/${commandName} refresh"
                    ).setHoverText("REFRESH")
                )
            }.chat()
        }

    }

}