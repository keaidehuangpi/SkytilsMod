package gg.skytils.skytilsmod.utils;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.BlockPos;

public final class Misc {
    public static ScaledResolution getScaledResolution() {
        return new ScaledResolution(Minecraft.getMinecraft());
    }

    public static void clickMouse() {
        int leftClickCounter = (int) ReflectionUtil.getFieldValue(Minecraft.getMinecraft(), "leftClickCounter", "field_71429_W");
        if (leftClickCounter <= 0) {
            Minecraft.getMinecraft().thePlayer.swingItem();
            if (Minecraft.getMinecraft().objectMouseOver == null) {
                if (Minecraft.getMinecraft().playerController.isNotCreative()) {
                    ReflectionUtil.setFieldValue(Minecraft.getMinecraft(), 10, "leftClickCounter", "field_71429_W");
                }
            } else {
                switch (Minecraft.getMinecraft().objectMouseOver.typeOfHit) {
                    case ENTITY:
                        try {
                            Minecraft.getMinecraft().playerController.attackEntity(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().objectMouseOver.entityHit);
                        } catch (NullPointerException exception) {
                            exception.printStackTrace();
                        }
                        break;

                    case BLOCK:
                        BlockPos blockpos = Minecraft.getMinecraft().objectMouseOver.getBlockPos();

                        if (Minecraft.getMinecraft().theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air) {
                            Minecraft.getMinecraft().playerController.clickBlock(blockpos, Minecraft.getMinecraft().objectMouseOver.sideHit);
                            break;
                        }

                    case MISS:
                    default:
                        if (Minecraft.getMinecraft().playerController.isNotCreative()) {
                            ReflectionUtil.setFieldValue(Minecraft.getMinecraft(), 10, "leftClickCounter", "field_71429_W");
                        }
                }
            }
        }
    }
}
