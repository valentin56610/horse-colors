package sekelsta.horse_colors.renderer;

import net.minecraft.entity.passive.horse.*;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

import sekelsta.horse_colors.entity.AbstractHorseGenetic;

@OnlyIn(Dist.CLIENT)
public class HorseGeneticModel<T extends AbstractHorseEntity> extends EntityModel<T>
{
    private final RendererModel head;
    private final RendererModel horn;
    private final RendererModel babyHorn;
    private final RendererModel upperMouth;
    private final RendererModel lowerMouth;
    private final RendererModel horseLeftEar;
    private final RendererModel horseRightEar;
    /** The left ear box for the mule model. */
    private final RendererModel muleLeftEar;
    /** The right ear box for the mule model. */
    private final RendererModel muleRightEar;
    private final RendererModel neck;
    /** The box for the horse's ropes on its face. */
    private final RendererModel horseFaceRopes;
    private final RendererModel mane;
    private final RendererModel body;
    private final RendererModel tailBase;
    private final RendererModel tailMiddle;
    private final RendererModel tailTip;
    private final RendererModel tailThin; // For donkeys
    private final RendererModel tailTuft; // For donkeys
    private final RendererModel backLeftLeg;
    private final RendererModel backLeftShin;
    private final RendererModel backLeftHoof;
    private final RendererModel backRightLeg;
    private final RendererModel backRightShin;
    private final RendererModel backRightHoof;
    private final RendererModel frontLeftLeg;
    private final RendererModel frontLeftShin;
    private final RendererModel frontLeftHoof;
    private final RendererModel frontRightLeg;
    private final RendererModel frontRightShin;
    private final RendererModel frontRightHoof;
    /** The left chest box on the mule model. */
    private final RendererModel muleLeftChest;
    /** The right chest box on the mule model. */
    private final RendererModel muleRightChest;
    // The stirrups
    private final RendererModel horseLeftSaddleRope;
    private final RendererModel horseRightSaddleRope;

    private final RendererModel[] tackArray;
    private final RendererModel[] extraTackArray;

    private float ageScale = 0.5f;

    public HorseGeneticModel() {
        this(0.0F);
    }
    // 1.14's HorseModel takes a scale factor as constructor argument
    public HorseGeneticModel(float scaleFactor)
    {
        this.textureWidth = 128;
        this.textureHeight = 128;
        this.body = new RendererModel(this, 0, 34);
        this.body.addBox(-5.0F, -8.0F, -19.0F, 10, 10, 24, scaleFactor);
        this.body.setRotationPoint(0.0F, 11.0F, 9.0F);
        this.tailBase = new RendererModel(this, 44, 0);
        this.tailBase.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 3, scaleFactor);
        this.tailBase.setRotationPoint(0.0F, -8.0F, 5.0F);
        this.tailBase.rotateAngleX = -1.134464F;
        this.tailMiddle = new RendererModel(this, 38, 7);
        this.tailMiddle.addBox(-1.5F, -2.0F, 3.0F, 3, 4, 7, scaleFactor);
        this.tailBase.addChild(tailMiddle);
        this.tailTip = new RendererModel(this, 24, 3);
        this.tailTip.addBox(-1.5F, -4.5F, 9.0F, 3, 4, 7, scaleFactor);
        this.tailTip.rotateAngleX = -0.2618004F;
        this.tailMiddle.addChild(tailTip);
        this.body.addChild(tailBase);

        this.tailThin = new RendererModel(this, 116, 0);
        this.tailThin.addBox(-0.5F, 0.0F, 0.5F, 1, 5, 1, scaleFactor);
        this.tailThin.setRotationPoint(0.0F, -6F, 4.0F);
        this.tailThin.rotateAngleX = -1.134464F; // This doesn't seem to matter at all
        this.tailTuft = new RendererModel(this, 120, 0);
        this.tailTuft.addBox(-1.0F, 0F, 0.25F, 2, 6, 2, scaleFactor);
        this.tailTuft.setRotationPoint(0.0F, 5.0F, 0.0F);
        this.tailThin.addChild(tailTuft);
        this.body.addChild(tailThin);

        // When making something a child that wasn't before, subtract the
        // parent's rotationPoint
        this.backLeftLeg = new RendererModel(this, 78, 29);
        this.backLeftLeg.addBox(-2.5F, -2.0F, -2.5F, 4, 9, 5, scaleFactor);
        this.backLeftLeg.setRotationPoint(4.0F, 9.0F, 11.0F);
        this.backLeftShin = new RendererModel(this, 78, 43);
        this.backLeftShin.addBox(-2.0F, 0.0F, -1F, 3, 5, 3, scaleFactor);
        this.backLeftShin.setRotationPoint(0.0F, 7.0F, 0.0F);
        this.backLeftLeg.addChild(backLeftShin);
        this.backLeftHoof = new RendererModel(this, 78, 51);
        this.backLeftHoof.addBox(-2.5F, 5.1F, -2.0F, 4, 3, 4, scaleFactor);
        this.backLeftShin.addChild(this.backLeftHoof);

        this.backRightLeg = new RendererModel(this, 96, 29);
        this.backRightLeg.addBox(-1.5F, -2.0F, -2.5F, 4, 9, 5, scaleFactor);
        this.backRightLeg.setRotationPoint(-4.0F, 9.0F, 11.0F);
        this.backRightShin = new RendererModel(this, 96, 43);
        this.backRightShin.addBox(-1.0F, 0.0F, -1F, 3, 5, 3, scaleFactor);
        this.backRightShin.setRotationPoint(0.0F, 7.0F, 0.0F);
        this.backRightLeg.addChild(this.backRightShin);
        this.backRightHoof = new RendererModel(this, 96, 51);
        this.backRightHoof.addBox(-1.5F, 5.1F, -1.5F, 4, 3, 4, scaleFactor);
        this.backRightShin.addChild(this.backRightHoof);

        this.frontLeftLeg = new RendererModel(this, 44, 29);
        this.frontLeftLeg.addBox(-1.9F, -1.0F, -2.1F, 3, 8, 4, scaleFactor);
        this.frontLeftLeg.setRotationPoint(4.0F, 9.0F, -8.0F);
        this.frontLeftShin = new RendererModel(this, 44, 41);
        this.frontLeftShin.addBox(-1.9F, 0.0F, -1.6F, 3, 5, 3, scaleFactor);
        this.frontLeftShin.setRotationPoint(0.0F, 7.0F, 0.0F);
        this.frontLeftLeg.addChild(this.frontLeftShin);
        this.frontLeftHoof = new RendererModel(this, 44, 51);
        this.frontLeftHoof.addBox(-2.4F, 5.1F, -2.1F, 4, 3, 4, scaleFactor);
        this.frontLeftShin.addChild(this.frontLeftHoof);

        this.frontRightLeg = new RendererModel(this, 60, 29);
        this.frontRightLeg.addBox(-1.1F, -1.0F, -2.1F, 3, 8, 4, scaleFactor);
        this.frontRightLeg.setRotationPoint(-4.0F, 9.0F, -8.0F);
        this.frontRightShin = new RendererModel(this, 60, 41);
        this.frontRightShin.addBox(-1.1F, 0.0F, -1.6F, 3, 5, 3, scaleFactor);
        this.frontRightShin.setRotationPoint(0.0F, 7.0F, 0.0F);
        this.frontRightLeg.addChild(this.frontRightShin);
        this.frontRightHoof = new RendererModel(this, 60, 51);
        this.frontRightHoof.addBox(-1.6F, 5.1F, -2.1F, 4, 3, 4, scaleFactor);
        this.frontRightShin.addChild(this.frontRightHoof);

        this.head = new RendererModel(this, 0, 0);
        this.head.addBox(-2.5F, -10.0F, -1.5F, 5, 5, 7, scaleFactor);
        this.head.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.head.rotateAngleX = 0.5235988F;
        this.upperMouth = new RendererModel(this, 24, 18);
        this.upperMouth.addBox(-2.0F, -10.0F, -7.0F, 4, 3, 6, scaleFactor);
        this.lowerMouth = new RendererModel(this, 24, 27);
        this.lowerMouth.addBox(-2.0F, -7.0F, -6.5F, 4, 2, 5, scaleFactor);
        this.head.addChild(this.upperMouth);
        this.head.addChild(this.lowerMouth);
        this.horseLeftEar = new RendererModel(this, 0, 0);
        this.horseLeftEar.addBox(0.45F, -12.0F, 4.0F, 2, 3, 1, scaleFactor);
        this.horseRightEar = new RendererModel(this, 0, 0);
        this.horseRightEar.addBox(-2.45F, -12.0F, 4.0F, 2, 3, 1, scaleFactor);
        this.muleLeftEar = new RendererModel(this, 0, 12);
        this.muleLeftEar.addBox(-2.0F, -16.0F, 4.0F, 2, 7, 1, scaleFactor);
        this.muleLeftEar.rotateAngleZ = 0.2617994F;
        this.muleRightEar = new RendererModel(this, 0, 12);
        this.muleRightEar.addBox(0.0F, -16.0F, 4.0F, 2, 7, 1, scaleFactor);
        this.muleRightEar.rotateAngleZ = -0.2617994F;
        this.head.addChild(horseLeftEar);
        this.head.addChild(horseRightEar);
        this.head.addChild(muleLeftEar);
        this.head.addChild(muleRightEar);

        this.neck = new RendererModel(this, 0, 12);
        this.neck.addBox(-2.05F, -9.8F, -2.0F, 4, 14, 8, scaleFactor);
        this.neck.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.neck.rotateAngleX = 0.5235988F;
        //this.neck.addChild(this.head);


        this.muleLeftChest = new RendererModel(this, 0, 34);
        this.muleLeftChest.addBox(-3.0F, 0.0F, 0.0F, 8, 8, 3, scaleFactor);
        this.muleLeftChest.setRotationPoint(-7.5F, 3.0F, 10.0F);
        this.muleLeftChest.rotateAngleY = ((float)Math.PI / 2F);
        this.muleRightChest = new RendererModel(this, 0, 47);
        this.muleRightChest.addBox(-3.0F, 0.0F, 0.0F, 8, 8, 3, scaleFactor);
        this.muleRightChest.setRotationPoint(4.5F, 3.0F, 10.0F);
        this.muleRightChest.rotateAngleY = ((float)Math.PI / 2F);
        RendererModel horseSaddleBottom = new RendererModel(this, 80, 0);
        horseSaddleBottom.addBox(-5.0F, 0.0F, -3.0F, 10, 1, 8, scaleFactor);
        horseSaddleBottom.setRotationPoint(0.0F, -9.0F, -7.0F);
        body.addChild(horseSaddleBottom);

        RendererModel horseSaddleFront = new RendererModel(this, 106, 9);
        horseSaddleFront.addBox(-1.5F, -1.0F, -3.0F, 3, 1, 2, scaleFactor);
        horseSaddleBottom.addChild(horseSaddleFront);

        RendererModel horseSaddleBack = new RendererModel(this, 80, 9);
        horseSaddleBack.addBox(-4.0F, -1.0F, 3.0F, 8, 1, 2, scaleFactor);
        horseSaddleBottom.addChild(horseSaddleBack);

        this.horseLeftSaddleRope = new RendererModel(this, 70, 0);
        this.horseLeftSaddleRope.addBox(-0.5F, 0.0F, -0.5F, 1, 6, 1, scaleFactor);
        this.horseLeftSaddleRope.setRotationPoint(5.0F, 1.0F, 0.0F);
        horseSaddleBottom.addChild(this.horseLeftSaddleRope);

        RendererModel horseLeftSaddleMetal = new RendererModel(this, 74, 0);
        horseLeftSaddleMetal.addBox(-0.5F, 6.0F, -1.0F, 1, 2, 2, scaleFactor);
        horseLeftSaddleRope.addChild(horseLeftSaddleMetal);

        this.horseRightSaddleRope = new RendererModel(this, 80, 0);
        this.horseRightSaddleRope.addBox(-0.5F, 0.0F, -0.5F, 1, 6, 1, scaleFactor);
        this.horseRightSaddleRope.setRotationPoint(-5.0F, 1.0F, 0.0F);
        horseSaddleBottom.addChild(this.horseRightSaddleRope);

        RendererModel horseRightSaddleMetal = new RendererModel(this, 74, 4);
        horseRightSaddleMetal.addBox(-0.5F, 6.0F, -1.0F, 1, 2, 2, scaleFactor);
        horseRightSaddleRope.addChild(horseRightSaddleMetal);

        RendererModel horseLeftFaceMetal = new RendererModel(this, 74, 13);
        horseLeftFaceMetal.addBox(1.5F, -8.0F, -4.0F, 1, 2, 2, scaleFactor);
        this.head.addChild(horseLeftFaceMetal);
        RendererModel horseRightFaceMetal = new RendererModel(this, 74, 13);
        horseRightFaceMetal.addBox(-2.5F, -8.0F, -4.0F, 1, 2, 2, scaleFactor);
        this.head.addChild(horseRightFaceMetal);
        this.horseFaceRopes = new RendererModel(this, 80, 12);
        this.horseFaceRopes.addBox(-2.5F, -10.1F, -7.0F, 5, 5, 12, 0.2F);
        this.head.addChild(horseFaceRopes);

        this.tackArray = new RendererModel[]{horseSaddleBottom, horseSaddleFront, horseSaddleBack, horseLeftSaddleMetal, horseLeftSaddleRope, horseRightSaddleMetal, horseRightSaddleRope, horseLeftFaceMetal, horseRightFaceMetal, horseFaceRopes};

        // Do not scale the reins, otherwise they don't display properly
        // I assume scaling doesn't work for things with 0 width./gradle
        RendererModel horseLeftRein = new RendererModel(this, 44, 10);
        horseLeftRein.addBox(2.6F, -6.0F, -6.0F, 0, 3, 16);
        RendererModel horseRightRein = new RendererModel(this, 44, 5);
        horseRightRein.addBox(-2.6F, -6.0F, -6.0F, 0, 3, 16);
        this.neck.addChild(horseLeftRein);
        this.neck.addChild(horseRightRein);
        horseLeftRein.rotateAngleX = -0.5235988F;
        horseRightRein.rotateAngleX = -0.5235988F;
        this.extraTackArray = new RendererModel[]{horseLeftRein, horseRightRein};

        this.mane = new RendererModel(this, 58, 0);
        this.mane.addBox(-1.0F, -11.5F, 5.0F, 2, 16, 4, scaleFactor);
        this.mane.setRotationPoint(0.0F, 0.0F, -1.0F);
        this.neck.addChild(mane);

        int hornLength = 7;
        this.horn = new RendererModel(this, 84, 0);
        this.horn.addBox(-0.5F, -10.0F - hornLength, 2.0F, 1, hornLength, 1, scaleFactor);
        this.head.addChild(horn);
        int babyHornLength = 3;
        this.babyHorn = new RendererModel(this, 84, 0);
        this.babyHorn.addBox(-0.5F, -10.0F - babyHornLength, 2.0F, 1, babyHornLength, 1, scaleFactor);
        this.head.addChild(babyHorn);
    }

    public void updateBoxes(T entityIn) {
        if (entityIn instanceof AbstractHorseGenetic) {
            AbstractHorseGenetic horse = (AbstractHorseGenetic)entityIn;
            this.muleLeftEar.showModel = horse.longEars();
            this.muleRightEar.showModel = horse.longEars();
            this.horseLeftEar.showModel = !horse.longEars();
            this.horseRightEar.showModel = !horse.longEars();
            this.tailBase.showModel = horse.fluffyTail();
            this.tailThin.showModel = !horse.fluffyTail();
            this.ageScale = horse.getGangliness();
        }
        else {
            System.out.println("Attempting to use HorseGeneticModel on an unsupported entity type");
        }
        this.horn.showModel = false;
        this.babyHorn.showModel = false;

        boolean hasChest = false;
        if (entityIn instanceof AbstractChestedHorseEntity) {
            hasChest = ((AbstractChestedHorseEntity)entityIn).hasChest();
        }
        this.muleLeftChest.showModel = hasChest;
        this.muleRightChest.showModel = hasChest;

        boolean isSaddled = entityIn.isHorseSaddled();
        boolean isRidden = entityIn.isBeingRidden();

        for(RendererModel tack_piece : this.tackArray) {
            tack_piece.showModel = isSaddled;
        }

        for(RendererModel extra_tack : this.extraTackArray) {
            extra_tack.showModel = isRidden && isSaddled;
        }

        // Probably because the body only rotates for rearing
        this.body.rotationPointY = 11.0F;
     }

    /**
     * Fixes and offsets a rotation in the ModelHorse class.
     *//* Replaced by MathHelper.func_226167_j_() */
    private float updateHorseRotation(float prevRotation, float currentRotation, float partialTickTime)
    {
        float bodyRotation;

        for (bodyRotation = currentRotation - prevRotation; bodyRotation < -180.0F; bodyRotation += 360.0F)
        {
            ;
        }

        while (bodyRotation >= 180.0F)
        {
            bodyRotation -= 360.0F;
        }

        return prevRotation + partialTickTime * bodyRotation;
    }

    // Copied and modified from the familiar horses mod as allowed by the Unlicense
    /**
     * Sets the models various rotation angles then renders the model.
     */
    @Override
    public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        //Consumer<RendererModel> render = model -> model.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        updateBoxes(entityIn);
        float f = (this.head.rotateAngleX - 0.5235988F) * 0.6031134647F;

        // ageScale is 0.5f for the smallest foals
        if (this.isChild) {
            GlStateManager.pushMatrix();
            GlStateManager.scalef(ageScale, 0.5F + ageScale * 0.5F, ageScale);
            // Move the foal's legs downward so they reach the ground
            GlStateManager.translatef(0.0F, 0.95F * (1.0F - ageScale), 0.0F);
        }

        this.backLeftLeg.render(scale);
        this.backRightLeg.render(scale);
        this.frontLeftLeg.render(scale);
        this.frontRightLeg.render(scale);

        if (this.isChild) {
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            // Move the body downwards but not as far as the legs
            GlStateManager.translatef(0.0F, ageScale * 1.35F * (1.0F - ageScale), 0.0F);
            GlStateManager.scalef(ageScale, ageScale, ageScale);
        }

        this.body.render(scale);
        this.neck.render(scale);

        if (this.isChild) {
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            float headScale = 0.5F + ageScale * ageScale * 0.5F;
            // Translate to match the body position
            GlStateManager.translatef(0.0F, ageScale * 1.35F * (1.0F - ageScale), 0.0F);
            GlStateManager.scalef(headScale, headScale, headScale);
            float extra = (headScale - ageScale) * 1.35F * (1.0F - ageScale);
            // The head's rest angle is 0.5235988F, 30 degrees
            GlStateManager.translatef(0.0F, extra * (float)Math.cos(this.head.rotateAngleX), extra * (float)Math.sin(this.head.rotateAngleX));
        }

        this.head.render(scale);

        if (this.isChild) {
            GlStateManager.popMatrix();
        }

        this.muleLeftChest.render(scale);
        this.muleRightChest.render(scale);
    }

    private void setMouthAnimations(float mouthOpenAmount) {
        this.upperMouth.rotationPointY = 0.02F;
        this.lowerMouth.rotationPointY = 0.0F;
        this.upperMouth.rotationPointZ = 0.02F - mouthOpenAmount;
        this.lowerMouth.rotationPointZ = mouthOpenAmount;
        this.upperMouth.rotateAngleX = -0.09424778F * mouthOpenAmount;
        this.lowerMouth.rotateAngleX = 0.15707964F * mouthOpenAmount;
        this.upperMouth.rotateAngleY = 0.0F;
        this.lowerMouth.rotateAngleY = 0.0F;
    }


    /**
     * Used for easily adding entity-dependent animations. The second and third float params here are the same second
     * and third as in the setRotationAngles method.
     */
    public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTickTime)
    {
        super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTickTime);
        float bodyRotation = this.updateHorseRotation(entityIn.prevRenderYawOffset, entityIn.renderYawOffset, partialTickTime);
        float headRotation = this.updateHorseRotation(entityIn.prevRotationYawHead, entityIn.rotationYawHead, partialTickTime);
        float interpolatedPitch = entityIn.prevRotationPitch + (entityIn.rotationPitch - entityIn.prevRotationPitch) * partialTickTime;
        float headRelativeRotation = headRotation - bodyRotation;
        float f4 = interpolatedPitch * 0.017453292F;

        if (headRelativeRotation > 20.0F)
        {
            headRelativeRotation = 20.0F;
        }

        if (headRelativeRotation < -20.0F)
        {
            headRelativeRotation = -20.0F;
        }

        if (limbSwingAmount > 0.2F)
        {
            f4 += MathHelper.cos(limbSwing * 0.4F) * 0.15F * limbSwingAmount;
        }

        AbstractHorseEntity abstracthorse = (AbstractHorseEntity)entityIn;
        float grassEatingAmount = abstracthorse.getGrassEatingAmount(partialTickTime);
        float rearingAmount = abstracthorse.getRearingAmount(partialTickTime);
        float notRearingAmount = 1.0F - rearingAmount;
        boolean isSwishingTail = abstracthorse.tailCounter != 0;
        boolean isSaddled = abstracthorse.isHorseSaddled();
        boolean isBeingRidden = abstracthorse.isBeingRidden();
        float ticks = (float)entityIn.ticksExisted + partialTickTime;
        float legRotationBase = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI);
        float legRotation1 = legRotationBase * 0.8F * limbSwingAmount;
        float neckBend = rearingAmount + 1.0F - Math.max(rearingAmount, grassEatingAmount);


        float mouthOpenAmount = abstracthorse.getMouthOpennessAngle(partialTickTime);
        this.setMouthAnimations(mouthOpenAmount);

        this.neck.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.neck.rotateAngleX = rearingAmount * (0.2617994F + f4) + grassEatingAmount * 2.1816616F + (1.0F - Math.max(rearingAmount, grassEatingAmount)) * 0.5235988F + f4;
        this.neck.rotateAngleY = neckBend * headRelativeRotation * 0.017453292F;
        this.neck.rotationPointY = rearingAmount * -6.0F + grassEatingAmount * 11.0F + (1.0F - Math.max(rearingAmount, grassEatingAmount)) * this.neck.rotationPointY;
        this.neck.rotationPointZ = rearingAmount * -1.0F + grassEatingAmount * -10.0F + (1.0F - Math.max(rearingAmount, grassEatingAmount)) * this.neck.rotationPointZ;
        this.body.rotateAngleX = rearingAmount * -((float)Math.PI / 4F);
        this.head.rotationPointX = this.neck.rotationPointX;
        this.head.rotationPointY = this.neck.rotationPointY;
        this.head.rotationPointZ = this.neck.rotationPointZ;
        this.head.rotateAngleX = this.neck.rotateAngleX;
        this.head.rotateAngleY = this.neck.rotateAngleY;
        float legRotationRearing = 0.2617994F * rearingAmount;
        float legRotationTicks = MathHelper.cos(ticks * 0.6F + (float)Math.PI);
        this.frontLeftLeg.rotationPointY = -2.0F * rearingAmount + 9.0F * notRearingAmount;
        this.frontLeftLeg.rotationPointZ = -2.0F * rearingAmount + -8.0F * notRearingAmount;
        this.frontRightLeg.rotationPointY = this.frontLeftLeg.rotationPointY;
        this.frontRightLeg.rotationPointZ = this.frontLeftLeg.rotationPointZ;
        float legRotation4 = (-1.0471976F + legRotationTicks) * rearingAmount + legRotation1 * notRearingAmount;
        float legRotation5 = (-1.0471976F - legRotationTicks) * rearingAmount + -legRotation1 * notRearingAmount;
        this.backLeftLeg.rotateAngleX = legRotationRearing + -legRotationBase * 0.5F * limbSwingAmount * notRearingAmount;
        this.backRightLeg.rotateAngleX = legRotationRearing + legRotationBase * 0.5F * limbSwingAmount * notRearingAmount;
        this.frontLeftLeg.rotateAngleX = legRotation4;
        this.frontLeftShin.rotateAngleX = (this.frontLeftLeg.rotateAngleX + (float)Math.PI * Math.max(0.0F, 0.2F + legRotationTicks * 0.2F)) * rearingAmount + (legRotation1 + Math.max(0.0F, legRotationBase * 0.5F * limbSwingAmount)) * notRearingAmount - this.frontLeftLeg.rotateAngleX;
        // This might do the same thing
        //this.frontLeftShin.rotateAngleX = ((float)Math.PI * Math.max(0.0F, 0.2F + legRotationTicks * 0.2F)) * rearingAmount;
        this.frontRightLeg.rotateAngleX = legRotation5;
        this.frontRightShin.rotateAngleX = (this.frontRightLeg.rotateAngleX + (float)Math.PI * Math.max(0.0F, 0.2F - legRotationTicks * 0.2F)) * rearingAmount + (-legRotation1 + Math.max(0.0F, -legRotationBase * 0.5F * limbSwingAmount)) * notRearingAmount - this.frontRightLeg.rotateAngleX;
        //this.frontRightShin.rotateAngleX = ((float)Math.PI * Math.max(0.0F, 0.2F - legRotationTicks * 0.2F)) * rearingAmount;

        this.muleRightChest.rotationPointY = 3.0F;
        this.muleRightChest.rotationPointZ = 10.0F;
        this.muleRightChest.rotationPointY = rearingAmount * 5.5F + notRearingAmount * this.muleRightChest.rotationPointY;
        this.muleRightChest.rotationPointZ = rearingAmount * 15.0F + notRearingAmount * this.muleRightChest.rotationPointZ;
        this.muleLeftChest.rotateAngleX = legRotation1 / 5.0F;
        this.muleRightChest.rotateAngleX = -legRotation1 / 5.0F;

        if (isSaddled)
        {
            this.muleLeftChest.rotationPointY = this.muleRightChest.rotationPointY;
            this.muleLeftChest.rotationPointZ = this.muleRightChest.rotationPointZ;

            if (isBeingRidden)
            {
                this.horseLeftSaddleRope.rotateAngleX = -1.0471976F;
                this.horseRightSaddleRope.rotateAngleX = -1.0471976F;
                this.horseLeftSaddleRope.rotateAngleZ = 0.0F;
                this.horseRightSaddleRope.rotateAngleZ = 0.0F;
            }
            else
            {
                this.horseLeftSaddleRope.rotateAngleX = legRotation1 / 3.0F;
                this.horseRightSaddleRope.rotateAngleX = legRotation1 / 3.0F;
                this.horseLeftSaddleRope.rotateAngleZ = legRotation1 / 5.0F;
                this.horseRightSaddleRope.rotateAngleZ = -legRotation1 / 5.0F;
            }
        }

        float tailRotation = -1.3089969F + limbSwingAmount * 1.5F;
        float donkeyTailRotate = 0.17F + limbSwingAmount;

        if (tailRotation > 0.0F)
        {
            tailRotation = 0.0F;
        }

        if (isSwishingTail)
        {
            this.tailBase.rotateAngleY = MathHelper.cos(ticks * 0.7F);
            this.tailThin.rotateAngleY = MathHelper.cos(ticks * 0.7F);
            tailRotation = 0.0F;
            donkeyTailRotate = (float)Math.PI / 2f;
        }
        else
        {
            this.tailBase.rotateAngleY = 0.0F;
            this.tailThin.rotateAngleY = 0.0F;
        }

        this.tailBase.rotateAngleX = tailRotation;
        this.tailThin.rotateAngleX = donkeyTailRotate;

        // Make donkeys have a thinner mane
        if (abstracthorse instanceof AbstractHorseGenetic && ((AbstractHorseGenetic)abstracthorse).thinMane()) {
            this.mane.rotationPointZ = -1.0F;
        }
        else {
            this.mane.rotationPointZ = 0.0F;
        }
    }
}
