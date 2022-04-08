/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
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

package me.pietelite.nope.common.setting;

import java.lang.reflect.Modifier;
import java.util.Arrays;

import me.pietelite.nope.common.api.setting.SettingCategory;
import me.pietelite.nope.common.api.setting.data.BlockChange;
import me.pietelite.nope.common.api.setting.data.DamageCause;
import me.pietelite.nope.common.api.setting.data.Explosive;
import me.pietelite.nope.common.api.setting.data.Movement;
import me.pietelite.nope.common.setting.sets.BlockChangeSet;
import me.pietelite.nope.common.setting.sets.DamageCauseSet;
import me.pietelite.nope.common.setting.sets.ExplosiveSet;
import me.pietelite.nope.common.setting.sets.MovementSet;
import me.pietelite.nope.common.setting.sets.StringSet;

/**
 * A utility class to store all constant default Nope {@link SettingKey}s.
 */
public final class SettingKeys {

  public static final SettingKey.Poly<BlockChange, BlockChangeSet> BLOCK_CHANGE =
      SettingKeyManagers.POLY_BLOCK_CHANGE_KEY_MANAGER.keyBuilder("block-change")
          .fillDefaultData()
          .blurb("How blocks may be changed")
          .description("A list of ways that blocks may be changed.")
          .category(SettingCategory.BLOCKS)
          .playerRestrictive()
          .build();
  public static final SettingKey.Poly<String, StringSet> BLOCK_CHANGING_MOBS =
      SettingKeyManagers.POLY_ENTITY_KEY_MANAGER.keyBuilder("mob-grief")
          .fillDefaultData()
          .blurb("Mobs that can change blocks")
          .description("A list of all mobs that can change blocks")
          .category(SettingCategory.ENTITIES)
          .build();
  public static final SettingKey.Unary<Boolean> BLOCK_PROPAGATE =
      SettingKeyManagers.TOGGLE_KEY_MANAGER.keyBuilder("block-propagate")
          .defaultValue(true)
          .blurb("Block updates to neighbors")
          .description("When disabled, blocks will not update each other.")
          .category(SettingCategory.BLOCKS)
          .build();
  public static final SettingKey.Unary<Integer> CACHE_SIZE =
      SettingKeyManagers.INTEGER_KEY_MANAGER.keyBuilder("cache-size")
          .defaultValue(75000)
          .blurb("Size of world block caches")
          .description("This is the quantity of block locations to cache for each world. "
              + "Total memory is roughly this multiplied by 56 bytes, "
              + "multiplied by the number of worlds. Set 0 to disable caching.")
          .playerRestrictive()
          .global()
          .functional()
          .build();
  public static final SettingKey.Unary<Boolean> CONCRETE_SOLIDIFICATION =
      SettingKeyManagers.TOGGLE_KEY_MANAGER.keyBuilder("concrete-solidification")
          .defaultValue(true)
          .blurb("Concrete powder solidification")
          .description("When disabled, concrete powder does not solidify into concrete.")
          .category(SettingCategory.BLOCKS)
          .playerRestrictive()
          .build();
  public static final SettingKey.Poly<Explosive, ExplosiveSet> DESTRUCTIVE_EXPLOSIVES =
      SettingKeyManagers.POLY_EXPLOSIVE_KEY_MANAGER.keyBuilder("destructive-explosives")
          .fillDefaultData()
          .blurb("Explosives causing no world damage")
          .description("A list of explosives whose explosions to not cause damage to the world.")
          .category(SettingCategory.BLOCKS)
          .build();
  public static final SettingKey.Unary<Boolean> DROP_EXP =
      SettingKeyManagers.TOGGLE_KEY_MANAGER.keyBuilder("drop-exp")
          .defaultValue(true)
          .blurb("Experience drop")
          .description("When disabled, experience points are never dropped.")
          .category(SettingCategory.MISC)
          .build();
  public static final SettingKey.Poly<Movement, MovementSet> ENTRY =
      SettingKeyManagers.POLY_MOVEMENT_KEY_MANAGER.keyBuilder("entry")
          .fillDefaultData()
          .blurb("Host entrance restriction")
          .description("Specify which type of movement is allowed by players to enter.")
          .category(SettingCategory.MOVEMENT)
          .functional()
          .playerRestrictive()
          .build();
  public static final SettingKey.Unary<String> ENTRY_DENY_MESSAGE =
      SettingKeyManagers.STRING_KEY_MANAGER.keyBuilder("entry-deny-message")
          .defaultValue("You are not allowed to go there")
          .naturalValue("")
          .blurb("Message when entry is denied")
          .description("The message that is sent to a player if they are barred from entry.")
          .category(SettingCategory.MOVEMENT)
          .functional()
          .build();
  public static final SettingKey.Unary<String> ENTRY_DENY_SUBTITLE =
      SettingKeyManagers.STRING_KEY_MANAGER.keyBuilder("entry-deny-subtitle")
          .defaultValue("")
          .blurb("Subtitle when entry is denied")
          .description("The subtitle that is sent to a player if they are barred from entry.")
          .category(SettingCategory.MOVEMENT)
          .functional()
          .build();
  public static final SettingKey.Unary<String> ENTRY_DENY_TITLE =
      SettingKeyManagers.STRING_KEY_MANAGER.keyBuilder("entry-deny-title")
          .defaultValue("")
          .blurb("Title when entry is denied")
          .description("The title that is sent to a player if they are barred from entry.")
          .category(SettingCategory.MOVEMENT)
          .functional()
          .build();
  public static final SettingKey.Poly<Movement, MovementSet> EXIT =
      SettingKeyManagers.POLY_MOVEMENT_KEY_MANAGER.keyBuilder("exit")
          .fillDefaultData()
          .blurb("Host exit restriction")
          .description("Specify which type of movement is allowed by players to exit.")
          .category(SettingCategory.MOVEMENT)
          .playerRestrictive()
          .functional()
          .build();
  public static final SettingKey.Unary<String> EXIT_DENY_MESSAGE =
      SettingKeyManagers.STRING_KEY_MANAGER.keyBuilder("exit-deny-message")
          .defaultValue("You are not allowed to leave here")
          .naturalValue("")
          .blurb("Message when exit is denied")
          .description("The message that is sent to the player if they are barred from exiting.")
          .category(SettingCategory.MOVEMENT)
          .functional()
          .build();
  public static final SettingKey.Unary<String> EXIT_DENY_SUBTITLE =
      SettingKeyManagers.STRING_KEY_MANAGER.keyBuilder("exit-deny-subtitle")
          .defaultValue("")
          .blurb("Subtitle when exit is denied")
          .description("The subtitle that is sent to a player if they are barred from exiting")
          .category(SettingCategory.MOVEMENT)
          .functional()
          .build();
  public static final SettingKey.Unary<String> EXIT_DENY_TITLE =
      SettingKeyManagers.STRING_KEY_MANAGER.keyBuilder("exit-deny-title")
          .defaultValue("")
          .blurb("Title when exit is denied")
          .description("The title that is sent to a player if they are barred from exiting")
          .category(SettingCategory.MOVEMENT)
          .functional()
          .build();
  public static final SettingKey.Unary<String> FAREWELL =
      SettingKeyManagers.STRING_KEY_MANAGER.keyBuilder("farewell")
          .defaultValue("")
          .blurb("Message upon exit")
          .description("The message to a player when they leave the host.")
          .category(SettingCategory.MOVEMENT)
          .functional()
          .build();
  public static final SettingKey.Unary<String> FAREWELL_SUBTITLE =
      SettingKeyManagers.STRING_KEY_MANAGER.keyBuilder("farwell-subtitle")
          .defaultValue("")
          .blurb("Subtitle upon exit")
          .description("The subtitle that appears to a player when they leave the host.")
          .category(SettingCategory.MOVEMENT)
          .functional()
          .build();
  public static final SettingKey.Unary<String> FAREWELL_TITLE =
      SettingKeyManagers.STRING_KEY_MANAGER.keyBuilder("farewell-title")
          .defaultValue("")
          .blurb("Title upon exit")
          .description("The title that appears to a player when they leave the host.")
          .category(SettingCategory.MOVEMENT)
          .functional()
          .build();
  public static final SettingKey.Unary<Boolean> FIRE_EFFECT =
      SettingKeyManagers.TOGGLE_KEY_MANAGER.keyBuilder("fire-effect")
          .defaultValue(true)
          .blurb("Fire spread/damage")
          .description("When disabled, fire does not spread or cause block damage")
          .category(SettingCategory.MISC)
          .build();
  public static final SettingKey.Unary<Boolean> FIRE_IGNITION =
      SettingKeyManagers.STATE_KEY_MANAGER.keyBuilder("fire-ignition")
          .defaultValue(true)
          .blurb("Fire ignition restriction")
          .description("When disabled, fire may not be lit")
          .category(SettingCategory.MISC)
          .playerRestrictive()
          .build();
  public static final SettingKey.Unary<Boolean> FROSTED_ICE_FORM =
      SettingKeyManagers.TOGGLE_KEY_MANAGER.keyBuilder("frosted-ice-form")
          .defaultValue(true)
          .blurb("Frosted ice formation")
          .description("When disabled, frost ice does not form.")
          .category(SettingCategory.BLOCKS)
          .build();
  public static final SettingKey.Unary<Boolean> FROSTED_ICE_MELT =
      SettingKeyManagers.TOGGLE_KEY_MANAGER.keyBuilder("frosted-ice-melt")
          .defaultValue(true)
          .blurb("Frosted ice melt")
          .description("When disabled, frosted ice does not melt")
          .category(SettingCategory.BLOCKS)
          .build();
  public static final SettingKey.Unary<Boolean> GRASS_GROWTH =
      SettingKeyManagers.TOGGLE_KEY_MANAGER.keyBuilder("grass-growth")
          .defaultValue(true)
          .blurb("Grass growth")
          .description("When disabled, grass cannot grow naturally")
          .category(SettingCategory.BLOCKS)
          .build();
  public static final SettingKey.Unary<String> GREETING =
      SettingKeyManagers.STRING_KEY_MANAGER.keyBuilder("greeting")
          .defaultValue("")
          .blurb("Message upon entry")
          .description("The message to a player when they enter")
          .category(SettingCategory.MOVEMENT)
          .functional()
          .build();
  public static final SettingKey.Unary<String> GREETING_SUBTITLE =
      SettingKeyManagers.STRING_KEY_MANAGER.keyBuilder("greeting-subtitle")
          .defaultValue("")
          .blurb("Subtitle upon entry")
          .description("The subtitle that appears to a player when they enter")
          .category(SettingCategory.MOVEMENT)
          .functional()
          .build();
  public static final SettingKey.Unary<String> GREETING_TITLE =
      SettingKeyManagers.STRING_KEY_MANAGER.keyBuilder("greeting-title")
          .defaultValue("")
          .blurb("Title upon entry")
          .description("The title that appears to a player when they enter")
          .category(SettingCategory.MOVEMENT)
          .functional()
          .build();
  public static final SettingKey.Poly<String, StringSet> GROWABLES =
      SettingKeyManagers.POLY_GROWABLE_KEY_MANAGER.keyBuilder("growables")
          .fillDefaultData()
          .blurb("Growable blocks")
          .description("A list of blocks that can grow")
          .category(SettingCategory.BLOCKS)
          .playerRestrictive()
          .build();
  public static final SettingKey.Poly<Explosive, ExplosiveSet> HARMFUL_EXPLOSIVES =
      SettingKeyManagers.POLY_EXPLOSIVE_KEY_MANAGER.keyBuilder("harmful-explosives")
          .fillDefaultData()
          .blurb("Explosives causing no entity damage")
          .description("A list of explosives whose explosions do not cause damage to entities.")
          .category(SettingCategory.DAMAGE)
          .build();
  public static final SettingKey.Unary<Boolean> HEALTH_REGEN =
      SettingKeyManagers.TOGGLE_KEY_MANAGER.keyBuilder("health-regen")
          .defaultValue(true)
          .blurb("Health regen")
          .description("When disabled, players do not regenerate health")
          .category(SettingCategory.DAMAGE)
          .playerRestrictive()
          .build();

  public static final SettingKey.Poly<String, StringSet> HOOKABLE_ENTITIES =
      SettingKeyManagers.POLY_ENTITY_KEY_MANAGER.keyBuilder("hookable-entities")
          .fillDefaultData()
          .blurb("Entities that can be hooked")
          .description("A list of entities that can be hooked with a fishing rod")
          .category(SettingCategory.ENTITIES)
          .playerRestrictive()
          .build();
  public static final SettingKey.Unary<Boolean> HUNGER_DRAIN =
      SettingKeyManagers.TOGGLE_KEY_MANAGER.keyBuilder("hunger-drain")
          .defaultValue(true)
          .blurb("Player hunger drain")
          .description("When disabled, player hunger does not drain naturally.")
          .playerRestrictive()
          .build();
  public static final SettingKey.Unary<Boolean> ICE_FORM =
      SettingKeyManagers.TOGGLE_KEY_MANAGER.keyBuilder("ice-form")
          .defaultValue(true)
          .blurb("Ice formation")
          .description("When disabled, ice does not form.")
          .category(SettingCategory.BLOCKS)
          .build();
  public static final SettingKey.Unary<Boolean> ICE_MELT =
      SettingKeyManagers.TOGGLE_KEY_MANAGER.keyBuilder("ice-melt")
          .defaultValue(true)
          .blurb("Ice melt")
          .description("When disabled, ice does not melt.")
          .category(SettingCategory.BLOCKS)
          .build();
  public static final SettingKey.Poly<String, StringSet> IGNORED_PLUGINS =
      SettingKeyManagers.POLY_PLUGIN_MANAGER.keyBuilder("ignored-plugins")
          .emptyDefaultData()
          .blurb("Plugins unaffected by Nope")
          .description("A list of all plugins that Nope does not affect")
          .build();
  public static final SettingKey.Poly<String, StringSet> INTERACTIVE_BLOCKS =
      SettingKeyManagers.POLY_BLOCK_KEY_MANAGER.keyBuilder("interactive-blocks")
          .fillDefaultData()
          .blurb("Interactive blocks")
          .description("A list of blocks with which that can be interacted.")
          .category(SettingCategory.BLOCKS)
          .playerRestrictive()
          .build();
  public static final SettingKey.Poly<String, StringSet> INTERACTIVE_ENTITIES =
      SettingKeyManagers.POLY_ENTITY_KEY_MANAGER.keyBuilder("interactive-entities")
          .fillDefaultData()
          .blurb("Entity interactivity")
          .description("List of entities that can be interacted with.")
          .category(SettingCategory.DAMAGE)
          .build();
  public static final SettingKey.Poly<String, StringSet> INVINCIBLE_ENTITIES =
      SettingKeyManagers.POLY_ENTITY_KEY_MANAGER.keyBuilder("invincible-entities")
          .emptyDefaultData()
          .blurb("Entity invincibility")
          .description("List of entities which cannot be damaged or destroyed.")
          .category(SettingCategory.DAMAGE)
          .build();
  public static final SettingKey.Unary<Boolean> ITEM_DROP =
      SettingKeyManagers.TOGGLE_KEY_MANAGER.keyBuilder("item-drop")
          .defaultValue(true)
          .blurb("Item drop restriction")
          .description("When disabled, items cannot drop.")
          .playerRestrictive()
          .build();
  public static final SettingKey.Unary<Boolean> ITEM_PICKUP =
      SettingKeyManagers.TOGGLE_KEY_MANAGER.keyBuilder("item-pickup")
          .defaultValue(true)
          .blurb("Item pickup restriction")
          .description("When disabled, items may not be picked up.")
          .playerRestrictive()
          .build();
  public static final SettingKey.Unary<Boolean> LAVA_FLOW =
      SettingKeyManagers.TOGGLE_KEY_MANAGER.keyBuilder("lava-flow")
          .defaultValue(true)
          .blurb("Lava flow")
          .description("When disabled, lava does not spread")
          .category(SettingCategory.BLOCKS)
          .build();
  public static final SettingKey.Unary<Boolean> LEAF_DECAY =
      SettingKeyManagers.TOGGLE_KEY_MANAGER.keyBuilder("leaf-decay")
          .defaultValue(true)
          .blurb("Leaf decay")
          .description("When disabled, leaves will not decay naturally.")
          .category(SettingCategory.BLOCKS)
          .build();
  public static final SettingKey.Poly<String, StringSet> LEASHABLE_ENTITIES =
      SettingKeyManagers.POLY_ENTITY_KEY_MANAGER.keyBuilder("leashable-entities")
          .fillDefaultData()
          .blurb("Entities that can have leads")
          .description("A list of entities which can have leads attached to them")
          .category(SettingCategory.BLOCKS)
          .playerRestrictive()
          .build();
  public static final SettingKey.Unary<Boolean> LIGHTNING =
      SettingKeyManagers.TOGGLE_KEY_MANAGER.keyBuilder("lightning")
          .defaultValue(true)
          .blurb("Lightning strikes")
          .description("When disabled, lightning cannot strike.")
          .build();
  public static final SettingKey.Unary<Boolean> LIGHT_NETHER_PORTAL =
      SettingKeyManagers.STATE_KEY_MANAGER.keyBuilder("light-nether-portal")
          .defaultValue(true)
          .blurb("Lighting nether portals")
          .description("When disabled, players cannot light nether portals")
          .category(SettingCategory.MISC)
          .playerRestrictive()
          .build();
  public static final SettingKey.Poly<Movement, MovementSet> MOVE =
      SettingKeyManagers.POLY_MOVEMENT_KEY_MANAGER.keyBuilder("move")
          .fillDefaultData()
          .blurb("Movement within a host")
          .description("Specify which type of movement is allowed.")
          .category(SettingCategory.MOVEMENT)
          .playerRestrictive()
          .build();
  public static final SettingKey.Unary<Boolean> MYCELIUM_SPREAD =
      SettingKeyManagers.TOGGLE_KEY_MANAGER.keyBuilder("mycelium-spread")
          .defaultValue(true)
          .blurb("Mycelium spread")
          .description("When disabled, mycelium does not spread")
          .category(SettingCategory.BLOCKS)
          .build();
  public static final SettingKey.Unary<Boolean> PLAYER_COLLISION =
      SettingKeyManagers.TOGGLE_KEY_MANAGER.keyBuilder("player-collision")
          .defaultValue(true)
          .blurb("Collision between players")
          .description("When disabled, players do not collide")
          .category(SettingCategory.MOVEMENT)
          .build();
  public static final SettingKey.Poly<DamageCause, DamageCauseSet> PLAYER_DAMAGE_SOURCE =
      SettingKeyManagers.POLY_DAMAGE_SOURCE_KEY_MANAGER.keyBuilder("player-damage-source")
          .fillDefaultData()
          .blurb("Damage sources to players")
          .description("A list of damage sources that may inflict damage to players")
          .category(SettingCategory.DAMAGE)
          .build();
  public static final SettingKey.Unary<Boolean> RIDE =
      SettingKeyManagers.STATE_KEY_MANAGER.keyBuilder("ride")
          .defaultValue(true)
          .blurb("Ability to ride entities")
          .description("When disabled, players may not ride entities")
          .category(SettingCategory.ENTITIES)
          .playerRestrictive()
          .build();
  public static final SettingKey.Unary<Boolean> SLEEP =
      SettingKeyManagers.STATE_KEY_MANAGER.keyBuilder("sleep")
          .defaultValue(true)
          .blurb("Ability to sleep")
          .description("When disabled, players may not sleep.")
          .playerRestrictive()
          .build();
  public static final SettingKey.Poly<String, StringSet> SPAWNABLE_ENTITIES =
      SettingKeyManagers.POLY_ENTITY_KEY_MANAGER.keyBuilder("spawnable-entities")
          .fillDefaultData()
          .blurb("Spawnable entities")
          .description("List of entities which can be spawned")
          .category(SettingCategory.ENTITIES)
          .playerRestrictive()
          .build();
  public static final SettingKey.Unary<Boolean> TNT_IGNITION =
      SettingKeyManagers.STATE_KEY_MANAGER.keyBuilder("tnt-ignition")
          .defaultValue(true)
          .blurb("TNT ignition")
          .description("When disabled, TNT may not be primed.")
          .category(SettingCategory.BLOCKS)
          .build();
  public static final SettingKey.Unary<Boolean> TRAMPLE =
      SettingKeyManagers.STATE_KEY_MANAGER.keyBuilder("trample")
          .defaultValue(true)
          .blurb("Farmland trample restriction")
          .description("When disabled, blocks like farmland may not be trampled.")
          .category(SettingCategory.BLOCKS)
          .playerRestrictive()
          .build();
  public static final SettingKey.Unary<Boolean> USE_NAME_TAG =
      SettingKeyManagers.STATE_KEY_MANAGER.keyBuilder("use-name-tag")
          .defaultValue(true)
          .blurb("use-name-tag")
          .description("When disabled, players may not use name tags")
          .playerRestrictive()
          .build();
  public static final SettingKey.Unary<Boolean> WATER_FLOW =
      SettingKeyManagers.TOGGLE_KEY_MANAGER.keyBuilder("water-flow")
          .defaultValue(true)
          .blurb("Lava flow")
          .description("When disabled, lava does not spread")
          .category(SettingCategory.BLOCKS)
          .build();

  /**
   * Private constructor because its not supposed to be instantiated.
   */
  private SettingKeys() {
  }

  /**
   * Prepare the SettingLibrary for easy accessing of SettingKeys.
   *
   * @param keyStore a {@link SettingKeyStore} into which to store all the {@link SettingKey}s in this class.
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
