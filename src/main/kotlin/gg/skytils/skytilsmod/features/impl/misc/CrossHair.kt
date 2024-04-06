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

import gg.skytils.skytilsmod.Skytils
import gg.skytils.skytilsmod.events.impl.MainReceivePacketEvent
import gg.skytils.skytilsmod.events.impl.RenderHUDEvent
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.network.play.server.S29PacketSoundEffect
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11

object CrossHair {
    var stampHit = 0L;

    @SubscribeEvent
    fun onEvent(event: MainReceivePacketEvent<*, *>) {
        if (!Skytils.config.hitMark) {
            return
        }
        if (event.packet is S29PacketSoundEffect) {
            val p = event.packet
            if (p.pitch.equals(0.7936508F) && p.soundName.equals("random.successful_hit")) {
                stampHit = System.currentTimeMillis()

            }


        }
    }

    @SubscribeEvent
    fun onRender(event: RenderHUDEvent) {
        if (!Skytils.config.hitMark) {
            return
        }
        if (System.currentTimeMillis() - stampHit < Skytils.config.hitMarkTime) {
            val sr = event.event.resolution
            val width = 0.5f
            val size = 3f
            val gap = 2f
            GL11.glPushMatrix()
            GlStateManager.enableBlend()
            GlStateManager.disableTexture2D()
            GlStateManager.tryBlendFuncSeparate(
                GL11.GL_SRC_ALPHA,
                GL11.GL_ONE_MINUS_SRC_ALPHA,
                GL11.GL_ONE,
                GL11.GL_ZERO
            )

            GL11.glColor4f(100f, 0f, 0f, (System.currentTimeMillis() - stampHit / Skytils.config.hitMarkTime).toFloat())
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glLineWidth(1f)
            GL11.glBegin(3)
            GL11.glVertex2f(sr.scaledWidth / 2f + gap, sr.scaledHeight / 2f + gap)
            GL11.glVertex2f(sr.scaledWidth / 2f + gap + size, sr.scaledHeight / 2f + gap + size)
            GL11.glEnd()
            GL11.glBegin(3)
            GL11.glVertex2f(sr.scaledWidth / 2f - gap, sr.scaledHeight / 2f - gap)
            GL11.glVertex2f(sr.scaledWidth / 2f - gap - size, sr.scaledHeight / 2f - gap - size)
            GL11.glEnd()
            GL11.glBegin(3)
            GL11.glVertex2f(sr.scaledWidth / 2f - gap, sr.scaledHeight / 2f + gap)
            GL11.glVertex2f(sr.scaledWidth / 2f - gap - size, sr.scaledHeight / 2f + gap + size)
            GL11.glEnd()
            GL11.glBegin(3)
            GL11.glVertex2f(sr.scaledWidth / 2f + gap, sr.scaledHeight / 2f - gap)
            GL11.glVertex2f(sr.scaledWidth / 2f + gap + size, sr.scaledHeight / 2f - gap - size)
            GL11.glEnd()
            GlStateManager.enableTexture2D()
            GlStateManager.disableBlend()
            GL11.glPopMatrix()
        }
    }
}