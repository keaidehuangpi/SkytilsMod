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


import gg.skytils.skytilsmod.Skytils.Companion.mc
import gg.skytils.skytilsmod.core.Config
import gg.skytils.skytilsmod.utils.RenderUtil
import gg.skytils.skytilsmod.utils.cheats.ColorUtils
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.EntityLivingBase
import net.minecraftforge.client.event.RenderLivingEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.opengl.GL11
import java.math.BigDecimal
import kotlin.math.abs
import kotlin.random.Random


object DMGParticle {
    private val healthData = mutableMapOf<Int, Float>()
    private val particles = mutableListOf<SingleParticle>()

    @SubscribeEvent
    fun onUpdate(event: TickEvent.ClientTickEvent) {
        synchronized(particles) {
            for(entity in mc.theWorld.loadedEntityList) {
                if(entity is EntityLivingBase) {
                    val lastHealth = healthData.getOrDefault(entity.entityId,entity.maxHealth)
                    healthData[entity.entityId] = entity.health
                    if(lastHealth == entity.health) continue

                    val prefix =
                        (if (lastHealth > entity.health) {
                            "-"
                        } else {
                            "+"
                        })

                    particles.add(SingleParticle(prefix + BigDecimal(abs(lastHealth - entity.health).toDouble()).setScale(1, BigDecimal.ROUND_HALF_UP).toDouble()
                        ,entity.posX - 0.5 + Random(System.currentTimeMillis()).nextInt(5).toDouble() * 0.1
                        ,entity.entityBoundingBox.minY + (entity.entityBoundingBox.maxY - entity.entityBoundingBox.minY) / 2.0
                        ,entity.posZ - 0.5 + Random(System.currentTimeMillis() + 1L).nextInt(5).toDouble() * 0.1)
                    )
                }
            }

            val needRemove = ArrayList<SingleParticle> ()
            for (particle in particles) {
                particle.ticks++
                if (particle.ticks>Config.aliveTicks) {
                    needRemove.add(particle)
                }
            }
            for (particle in needRemove) {
                particles.remove(particle)
            }
        }
    }

    @SubscribeEvent
    fun onRender3d(event: RenderLivingEvent.Pre<*>) {
        synchronized(particles) {
            val renderManager = mc.renderManager
            val size = Config.dpSize * 0.01

            for (particle in particles) {
                val n: Double = particle.posX - RenderUtil.getRenderX()
                val n2: Double = particle.posY - RenderUtil.getRenderY()
                val n3: Double = particle.posZ - RenderUtil.getRenderZ()
                GlStateManager.pushMatrix()
                GlStateManager.enablePolygonOffset()
                GlStateManager.doPolygonOffset(1.0f, -1500000.0f)
                GlStateManager.translate(n.toFloat(), n2.toFloat(), n3.toFloat())
                GlStateManager.rotate(-renderManager.playerViewY, 0.0f, 1.0f, 0.0f)
                val textY = if (mc.gameSettings.thirdPersonView == 2) { -1.0f } else { 1.0f }

                GlStateManager.rotate(renderManager.playerViewX, textY, 0.0f, 0.0f)
                GlStateManager.scale(-size, -size, size)
                GL11.glDepthMask(false)
                mc.fontRendererObj.drawStringWithShadow(
                    particle.str,
                    (-(mc.fontRendererObj.getStringWidth(particle.str) / 2)).toFloat(),
                    (-(mc.fontRendererObj.FONT_HEIGHT - 1)).toFloat(),
                    (ColorUtils.rainbowWithAlpha(Config.dpColorAlpha)).rgb
                )
                GL11.glColor4f(187.0f, 255.0f, 255.0f, 1.0f)
                GL11.glDepthMask(true)
                GlStateManager.doPolygonOffset(1.0f, 1500000.0f)
                GlStateManager.disablePolygonOffset()
                GlStateManager.resetColor()
                GlStateManager.popMatrix()
            }
        }
    }

    @SubscribeEvent
    fun onWorld(event: WorldEvent) {
        particles.clear()
        healthData.clear()
    }
}


class SingleParticle(val str: String, val posX: Double, val posY: Double, val posZ: Double) {
    var ticks = 0
}
