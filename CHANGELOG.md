# 3.1.1+1.20.1

> ### ⚠️ Read this before updating
>
> This release is a **major technical overhaul and is not backwards compatible.**
>
> - **Requires the matching Spell Engine and More RPG Library releases.** This version will not run
>   on Spell Engine **0.9.x**, and mods built against 0.9.x will not work alongside it.
> - **Update the whole set together.** Spell Engine, More RPG Library and every RPG Series mod must
>   be on matching versions. Mixing in an older add-on will break at startup or misbehave in play.
> - **Spell books must be re-obtained.** Spell books from an older world no longer carry valid
>   spell data. Re-craft them, or re-bind their spells at the Spell Binding Table.
>
> **Back up your world before updating.**

- Thanks to Daedelus for the PR!
- Ported to Minecraft 1.20.1 (Fabric + Forge 47). NeoForge is replaced by Forge on this line; the same
  Forge jar also loads on NeoForge 1.20.1.
- Requires the matching 1.20.1 releases of Spell Engine (1.10.5), Spell Power (1.6.0), More RPG Library
  (2.7.2), Armor Model API (1.0.0) and Wizards (3.1.2).
- Forge-specific rebuild: own entrypoint, `mods.toml`, manifest mixin config, and a hand-assembled pack
  finder for the built-in `wizard_changes` data pack (Forge 47 has no `addPackFinders(Identifier, ...)`).
- Every registry write goes through Forge's `RegisterEvent` window, so the mod also boots on Forge
  47.0-47.3 and on NeoForge 1.20.1, which never unlock the vanilla registries.
- The Hurricane, Mountain and Ocean robes are now always registered, so a server without Armory RPGs
  starts (their set bonuses used to fail to load and abort the startup). Crafting these sets still
  requires Armory RPGs.
- Wizard-merchant trades are built on the public trade constructor instead of the package-private vanilla
  trade factories; prices, counts, uses, XP and enchant rolls are unchanged.

### Accepted 1.20.1 limitations

- `minecraft:wind_charge` does not exist on 1.20.1. The four wind-armor recipes, the wind staff recipe and
  the matching unsmelt outputs use `minecraft:feather` instead, which makes the wind set the cheapest of
  the four.
- 1.20.1 has no jump-strength attribute for players, so **Impaled no longer prevents jumping**; it keeps
  only its movement-speed lock.
- 1.20.1 has no entity-scale attribute, so the Impaled spike ring is drawn at a fixed scale instead of
  scaling with the victim.
- The vanilla `head_armor`/`chest_armor`/`leg_armor`/`foot_armor` item tags are 1.21-only; the robes are
  tagged `minecraft:trimmable_armor` instead.
