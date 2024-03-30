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
                        renderingBlocked(f, f1);
                        break;

                    case BOW:
                        this.transformFirstPersonItem(f, f1);
                        this.doBowTransformations(partialTicks, entityplayersp);
                }
            } else {
                if ((
                        this.mc.gameSettings.keyBindUseItem.isKeyDown())
                        && Config.INSTANCE.getEverythingBlock()) {
                    renderingBlocked(f, f1);
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

    private void renderingBlocked(float swingProgress, float equippedProgress) {
        final float hand = MathHelper.sin(MathHelper.sqrt_float(equippedProgress) * (float)Math.PI);


        if (!Config.INSTANCE.getAnimations()) {
            this.transformFirstPersonItem(swingProgress, 0.0F);
            this.doBlockTransformations();
        } else {
            GL11.glTranslated(Config.INSTANCE.getBlockPosX(), Config.INSTANCE.getBlockPosY(), Config.INSTANCE.getBlockPosZ());
            if (Config.INSTANCE.getAnimationsMode()==(6)) {
                this.transformFirstPersonItem(equippedProgress, 0.0f);
                float swong = MathHelper.sin((float) (MathHelper.sqrt_float(swingProgress) * Math.PI));
                GlStateManager.rotate(-swong * 55 / 2.0F, -8.0F, -0.0F, 9.0F);
                GlStateManager.rotate(-swong * 45, 1.0F, swong/2, -0.0F);
                this.doBlockTransformations();
                GL11.glTranslated(1.2, 0.3,0.5);
                GL11.glTranslatef(-1, this.mc.thePlayer.isSneaking() ? -0.1F : -0.2F, 0.2F);
            } else if (Config.INSTANCE.getAnimationsMode()==23) {
                this.transformFirstPersonItem(0.2f, equippedProgress);
                this.doBlockTransformations();
                GlStateManager.translate(-0.5, 0.2, 0.0);
            } else if (Config.INSTANCE.getAnimationsMode()==0) {
                this.transformFirstPersonItem(swingProgress, 0.0F);
                this.doBlockTransformations();
            } else if (Config.INSTANCE.getAnimationsMode()==24) {
                this.transformFirstPersonItem(swingProgress, 0.0F);
                this.doBlockTransformations();
                final float sin2 = MathHelper.sin((float) (MathHelper.sqrt_float(equippedProgress) * Math.PI));
                GlStateManager.scale(1.0f, 1.0f, 1.0f);
                GlStateManager.translate(-0.2f, 0.45f, 0.25f);
                GlStateManager.rotate(-sin2 * 20.0f, -5.0f, -5.0f, 9.0f);
            } else if (Config.INSTANCE.getAnimationsMode()==1) {
                this.transformFirstPersonItem(swingProgress - 0.3F, equippedProgress);
                this.doBlockTransformations();
            } else if (Config.INSTANCE.getAnimationsMode()==2) {
                this.transformFirstPersonItem(swingProgress / 2.0F, equippedProgress);
                float var15;
                var15 = MathHelper.sin((float) (MathHelper.sqrt_float(equippedProgress) * Math.PI));
                GlStateManager.rotate(var15 * 30.0F / 2.0F, -var15, -0.0F, 9.0F);
                GlStateManager.rotate(var15 * 40.0F, 1.0F, -var15 / 2.0F, -0.0F);

                this.doBlockTransformations();
            } else if (Config.INSTANCE.getAnimationsMode()==3) {
                this.transformFirstPersonItem(swingProgress / 2.0F, equippedProgress);
                float var15;
                var15 = MathHelper.sin((float) (MathHelper.sqrt_float(swingProgress) * Math.PI));
                GlStateManager.rotate(var15 * 30.0F, -var15, -0.0F, 9.0F);
                GlStateManager.rotate(var15 * 40.0F, 1.0F, -var15, -0.0F);

                this.doBlockTransformations();
            } else if (Config.INSTANCE.getAnimationsMode()==4) {
                this.transformFirstPersonItem(swingProgress / 2.0F, 0.0F);
                float var151 = MathHelper.sin((float) (MathHelper.sqrt_float(equippedProgress) * Math.PI));
                GlStateManager.rotate(-var151 * 40.0F / 2.0F, var151 / 2.0F, -0.0F, 9.0F);
                GlStateManager.rotate(-var151 * 30.0F, 1.0F, var151 / 2.0F, -0.0F);

                this.doBlockTransformations();
            } else if (Config.INSTANCE.getAnimationsMode()==25) {
                this.transformFirstPersonItem(0.1f, equippedProgress);
                this.doBlockTransformations();
                GlStateManager.translate(-0.5, 0, 0);
            } else if (Config.INSTANCE.getAnimationsMode()==26) {
                this.transformFirstPersonItem(0.1f, equippedProgress);
                this.doBlockTransformations();
                float var15 = MathHelper.sin((float) (MathHelper.sqrt_float(equippedProgress) * Math.PI));
                GlStateManager.translate(-0.0f, -0.3f, 0.4f);
                GlStateManager.rotate((-var15) * 22.5f, -9.0f, -0.0f, 9.0f);
                GlStateManager.rotate((-var15) * 10.0f, 1.0f, -0.4f, -0.5f);
            } else if (Config.INSTANCE.getAnimationsMode()==6) {
                GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
                GlStateManager.translate(0.0F, 0 * -0.6F, 0.0F);
                GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
                float var3 = MathHelper.sin((float) (0.0F * 0.0F * Math.PI));
                float var4 = MathHelper.sin((float) (MathHelper.sqrt_float(0.0F) * Math.PI));
                GlStateManager.rotate(var3 * -20.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(var4 * -20.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(var4 * -80.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.scale(0.4F, 0.4F, 0.4F);

                GlStateManager.translate(-0.5F, 0.2F, 0.0F);
                GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
                int alpha = (int) Math.min(255,
                        ((System.currentTimeMillis() % 255) > 255 / 2
                                ? (Math.abs(Math.abs(System.currentTimeMillis()) % 255 - 255))
                                : System.currentTimeMillis() % 255) * 2);
                GlStateManager.translate(0.3f, -0.0f, 0.40f);
                GlStateManager.rotate(0.0f, 0.0f, 0.0f, 1.0f);
                GlStateManager.translate(0, 0.5f, 0);

                GlStateManager.rotate(90, 1.0f, 0.0f, -1.0f);
                GlStateManager.translate(0.6f, 0.5f, 0);
                GlStateManager.rotate(-90, 1.0f, 0.0f, -1.0f);

                GlStateManager.rotate(-10, 1.0f, 0.0f, -1.0f);
                GlStateManager.rotate(mc.thePlayer.isSwingInProgress ? -alpha / 5f : 1, 1.0f, -0.0f, 1.0f);
            } else if (Config.INSTANCE.getAnimationsMode()==27) {
                this.transformFirstPersonItem(swingProgress / 2.0f - 0.18f, 0.0f);
                GL11.glRotatef(hand * 60.0f / 2.0f, -hand / 2.0f, -0.0f, -16.0f);
                GL11.glRotatef(-hand * 30.0f, 1.0f, hand / 2.0f, -1.0f);
                this.doBlockTransformations();
            } else if (Config.INSTANCE.getAnimationsMode()==15) {
                this.transformFirstPersonItem(swingProgress / 2.0f - 0.18f, 0.0f);
                GL11.glRotatef(-hand * 40.0f / 2.0f, hand / 2.0f, -0.0f, 9.0f);
                GL11.glRotatef(-hand * 30.0f, 1.0f, hand / 2.0f, -0.0f);
                this.doBlockTransformations();
            } else if (Config.INSTANCE.getAnimationsMode()==14) {
                this.transformFirstPersonItem(swingProgress, 0.0f);
                this.doBlockTransformations();
                final float var19 = MathHelper.sin((float) (MathHelper.sqrt_float(equippedProgress) * Math.PI));
                GlStateManager.translate(-0.05f, 0.6f, 0.3f);
                GlStateManager.rotate(-var19 * 70.0f / 2.0f, -8.0f, -0.0f, 9.0f);
                GlStateManager.rotate(-var19 * 70.0f, 1.5f, -0.4f, -0.0f);
            } else if (Config.INSTANCE.getAnimationsMode()==12) {
                this.transformFirstPersonItem(swingProgress * 0.5f, 0.0f);
                GlStateManager.rotate(-hand * -74.0f / 4.0f, -8.0f, -0.0f, 9.0f);
                GlStateManager.rotate(-hand * 15.0f, 1.0f, hand / 2.0f, -0.0f);
                this.doBlockTransformations();
                GL11.glTranslated(1.2, 0.3, 0.5);
                GL11.glTranslatef(-1.0f, this.mc.thePlayer.isSneaking() ? -0.1f : -0.2f, 0.2f);
            } else if (Config.INSTANCE.getAnimationsMode()==7) {
                this.transformFirstPersonItem(0, 0.0f);
                this.doBlockTransformations();
                float var9 = MathHelper.sin((float) (MathHelper.sqrt_float(equippedProgress) * Math.PI));
                GlStateManager.translate(-0.05f, -0.0f, 0.35f);
                GlStateManager.rotate(-var9 * (float) 60.0 / 2.0f, -15.0f, -0.0f, 9.0f);
                GlStateManager.rotate(-var9 * (float) 70.0, 1.0f, -0.4f, -0.0f);
            } else if (Config.INSTANCE.getAnimationsMode()==13) {
                this.transformFirstPersonItem(0, 0.0f);
                this.doBlockTransformations();
                float var9 = MathHelper.sin(MathHelper.sqrt_float(equippedProgress) * 0.3215927f);
                GlStateManager.translate(-0.05f, -0.0f, 0.3f);
                GlStateManager.rotate(-var9 * (float) 60.0 / 2.0f, -15.0f, -0.0f, 9.0f);
                GlStateManager.rotate(-var9 * (float) 70.0, 1.0f, -0.4f, -0.0f);
            } else if (Config.INSTANCE.getAnimationsMode()==8) {
                float f6 = MathHelper.sin((float) (MathHelper.sqrt_float(equippedProgress) * Math.PI));
                GL11.glTranslated(-0.05D, 0.0D, -0.25);
                this.transformFirstPersonItem(swingProgress / 2, 0.0f);
                GlStateManager.rotate(-f6 * 60.0F, 2.0F, -f6 * 2, -0.0f);
                this.doBlockTransformations();
            } else if (Config.INSTANCE.getAnimationsMode()==9) {
                float f6 = MathHelper.sin((float) (MathHelper.sqrt_float(equippedProgress) * 3.1));
                this.transformFirstPersonItem(swingProgress / 3, 0.0f);
                GlStateManager.rotate(f6 * 30.0F / 1.0F, f6 / -1.0F, 1.0F, 0.0F);
                GlStateManager.rotate(f6 * 10.0F / 10.0F, -f6 / -1.0F, 1.0F, 0.0F);
                GL11.glTranslated(0.0D, 0.4D, 0.0D);
                this.doBlockTransformations();
            } else if (Config.INSTANCE.getAnimationsMode()==10) {
                float f6 = MathHelper.sin((float) (MathHelper.sqrt_float(equippedProgress) * 3.1));
                GL11.glTranslated(0.0D, 0.125D, -0.1D);
                this.transformFirstPersonItem(swingProgress / 3, 0.0F);
                GlStateManager.rotate(-f6 * 75.0F / 4.5F, f6 / 3.0F, -2.4F, 5.0F);
                GlStateManager.rotate(-f6 * 75.0F, 1.5F, f6 / 3.0F, -0.0F);
                GlStateManager.rotate(f6 * 72.5F / 2.25F, f6 / 3.0F, -2.7F, 5.0F);
                this.doBlockTransformations();
            } else if (Config.INSTANCE.getAnimationsMode()==11) {
                this.transformFirstPersonItem(swingProgress, 0);
                this.doBlockTransformations();
                GlStateManager.rotate(-MathHelper.sin((float) (MathHelper.sqrt_float(equippedProgress) * Math.PI)) * 30.0F, 0.5F, 0.5F, 0);
            } else if (Config.INSTANCE.getAnimationsMode()==28) {
                this.avatar(swingProgress, equippedProgress);
                this.doBlockTransformations();
            } else if (Config.INSTANCE.getAnimationsMode()==29) {
                this.transformFirstPersonItem(swingProgress, 0.0F);
                this.doBlockTransformations();
                GlStateManager.rotate(-MathHelper.sin((float) (MathHelper.sqrt_float(equippedProgress) * Math.PI)) * 35.0F, -8.0F, -0.0F, 9.0F);
                GlStateManager.rotate(-MathHelper.sin((float) (MathHelper.sqrt_float(equippedProgress) * Math.PI)) * 10.0F, 1.0F, -0.4F, -0.5F);
            }
            else if (Config.INSTANCE.getAnimationsMode()==16) {
                this.transformFirstPersonItem(swingProgress * 0.5f, 0.0f);
                GlStateManager.rotate(-hand * 10.0f, 0.0f, 15.0f, 300.0f);
                GlStateManager.rotate(-hand * 10.0f, 300.0f, hand / 2.0f, 1.0f);
                this.doBlockTransformations();
                GL11.glTranslated(1.2, 0.2, 0.1);
                GL11.glTranslatef(-2.1f, -0.2f, 0.1f);
            }
            else if (Config.INSTANCE.getAnimationsMode()==17) {
                this.transformFirstPersonItem(swingProgress, equippedProgress);
                this.doBlockTransformations();
                GL11.glTranslatef(0.1f, -0.1f, 0.3f);
                GlStateManager.translate(0.1f, -0.1f, 0.4f);
            }
            else if (Config.INSTANCE.getAnimationsMode()==18) {
                this.transformFirstPersonItem(swingProgress, equippedProgress / 40.0f);
                this.doBlockTransformations();
            }
            else if (Config.INSTANCE.getAnimationsMode()==19) {
                this.transformFirstPersonItem(swingProgress, 1.0f);
                this.doBlockTransformations();
                GL11.glTranslatef(0.6f, 0.3f, 0.7f);
                final float slide = MathHelper.sin(equippedProgress * equippedProgress * 5.1415925f);
                GlStateManager.translate(-0.52f, -0.1f, -0.2f);
                GlStateManager.rotate(slide * -19.0f, 25.0f, -0.4f, -5.0f);
            }
            else if (Config.INSTANCE.getAnimationsMode()==20) {
                this.transformFirstPersonItem(swingProgress * 0.5f, 0.0f);
                GlStateManager.rotate(-hand * -74.0f / 4.0f, -8.0f, -0.0f, 9.0f);
                GlStateManager.rotate(-hand * 15.0f, 1.0f, hand / 2.0f, -0.0f);
                this.doBlockTransformations();
                GL11.glTranslated(1.2, 0.3, 0.5);
                GL11.glTranslatef(-1.0f, this.mc.thePlayer.isSneaking() ? -0.1f : -0.2f, 0.2f);
            }
            else if (Config.INSTANCE.getAnimationsMode()==21) {
                this.transformFirstPersonItem(swingProgress, equippedProgress);
                this.doBlockTransformations();
                GlStateManager.translate(0.0f, 0.0f, 0.0f);
                GlStateManager.rotate(5.0f, 50.0f, 100.0f, 50.0f);
            }
            else if (Config.INSTANCE.getAnimationsMode()==22) {
                this.transformFirstPersonItem(swingProgress, 0.0f);
                this.doBlockTransformations();
                GlStateManager.translate(-0.0f, 0.4f, 0.1f);
                GlStateManager.rotate(-hand * 35.0f, -8.0f, -0.0f, 9.0f);
                GlStateManager.rotate(-hand * 10.0f, 1.0f, -0.4f, -0.5f);
            }
        }

    }
    private void avatar(float equipProgress, float swingProgress) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, 0, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float f = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
        float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
        GlStateManager.rotate(f * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f1 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f1 * -40.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
    }

}
