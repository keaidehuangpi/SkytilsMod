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

import com.baidubce.qianfan.Qianfan
import com.baidubce.qianfan.core.auth.Auth
import gg.essential.universal.UChat
import gg.skytils.skytilsmod.Skytils
import gg.skytils.skytilsmod.commands.BaseCommand
import net.minecraft.client.entity.EntityPlayerSP

object WXYYChatCommand : BaseCommand("wxyychat") {
    val prefix = "§9§lAI ASSISTANT §8» "
    override fun processCommand(player: EntityPlayerSP, args: Array<String>) {
        if (!Skytils.config.wxyy) {
            UChat.chat(prefix + "U DIDN'T ENABLE ME!")
            return
        }
        if (Skytils.config.wxyyak == "" || Skytils.config.wxyysk == "") {
            UChat.chat(prefix + "YOUR KEY(S) NOT SET!")
            return
        }


        val msg = StringBuilder();
        args.forEachIndexed { index, s ->
            run {
                if (index == 0) {
                    msg.append(s)
                } else {
                    msg.append(" ")
                    msg.append(s)
                }
            }
        }
        Thread {
            val qianfan = Qianfan(Auth.TYPE_OAUTH, Skytils.config.wxyyak, Skytils.config.wxyysk)
            val builder = qianfan.chatCompletion()
                .endpoint(Skytils.config.wxyyep)
                .topP(0.2)
                .system("你是一个AI助手，请给我尽量简短的回复。回答所用语言请务必与我问你的时候使用的语言一致。")
            val res = builder.addMessage("user", msg.toString()).execute().result
            UChat.chat("$prefix&r$res")
        }.start()

    }

}
