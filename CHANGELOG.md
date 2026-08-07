# 3.0.2 - 1.21.1
- Armory & Arsenal Compat Equipment is now added to a separate Item Group
- Remove old loot Tables causing an error message in the Logs - GH #12
- Add some sound effects for the Earth Golem & the Tornado

# 3.0.1 - 1.21.1
- Converted the Tornado Spell to a Spell Cloud instead of an Entity
- Also, the Tornado received a new Spawning & De-Spawning Animation
- Attempt to fix a crash on Neoforge, caused by the TerraStoneEntity
- removed the custom knock up spell impacts and use spell engines new velocity impact
- improved the earth golem's spell casting behaviour
- Added uk_ua localization- #11 - thx Ch1sho

# 3.0.0 - 1.21.1
- Adapt to Spell Engine 1.9.10+ API Changes
**Balancing & Internal Changes:**
- Earthquake: now is a channeled spell, increasing it's range the longer you channel them.
- Earth Golem: Completely uses Spell Engine's Summoned Entity System now.  It can now also cast the Stone Throw Ability.
- Improved the behaviour of many Custom Spell Entities.
- The Earth Golem will receive some additional Animations soon

# 2.6.8 - 1.21.1
- Improved the Bubble Model (Thx Slepykat)
- Fixed squished Healing Rain Entity Model

# 2.6.7 - 1.21.1
- Attempt to fix Neoforge Server Crash due to Entities Ticking
- Change Project Suffix to RPG Series Plus

# 2.6.6 - 1.21.1
- Fixed a client crash when leaving a server due to a wrong registered sound id

# 2.6.5 - 1.21.1
- Added ja_jp.json - Thanks Anpan715
- changed the tornado texture (a bit brighter and some transparent parts) - Ty Slepykat
- Fixed the rendering issue where you could see clouds through the tornado model
- Added a small Heal Impact to the Waterball spell
- Fixed Bubble Foam's Pushback Mechanism not working on players if a mob has the effect
- Fixed Stone Flesh Effect Renderer not working on Mob-Entities
- Added a small emerge animation for the ImpaleEffect Renderer

# 2.6.4 - 1.21.1
- Fixed Stormdraft only damaging the first target it hit
- Fixed the golems summoned stone spike renderer issue on neoforge

# 2.6.3 - 1.21.1
- Fixed that the Summoned Earth Golem stops attacking other entities after it killed one
- Fixed that the Summoned Earth Golem Spikes deal to fast multiple damage to a target
- The Earth Golem can now move up 1 Block high to not get stuck behind
- Now when the Earth Golem de-spawns a sound is played and dust & stone particles are spawned
- There is now a small damage and knockback area around the Earth Golem when he is smashing the ground 
- Added a visual Status Effect Model renderer with an Animation for the Stone Flesh Status Effect
- Also, if the player has 100% health, the player model is completely rendered with the dripstone-block texture
- Also, I gave the BubbleFoam Status Effect a new visual animation and renderer!
- Increased the Damage Window for the Stormdraft spell

# 2.6.2 - 1.21.1
**Elemental Wizards Spell Expansion:**
- New Spell Choices for the Spell Tier's 2-4 for every Elemental Wizard Spell Book!
- **Earth:** T2: Impale T3: Shattering Stones (Old LNE Spell) T4: Stone Golem
- Impale: Impales a target with a sharp stone, blocking its movement and dealing damage to it.
- Shattering Stones: Launch a stone that shatters on impact, sending fragments in all directions that cause bleeding wounds.
- Stone Golem: Summons a Earth Golem, that smashes the ground and summons damaging spikes that deal damage and knock up on contact.
- **Water:** T2: Waterballs T4: Tidal Wave
- Waterballs: Shoots multiple water balls that deal {damage} damage.
- Tidal Wave: Tidal Wave that travels back and forth and deals damage and knocks targets back.
- **Wind:** T2: Twister T3: Windfield T4: Storm Draft
- Twister: Launch a circling-moving whirlwind, that knock's back targets it hits and damages them.
- Windfield: Calls a field of strong wind that deals damage and reduces movement speed.
- Storm Draft: Channel bursts of high pressure air that deal damage and shortly stun enemies.
- **Huge thanks to Slepykat for the new Projectile- & Effect- and Entity Models <3**
**Fixes and Changes**
- Moved the LNE-Extra Spells to the LNE-Wizards Mod (Explosive Bubble & Aeroburst)
- Improved the Tornado Model (by Slepykat) 
- Fixed some Missing Weapon Recipes
- Internal Changes for some Spell Spawned Entities to improve their behaviour
- Improved the Spell Textures for some of the existing Earth Wizard Spell Icons
- Cleaned up the Code

# 2.6.1 - 1.21.1
- Fix Neoforge not loading (Did not Register the Entity Attributes on NeoForge)
- Fixed some model and particle renderer issues on NeoForge

# 2.6.0 - 1.21.1
**Update to use Spell Engine 1.9.0**
- DISCLAIMER: All spell books and spell scrolls will be reset, due to major API changes.
- The Elemental Wizard Spell Books now offer 3 spells only, to match other classes
- Splash, Gust & Stone Throw are not in the Spell Book any longer, they're Weapon Spells for Wand's
- Water Whip, Wind Cutter & Stone Spear are not in the Spell Book any longer, they're Weapon Spells for Stave's
- The Elemental Staff, Avatar's Staff & Valkyrie Elementalist Staff can now choose between all three Elemental Staves Weapon Spells
- Hydro Beam is no longer the Tier 4 Spell of the Water Wizard, it is now a Tier 3 choice with the Springwater Spell
- Hydro Beam's Damage was nerfed and knockback buffed
- New Tier 4 Water Wizard Spell: **Healing Rain Cloud**, a new Entity that is following the caster or the Ally with the lowest health.
- The Healing Rain Cloud deals damage to targets and heals allies that are below the cloud entity standing in the rain
- All Heal coefficients of Water Spells where slightly nerfed
- The following spells where changed from Spell Cloud Entities to custom Entities: Terra Circle, Tornado, Earthquake
- Earthquake and Terra Circle received some new renderers for better visuals
- Enemies cant move through the Terra Circle Entities now
- The Tornado Entity will now pull enemies in its center
- The Tornado & Terra Circle can now be spawned at the targets location
- I've added Spell Book Descriptions in the Spell Binding table
- Already added some internal code, for future extra Spells
- The Elemental Avatar Passive from the Avatar's Staff will now show in the tooltip which spell will trigger.

# 2.5.4 - 1.21.1
- fix Stone Throw and Stone Spear not damaging Targets around correctly
- Update to newest MRPG-Lib version

# 2.5.3 - 1.21.1
- fix minecraft armor tags not loading when armory compat sets are not loading

# 2.5.2 - 1.21.1
- Buff Weapon Spell Power
- Add T5 Armor Sets (Assets by Slepykat)

# 2.5.1 - 1.21.1
- Re-Balance some of the armor attributes
- Change some LNE-Wizards Biome Tags
- Add Avatar's Staff to "Crystal Loot Theme"
- Nerf Terra Circle again

# 2.5.0 - 1.21.1
- Move to Architectury Enviroment for Multiloader
- NeoForge Beta!
- Changed Spell Book Texture to be more in line with the RPG-Series Books (by SirGhaith, Thanks!)
- Improve Armor Model Assets (by Slepykat, Thanks!)
- change some inventory item textures to match their new asset looks
- rename spell books to "Tome of..." to match the original wizards mod names
- fixed missing spell assignments
- improve some water spell textures
- Add new Terra Circle Dripstone Models
- Fix wrong Air Cutter Description
- fix unused sprite error with storm_layer texture

# 2.4.11 - 1.21.1
### New Content
- **Added a new Tier 5 Staff!**
- [DISCLAIMER] Only available if Arsenal (RPG-Series) is installed or the config is enabled
- Staff: The Avatar
- New Passive: The Staff casts a unique passive spell, depending on your highest spell power element
### Internal Changes
- Explosive Bubbles, Shattering Stone & Aeroburst are now T4 Spells (Only important if LNE-Wizards is installed)
- Nerf the Springwater Spell a bit (Had a lot of impacts)
- already added Armory Compat Code (just awaiting Armor Model & Texture for release)
- again change the Terra Circle -> Its less scattered and more dense, but is more compact now
- nerfed Earthquake range
- removed the trembling effect, Earthquake now uses custom spell impact from the MRPGLib

# 2.4.10 - 1.21.1
- Update to MRPGLib 2.3.0
- Use new Custom Impact Types from MRPGLib
- Delete Soaked Effect, is now registered via MRPGLib

# 2.4.9 - 1.21.1
- Spell Engine 1.7 Update
- Changed Terra Circle Spell a bit, outer circles now have a delay and the spawn locations got rearranged
- Add missing recipe for Elemental Staff

# 2.4.8 - 1.21.1
- fix projectile spells

# 2.4.7 - 1.21.1
- Reduce Water Wizard Particles due to server lag
- Improve TornadoEffect a bit

# 2.4.6 - 1.21.1
- fix some spell animations
- fix earth tower biome tag for LNE-Add-On
- Fix some spells, which could damage the caster

# 2.4.5 - 1.21.1
- fix some target modifiers in Passive Spell Impact

# 2.4.4 - 1.21.1
- Update for newest Spell Engine API
- fix elemental wizard stuff loot injection in villager wizard chest
- Fix Springwater Spell Tier
- Add LNE related data & spells

# 2.4.3 - 1.21.1
- add armor meta type tags
- add weapon type tags for damage wands

# 2.4.2 - 1.21.1
- Add Spell Scroll Textures
- Add smelting recipes for disassembling weapons and armor pieces
- Add Datagen

# 2.4.1 - 1.21.1
- The Dripstone Circle Spell can now also be casted on the targets location, with the casters location as fallback
- Spell Engine 1.6 Update
- changed some tags
- inject elemental wizards loot in Wizard Villager Chest

# 2.4.0 - 1.21.1
- Spell Engine 1.5 Update
- deleted unused old spells
- bubble beam will now only give the shield to the caster
- removed custom spell impacts, now every spell works with spell engine api options
- increased trembling effect amplifier stun conversion
- tweaked some cooldowns
- [BREAKING FOR TEXTURE PACKS] Clean up item texture folder
- Add Elementalist Staff with all 3 elemental Spell Powers
- Re-Textured and recolored some Water Mage Spells
- Re-Textured all Item-Textures
- Changed the elementalist armor texture a bit
- Add TweaksConfig, so special Weapons can also be used without Better End & Nether and the Aether
- Update to Fabric Loom 1.9
- The Tornado can now be spawned on the target, with the caster as a fallback

# 2.3.1 - 1.21.1
- Wind related Spell Effect Conditions, like the Tornado knock up will not work on bosses "#c:bosses"
- Earthquake Spell Effect Conditions, like the Tornado knock up will not work on bosses "#c:bosses"
- The Soaked Effect cant be applied on entities with the tag "more_rpg_classes:resistant_to_to_water_spells"
- Dripstone Circle and Stonespear Bleeding Effect cant be applied on "#minecraft:undead"

# 2.3.0 - 1.21.1
**- Spell Engine 1.4 Update!**
- AzureLib Armor 3.0 Update!
- Water Spells now always crit against Fire themed Mobs (via Entity Type Tag: "more_rpg_classes:vulnerable_to_water_spells")
- Water Spells deal 30% less damage against Underwater themed Mobs (via Entity Type Tag: "more_rpg_classes:resistant_to_to_water_spells")

2.2.1 - 1.21.1
- Update for Spell Scrolls

2.2.0
- Spell Engine 1.2 Update
- added the Valkyrie Elementalist Staff (Aether Variant)
- made the outlines for the Wind Staff a bit darker for better contrast

2.1.1
- forgot to add minecraft armor tags for enchanting

2.1.0
- Added 3rd tier Armor, Netherite Armor for Elemental Wizards
- Changed balancing of Weapons & Armor, to match the new RPG Series balancing

2.0.2
- nerfed Dripstone Circles Damage Multiplier and impact time
- fixed the missing Wind Spell Book Advancement

2.0.1
- Netherite Wind Staff & Wand recipe had a typo
- nerfed Air Cutter ang Gust Knockback a bit

2.0.0
- 1.21.x Release!