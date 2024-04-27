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

import gg.essential.universal.utils.MCClickEventAction
import gg.essential.universal.utils.MCHoverEventAction
import gg.essential.universal.wrappers.message.UMessage
import gg.essential.universal.wrappers.message.UTextComponent
import gg.skytils.skytilsmod.Skytils
import gg.skytils.skytilsmod.events.impl.MainReceivePacketEvent
import net.minecraft.network.play.server.S29PacketSoundEffect
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.*


object SoundBlocker {
    val blockedSounds = HashSet<SoundProperty>()
    val blockedALL = HashSet<String>()

    @SubscribeEvent
    fun onPacket(event: MainReceivePacketEvent<*, *>) {
        if (event.packet is S29PacketSoundEffect) {
            if (Skytils.config.soundblocker && blockedALL.contains(event.packet.soundName)) {
                event.isCanceled = true
                return
            }
            if (Skytils.config.soundblocker && blockedSounds.contains(
                    SoundProperty(
                        event.packet.soundName,
                        event.packet.pitch
                    )
                )
            ) {
                event.isCanceled = true
                return
            }
            if (Skytils.config.editSoundBlocker) {
                val p = event.packet
                UMessage(
                    UTextComponent("&a&l[SoundBlocker]&r &eSoundName:&b ${p.soundName}   &ePitch:&b ${p.pitch}").setClick(
                        MCClickEventAction.RUN_COMMAND,
                        if (Skytils.config.soundBlockerEditMode == 0) "/blocksound ${p.soundName} ${p.pitch}" else "/blocksound ${p.soundName}"
                    ).setHover(
                        MCHoverEventAction.SHOW_TEXT,
                        if (Skytils.config.soundBlockerEditMode == 0) "CLICK TO BLOCK THIS!" else "CLICK TO BLOCK ALL OF ITS TYPE!"
                    ).chat()
                ).chat()
            }
        }
    }

}

class SoundProperty(
    val name: String,
    val pitch: Float

) {
    override fun equals(other: Any?): Boolean {
        if (other is SoundProperty) (
                return other.name.equals(name) && other.pitch.equals(pitch)
                ) else {
            return false
        }
    }

    override fun hashCode(): Int {
        return Objects.hash(name, pitch)
    }
}