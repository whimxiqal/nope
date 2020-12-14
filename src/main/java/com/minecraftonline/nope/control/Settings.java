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
 */

package com.minecraftonline.nope.control;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
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
import com.sk89q.worldedit.blocks.ItemType;
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
   * MUST be called if you want this class to function properly
   */
  public static void load() {
    setParents();
    Set<Setting<?>> mutableSettings = new HashSet<>();
    Multimap<Setting.Applicability, Setting<?>> mutableSettingApplicability = HashMultimap.create();
    for (Field field : Settings.class.getFields()) {
      // Check if its a parameter. It is already only public classes, but just in case
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
          if (field.getAnnotation(NotImplemented.class) != null) {
            setting.markNotImplemented();
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

  /**
   * Called when {@link #load()} is called,
   * and sets up the parents of all the settings,
   * since if they were inlined, they would be null
   */
  private static void setParents() {
    Settings.FLAG_DAMAGE_ANIMALS.setParent(Settings.FLAG_PVE);

    Settings.FLAG_BLOCK_BREAK.setParent(Settings.FLAG_BUILD);
    Settings.FLAG_BLOCK_PLACE.setParent(Settings.FLAG_BUILD);
    Settings.FLAG_INTERACT.setParent(Settings.FLAG_BUILD);
    Settings.FLAG_ENTITY_ARMOR_STAND_DESTROY.setParent(Settings.FLAG_BUILD);
    Settings.FLAG_ENTITY_ITEM_FRAME_DESTROY.setParent(Settings.FLAG_BUILD);
    Settings.FLAG_ENTITY_PAINTING_DESTROY.setParent(Settings.FLAG_BUILD);
    Settings.FLAG_VEHICLE_DESTROY.setParent(Settings.FLAG_BUILD);
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
    public SettingNotUniqueException(Setting<?> setting) {
      super("Multiple settings with id '" + setting.getId() + "'");
    }
  }

  // https://worldguard.enginehub.org/en/latest/config/

  // Sorts fields alphabetically
  // SORTFIELDS:ON

  /**
   * TODO: complete
   */
  @NotImplemented
  public static final Setting<Boolean> BUILD_PERMISSIONS = Setting.of("build-permission-nodes-enable", false, Boolean.class);

  @NotImplemented
  public static final Setting<Boolean> DEOP_ON_ENTER = Setting.of("deop-on-enter", false, Boolean.class)
      .withComment("Set to true will deop any player when they enter.")
      .withDescription("If this setting is applied globally, then anytime and op-ed player joins the server, their op status is removed. "
          + "If this setting is applied to just a world, then only when they join that specific world do they get de-opped.")
      .withApplicability(Setting.Applicability.GLOBAL, Setting.Applicability.WORLD)
      .withConfigurationPath("security.deop-on-enter");

  @NotImplemented
  public static final Setting<Boolean> ENABLE_PLUGIN = Setting.of("enable-plugin", true, Boolean.class)
      .withDescription("Set to false will disable all plugin functionality")
      .withConfigurationPath("enable-plugin");

  @NotImplemented
  public static final Setting<FlagStringSet> FLAG_ALLOWED_COMMANDS = Setting.of("allowed-cmds", new FlagStringSet(Sets.newHashSet()), FlagStringSet.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.allowed-cmds");

  public static final Setting<FlagStringSet> FLAG_BLOCKED_COMMANDS = Setting.of("blocked-cmds", new FlagStringSet(Sets.newHashSet()), FlagStringSet.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.blocked-cmds");

  public static final Setting<FlagState> FLAG_BLOCK_BREAK = Setting.of("block-break", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.block-break")
      .withCategory(Setting.Category.BLOCKS);

  public static final Setting<FlagState> FLAG_BLOCK_PLACE = Setting.of("block-place", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.block-place")
      .withCategory(Setting.Category.BLOCKS);

  @NotImplemented
  public static final Setting<FlagState> FLAG_BLOCK_TRAMPLING = Setting.of("block-trampling", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.block-trampling")
      .withCategory(Setting.Category.BLOCKS);

  public static final Setting<FlagState> FLAG_BUILD = Setting.of("build", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.build")
      .withCategory(Setting.Category.BLOCKS);

  @NotImplemented
  public static final Setting<FlagState> FLAG_CHEST_ACCESS = Setting.of("chest-access", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.chess-access")
      .withCategory(Setting.Category.BLOCKS);

  @NotImplemented
  public static final Setting<FlagState> FLAG_CHORUS_FRUIT_TELEPORT = Setting.of("chorus-fruit-teleport", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.chorus-fruit-teleport");

  public static final Setting<FlagString> FLAG_COMMAND_DENY_MESSAGE = Setting.of("command-deny-message", new FlagString(""), FlagString.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.command-deny-message");

  @NotImplemented
  public static final Setting<FlagState> FLAG_CORAL_FADE = Setting.of("coral-fade", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.coral-fade")
      .withCategory(Setting.Category.BLOCKS);

  @NotImplemented
  public static final Setting<FlagState> FLAG_CREEPER_EXPLOSION = Setting.of("creeper-explosion", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.creeper-explosion");

  @NotImplemented
  public static final Setting<FlagState> FLAG_CROP_GROWTH = Setting.of("crop-growth", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.crop-growth")
      .withCategory(Setting.Category.BLOCKS);

  public static final Setting<FlagState> FLAG_DAMAGE_ANIMALS = Setting.of("damage-animals", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.damage-animals")
      .withCategory(Setting.Category.DAMAGE);

  public static final Setting<FlagString> FLAG_DENY_MESSAGE = Setting.of("deny-message", new FlagString(""), FlagString.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.deny-message");

  @NotImplemented
  public static final Setting<FlagEntitySet> FLAG_DENY_SPAWN = Setting.of("deny-spawn", new FlagEntitySet(Sets.newHashSet()), FlagEntitySet.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.deny-spawn");

  @NotImplemented
  public static final Setting<FlagState> FLAG_ENDERDRAGON_BLOCK_DAMAGE = Setting.of("enderdragon-block-damage", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.enderdragon-block-damage")
      .withCategory(Setting.Category.BLOCKS);

  @NotImplemented
  public static final Setting<FlagState> FLAG_ENDERMAN_GRIEF = Setting.of("enderman-grief", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.enderman-grief")
      .withCategory(Setting.Category.BLOCKS);

  @NotImplemented
  public static final Setting<FlagState> FLAG_ENDERPEARL = Setting.of("enderpearl", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.enderpearl");

  public static final Setting<FlagState> FLAG_ENTITY_ARMOR_STAND_DESTROY = Setting.of("entity-armor-stand-destroy", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.entity-armor-stand-destroy")
      .withDescription("A flag for whether a player can break armor stands in the region")
      .withCategory(Setting.Category.DAMAGE);

  public static final Setting<FlagState> FLAG_ENTITY_ITEM_FRAME_DESTROY = Setting.of("entity-item-frame-destroy", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.entity-item-frame-destroy")
      .withCategory(Setting.Category.DAMAGE);

  public static final Setting<FlagState> FLAG_ENTITY_PAINTING_DESTROY = Setting.of("entity-painting-destroy", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.entity-painting-destroy")
      .withCategory(Setting.Category.DAMAGE);

  @NotImplemented
  public static final Setting<FlagState> FLAG_ENTITY_VEHICLE_DESTROY = Setting.of("entity-vehicle-destroy", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.entity-vehicle-destroy")
      .withDescription("A flag for whether a player can break vehicles in the region")
      .withCategory(Setting.Category.DAMAGE);

  public static final Setting<FlagState> FLAG_ENTRY = Setting.of("entry", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.entry");

  public static final Setting<FlagString> FLAG_ENTRY_DENY_MESSAGE = Setting.of("entry-deny-message", new FlagString(""), FlagString.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.entry-deny-message");

  public static final Setting<FlagState> FLAG_EVP = Setting.of("evp", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.evp")
      .withDescription("Whether players can receive damage from the environment")
      .withCategory(Setting.Category.DAMAGE);

  public static final Setting<FlagState> FLAG_EXIT = Setting.of("exit", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.exit");

  public static final Setting<FlagString> FLAG_EXIT_DENY_MESSAGE = Setting.of("exit-deny-message", new FlagString(""), FlagString.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.exit-deny-message");

  @NotImplemented
  public static final Setting<FlagBoolean> FLAG_EXIT_OVERRIDE = Setting.of("exit-override", new FlagBoolean(true), FlagBoolean.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.exit-override");

  public static final Setting<FlagState> FLAG_EXIT_VIA_TELEPORT = Setting.of("exit-via-teleport", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.exit-via-teleport");

  @NotImplemented
  public static final Setting<FlagState> FLAG_EXP_DROPS = Setting.of("exp-drops", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.exp-drops");

  @NotImplemented
  public static final Setting<FlagState> FLAG_FALL_DAMAGE = Setting.of("fall-damage", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.fall-damage")
      .withCategory(Setting.Category.DAMAGE);

  public static final Setting<FlagString> FLAG_FAREWELL = Setting.of("farewell", new FlagString(""), FlagString.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.farewell");

  @NotImplemented
  public static final Setting<FlagString> FLAG_FAREWELL_TITLE = Setting.of("farewell-title", new FlagString(""), FlagString.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.farewell-title");

  @NotImplemented
  public static final Setting<FlagInteger> FLAG_FEED_AMOUNT = Setting.of("feed-amount", new FlagInteger(0), FlagInteger.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.feed-amount");

  @NotImplemented
  public static final Setting<FlagInteger> FLAG_FEED_DELAY = Setting.of("feed-delay", new FlagInteger(0), FlagInteger.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.feed-delay");

  @NotImplemented
  public static final Setting<FlagDouble> FLAG_FEED_MIN_HUNGER = Setting.of("feed-min-hunger", new FlagDouble(0D), FlagDouble.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.feed-min-hunger");

  @NotImplemented
  public static final Setting<FlagState> FLAG_FIREWORK_DAMAGE = Setting.of("firework-damage", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.firework-damage")
      .withCategory(Setting.Category.DAMAGE);

  @NotImplemented
  public static final Setting<FlagState> FLAG_FIRE_SPREAD = Setting.of("fire-spread", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.fire-spread");

  @NotImplemented
  public static final Setting<FlagState> FLAG_FROSTED_ICE_FORM = Setting.of("frosted-ice-form", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.frosted-ice-form")
      .withCategory(Setting.Category.BLOCKS);

  @NotImplemented
  public static final Setting<FlagState> FLAG_FROSTED_ICE_MELT = Setting.of("frosted-ice-melt", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.frosted-ice-melt")
      .withCategory(Setting.Category.BLOCKS);

  @NotImplemented
  public static final Setting<FlagGameMode> FLAG_GAME_MODE = Setting.of("game-mode", new FlagGameMode(GameModes.NOT_SET), FlagGameMode.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.game-mode");

  @NotImplemented
  public static final Setting<FlagState> FLAG_GHAST_FIREBALL = Setting.of("ghast-fireball", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.ghast-fireball");

  @NotImplemented
  public static final Setting<FlagState> FLAG_GRASS_GROWTH = Setting.of("grass-growth", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.grass-growth")
      .withCategory(Setting.Category.BLOCKS);

  public static final Setting<FlagString> FLAG_GREETING = Setting.of("greeting", new FlagString(""), FlagString.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.greeting");

  @NotImplemented
  public static final Setting<FlagString> FLAG_GREETING_TITLE = Setting.of("greeting-title", new FlagString(""), FlagString.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.greeting-title");

  @NotImplemented
  public static final Setting<FlagInteger> FLAG_HEAL_AMOUNT = Setting.of("heal-amount", new FlagInteger(0), FlagInteger.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.heal-amount");

  @NotImplemented
  public static final Setting<FlagInteger> FLAG_HEAL_DELAY = Setting.of("heal-delay", new FlagInteger(0), FlagInteger.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.heal-delay");

  @NotImplemented
  public static final Setting<FlagDouble> FLAG_HEAL_MAX_HEALTH = Setting.of("heal-max-health", new FlagDouble(0D), FlagDouble.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.heal-max-health");

  @NotImplemented
  public static final Setting<FlagDouble> FLAG_HEAL_MAX_HUNGER = Setting.of("heal-max-hunger", new FlagDouble(0D), FlagDouble.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.feed-max-hunger");

  @NotImplemented
  public static final Setting<FlagDouble> FLAG_HEAL_MIN_HEALTH = Setting.of("heal-min-health", new FlagDouble(0D), FlagDouble.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.heal-min-health");

  @NotImplemented
  public static final Setting<FlagState> FLAG_ICE_FORM = Setting.of("ice-form", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.ice-form")
      .withCategory(Setting.Category.BLOCKS);

  public static final Setting<FlagState> FLAG_INTERACT = Setting.of("interact", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.interact")
      .withCategory(Setting.Category.BLOCKS);

  @NotImplemented
  public static final Setting<FlagState> FLAG_INVINCIBLE = Setting.of("invincible", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.invincible");

  @NotImplemented
  public static final Setting<FlagState> FLAG_ITEM_DROP = Setting.of("item-drop", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.item-drop");

  @NotImplemented
  public static final Setting<FlagState> FLAG_ITEM_PICKUP = Setting.of("item-pickup", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.item-pickup");

  @NotImplemented
  public static final Setting<FlagState> FLAG_LAVA_FIRE = Setting.of("lava-fire", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.lava-fire");

  @NotImplemented
  public static final Setting<FlagState> FLAG_LAVA_FLOW = Setting.of("lava-flow", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.lava-flow")
      .withCategory(Setting.Category.BLOCKS);

  @NotImplemented
  public static final Setting<FlagState> FLAG_LEAF_DECAY = Setting.of("leaf-decay", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.leaf-decay")
      .withCategory(Setting.Category.BLOCKS);

  @NotImplemented
  public static final Setting<FlagState> FLAG_LIGHTER = Setting.of("lighter", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.lighter")
      .withCategory(Setting.Category.BLOCKS);

  @NotImplemented
  public static final Setting<FlagState> FLAG_LIGHTNING = Setting.of("lightning", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.lightning")
      .withCategory(Setting.Category.BLOCKS);

  public static final Setting<FlagState> FLAG_MOB_DAMAGE = Setting.of("mob-damage", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.mob-damage")
      .withCategory(Setting.Category.DAMAGE);

  @NotImplemented
  public static final Setting<FlagState> FLAG_MOB_SPAWNING = Setting.of("mob-spawning", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.mob-spawning");

  @NotImplemented
  public static final Setting<FlagState> FLAG_MUSHROOM_GROWTH = Setting.of("mushroom-growth", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.mushroom-growth")
      .withCategory(Setting.Category.BLOCKS);

  @NotImplemented
  public static final Setting<FlagState> FLAG_MYCELIUM_SPREAD = Setting.of("mycelium-spread", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.mycelium-spread")
      .withCategory(Setting.Category.BLOCKS);

  @NotImplemented
  public static final Setting<FlagState> FLAG_NATURAL_HEALTH_REGEN = Setting.of("natural-health-regen", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.natural-health-regen");

  @NotImplemented
  public static final Setting<FlagState> FLAG_NATURAL_HUNGER_DRAIN = Setting.of("natural-hunger-drain", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.natural-hunger-drain");

  @NotImplemented
  public static final Setting<FlagBoolean> FLAG_NOTIFY_ENTER = Setting.of("notify-enter", new FlagBoolean(true), FlagBoolean.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.notify-enter");

  @NotImplemented
  public static final Setting<FlagBoolean> FLAG_NOTIFY_LEAVE = Setting.of("notify-leave", new FlagBoolean(true), FlagBoolean.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.notify-leave");

  @NotImplemented
  public static final Setting<FlagState> FLAG_OTHER_EXPLOSION = Setting.of("other-explosion", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.other-explosion");

  public static final Setting<FlagState> FLAG_PASSTHROUGH = Setting.of("passthrough", new FlagState(true), FlagState.class) // By default, unlike WG, allow by default
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.passthrough");

  public static final Setting<FlagState> FLAG_PLAYER_COLLISION = Setting.of("player-collision", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.player-collision")
      .withDescription("A flag for whether players should collide in a region");

  public static final Setting<FlagState> FLAG_PVE = Setting.of("pve", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.pve")
      .withDescription("Whether players can hurt entities")
      .withCategory(Setting.Category.DAMAGE);

  public static final Setting<FlagState> FLAG_PVP = Setting.of("pvp", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.pvp")
      .withCategory(Setting.Category.DAMAGE);

  @NotImplemented
  public static final Setting<FlagState> FLAG_RIDE = Setting.of("ride", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.ride");

  @NotImplemented
  public static final Setting<FlagState> FLAG_SLEEP = Setting.of("sleep", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.sleep");

  @NotImplemented
  public static final Setting<FlagState> FLAG_SNOWMAN_TRAILS = Setting.of("snowman-trails", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.snowman-trails")
      .withCategory(Setting.Category.BLOCKS);

  @NotImplemented
  public static final Setting<FlagState> FLAG_SNOW_FALL = Setting.of("snow-fall", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.snow-fall")
      .withCategory(Setting.Category.BLOCKS);

  @NotImplemented
  public static final Setting<FlagState> FLAG_SNOW_MELT = Setting.of("snow-melt", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.snow-melt")
      .withCategory(Setting.Category.BLOCKS);

  @NotImplemented
  public static final Setting<FlagState> FLAG_SOIL_DRY = Setting.of("soil-dry", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.soil-dry")
      .withCategory(Setting.Category.BLOCKS);

  @NotImplemented
  public static final Setting<FlagVector3d> FLAG_SPAWN = Setting.of("spawn", new FlagVector3d(Vector3d.ZERO), FlagVector3d.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.spawn");

  @NotImplemented
  public static final Setting<FlagVector3d> FLAG_TELEPORT = Setting.of("teleport", new FlagVector3d(Vector3d.ZERO), FlagVector3d.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.teleport");

  @NotImplemented
  public static final Setting<FlagString> FLAG_TIME_LOCK = Setting.of("time-lock", new FlagString(""), FlagString.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.time-lock");

  @NotImplemented
  public static final Setting<FlagState> FLAG_TNT = Setting.of("tnt", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.tnt");

  @NotImplemented
  public static final Setting<FlagState> FLAG_USE = Setting.of("use", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.use");

  public static final Setting<FlagState> FLAG_VEHICLE_DESTROY = Setting.of("vehicle-destroy", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.vehicle-destroy")
      .withCategory(Setting.Category.DAMAGE);

  @NotImplemented
  public static final Setting<FlagState> FLAG_VEHICLE_PLACE = Setting.of("vehicle-place", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.vehicle-place")
      .withCategory(Setting.Category.DAMAGE);

  @NotImplemented
  public static final Setting<FlagState> FLAG_VINE_GROWTH = Setting.of("vine-growth", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.vine-growth")
      .withCategory(Setting.Category.BLOCKS);

  @NotImplemented
  public static final Setting<FlagState> FLAG_WATER_FLOW = Setting.of("water-flow", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.water-flow")
      .withCategory(Setting.Category.BLOCKS);

  @NotImplemented
  public static final Setting<FlagState> FLAG_WEATHER_LOCK = Setting.of("weather-lock", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.weather-lock");

  @NotImplemented
  public static final Setting<FlagState> FLAG_WITHER_DAMAGE = Setting.of("wither-damage", new FlagState(true), FlagState.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("flags.wither-damage")
      .withCategory(Setting.Category.DAMAGE);

  // Should this be removed entirely - theres a flag for it, can be put in global region.
  @NotImplemented
  public static final Setting<Boolean> GLOBAL_LEAF_DECAY = Setting.of("global-leaf-decay", true, Boolean.class)
      .withDescription("Set to false will disable all natural leaf decay")
      .withApplicability(Setting.Applicability.GLOBAL,
          Setting.Applicability.WORLD)
      .withConfigurationPath("dynamics.leaf-decay")
      .withCategory(Setting.Category.BLOCKS);

  @NotImplemented
  public static final Setting<Boolean> OP_PERMISSIONS = Setting.of("op-permissions", true, Boolean.class)
      .withApplicability(Setting.Applicability.WORLD)
      .withConfigurationPath("op-permissions");

  public static final Setting<Vector3i> REGION_MAX = Setting.of("region-max", Vector3i.ZERO, Vector3i.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("max");

  public static final Setting<TargetSet> REGION_MEMBERS = Setting.of("region-members", new TargetSet(), TargetSet.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("members");

  public static final Setting<Vector3i> REGION_MIN = Setting.of("region-min", Vector3i.ZERO, Vector3i.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("min");

  public static final Setting<TargetSet> REGION_OWNERS = Setting.of("region-owners", new TargetSet(), TargetSet.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("owners");

  public static final Setting<Integer> REGION_PRIORITY = Setting.of("region-priority", 0, Integer.class)
      .withApplicability(Setting.Applicability.REGION)
      .withConfigurationPath("priority");

  @NotImplemented
  public static final Setting<String> SQL_DSN = Setting.of("sql-dsn", "jdbc:mysql://localhost/nope", String.class)
      .withApplicability(Setting.Applicability.GLOBAL)
      .withConfigurationPath("regions.sql.sql-dsn");

  @NotImplemented
  public static final Setting<Boolean> SQL_ENABLE = Setting.of("sql-enable", false, Boolean.class)
      .withApplicability(Setting.Applicability.GLOBAL)
      .withConfigurationPath("regions.sql.sql-enable");

  @NotImplemented
  public static final Setting<String> SQL_PASSWORD = Setting.of("sql-password", "nope", String.class)
      .withApplicability(Setting.Applicability.GLOBAL)
      .withConfigurationPath("regions.sql.sql-password");

  @NotImplemented
  public static final Setting<String> SQL_TABLE_PREFIX = Setting.of("sql-table-prefix", "", String.class)
      .withApplicability(Setting.Applicability.GLOBAL)
      .withConfigurationPath("regions.sql.table-prefix");

  @NotImplemented
  public static final Setting<String> SQL_USERNAME = Setting.of("sql-username", "nope", String.class)
      .withApplicability(Setting.Applicability.GLOBAL)
      .withConfigurationPath("regions.sql.sql-username");

  /**
   * TODO: add more config options, add ability for damage values, etc.
   */
  public static final Setting<ItemType> WAND_ITEM = Setting.of("wand-item", ItemType.STICK, ItemType.class)
      .withComment("Item given when /nope region wand is used")
      .withApplicability(Setting.Applicability.GLOBAL)
      .withConfigurationPath("wand-item");

  // SORTFIELDS:OFF
}
