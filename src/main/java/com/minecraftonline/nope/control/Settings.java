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

package com.minecraftonline.nope.control;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.minecraftonline.nope.control.flags.FlagBoolean;
import com.minecraftonline.nope.control.flags.FlagDouble;
import com.minecraftonline.nope.control.flags.FlagEntitySet;
import com.minecraftonline.nope.control.flags.FlagGameMode;
import com.minecraftonline.nope.control.flags.FlagInteger;
import com.minecraftonline.nope.control.flags.FlagState;
import com.minecraftonline.nope.control.flags.FlagString;
import com.minecraftonline.nope.control.flags.FlagStringSet;
import com.minecraftonline.nope.control.flags.FlagVector3d;
import com.minecraftonline.nope.control.target.TargetSet;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * A library of methods to generate {@link Setting}s for use in configuration.
 */
public final class Settings {

  /**
   * Disable constructor.
   */
  private Settings() {
  }

  private static ImmutableSet<Setting<?>> settings;
  // This is so we can avoid having to get which settings with certain applicability by filtering repeatedly.
  // This way it can be filtered only once.
  private static ImmutableMultimap<Setting.Applicability, Setting<?>> settingApplicability;

  /**
   * Loads this class.
   * MUST be called before REGISTRY_MODULE is used
   */
  public static void load() {
    Set<Setting<?>> mutableSettings = new HashSet<>();
    Multimap<Setting.Applicability, Setting<?>> mutableSettingApplicability = HashMultimap.create();
    for (Field field : Settings.class.getFields()) {
      // Check if its a parameter. It is already only public classes, but just incase
      if (field.getType().isAssignableFrom(Setting.class)) {
        try {
          Setting<?> setting = (Setting<?>) field.get(null);
          if (setting == null) {
            System.out.println(field.getName() + " was null");
            continue;
          }
          if (mutableSettings.contains(setting)) {
            // Already have a setting with this id!
            throw new SettingNotUniqueException(setting);
          }
          mutableSettings.add(setting);
          for (Setting.Applicability applicability : Setting.Applicability.values()) {
            if (setting.isApplicable(applicability)) {
              mutableSettingApplicability.put(applicability, setting);
            }
          }
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    }
    settings = ImmutableSet.copyOf(mutableSettings);
    settingApplicability = ImmutableMultimap.copyOf(mutableSettingApplicability);
  }

  public static final SettingRegistryModule REGISTRY_MODULE = new SettingRegistryModule() {
    @Nonnull
    @Override
    public Optional<Setting<?>> getById(@Nonnull String id) {
      for (Setting<?> setting : getAll()) {
        if (setting.getId().equalsIgnoreCase(id)) {
          return Optional.of(setting);
        }
      }
      return Optional.empty();
    }

    @Nonnull
    @Override
    public Collection<Setting<?>> getAll() {
      return settings;
    }

    @Nonnull
    @Override
    public Collection<Setting<?>> getByApplicability(Setting.Applicability applicability) {
      Collection<Setting<?>> collection = settingApplicability.get(applicability);
      if (collection == null) {
        return ImmutableList.of();
      }
      return collection;
    }
  };

  public static final class SettingNotUniqueException extends IllegalArgumentException {
    public SettingNotUniqueException(Setting setting) {
      super("Multiple settings with id '" + setting.getId() + "'");
    }
  }

  // https://worldguard.enginehub.org/en/latest/config/

  // Sorts fields alphabetically
  // SORTFIELDS:ON

  public static final Setting<Boolean> BUILD_PERMISSIONS = Setting.of("build-permission-nodes-enable", false, Boolean.class);

  public static final Setting<Boolean> DEOP_ON_ENTER = Setting.of("deop-on-enter", false, Boolean.class)
      .withComment("Set to true will deop any player when they enter.")
      .withDescription("If this setting is applied globally, then anytime and op-ed player joins the server, their op status is removed. "
          + "If this setting is applied to just a world, then only when they join that specific world do they get de-opped.")
      .withApplicability(Setting.Applicability.GLOBAL, Setting.Applicability.WORLD)
      .withConfigurationPath("security.deop-on-enter");

  public static final Setting<Boolean> ENABLE_PLUGIN = Setting.of("enable-plugin", true, Boolean.class)
      .withDescription("Set to false will disable all plugin functionality")
      .withConfigurationPath("enable-plugin");

  public static final Setting<FlagStringSet> FLAG_ALLOWED_COMMANDS = Setting.of("flag-allowed-cmds", new FlagStringSet(Sets.newHashSet()), FlagStringSet.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.allowed-cmds");

  public static final Setting<FlagStringSet> FLAG_BLOCKED_COMMANDS = Setting.of("flag-blocked-cmds", new FlagStringSet(Sets.newHashSet()), FlagStringSet.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.blocked-cmds");

  public static final Setting<FlagState> FLAG_BLOCK_BREAK = Setting.of("flag-block-break", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.block-break");

  public static final Setting<FlagState> FLAG_BLOCK_PLACE = Setting.of("flag-block-place", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.block-place");

  public static final Setting<FlagState> FLAG_BLOCK_TRAMPLING = Setting.of("flag-block-trampling", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.block-trampling");

  public static final Setting<FlagState> FLAG_BUILD = Setting.of("flag-build", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.build");

  public static final Setting<FlagState> FLAG_CHEST_ACCESS = Setting.of("flag-chest-access", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.chess-access");

  public static final Setting<FlagState> FLAG_CHORUS_FRUIT_TELEPORT = Setting.of("flag-chorus-fruit-teleport", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.chorus-fruit-teleport");

  public static final Setting<FlagState> FLAG_CORAL_FADE = Setting.of("flag-coral-fade", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.coral-fade");

  public static final Setting<FlagState> FLAG_CREEPER_EXPLOSION = Setting.of("flag-creeper-explosion", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.creeper-explosion");

  public static final Setting<FlagState> FLAG_CROP_GROWTH = Setting.of("flag-crop-growth", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.crop-growth");

  public static final Setting<FlagState> FLAG_DAMAGE_ANIMALS = Setting.of("flag-damage-animals", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.damage-animals");

  public static final Setting<FlagString> FLAG_DENY_MESSAGE = Setting.of("flag-deny-message", new FlagString(""), FlagString.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.deny-message");

  public static final Setting<FlagEntitySet> FLAG_DENY_SPAWN = Setting.of("flag-deny-spawn", new FlagEntitySet(Sets.newHashSet()), FlagEntitySet.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.deny-spawn");

  public static final Setting<FlagState> FLAG_ENDERDRAGON_BLOCK_DAMAGE = Setting.of("flag-enderdragon-block-damage", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.enderdragon-block-damage");

  public static final Setting<FlagState> FLAG_ENDERMAN_GRIEF = Setting.of("flag-enderman-grief", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.enderman-grief");

  public static final Setting<FlagState> FLAG_ENDERPEAL = Setting.of("flag-enderpearl", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.enderpearl");

  public static final Setting<FlagState> FLAG_ENTITY_ITEM_FRAME_DESTROY = Setting.of("flag-entity-item-frame-destroy", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.entity-item-frame-destroy");

  public static final Setting<FlagState> FLAG_ENTITY_PAINTING_DESTROY = Setting.of("flag-entity-painting-destroy", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.entity-painting-destroy");

  public static final Setting<FlagState> FLAG_ENTRY = Setting.of("flag-entry", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.entry");

  public static final Setting<FlagString> FLAG_ENTRY_DENY_MESSAGE = Setting.of("flag-entry-deny-message", new FlagString(""), FlagString.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.entry-deny-message");

  public static final Setting<FlagState> FLAG_EXIT = Setting.of("flag-exit", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.exit");

  public static final Setting<FlagString> FLAG_EXIT_DENY_MESSAGE = Setting.of("flag-exit-deny-message", new FlagString(""), FlagString.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.exit-deny-message");

  public static final Setting<FlagBoolean> FLAG_EXIT_OVERRIDE = Setting.of("flag-exit-override", new FlagBoolean(true), FlagBoolean.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.exit-override");

  public static final Setting<FlagState> FLAG_EXIT_VIA_TELEPORT = Setting.of("flag-exit-via-teleport", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.exit-via-teleport");

  public static final Setting<FlagState> FLAG_EXP_DROPS = Setting.of("flag-exp-drops", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.exp-drops");

  public static final Setting<FlagState> FLAG_FALL_DAMAGE = Setting.of("flag-fall-damage", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.fall-damage");

  public static final Setting<FlagString> FLAG_FAREWELL = Setting.of("flag-farewell", new FlagString(""), FlagString.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.farewell");

  public static final Setting<FlagString> FLAG_FAREWELL_TITLE = Setting.of("flag-farewell-title", new FlagString(""), FlagString.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.farewell-title");

  public static final Setting<FlagInteger> FLAG_FEED_AMOUNT = Setting.of("flag-feed-amount", new FlagInteger(0), FlagInteger.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.feed-amount");

  public static final Setting<FlagInteger> FLAG_FEED_DELAY = Setting.of("flag-feed-delay", new FlagInteger(0), FlagInteger.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.feed-delay");

  public static final Setting<FlagState> FLAG_FIREWORK_DAMAGE = Setting.of("flag-firework-damage", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.firework-damage");

  public static final Setting<FlagState> FLAG_FIRE_SPREAD = Setting.of("flag-fire-spread", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.fire-spread");

  public static final Setting<FlagState> FLAG_FROSTED_ICE_FORM = Setting.of("flag-frosted-ice-form", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.frosted-ice-form");

  public static final Setting<FlagState> FLAG_FROSTED_ICE_MELT = Setting.of("flag-frosted-ice-melt", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.frosted-ice-melt");

  public static final Setting<FlagGameMode> FLAG_GAME_MODE = Setting.of("flag-game-mode", new FlagGameMode(GameModes.NOT_SET), FlagGameMode.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.flag-game-mode");

  public static final Setting<FlagState> FLAG_GHAST_FIREBALL = Setting.of("flag-ghast-fireball", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.ghast-fireball");

  public static final Setting<FlagState> FLAG_GRASS_GROWTH = Setting.of("flag-grass-growth", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.grass-growth");

  public static final Setting<FlagString> FLAG_GREETING = Setting.of("flag-greeting", new FlagString(""), FlagString.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.greeting");

  public static final Setting<FlagString> FLAG_GREETING_TITLE = Setting.of("flag-greeting-title", new FlagString(""), FlagString.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.greeting-title");

  public static final Setting<FlagInteger> FLAG_HEAL_AMOUNT = Setting.of("flag-heal-amount", new FlagInteger(0), FlagInteger.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.heal-amount");

  public static final Setting<FlagInteger> FLAG_HEAL_DELAY = Setting.of("flag-heal-delay", new FlagInteger(0), FlagInteger.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.heal-delay");

  public static final Setting<FlagDouble> FLAG_HEAL_MAX_HEALTH = Setting.of("flag-heal-max-health", new FlagDouble(0D), FlagDouble.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.heal-max-health");

  public static final Setting<FlagDouble> FLAG_HEAL_MAX_HUNGER = Setting.of("flag-heal-max-hunger", new FlagDouble(0D), FlagDouble.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.feed-max-hunger");

  public static final Setting<FlagDouble> FLAG_HEAL_MIN_HEALTH = Setting.of("flag-heal-min-health", new FlagDouble(0D), FlagDouble.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.heal-min-health");

  public static final Setting<FlagState> FLAG_ICE_FORM = Setting.of("flag-ice-form", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.ice-form");

  public static final Setting<FlagState> FLAG_INTERACT = Setting.of("flag-interact", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.interact");

  public static final Setting<FlagState> FLAG_INVINCIBLE = Setting.of("flag-invincible", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.invincible");

  public static final Setting<FlagState> FLAG_ITEM_DROP = Setting.of("flag-item-drop", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.item-drop");

  public static final Setting<FlagState> FLAG_ITEM_PICKUP = Setting.of("flag-item-pickup", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.item-pickup");

  public static final Setting<FlagState> FLAG_LAVA_FIRE = Setting.of("flag-lava-fire", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.lava-fire");

  public static final Setting<FlagState> FLAG_LAVA_FLOW = Setting.of("flag-lava-flow", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.lava-flow");

  public static final Setting<FlagState> FLAG_LEAF_DECAY = Setting.of("flag-leaf-decay", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.leaf-decay");

  public static final Setting<FlagState> FLAG_LIGHTER = Setting.of("flag-lighter", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.lighter");

  public static final Setting<FlagState> FLAG_LIGHTNING = Setting.of("flag-lightning", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.lightning");

  public static final Setting<FlagState> FLAG_MOB_DAMAGE = Setting.of("flag-mob-damage", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.mob-damage");

  public static final Setting<FlagState> FLAG_MOB_SPAWNING = Setting.of("flag-mob-spawning", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.mob-spawning");

  public static final Setting<FlagState> FLAG_MUSHROOM_GROWTH = Setting.of("flag-mushroom-growth", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.mushroom-growth");

  public static final Setting<FlagState> FLAG_MYCELIUM_SPREAD = Setting.of("flag-mycelium-spread", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.mycelium-spread");

  public static final Setting<FlagState> FLAG_NATURAL_HEALTH_REGEN = Setting.of("flag-natural-health-regen", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.natural-health-regen");

  public static final Setting<FlagState> FLAG_NATURAL_HUNGER_DRAIN = Setting.of("flag-natural-hunger-drain", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.natural-hunger-drain");

  public static final Setting<FlagBoolean> FLAG_NOTIFY_ENTER = Setting.of("flag-notify-enter", new FlagBoolean(true), FlagBoolean.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.notify-enter");

  public static final Setting<FlagBoolean> FLAG_NOTIFY_LEAVE = Setting.of("flag-notify-leave", new FlagBoolean(true), FlagBoolean.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.notify-leave");

  public static final Setting<FlagState> FLAG_OTHER_EXPLOSION = Setting.of("flag-other-explosion", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.other-explosion");

  public static final Setting<FlagState> FLAG_PASSTHROUGH = Setting.of("flag-passthrough", new FlagState(false), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.passthrough");

  public static final Setting<FlagState> FLAG_PVP = Setting.of("flag-pvp", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.pvp");

  public static final Setting<FlagState> FLAG_RIDE = Setting.of("flag-ride", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.ride");

  public static final Setting<FlagState> FLAG_SLEEP = Setting.of("flag-sleep", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.sleep");

  public static final Setting<FlagState> FLAG_SNOWMAN_TRAILS = Setting.of("flag-snowman-trails", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.snowman-trails");

  public static final Setting<FlagState> FLAG_SNOW_FALL = Setting.of("flag-snow-fall", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.snow-fall");

  public static final Setting<FlagState> FLAG_SNOW_MELT = Setting.of("flag-snow-melt", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.snow-melt");

  public static final Setting<FlagState> FLAG_SOIL_DRY = Setting.of("flag-soil-dry", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.soil-dry");

  public static final Setting<FlagVector3d> FLAG_SPAWN = Setting.of("flag-spawn", new FlagVector3d(Vector3d.ZERO), FlagVector3d.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.spawn");

  public static final Setting<FlagVector3d> FLAG_TELEPORT = Setting.of("flag-teleport", new FlagVector3d(Vector3d.ZERO), FlagVector3d.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.teleport");

  public static final Setting<FlagString> FLAG_TIME_LOCK = Setting.of("flag-time-lock", new FlagString(""), FlagString.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.time-lock");

  public static final Setting<FlagState> FLAG_TNT = Setting.of("flag-tnt", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.tnt");

  public static final Setting<FlagState> FLAG_USE = Setting.of("flag-use", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.use");

  public static final Setting<FlagState> FLAG_VEHICLE_DESTROY = Setting.of("flag-vehicle-destroy", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.vehicle-destroy");

  public static final Setting<FlagState> FLAG_VEHICLE_PLACE = Setting.of("flag-vehicle-place", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.vehicle-place");

  public static final Setting<FlagState> FLAG_VINE_GROWTH = Setting.of("flag-vine-growth", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.vine-growth");

  public static final Setting<FlagState> FLAG_WATER_FLOW = Setting.of("flag-water-flow", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.water-flow");

  public static final Setting<FlagState> FLAG_WEATHER_LOCK = Setting.of("flag-weather-lock", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.weather-lock");

  public static final Setting<FlagState> FLAG_WITHER_DAMAGE = Setting.of("flag-wither-damage", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.wither-damage");

  public static final Setting<FlagDouble> FLAG_feed_MIN_HUNGER = Setting.of("flag-feed-min-hunger", new FlagDouble(0D), FlagDouble.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.feed-min-hunger");

  public static final Setting<Boolean> LEAF_DECAY = Setting.of("leaf-decay", true, Boolean.class)
      .withDescription("Set to false will disable all natural leaf decay")
      .withApplicability(Setting.Applicability.GLOBAL,
          Setting.Applicability.WORLD)
      .withConfigurationPath("dynamics.leaf-decay");

  public static final Setting<Boolean> OP_PERMISSIONS = Setting.of("op-permissions", true, Boolean.class)
      .withApplicability(Setting.Applicability.WORLD)
      .withConfigurationPath("op-permissions");

  public static final Setting<Vector3d> REGION_MAX = Setting.of("region-max", Vector3d.ZERO, Vector3d.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("max");

  public static final Setting<TargetSet> REGION_MEMBERS = Setting.of("region-members", new TargetSet(), TargetSet.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("members");

  public static final Setting<Vector3d> REGION_MIN = Setting.of("region-min", Vector3d.ZERO, Vector3d.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("min");

  public static final Setting<TargetSet> REGION_OWNERS = Setting.of("region-owners", new TargetSet(), TargetSet.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("owners");

  public static final Setting<Integer> REGION_PRIORITY = Setting.of("region-priority", 0, Integer.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("priority");

  // SORTFIELDS:OFF
}
