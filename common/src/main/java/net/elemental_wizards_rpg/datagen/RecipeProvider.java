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

/**
 * Generic generator for Crafting recipes (shaped and shapeless)
 */
public abstract class RecipeProvider implements DataProvider {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    protected final FabricDataOutput output;
    protected final String modId;
    private final List<RecipeData> recipes = new ArrayList<>();

    public RecipeProvider(FabricDataOutput output, String modId) {
        this.output = output;
        this.modId = modId;
    }

    /**
     * Implement this method to generate your recipes
     */
    public abstract void generate();

    // ==========================================
    // SHAPED RECIPES
    // ==========================================

    /**
     * Creates a shaped crafting recipe WITHOUT load conditions
     */
    public void createShapedRecipe(String name, Item result, int count, String[] pattern, Object... keys) {
        recipes.add(new RecipeData(name, RecipeType.SHAPED, result, count, pattern, keys, null, false));
    }

    /**
     * Creates a shaped crafting recipe WITH load conditions (single mod)
     */
    public void createConditionalShapedRecipe(String name, Item result, int count, String[] pattern, String requiredMod, Object... keys) {
        recipes.add(new RecipeData(name, RecipeType.SHAPED, result, count, pattern, keys, new String[]{requiredMod}, true));
    }

    /**
     * Creates a shaped crafting recipe with multiple required mods
     */
    public void createConditionalShapedRecipe(String name, Item result, int count, String[] pattern, String[] requiredMods, Object... keys) {
        recipes.add(new RecipeData(name, RecipeType.SHAPED, result, count, pattern, keys, requiredMods, true));
    }

    // ==========================================
    // SHAPELESS RECIPES
    // ==========================================

    /**
     * Creates a shapeless crafting recipe WITHOUT load conditions
     */
    public void createShapelessRecipe(String name, Item result, int count, Object... ingredients) {
        recipes.add(new RecipeData(name, RecipeType.SHAPELESS, result, count, null, ingredients, null, false));
    }

    /**
     * Creates a shapeless crafting recipe WITH load conditions (single mod)
     */
    public void createConditionalShapelessRecipe(String name, Item result, int count, String requiredMod, Object... ingredients) {
        recipes.add(new RecipeData(name, RecipeType.SHAPELESS, result, count, null, ingredients, new String[]{requiredMod}, true));
    }

    /**
     * Creates a shapeless crafting recipe with multiple required mods
     */
    public void createConditionalShapelessRecipe(String name, Item result, int count, String[] requiredMods, Object... ingredients) {
        recipes.add(new RecipeData(name, RecipeType.SHAPELESS, result, count, null, ingredients, requiredMods, true));
    }

    // ==========================================
    // INTERNAL LOGIC
    // ==========================================

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        generate(); // Call generate to populate recipes

        return CompletableFuture.allOf(recipes.stream().map(recipeData -> {
            JsonObject recipe = buildRecipeJson(recipeData);
            Path path = output.getResolver(net.minecraft.data.DataOutput.OutputType.DATA_PACK, "recipe")
                    .resolveJson(Identifier.of(modId, recipeData.name));

            return DataProvider.writeToPath(writer, recipe, path);
        }).toArray(CompletableFuture[]::new));
    }

    private JsonObject buildRecipeJson(RecipeData data) {
        JsonObject recipe = new JsonObject();

        // Add Load Conditions if requested
        if (data.withLoadConditions && data.requiredMods != null && data.requiredMods.length > 0) {
            // Fabric Load Conditions
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

            // NeoForge Conditions
            JsonArray neoforgeConditions = new JsonArray();
            if (data.requiredMods.length == 1) {
                JsonObject neoforgeCondition = new JsonObject();
                neoforgeCondition.addProperty("type", "neoforge:mod_loaded");
                neoforgeCondition.addProperty("modid", data.requiredMods[0]);
                neoforgeConditions.add(neoforgeCondition);
            } else {
                // Multiple mods: use "and" condition
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

        // Recipe Type
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
        // Pattern
        JsonArray patternArray = new JsonArray();
        for (String line : data.pattern) {
            patternArray.add(line);
        }
        recipe.add("pattern", patternArray);

        // Keys
        JsonObject keyObject = new JsonObject();

        // Debug output
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

            // Handle both JsonObject and JsonArray
            if (ingredientJson instanceof JsonObject) {
                keyObject.add(keyChar, (JsonObject) ingredientJson);
            } else if (ingredientJson instanceof JsonArray) {
                keyObject.add(keyChar, (JsonArray) ingredientJson);
            }
        }
        recipe.add("key", keyObject);

        // Result
        JsonObject resultObj = new JsonObject();
        resultObj.addProperty("id", getItemId(data.result));
        if (data.count > 1) {
            resultObj.addProperty("count", data.count);
        }
        recipe.add("result", resultObj);
    }

    private void buildShapelessRecipe(JsonObject recipe, RecipeData data) {
        // Ingredients
        JsonArray ingredientsArray = new JsonArray();
        for (Object ingredient : data.keys) {
            Object ingredientJson = createIngredientJson(ingredient);

            // Handle both JsonObject and JsonArray
            if (ingredientJson instanceof JsonObject) {
                ingredientsArray.add((JsonObject) ingredientJson);
            } else if (ingredientJson instanceof JsonArray) {
                // For arrays in shapeless recipes, we just add the first item
                ingredientsArray.add((JsonArray) ingredientJson);
            }
        }
        recipe.add("ingredients", ingredientsArray);

        // Result
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
            // For arrays, create a JSON array with item objects
            JsonArray array = new JsonArray();
            for (Item item : items) {
                JsonObject obj = new JsonObject();
                obj.addProperty("item", getItemId(item));
                array.add(obj);
            }
            // Return the array itself for proper JSON structure
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
