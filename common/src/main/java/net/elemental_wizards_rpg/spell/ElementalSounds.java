package net.elemental_wizards_rpg.spell;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class ElementalSounds {
    public static final class Entry {
        private final Identifier id;
        private final SoundEvent soundEvent;
        private RegistryEntry<SoundEvent> entry;
        private int variants = 1;

        public Entry(Identifier id, SoundEvent soundEvent) {
            this.id = id;
            this.soundEvent = soundEvent;
        }

        public Entry(String name) {
            this(Identifier.of(MOD_ID, name));
        }

        public Entry(Identifier id) {
            this(id, SoundEvent.of(id));
        }

        public Entry travelDistance(float distance) {
            return new Entry(id, SoundEvent.of(id, distance));
        }

        public Entry variants(int variants) {
            this.variants = variants;
            return this;
        }

        public Identifier id() { return id; }
        public SoundEvent soundEvent() { return soundEvent; }
        public RegistryEntry<SoundEvent> entry() { return entry; }
        public int variants() { return variants; }
    }

    public static final List<Entry> entries = new ArrayList<>();

    public static Entry add(Entry entry) {
        entries.add(entry);
        return entry;
    }

    public static final Entry TIDAL_WAVE_IMPACT = add(new Entry("tidal_wave_impact"));
    public static final Entry TIDAL_WAVE_RELEASE = add(new Entry("tidal_wave_release"));
    public static final Entry WATER_WHIP_IMPACT = add(new Entry("water_whip_impact"));
    public static final Entry WATER_WHIP_RELEASE = add(new Entry("water_whip_release"));
    public static final Entry TWISTER_LAUNCH = add(new Entry("twister_launch"));
    public static final Entry WIND_ENTITY_LOOP = add(new Entry("wind_entity_loop"));
    public static final Entry STORM_DRAFT_LAUNCH = add(new Entry("storm_draft_launch"));
    public static final Entry EARTH_SUMMON = add(new Entry("earth_summon"));
    public static final Entry GOLEM_GROUND_SLAM = add(new Entry("golem_ground_slam"));
    public static final Entry GOLEM_SPIKE_SUMMON = add(new Entry("golem_spike_summon"));
    public static final Entry GOLEM_GROWL = add(new Entry("golem_growl").variants(4));
    public static final Entry STONE_FLESH = add(new Entry("stone_flesh"));

    public static void register() {
        for (var entry : entries) {
            entry.entry = Registry.registerReference(Registries.SOUND_EVENT, entry.id(), entry.soundEvent());
        }
    }
    
}
