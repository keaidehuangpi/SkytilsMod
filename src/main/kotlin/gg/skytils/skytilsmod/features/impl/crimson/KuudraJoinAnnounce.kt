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

import gg.essential.universal.UChat
import gg.essential.universal.wrappers.message.UMessage
import gg.essential.universal.wrappers.message.UTextComponent
import gg.skytils.hypixel.types.skyblock.Member
import gg.skytils.hypixel.types.skyblock.Pet
import gg.skytils.skytilsmod.Skytils
import gg.skytils.skytilsmod.core.API
import gg.skytils.skytilsmod.utils.*
import gg.skytils.skytilsmod.utils.NumberUtil.toRoman
import gg.skytils.skytilsmod.utils.SkillUtils.level
import kotlinx.coroutines.launch
import net.minecraft.event.ClickEvent
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.*
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

object KuudraJoinAnnounce {

    private val partyFinderRegex = Regex(
        "^Party Finder > (?<name>\\w+) joined the group! \\(Combat Level (?<combatlevel>\\d+)\\)$"
    )

    @SubscribeEvent(receiveCanceled = true, priority = EventPriority.HIGHEST)
    fun onChat(event: ClientChatReceivedEvent) {
        if (!Utils.isOnHypixel || event.type == 2.toByte()) return
        if (Skytils.config.kuudraJoinAnnounce) {
            val match = partyFinderRegex.find(event.message.formattedText.stripControlCodes()) ?: return
            val username = match.groups["name"]?.value ?: return
            if (username == Skytils.mc.thePlayer.name) {
                Skytils.sendMessageQueue.add("/pc " + Skytils.config.messageKJA.ifBlank { "SkytilsAnnouncer > Message not specified" })
            }

        }
    }


}