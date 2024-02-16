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

package gg.skytils.skytilsmod.mixins.transformers.network;

import gg.skytils.skytilsmod.Skytils;
import gg.skytils.skytilsmod.mixins.hooks.network.NetworkManagerHookKt;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static gg.skytils.skytilsmod.utils.cheats.TabList.isInTabList;

@Mixin(value = NetworkManager.class, priority = 1001)
public abstract class MixinNetworkManager extends SimpleChannelInboundHandler<Packet<?>> {
    @Shadow @Final private EnumPacketDirection direction;

    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    private void onReceivePacket(ChannelHandlerContext context, Packet<?> packet, CallbackInfo ci) {


        if (this.direction == EnumPacketDirection.CLIENTBOUND)
            NetworkManagerHookKt.onReceivePacket(context, packet, ci);

        //antibot
        if (Skytils.Companion.getConfig().getAntibot()) {
            if (packet instanceof S18PacketEntityTeleport) {
                S18PacketEntityTeleport p = (S18PacketEntityTeleport) packet;
                if (Minecraft.getMinecraft().theWorld.getEntityByID(p.getEntityId()) instanceof EntityPlayer) {
                    EntityPlayer entity = (EntityPlayer) Minecraft.getMinecraft().theWorld.getEntityByID(p.getEntityId());
                    if (entity instanceof EntityPlayer && entity.isInvisible() && entity.ticksExisted > 3 && Minecraft.getMinecraft().theWorld.playerEntities.contains(entity) && !isInTabList(entity)) {
                        Minecraft.getMinecraft().theWorld.removeEntity(entity);
                    }
                }
            }

            if (packet instanceof S0CPacketSpawnPlayer) {
                S0CPacketSpawnPlayer p = (S0CPacketSpawnPlayer) packet;
                if (Minecraft.getMinecraft().theWorld.getEntityByID(p.getEntityID()) instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) Minecraft.getMinecraft().theWorld.getEntityByID(p.getEntityID());

                    double posX = p.getX() / 32.0D, posY = p.getY() / 32.0D, posZ = p.getZ() / 32.0D;
                    double difX = Minecraft.getMinecraft().thePlayer.posX - posX, difY = Minecraft.getMinecraft().thePlayer.posY - posY, difZ = Minecraft.getMinecraft().thePlayer.posZ - posZ;
                    double dist = Math.sqrt(difX * difX + difY * difY + difZ * difZ);

                    if (Minecraft.getMinecraft().theWorld.playerEntities.contains(player) && dist <= 17.0D && !player.equals(Minecraft.getMinecraft().thePlayer)
                            && posX != Minecraft.getMinecraft().thePlayer.posX && posY != Minecraft.getMinecraft().thePlayer.posY && posZ != Minecraft.getMinecraft().thePlayer.posZ) {
                        Minecraft.getMinecraft().theWorld.removeEntity(player);
                    }
                }
            }
        }
    }


}
