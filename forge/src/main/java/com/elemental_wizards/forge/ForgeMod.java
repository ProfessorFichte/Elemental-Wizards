package com.elemental_wizards.forge;

import com.elemental_wizards.forge.client.ForgeClient;
import net.elemental_wizards_rpg.ElementalMod;
import net.elemental_wizards_rpg.compat.wizards.WizardMerchantTrades;
import net.elemental_wizards_rpg.entity.ModEntitiesRegistry;
import net.elemental_wizards_rpg.item.ElementalGroup;
import net.elemental_wizards_rpg.item.armor.Armors;
import net.elemental_wizards_rpg.item.weapons.WeaponsRegister;
import net.elemental_wizards_rpg.spell.ElementalSounds;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
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
import net.wizards.villager.WizardVillagers;

import java.util.ArrayList;

/// Forge 47 entrypoint (1.20.1 port of the NeoForge entrypoint).
///
/// Forge locks every vanilla registry outside its own `RegisterEvent` window, so each `registerX()`
/// call sits inside the window of the registry it writes to.
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

    public static void register(RegisterEvent event) {
        // ITEM_GROUP is a vanilla-only registry (not Forge-wrapped) and stays unfrozen for the whole
        // RegisterEvent phase, so registering the group from its own window is fine.
        event.register(RegistryKeys.ITEM_GROUP, reg -> {
            ElementalGroup.ELEMENTAL_WIZARD = ItemGroup.builder()
                    .icon(ElementalGroup::icon)
                    .displayName(ElementalGroup.displayName())
                    .build();
            Registry.register(Registries.ITEM_GROUP, ElementalGroup.ELEMENTAL_WIZARD_KEY, ElementalGroup.ELEMENTAL_WIZARD);
        });
        event.register(RegistryKeys.ITEM, reg -> ElementalMod.registerItems());
        event.register(RegistryKeys.STATUS_EFFECT, reg -> ElementalMod.registerEffects());
        event.register(RegistryKeys.PARTICLE_TYPE, reg -> ElementalMod.registerParticles());
        event.register(RegistryKeys.ENTITY_TYPE, reg -> ElementalMod.registerEntities());
        event.register(RegistryKeys.SOUND_EVENT, reg -> ElementalSounds.register());
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
