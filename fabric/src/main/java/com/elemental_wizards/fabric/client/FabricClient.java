package com.elemental_wizards.fabric.client;

import net.elemental_wizards_rpg.client.ElementalClient;
import net.fabricmc.api.ClientModInitializer;

public final class FabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ElementalClient.init();
    }
}
