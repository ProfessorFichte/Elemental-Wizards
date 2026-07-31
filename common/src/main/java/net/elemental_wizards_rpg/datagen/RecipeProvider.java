package net.elemental_wizards_rpg.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class RecipeProvider implements DataProvider {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    protected final FabricDataOutput output;
    protected final String modId;
    private final List<RecipeData> recipes = new ArrayList<>();

    public RecipeProvider(FabricDataOutput output, String modId) {
        this.output = output;
        this.modId = modId;
    }

    public abstract void generate();

    public void createShapedRecipe(String name, Item result, int count, String[] pattern, Object... keys) {
        recipes.add(new RecipeData(name, RecipeType.SHAPED, result, count, pattern, keys, null, false));
    }

    public void createConditionalShapedRecipe(String name, Item result, int count, String[] pattern, String requiredMod, Object... keys) {
        recipes.add(new RecipeData(name, RecipeType.SHAPED, result, count, pattern, keys, new String[]{requiredMod}, true));
    }

    public void createConditionalShapedRecipe(String name, Item result, int count, String[] pattern, String[] requiredMods, Object... keys) {
        recipes.add(new RecipeData(name, RecipeType.SHAPED, result, count, pattern, keys, requiredMods, true));
    }

    public void createShapelessRecipe(String name, Item result, int count, Object... ingredients) {
        recipes.add(new RecipeData(name, RecipeType.SHAPELESS, result, count, null, ingredients, null, false));
    }

    public void createConditionalShapelessRecipe(String name, Item result, int count, String requiredMod, Object... ingredients) {
        recipes.add(new RecipeData(name, RecipeType.SHAPELESS, result, count, null, ingredients, new String[]{requiredMod}, true));
    }

    public void createConditionalShapelessRecipe(String name, Item result, int count, String[] requiredMods, Object... ingredients) {
        recipes.add(new RecipeData(name, RecipeType.SHAPELESS, result, count, null, ingredients, requiredMods, true));
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        generate();

        return CompletableFuture.allOf(recipes.stream().map(recipeData -> {
            JsonObject recipe = buildRecipeJson(recipeData);
            Path path = output.getResolver(net.minecraft.data.DataOutput.OutputType.DATA_PACK, "recipe")
                    .resolveJson(Identifier.of(modId, recipeData.name));

            return DataProvider.writeToPath(writer, recipe, path);
        }).toArray(CompletableFuture[]::new));
    }

    private JsonObject buildRecipeJson(RecipeData data) {
        JsonObject recipe = new JsonObject();

        if (data.withLoadConditions && data.requiredMods != null && data.requiredMods.length > 0) {
            JsonArray fabricLoadConditions = new JsonArray();
            JsonObject fabricCondition = new JsonObject();
            fabricCondition.addProperty("condition", "fabric:all_mods_loaded");
            JsonArray modValues = new JsonArray();
            for (String mod : data.requiredMods) {
                modValues.add(mod);
            }
            fabricCondition.add("values", modValues);
            fabricLoadConditions.add(fabricCondition);
            recipe.add("fabric:load_conditions", fabricLoadConditions);

            JsonArray neoforgeConditions = new JsonArray();
            if (data.requiredMods.length == 1) {
                JsonObject neoforgeCondition = new JsonObject();
                neoforgeCondition.addProperty("type", "neoforge:mod_loaded");
                neoforgeCondition.addProperty("modid", data.requiredMods[0]);
                neoforgeConditions.add(neoforgeCondition);
            } else {
                JsonObject andCondition = new JsonObject();
                andCondition.addProperty("type", "neoforge:and");
                JsonArray innerConditions = new JsonArray();
                for (String mod : data.requiredMods) {
                    JsonObject modCondition = new JsonObject();
                    modCondition.addProperty("type", "neoforge:mod_loaded");
                    modCondition.addProperty("modid", mod);
                    innerConditions.add(modCondition);
                }
                andCondition.add("conditions", innerConditions);
                neoforgeConditions.add(andCondition);
            }
            recipe.add("neoforge:conditions", neoforgeConditions);
        }

        if (data.type == RecipeType.SHAPED) {
            recipe.addProperty("type", "minecraft:crafting_shaped");
            buildShapedRecipe(recipe, data);
        } else {
            recipe.addProperty("type", "minecraft:crafting_shapeless");
            buildShapelessRecipe(recipe, data);
        }

        return recipe;
    }

    private void buildShapedRecipe(JsonObject recipe, RecipeData data) {
        JsonArray patternArray = new JsonArray();
        for (String line : data.pattern) {
            patternArray.add(line);
        }
        recipe.add("pattern", patternArray);

        JsonObject keyObject = new JsonObject();

        if (data.keys.length % 2 != 0) {
            StringBuilder debugInfo = new StringBuilder("Keys array has odd length! Recipe: " + data.name + "\nKeys: [");
            for (int i = 0; i < data.keys.length; i++) {
                debugInfo.append("\n  [").append(i).append("] = ").append(data.keys[i]);
            }
            debugInfo.append("\n]");
            throw new IllegalStateException(debugInfo.toString());
        }

        for (int i = 0; i < data.keys.length; i += 2) {
            if (!(data.keys[i] instanceof String)) {
                throw new IllegalArgumentException("Recipe: " + data.name + " - Key at index " + i + " must be String, got: " +
                    data.keys[i].getClass().getName() + " - " + data.keys[i] +
                    " (keys array length: " + data.keys.length + ")");
            }
            String keyChar = (String) data.keys[i];
            Object ingredient = data.keys[i + 1];
            Object ingredientJson = createIngredientJson(ingredient);

            if (ingredientJson instanceof JsonObject) {
                keyObject.add(keyChar, (JsonObject) ingredientJson);
            } else if (ingredientJson instanceof JsonArray) {
                keyObject.add(keyChar, (JsonArray) ingredientJson);
            }
        }
        recipe.add("key", keyObject);

        JsonObject resultObj = new JsonObject();
        resultObj.addProperty("id", getItemId(data.result));
        if (data.count > 1) {
            resultObj.addProperty("count", data.count);
        }
        recipe.add("result", resultObj);
    }

    private void buildShapelessRecipe(JsonObject recipe, RecipeData data) {
        JsonArray ingredientsArray = new JsonArray();
        for (Object ingredient : data.keys) {
            Object ingredientJson = createIngredientJson(ingredient);

            if (ingredientJson instanceof JsonObject) {
                ingredientsArray.add((JsonObject) ingredientJson);
            } else if (ingredientJson instanceof JsonArray) {
                ingredientsArray.add((JsonArray) ingredientJson);
            }
        }
        recipe.add("ingredients", ingredientsArray);

        JsonObject resultObj = new JsonObject();
        resultObj.addProperty("id", getItemId(data.result));
        if (data.count > 1) {
            resultObj.addProperty("count", data.count);
        }
        recipe.add("result", resultObj);
    }

    private Object createIngredientJson(Object ingredient) {
        if (ingredient instanceof Item item) {
            JsonObject obj = new JsonObject();
            obj.addProperty("item", getItemId(item));
            return obj;
        } else if (ingredient instanceof Identifier id) {
            JsonObject obj = new JsonObject();
            obj.addProperty("item", id.toString());
            return obj;
        } else if (ingredient instanceof String str) {
            JsonObject obj = new JsonObject();
            obj.addProperty("item", str);
            return obj;
        } else if (ingredient instanceof TagKey<?> tag) {
            JsonObject obj = new JsonObject();
            obj.addProperty("tag", tag.id().toString());
            return obj;
        } else if (ingredient instanceof Item[] items) {
            JsonArray array = new JsonArray();
            for (Item item : items) {
                JsonObject obj = new JsonObject();
                obj.addProperty("item", getItemId(item));
                array.add(obj);
            }
            return array;
        }
        throw new IllegalArgumentException("Unsupported ingredient type: " + ingredient.getClass());
    }

    private String getItemId(Object itemOrId) {
        if (itemOrId instanceof Identifier id) {
            return id.toString();
        } else if (itemOrId instanceof String str) {
            return str;
        } else if (itemOrId instanceof Item item) {
            return Registries.ITEM.getId(item).toString();
        }
        throw new IllegalArgumentException("Result must be Item, Identifier, or String");
    }

    @Override
    public String getName() {
        return "Crafting Recipes (" + modId + ")";
    }

    private enum RecipeType {
        SHAPED,
        SHAPELESS
    }

    private record RecipeData(
            String name,
            RecipeType type,
            Item result,
            int count,
            String[] pattern,
            Object[] keys,
            String[] requiredMods,
            boolean withLoadConditions
    ) {}
}
