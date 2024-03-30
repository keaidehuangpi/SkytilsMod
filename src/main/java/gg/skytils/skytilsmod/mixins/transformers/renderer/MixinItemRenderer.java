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

import gg.skytils.skytilsmod.core.Config;
import gg.skytils.skytilsmod.mixins.hooks.renderer.ItemRendererHookKt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {
    @Shadow
    private ItemStack itemToRender;
    @Final
    @Shadow
    private Minecraft mc;
    @Shadow
    private float equippedProgress;
    @Shadow
    private float prevEquippedProgress;
    @Final
    @Shadow
    private RenderManager renderManager;

    @Shadow
    protected abstract void doBlockTransformations();

    @Shadow
    protected abstract void renderPlayerArm(AbstractClientPlayer clientPlayer, float p_178095_2_, float p_178095_3_);

    @Shadow
    public abstract void renderItem(EntityLivingBase entityIn, ItemStack heldStack,
                                    ItemCameraTransforms.TransformType transform);

    @Shadow
    protected abstract void rotateArroundXAndY(float f2, float f3);

    @Shadow
    protected abstract void renderItemMap(AbstractClientPlayer clientPlayer, float p_178097_2_, float p_178097_3_,
                                          float p_178097_4_);
    @Shadow
    protected abstract void setLightMapFromPlayer(AbstractClientPlayer var1);

    @Shadow
    protected abstract void doItemUsedTransformations(float f1);

    @Shadow
    protected abstract void doBowTransformations(float p_178098_1_, AbstractClientPlayer clientPlayer);

    @Shadow
    protected abstract void rotateWithPlayerRotations(EntityPlayerSP entityplayersp, float partialTicks);

    @Shadow
    protected abstract void performDrinking(AbstractClientPlayer clientPlayer, float p_178104_2_);

    @Redirect(method = "renderItemInFirstPerson", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;getItemInUseCount()I"))
    private int getItemInUseCountForFirstPerson(AbstractClientPlayer abstractClientPlayer) {
        return ItemRendererHookKt.getItemInUseCountForFirstPerson(abstractClientPlayer, itemToRender);
    }
    /**
     * @author hanabi
     * @reason itemScale
     */
    @Overwrite
    private void transformFirstPersonItem(float equipProgress, float swingProgress)
    {

        GL11.glTranslatef(0.56f, -0.52f, -0.72f);
        GL11.glTranslatef(0.0f, equipProgress * -0.6f, 0.0f);
        GL11.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
        if (swingProgress > 0.0) {
            final float f = MathHelper.sin((float) (swingProgress * swingProgress * Math.PI));
            final float f2 = MathHelper.sin((float) (MathHelper.sqrt_float(swingProgress) * Math.PI));
            GL11.glRotatef(f * -20.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(f2 * -20.0f, 0.0f, 0.0f, 1.0f);
            GL11.glRotatef(f2 * -80.0f, 1.0f, 0.0f, 0.0f);
        }
        float scale = 0.4f;
        if (Config.INSTANCE.getAnimations()) {
            scale *= (float) Config.INSTANCE.getItemScale();
        }
        GL11.glScalef(scale, scale, scale);

    }
    private void renderingBlockeddd(float swingProgress, float equippedProgress) {
            this.transformFirstPersonItem(swingProgress, 0.0F);
            this.doBlockTransformations();

    }


    /**
     * @author
     * @reason
     */
    @Overwrite
    public void renderItemInFirstPerson(float partialTicks) {
        float f = 1.0F
                - (this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * partialTicks);
        EntityPlayerSP entityplayersp = this.mc.thePlayer;
        float f1 = entityplayersp.getSwingProgress(partialTicks);
        float f2 = entityplayersp.prevRotationPitch
                + (entityplayersp.rotationPitch - entityplayersp.prevRotationPitch) * partialTicks;
        float f3 = entityplayersp.prevRotationYaw
                + (entityplayersp.rotationYaw - entityplayersp.prevRotationYaw) * partialTicks;
        float var2 = 1.0F
                - (this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * partialTicks);
        EntityPlayerSP var3 = this.mc.thePlayer;
        float var4 = var3.getSwingProgress(partialTicks);

        this.rotateArroundXAndY(f2, f3);
        this.setLightMapFromPlayer(entityplayersp);
        this.rotateWithPlayerRotations(entityplayersp, partialTicks);
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();

        if (this.itemToRender != null) {
            if (this.itemToRender.getItem() == Items.filled_map) {
                this.renderItemMap(entityplayersp, f2, f, f1);
            } else if (entityplayersp.getItemInUseCount() > 0) {
                EnumAction enumaction = this.itemToRender.getItemUseAction();

                switch (enumaction) {
                    case NONE:
                        this.transformFirstPersonItem(f, 0.0F);
                        break;

                    case EAT:
                    case DRINK:
                        this.performDrinking(entityplayersp, partialTicks);
                        this.transformFirstPersonItem(f, f1);
                        break;

                    case BLOCK:
                        renderingBlockeddd(f, f1);
                        break;

                    case BOW:
                        this.transformFirstPersonItem(f, f1);
                        this.doBowTransformations(partialTicks, entityplayersp);
                }
            } else {
                if ((
                        this.mc.gameSettings.keyBindUseItem.isKeyDown())
                        && Config.INSTANCE.getEverythingBlock()) {
                    renderingBlockeddd(f, f1);
                } else {
                    this.doItemUsedTransformations(f1);
                    this.transformFirstPersonItem(f, f1);
                }

            }

            this.renderItem(entityplayersp, this.itemToRender, ItemCameraTransforms.TransformType.FIRST_PERSON);
        } else if (!entityplayersp.isInvisible()) {
            this.renderPlayerArm(entityplayersp, f, f1);
        }

        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
    }
}
