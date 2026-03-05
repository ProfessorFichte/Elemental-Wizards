package net.elemental_wizards_rpg.datagen;

import com.google.gson.JsonObject;
import net.elemental_wizards_rpg.item.weapons.WeaponsRegister;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        for (var entry : WeaponsRegister.entries) {
            Item item = entry.item();
            if (item == null) continue;

            Identifier itemId = Registries.ITEM.getId(item);
            String name = itemId.getPath();

            if (name.contains("wand")) {
                generateWandModel(itemModelGenerator, itemId, name);
            } else if (name.contains("staff")) {
                generateStaffModel(itemModelGenerator, itemId, name);
            }
        }
    }

    private void generateWandModel(ItemModelGenerator gen, Identifier itemId, String name) {
        Identifier modelId = Identifier.of(itemId.getNamespace(), "item/" + name);

        JsonObject json = new JsonObject();
        json.addProperty("parent", "minecraft:item/handheld");

        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", MOD_ID + ":item/weapons/wands/" + name);
        json.add("textures", textures);

        gen.writer.accept(modelId, () -> json);
    }

    private void generateStaffModel(ItemModelGenerator gen, Identifier itemId, String name) {
        Identifier modelId = Identifier.of(itemId.getNamespace(), "item/" + name);

        JsonObject json = new JsonObject();
        json.addProperty("parent", "wizards:item/medium_staff");

        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", MOD_ID + ":item/weapons/staves/" + name);
        json.add("textures", textures);

        gen.writer.accept(modelId, () -> json);
    }
}
