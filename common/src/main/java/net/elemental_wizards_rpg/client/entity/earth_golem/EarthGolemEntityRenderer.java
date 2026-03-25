package net.elemental_wizards_rpg.client.entity.earth_golem;

import net.elemental_wizards_rpg.entity.spell_spawned.EarthGolemEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class EarthGolemEntityRenderer extends MobEntityRenderer<EarthGolemEntity, EarthGolemEntityModel> {
    private static final Identifier TEXTURE = Identifier.of(MOD_ID, "textures/entity/stone_golem.png");

    public EarthGolemEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new EarthGolemEntityModel(context.getPart(EarthGolemEntityModel.LAYER_LOCATION)), 0.7F);
    }

    @Override
    public Identifier getTexture(EarthGolemEntity entity) {
        return TEXTURE;
    }
}
