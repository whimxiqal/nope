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

import com.minecraftonline.nope.common.setting.sets.BlockSet;
import com.minecraftonline.nope.common.setting.sets.EntitySet;
import com.minecraftonline.nope.common.setting.sets.ExplosiveSet;
import com.minecraftonline.nope.common.setting.sets.MovementSet;
import com.minecraftonline.nope.common.struct.AltSet;
import java.lang.reflect.Modifier;
import java.util.Arrays;

@SuppressWarnings("unused")
public class SettingKeys {

  public static final SettingKey.Unary<Boolean> ARMOR_STAND_DESTROY =
      SettingKey.Unary.builder("armor-stand-destroy", true, SettingKeyManagers.STATE_KEY_MANAGER)
          .blurb("Armor stand destruction restriction")
          .description("When disabled, armor stands may not be broken by players.")
          .category(SettingKey.Category.ENTITIES)
          .playerRestrictive()
          .build();

  public static final SettingKey.Unary<Boolean> ARMOR_STAND_INTERACT =
      SettingKey.Unary.builder("armor-stand-interact", true, SettingKeyManagers.STATE_KEY_MANAGER)
          .blurb("Armor stand interact restriction")
          .description("When disabled, armor stands may not be interacted with by players.")
          .category(SettingKey.Category.ENTITIES)
          .playerRestrictive()
          .build();

  public static final SettingKey.Unary<Boolean> ARMOR_STAND_PLACE =
      SettingKey.Unary.builder("armor-stand-place", true, SettingKeyManagers.STATE_KEY_MANAGER)
          .blurb("Armor stand placement restriction")
          .description("When disabled, armor stands may not be placed by players.")
          .category(SettingKey.Category.ENTITIES)
          .playerRestrictive()
          .build();

  public static final SettingKey.Unary<Boolean> BLOCK_BREAK =
      SettingKey.Unary.builder("block-break", true, SettingKeyManagers.STATE_KEY_MANAGER)
          .blurb("Block break restriction")
          .description("When disabled, blocks may not be broken by players.")
          .category(SettingKey.Category.BLOCKS)
          .playerRestrictive()
          .build();

  public static final SettingKey.Unary<Boolean> BLOCK_PLACE =
      SettingKey.Unary.builder("block-place", true, SettingKeyManagers.STATE_KEY_MANAGER)
          .blurb("Block place restriction")
          .description("When disabled, blocks may not be placed by players.")
          .category(SettingKey.Category.BLOCKS)
          .playerRestrictive()
          .build();

  public static final SettingKey.Unary<Boolean> BLOCK_PROPAGATE_ACROSS =
      SettingKey.Unary.builder("block-propagate-across", true, SettingKeyManagers.BOOLEAN_KEY_MANAGER)
          .blurb("Inside to outside block updates")
          .description("When disabled, block updates will not affect others across the zone boundary.")
          .category(SettingKey.Category.BLOCKS)
          .build();

  public static final SettingKey.Unary<Boolean> BLOCK_PROPAGATE_WITHIN =
      SettingKey.Unary.builder("block-propagate-within", true, SettingKeyManagers.BOOLEAN_KEY_MANAGER)
          .blurb("Inside to inside block updates")
          .description("When disabled, block updates will not affect others within the zone.")
          .category(SettingKey.Category.BLOCKS)
          .build();

  public static final SettingKey.Unary<Boolean> TRAMPLE =
      SettingKey.Unary.builder("trample", true, SettingKeyManagers.STATE_KEY_MANAGER)
          .blurb("Farmland trample restriction")
          .description("When disabled, blocks like farmland may not be trampled.")
          .category(SettingKey.Category.BLOCKS)
          .playerRestrictive()
          .build();

  public static final SettingKey.Unary<Integer> CACHE_SIZE =
      SettingKey.Unary.builder("cache-size", 75000, SettingKeyManagers.INTEGER_KEY_MANAGER)
          .blurb("Size of world block caches")
          .description("This is the quantity of block locations to cache for each world. "
      + "Total memory is roughly this multiplied by 56 bytes, "
      + "multiplied by the number of worlds. Set 0 to disable caching.")
          .category(SettingKey.Category.ENTITIES)
          .playerRestrictive()
          .global()
          .functional()
          .build();

  public static final SettingKey.Unary<Boolean> CHEST_ACCESS =
      SettingKey.Unary.builder("chest-access", true, SettingKeyManagers.STATE_KEY_MANAGER)
          .blurb("Chest access restriction")
          .description("When disabled, players may not open chests.")
          .category(SettingKey.Category.BLOCKS)
          .playerRestrictive()
          .build();

  public static final SettingKey.Unary<Boolean> CHORUS_FRUIT_TELEPORT =
      SettingKey.Unary.builder("chorus-fruit-teleport", true, SettingKeyManagers.STATE_KEY_MANAGER)
          .blurb("Chorus fruit teleport restriction")
          .description("When disabled, players may not teleport by eating a chorus fruit.")
          .category(SettingKey.Category.MOVEMENT)
          .playerRestrictive()
          .build();

  public static final SettingKey.Unary<Boolean> CONCRETE_SOLIDIFICATION =
      SettingKey.Unary.builder("concrete-solidification", true, SettingKeyManagers.BOOLEAN_KEY_MANAGER)
          .blurb("Concrete powder solidification")
          .description("When disabled, concrete powder does not solidify into concrete.")
          .category(SettingKey.Category.BLOCKS)
          .playerRestrictive()
          .build();

  public static final SettingKey.Unary<Boolean> CROP_GROWTH =
      SettingKey.Unary.builder("crop-growth", true, SettingKeyManagers.BOOLEAN_KEY_MANAGER)
          .blurb("Crop growth")
          .description("When disabled, crops do not grow.")
          .category(SettingKey.Category.BLOCKS)
          .playerRestrictive()
          .build();

  public static final SettingKey.Unary<Boolean> DROP_EXP =
      SettingKey.Unary.builder("drop-exp-entity", true, SettingKeyManagers.BOOLEAN_KEY_MANAGER)
          .blurb("Entity experience drop")
          .description("When disabled, experience points are never dropped.")
          .category(SettingKey.Category.MISC)
          .build();

  public static final SettingKey.Unary<Boolean> ENDERDRAGON_GRIEF =
      SettingKey.Unary.builder("enderdragon-grief", true, SettingKeyManagers.BOOLEAN_KEY_MANAGER)
          .blurb("Grief caused by the Enderdragon")
          .description("Enables grief caused by the Enderdragon.")
          .category(SettingKey.Category.BLOCKS)
          .build();

  public static final SettingKey.Unary<Boolean> ENDERMAN_GRIEF =
      SettingKey.Unary.builder("enderman-grief", true, SettingKeyManagers.BOOLEAN_KEY_MANAGER)
          .blurb("Grief caused by endermen")
          .description("When disabled, endermen do not grief blocks by picking them up.")
          .category(SettingKey.Category.BLOCKS)
          .build();

  public static final SettingKey.Unary<Boolean> ENDERPEARL_TELEPORT =
      SettingKey.Unary.builder("enderpearl-teleport", true, SettingKeyManagers.STATE_KEY_MANAGER)
          .blurb("Enderpearl teleport restriction")
          .description("When disabled, enderpearls may not be used for teleportation")
          .category(SettingKey.Category.MOVEMENT)
          .playerRestrictive()
          .build();

  public static final SettingKey.Unary<MovementSet.Movement> ENTRY =
      SettingKey.Unary.builder("entry", MovementSet.Movement.ALL, SettingKeyManagers.MOVEMENT_KEY_MANAGER)
          .blurb("Host entrance restriction")
          .description("Specify which type of movement is allowed by players to enter.")
          .category(SettingKey.Category.MOVEMENT)
          .playerRestrictive()
          .build();

  public static final SettingKey.Unary<String> ENTRY_DENY_MESSAGE =
      SettingKey.Unary.builder("entry-deny-message",
              "You are not allowed to go there",
              SettingKeyManagers.STRING_KEY_MANAGER)
          .blurb("Message when entry is denied")
          .description("The message that is sent to a player if they are barred from entry.")
          .category(SettingKey.Category.MOVEMENT)
          .build();

  public static final SettingKey.Unary<String> ENTRY_DENY_SUBTITLE =
      SettingKey.Unary.builder("entry-deny-subtitle", "", SettingKeyManagers.STRING_KEY_MANAGER)
          .blurb("Subtitle when entry is denied")
          .description("The subtitle that is sent to a player if they are barred from entry.")
          .category(SettingKey.Category.MOVEMENT)
          .build();

  public static final SettingKey.Unary<String> ENTRY_DENY_TITLE =
      SettingKey.Unary.builder("entry-deny-title", "", SettingKeyManagers.STRING_KEY_MANAGER)
          .blurb("Title when entry is denied")
          .description("The title that is sent to a player if they are barred from entry.")
          .category(SettingKey.Category.MOVEMENT)
          .build();

  public static final SettingKey.Unary<Boolean> EVP =
      SettingKey.Unary.builder("evp", true, SettingKeyManagers.BOOLEAN_KEY_MANAGER)
          .blurb("Environment-to-player damage")
          .description("When disabled, the environment cannot inflict damage on players.")
          .category(SettingKey.Category.DAMAGE)
          .build();

  public static final SettingKey.Unary<MovementSet.Movement> EXIT =
      SettingKey.Unary.builder("exit", MovementSet.Movement.ALL, SettingKeyManagers.MOVEMENT_KEY_MANAGER)
          .blurb("Host exit restriction")
          .description("Specify which type of movement is allowed by players to exit.")
          .category(SettingKey.Category.MOVEMENT)
          .playerRestrictive()
          .build();

  public static final SettingKey.Unary<String> EXIT_DENY_MESSAGE =
      SettingKey.Unary.builder("exit-deny-message",
              "You are not allowed to leave here",
              SettingKeyManagers.STRING_KEY_MANAGER)
          .blurb("Message when exit is denied")
          .description("The message that is sent to the player if they are barred from exiting.")
          .category(SettingKey.Category.MOVEMENT)
          .build();

  public static final SettingKey.Unary<String> EXIT_DENY_SUBTITLE =
      SettingKey.Unary.builder("exit-deny-subtitle", "", SettingKeyManagers.STRING_KEY_MANAGER)
          .blurb("Subtitle when exit is denied")
          .description("The subtitle that is sent to a player if they are barred from exiting")
          .category(SettingKey.Category.MOVEMENT)
          .build();

  public static final SettingKey.Unary<String> EXIT_DENY_TITLE =
      SettingKey.Unary.builder("exit-deny-title", "", SettingKeyManagers.STRING_KEY_MANAGER)
          .blurb("Title when exit is denied")
          .description("The title that is sent to a player if they are barred from exiting")
          .category(SettingKey.Category.MOVEMENT)
          .build();

  public static final SettingKey.Poly<ExplosiveSet.Explosive, ExplosiveSet> HARMLESS_EXPLOSIVES =
      SettingKey.Poly.builder("harmless-explosives", new ExplosiveSet(), SettingKeyManagers.POLY_EXPLOSIVE_KEY_MANAGER)
          .blurb("Explosives causing no entity damage")
          .description("A list of explosives whose explosions do not cause damage to entities.")
          .category(SettingKey.Category.DAMAGE)
          .build();

  public static final SettingKey.Poly<ExplosiveSet.Explosive, ExplosiveSet> NONDESTRUCTIVE_EXPLOSIVES =
      SettingKey.Poly.builder("nondestructive-explosives", new ExplosiveSet(), SettingKeyManagers.POLY_EXPLOSIVE_KEY_MANAGER)
          .blurb("Explosives causing no world damage")
          .description("A list of explosives whose explosions to not cause damage to the world.")
          .category(SettingKey.Category.BLOCKS)
          .build();

  public static final SettingKey.Unary<Boolean> FALL_DAMAGE =
      SettingKey.Unary.builder("fall-damage", true, SettingKeyManagers.BOOLEAN_KEY_MANAGER)
          .blurb("Fall damage")
          .description("When disabled, players are not inflicted with damage from falling")
          .category(SettingKey.Category.DAMAGE)
          .build();

  public static final SettingKey.Unary<String> FAREWELL =
      SettingKey.Unary.builder("farewell", "", SettingKeyManagers.STRING_KEY_MANAGER)
          .blurb("Message upon exit")
          .description("The message to a player when they leave the host.")
          .category(SettingKey.Category.MOVEMENT)
          .build();

  public static final SettingKey.Unary<String> FAREWELL_SUBTITLE =
      SettingKey.Unary.builder("farewell-subtitle", "", SettingKeyManagers.STRING_KEY_MANAGER)
          .blurb("Subtitle upon exit")
          .description("The subtitle that appears to a player when they leave the host.")
          .category(SettingKey.Category.MOVEMENT)
          .build();

  public static final SettingKey.Unary<String> FAREWELL_TITLE =
      SettingKey.Unary.builder("farewell-title", "", SettingKeyManagers.STRING_KEY_MANAGER)
          .blurb("Title upon exit")
          .description("The title that appears to a player when they leave the host.")
          .category(SettingKey.Category.MOVEMENT)
          .build();

  public static final SettingKey.Unary<Boolean> FIRE_EFFECT =
      SettingKey.Unary.builder("fire-effect", true, SettingKeyManagers.STATE_KEY_MANAGER)
          .blurb("Fire spread/damage")
          .description("When disabled, fire does not spread or cause block damage")
          .category(SettingKey.Category.BLOCKS)
          .build();

  public static final SettingKey.Unary<Boolean> FIRE_IGNITION =
      SettingKey.Unary.builder("fire-ignition", true, SettingKeyManagers.STATE_KEY_MANAGER)
          .blurb("Fire ignition restriction")
          .description("When disabled, players cannot light fire")
          .category(SettingKey.Category.BLOCKS)
          .playerRestrictive()
          .build();

  public static final SettingKey.Unary<Boolean> FIRE_NATURAL_IGNITION =
      SettingKey.Unary.builder("fire-natural-ignition", true, SettingKeyManagers.STATE_KEY_MANAGER)
          .blurb("Natural fire ignition")
          .description("When disabled, fire cannot be started naturally.")
          .category(SettingKey.Category.BLOCKS)
          .build();

  public static final SettingKey.Poly<String, BlockSet> INTERACTIVE_BLOCKS =
      SettingKey.Poly.builder("interactive-blocks", AltSet.full(new BlockSet()), SettingKeyManagers.POLY_BLOCK_KEY_MANAGER)
          .blurb("Interactive blocks")
          .description("A list of blocks with which that can be interacted.")
          .category(SettingKey.Category.BLOCKS)
          .playerRestrictive()
          .build();

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

public static final SettingKey.Poly<String, EntitySet> SPAWNABLE_ENTITIES =
    SettingKey.Poly.builder("spawnable-entities",
            AltSet.full(new EntitySet()),
            SettingKeyManagers.POLY_ENTITY_KEY_MANAGER)
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
            keyStore.register((SettingKey<?, ?, ?>) field.get(null));
          } catch (IllegalAccessException e) {
            e.printStackTrace();
          }
        });
    if (keyStore.isEmpty()) {
      throw new RuntimeException("Tried to initialize SettingLibrary, "
          + "but it did not appear to work");
    }
  }

}
