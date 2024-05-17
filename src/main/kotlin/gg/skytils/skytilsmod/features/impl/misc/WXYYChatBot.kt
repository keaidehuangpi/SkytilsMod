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

package gg.skytils.skytilsmod.features.impl.misc

import com.baidubce.qianfan.Qianfan
import com.baidubce.qianfan.core.auth.Auth
import gg.essential.universal.UChat
import gg.skytils.skytilsmod.Skytils
import gg.skytils.skytilsmod.utils.Utils
import gg.skytils.skytilsmod.utils.cheats.ColorUtils
import gg.skytils.skytilsmod.utils.stripControlCodes
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object WXYYChatBot {
    private val partyChatRegex = Regex(
        "^Party > (?<name>.+): !wxyy (?<msg>[\\s\\S]+)$"
    )

    @SubscribeEvent(receiveCanceled = true, priority = EventPriority.HIGHEST)
    fun onChat(event: ClientChatReceivedEvent) {
        if (!Utils.isOnHypixel || event.type == 2.toByte()) return
        if (Skytils.config.wxyy) {
            val match =
                partyChatRegex.find(ColorUtils.stripColor(event.message.formattedText.stripControlCodes())) ?: return
            val msg = match.groups["msg"]?.value ?: return
            UChat.chat("§9§lAI ASSISTANT §8» §rPLEASE WAIT...")
            Thread {
                val qianfan = Qianfan(Auth.TYPE_OAUTH, Skytils.config.wxyyak, Skytils.config.wxyysk)
                val builder = qianfan.chatCompletion()
                    .endpoint(Skytils.config.wxyyep)
                    .topP(0.2)
                    .system("你是一个AI助手，请给我尽量简短的回复。如果我问你问题用的是英语，请你用英语回答我")
                val res = builder.addMessage("user", msg).execute().result
                Skytils.sendMessageQueue.add(res)
            }.start()
        }
    }
}