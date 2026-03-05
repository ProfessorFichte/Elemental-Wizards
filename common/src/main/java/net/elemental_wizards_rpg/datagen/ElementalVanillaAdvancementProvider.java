package net.elemental_wizards_rpg.datagen;

import net.elemental_wizards_rpg.item.armor.Armors;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class ElementalVanillaAdvancementProvider extends FabricAdvancementProvider {

    public record Entry(
            Identifier id,
            String title,
            String description,
            @Nullable Identifier parent,
            Item iconItem,
            AdvancementFrame frame,
            boolean showToast,
            boolean announceToChat,
            boolean hidden,
            @Nullable String background,
            Item[] requiredItems,
            @Nullable Integer experienceReward,
            @Nullable String translationKeyNamespace
    ) {
        public Entry(Identifier id, String title, String description,
                     @Nullable Identifier parent, Item iconItem, AdvancementFrame frame,
                     boolean showToast, boolean announceToChat, boolean hidden,
                     @Nullable String background, Item[] requiredItems, @Nullable Integer experienceReward) {
            this(id, title, description, parent, iconItem, frame, showToast, announceToChat, hidden,
                    background, requiredItems, experienceReward, null);
        }

        public String titleKey() {
            String ns = translationKeyNamespace != null ? translationKeyNamespace : id.getNamespace();
            return "advancements." + ns + "." + id.getPath().replace("/", ".") + ".title";
        }

        public String descriptionKey() {
            String ns = translationKeyNamespace != null ? translationKeyNamespace : id.getNamespace();
            return "advancements." + ns + "." + id.getPath().replace("/", ".") + ".description";
        }
    }

    public static final List<Entry> entries = new ArrayList<>();

    private static Entry addEntry(Entry entry) {
        entries.add(entry);
        return entry;
    }

    private static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    public static void init() {
        var elemental = Armors.elementalArmor.armorSet();
        addEntry(new Entry(
                id("equipment/elemental_robe_set"),
                "Elemental Apprentice",
                "Obtain all Elemental Apprentice Armor Set Parts",
                Identifier.of("more_rpg_content", "root"),
                elemental.chest,
                AdvancementFrame.GOAL,
                true, true, false, null,
                new Item[]{
                        elemental.head,
                        elemental.chest,
                        elemental.legs,
                        elemental.feet
                },
                null
        ));
        addEntry(new Entry(
                Identifier.of("more_rpg_content", "air_rune"),
                "Path of Wind",
                "Obtain a Air Rune",
                Identifier.of("more_rpg_content", "root"),
                Registries.ITEM.get(Identifier.of("more_rpg_classes", "storm_stone")),
                AdvancementFrame.TASK,
                true, true, false, null,
                new Item[]{ Registries.ITEM.get(Identifier.of("more_rpg_classes", "storm_stone")) },
                null,
                "rpg_classes"
        ));
        addEntry(new Entry(
                Identifier.of("more_rpg_content", "earth_rune"),
                "Path of Earth",
                "Obtain a Earth Rune",
                Identifier.of("more_rpg_content", "root"),
                Registries.ITEM.get(Identifier.of("more_rpg_classes", "terra_stone")),
                AdvancementFrame.TASK,
                true, true, false, null,
                new Item[]{ Registries.ITEM.get(Identifier.of("more_rpg_classes", "terra_stone")) },
                null,
                "rpg_classes"
        ));
        addEntry(new Entry(
                Identifier.of("more_rpg_content", "water_rune"),
                "Path of Water",
                "Obtain a Water Rune",
                Identifier.of("more_rpg_content", "root"),
                Registries.ITEM.get(Identifier.of("more_rpg_classes", "aqua_stone")),
                AdvancementFrame.TASK,
                true, true, false, null,
                new Item[]{ Registries.ITEM.get(Identifier.of("more_rpg_classes", "aqua_stone")) },
                null,
                "rpg_classes"
        ));
    }

    public ElementalVanillaAdvancementProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(RegistryWrapper.WrapperLookup wrapperLookup, Consumer<AdvancementEntry> consumer) {
        for (Entry entry : entries) {
            generateAdvancementEntry(entry, consumer);
        }
    }

    private void generateAdvancementEntry(Entry entry, Consumer<AdvancementEntry> consumer) {
        Item iconItem = entry.iconItem() != null ? entry.iconItem() : Items.BARRIER;

        var builder = Advancement.Builder.create()
                .display(
                        iconItem,
                        Text.translatable(entry.titleKey()),
                        Text.translatable(entry.descriptionKey()),
                        entry.background() != null ? Identifier.tryParse(entry.background()) : null,
                        entry.frame(),
                        entry.showToast(),
                        entry.announceToChat(),
                        entry.hidden()
                );

        builder.criterion("has_all_items", InventoryChangedCriterion.Conditions.items(entry.requiredItems()));

        if (entry.parent() != null) {
            @SuppressWarnings("deprecation")
            var builderWithParent = builder.parent(entry.parent());
            builder = builderWithParent;
        }

        if (entry.experienceReward() != null) {
            builder.rewards(AdvancementRewards.Builder.experience(entry.experienceReward()));
        }

        consumer.accept(builder.build(consumer, entry.id().toString()));
    }

    public static List<Entry> getEntries() {
        return entries;
    }
}
