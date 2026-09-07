package com.elemental_wizards.neoforge;

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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.wizards.villager.WizardVillagers;

@Mod(ElementalMod.MOD_ID)
public final class NeoForgeMod {
    public NeoForgeMod(IEventBus modBus) {
        ElementalMod.init();
        modBus.addListener(RegisterEvent.class, NeoForgeMod::register);
        modBus.addListener(EntityAttributeCreationEvent.class, NeoForgeMod::registerAttributes);
        modBus.addListener(BuildCreativeModeTabContentsEvent.class, NeoForgeMod::buildTabContents);
        modBus.addListener(AddPackFindersEvent.class, NeoForgeMod::addPackFinders);
        NeoForge.EVENT_BUS.addListener(VillagerTradesEvent.class, NeoForgeMod::onVillagerTrades);
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

    private static void addPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() != ResourceType.SERVER_DATA) {
            return;
        }
        event.addPackFinders(
                Identifier.of(ElementalMod.MOD_ID, "resourcepacks/wizard_changes"),
                ResourceType.SERVER_DATA,
                Text.literal("Elemental Wizards - Wizard Changes"),
                ResourcePackSource.BUILTIN,
                true,
                ResourcePackProfile.InsertionPosition.TOP
        );
    }

    public static void register(RegisterEvent event) {
        event.register(RegistryKeys.ITEM_GROUP, reg -> {
            ElementalGroup.ELEMENTAL_WIZARD = ItemGroup.builder()
                    .icon(ElementalGroup::icon)
                    .displayName(ElementalGroup.displayName())
                    .build();
            Registry.register(Registries.ITEM_GROUP, ElementalGroup.ELEMENTAL_WIZARD_KEY, ElementalGroup.ELEMENTAL_WIZARD);
        });
        event.register(RegistryKeys.ITEM, reg -> {
            ElementalMod.registerItems();
        });
        event.register(RegistryKeys.STATUS_EFFECT, reg -> {
            ElementalMod.registerEffects();
        });
        event.register(RegistryKeys.PARTICLE_TYPE, reg -> {
            ElementalMod.registerParticles();
        });
        event.register(RegistryKeys.ENTITY_TYPE, reg -> {
            ElementalMod.registerEntities();
        });
        event.register(RegistryKeys.SOUND_EVENT, reg -> {
            ElementalSounds.register();
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
                event.add(override.getKey().item());
            }
        }
        for (var override : Armors.groupOverrides.entrySet()) {
            if (override.getValue().equals(key)) {
                for (var piece : override.getKey().armorSet().pieces()) {
                    event.add((ArmorItem) piece);
                }
            }
        }

        if (key.equals(ItemGroups.INGREDIENTS)) {
            ElementalGroup.addItemsToIngredientItemGroup(event);
        }
    }

    private static void removeFromTab(BuildCreativeModeTabContentsEvent event, Item item) {
        var toRemove = new java.util.ArrayList<ItemStack>();
        for (var stack : event.getParentEntries()) {
            if (stack.isOf(item)) {
                toRemove.add(stack);
            }
        }
        for (var stack : event.getSearchEntries()) {
            if (stack.isOf(item)) {
                toRemove.add(stack);
            }
        }
        for (var stack : toRemove) {
            event.remove(stack, ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);
        }
    }
}
