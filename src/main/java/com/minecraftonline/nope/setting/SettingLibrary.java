/*
 * MIT License
 *
 * Copyright (c) 2020 MinecraftOnline
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
 *
 */

package com.minecraftonline.nope.setting;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.update.SettingUpdates;
import com.minecraftonline.nope.util.Format;
import com.minecraftonline.nope.util.NopeTypeTokens;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.entity.EnderCrystal;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.explosive.Explosive;
import org.spongepowered.api.entity.explosive.PrimedTNT;
import org.spongepowered.api.entity.living.monster.Creeper;
import org.spongepowered.api.entity.living.monster.Wither;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.entity.projectile.Firework;
import org.spongepowered.api.entity.projectile.explosive.WitherSkull;
import org.spongepowered.api.entity.projectile.explosive.fireball.LargeFireball;
import org.spongepowered.api.entity.vehicle.minecart.TNTMinecart;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.annotation.*;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public final class SettingLibrary {

  @Description("When disabled, armor stands may not be broken by players")
  @Category(SettingKey.CategoryType.ENTITY)
  @PlayerRestrictive
  public static final SettingKey<Boolean> ARMOR_STAND_DESTROY = new StateSetting(
      "armor-stand-destroy",
      true
  );
  @Description("When disabled, armor stands may not be interacted with by a player")
  @Category(SettingKey.CategoryType.ENTITY)
  @PlayerRestrictive
  public static final SettingKey<Boolean> ARMOR_STAND_INTERACT = new StateSetting(
      "armor-stand-interact",
      true
  );
  @Description("When disabled, armor stands may not be placed")
  @Category(SettingKey.CategoryType.ENTITY)
  @PlayerRestrictive
  public static final SettingKey<Boolean> ARMOR_STAND_PLACE = new StateSetting(
      "armor-stand-place",
      true
  );
  @Description("When disabled, blocks may not be broken by players")
  @Category(SettingKey.CategoryType.BLOCKS)
  @PlayerRestrictive
  public static final SettingKey<Boolean> BLOCK_BREAK = new StateSetting(
      "block-break",
      true
  );
  @Description("When disabled, blocks may not be placed by players")
  @Category(SettingKey.CategoryType.BLOCKS)
  @PlayerRestrictive
  public static final SettingKey<Boolean> BLOCK_PLACE = new StateSetting(
      "block-place",
      true
  );
  @Description("When disabled, block updates will not affect others across the zone boundary")
  @Category(SettingKey.CategoryType.BLOCKS)
  public static final SettingKey<Boolean> BLOCK_PROPAGATE_ACROSS = new BooleanSetting(
      "block-propagate-across",
      true
  );
  @Description("When disabled, block updates will not affect others within the zone")
  @Category(SettingKey.CategoryType.BLOCKS)
  public static final SettingKey<Boolean> BLOCK_PROPAGATE_WITHIN = new BooleanSetting(
      "block-propagate-within",
      true
  );
  @Description("When disabled, blocks like farmland may not be trampled")
  @Category(SettingKey.CategoryType.BLOCKS)
  @PlayerRestrictive
  public static final SettingKey<Boolean> BLOCK_TRAMPLE = new StateSetting(
      "block-trample",
      true
  );
  @Description("The quantity of block locations to cache for each world. "
      + "Total memory is roughly this multiplied by 56 bytes, "
      + "multiplied by the number of worlds. Set 0 to disable caching.")
  @Global
  public static final SettingKey<Integer> CACHE_SIZE = new PositiveIntegerSetting(
      "cache-size",
      75000
  );
  @Description("When disabled, players may not open chests")
  @Category(SettingKey.CategoryType.BLOCKS)
  @PlayerRestrictive
  public static final SettingKey<Boolean> CHEST_ACCESS = new StateSetting(
      "chest-access",
      true
  );
  @Description("When disabled, players may not teleport by eating a chorus fruit")
  @NotImplemented
  @PlayerRestrictive
  public static final SettingKey<Boolean> CHORUS_FRUIT_TELEPORT = new StateSetting(
      "chorus-fruit-teleport",
      true
  );
  @Description("When disabled, concrete powder does not solidify into concrete")
  @Category(SettingKey.CategoryType.BLOCKS)
  public static final SettingKey<Boolean> CONCRETE_SOLIDIFICATION = new BooleanSetting(
      "concrete-solidification",
      true
  );
  @Description("When disabled, crops do not grow")
  @Category(SettingKey.CategoryType.BLOCKS)
  @PlayerRestrictive
  public static final SettingKey<Boolean> CROP_GROWTH = new BooleanSetting(
      "crop-growth",
      true
  );
  @Description("Deop the player upon entering")
  @NotImplemented
  @PlayerRestrictive
  public static final SettingKey<Boolean> DEOP_ON_ENTER = new BooleanSetting(
      "deop-on-enter",
      false
  );
  @Description("When disabled, experience points are never dropped")
  @NotImplemented
  @PlayerRestrictive
  public static final SettingKey<Boolean> DROP_EXP = new BooleanSetting(
      "drop-exp",
      false
  );
  @Description("Enables grief caused by the enderdragon")
  @Category(SettingKey.CategoryType.BLOCKS)
  public static final SettingKey<Boolean> ENDERDRAGON_GRIEF = new BooleanSetting(
      "enderdragon-grief",
      true
  );
  @Description("When disabled, endermen do not grief blocks by picking them up")
  @Category(SettingKey.CategoryType.BLOCKS)
  public static final SettingKey<Boolean> ENDERMAN_GRIEF = new BooleanSetting(
      "enderman-grief",
      true
  );
  @Description("When disabled, enderpearls may not be used for teleportation")
  @Category(SettingKey.CategoryType.MOVEMENT)
  @PlayerRestrictive
  public static final SettingKey<Boolean> ENDERPEARL_TELEPORT = new StateSetting(
      "enderpearl-teleport",
      true
  );
  @Description("Specify which type of movement is allowed by players to enter")
  @Category(SettingKey.CategoryType.MOVEMENT)
  @PlayerRestrictive
  public static final SettingKey<Movement> ENTRY = new EnumSetting<>(
      "entry",
      Movement.ALL,
      Movement.class
  );
  @Description("The message that is sent to a player if they are barred from entry")
  @Category(SettingKey.CategoryType.MOVEMENT)
  public static final SettingKey<Text> ENTRY_DENY_MESSAGE = new TextSetting(
      "entry-deny-message",
      Text.of(TextColors.RED, "You are not allowed to go there")
  );
  @Description("The subtitle that is sent to a player if they are barred from entry")
  @Category(SettingKey.CategoryType.MOVEMENT)
  public static final SettingKey<Text> ENTRY_DENY_SUBTITLE = new TextSetting(
      "entry-deny-subtitle",
      Text.EMPTY
  );
  @Description("The title that is sent to a player if they are barred from entry")
  @Category(SettingKey.CategoryType.MOVEMENT)
  public static final SettingKey<Text> ENTRY_DENY_TITLE = new TextSetting(
      "entry-deny-title",
      Text.EMPTY
  );
  @Description("When disabled, the environment cannot inflict damage on players")
  @Category(SettingKey.CategoryType.DAMAGE)
  @PlayerRestrictive
  public static final SettingKey<Boolean> EVP = new StateSetting(
      "evp",
      true
  );
  @Description("Specify which type of movement is allowed by players to exit")
  @Category(SettingKey.CategoryType.MOVEMENT)
  @PlayerRestrictive
  public static final SettingKey<Movement> EXIT = new EnumSetting<>(
      "exit",
      Movement.ALL,
      Movement.class
  );
  @Description("The message that is sent to the player if they are barred from exiting")
  @Category(SettingKey.CategoryType.MOVEMENT)
  public static final SettingKey<Text> EXIT_DENY_MESSAGE = new TextSetting(
      "exit-deny-message",
      Text.of(TextColors.RED, "You are not allowed to leave here")
  );
  @Description("The subtitle that is sent to a player if they are barred from exiting")
  @Category(SettingKey.CategoryType.MOVEMENT)
  public static final SettingKey<Text> EXIT_DENY_SUBTITLE = new TextSetting(
      "exit-deny-subtitle",
      Text.EMPTY
  );
  @Description("The title that is sent to a player if they are barred from exiting")
  @Category(SettingKey.CategoryType.MOVEMENT)
  public static final SettingKey<Text> EXIT_DENY_TITLE = new TextSetting(
      "exit-deny-title",
      Text.EMPTY
  );
  @Description("When disabled, creepers do not grief blocks when they explode")
  @Category(SettingKey.CategoryType.BLOCKS)
  public static final SettingKey<Set<ExplosiveEnum>> EXPLOSION_GRIEF_BLACKLIST = new EnumSetSetting<>(
      "explosion-block-grief-blacklist",
      new HashSet<>(),
      ExplosiveEnum.class
  );
  @Description("When disabled, creepers do not cause damage")
  @Category(SettingKey.CategoryType.DAMAGE)
  public static final SettingKey<Set<ExplosiveEnum>> EXPLOSION_DAMAGE_BLACKLIST = new EnumSetSetting<>(
      "explosion-damage-blacklist",
      new HashSet<>(),
      ExplosiveEnum.class
  );
  @Description("When disabled, players do not experience fall damage")
  @Category(SettingKey.CategoryType.DAMAGE)
  public static final SettingKey<Boolean> FALL_DAMAGE = new BooleanSetting(
      "fall-damage",
      false
  );
  @Description("The message to a player when they leave")
  @Category(SettingKey.CategoryType.MOVEMENT)
  public static final SettingKey<Text> FAREWELL = new TextSetting(
      "farewell",
      Text.EMPTY
  );
  @Description("The subtitle that appears to a player when they leave")
  @Category(SettingKey.CategoryType.MOVEMENT)
  public static final SettingKey<Text> FAREWELL_SUBTITLE = new TextSetting(
      "farewell-subtitle",
      Text.EMPTY
  );
  @Description("The title that appears to a player when they leave")
  @Category(SettingKey.CategoryType.MOVEMENT)
  public static final SettingKey<Text> FAREWELL_TITLE = new TextSetting(
      "farewell-title",
      Text.EMPTY
  );
  @Description("When disabled, fire does not spread or cause block damage")
  @Category(SettingKey.CategoryType.BLOCKS)
  public static final SettingKey<Boolean> FIRE_EFFECT = new StateSetting(
      "fire-effect",
      true
  );
  @Description("When disabled, players cannot light fire")
  @Category(SettingKey.CategoryType.BLOCKS)
  @PlayerRestrictive
  public static final SettingKey<Boolean> FIRE_IGNITION = new StateSetting(
      "fire-ignition",
      true
  );
  @Description("When disabled, fire is not started naturally")
  public static final SettingKey<Boolean> FIRE_NATURAL_IGNITION = new StateSetting(
      "fire-natural-ignition",
      true
  );
  @Description("When disabled, frosted ice does not form")
  @Category(SettingKey.CategoryType.BLOCKS)
  public static final SettingKey<Boolean> FROSTED_ICE_FORM = new StateSetting(
      "frosted-ice-form",
      true
  );
  @Description("When disabled, frosted ice does not melt")
  @Category(SettingKey.CategoryType.BLOCKS)
  public static final SettingKey<Boolean> FROSTED_ICE_MELT = new StateSetting(
      "frosted-ice-melt",
      true
  );
  @Description("The default gamemode of players")
  @NotImplemented
  public static final SettingKey<String> GAME_MODE = new CatalogTypeSetting<>(
      "game-mode",
      GameModes.NOT_SET,
      GameMode.class
  );
  @Description("When disabled, ghasts do not shoot fireballs")
  @Category(SettingKey.CategoryType.ENTITY)
  public static final SettingKey<Boolean> GHAST_FIREBALL = new StateSetting(
      "ghast-fireball",
      true
  );
  @Description("When disabled, grass does not grow naturally")
  @Category(SettingKey.CategoryType.BLOCKS)
  @PlayerRestrictive
  public static final SettingKey<Boolean> GRASS_GROWTH = new StateSetting(
      "grass-growth",
      true
  );
  @Description("The message to a player when they enter")
  @Category(SettingKey.CategoryType.MOVEMENT)
  public static final SettingKey<Text> GREETING = new TextSetting(
      "greeting",
      Text.EMPTY
  );
  @Description("The subtitle that appears to a player when they enter")
  @Category(SettingKey.CategoryType.MOVEMENT)
  public static final SettingKey<Text> GREETING_SUBTITLE = new TextSetting(
      "greeting-subtitle",
      Text.EMPTY
  );
  @Description("The title that appears to a player when they enter")
  @Category(SettingKey.CategoryType.MOVEMENT)
  public static final SettingKey<Text> GREETING_TITLE = new TextSetting(
      "greeting-title",
      Text.EMPTY
  );
  @Description("When disabled, hostile creatures cannot inflict damage on players")
  @Category(SettingKey.CategoryType.DAMAGE)
  public static final SettingKey<Boolean> HVP = new StateSetting(
      "hvp",
      true
  );
  @Description("When disabled, ice does not form")
  @Category(SettingKey.CategoryType.BLOCKS)
  public static final SettingKey<Boolean> ICE_FORM = new BooleanSetting(
      "ice-form",
      true
  );
  @Description("When disabled, ice does not melt")
  @Category(SettingKey.CategoryType.BLOCKS)
  public static final SettingKey<Boolean> ICE_MELT = new BooleanSetting(
      "ice-melt",
      true
  );
  @Description("When disabled, players may not interact with any blocks")
  @Category(SettingKey.CategoryType.BLOCKS)
  @PlayerRestrictive
  public static final SettingKey<Boolean> INTERACT = new StateSetting(
      "interact",
      true
  );
  @Description("When disabled, animals are invincible")
  @Category(SettingKey.CategoryType.DAMAGE)
  public static final SettingKey<Boolean> INVINCIBLE_ANIMALS = new BooleanSetting(
      "invincible-animals",
      false
  );
  @Description("When disabled, mobs cannot take damage")
  @Category(SettingKey.CategoryType.DAMAGE)
  public static final SettingKey<Boolean> INVINCIBLE_MOBS = new BooleanSetting(
      "invincible-mobs",
      false
  );
  @Description("When enabled, players cannot take damage")
  @Category(SettingKey.CategoryType.DAMAGE)
  public static final SettingKey<Boolean> INVINCIBLE_PLAYERS = new BooleanSetting(
      "invincible-players",
      false
  );
  @Description("When disabled, players cannot drop items")
  @PlayerRestrictive
  public static final SettingKey<Boolean> ITEM_DROP = new BooleanSetting(
      "item-drop",
      true
  );
  @Description("When disabled, item frames may not be attacked by players")
  @Category(SettingKey.CategoryType.ENTITY)
  @PlayerRestrictive
  public static final SettingKey<Boolean> ITEM_FRAME_DESTROY = new StateSetting(
      "item-frame-destroy",
      true
  );
  @Description("When disabled, item frames may not be interacted with by a player")
  @Category(SettingKey.CategoryType.ENTITY)
  @PlayerRestrictive
  public static final SettingKey<Boolean> ITEM_FRAME_INTERACT = new StateSetting(
      "item-frame-interact",
      true
  );
  @Description("When disabled, item frames may not be placed")
  @Category(SettingKey.CategoryType.ENTITY)
  @PlayerRestrictive
  public static final SettingKey<Boolean> ITEM_FRAME_PLACE = new StateSetting(
      "item-frame-place",
      true
  );
  @Description("When disabled, players cannot pick up items")
  @PlayerRestrictive
  public static final SettingKey<Boolean> ITEM_PICKUP = new StateSetting(
      "item-pickup",
      true
  );
  @Description("When disabled, lava does not spread")
  @Category(SettingKey.CategoryType.BLOCKS)
  public static final SettingKey<Boolean> LAVA_FLOW = new BooleanSetting(
      "lava-flow",
      true
  );
  @Description("When disabled, lava does not break blocks")
  @Category(SettingKey.CategoryType.BLOCKS)
  public static final SettingKey<Boolean> LAVA_GRIEF = new BooleanSetting(
      "lava-grief",
      true
  );
  @Description("When disabled, leaf will not decay naturally")
  @Category(SettingKey.CategoryType.BLOCKS)
  public static final SettingKey<Boolean> LEAF_DECAY = new StateSetting(
      "leaf-decay",
      true
  );
  @Description("When disabled, players cannot put leads on mobs")
  @Category(SettingKey.CategoryType.BLOCKS)
  @PlayerRestrictive
  public static final SettingKey<Boolean> LEASH = new StateSetting(
      "leash",
      true
  );
  @Description("When disabled, lightning cannot strike")
  @Category(SettingKey.CategoryType.BLOCKS)
  public static final SettingKey<Boolean> LIGHTNING = new BooleanSetting(
      "lightning",
      true
  );
  @Description("When disabled, mushrooms do not grow")
  @Category(SettingKey.CategoryType.BLOCKS)
  @PlayerRestrictive
  public static final SettingKey<Boolean> MUSHROOM_GROWTH = new BooleanSetting(
      "mushroom-growth",
      true
  );
  @Description("When disabled, mycelium does not spread")
  @Category(SettingKey.CategoryType.BLOCKS)
  public static final SettingKey<Boolean> MYCELIUM_SPREAD = new BooleanSetting(
      "mycelium-spread",
      true
  );
  @Description("When disabled, health does not regenerate naturally")
  @PlayerRestrictive
  public static final SettingKey<Boolean> NATURAL_HEALTH_REGEN = new BooleanSetting(
      "natural-health-regen",
      true
  );
  @Description("When disabled, hunger does not drain naturally")
  @NotImplemented
  public static final SettingKey<Boolean> NATURAL_HUNGER_DRAIN = new BooleanSetting(
      "natural-hunger-drain",
      true
  );
  // TODO write description
  @NotImplemented
  public static final SettingKey<Boolean> OP_PERMISSIONS = new StateSetting(
      "op-permissions",
      true
  );
  @Description("When disabled, paintings may not be broken by players")
  @Category(SettingKey.CategoryType.ENTITY)
  @PlayerRestrictive
  public static final SettingKey<Boolean> PAINTING_DESTROY = new StateSetting(
      "painting-destroy",
      true
  );
  @Description("When disabled, paintings may not be placed")
  @Category(SettingKey.CategoryType.ENTITY)
  @PlayerRestrictive
  public static final SettingKey<Boolean> PAINTING_PLACE = new StateSetting(
      "painting-place",
      true
  );
  @Description("When disabled, all events caused by player collision are cancelled")
  @Category(SettingKey.CategoryType.ENTITY)
  public static final SettingKey<Boolean> PLAYER_COLLISION = new StateSetting(
      "player-collision",
      true
  );
  @Description("When disabled, players cannot inflict damage on animals")
  @Category(SettingKey.CategoryType.DAMAGE)
  @PlayerRestrictive
  public static final SettingKey<Boolean> PVA = new StateSetting(
      "pva",
      true
  );
  @Description("When disabled, players cannot inflict damage on hostile creatures")
  @Category(SettingKey.CategoryType.DAMAGE)
  @PlayerRestrictive
  public static final SettingKey<Boolean> PVH = new StateSetting(
      "pvh",
      true
  );
  @Description("When disabled, players cannot inflict damage on other players")
  @Category(SettingKey.CategoryType.DAMAGE)
  @PlayerRestrictive
  public static final SettingKey<Boolean> PVP = new StateSetting(
      "pvp",
      true
  );
  @Description("When disabled, players cannot ride other entities")
  @Category(SettingKey.CategoryType.ENTITY)
  @PlayerRestrictive
  public static final SettingKey<Boolean> RIDE = new StateSetting(
      "ride",
      true
  );
  @Description("When disabled, players cannot sleep")
  @PlayerRestrictive
  public static final SettingKey<Boolean> SLEEP = new StateSetting(
      "sleep",
      true
  );
  @Description("When disabled, snowmen do not make trails")
  @Category(SettingKey.CategoryType.BLOCKS)
  public static final SettingKey<Boolean> SNOWMAN_TRAILS = new StateSetting(
      "snowman-trails",
      true
  );
  @Description("When disabled, snow does not accumulate naturally")
  @Category(SettingKey.CategoryType.BLOCKS)
  public static final SettingKey<Boolean> SNOW_ACCUMULATION = new StateSetting(
      "snow-accumulation",
      true
  );
  @Description("When disabled, snow does not melt")
  @Category(SettingKey.CategoryType.BLOCKS)
  public static final SettingKey<Boolean> SNOW_MELT = new StateSetting(
      "snow-melt",
      true
  );
  @Description("When disabled, mobs cannot spawn")
  @Category(SettingKey.CategoryType.ENTITY)
  @PlayerRestrictive
  public static final SettingKey<Boolean> SPAWN_ANIMAL = new StateSetting(
      "spawn-animal",
      true
  );
  @Description("When disabled, mobs cannot spawn")
  @Category(SettingKey.CategoryType.ENTITY)
  @PlayerRestrictive
  public static final SettingKey<Boolean> SPAWN_HOSTILE = new StateSetting(
      "spawn-hostile",
      true
  );
  @Description("When disabled, mobs cannot spawn")
  @Category(SettingKey.CategoryType.ENTITY)
  @PlayerRestrictive
  public static final SettingKey<Boolean> SPAWN_MOB = new StateSetting(
      "spawn-mob",
      true
  );
  @Description("The Data Source name of the SQL database to be used if SQL is the storage type")
  @NotImplemented
  @Global
  public static final SettingKey<String> SQL_DSN = new StringSetting(
      "sql-dsn",
      "jdbc:mysql://localhost/nope"
  );
  @Description("The password for the SQL database to be used if SQL is the storage type")
  @NotImplemented
  @Global
  public static final SettingKey<String> SQL_PASSWORD = new StringSetting(
      "sql-password",
      "nope"
  );
  @Description("The table prefix to be placed before SQL tables if SQL is the storage type")
  @NotImplemented
  @Global
  public static final SettingKey<String> SQL_TABLE_PREFIX = new StringSetting(
      "sql-table-prefix",
      "nope"
  );
  @Description("The username for the SQL database to be used if SQL is the storage type")
  @NotImplemented
  @Global
  public static final SettingKey<String> SQL_USERNAME = new StringSetting(
      "sql-username",
      "nope"
  );
  @Description("The type of storage to persist Nope server state")
  @NotImplemented
  @Global
  public static final SettingKey<StorageType> STORAGE_TYPE = new EnumSetting<>(
      "storage-type",
      StorageType.HOCON,
      StorageType.class
  );
  @Description("The designated point of access to the zone via teleport")
  public static final SettingKey<Vector3d> TELEPORT_LOCATION = new Vector3dSetting(
      "teleport-location",
      Vector3d.ZERO
  );
  @Description("When disabled, tnt may not be activated")
  public static final SettingKey<Boolean> TNT_IGNITION = new StateSetting(
      "tnt-ignition",
      true
  );
  @Description("When disabled, tnt may not be placed")
  @Category(SettingKey.CategoryType.BLOCKS)
  @PlayerRestrictive
  public static final SettingKey<Boolean> TNT_PLACEMENT = new StateSetting(
      "tnt-placement",
      true
  );
  @Description("These entity types will not be allowed to spawn")
  @Category(SettingKey.CategoryType.ENTITY)
  @PlayerRestrictive
  public static final SettingKey<Set<EntityType>> UNSPAWNABLE_MOBS = new EntityTypeSetSetting(
      "unspawnable-mobs",
      Sets.newHashSet()
  );
  @Description("When disabled, players may not break vehicles")
  @Category(SettingKey.CategoryType.ENTITY)
  @PlayerRestrictive
  public static final SettingKey<Boolean> VEHICLE_DESTROY = new StateSetting(
      "vehicle-destroy",
      true
  );
  @Description("When disabled, players may not place vehicles")
  @Category(SettingKey.CategoryType.ENTITY)
  @PlayerRestrictive
  public static final SettingKey<Boolean> VEHICLE_PLACE = new StateSetting(
      "vehicle-place",
      true
  );
  @Description("When disabled, vines do not grow naturally")
  @Category(SettingKey.CategoryType.BLOCKS)
  public static final SettingKey<Boolean> VINE_GROWTH = new StateSetting(
      "vine-growth",
      true
  );
  @Description("The type of item to be used as the Nope wand")
  @Global
  public static final SettingKey<String> WAND_ITEM = new CatalogTypeSetting<>(
      "wand-item",
      ItemTypes.STICK,
      ItemType.class
  );
  @Description("When disabled, water cannot flow")
  @Category(SettingKey.CategoryType.BLOCKS)
  public static final SettingKey<Boolean> WATER_FLOW = new BooleanSetting(
      "water-flow",
      true
  );
  @Description("When disabled, water cannot break blocks")
  @Category(SettingKey.CategoryType.BLOCKS)
  public static final SettingKey<Boolean> WATER_GRIEF = new BooleanSetting(
      "water-grief",
      true
  );
  @Description("When disabled, zombies cannot break blocks")
  @Category(SettingKey.CategoryType.BLOCKS)
  public static final SettingKey<Boolean> ZOMBIE_GRIEF = new BooleanSetting(
      "zombie-grief",
      true
  );
  @Description("The plugins that cannot break restrictive setting rules")
  @Category(SettingKey.CategoryType.MISC)
  public static final SettingKey<Set<String>> RESTRICTED_PLUGINS = new StringSetSetting(
      "restricted-plugins",
      new HashSet<>()
  );
  private static final String SET_SPLIT_REGEX = "(?<![ ,])(( )+|( *, *))(?![ ,])";  //"(, )|[ ,]";
  private static final HashMap<String, SettingKey<?>> settingMap = Maps.newHashMap();

  /**
   * Private constructor because its not supposed to be instantiated.
   */
  private SettingLibrary() {
  }

  private static void ensureInitialized() {
    if (settingMap.isEmpty()) {
      throw new RuntimeException("The SettingLibrary must be initialized");
    }
  }

  /**
   * Get a SettingKey based on its id.
   *
   * @param id the id of a SettingKey
   * @return the SettingKey keyed with that id
   * @throws NoSuchElementException if there is no SettingKey with that id
   */
  public static SettingKey<?> lookup(@Nonnull String id) throws NoSuchElementException {
    ensureInitialized();
    SettingKey<?> output = settingMap.get(id);
    if (output == null) {
      throw new NoSuchElementException(String.format(
          "There is no setting with id '%s'",
          id));
    }
    return output;
  }

  public static Collection<SettingKey<?>> getAll() {
    ensureInitialized();
    return settingMap.values();
  }

  /**
   * Prepare the SettingLibrary for easy accessing of SettingKeys.
   *
   * @throws IllegalStateException if there are multiple SettingKeys with the same ID.
   */
  public static void initialize() throws IllegalStateException {
    Arrays.stream(SettingLibrary.class.getDeclaredFields())
        .filter(field -> Modifier.isStatic(field.getModifiers()))
        .filter(field -> SettingKey.class.isAssignableFrom(field.getType()))
        .forEach(field -> {
          try {
            SettingKey<?> key = (SettingKey<?>) field.get(null);
            if (settingMap.put(key.getId(), key) != null) {
              throw new IllegalStateException("SettingKeys may not have the same id: " + key.getId());
            }
            for (Annotation annotation : field.getAnnotations()) {
              if (annotation instanceof Description) {
                key.setDescription(((Description) annotation).value());
              } else if (annotation instanceof Category) {
                key.setCategory(((Category) annotation).value());
              } else if (annotation instanceof Global) {
                key.setGlobal(true);
              } else if (annotation instanceof NotImplemented) {
                key.setImplemented(false);
              } else if (annotation instanceof UnnaturalDefault) {
                key.setUnnaturalDefault(true);
              } else if (annotation instanceof PlayerRestrictive) {
                key.setPlayerRestrictive(true);
              }
            }
          } catch (IllegalAccessException e) {
            e.printStackTrace();
          }
        });
    if (settingMap.isEmpty()) {
      throw new RuntimeException("Tried to initialize SettingLibrary, "
          + "but it did not appear to work");
    }
  }

  /**
   * Send a SettingMap to a JsonElement to be saved elsewhere
   * so that it can be restored later using
   * {@link #deserializeSettingAssignments(JsonElement, String)} )}.
   *
   * @param map the map of settings
   * @return the finished json element
   */
  public static JsonElement serializeSettingAssignments(SettingMap map) {
    List<Map<String, Object>> settingList = Lists.newLinkedList();
    for (Setting<?> setting : map.entries()) {
      Map<String, Object> elem = Maps.newHashMap();
      elem.put("id", setting.getKey().getId());
      // This does not deserialize:
      if (setting.getKey().getDescription() != null) {
        elem.put("description", setting.getKey().getDescription());
      }
      // This does not deserialize
      elem.put("restricted", setting.getKey().isPlayerRestrictive());
      elem.put("value", setting.getKey().dataToJson(setting.getValue().getData()));
      elem.put("target", SettingValue.Target.toJson(setting.getValue().getTarget()));
      settingList.add(elem);
    }
    return new Gson().toJsonTree(settingList);
  }

  /**
   * Rebuild a SettingMap from a JsonElement that was stored
   * in some persistent storage location.
   *
   * @param json the json object
   * @return the restored SettingMap
   */
  @SuppressWarnings("unchecked")
  public static SettingMap deserializeSettingAssignments(JsonElement json, String hostName) {
    JsonElement element = json.getAsJsonObject().get("settings");
    SettingMap map = new SettingMap();

    if (element == null) {
      return map;
    }

    JsonArray serializedSettings = element.getAsJsonArray();
    for (JsonElement serializedSetting : serializedSettings) {
      JsonObject object = serializedSetting.getAsJsonObject();
      SettingKey<?> key;

      try {
        key = lookup(object.get("id").getAsString());
      } catch (NoSuchElementException e) {
        Optional<? extends Setting<?>> updated = SettingUpdates.convertSetting(
            object.get("id").getAsString(),
            object.get("value"),
            object.get("target"));
        if (updated.isPresent()) {
          Nope.getInstance().getLogger().warn("Old SettingKey id: "
              + object.get("id")
              + ". This was replaced with the a new Setting with SettingKey: "
              + updated.get().getKey().getId());
          map.put(updated.get());
        } else {
          Nope.getInstance().getLogger().error("Invalid SettingKey id: "
              + object.get("id")
              + ". Is this old? Skipping...");
        }
        continue;
      }

      SettingValue<Object> val;
      try {
        val = SettingValue.of(
            key.dataFromJson(object.get("value")),
            SettingValue.Target.fromJson(object.get("target")));
      } catch (SettingKey.ParseSettingException e) {
        Nope.getInstance().getLogger().error("Host: "
            + hostName
            + ", Invalid SettingKey value: "
            + object.get("value")
            + ". Is this old? Skipping...");
        continue;
      }
      map.put(Setting.of((SettingKey<Object>) key, val));
    }
    return map;
  }

  public enum Movement {
    ALL,
    UNNATURAL,
    NONE
  }

  public enum StorageType {
    MARIADB,
    SQLITE,
    HOCON
  }

  public enum ExplosiveEnum {
    CREEPER(Creeper.class),
    ENDERCRYSTAL(EnderCrystal.class),
    FIREWORK(Firework.class),
    LARGEFIREBALL(LargeFireball.class),
    PRIMEDTNT(PrimedTNT.class),
    TNTMINECART(TNTMinecart.class),
    WITHER(Wither.class),
    WITHERSKULL(WitherSkull.class);

    private final Class<? extends Explosive> wrapped;

    ExplosiveEnum(Class<? extends Explosive> wrapped) {
      this.wrapped = wrapped;
    }

    public Class<? extends Explosive> getExplosive() {
      return wrapped;
    }
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface Description {
    /**
     * The description of a {@link SettingKey}.
     *
     * @return SettingKey description
     */
    String value();
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface Global {
    // Empty
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface Category {
    /**
     * The category of a {@link SettingKey}.
     *
     * @return SettingKey category
     */
    SettingKey.CategoryType value();
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface NotImplemented {
    // Empty
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface PlayerRestrictive {
    // Empty
  }

  /**
   * An annotation for SettingKeys to designate its
   * default value as unnatural. This is used for
   * listener initializations.
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface UnnaturalDefault {
    // Empty
  }

  public static class BooleanSetting extends SettingKey<Boolean> {
    public BooleanSetting(String id, Boolean defaultValue) {
      super(id, defaultValue);
    }

    @Override
    public Boolean parse(String data) throws ParseSettingException {
      switch (data.toLowerCase()) {
        case "true":
        case "t":
          return true;
        case "false":
        case "f":
          return false;
        default:
          throw new ParseSettingException("Allowed values: t, true, f, false");
      }
    }

    @Override
    public Optional<List<String>> getParsable() {
      return Optional.of(Lists.newArrayList("true", "false", "t", "f"));
    }
  }

  public static class PositiveIntegerSetting extends SettingKey<Integer> {
    public PositiveIntegerSetting(String id, Integer defaultValue) {
      super(id, defaultValue);
    }

    @Override
    public Integer dataFromJsonGenerified(JsonElement json) throws ParseSettingException {
      int integer = json.getAsInt();
      if (integer < 0) {
        throw new ParseSettingException("Data must be a positive integer");
      }
      return integer;
    }

    @Override
    public Integer parse(String data) throws ParseSettingException {
      int integer;
      try {
        integer = Integer.parseInt(data);
      } catch (NumberFormatException e) {
        throw new ParseSettingException("Data must be an integer");
      }
      if (integer < 0) {
        throw new ParseSettingException("Data must be a positive integer");
      }
      return integer;
    }
  }

  public static class DoubleSetting extends SettingKey<Double> {
    public DoubleSetting(String id, Double defaultValue) {
      super(id, defaultValue);
    }
  }

  public static class StringSetting extends SettingKey<String> {
    public StringSetting(String id, String defaultValue) {
      super(id, defaultValue);
    }
  }

  public static class StateSetting extends SettingKey<Boolean> {
    public StateSetting(String id, Boolean defaultValue) {
      super(id, defaultValue);
    }

    @Override
    public JsonElement dataToJsonGenerified(Boolean value) {
      return new JsonPrimitive(value ? "allow" : "deny");
    }

    @Override
    public Boolean dataFromJsonGenerified(JsonElement jsonElement) {
      final String s = jsonElement.getAsString();
      return parse(s);
    }

    @Override
    public Boolean parse(String s) throws ParseSettingException {
      switch (s.toLowerCase()) {
        case "allow":
        case "true":
          return true;
        case "deny":
        case "false":
          return false;
        default:
          throw new ParseSettingException("Invalid state string. "
              + "Should be allow or deny. Was: " + s);
      }
    }

    @Override
    public Optional<List<String>> getParsable() {
      return Optional.of(Lists.newArrayList("allow", "deny"));
    }
  }

  public static class TextSetting extends SettingKey<Text> {

    protected TextSetting(String id, Text defaultValue) {
      super(id, defaultValue);
    }

    @Override
    protected JsonElement dataToJsonGenerified(Text data) {
      try {
        return new Gson().toJsonTree(DataFormats.JSON.write(data.toContainer()));
      } catch (IOException e) {
        Nope.getInstance().getLogger().error("Could not serialize Text", e);
        return new Gson().toJsonTree("");
      }
    }

    @Override
    public Text dataFromJsonGenerified(JsonElement json) {
      try {
        return Sponge.getDataManager()
            .deserialize(Text.class, DataFormats.JSON.read(json.getAsString()))
            .orElseThrow(() -> new RuntimeException(
                "The json for Text cannot be serialized: "
                    + json.toString()));
      } catch (IllegalStateException | IOException e) {
        Nope.getInstance().getLogger().error("Could not deserialize Text", e);
        return Text.EMPTY;
      }
    }

    @Nonnull
    @Override
    public Text print(Text data) {
      return data;
    }

    @Override
    public Text parse(String s) throws ParseSettingException {
      return TextSerializers.FORMATTING_CODE.deserialize(s);
    }
  }

  public static class EnumSetting<E extends Enum<E>> extends SettingKey<E> {

    private final Class<E> enumClass;

    protected EnumSetting(String id, E defaultData, Class<E> enumClass) {
      super(id, defaultData);
      this.enumClass = enumClass;
    }

    @Override
    protected JsonElement dataToJsonGenerified(E data) {
      return new Gson().toJsonTree(data.name().toLowerCase());
    }

    @Override
    public E dataFromJsonGenerified(JsonElement json) {
      return parse(json.getAsString());
    }

    @Override
    public E parse(String s) throws ParseSettingException {
      try {
        return Enum.valueOf(enumClass, s.toUpperCase());
      } catch (IllegalArgumentException ex) {
        throw new ParseSettingException(s + " is not a valid "
            + enumClass.getSimpleName()
            + " type. "
            + (
            (enumClass.getEnumConstants().length <= 8)
                ? "Allowed types: "
                + Arrays.stream(enumClass.getEnumConstants()).map(e ->
                e.toString().toLowerCase()).collect(Collectors.joining(", "))
                : ""));
      }
    }

    @Override
    public Optional<List<String>> getParsable() {
      return Optional.of(Arrays.stream(enumClass.getEnumConstants())
          .map(E::toString)
          .map(String::toLowerCase)
          .collect(Collectors.toList()));
    }
  }

  public static class StringSetSetting extends SettingKey<Set<String>> {

    public StringSetSetting(String id, Set<String> defaultValue) {
      super(id, defaultValue);
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public JsonElement dataToJsonGenerified(Set<String> value) {
      return new Gson().toJsonTree(value, NopeTypeTokens.STRING_SET_TOKEN.getType());
    }

    @Override
    public Set<String> dataFromJsonGenerified(JsonElement jsonElement) {
      final Set<String> set = new HashSet<>();
      jsonElement.getAsJsonArray().forEach(element -> set.add(element.getAsString()));
      return set;
    }

    @Override
    public Set<String> parse(String s) throws ParseSettingException {
      return new HashSet<>(Arrays.asList(s.split(SET_SPLIT_REGEX)));
    }
  }

  public static class EnumSetSetting<E extends Enum<E>> extends SettingKey<Set<E>> {
    private final Class<E> enumClass;

    public EnumSetSetting(String id, Set<E> defaultValue, Class<E> enumClass) {
      super(id, defaultValue);
      this.enumClass = enumClass;
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public JsonElement dataToJsonGenerified(Set<E> value) {
      return new Gson().toJsonTree(value, NopeTypeTokens.STRING_SET_TOKEN.getType());
    }

    @Override
    public Set<E> dataFromJsonGenerified(JsonElement jsonElement) {
      final Set<E> set = new HashSet<>();
      jsonElement.getAsJsonArray().forEach(element ->
          set.add(Enum.valueOf(enumClass, element.getAsString().toUpperCase())));
      return set;
    }

    @Override
    public Set<E> parse(String s) throws ParseSettingException {
      Set<E> set = new HashSet<>();
      for (String token : s.split(SET_SPLIT_REGEX)) {
        try {
          set.add(Enum.valueOf(enumClass, token.toUpperCase()));
        } catch (IllegalArgumentException ex) {
          throw new ParseSettingException(token + " is not a valid "
              + enumClass.getSimpleName()
              + " type. "
              + (
              (enumClass.getEnumConstants().length <= 8)
                  ? "Allowed types: "
                  + Arrays.stream(enumClass.getEnumConstants()).map(e ->
                  e.toString().toLowerCase()).collect(Collectors.joining(", "))
                  : ""));
        }
      }
      return set;
    }

    @Nonnull
    @Override
    public Text print(Set<E> data) {
      return Text.of("[",
          data.stream().map(enu -> enu.name().toLowerCase()).collect(Collectors.joining(", ")),
          "]");
    }
  }

  public static class CatalogTypeSetting<C extends CatalogType> extends SettingKey<String> {
    private final Class<C> clazz;

    public CatalogTypeSetting(String id, C defaultData, Class<C> clazz) {
      super(id, defaultData.getId());
      this.clazz = clazz;
    }

    @Override
    public String parse(String id) throws ParseSettingException {
      Sponge.getRegistry().getType(this.clazz, id).orElseThrow(() ->
          new ParseSettingException("The given id "
              + id
              + " id not a valid "
              + this.clazz.getSimpleName()));
      return id;
    }

    @Override
    public Optional<List<String>> getParsable() {
      return Optional.of(Lists.newArrayList(Sponge.getRegistry()
          .getAllOf(this.clazz)
          .stream()
          .map(CatalogType::getName)
          .collect(Collectors.toList())));
    }
  }

  public static class EntityTypeSetSetting extends SettingKey<Set<EntityType>> {
    public EntityTypeSetSetting(String id, Set<EntityType> defaultValue) {
      super(id, defaultValue);
    }

    @Override
    public JsonElement dataToJsonGenerified(Set<EntityType> value) {
      final JsonArray jsonArray = new JsonArray();
      for (EntityType entityType : value) {
        jsonArray.add(new JsonPrimitive(entityType.getId()));
      }
      return jsonArray;
    }

    @Override
    public Set<EntityType> dataFromJsonGenerified(JsonElement jsonElement) {
      return stringsToEntityTypes(Lists.newLinkedList(jsonElement.getAsJsonArray())
          .stream()
          .map(JsonElement::getAsString)
          .collect(Collectors.toList()));
    }

    @Override
    public Set<EntityType> parse(String s) throws ParseSettingException {
      Nope.getInstance().getLogger().info("EntityType parsing string: " + s);
      return stringsToEntityTypes(Arrays.asList(s.split(SET_SPLIT_REGEX)));
    }

    private Set<EntityType> stringsToEntityTypes(Collection<String> strings) {
      Nope.getInstance().getLogger().info("Split strings: " + String.join("|", strings));
      Set<EntityType> set = new HashSet<>();
      for (String s : strings) {
        final EntityType entityType = Sponge.getRegistry()
            .getType(EntityType.class, s)
            .orElseThrow(() -> new ParseSettingException("Unknown EntityType: " + s));
        set.add(entityType);
      }
      return set;
    }
  }

  public static class Vector3dSetting extends SettingKey<Vector3d> {
    public Vector3dSetting(String id, Vector3d defaultValue) {
      super(id, defaultValue);
    }

    @Override
    public JsonElement dataToJsonGenerified(Vector3d value) {
      final JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("x", value.getX());
      jsonObject.addProperty("y", value.getY());
      jsonObject.addProperty("z", value.getZ());
      return jsonObject;
    }

    @Override
    public Vector3d dataFromJsonGenerified(JsonElement jsonElement) {
      final JsonObject jsonObject = jsonElement.getAsJsonObject();
      return Vector3d.from(
          jsonObject.get("x").getAsDouble(),
          jsonObject.get("y").getAsDouble(),
          jsonObject.get("z").getAsDouble()
      );
    }

    @Override
    public Vector3d parse(String s) throws ParseSettingException {
      String[] parts = s.split(SET_SPLIT_REGEX, 3);
      if (parts.length != 3) {
        throw new ParseSettingException("Expected 3 parts for Vector3d, got " + parts.length);
      }
      int i = 0;
      try {
        double x = Double.parseDouble(parts[i++]);
        double y = Double.parseDouble(parts[i++]);
        double z = Double.parseDouble(parts[i]);
        if (Math.max(Math.abs(x), Math.abs(z)) > Nope.WORLD_RADIUS
            || Math.abs(y) > Nope.WORLD_DEPTH) {
          throw new ParseSettingException("The magnitudes of these numbers are too high!");
        }
        return Vector3d.from(x, y, z);
      } catch (NumberFormatException e) {
        throw new ParseSettingException("Value at position " + i + ", "
            + "could not be parsed into a double");
      }
    }

    @Nonnull
    @Override
    public Text print(Vector3d data) {
      return Text.of(Format.keyValue("x:", String.valueOf(data.getX())),
          ", ",
          Format.keyValue("y:", String.valueOf(data.getY())),
          ", ",
          Format.keyValue("z:", String.valueOf(data.getZ())));
    }
  }

}
