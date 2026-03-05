package net.elemental_wizards_rpg.client.entity;

import net.elemental_wizards_rpg.entity.StormDraftEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class StormDraftRenderer extends EntityRenderer<StormDraftEntity> {

    public StormDraftRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(StormDraftEntity entity) {
        return null;
    }
}
