> [English](README.md) | [繁體中文](README-zh.md)

# Destroy the Core III

—— A Minecraft mini-game without a single command!

[//]: # (<img src="screenshot.png" alt="Screenshot" />)

## Introduction

The DTC I & II, created by Hageeshow (`Hageeshow`), were once hugely popular.

However, using data packs has various drawbacks. After all, they are officially designed by Mojang for players, which means many things require going around in circles, or are simply impossible.
The most significant problem is that its API is revised every time Minecraft updates, causing the created data packs to become obsolete.

晴 (`_hueeey_`) played the modified version of DTC II by `just_like_bear` and `ItzYoyo`, among others.
Decided to remake the game as a plugin, and thus DTC III was born.

A brief introduction to the differences between DTC III and DTC II:
- Fast computation
> Paper + plugin may be more than 10 times faster than Vanilla + data pack
- Profession titles
> Such as `[Guardian] Hi_Chocolate`
- Information sidebar
> Includes profession, stage, faction health, etc.
- Statistics
> Displays wins, kills, attacks, and mining counts
- Translation system
> Currently supports English and Traditional Chinese
- Map system
> Supports dynamic map loading and automatically resets after each game
- More details and various particle effects
> Mineral cooldowns have block breaking animations
> Many items have a magical aura-like glow effect
- Team-shared Ender Chest

## For Players

Here, I directly copy the [introduction of DTC II](https://forum.gamer.com.tw/C.php?bsn=18673&snA=173606)

- Map Name: DTC III
- Number of Players: Recommended at least 4v4, at most... I don't know XD (But of course, you can also do 1v1...)
- Game Time: Ends within 1 hour, may be faster depending on the intensity of player attacks
- Version Recommendation: 1.21.4+

### Map Download

No map is currently provided

### How to Win

DTC III is divided into two factions: Red Team and Green Team
The player's mission is to protect their own faction's core and destroy the enemy faction's core
Victory is achieved when the enemy faction's health reaches zero

### Map Introduction

(To be added later)

### Faction Core

The core is that Ender Stone, containing powerful energy, and nearby enemy faction members will receive mining fatigue
As long as the Ender Stone is mined, the faction's health will be directly reduced
At the same time, the miner will gain 30 seconds of glowing due to the core's energy leaking onto them, and increase their sin value by 3 points
Water, lava, and obsidian near the core will be directly absorbed by the core

### Mineral System

The mineral system is different from DTC II. Iron and gold are not automatically smelted (but the speed of the furnace is 10× that of vanilla Minecraft)
After a mineral is mined, it will turn into bedrock for cooling, and the mineral will regenerate after the time is up

| Resource | Use |
|---|---|
| Iron Ingot | Crafting equipment and tools / Buying potions |
| Gold Ingot | Buying food / Exchanging for other minerals |
| Redstone | Buying bows and arrows |
| Lapis Lazuli | Enchanting |
| Emerald | Buying special items and tokens |
| Diamond | Crafting equipment and tools |
| Coal | Gaining experience points |

In the default map, iron is in the left mine, diamonds are under the central river, and others are in the right mine

### Game Stages

Each stage is 10 minutes:

- Stage 1: Core Invincible
> The core cannot be attacked in this stage, so collect as many resources as possible to prepare for battle
- Stage 2: Exchange Opens
> In this stage, you can start destroying the core
> Villagers are also open for trading
- Stage 3: Random Mission
> At this time, a random mission will appear every 2 minutes. Completing the mission can earn rewards or give penalties to the enemy
> At the same time, the diamond mine in the center of the map is open for mining
- Stage 4: Death Penalty
> The cooldown time of minerals will be halved
> Dying will deduct 1 point from the faction's health
- Stage 5: Core Double Damage
> Originally, destroying the core only dealt 1 point of damage, but in this stage, it will deduct 2 points
- Final Stage: Core Wither
> This is the final stage. To speed up the game, both sides' cores will lose health every 15 seconds

### Sin Value

Sin value is a new system added in DTC II, which is simply the respawn cooldown time
The initial sin value for each stage is different, namely 5, 8, 11, 14, 17, 20
Killing a player will gain 2 sin points, and destroying the core will gain 3 sin points
The minimum will not be lower than 5 points, and the maximum will not exceed 180 points

In order to prevent the defenders from killing people all the time, resulting in too high a sin value, here is a method to reduce the sin value
As long as you crouch near the core and feel the core energy, your sin value will slowly recover after a certain period of time, and it will also give you a little experience

### Item Drop System

After all, this is a battle game, and players will inevitably die in the game, so it is designed so that things will not all drop out after death
But there is a 10% chance that equipment or items on the body will disappear
There are a total of 36 slots on the player's body, plus 4 equipment slots, for a total of 40 slots of items. On average, 4 slots of items will disappear each time you die and be replaced with Chaos Matter

Enchanting tables and Ender Chests will definitely drop, while shields and skill books will not

### Other Settings

#### Killing

Killing a player will increase the sin value by 2 points, and gain five seconds of Weakness II, Glowing, Blindness, and Slowness
Killing 10 people in a row will become the Killing Leader, and killing them will reward 10 emeralds

#### Invisibility

Invisibility will be canceled when taking more than 1 heart of damage

#### Miscellaneous

- Luck effect will double the chance of mineral drops
- Opening the Ender Chest will be a large team-shared chest
- Blocks cannot be placed near the respawn waiting area and respawn point
- Players above the height of the respawn waiting area will get altitude sickness, triggering freezing and nausea effects
- Pistons cannot push minerals, wood, or the core
- Netherite blocks cannot be destroyed

#### Lottery

The lottery is 15% grand prize, 35% normal, 50% trash
Each time you win a grand prize, the grand prize probability will decrease by 2% (the probability of trash will increase)

## For Server Owners

DTC III provides dynamic maps and editing toolsets

### Server Setup

I haven't tested it on Bukkit or Spigot, so you better use Paper or above server core

First, your server must have a world called `lobby` as the lobby

```properties
# server.properties
level-name=lobby
```

Each map exists in `template-[name]`, and the map named `castle` will be loaded by default

Install [ProtocolLib](https://github.com/dmulloy2/ProtocolLib) and this plugin, then just run your server

### Commands

| Format | Introduction | Remarks |
|---|---|---|
| `/shout` | Shout, so the enemy can see your message | |
| `/rejoin` | If you disconnect, type this to re-enter the game | |
| `/night-vision` | Turn on / off night vision effect | |
| `/shuffle-team` | Random team assignment | Admin only |
| `/revive [player]` | Instantly revive someone | Admin only |
| `/language <locale>` | Set language | Admin only |
| `/edit <lobby\|game>` | Enter edit mode | Admin only |
| `/reset` | End the game and reset the map | Admin only |
| `/dtc` | View plugin version | |
| `/dtc reload` | Reload settings from file | Admin only |
| `/dtc save` | Save settings to file | Admin only |
| `/dtc team <team> [player]` | Join team | Non-admins can only use it on themselves in the lobby |
| `/dtc role <role> [player]` | Change profession | Non-admins can only use it on themselves in the lobby |
| `/dtc give <item>` | Give you special items | Admin only |
| `/dtc skip` | Skip this stage | Admin only |
| `/dtc stop` | End the game | Admin only, does not reset the map |
| `/dtc world <name>` | Let you travel in different worlds | Admin only |
| `/dtc map <name>` | Set map | Admin only |
| `/dtc help` | I didn't even make this command, using it will only give you a :3 |

Commands without `/dtc` can also be added with `/dtc` at the beginning
For example, `/rejoin` and `/dtc rejoin` have the same effect

### Editor

| Editor Type | How to Use |
|---|---|
| Block | Right-click to set. If pointing at a block, it will be set to that block, otherwise it will become the player's current position <br/> The direction will be the same as the direction the player is facing <br/> Left-click to remove this position |
| Region | Need to set two corners, left-click will remove the entire region |
| Block List | Same operation as block, but there are many |

Particle effects will be displayed when the editor is held in hand

#### Lobby

Use `/edit lobby`, you will get 5 tools:

| Tool | Type | Introduction |
|---|---|---|
| Lobby Respawn Point | Block | Players will spawn here when entering the server |
| Start Button | Block | It doesn't have to be a button, right-clicking it will count down 5 seconds to start the game (press again to cancel) |
| Join Red Team | Region | Players who enter this area will join the Red Team |
| Join Green Team | Region | Players who enter this area will join the Green Team |
| Join Spectator | Region | Players who enter this area will join the Spectator |

If your players are hyperactive, they may accidentally change teams
It is recommended to put the team joining area in a more remote corner, or simply not place it and use commands to assign teams

#### Map

Set the Red Team's location, and the Green Team's location will be mirrored to (0, 0) (x, z will be the reverse of the Red Team)
If the center position of the map you are currently building is incorrect, you can use [WorldEdit](https://github.com/EngineHub/WorldEdit) to move the map

First use `/dtc world template-[name]` to enter the template world
Use `/edit game`, you will get 7 tools:

| Tool | Type | Introduction |
|---|---|---|
| Respawn Waiting Area | Block | The area where you wait when you die, blocks cannot be placed, destroyed, or used within 6 blocks |
| Respawn Point | Block | The player's respawn point, blocks cannot be placed above 3×3×3 |
| Core | Block | Core |
| Mineral | Block List | The blocks that will turn into bedrock if the faction is blocked by minerals |
| Diamond | Block List | Diamond mine, will not be mirrored |
| Mission Point | Block | Central mission point, will not be mirrored |

## For Developers

The following will explain the code of this plugin, if you are not a developer, you can skip this section

### Required APIs

| API | Reason |
|---|---|
| Paper API | Obviously |
| Apache Commons IO | World file read and write |
| Protocol Lib | Block mining effects, Ender Chest switch effects |
| Fast Board | Custom sidebar |
| Inv UI | Custom GUI |

### Precautions

- In order to avoid confusion with Minecraft's built-in Scoreboard Team, the team is called Side here

### Basic Structure

- `DestroyTheCore` is the basic `JavaPlugin`, which contains the most basic logic for plugin loading
- `Game` is the game's Lifecycle
- `Constants` are some commonly used fixed information

| Package | Content |
|---|---|
| `bases` | Various templates |
| `commands` | Commands (`Subcommand`) |
| `gui` | `InvUI` items (`GUIItem`) |
| `items` | Items (`ItemGen`) |
| `managers` | Various managers |
| `missions` | Missions (`Mission`) |
| `records` | Custom data format |
| `roles` | Professions (`Role`) |
| `tools` | Editors (`EditorTool`) |
| `utils` | Collection of various common functions |

I'll just pick out a few key points to talk about

### Bases

The basics of everything, basically used for Extend templates

#### About `EditorTool`

Defines the most basic editor, including item material, button events, etc.

The `bases/editorTools` below contains the implementation of block, region, and block list tools

#### About `GUIItem`

It is a button item for `InvUI`

#### About `ItemGen`

Custom item, the ID here is `ItemsManager.ItemKey`, remember to add new items to `ItemsManager.gens`

The `bases/itemGens` below have:
- `AssistItemGen`: Items that have effects when held in the off-hand
- `ProjItemGen`: Custom arrow items
- `UsableItemGen`: Items that have effects when right-clicked

#### About `Mission`

Mission, including start, tick, and end

The `bases/missions` below have:
- `InstantMission`: Instantly ending mission
- `ProgressiveMission`: Mission with progress competition between two teams, built-in progress bar
- `TimedMission`: Mission with countdown timer

#### About `Role`

Profession, the ID here is `RolesManager.RoleKey`, remember to add new professions to `RolesManager.roles`

#### About `Subcommand`

Command, remember to add new commands to `CommandsManager#subcommands`

### Items

More special items:
- `GrenadeGen`: It has its own `onProjectileHit` listener

### Managers

| Manager | Purpose |
|---|---|
| `BoardsManager` | Manage the information displayed on the sidebar |
| `CommandsManager` | Manage commands |
| `ConfigManager` | Manage configuration data |
| `DamangeManager` | Use maximum damage instead of Minecraft's default last-hit system |
| `EventsManager` | Manage event listeners |
| `GUIManager` | Manage GUI |
| `InventoriesManager` | Manage player inventory |
| `ItemsManager` | Manage custom items |
| `MissionsManager` | Manage missions |
| `QuizManager` | Manage respawn math problem system |
| `RecipesManager` | Manage crafting recipes |
| `TicksManager` | Manage things that need to be executed every frame |
| `ToolsManager` | Manage editors |
| `TranslationsManager` | Manage text translation |
| `WorldsManager` | Manage worlds |

### Missions

There is also a `missions/result` below, which is responsible for the rewards/punishments at the end of each mission

### Data Storage

| Item | Purpose |
|---|---|
| `Region` | Used to store a square area |
| `Stats` | Used to store player statistics (permanent) |
| `SideData` | Current faction information in the game |
| `PlayerData` | Current player information in the game |

### Utilities

| Utility | Purpose |
|---|---|
| `CoreUtils` | Miscellaneous functions |
| `AttributeUtils` | Item modifier |
| `LocationUtils` | Various functions about location |
| `ParticleUtils` | Used to generate regional particle effects |
| `PlayerUtils` | A bunch of functions related to players |
| `RandomUtils` | Generate random |
| `TextUtils` | Basically a shortcut to `TranslationsManager` |

### Game Logic

`Game` is the focus of the entire plugin, and the entire life cycle of the game is written here

| Content | Explanation |
|---|---|
| `Game.LobbyLocs` | Records the locations of the lobby |
| `Game.MapLocs` | Records the locations of the map |
| `Game.Phase` | Is the stage of the game |
| `Game.Side` | Is the faction of the game |

## Credits

| Item | List |
|---|---|
| Original Creator | 哈記 (`Hageeshow`) |
| Testers | 溜溜球 (`ItzYoyo`) <br/> 張哥 (`QinnYu`) <br/> 潤迪 (`rendyhsu`) <br/> 絡達洛 (`Lodarod29`) <br/> 我哥萊 (`c0ldc0al`) <br/> 喜歡熊 (`just_like_bear`) <br/> 鴨鴨太短 (`duckduck960325`) <br/> 帖帖 (`tete0804`) <br/> 遊戲亡 (`gdnb_0428`) |
| Players | Everyone who plays DTC III |

## Report Issues

Don't forget to follow our [OnlyFans](https://onlyfans.com/destroy_the_core/)
And join the DTC III official Discord group
