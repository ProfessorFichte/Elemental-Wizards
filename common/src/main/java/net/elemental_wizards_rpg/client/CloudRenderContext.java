package net.elemental_wizards_rpg.client;

import net.spell_engine.entity.SpellCloud;

public final class CloudRenderContext {
    private static final ThreadLocal<SpellCloud> CURRENT = new ThreadLocal<>();

    private CloudRenderContext() {
    }

    public static void set(SpellCloud cloud) {
        CURRENT.set(cloud);
    }

    public static SpellCloud current() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }
}
