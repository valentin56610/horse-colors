package sekelsta.horse_colors.renderer;

import sekelsta.horse_colors.entity.HorseGeneticEntity;
import sekelsta.horse_colors.util.HorseArmorer;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.CarpetBlock;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.passive.horse.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeableHorseArmorItem;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class HorseArmorLayer extends LayerRenderer<AbstractHorseEntity, HorseGeneticModel<AbstractHorseEntity>> {
    private final HorseGeneticModel<AbstractHorseEntity> horseModel = new HorseGeneticModel<>();

    public HorseArmorLayer(IEntityRenderer<AbstractHorseEntity, HorseGeneticModel<AbstractHorseEntity>> model) {
       super(model);
    }

    @Override
    // Render function
    public void render(AbstractHorseEntity entityIn, float limbSwing, float limbSwingAmount, float partialTickTime, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (!(entityIn instanceof HorseGeneticEntity)) {
            return;
        }
        HorseGeneticEntity horse = (HorseGeneticEntity)entityIn;
        ItemStack itemstack = horse.getHorseArmor();
        Item armor = itemstack.getItem();
        ResourceLocation textureLocation = HorseArmorer.getTexture(armor);
        if (textureLocation != null) {
            this.getEntityModel().setModelAttributes(this.horseModel);
            this.horseModel.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTickTime);
            this.bindTexture(textureLocation);
            float r = 1;
            float g = 1;
            float b = 1;
            int color = 0xffffff;
            if (armor instanceof DyeableHorseArmorItem) {
                color = ((DyeableHorseArmorItem)armor).getColor(itemstack);
                r = (float)(color >> 16 & 255) / 255.0F;
                g = (float)(color >> 8 & 255) / 255.0F;
                b = (float)(color & 255) / 255.0F;
            } 
            else if (armor instanceof BlockItem) {
                BlockItem blockItem = (BlockItem)armor;
                if (blockItem.getBlock() instanceof CarpetBlock) {
                    // func_196057_c() == getSwappedColorValue()
                    // I don't know why there's a method to get the inverted color
                    // but not one to get the regular color
                    color = ((CarpetBlock)(blockItem.getBlock())).getColor().func_196057_c();
                    b = (float)(color >> 16 & 255) / 255.0F;
                    g = (float)(color >> 8 & 255) / 255.0F;
                    r = (float)(color & 255) / 255.0F;
                }
            }

            GlStateManager.color4f(r, g, b, 1.0F);
            this.horseModel.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }


    public boolean shouldCombineTextures() {
        return false;
    }
}
