/*
 *
 * MIT License
 *
 * Copyright (c) 2022 Pieter Svenson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.minecraftonline.nope.common.setting;

import com.google.common.collect.Maps;
import com.minecraftonline.nope.common.setting.manager.BooleanKeyManager;
import com.minecraftonline.nope.common.setting.manager.IntegerKeyManager;
import com.minecraftonline.nope.common.setting.manager.PolyStringKeyManager;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import org.jetbrains.annotations.NotNull;

public class SettingKeys {

  private static final BooleanKeyManager BOOLEAN_KEY_MANAGER = new BooleanKeyManager();
  private static final BooleanKeyManager STATE_KEY_MANAGER = new BooleanKeyManager(true);
  private static final IntegerKeyManager INTEGER_KEY_MANAGER = new IntegerKeyManager();

  private static final PolyStringKeyManager POLY_STRING_KEY_MANAGER = new PolyStringKeyManager();

  public static final SettingKey.Unary<Boolean> ARMOR_STAND_DESTROY =
      SettingKey.Unary.builder("armor-stand-destroy", true, STATE_KEY_MANAGER)
          .blurb("Armor stand destruction restriction")
          .description("When disabled, armor stands may not be broken by players.")
          .category(SettingKey.Category.ENTITIES)
          .playerRestrictive()
          .build();

  public static final SettingKey.Unary<Boolean> ARMOR_STAND_INTERACT =
      SettingKey.Unary.builder("armor-stand-interact", true, STATE_KEY_MANAGER)
          .blurb("Armor stand interact restriction")
          .description("When disabled, armor stands may not be interacted with by players.")
          .category(SettingKey.Category.ENTITIES)
          .playerRestrictive()
          .build();

  public static final SettingKey.Unary<Boolean> ARMOR_STAND_PLACE =
      SettingKey.Unary.builder("armor-stand-place", true, STATE_KEY_MANAGER)
          .blurb("Armor stand placement restriction")
          .description("When disabled, armor stands may not be placed by players.")
          .category(SettingKey.Category.ENTITIES)
          .playerRestrictive()
          .build();
//
//  @Blurb("Block break restriction")
//  @Description("When disabled, blocks may not be broken by players.")
//  @Category(SettingKey.CategoryType.BLOCKS)
//  @PlayerRestrictive
//  public static final SettingKey<Boolean> BLOCK_BREAK = new StateSettingKey(
//      "block-break",
//      true
//  );
//  @Blurb("Block place restriction")
//  @Description("When disabled, blocks may not be placed by players.")
//  @Category(SettingKey.CategoryType.BLOCKS)
//  @PlayerRestrictive
//  public static final SettingKey<Boolean> BLOCK_PLACE = new StateSettingKey(
//      "block-place",
//      true
//  );
//  @Blurb("Inside to outside block updates")
//  @Description("When disabled, block updates will not affect others across the zone boundary.")
//  @Category(SettingKey.CategoryType.BLOCKS)
//  public static final SettingKey<Boolean> BLOCK_PROPAGATE_ACROSS = new BooleanSettingKey(
//      "block-propagate-across",
//      true
//  );
//  @Blurb("Inside to inside block updates")
//  @Description("When disabled, block updates will not affect others within the zone.")
//  @Category(SettingKey.CategoryType.BLOCKS)
//  public static final SettingKey<Boolean> BLOCK_PROPAGATE_WITHIN = new BooleanSettingKey(
//      "block-propagate-within",
//      true
//  );
//  @Blurb("Trample restriction")
//  @Description("When disabled, blocks like farmland may not be trampled.")
//  @Category(SettingKey.CategoryType.BLOCKS)
//  @PlayerRestrictive
//  public static final SettingKey<Boolean> BLOCK_TRAMPLE = new StateSettingKey(
//      "block-trample",
//      true
//  );

  public static final SettingKey.Unary<Integer> CACHE_SIZE =
      SettingKey.Unary.builder("cache-size", 75000, INTEGER_KEY_MANAGER)
          .blurb("Size of world block caches")
          .description("This is the quantity of block locations to cache for each world. "
      + "Total memory is roughly this multiplied by 56 bytes, "
      + "multiplied by the number of worlds. Set 0 to disable caching.")
          .category(SettingKey.Category.ENTITIES)
          .playerRestrictive()
          .build();

//  @Blurb("Size of world block caches")
//  @Description("This is the quantity of block locations to cache for each world. "
//      + "Total memory is roughly this multiplied by 56 bytes, "
//      + "multiplied by the number of worlds. Set 0 to disable caching.")
//  @Global
//  public static final SettingKey<Integer> CACHE_SIZE = new PositiveIntegerSettingKey(
//      "cache-size",
//      75000
//  );
//  @Blurb("Chest access restriction")
//  @Description("When disabled, players may not open chests.")
//  @Category(SettingKey.CategoryType.BLOCKS)
//  @PlayerRestrictive
//  public static final SettingKey<Boolean> CHEST_ACCESS = new StateSettingKey(
//      "chest-access",
//      true
//  );
//  @Blurb("Chorus fruit teleport restriction")
//  @Description("When disabled, players may not teleport by eating a chorus fruit.")
//  @Category(SettingKey.CategoryType.MOVEMENT)
//  @PlayerRestrictive
//  public static final SettingKey<Boolean> CHORUS_FRUIT_TELEPORT = new StateSettingKey(
//      "chorus-fruit-teleport",
//      true
//  );
//  @Blurb("Concrete powder solidification")
//  @Description("When disabled, concrete powder does not solidify into concrete.")
//  @Category(SettingKey.CategoryType.BLOCKS)
//  public static final SettingKey<Boolean> CONCRETE_SOLIDIFICATION = new BooleanSettingKey(
//      "concrete-solidification",
//      true
//  );
//  @Blurb("Crop growth")
//  @Description("When disabled, crops do not grow.")
//  @Category(SettingKey.CategoryType.BLOCKS)
//  @PlayerRestrictive
//  public static final SettingKey<Boolean> CROP_GROWTH = new BooleanSettingKey(
//      "crop-growth",
//      true
//  );
//  @Blurb("Entity experience drop")
//  @Description("When disabled, experience points are never dropped.")
//  @PlayerRestrictive
//  public static final SettingKey<Boolean> DROP_EXP = new BooleanSettingKey(
//      "drop-exp",
//      false
//  );
//  @Blurb("Grief caused by the enderdragon")
//  @Description("Enables grief caused by the enderdragon.")
//  @Category(SettingKey.CategoryType.BLOCKS)
//  public static final SettingKey<Boolean> ENDERDRAGON_GRIEF = new BooleanSettingKey(
//      "enderdragon-grief",
//      true
//  );
//  @Blurb("Grief caused by endermen")
//  @Description("When disabled, endermen do not grief blocks by picking them up.")
//  @Category(SettingKey.CategoryType.BLOCKS)
//  public static final SettingKey<Boolean> ENDERMAN_GRIEF = new BooleanSettingKey(
//      "enderman-grief",
//      true
//  );
//  @Blurb("Enderpearl teleport restriction")
//  @Description("When disabled, enderpearls may not be used for teleportation.")
//  @Category(SettingKey.CategoryType.MOVEMENT)
//  @PlayerRestrictive
//  public static final SettingKey<Boolean> ENDERPEARL_TELEPORT = new StateSettingKey(
//      "enderpearl-teleport",
//      true
//  );
//  @Blurb("Host entrance restriction")
//  @Description("Specify which type of movement is allowed by players to enter.")
//  @Category(SettingKey.CategoryType.MOVEMENT)
//  @PlayerRestrictive
//  public static final SettingKey<Movement> ENTRY = new EnumSettingKey<>(
//      "entry",
//      Movement.ALL,
//      Movement.class
//  );
//  @Blurb("Message when entry is denied")
//  @Description("The message that is sent to a player if they are barred from entry.")
//  @Category(SettingKey.CategoryType.MOVEMENT)
//  public static final SettingKey<String> ENTRY_DENY_MESSAGE = new StringSettingKey(
//      "entry-deny-message",
//      "You are not allowed to go there"
//  );
//  @Blurb("Subtitle when entry is denied")
//  @Description("The subtitle that is sent to a player if they are barred from entry.")
//  @Category(SettingKey.CategoryType.MOVEMENT)
//  public static final SettingKey<String> ENTRY_DENY_SUBTITLE = new StringSettingKey(
//      "entry-deny-subtitle",
//      ""
//  );
//  @Blurb("Title when entry is denied")
//  @Description("The title that is sent to a player if they are barred from entry.")
//  @Category(SettingKey.CategoryType.MOVEMENT)
//  public static final SettingKey<String> ENTRY_DENY_TITLE = new StringSettingKey(
//      "entry-deny-title",
//      ""
//  );
//  @Blurb("Environment-to-player damage")
//  @Description("When disabled, the environment cannot inflict damage on players.")
//  @Category(SettingKey.CategoryType.DAMAGE)
//  @PlayerRestrictive
//  public static final SettingKey<Boolean> EVP = new StateSettingKey(
//      "evp",
//      true
//  );
//  @Blurb("Host exit restriction")
//  @Description("Specify which type of movement is allowed by players to exit.")
//  @Category(SettingKey.CategoryType.MOVEMENT)
//  @PlayerRestrictive
//  public static final SettingKey<Movement> EXIT = new EnumSettingKey<>(
//      "exit",
//      Movement.ALL,
//      Movement.class
//  );
//  @Blurb("Message when exit is denied")
//  @Description("The message that is sent to the player if they are barred from exiting.")
//  @Category(SettingKey.CategoryType.MOVEMENT)
//  public static final SettingKey<String> EXIT_DENY_MESSAGE = new StringSettingKey(
//      "exit-deny-message",
//      "You are not allowed to leave here"
//  );
//  @Blurb("Subtitle when exit is denied")
//  @Description("The subtitle that is sent to a player if they are barred from exiting.")
//  @Category(SettingKey.CategoryType.MOVEMENT)
//  public static final SettingKey<String> EXIT_DENY_SUBTITLE = new StringSettingKey(
//      "exit-deny-subtitle",
//      ""
//  );
//  @Blurb("Title when exit is denied")
//  @Description("The title that is sent to a player if they are barred from exiting.")
//  @Category(SettingKey.CategoryType.MOVEMENT)
//  public static final SettingKey<String> EXIT_DENY_TITLE = new StringSettingKey(
//      "exit-deny-title",
//      ""
//  );
//  @Blurb("Harmless explosions")
//  @Description("A list of explosives whose explosions do not cause damage.")
//  @Category(SettingKey.CategoryType.DAMAGE)
//  public static final SettingKey<Set<Explosive>> EXPLOSION_DAMAGE_BLACKLIST = new EnumSetSettingKey<>(
//      "explosion-damage-blacklist",
//      new HashSet<>(),
//      Explosive.class
//  );
//  @Blurb("Nondestructive explosions")
//  @Description("A list of explosives whose explosions do not grief.")
//  @Category(SettingKey.CategoryType.BLOCKS)
//  public static final SettingKey<Set<Explosive>> EXPLOSION_GRIEF_BLACKLIST = new EnumSetSettingKey<>(
//      "explosion-block-grief-blacklist",
//      new HashSet<>(),
//      Explosive.class
//  );
//  @Blurb("Fall damage")
//  @Description("When disabled, players do not experience fall damage.")
//  @Category(SettingKey.CategoryType.DAMAGE)
//  public static final SettingKey<Boolean> FALL_DAMAGE = new BooleanSettingKey(
//      "fall-damage",
//      false
//  );
//  @Blurb("Message upon exit")
//  @Description("The message to a player when they leave the host.")
//  @Category(SettingKey.CategoryType.MOVEMENT)
//  public static final SettingKey<String> FAREWELL = new StringSettingKey(
//      "farewell",
//      ""
//  );
//  @Blurb("Subtitle upon exit")
//  @Description("The subtitle that appears to a player when they leave.")
//  @Category(SettingKey.CategoryType.MOVEMENT)
//  public static final SettingKey<String> FAREWELL_SUBTITLE = new StringSettingKey(
//      "farewell-subtitle",
//      ""
//  );
//  @Blurb("Title upon exit")
//  @Description("The title that appears to a player when they leave.")
//  @Category(SettingKey.CategoryType.MOVEMENT)
//  public static final SettingKey<String> FAREWELL_TITLE = new StringSettingKey(
//      "farewell-title",
//      ""
//  );
//  @Blurb("Fire spread/damage")
//  @Description("When disabled, fire does not spread or cause block damage.")
//  @Category(SettingKey.CategoryType.BLOCKS)
//  public static final SettingKey<Boolean> FIRE_EFFECT = new StateSettingKey(
//      "fire-effect",
//      true
//  );
//  @Blurb("Fire ignition restriction")
//  @Description("When disabled, players cannot light fire")
//  @Category(SettingKey.CategoryType.BLOCKS)
//  @PlayerRestrictive
//  public static final SettingKey<Boolean> FIRE_IGNITION = new StateSettingKey(
//      "fire-ignition",
//      true
//  );
//  @Blurb("Natural fire ignition")
//  @Description("When disabled, fire is not started naturally.")
//  public static final SettingKey<Boolean> FIRE_NATURAL_IGNITION = new StateSettingKey(
//      "fire-natural-ignition",
//      true
//  );
//  @Blurb("Flower pot interaction restriction")
//  @Description("When disabled, players cannot interact with flower pots.")
//  @Category(SettingKey.CategoryType.BLOCKS)
//  @PlayerRestrictive
//  public static final SettingKey<Boolean> FLOWER_POT_INTERACT = new StateSettingKey(
//      "flower-pot-interact",
//      true
//  );
//  @Blurb("Frosted ice formation")
//  @Description("When disabled, frosted ice does not form.")
//  @Category(SettingKey.CategoryType.BLOCKS)
//  public static final SettingKey<Boolean> FROSTED_ICE_FORM = new StateSettingKey(
//      "frosted-ice-form",
//      true
//  );
//  @Blurb("Frosted ice melt")
//  @Description("When disabled, frosted ice does not melt.")
//  @Category(SettingKey.CategoryType.BLOCKS)
//  public static final SettingKey<Boolean> FROSTED_ICE_MELT = new StateSettingKey(
//      "frosted-ice-melt",
//      true
//  );
//  @Blurb("Ghast production of fireballs")
//  @Description("When disabled, ghasts do not shoot fireballs.")
//  @Category(SettingKey.CategoryType.ENTITIES)
//  public static final SettingKey<Boolean> GHAST_FIREBALL = new StateSettingKey(
//      "ghast-fireball",
//      true
//  );
//  @Blurb("Grass growth")
//  @Description("When disabled, grass does not grow naturally.")
//  @Category(SettingKey.CategoryType.BLOCKS)
//  @PlayerRestrictive
//  public static final SettingKey<Boolean> GRASS_GROWTH = new StateSettingKey(
//      "grass-growth",
//      true
//  );
//  @Blurb("Message upon entry")
//  @Description("The message to a player when they enter")
//  @Category(SettingKey.CategoryType.MOVEMENT)
//  public static final SettingKey<String> GREETING = new StringSettingKey(
//      "greeting",
//      ""
//  );
//  @Blurb("Subtitle upon entry")
//  @Description("The subtitle that appears to a player when they enter.")
//  @Category(SettingKey.CategoryType.MOVEMENT)
//  public static final SettingKey<String> GREETING_SUBTITLE = new StringSettingKey(
//      "greeting-subtitle",
//      ""
//  );
//  @Blurb("Title upon entry")
//  @Description("The title that appears to a player when they enter.")
//  @Category(SettingKey.CategoryType.MOVEMENT)
//  public static final SettingKey<String> GREETING_TITLE = new StringSettingKey(
//      "greeting-title",
//      ""
//  );
//  @Blurb("Fishing hook to entity attachment")
//  @Description("When disabled, entities cannot be hooked with a fishing hook.")
//  @Category(SettingKey.CategoryType.ENTITIES)
//  @PlayerRestrictive
//  public static final SettingKey<Boolean> HOOK_ENTITY = new StateSettingKey(
//      "hook-entity",
//      true
//  );
//  @Blurb("Hostile to player damage")
//  @Description("When disabled, hostile creatures cannot inflict damage on players.")
//  @Category(SettingKey.CategoryType.DAMAGE)
//  public static final SettingKey<Boolean> HVP = new StateSettingKey(
//      "hvp",
//      true
//  );
//  @Blurb("Ice formation")
//  @Description("When disabled, ice does not form.")
//  @Category(SettingKey.CategoryType.BLOCKS)
//  public static final SettingKey<Boolean> ICE_FORM = new BooleanSettingKey(
//      "ice-form",
//      true
//  );
//  @Blurb("Ice melt")
//  @Description("When disabled, ice does not melt.")
//  @Category(SettingKey.CategoryType.BLOCKS)
//  public static final SettingKey<Boolean> ICE_MELT = new BooleanSettingKey(
//      "ice-melt",
//      true
//  );
//  @Blurb("General interaction restriction")
//  @Description("When disabled, players may not interact with any blocks.")
//  @Category(SettingKey.CategoryType.BLOCKS)
//  @PlayerRestrictive
//  public static final SettingKey<Boolean> INTERACT = new StateSettingKey(
//      "interact",
//      true
//  );
//  @Blurb("Animal invincibility")
//  @Description("When disabled, animals are invincible.")
//  @Category(SettingKey.CategoryType.DAMAGE)
//  public static final SettingKey<Boolean> INVINCIBLE_ANIMALS = new BooleanSettingKey(
//      "invincible-animals",
//      false
//  );
//  @Blurb("All mob invincibility")
//  @Description("When disabled, mobs cannot take damage.")
//  @Category(SettingKey.CategoryType.DAMAGE)
//  public static final SettingKey<Boolean> INVINCIBLE_MOBS = new BooleanSettingKey(
//      "invincible-mobs",
//      false
//  );
//  @Blurb("Player invincibility")
//  @Description("When enabled, players cannot take damage.")
//  @Category(SettingKey.CategoryType.DAMAGE)
//  public static final SettingKey<Boolean> INVINCIBLE_PLAYERS = new BooleanSettingKey(
//      "invincible-players",
//      false
//  );
//  @Blurb("Item drop restriction")
//  @Description("When disabled, players cannot drop items.")
//  @PlayerRestrictive
//  public static final SettingKey<Boolean> ITEM_DROP = new BooleanSettingKey(
//      "item-drop",
//      true
//  );
//  @Blurb("Item frame destruction restriction")
//  @Description("When disabled, item frames may not be attacked by players.")
//  @Category(SettingKey.CategoryType.ENTITIES)
//  @PlayerRestrictive
//  public static final SettingKey<Boolean> ITEM_FRAME_DESTROY = new StateSettingKey(
//      "item-frame-destroy",
//      true
//  );
//  @Blurb("Item frame interaction restriction")
//  @Description("When disabled, item frames may not be interacted with by a player.")
//  @Category(SettingKey.CategoryType.ENTITIES)
//  @PlayerRestrictive
//  public static final SettingKey<Boolean> ITEM_FRAME_INTERACT = new StateSettingKey(
//      "item-frame-interact",
//      true
//  );
//  @Blurb("Item frame placement restriction")
//  @Description("When disabled, item frames may not be placed.")
//  @Category(SettingKey.CategoryType.ENTITIES)
//  @PlayerRestrictive
//  public static final SettingKey<Boolean> ITEM_FRAME_PLACE = new StateSettingKey(
//      "item-frame-place",
//      true
//  );
//  @Blurb("Item pickup restriction")
//  @Description("When disabled, players cannot pick up items.")
//  @PlayerRestrictive
//  public static final SettingKey<Boolean> ITEM_PICKUP = new StateSettingKey(
//      "item-pickup",
//      true
//  );
//  @Blurb("Lava flow")
//  @Description("When disabled, lava does not spread.")
//  @Category(SettingKey.CategoryType.BLOCKS)
//  public static final SettingKey<Boolean> LAVA_FLOW = new BooleanSettingKey(
//      "lava-flow",
//      true
//  );
//  @Blurb("Grief caused by lava")
//  @Description("When disabled, lava does not break blocks.")
//  @Category(SettingKey.CategoryType.BLOCKS)
//  public static final SettingKey<Boolean> LAVA_GRIEF = new BooleanSettingKey(
//      "lava-grief",
//      true
//  );
//  @Blurb("Leaf decay")
//  @Description("When disabled, leaf will not decay naturally.")
//  @Category(SettingKey.CategoryType.BLOCKS)
//  public static final SettingKey<Boolean> LEAF_DECAY = new StateSettingKey(
//      "leaf-decay",
//      true
//  );
//  @Blurb("Putting leads on entities restriction")
//  @Description("When disabled, players cannot put leads on entities.")
//  @Category(SettingKey.CategoryType.BLOCKS)
//  @PlayerRestrictive
//  public static final SettingKey<Boolean> LEASH = new StateSettingKey(
//      "leash",
//      true
//  );
//  @Blurb("Lightning strikes")
//  @Description("When disabled, lightning cannot strike.")
//  @Category(SettingKey.CategoryType.BLOCKS)
//  public static final SettingKey<Boolean> LIGHTNING = new BooleanSettingKey(
//      "lightning",
//      true
//  );
//  @Blurb("Mushroom growth")
//  @Description("When disabled, mushrooms do not grow.")
//  @Category(SettingKey.CategoryType.BLOCKS)
//  @PlayerRestrictive
//  public static final SettingKey<Boolean> MUSHROOM_GROWTH = new BooleanSettingKey(
//      "mushroom-growth",
//      true
//  );
//  @Blurb("Mycelium spread")
//  @Description("When disabled, mycelium does not spread.")
//  @Category(SettingKey.CategoryType.BLOCKS)
//  public static final SettingKey<Boolean> MYCELIUM_SPREAD = new BooleanSettingKey(
//      "mycelium-spread",
//      true
//  );
//  @Blurb("Player health regeneration")
//  @Description("When disabled, player health does not regenerate naturally.")
//  @PlayerRestrictive
//  public static final SettingKey<Boolean> NATURAL_HEALTH_REGEN = new BooleanSettingKey(
//      "natural-health-regen",
//      true
//  );
//  @Blurb("Player hunger drain")
//  @Description("When disabled, player hunger does not drain naturally.")
//  @NotImplemented
//  public static final SettingKey<Boolean> NATURAL_HUNGER_DRAIN = new BooleanSettingKey(
//      "natural-hunger-drain",
//      true
//  );
//  @Blurb("Painting destruction restriction")
//  @Description("When disabled, paintings may not be broken by players.")
//  @Category(SettingKey.CategoryType.ENTITIES)
//  @PlayerRestrictive
//  public static final SettingKey<Boolean> PAINTING_DESTROY = new StateSettingKey(
//      "painting-destroy",
//      true
//  );
//  @Blurb("Painting placement restriction")
//  @Description("When disabled, paintings may not be placed.")
//  @Category(SettingKey.CategoryType.ENTITIES)
//  @PlayerRestrictive
//  public static final SettingKey<Boolean> PAINTING_PLACE = new StateSettingKey(
//      "painting-place",
//      true
//  );
//  @Blurb("Player collision")
//  @Description("When disabled, all interactions caused by player collision are cancelled.")
//  @Category(SettingKey.CategoryType.ENTITIES)
//  public static final SettingKey<Boolean> PLAYER_COLLISION = new StateSettingKey(
//      "player-collision",
//      true
//  );
//  @Blurb("Player to animal damage")
//  @Description("When disabled, players cannot inflict damage on animals.")
//  @Category(SettingKey.CategoryType.DAMAGE)
//  @PlayerRestrictive
//  public static final SettingKey<Boolean> PVA = new StateSettingKey(
//      "pva",
//      true
//  );
//  @Blurb("Player to hostile mob damage")
//  @Description("When disabled, players cannot inflict damage on hostile creatures.")
//  @Category(SettingKey.CategoryType.DAMAGE)
//  @PlayerRestrictive
//  public static final SettingKey<Boolean> PVH = new StateSettingKey(
//      "pvh",
//      true
//  );
//  @Blurb("Player to player damage")
//  @Description("When disabled, players cannot inflict damage on other players.")
//  @Category(SettingKey.CategoryType.DAMAGE)
//  @PlayerRestrictive
//  public static final SettingKey<Boolean> PVP = new StateSettingKey(
//      "pvp",
//      true
//  );
//  @Blurb("Plugins which are considered restricted")
//  @Description("The plugins that cannot break restrictive setting rules.")
//  @NotImplemented
//  public static final SettingKey<Set<String>> RESTRICTED_PLUGINS = new StringSetSettingKey(
//      "restricted-plugins",
//      new HashSet<>()
//  );
//  @Blurb("Players' ability to ride entities")
//  @Description("When disabled, players cannot ride other entities.")
//  @Category(SettingKey.CategoryType.ENTITIES)
//  @PlayerRestrictive
//  public static final SettingKey<Boolean> RIDE = new StateSettingKey(
//      "ride",
//      true
//  );
//  public static final String SET_SPLIT_REGEX = "(?<![ ,])(( )+|( *, *))(?![ ,])";
//  @Blurb("Players' ability to sleep")
//  @Description("When disabled, players cannot sleep.")
//  @PlayerRestrictive
//  public static final SettingKey<Boolean> SLEEP = new StateSettingKey(
//      "sleep",
//      true
//  );
//  @Blurb("Snowman snow trail creation")
//  @Description("When disabled, snowmen do not make snow trails.")
//  @Category(SettingKey.CategoryType.BLOCKS)
//  public static final SettingKey<Boolean> SNOWMAN_TRAILS = new StateSettingKey(
//      "snowman-trails",
//      true
//  );
//  @Blurb("Snowfall snow placement")
//  @Description("When disabled, snow does not accumulate naturally.")
//  @Category(SettingKey.CategoryType.BLOCKS)
//  public static final SettingKey<Boolean> SNOW_ACCUMULATION = new StateSettingKey(
//      "snow-accumulation",
//      true
//  );
//  @Blurb("Snow melt")
//  @Description("When disabled, snow does not melt.")
//  @Category(SettingKey.CategoryType.BLOCKS)
//  public static final SettingKey<Boolean> SNOW_MELT = new StateSettingKey(
//      "snow-melt",
//      true
//  );
//  @Blurb("Animal spawning")
//  @Description("When disabled, animals cannot spawn.")
//  @Category(SettingKey.CategoryType.ENTITIES)
//  @PlayerRestrictive
//  public static final SettingKey<Boolean> SPAWN_ANIMAL = new StateSettingKey(
//      "spawn-animal",
//      true
//  );
//  @Blurb("Hostile mob spawning")
//  @Description("When disabled, hostile mobs cannot spawn.")
//  @Category(SettingKey.CategoryType.ENTITIES)
//  @PlayerRestrictive
//  public static final SettingKey<Boolean> SPAWN_HOSTILE = new StateSettingKey(
//      "spawn-hostile",
//      true
//  );
//  @Blurb("All mob spawning")
//  @Description("When disabled, no mobs can spawn.")
//  @Category(SettingKey.CategoryType.ENTITIES)
//  @PlayerRestrictive
//  public static final SettingKey<Boolean> SPAWN_MOB = new StateSettingKey(
//      "spawn-mob",
//      true
//  );
//  @Blurb("SQL data source name")
//  @Description("The Data Source name of the SQL database to be used if SQL is the storage type.")
//  @NotImplemented
//  @Global
//  public static final SettingKey<String> SQL_DSN = new StringSettingKey(
//      "sql-dsn",
//      "jdbc:mysql://localhost/nope"
//  );
//  @Blurb("SQL password")
//  @Description("The password for the SQL database to be used if SQL is the storage type.")
//  @NotImplemented
//  @Global
//  public static final SettingKey<String> SQL_PASSWORD = new StringSettingKey(
//      "sql-password",
//      "nope"
//  );
//  @Blurb("SQL table prefix")
//  @Description("The table prefix to be placed before SQL tables if SQL is the storage type.")
//  @NotImplemented
//  @Global
//  public static final SettingKey<String> SQL_TABLE_PREFIX = new StringSettingKey(
//      "sql-table-prefix",
//      "nope"
//  );
//  @Blurb("SQL username")
//  @Description("The username for the SQL database to be used if SQL is the storage type.")
//  @NotImplemented
//  @Global
//  public static final SettingKey<String> SQL_USERNAME = new StringSettingKey(
//      "sql-username",
//      "nope"
//  );
//  @Blurb("Storage type")
//  @Description("The type of storage to persist Nope server state.")
//  @NotImplemented
//  @Global
//  public static final SettingKey<Storage> STORAGE_TYPE = new EnumSettingKey<>(
//      "storage-type",
//      Storage.SQLITE,
//      Storage.class
//  );
//  @Blurb("Location at which to teleport")
//  @Description("The designated point of access to the zone via teleport.")
//  public static final SettingKey<Vector3d> TELEPORT_LOCATION = new Vector3dSettingKey(
//      "teleport-location",
//      null,
//      Vector3d.class
//  );
//  @Blurb("TNT ignition restriction")
//  @Description("When disabled, tnt may not be activated.")
//  @PlayerRestrictive
//  public static final SettingKey<Boolean> TNT_IGNITION = new StateSettingKey(
//      "tnt-ignition",
//      true
//  );
//  @Blurb("TNT placement restriction")
//  @Description("When disabled, tnt may not be placed.")
//  @Category(SettingKey.CategoryType.BLOCKS)
//  @PlayerRestrictive
//  public static final SettingKey<Boolean> TNT_PLACEMENT = new StateSettingKey(
//      "tnt-placement",
//      true
//  );
//  @Blurb("Commands which directly cause movement")
//  @Description("These commands will be considered unnatural methods of teleportation.")
//  @PlayerRestrictive
//  @Global
//  public static final SettingKey<Set<String>> MOVEMENT_COMMANDS = new StringSetSettingKey(
//      "movement-commands",
//      Sets.newHashSet()
//  );

public static final SettingKey.Poly<String> UNSPAWNABLE_MOBS =
    SettingKey.Poly.builderEmptyDefault("unspawnable-mobs", POLY_STRING_KEY_MANAGER)
        .blurb("Armor stand placement restriction")
        .description("When disabled, armor stands may not be placed by players.")
        .category(SettingKey.Category.ENTITIES)
        .playerRestrictive()
        .build();

//  @Blurb("Mobs which are unspawnable")
//  @Description("These entity types will not be allowed to spawn.")
//  @Category(SettingKey.CategoryType.ENTITIES)
//  @PlayerRestrictive
//  public static final SettingKey<Set<String>> UNSPAWNABLE_MOBS = new StringSetSettingKey(
//      "unspawnable-mobs",
//      Sets.newHashSet()
//  );
//  @Blurb("Name tag use")
//  @Description("When disabled, players may not use a name tag to rename an entity")
//  @PlayerRestrictive
//  public static final SettingKey<Boolean> USE_NAME_TAG = new StateSettingKey(
//      "use-name-tag",
//      true
//  );
//  @Blurb("Vehicle destruction restriction")
//  @Description("When disabled, players may not break vehicles.")
//  @Category(SettingKey.CategoryType.ENTITIES)
//  @PlayerRestrictive
//  public static final SettingKey<Boolean> VEHICLE_DESTROY = new StateSettingKey(
//      "vehicle-destroy",
//      true
//  );
//  @Blurb("Vehicle placement restriction")
//  @Description("When disabled, players may not place vehicles.")
//  @Category(SettingKey.CategoryType.ENTITIES)
//  @PlayerRestrictive
//  public static final SettingKey<Boolean> VEHICLE_PLACE = new StateSettingKey(
//      "vehicle-place",
//      true
//  );
//  @Blurb("Vine growth")
//  @Description("When disabled, vines do not grow naturally.")
//  @Category(SettingKey.CategoryType.BLOCKS)
//  public static final SettingKey<Boolean> VINE_GROWTH = new StateSettingKey(
//      "vine-growth",
//      true
//  );
//  @Blurb("Item type used as the Nope Wand")
//  @Description("The type of item to be used as the Nope Wand.")
//  @Global
//  public static final SettingKey<String> WAND_ITEM = new StringSettingKey(
//      "wand-item",
//      "minecraft:stick"
//  );
//  @Blurb("Water flow")
//  @Description("When disabled, water cannot flow")
//  @Category(SettingKey.CategoryType.BLOCKS)
//  public static final SettingKey<Boolean> WATER_FLOW = new BooleanSettingKey(
//      "water-flow",
//      true
//  );
//  @Blurb("Grief caused by water")
//  @Description("When disabled, water cannot break blocks")
//  @Category(SettingKey.CategoryType.BLOCKS)
//  public static final SettingKey<Boolean> WATER_GRIEF = new BooleanSettingKey(
//      "water-grief",
//      true
//  );
//  @Blurb("Grief caused by zombies")
//  @Description("When disabled, zombies cannot break blocks.")
//  @Category(SettingKey.CategoryType.BLOCKS)
//  public static final SettingKey<Boolean> ZOMBIE_GRIEF = new BooleanSettingKey(
//      "zombie-grief",
//      true
//  );

  /**
   * Private constructor because its not supposed to be instantiated.
   */
  private SettingKeys() {
  }

  /**
   * Prepare the SettingLibrary for easy accessing of SettingKeys.
   *
   * @throws IllegalStateException if there are multiple SettingKeys with the same ID.
   */
  public static void registerTo(SettingKeyStore keyStore) throws IllegalStateException {
    Arrays.stream(SettingKeys.class.getDeclaredFields())
        .filter(field -> Modifier.isStatic(field.getModifiers()))
        .filter(field -> SettingKey.class.isAssignableFrom(field.getType()))
        .forEach(field -> {
          try {
            keyStore.register((SettingKey<?, ?>) field.get(null));
          } catch (IllegalAccessException e) {
            e.printStackTrace();
          }
        });
    if (keyStore.isEmpty()) {
      throw new RuntimeException("Tried to initialize SettingLibrary, "
          + "but it did not appear to work");
    }
  }

  /**
   * Enumeration for all movement types considered by Nope.
   */
  public enum Movement {
    ALL,
    NATURAL,
    NONE,
    UNNATURAL
  }

  /**
   * Enumeration for all explosive types considered by Nope.
   */
  public enum Explosive {
    CREEPER,
    ENDERCRYSTAL,
    FIREWORK,
    LARGEFIREBALL,
    PRIMEDTNT,
    TNTMINECART,
    WITHER,
    WITHERSKULL
  }

}
