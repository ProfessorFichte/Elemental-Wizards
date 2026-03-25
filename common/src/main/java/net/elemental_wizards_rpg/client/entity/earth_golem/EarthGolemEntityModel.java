package net.elemental_wizards_rpg.client.entity.earth_golem;

import net.elemental_wizards_rpg.entity.spell_spawned.EarthGolemEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class EarthGolemEntityModel extends SinglePartEntityModel<EarthGolemEntity> {
    public static final EntityModelLayer LAYER_LOCATION = new EntityModelLayer(Identifier.of(MOD_ID, "stone_golem"), "main");
    private final ModelPart root;
    private final ModelPart golem;
    private final ModelPart waist;
    private final ModelPart upper_body;
    private final ModelPart bone;
    private final ModelPart left_arm;
    private final ModelPart left_forearm;
    private final ModelPart finger2;
    private final ModelPart finger3;
    private final ModelPart finger10;
    private final ModelPart right_arm;
    private final ModelPart right_forearm;
    private final ModelPart finger7;
    private final ModelPart finger8;
    private final ModelPart finger9;
    private final ModelPart head;
    private final ModelPart left_leg;
    private final ModelPart right_leg;

    public EarthGolemEntityModel(ModelPart root) {
        this.root = root;
        this.golem = root.getChild("golem");
        this.waist = this.golem.getChild("waist");
        this.upper_body = this.waist.getChild("upper_body");
        this.bone = this.upper_body.getChild("bone");
        this.left_arm = this.upper_body.getChild("left_arm");
        this.left_forearm = this.left_arm.getChild("left_forearm");
        this.finger2 = this.left_forearm.getChild("finger2");
        this.finger3 = this.left_forearm.getChild("finger3");
        this.finger10 = this.left_forearm.getChild("finger10");
        this.right_arm = this.upper_body.getChild("right_arm");
        this.right_forearm = this.right_arm.getChild("right_forearm");
        this.finger7 = this.right_forearm.getChild("finger7");
        this.finger8 = this.right_forearm.getChild("finger8");
        this.finger9 = this.right_forearm.getChild("finger9");
        this.head = this.upper_body.getChild("head");
        this.left_leg = root.getChild("left_leg");
        this.right_leg = root.getChild("right_leg");
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }

    public static TexturedModelData createBodyLayer() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        ModelPartData golem = modelPartData.addChild("golem", ModelPartBuilder.create(), ModelTransform.pivot(-1.2543F, 3.1569F, 0.25F));

        ModelPartData waist = golem.addChild("waist", ModelPartBuilder.create().uv(49, 106).cuboid(-9.7489F, -9.5457F, -3.661F, 19.0F, 10.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(1.2575F, -0.4543F, -0.3389F));

        ModelPartData upper_body = waist.addChild("upper_body", ModelPartBuilder.create().uv(0, 0).cuboid(-13.069F, -21.5462F, -11.928F, 25.0F, 22.0F, 17.0F, new Dilation(0.0F)), ModelTransform.pivot(0.3201F, -9.9995F, 3.2669F));

        ModelPartData cube_r1 = upper_body.addChild("cube_r1", ModelPartBuilder.create().uv(104, 106).cuboid(-6.5F, -12.5F, 0.0F, 13.0F, 17.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.3232F, -10.3174F, 5.2117F, -1.2154F, 0.3931F, -0.8012F));

        ModelPartData cube_r2 = upper_body.addChild("cube_r2", ModelPartBuilder.create().uv(104, 106).cuboid(-6.5F, -12.5F, 0.0F, 13.0F, 17.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.3232F, -10.3174F, 5.2117F, -1.1544F, -0.3272F, 0.6284F));

        ModelPartData bone = upper_body.addChild("bone", ModelPartBuilder.create(), ModelTransform.pivot(0.931F, 4.4538F, -2.928F));

        ModelPartData left_arm = upper_body.addChild("left_arm", ModelPartBuilder.create(), ModelTransform.of(16.3372F, -18.3975F, -3.6427F, 0.0F, 0.0436F, 0.0F));

        ModelPartData cube_r3 = left_arm.addChild("cube_r3", ModelPartBuilder.create().uv(71, 40).cuboid(-6.0F, -6.0F, -8.5F, 13.0F, 13.0F, 17.0F, new Dilation(0.0F)), ModelTransform.of(-0.2737F, -2.4912F, -1.1442F, -0.0928F, -0.0924F, 2.0114F));

        ModelPartData cube_r4 = left_arm.addChild("cube_r4", ModelPartBuilder.create().uv(0, 40).cuboid(-12.0F, -6.0F, -5.5F, 22.0F, 12.0F, 13.0F, new Dilation(0.0F)), ModelTransform.of(3.3251F, 8.2527F, -0.6616F, 0.0F, -0.1309F, 1.2217F));

        ModelPartData left_forearm = left_arm.addChild("left_forearm", ModelPartBuilder.create(), ModelTransform.of(11.1617F, 18.0073F, 2.3213F, -0.0436F, 0.1745F, 0.0F));

        ModelPartData cube_r5 = left_forearm.addChild("cube_r5", ModelPartBuilder.create().uv(0, 66).cuboid(-18.4387F, -6.0172F, -6.5F, 22.0F, 12.0F, 13.0F, new Dilation(0.0F)), ModelTransform.of(-3.9564F, 17.4595F, -1.2577F, 0.0F, 0.0F, 1.5708F));

        ModelPartData finger2 = left_forearm.addChild("finger2", ModelPartBuilder.create(), ModelTransform.pivot(0.0608F, 21.0208F, 2.7423F));

        ModelPartData cube_r6 = finger2.addChild("cube_r6", ModelPartBuilder.create().uv(0, 119).cuboid(2.264F, -6.3455F, 1.0F, 6.0F, 4.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-4.0172F, -3.5613F, -4.0F, 0.0F, 0.0F, 1.7453F));

        ModelPartData finger3 = left_forearm.addChild("finger3", ModelPartBuilder.create(), ModelTransform.pivot(-7.4392F, 21.0208F, -2.7577F));

        ModelPartData cube_r7 = finger3.addChild("cube_r7", ModelPartBuilder.create().uv(49, 92).cuboid(2.5506F, 2.0594F, -4.0F, 5.0F, 4.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(3.4828F, -3.5613F, 1.5F, 0.0F, 0.0F, 1.4399F));

        ModelPartData finger10 = left_forearm.addChild("finger10", ModelPartBuilder.create(), ModelTransform.pivot(-0.591F, 21.3981F, -4.7577F));

        ModelPartData cube_r8 = finger10.addChild("cube_r8", ModelPartBuilder.create().uv(23, 119).cuboid(2.264F, -6.3455F, -6.0F, 6.0F, 4.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-3.3654F, -3.9387F, 3.5F, 0.0F, 0.0F, 1.7453F));

        ModelPartData right_arm = upper_body.addChild("right_arm", ModelPartBuilder.create(), ModelTransform.of(-16.9837F, -18.3975F, -3.6427F, 0.0F, -0.0436F, 0.0F));

        ModelPartData cube_r9 = right_arm.addChild("cube_r9", ModelPartBuilder.create().uv(71, 40).mirrored().cuboid(-7.0F, -6.0F, -8.5F, 13.0F, 13.0F, 17.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.2737F, -2.4912F, -1.1442F, -0.0928F, 0.0924F, -2.0114F));

        ModelPartData cube_r10 = right_arm.addChild("cube_r10", ModelPartBuilder.create().uv(0, 40).mirrored().cuboid(-10.0F, -6.0F, -5.5F, 22.0F, 12.0F, 13.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-3.3251F, 8.2527F, -0.6616F, 0.0F, 0.1309F, -1.2217F));

        ModelPartData right_forearm = right_arm.addChild("right_forearm", ModelPartBuilder.create(), ModelTransform.of(-10.9883F, 18.0509F, 1.3375F, -0.0436F, -0.1745F, 0.0F));

        ModelPartData cube_r11 = right_forearm.addChild("cube_r11", ModelPartBuilder.create().uv(0, 66).mirrored().cuboid(-3.5613F, -6.0172F, -6.5F, 22.0F, 12.0F, 13.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(3.9565F, 17.4595F, -0.2577F, 0.0F, 0.0F, -1.5708F));

        ModelPartData finger7 = right_forearm.addChild("finger7", ModelPartBuilder.create(), ModelTransform.pivot(-0.0608F, 21.0208F, 3.7423F));

        ModelPartData cube_r12 = finger7.addChild("cube_r12", ModelPartBuilder.create().uv(0, 119).mirrored().cuboid(-8.2641F, -6.3456F, 1.0F, 6.0F, 4.0F, 5.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(4.0172F, -3.5613F, -4.0F, 0.0F, 0.0F, -1.7453F));

        ModelPartData finger8 = right_forearm.addChild("finger8", ModelPartBuilder.create(), ModelTransform.pivot(7.4392F, 21.0208F, -1.7577F));

        ModelPartData cube_r13 = finger8.addChild("cube_r13", ModelPartBuilder.create().uv(49, 92).mirrored().cuboid(-7.5506F, 2.0594F, -4.0F, 5.0F, 4.0F, 5.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-3.4828F, -3.5613F, 1.5F, 0.0F, 0.0F, -1.4399F));

        ModelPartData finger9 = right_forearm.addChild("finger9", ModelPartBuilder.create(), ModelTransform.pivot(0.591F, 21.3981F, -3.7577F));

        ModelPartData cube_r14 = finger9.addChild("cube_r14", ModelPartBuilder.create().uv(23, 119).mirrored().cuboid(-8.2641F, -6.3456F, -6.0F, 6.0F, 4.0F, 5.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(3.3654F, -3.9387F, 3.5F, 0.0F, 0.0F, -1.7453F));

        ModelPartData head = upper_body.addChild("head", ModelPartBuilder.create().uv(0, 92).cuboid(-6.5F, -7.5F, -11.5F, 13.0F, 15.0F, 11.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.3232F, -13.0462F, -10.428F));

        ModelPartData left_leg = modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(85, 0).cuboid(-6.0F, -1.0F, -6.0F, 12.0F, 22.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(7.5F, 3.0F, 0.25F));

        ModelPartData right_leg = modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(71, 71).cuboid(-6.0F, -1.0F, -6.0F, 12.0F, 22.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(-7.5F, 3.0F, 0.25F));

        return TexturedModelData.of(modelData, 256, 256);
    }

    @Override
    public void setAngles(EarthGolemEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);

        this.animateMovement(EarthGolemAnimations.walking, limbSwing, limbSwingAmount, 2f, 2.5f);

        this.updateAnimation(entity.attackAnimationState, EarthGolemAnimations.attack, ageInTicks);
        this.updateAnimation(entity.idleAnimationState, EarthGolemAnimations.idle, ageInTicks);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        golem.render(matrices, vertices, light, overlay, color);
        left_leg.render(matrices, vertices, light, overlay, color);
        right_leg.render(matrices, vertices, light, overlay, color);
    }
}
