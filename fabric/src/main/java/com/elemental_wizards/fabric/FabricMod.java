package com.elemental_wizards.fabric;

import net.elemental_wizards_rpg.ElementalMod;
import net.elemental_wizards_rpg.compat.wizards.WizardMerchantTrades;
import net.elemental_wizards_rpg.entity.ModEntitiesRegistry;
import net.elemental_wizards_rpg.item.ElementalGroup;
import net.elemental_wizards_rpg.item.armor.Armors;
import net.elemental_wizards_rpg.item.weapons.WeaponsRegister;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.wizards.villager.WizardVillagers;

public final class FabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        ElementalMod.init();
        registerItemGroup();
        ElementalMod.registerEffects();
        ElementalMod.registerItems();
        ElementalMod.registerEntities();
        ElementalMod.registerParticles();
        ElementalMod.registerSounds();

        registerEntityAttributes();
        registerItemGroupContent();
        registerBuiltinResourcePack();
        registerWizardTrades();
    }

    private void registerWizardTrades() {
        if (WizardVillagers.PROFESSION == null) {
            return;
        }
        WizardMerchantTrades.build().forEach((tier, factories) ->
                TradeOfferHelper.registerVillagerOffers(WizardVillagers.PROFESSION, tier, list -> list.addAll(factories)));
    }

    private void registerBuiltinResourcePack() {
        FabricLoader.getInstance().getModContainer(ElementalMod.MOD_ID).ifPresent(modContainer ->
                ResourceManagerHelper.registerBuiltinResourcePack(
                        Identifier.of(ElementalMod.MOD_ID, "wizard_changes"),
                        modContainer,
                        ResourcePackActivationType.ALWAYS_ENABLED
                ));
    }

    private void registerEntityAttributes() {
        ModEntitiesRegistry.registerEntityAttributes(FabricDefaultAttributeRegistry::register);
    }

    private void registerItemGroup() {
        ElementalGroup.ELEMENTAL_WIZARD = FabricItemGroup.builder()
                .icon(ElementalGroup::icon)
                .displayName(ElementalGroup.displayName())
                .build();
        Registry.register(Registries.ITEM_GROUP, ElementalGroup.ELEMENTAL_WIZARD_KEY, ElementalGroup.ELEMENTAL_WIZARD);
    }

    private void registerItemGroupContent() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ElementalGroup::addItemsToIngredientItemGroup);

        for (var override : WeaponsRegister.groupOverrides.entrySet()) {
            var item = override.getKey().item();
            var key = override.getValue();
            ItemGroupEvents.modifyEntriesEvent(ElementalGroup.ELEMENTAL_WIZARD_KEY).register(content -> {
                content.getDisplayStacks().removeIf(stack -> stack.isOf(item));
                content.getSearchTabStacks().removeIf(stack -> stack.isOf(item));
            });
            ItemGroupEvents.modifyEntriesEvent(key).register(content -> content.add(item));
        }

        for (var override : Armors.groupOverrides.entrySet()) {
            var pieces = override.getKey().armorSet().pieces();
            var key = override.getValue();
            ItemGroupEvents.modifyEntriesEvent(ElementalGroup.ELEMENTAL_WIZARD_KEY).register(content -> {
                content.getDisplayStacks().removeIf(stack -> pieces.stream().anyMatch(p -> stack.isOf((ArmorItem) p)));
                content.getSearchTabStacks().removeIf(stack -> pieces.stream().anyMatch(p -> stack.isOf((ArmorItem) p)));
            });
            ItemGroupEvents.modifyEntriesEvent(key).register(content -> {
                for (var piece : pieces) {
                    content.add((ArmorItem) piece);
                }
            });
        }
    }
}
