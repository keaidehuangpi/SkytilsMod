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

package gg.skytils.skytilsmod.mixins.transformers.renderer;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import gg.skytils.skytilsmod.Skytils;
import gg.skytils.skytilsmod.mixins.hooks.renderer.EntityRendererHookKt;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.FloatBuffer;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer implements IResourceManagerReloadListener {
    @Inject(method = "hurtCameraEffect", at = @At("HEAD"), cancellable = true)
    private void onHurtcam(float partialTicks, CallbackInfo ci) {
        EntityRendererHookKt.onHurtcam(partialTicks, ci);
    }

    @ModifyExpressionValue(method = "updateLightmap", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getLastLightningBolt()I"))
    private int getLastLightningBolt(int orig) {
        return EntityRendererHookKt.getLastLightningBolt(orig);
    }

    @Shadow
    private Minecraft mc;
    @Shadow
    private float fogColorRed;
    @Shadow
    private float fogColorGreen;
    @Shadow
    private float fogColorBlue;
    @Shadow
    private float fogColor2;
    @Shadow
    private float fogColor1;
    @Shadow
    private boolean cloudFog;
    @Shadow
    private float bossColorModifier;
    @Shadow
    private float bossColorModifierPrev;

    @Shadow
    protected abstract float getNightVisionBrightness(EntityLivingBase entitylivingbaseIn, float partialTicks);

    @Shadow
    private float farPlaneDistance;

    @Shadow
    protected abstract FloatBuffer setFogColorBuffer(float red, float green, float blue, float alpha);

    /**
     * @author huiwow
     * @reason to disablefogs.
     */
    @Overwrite
    private void updateFogColor(float partialTicks) {

    }

    /**
     * @author huiwow
     * @reason to disable fogs.
     */
    @Overwrite
    private void setupFog(int startCoords, float partialTicks) {

    }

}
