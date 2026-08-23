package net.elemental_wizards_rpg.particle;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.spell_engine.api.spell.fx.Easing;
import net.spell_engine.api.spell.fx.ParticleGroup.Appearance;
import net.spell_engine.client.util.Color;
import net.spell_engine.fx.SpellEngineParticles.Entry;
import net.spell_engine.fx.SpellEngineParticles.Texture;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class ModParticles {

    private static final List<Entry> entries = new ArrayList<>();

    public static List<Entry> entries() {
        return entries;
    }

    private static Entry add(String name, int frames, Consumer<Appearance> defaults) {
        var entry = new Entry(Identifier.of(MOD_ID, name),
                new Texture(Identifier.of(MOD_ID, name), frames))
                .defaults(defaults);
        entries.add(entry);
        return entry;
    }

    public static final Entry HEALING_RAIN = add("healing_rain", 3, p -> p
            .glow(false).color(Color.from(0x73dfff).toRGBA())
            .scale(0.2F, 0.25F).opacity(0.85F, Easing.Curve.fadeOut(Easing.LINEAR, 0.83F))
            .gravity(1.5F).drag(0.98F)
            .playbackSpeed(0.102F).lifetimeVariance(0.32F));

    public static void register() {
        for (var entry: entries) {
            Registry.register(Registries.PARTICLE_TYPE, entry.id(), entry.type());
        }
    }
}
