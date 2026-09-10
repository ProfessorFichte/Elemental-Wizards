package net.elemental_wizards_rpg.spell;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
            this(new Identifier(MOD_ID, name));
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
    public static final Entry GOLEM_DEATH = add(new Entry("golem_death"));
    public static final Entry GOLEM_GROWL = add(new Entry("golem_growl").variants(4));
    public static final Entry STONE_FLESH = add(new Entry("stone_flesh"));
    public static final Entry TORNADO_IDLE = add(new Entry("tornado_idle"));

    public static void register() {
        for (var entry : entries) {
            entry.entry = Registry.registerReference(Registries.SOUND_EVENT, entry.id(), entry.soundEvent());
        }
    }

    /// Creation only — every sound that still needs registering, keyed by the id it registers under. A
    /// loader that registers sounds itself (Forge) iterates this instead of calling {@link #register}.
    ///
    /// There is deliberately no `linkEntries()` companion: nothing outside this class reads
    /// {@link Entry#entry()}, so the `RegistryEntry` the Fabric path gets for free is not reproduced.
    /// (Grep `.entry()` before adding one — Paladins needs it, Wizards does not, and neither does this mod.)
    public static Map<Identifier, SoundEvent> soundsToRegister() {
        var sounds = new LinkedHashMap<Identifier, SoundEvent>();
        for (var entry : entries) {
            if (entry.entry != null || Registries.SOUND_EVENT.containsId(entry.id())) { continue; }
            sounds.put(entry.id(), entry.soundEvent());
        }
        return sounds;
    }
    
}
