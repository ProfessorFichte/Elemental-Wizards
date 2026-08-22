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

/// Registry of Elemental Wizards' own particles.
///
/// Ported from the V1 hand-written particle classes to Spell Engine 1.10's single
/// generic factory: each entry is a texture plus the defaults of the particle drawn
/// from it, and [net.spell_engine.client.particle.SpellParticle] resolves entry
/// defaults + per-spawn `ParticleGroup.Appearance` payload into the final look.
///
/// This is what makes colour / scale / motion set on a `ParticleGroup` actually apply
/// to these ids — a vanilla or hand-written factory ignores the payload entirely.
///
/// Sprites still come from `assets/elemental_wizards_rpg/particles/<name>.json`; the
/// [Texture] frame count here only drives the entry's natural lifetime.
///
/// ### Lifetime
/// An animated entry lives for its frame count, stretched by `playbackSpeed`:
/// `effective maxAge = frames / playback_speed`. Single-frame entries take
/// `lifetime(ticks)` instead. Each entry below reproduces the V1 `maxAge` that way —
/// the number in the comment is the V1 value.
///
/// ### Scale
/// V1 mixed two conventions and they do *not* mean the same thing: `this.scale = X`
/// was absolute, while `this.scale(X)` *multiplied* vanilla's random `0.1..0.2` base
/// (mean `0.15`). V2 `scale` is absolute, so the latter ports as `0.15 * X` with the
/// base's own +/-33% as variance. The one entry here used the absolute form.
public class ModParticles {

    private static final List<Entry> entries = new ArrayList<>();

    /// Every entry owned by this mod. Registered here (server + client) and bound to
    /// the generic factory per platform — see `ElementalClient`.
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

    /// V1 `HealingRainParticle`, a hand-written `SpriteBillboardParticle` drawn from the
    /// three vanilla drip frames its particle json names (`drip_hang`, `drip_fall`,
    /// `drip_land`) — `setSpriteForAge` walked them across the particle's life, which is
    /// exactly what the generic factory does for an animated entry.
    ///
    /// Field by field:
    /// - `PARTICLE_SHEET_TRANSLUCENT` and no `getBrightness` override → the default
    ///   `Render.TRANSLUCENT`, world-lit, hence `glow(false)`.
    /// - `this.scale = 0.15 + rand * 0.1` — the absolute form, i.e. `0.15..0.25`:
    ///   `scale(0.2F, 0.25F)`.
    /// - `this.maxAge = 20 + rand(20)` → `20..39`, mean `29.5`. Three frames stretched
    ///   over that mean is `playbackSpeed = 3 / 29.5`, with the spread as
    ///   `lifetimeVariance(9.5 / 29.5)`.
    /// - `this.gravityStrength = 0.06`, but its `tick` override subtracted that value
    ///   *raw* where vanilla subtracts `0.04 * gravityStrength` — so the V2 equivalent
    ///   of the same fall is `gravity(0.06 / 0.04)`.
    /// - `velocityX/Z *= 0.98` per tick → `drag(0.98F)`.
    /// - the constructor keeps the velocity it was handed verbatim (it overwrites what
    ///   vanilla's 7-arg `Particle` constructor randomised), which is `Motion.STATIC`,
    ///   the default.
    ///
    /// Approximations, all in `tick`/colour and none reachable through `Appearance`:
    /// - **Per-channel colour jitter.** V1 rolled `red 0.40..0.50`, `green 0.80..0.95`,
    ///   `blue 1.0` independently. `color_variance` is a single darkening factor applied
    ///   to all three, so it cannot reproduce a per-channel spread; the entry takes the
    ///   mean tint `0x73dfff` flat.
    /// - **Opacity jitter.** `alpha = 0.7 + rand * 0.3`; there is no opacity variance,
    ///   so the mean `0.85` is used. The last-5-tick fade (`alpha -= 0.15` per tick, from
    ///   `~0.85` down to `~0.1`) becomes a linear `fadeOut` holding for `24.5 / 29.5` of
    ///   the life — a full fade to zero rather than V1's stop just short of it.
    /// - **The `-0.2` spawn kick.** V1 subtracted `0.2` from the y velocity it was given.
    ///   A batch supplies velocity; the appearance cannot bias it, so the drop starts at
    ///   whatever the emitter hands it. `HealingRainCloudEntity` already spawns these with
    ///   a downward velocity of its own.
    /// - **Dying on contact.** V1 called `markDead()` on `onGround`, so a drop *popped* the
    ///   instant it reached the floor. `Appearance` has no die-on-contact switch. The first
    ///   port reached for `collides(true)`, which is the wrong half of the behaviour: the
    ///   drop stops dead on the floor and then lies there for the rest of its ~30 tick life,
    ///   reported in game as rain "lingering like a pebble". `collides(false)` is the closer
    ///   match — the drop keeps falling through the floor, so it is occluded by the ground
    ///   block and simply disappears at the surface, which is what popping looked like.
    ///   (It is still alive underground, and would be visible from inside an open cave below.)
    /// - **Undamped fall.** V1's `0.98` damping was applied to x and z only; `drag` is a
    ///   scalar over all three axes, so the descent bleeds slightly more speed than V1's.
    ///   With gravity this steep the terminal velocity is still ~3 blocks/tick, well past
    ///   anything reached inside a 30 tick life.
    public static final Entry HEALING_RAIN = add("healing_rain", 3, p -> p
            .glow(false).color(Color.from(0x73dfff).toRGBA())
            .scale(0.2F, 0.25F).opacity(0.85F, Easing.Curve.fadeOut(Easing.LINEAR, 0.83F))
            .gravity(1.5F).drag(0.98F)
            .playbackSpeed(0.102F).lifetimeVariance(0.32F));          // V1 maxAge 20..39

    public static void register() {
        for (var entry: entries) {
            Registry.register(Registries.PARTICLE_TYPE, entry.id(), entry.type());
        }
    }
}
