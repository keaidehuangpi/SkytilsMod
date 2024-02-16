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

package gg.skytils.skytilsmod.utils.cheats;

import com.google.common.collect.Ordering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

public class TabList {
    public static boolean isInTabList(EntityPlayer player) {
        return getList().contains(Minecraft.getMinecraft().getNetHandler().getPlayerInfo(player.getGameProfile().getId()));
    }

    public static List<NetworkPlayerInfo> getList() {
        return ORDERING.sortedCopy(Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap());
    }

    private static final Ordering<NetworkPlayerInfo> ORDERING = Ordering.from(new PlayerComparator());
}

