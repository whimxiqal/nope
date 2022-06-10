# Nope's Native Setting Keys
> **Note:** You can always create your own settings using [Nope's API](https://search.maven.org/search?q=me.pietelite.nope)

|Name|Value Type|Description|Default Value|
|---|---|---|---|
|invincible-entities|Multiple Value, StringSet|List of entities which cannot be damaged or destroyed.|none|
|water-flow|Single Value, Toggle (Boolean)|When disabled, lava does not spread|on|
|greeting|Single Value, Optional of String|The message to a player when they enter|*blank*|
|leaf-decay|Single Value, Toggle (Boolean)|When disabled, leaves will not decay naturally.|on|
|greeting-subtitle|Single Value, Optional of String|The subtitle that appears to a player when they enter|*blank*|
|cache-size|Single Value, Integer|This is the quantity of block locations to cache for each world. Total memory is roughly this multiplied by 56 bytes, multiplied by the number of worlds. Set 0 to disable caching.|75000|
|drop-exp|Single Value, Toggle (Boolean)|When disabled, experience points are never dropped.|on|
|trample|Single Value, State (Boolean)|When disabled, blocks like farmland may not be trampled.|allow|
|growables|Multiple Value, StringSet|A list of blocks that can grow|all|
|lightning|Single Value, Toggle (Boolean)|When disabled, lightning cannot strike.|on|
|ice-form|Single Value, Toggle (Boolean)|When disabled, ice does not form.|on|
|interactive-entities|Multiple Value, StringSet|List of entities that can be interacted with.|all|
|fire-effect|Single Value, Toggle (Boolean)|When disabled, fire does not spread or cause block damage|on|
|block-propagate|Single Value, Toggle (Boolean)|When disabled, blocks will not update each other.|on|
|exit-deny-subtitle|Single Value, Optional of String|The subtitle that is sent to a player if they are barred from exiting|*blank*|
|entry|Multiple Value, MovementSet|Specify which type of movement is allowed by players to enter.|all|
|exit|Multiple Value, MovementSet|Specify which type of movement is allowed by players to exit.|all|
|farwell-subtitle|Single Value, Optional of String|The subtitle that appears to a player when they leave the host.|*blank*|
|greeting-title|Single Value, Optional of String|The title that appears to a player when they enter|*blank*|
|farewell-title|Single Value, Optional of String|The title that appears to a player when they leave the host.|*blank*|
|fire-ignition|Single Value, State (Boolean)|When disabled, fire may not be lit|allow|
|farewell|Single Value, Optional of String|The message to a player when they leave the host.|*blank*|
|leashable-entities|Multiple Value, StringSet|A list of entities which can have leads attached to them|all|
|frosted-ice-form|Single Value, Toggle (Boolean)|When disabled, frost ice does not form.|on|
|mycelium-spread|Single Value, Toggle (Boolean)|When disabled, mycelium does not spread|on|
|interactive-blocks|Multiple Value, StringSet|A list of blocks with which that can be interacted.|all|
|block-change|Multiple Value, BlockChangeSet|A list of ways that blocks may be changed.|all|
|concrete-solidification|Single Value, Toggle (Boolean)|When disabled, concrete powder does not solidify into concrete.|on|
|ice-melt|Single Value, Toggle (Boolean)|When disabled, ice does not melt.|on|
|light-nether-portal|Single Value, State (Boolean)|When disabled, players cannot light nether portals|allow|
|ride|Single Value, State (Boolean)|When disabled, players may not ride entities|allow|
|use-name-tag|Single Value, State (Boolean)|When disabled, players may not use name tags|allow|
|sleep|Single Value, State (Boolean)|When disabled, players may not sleep.|allow|
|player-damage-source|Multiple Value, DamageCauseSet|A list of damage sources that may inflict damage to players|all|
|mob-grief|Multiple Value, StringSet|A list of all mobs that can change blocks|all|
|move|Multiple Value, MovementSet|Specify which type of movement is allowed.|all|
|tnt-ignition|Single Value, State (Boolean)|When disabled, TNT may not be primed.|allow|
|entry-deny-subtitle|Single Value, Optional of String|The subtitle that is sent to a player if they are barred from entry.|*blank*|
|health-regen|Single Value, Toggle (Boolean)|When disabled, players do not regenerate health|on|
|exit-deny-message|Single Value, Optional of String|The message that is sent to the player if they are barred from exiting.|You are not allowed to leave here|
|entry-deny-title|Single Value, Optional of String|The title that is sent to a player if they are barred from entry.|*blank*|
|destructive-explosives|Multiple Value, ExplosiveSet|A list of explosives whose explosions to not cause damage to the world.|all|
|item-drop|Single Value, Toggle (Boolean)|When disabled, items cannot drop.|on|
|ignored-plugins|Multiple Value, StringSet|A list of all plugins that Nope does not affect|none|
|player-collision|Single Value, Toggle (Boolean)|When disabled, players do not collide|on|
|item-pickup|Single Value, Toggle (Boolean)|When disabled, items may not be picked up.|on|
|spawnable-entities|Multiple Value, StringSet|List of entities which can be spawned|all|
|entry-deny-message|Single Value, Optional of String|The message that is sent to a player if they are barred from entry.|You are not allowed to go there|
|frosted-ice-melt|Single Value, Toggle (Boolean)|When disabled, frosted ice does not melt|on|
|harmful-explosives|Multiple Value, ExplosiveSet|A list of explosives whose explosions do not cause damage to entities.|all|
|hunger-drain|Single Value, Toggle (Boolean)|When disabled, player hunger does not drain naturally.|on|
|lava-flow|Single Value, Toggle (Boolean)|When disabled, lava does not spread|on|
|exit-deny-title|Single Value, Optional of String|The title that is sent to a player if they are barred from exiting|*blank*|
|grass-growth|Single Value, Toggle (Boolean)|When disabled, grass cannot grow naturally|on|
|hookable-entities|Multiple Value, StringSet|A list of entities that can be hooked with a fishing rod|all|
