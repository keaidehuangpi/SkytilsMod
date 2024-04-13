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
import gg.skytils.skytilsmod.utils.CPSDelay
import gg.skytils.skytilsmod.utils.Misc
import gg.skytils.skytilsmod.utils.ReflectionUtil
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Mouse
import java.util.concurrent.ThreadLocalRandom


object Autoclicker {
    private val cpsDelay = CPSDelay()

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (!Config.autoClicker) {
            return
        }
        if (Config.minCPS > Config.maxCPS) {
            Config.maxCPS = Config.minCPS
        }
        if (mc.currentScreen == null && Mouse.isButtonDown(0)) {


            if (cpsDelay.shouldAttack(
                    (if (Config.minCPS == Config.maxCPS) Config.maxCPS
                    else ThreadLocalRandom.current()
                        .nextInt(Config.minCPS, Config.maxCPS)).toDouble()
                )
            ) {
                ReflectionUtil.setFieldValue(Minecraft.getMinecraft(), 0, "leftClickCounter", "field_71429_W")
                Misc.clickMouse()

            }
        }
    }

}