package com.elemental_wizards.forge;

import com.elemental_wizards.forge.client.ForgeClient;
import net.elemental_wizards_rpg.ElementalMod;
import net.elemental_wizards_rpg.compat.wizards.WizardMerchantTrades;
import net.elemental_wizards_rpg.effect.ElementalEffects;
import net.elemental_wizards_rpg.entity.ModEntitiesRegistry;
import net.elemental_wizards_rpg.item.ElementalGroup;
import net.elemental_wizards_rpg.item.ElementalItems;
import net.elemental_wizards_rpg.item.armor.Armors;
import net.elemental_wizards_rpg.item.weapons.WeaponsRegister;
import net.elemental_wizards_rpg.particle.ModParticles;
import net.elemental_wizards_rpg.spell.ElementalSounds;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.resource.PathPackResources;
import net.spell_engine.api.effect.Effects;
import net.wizards.villager.WizardVillagers;

import java.util.ArrayList;

/// Forge 47 entrypoint (1.20.1 port of the NeoForge entrypoint).
///
/// Forge locks every vanilla registry outside its own `RegisterEvent` window, and on Forge 47.0-47.3 the
/// vanilla wrapper stays locked *inside* the window too, so every registration here goes through the
/// `RegisterHelper` the event hands out. See `register(RegisterEvent)`.
@Mod(ElementalMod.MOD_ID)
public final class ForgeMod {
    // FMLJavaModLoadingContext.get() is flagged for removal by late 47.x builds, but the
    // constructor-injected replacement doesn't exist on early 47.x; get() works on all of [47,).
    @SuppressWarnings("removal")
    public ForgeMod() {
        ElementalMod.init();

        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        // Explicit event classes: Forge 47's plain addListener(Consumer) infers the event type from the
        // lambda via TypeTools, which is fragile; the 4-arg overload takes it directly.
        modBus.addListener(EventPriority.NORMAL, false, RegisterEvent.class, ForgeMod::register);
        modBus.addListener(EventPriority.NORMAL, false, EntityAttributeCreationEvent.class, ForgeMod::registerAttributes);
        modBus.addListener(EventPriority.NORMAL, false, BuildCreativeModeTabContentsEvent.class, ForgeMod::buildTabContents);
        modBus.addListener(EventPriority.NORMAL, false, AddPackFindersEvent.class, ForgeMod::addPackFinders);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, VillagerTradesEvent.class, ForgeMod::onVillagerTrades);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            ForgeClient.register(modBus);
        }
    }

    private static void onVillagerTrades(VillagerTradesEvent event) {
        if (event.getType() != WizardVillagers.PROFESSION) {
            return;
        }
        WizardMerchantTrades.build().forEach((tier, factories) -> {
            var tierList = event.getTrades().get(tier.intValue());
            if (tierList != null) {
                tierList.addAll(factories);
            }
        });
    }

    /// Forge 47's `AddPackFindersEvent` only takes a `ResourcePackProvider`; the NeoForge
    /// `addPackFinders(Identifier, ...)` convenience does not exist, so the built-in pack profile is
    /// assembled by hand from the mod file's own `resourcepacks/wizard_changes` directory.
    private static void addPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() != ResourceType.SERVER_DATA) {
            return;
        }
        var modFile = net.minecraftforge.fml.ModList.get().getModFileById(ElementalMod.MOD_ID);
        if (modFile == null) {
            return;
        }
        var packPath = modFile.getFile().findResource("resourcepacks/wizard_changes");
        var packId = new Identifier(ElementalMod.MOD_ID, "wizard_changes").toString();
        event.addRepositorySource(consumer -> {
            var profile = ResourcePackProfile.create(
                    packId,
                    Text.literal("Elemental Wizards - Wizard Changes"),
                    true,
                    name -> new PathPackResources(name, true, packPath),
                    ResourceType.SERVER_DATA,
                    ResourcePackProfile.InsertionPosition.TOP,
                    ResourcePackSource.BUILTIN);
            if (profile != null) {
                consumer.accept(profile);
            }
        });
    }

    /// Forge clears the *vanilla* registry's own lock only from 47.4.0 onward, so on Forge 47.0-47.3 a
    /// plain `Registry.register` throws `Can not register to a locked registry` even inside the correct
    /// `RegisterEvent` window. Everything here therefore goes through the `RegisterHelper` the event hands
    /// out, iterating the same content `common` exposes to Fabric. The loops duplicate `common`'s
    /// `registerX()` on purpose — the workaround stays inside `forge/`.
    ///
    /// Every block is declared unconditionally: `event.register` is a no-op unless its key matches the
    /// event's registry, and Forge posts one event per registry.
    public static void register(RegisterEvent event) {
        event.register(RegistryKeys.SOUND_EVENT, helper ->
                ElementalSounds.soundsToRegister().forEach(helper::register));

        event.register(RegistryKeys.STATUS_EFFECT, helper -> {
            ElementalEffects.effectsToRegister(ElementalMod.effectsConfig.value).forEach(helper::register);
            Effects.linkEntries(ElementalEffects.entries);
            // Trailing side effect of ElementalMod.registerEffects() - the config write-back.
            ElementalMod.effectsConfig.save();
        });

        event.register(RegistryKeys.PARTICLE_TYPE, helper ->
                ModParticles.particlesToRegister().forEach(helper::register));

        event.register(RegistryKeys.ITEM, helper -> {
            ElementalItems.itemsToRegister().forEach(helper::register);
            // WeaponsRegister/Armors own the `isModLoaded` gating that appends entries before the Spell
            // Engine helper runs; calling Weapon.itemsToRegister/Armor.itemsToRegister here directly would
            // silently drop those entries.
            WeaponsRegister.itemsToRegister(ElementalMod.itemConfig.value.weapons).forEach(helper::register);
            Armors.itemsToRegister(ElementalMod.itemConfig.value.armor_sets).forEach(helper::register);
            // Trailing side effect of ElementalMod.registerItems() - the config write-back.
            ElementalMod.itemConfig.save();
        });

        // ENTITY_TYPE is event 8, ITEM is event 7. Building an EntityType needs an unfrozen registry
        // (Forge-patched `EntityType.<init>` creates an intrusive holder), so the types are built here.
        event.register(RegistryKeys.ENTITY_TYPE, helper -> {
            ModEntitiesRegistry.entitiesToRegister().forEach(helper::register);
            // Trailing side effect of ModEntitiesRegistry.registerEntities().
            ModEntitiesRegistry.registerSummonAttributes();
        });

        // The item group gets its own block: `creative_mode_tab` is event 65, `item` is event 7, so a group
        // registered from the ITEM pass would vanish with no error.
        event.register(RegistryKeys.ITEM_GROUP, helper -> {
            ElementalGroup.ELEMENTAL_WIZARD = ItemGroup.builder()
                    .icon(ElementalGroup::icon)
                    .displayName(ElementalGroup.displayName())
                    .build();
            helper.register(ElementalGroup.ELEMENTAL_WIZARD_KEY, ElementalGroup.ELEMENTAL_WIZARD);
        });
    }

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        ModEntitiesRegistry.registerEntityAttributes((entityType, builder) -> event.put(entityType, builder.build()));
    }

    private static void buildTabContents(BuildCreativeModeTabContentsEvent event) {
        var key = event.getTabKey();
        if (key.equals(ElementalGroup.ELEMENTAL_WIZARD_KEY)) {
            for (var override : WeaponsRegister.groupOverrides.keySet()) {
                removeFromTab(event, override.item());
            }
            for (var override : Armors.groupOverrides.keySet()) {
                for (var piece : override.armorSet().pieces()) {
                    removeFromTab(event, (ArmorItem) piece);
                }
            }
        }

        for (var override : WeaponsRegister.groupOverrides.entrySet()) {
            if (override.getValue().equals(key)) {
                event.accept(() -> override.getKey().item());
            }
        }
        for (var override : Armors.groupOverrides.entrySet()) {
            if (override.getValue().equals(key)) {
                for (var piece : override.getKey().armorSet().pieces()) {
                    event.accept(() -> (ArmorItem) piece);
                }
            }
        }

        if (key.equals(ItemGroups.INGREDIENTS)) {
            ElementalGroup.addItemsToIngredientItemGroup(event);
        }
    }

    /// Forge 47's `BuildCreativeModeTabContentsEvent` has no `remove(...)`/`getParentEntries()`
    /// (those are NeoForge); the backing `MutableHashedLinkedMap` is the removal seam.
    private static void removeFromTab(BuildCreativeModeTabContentsEvent event, Item item) {
        var toRemove = new ArrayList<ItemStack>();
        for (var entry : event.getEntries()) {
            if (entry.getKey().isOf(item)) {
                toRemove.add(entry.getKey());
            }
        }
        for (var stack : toRemove) {
            event.getEntries().remove(stack);
        }
    }
}
