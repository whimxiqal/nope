# 0.4.1

- Added `natural` option to movement settings `entry` and `exit`
- Fixed gradle settings
- Added `flower-pot-interact: false` to the `malicious-protections` template

# 0.4.0

- Made cache trimming lazy by scheduling it asynchronously
- Added settings:
    - `movement-commands` as described above
    - `flower-pot-interact` to disable the interacting of flower pots
    - `use-name-tag` to disable the use of name tags to name entities
    - implemented `chorus-fruit-teleport` to disable teleportation into, out of, or within hosts with is disabled
    - implemented `drop-exp` setting, which just doesn't let experience points spawn
- `teleport` command gives the option to show the boundaries of the given zone
- The `show` command will notify the user if the given zone boundaries are actually nearby, so the user immediately
  knows whether they can see the zone
- Add `-a` and `-r` flags to the `set` command to allow appending and removing of values if the data of a Setting is a
  collection of values, like those under the key `unspawnable-mobs`
- Setting `entry` and `exit` values `none` and `unnatural` work correctly, in which the `unnatural` value consults the
  global `unnatural-movement-commands` to identify the commands which contribute cause unnatural movement
- Added flag description information into command help menus
- When stuck on top of an impassible zone, a player is teleported far enough upwards to move and/or use commands to
  teleport away
- Liquid may now break some natural blocks, like snow layers and tall grass
- Reformatted settings list
    - Sort by category
    - Display shorter blurbs for better readability
- Remove random pesky info logs

# 0.3.0

- Fixed context calculator
- Fixed info menu to only show commands to those with permission
- Made some listeners prioritized EARLY instead of FIRST
- Added setting `hook-entity`
- Fixed player-caused damage settings to include fire ignition

# 0.2.0

- added settings
    - added `armor-stand-place` setting
    - added `armor-stand-interact` setting
    - added `item-frame-place`setting
    - added `item-frame-interact` setting
    - added `painting-place` setting
    - included the above to the `malicious-protections` template
    - added `leash` setting
- added block-cache size to `/nope` for debugging
- removed `interact` setting from the `malicious-protections` template
- `/nope reload` loads a new host tree in memory and swaps it with the plugin's tree to ensure no concurrency problems
- `/nope reload` now registers any new necessary listeners
- updated `/nope info` formatting to include redundant setting highlighting and clickable commands
- fixed `ride` setting
- `player-collision` denial now stops fishhooks from hooking players
- stopped block propagation to tnt when tnt-ignition is denied to deny activation with redstone
- made spawn-mob settings allow-deny instead of true-false
- ensured that any movement of any combination of riding-entities is handled correctly within movement settings
- stops from sending spamming duplicate messages for greeting/farewell messages but still sends quick subsequent
  distinct messages
- setting the priority of a zone with the same priority as another intersecting zone will bump other priorities higher
- creates a backup file every time the server closes
- bug fixes
