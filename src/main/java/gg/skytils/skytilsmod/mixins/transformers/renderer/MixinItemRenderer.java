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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
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
import net.minecraft.item.*;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

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

    @WrapOperation(method = "renderItemInFirstPerson", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;getItemInUseCount()I"))
    private int getItemInUseCountForFirstPerson(AbstractClientPlayer abstractClientPlayer, Operation<Integer> original) {
        return ItemRendererHookKt.getItemInUseCountForFirstPerson(abstractClientPlayer, itemToRender, original);
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
                        renderingBlocked(f, f1,f2);
                        break;

                    case BOW:
                        this.transformFirstPersonItem(f, f1);
                        this.doBowTransformations(partialTicks, entityplayersp);
                }
            } else {
                if ((
                        this.mc.gameSettings.keyBindUseItem.isKeyDown())
                        && Config.INSTANCE.getEverythingBlock()
                        && (
                        (itemToRender.getItem() instanceof ItemBlock && Config.INSTANCE.getEverythingBlockBlocks())
                                || (itemToRender.getItem() instanceof ItemSkull && Config.INSTANCE.getEverythingBlockSkull())
                                || (itemToRender.getItem() instanceof ItemTool && Config.INSTANCE.getEverythingBlockTools())
                                || (((!(itemToRender.getItem() instanceof ItemSkull)) && (!(itemToRender.getItem() instanceof ItemTool))) && Config.INSTANCE.getEverythingBlockOthers())
                )
                ) {
                    renderingBlocked(f, f1,f2);
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

    private void renderingBlocked(float f, float f1,float f2) {
        if (!Config.INSTANCE.getAnimations()) {
            this.transformFirstPersonItem(equippedProgress, 0.0F);
            this.doBlockTransformations();
        } else {
            GL11.glTranslated(Config.INSTANCE.getBlockPosX(), Config.INSTANCE.getBlockPosY(), Config.INSTANCE.getBlockPosZ());
            switch (Config.INSTANCE.getAnimationsMode()) {
                case 0: {
                    transformFirstPersonItem(f1, 0.0F);
                    doBlockTransformations();
                    break;
                }
                case 1: {
                    avatar(f1);
                    doBlockTransformations();
                    break;
                }
                case 2: {
                    etb(f, f1);
                    doBlockTransformations();
                    break;
                }
                case 3: {
                    transformFirstPersonItem(f, 0.83F);
                    float f4 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.83F);
                    GlStateManager.translate(-0.5F, 0.2F, 0.2F);
                    GlStateManager.rotate(-f4 * 0.0F, 0.0F, 0.0F, 0.0F);
                    GlStateManager.rotate(-f4 * 43.0F, 58.0F, 23.0F, 45.0F);
                    doBlockTransformations();
                    break;
                }
                case 4: {
                    push(f1);
                    doBlockTransformations();
                    break;
                }
                case 5: {
                    transformFirstPersonItem(f1, f1);
                    doBlockTransformations();
                    GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
                    break;
                }
                case 6: {
                    jello(f1);
                    doBlockTransformations();
                    break;
                }
                case 7: {
                    sigmaNew(0.2F, f1);
                    doBlockTransformations();
                    break;
                }
                case 8: {
                    sigmaOld(f);
                    float var15 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                    GlStateManager.rotate(-var15 * 55.0F / 2.0F, -8.0F, -0.0F, 9.0F);
                    GlStateManager.rotate(-var15 * 45.0F, 1.0F, var15 / 2.0F, -0.0F);
                    doBlockTransformations();
                    GL11.glTranslated(1.2D, 0.3D, 0.5D);
                    GL11.glTranslatef(-1.0F, mc.thePlayer.isSneaking() ? -0.1F : -0.2F, 0.2F);
                    GlStateManager.scale(1.2F, 1.2F, 1.2F);
                    break;
                }
                case 9: {
                    slide(f1);
                    doBlockTransformations();
                    break;
                }
                case 10: {
                    transformFirstPersonItem(0.2F, f1);
                    doBlockTransformations();
                    break;
                }
                case 11: {
                    transformFirstPersonItem(f / 2.0F, 0.0F);
                    GlStateManager.rotate(-MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F) * 40.0F / 2.0F, MathHelper.sqrt_float(f1) / 2.0F, -0.0F, 9.0F);
                    GlStateManager.rotate(-MathHelper.sqrt_float(f1) * 30.0F, 1.0F, MathHelper.sqrt_float(f1) / 2.0F, -0.0F);
                    doBlockTransformations();
                    break;
                }
                case 12: {
                    continuity(f1);
                    doBlockTransformations();
                    break;
                }
                case 13:{
                    GL11.glTranslated(-0.1, 0.15, 0.0);
                    this.transformFirstPersonItem(f / 0.15f, f1);
                    final float rot = MathHelper.sin(MathHelper.sqrt_float(f2) * 3.1415927f);
                    GlStateManager.rotate(rot * 30.0f, 2.0f, -rot, 9.0f);
                    GlStateManager.rotate(rot * 35.0f, 1.0f, -rot, -0.0f);
                    this.doBlockTransformations();
                    break;
                }
                case 14:{
                    this.transformFirstPersonItem(0.0f, 0.0f);
                    this.doBlockTransformations();
                    final int alpha = (int)Math.min(255L, ((System.currentTimeMillis() % 255L > 127L) ? Math.abs(Math.abs(System.currentTimeMillis()) % 255L - 255L) : (System.currentTimeMillis() % 255L)) * 2L);
                    GlStateManager.translate(0.3f, -0.0f, 0.4f);
                    GlStateManager.rotate(0.0f, 0.0f, 0.0f, 1.0f);
                    GlStateManager.translate(0.0f, 0.5f, 0.0f);
                    GlStateManager.rotate(90.0f, 1.0f, 0.0f, -1.0f);
                    GlStateManager.translate(0.6f, 0.5f, 0.0f);
                    GlStateManager.rotate(-90.0f, 1.0f, 0.0f, -1.0f);
                    GlStateManager.rotate(-10.0f, 1.0f, 0.0f, -1.0f);
                    GlStateManager.rotate(mc.thePlayer.isSwingInProgress ? (-alpha / 5.0f) : 1.0f, 1.0f, -0.0f, 1.0f);
                    break;
                }
                case 15:{
                    transformFirstPersonItem(f1!=0?Math.max(1-(f1*2),0)*0.7F:0, 1F);
                    doBlockTransformations();
                    break;
                }
                case 16:{
                    transformFirstPersonItem(0F,0F);
                    doBlockTransformations();
                    break;
                }
                case 17:{
                    rotateSword(f1);
                    break;
                }
                case 18: {
                    this.transformFirstPersonItem(f + 0.1F, f1);
                    this.doBlockTransformations();
                    GlStateManager.translate(-0.5F, 0.2F, 0.0F);
                    break;
                }
            }
        }

    }
    private void doItemRenderGLTranslate(){
        GlStateManager.translate(Config.INSTANCE.getBlockPosX(), Config.INSTANCE.getBlockPosY(), Config.INSTANCE.getBlockPosZ());
    }

    private void doItemRenderGLScale(){
        GlStateManager.scale(Config.INSTANCE.getItemScale(), Config.INSTANCE.getItemScale(), Config.INSTANCE.getItemScale());
    }
    private void sigmaOld(float f) {
        doItemRenderGLTranslate();
        GlStateManager.translate(0.0F, f * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(0F, 0.0F, 1.0F, 0.2F);
        GlStateManager.rotate(0F, 0.2F, 0.1F, 1.0F);
        GlStateManager.rotate(0F, 1.3F, 0.1F, 0.2F);
        doItemRenderGLScale();
    }

    //methods in LiquidBounce b73 Animation-No-Cross
    private void avatar(float swingProgress) {
        doItemRenderGLTranslate();
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float f = MathHelper.sin(swingProgress * swingProgress * 3.1415927F);
        float f2 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927F);
        GlStateManager.rotate(f * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f2 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f2 * -40.0F, 1.0F, 0.0F, 0.0F);
        doItemRenderGLScale();
    }

    private void slide(float var9) {
        doItemRenderGLTranslate();
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var11 = MathHelper.sin(var9 * var9 * 3.1415927F);
        float var12 = MathHelper.sin(MathHelper.sqrt_float(var9) * 3.1415927F);
        GlStateManager.rotate(var11 * 0.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var12 * 0.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(var12 * -40.0F, 1.0F, 0.0F, 0.0F);
        doItemRenderGLScale();
    }

    private void rotateSword(float f1){
        genCustom(0.0F, 0.0F);
        doBlockTransformations();
        GlStateManager.translate(-0.5F, 0.2F, 0.0F);
        GlStateManager.rotate(MathHelper.sqrt_float(f1) * 10.0F * 40.0F, 1.0F, -0.0F, 2.0F);
    }

    private void genCustom(float p_178096_1_, float p_178096_2_) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, p_178096_1_ * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var3 = MathHelper.sin(p_178096_2_ * p_178096_2_ * 3.1415927F);
        float var4 = MathHelper.sin(MathHelper.sqrt_float(p_178096_2_) * 3.1415927F);
        GlStateManager.rotate(var3 * -34.0F, 0.0F, 1.0F, 0.2F);
        GlStateManager.rotate(var4 * -20.7F, 0.2F, 0.1F, 1.0F);
        GlStateManager.rotate(var4 * -68.6F, 1.3F, 0.1F, 0.2F);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
    }


    private void jello(float var12) {
        doItemRenderGLTranslate();
        GlStateManager.rotate(48.57F, 0.0F, 0.24F, 0.14F);
        float var13 = MathHelper.sin(var12 * var12 * 3.1415927F);
        float var14 = MathHelper.sin(MathHelper.sqrt_float(var12) * 3.1415927F);
        GlStateManager.rotate(var13 * -35.0F, 0.0F, 0.0F, 0.0F);
        GlStateManager.rotate(var14 * 0.0F, 0.0F, 0.0F, 0.0F);
        GlStateManager.rotate(var14 * 20.0F, 1.0F, 1.0F, 1.0F);
        doItemRenderGLScale();
    }

    private void continuity(float var10) {
        doItemRenderGLTranslate();
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var12 = -MathHelper.sin(var10 * var10 * 3.1415927F);
        float var13 = MathHelper.cos(MathHelper.sqrt_float(var10) * 3.1415927F);
        float var14 = MathHelper.abs(MathHelper.sqrt_float((float) 0.1) * 3.1415927F);
        GlStateManager.rotate(var12 * var14 * 30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var13 * 0.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(var13 * 20.0F, 1.0F, 0.0F, 0.0F);
        doItemRenderGLScale();
    }

    public void sigmaNew(float var22, float var23) {
        doItemRenderGLTranslate();
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var24 = MathHelper.sin(var23 * MathHelper.sqrt_float(var22) * 3.1415927F);
        float var25 = MathHelper.abs(MathHelper.sqrt_double(var22) * 3.1415927F);
        GlStateManager.rotate(var24 * 20.0F * var25, 0.0F, 1.0F, 1.0F);
        doItemRenderGLScale();
    }

    private void etb(float equipProgress, float swingProgress) {
        doItemRenderGLTranslate();
        GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var3 = MathHelper.sin(swingProgress * swingProgress * 3.1415927F);
        float var4 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927F);
        GlStateManager.rotate(var3 * -34.0F, 0.0F, 1.0F, 0.2F);
        GlStateManager.rotate(var4 * -20.7F, 0.2F, 0.1F, 1.0F);
        GlStateManager.rotate(var4 * -68.6F, 1.3F, 0.1F, 0.2F);
        doItemRenderGLScale();
    }

    private void push(float idc) {
        doItemRenderGLTranslate();
        GlStateManager.translate(0.0F, (float) 0.1 * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var3 = MathHelper.sin(idc * idc * 3.1415927F);
        float var4 = MathHelper.sin(MathHelper.sqrt_float(idc) * 3.1415927F);
        GlStateManager.rotate(var3 * -10.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.rotate(var4 * -10.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.rotate(var4 * -10.0F, 1.0F, 1.0F, 1.0F);
        doItemRenderGLScale();
    }
}
