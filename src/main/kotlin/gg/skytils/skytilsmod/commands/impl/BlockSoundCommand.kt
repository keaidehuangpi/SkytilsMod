/*
 * Skytils - Hypixel Skyblock Quality of Life Mod
 * Copyright (C) 2020-2024 Skytils
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
import gg.skytils.skytilsmod.commands.BaseCommand
import gg.skytils.skytilsmod.features.impl.misc.SoundBlocker
import gg.skytils.skytilsmod.features.impl.misc.SoundProperty
import net.minecraft.client.entity.EntityPlayerSP

object BlockSoundCommand : BaseCommand("blocksound") {
    override fun processCommand(player: EntityPlayerSP, args: Array<String>) {
        if (args.size == 2) {
            SoundBlocker.blockedSounds.add(SoundProperty(args[0], parseDouble(args[1]).toFloat()))
            UChat.chat("Successfully added!")
        } else if (args.size == 1) {
            SoundBlocker.blockedALL.add(args[0])
            UChat.chat("Successfully added a type of blocked sounds!")
        } else {
            UChat.chat("Wrong usage!")
        }
    }
}
