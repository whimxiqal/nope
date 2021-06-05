# Nope

Nope is a Minecraft volume protection plugin written by PietElite and tyhdefu for the Sponge plugin platform. It is based upon LionsArea and WorldGuard (https://github.com/EngineHub/WorldGuard), and has been designed to be compatible with the first and to have the same functionality as the second. It allows the user to change the handling of various behaviour within volumes called Zones, worlds and the entire server. Its most common usage case is to prevent block breakage within given volumes, however many other settings exist, such as settings to prevent PVP and to disable any kind of damage to players.

## Installation

The first step is to have Sponge installed on your server, Sponge's documentation has the instructions on that (https://docs.spongepowered.org/stable/en/server/index.html). Put Nope's jar file into the mods folder of your server. After it starts up for the first time, it will automatically generate a HOCON configuration file with the name ``zones.conf``, where all the data and changes are saved. Support for MySQL databases is planned.

## Usage

If this is your first time using Nope, then it is recommended to read the ``tutorial.md`` file to learn how to use the plugin.

Please submit any bugs you encounter into our GitLab issue tracker.

### Commands  

All commands have a subcommand **help | ?** to display usage and further subcommands.

Pipes are equivalent to "or".

- **/nope** ...  
    - Central command for Nope. Displays a welcome splash screen  
    - **reload | load**
        - Reloads the configuration file from storage
    - **create | c | add** ... <*zone-name*> [[-w <*world*>] <*x1*> <*y1*> <*z1*> <*x2*> <*y2*> <*z3*>]] [-p <*p*>]
        - Creates a Zone with the given name using the data from the user's Selection, otherwise uses data from the input if the Zone dimensions and location are explicitly given
        - By default Nope gives the new Zone a priority, which is larger than the highest priority of intersecting Zones by one.
        - **slab** <*zone-name*>
            - Creates a Zone, which is unbounded on the X and Z axis. As a result it covers every block with an Y value in a certain range.
    - **destroy | remove** <*zone-name*>
        - Deletes the given zone
    - **info | i** [*zone-name*]  
        - Sends detailed information about the Zone through chat
        - Infers the zone the user is in if no name is given
    - **show** [*zone-name*]
        - Shows a Zone's boundaries with particles as they enter or leave them
        - Infers the zone the user is in if no name is given
    - **showall**
        - Shows the boundaries of every Zone as the user enters or leaves them
    - **teleport | tp** <*zone-name*>
        - Teleports the user to a random point inside the Zone
        - May teleport to a designated point if it is specificed in the Zone's settings
        - This command uses Sponge's safe teleporting, so the user won't get suffocated in any case
    - **list | l**
        - Sends the list of all zones the user is currently in
    - **listall**
        - Sends the list of all zones in the server
    - **wand | w**
        - Gives Nope Zone Wand to the user
        - Used for setting positions of the user's Selection with left and right clicks
    - **pos1** <*x*> <*y*> <*z*>
        - An alternative to the Wand, which sets the first position of the user's Selection to the given values
    - **pos2** <*x*> <*y*> <*z*>
        - An alternative to the Wand, which sets the second position of the user's Selection to the given values
    - **setpriority** [-z <*zone-name*>] <*priority*>
        - Sets the priority of the Zone
        - Infers the zone the user is in if no name is given
        - Cannot be used to set the priority of a WorldHost or the GlobalHost
        - Cannot be used to set a negative priority to the Zone, as they are reserved to WorldHosts and the GlobalHost
        - When the given Zone intersects with another Zone with the given priority, Nope bumps the priorities of all conflicting zones by one
    - **target** ...
        - Makes changes to how a Zone targets players
        - Anyone without added permission requirements will not be affected by the Zone's settings. In addition, specifically added players will be either the only ones affected or the only ones not affected, depending on whether this target type is a whitelist or a blacklist
        - **type** [-z <*zone-name*>] <*whitelist|blacklist*>
            - Changes the targeting type to a whitelist or a blacklist
        - **add** ...
            - Adds requirements for a setting to target a player
            - **permission | perm** <*setting-name*> <*permission*> <*setting-value*>
                - Adds a permission requirement
                - Players without the designated permission requirement will not be affected by the Zone's settings
            - **player** <*setting-name*> <*player*>
                - Adds a player to the whitelist or blacklist
        - **remove** ...
            - Removes specifiers for a setting targetting players
            - **permission | perm** <*setting-name*> <*permission*>
                - Removes a permission requirement from a setting
            - **player** <*setting-name*> <*player*>
                - Removes a player from the whitelist or blacklist
        - **force** <*setting*>
            - Toggles whether the ``nope.unrestricted`` permission is respected on the given setting
    - **settings**
        - Gives the list of all the Settings Nope has
    - **set** [-z <*zone-name*>] <*setting-name*> <*setting-value*>
        - Sets the value to the given Setting in the Zone
        - Infers the zone the user is in if no name is given
    - **unset** [-z <*zone-name*>] <*setting-name*>
        - Clears the given Setting in the Zone
        - Infers the zone the user is in if no name is given
    - **clear** [-z <*zone-name*>]
        - Clears all Settings in the Zone
        - Infers the zone the user is in if no name is given
    - **apply** [-z <*zone-name*>] <*setting-template*>
        - Applies the given setting template to the Zone
        - Used for quickly applying many settings
        - Infers the zone the user is in if no name is given

### Argument Syntax  

| Symbol | Meaning  |   
| :----: | :------: |
| <...>  | required |  
| [...]  | optional |

## Permissions

| Permission name | Purpose |
| :-------------: | :-----: |
| ``nope.command.create`` | Allows the user to create Zones |
| ``nope.command.destroy`` | Allows the user to destroy Zones |
| ``nope.command.edit`` | Allows the user to edit parameters of Zones |
| ``nope.command.info`` | Allows the user to view information about Zones |
| ``nope.command.list`` | Allows the user to view the list of all Zones |
| ``nope.command.show`` | Allows the user to see the boundaries of Zones |
| ``nope.command.teleport`` | Allows the user to teleport to a Zone |
| ``nope.command.reload`` | Allows the user to reload Nope |
| ``nope.command.setting`` | Allows the user to see the Settings and set them to Zones |
| ``nope.unrestricted`` | Unrestricted access to all commands |

## Templates

``default-protections`` - disables mob grief, lava and water block damage, TNT placement and ignition, fire ignition and damage.

``malicious-protections`` - disables destruction, interaction and placement of armor stands, block breakage and placement, destruction, interaction and placement of item frames, placement and destruction of paintings, placement and destruction of vehicles (boats and minecarts).

## Contributing

This project uses Google standards for code styling. You can apply real-time evaluation of your code for compliance with Google Checks using a plugin. For IntelliJ-IDEA, you can download Checkstyle-IDEA and apply Google Checks in the settings.

Commit early and commit often. When you submit a merge request (or push your changes directly, each commit purpose should be well defined.

## License

This project is licensed under the MIT License. You should have a copy of it together with Nope. If not, then you can read a copy of it on opensource.org (https://opensource.org/licenses/MIT).

## Credits

PietElite - Maintainer, Creator

tyhdefu - Developer

14mRh4X0r - Developer

Vagankovo - Contributor

