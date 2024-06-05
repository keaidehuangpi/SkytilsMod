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
import gg.skytils.skytilsmod.Skytils.Companion.mc
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook
import net.minecraft.util.MathHelper
import net.minecraftforge.client.event.RenderLivingEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.sqrt


object AimAssist {

    @SubscribeEvent
    fun onRenderWorld(event: RenderLivingEvent.Post<*>) {
        if (Skytils.config.carnivalAimAssist) {
            val h: Entity? = this.getTarget()
            if (/*!AimAssist.locky.getValue()*/true) {
                if (h != null && (getRot(h) > 1.0 || getRot(h) < -1.0)) {
                    val i: Boolean = getRot(h) > 0.0
                    val thePlayer: EntityPlayerSP = mc.thePlayer
                    thePlayer.rotationYaw += (if (i) (-(Math.abs(getRot(h)) / (101.0 - 80))) else (Math.abs(
                        getRot(h)
                    ) / (101.0 - 80))).toFloat()
                }
            } else {
                getFoc(h, false)
            }
        }
    }

    fun getRot(en: Entity): Double {
        return ((mc.thePlayer.rotationYaw - getRotion(en)) % 360.0 + 540.0) % 360.0 - 180.0
    }

    fun getFoc(s: Entity?, packet: Boolean) {
        if (s != null) {
            val t = getArray(s)
            if (t != null) {
                val y = t[0]
                val p = t[1] + 4.0f
                if (!packet) {
                    mc.thePlayer.rotationYaw = y
                    mc.thePlayer.rotationPitch = p
                } else {
                    mc.netHandler.addToSendQueue(C05PacketPlayerLook(y, p, mc.thePlayer.onGround))
                }
            }
        }
    }

    fun getArray(q: Entity?): FloatArray? {
        if (q == null) {
            return null
        }
        val diffX: Double = q.posX - mc.thePlayer.posX
        val diffY: Double
        if (q is EntityLivingBase) {
            val EntityLivingBase = q
            diffY =
                EntityLivingBase.posY + EntityLivingBase.eyeHeight * 0.9 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight())
        } else {
            diffY =
                (q.getEntityBoundingBox().minY + q.getEntityBoundingBox().maxY) / 2.0 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight())
        }
        val diffZ: Double = q.posZ - mc.thePlayer.posZ
        val dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ).toDouble()
        val yaw = (atan2(diffZ, diffX) * 180.0 / 3.141592653589793).toFloat() - 90.0f
        val pitch = (-(atan2(diffY, dist) * 180.0 / 3.141592653589793)).toFloat()
        return floatArrayOf(
            mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw),
            mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch)
        )
    }


    fun getRotion(ent: Entity): Float {
        val x: Double = ent.posX - mc.thePlayer.posX
        val y: Double = ent.posY - mc.thePlayer.posY
        val z: Double = ent.posZ - mc.thePlayer.posZ
        var yaw = atan2(x, z) * 57.2957795
        yaw = -yaw
        var pitch = asin(y / sqrt(x * x + y * y + z * z)) * 57.2957795
        pitch = -pitch
        return yaw.toFloat()
    }

    var curTarget: Entity? = null

    fun getTarget(): Entity? {
        var k: Entity? = null
        val f: Int = 180 /*fov*/
        var distance = 8964.0
        if (curTarget != null && curTarget!!.isEntityAlive) {
            return curTarget
        }
        for (ent in mc.theWorld.loadedEntityList) {
            if (ent.isEntityAlive && ent !== mc.thePlayer && mc.thePlayer.getDistanceToEntity(ent) <= 50 && ent is EntityLivingBase) {

                if (/*!locky && */!isTarget(ent, f.toFloat())) {
                    continue
                }
                if (ent.isInvisible()) {
                    continue
                }
                if (ent is EntityZombie) {
                    if (mc.thePlayer.getDistanceToEntity(ent) < distance) {
                        k = ent
                        distance = mc.thePlayer.getDistanceToEntity(ent).toDouble()
                    }
                }
            }
        }
        curTarget = k
        return k
    }

    fun isTarget(entity: Entity, b: Float): Boolean {
        var b = b
        b *= 0.5.toFloat()
        val v: Double = ((mc.thePlayer.rotationYaw - getRotion(entity)) % 360.0 + 540.0) % 360.0 - 180.0
        return (v > 0.0 && v < b) || (-b < v && v < 0.0)
    }
}