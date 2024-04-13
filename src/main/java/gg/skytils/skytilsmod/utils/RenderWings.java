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

package gg.skytils.skytilsmod.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderWings extends ModelBase {
    Minecraft mc = Minecraft.getMinecraft();
    private ResourceLocation location;
    private ModelRenderer wing;
    private ModelRenderer wingTip;
    private boolean playerUsesFullHeight;

    public RenderWings() {
        this.location = new ResourceLocation("skytils", "wings.png");
        this.playerUsesFullHeight = true;
        this.setTextureOffset("wing.bone", 0, 0);
        this.setTextureOffset("wing.skin", -10, 8);
        this.setTextureOffset("wingtip.bone", 0, 5);
        this.setTextureOffset("wingtip.skin", -10, 18);
        this.wing = new ModelRenderer(this, "wing");
        this.wing.setTextureSize(30, 30);
        this.wing.setRotationPoint(-2.0F, 0.0F, 0.0F);
        this.wing.addBox("bone", -10.0F, -1.0F, -1.0F, 10, 2, 2);
        this.wing.addBox("skin", -10.0F, 0.0F, 0.5F, 10, 0, 10);
        this.wingTip = new ModelRenderer(this, "wingtip");
        this.wingTip.setTextureSize(30, 30);
        this.wingTip.setRotationPoint(-10.0F, 0.0F, 0.0F);
        this.wingTip.addBox("bone", -10.0F, -0.5F, -0.5F, 10, 1, 1);
        this.wingTip.addBox("skin", -10.0F, 0.0F, 0.5F, 10, 0, 10);
        this.wing.addChild(this.wingTip);
    }

    public void renderWings(float partialTicks) {
        boolean per = mc.gameSettings.thirdPersonView == 0;
        double scale = 100 / 100.0D;
        double rotate = this.interpolate(mc.thePlayer.prevRenderYawOffset, mc.thePlayer.renderYawOffset, partialTicks);
        GL11.glPushMatrix();
        GL11.glScaled(-scale, -scale, scale);
        GL11.glRotated(180.0D + rotate, 0.0D, 1.0D, 0.0D);
        GL11.glTranslated((double) 0.0, (double) ((-(this.playerUsesFullHeight ? 1.45 : 1.25)) / scale), (double) 0.0);
        GL11.glTranslated(0.0D, 0.0D, 0.2D / scale);
        if (mc.thePlayer.isSneaking()) {
            GL11.glTranslated((double) 0.0, (double) (0.125 / scale), (double) 0.0);
        }
        GL11.glColor3f(1F, 1F, 1F);


        this.mc.getTextureManager().bindTexture(this.location);

        for (int j = 0; j < 2; ++j) {
            GL11.glEnable(2884);
            float f11 = (float) (System.currentTimeMillis() % 1000L) / 1000.0F * 3.1415927F * 2.0F;
            this.wing.rotateAngleX = (float) Math.toRadians(-80.0D) - (float) Math.cos((double) f11) * 0.2F;
            this.wing.rotateAngleY = (float) Math.toRadians(20.0D) + (float) Math.sin((double) f11) * 0.4F;
            this.wing.rotateAngleZ = (float) Math.toRadians(20.0D);
            this.wingTip.rotateAngleZ = -((float) (Math.sin((double) (f11 + 2.0F)) + 0.5D)) * 0.75F;
            this.wing.render(0.0625F);
            GL11.glScalef(-1.0F, 1.0F, 1.0F);
            if (j == 0) {
                GL11.glCullFace(1028);
            }
        }

        GL11.glCullFace(1029);
        GL11.glDisable(2884);
        GL11.glColor3f(255.0F, 255.0F, 255.0F);
        GL11.glPopMatrix();
    }

    private double interpolate(float yaw1, float yaw2, float percent) {
        double f = (yaw1 + (yaw2 - yaw1) * percent) % 360.0D;
        if (f < 0.0F) {
            f += 360.0F;
        }

        return f;
    }
}
