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

package gg.skytils.skytilsmod.features.impl.crimson

import gg.skytils.skytilsmod.Skytils
import gg.skytils.skytilsmod.events.impl.CheckRenderEntityEvent
import gg.skytils.skytilsmod.events.impl.GuiContainerEvent
import gg.skytils.skytilsmod.features.impl.protectitems.ProtectItems
import gg.skytils.skytilsmod.features.impl.protectitems.strategy.ItemProtectStrategy
import gg.skytils.skytilsmod.utils.ItemUtil
import gg.skytils.skytilsmod.utils.SBInfo
import gg.skytils.skytilsmod.utils.SkyblockIsland
import gg.skytils.skytilsmod.utils.Utils
import gg.skytils.skytilsmod.utils.cheats.ColorUtils
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.init.Blocks
import net.minecraft.inventory.ContainerChest
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.input.Keyboard

object ChestAnnounce {
    @SubscribeEvent
    fun onSlotClick(event: GuiContainerEvent.SlotClickEvent) {
        if (!Utils.inSkyblock) return
        if (SBInfo.mode != SkyblockIsland.KuudraHollow.mode) return
        if (event.container !is ContainerChest) return
        if (!Skytils.config.chestAnnounce) return
        if (!(ColorUtils.stripColor(event.chestName).equals("Paid Chest") || ColorUtils.stripColor(event.chestName)
                .equals("Free Chest"))
        ) return

        val inv = event.container.inventory
        val item = inv.get(event.slotId)
        if (item == null || !item.hasDisplayName() || item.displayName == null) return
        if (ColorUtils.stripColor(item.displayName).equals("Open Reward Chest")) {
            Skytils.sendMessageQueue.add(Skytils.config.messageChestAnnounce.ifBlank { "/pc Skytils > Kuudra chest opened and im r." })
        }


    }
}