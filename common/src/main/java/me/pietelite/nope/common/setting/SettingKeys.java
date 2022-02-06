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

import me.pietelite.nope.common.setting.sets.BlockChangeSet;
import me.pietelite.nope.common.setting.sets.DamageCauseSet;
import me.pietelite.nope.common.setting.sets.ExplosiveSet;
import me.pietelite.nope.common.setting.sets.MobGriefSet;
import me.pietelite.nope.common.setting.sets.MovementSet;
import me.pietelite.nope.common.setting.sets.StringSet;
import me.pietelite.nope.common.struct.AltSet;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class SettingKeys {

  public static final SettingKey.Poly<BlockChangeSet.BlockChange, BlockChangeSet> BLOCK_CHANGE =
      SettingKey.Poly.builder("block-change", AltSet.full(new BlockChangeSet()), SettingKeyManagers.POLY_BLOCK_CHANGE_KEY_MANAGER)
          .blurb("How blocks may be changed")
          .description("A list of ways that blocks may be changed.")
          .category(SettingKey.Category.BLOCKS)
          .playerRestrictive()
          .build();
  public static final SettingKey.Unary<Boolean> BLOCK_PROPAGATE =
      SettingKey.Unary.builder("block-propagate", true, SettingKeyManagers.TOGGLE_KEY_MANAGER)
          .blurb("Block updates to neighbors")
          .description("When disabled, blocks will not update each other.")
          .category(SettingKey.Category.BLOCKS)
          .build();
  public static final SettingKey.Unary<Integer> CACHE_SIZE =
      SettingKey.Unary.builder("cache-size", 75000, SettingKeyManagers.INTEGER_KEY_MANAGER)
          .blurb("Size of world block caches")
          .description("This is the quantity of block locations to cache for each world. "
              + "Total memory is roughly this multiplied by 56 bytes, "
              + "multiplied by the number of worlds. Set 0 to disable caching.")
          .playerRestrictive()
          .global()
          .functional()
          .build();
  public static final SettingKey.Unary<Boolean> CONCRETE_SOLIDIFICATION =
      SettingKey.Unary.builder("concrete-solidification", true, SettingKeyManagers.TOGGLE_KEY_MANAGER)
          .blurb("Concrete powder solidification")
          .description("When disabled, concrete powder does not solidify into concrete.")
          .category(SettingKey.Category.BLOCKS)
          .playerRestrictive()
          .build();
  public static final SettingKey.Poly<ExplosiveSet.Explosive, ExplosiveSet> DESTRUCTIVE_EXPLOSIVES =
      SettingKey.Poly.builder("destructive-explosives",
              AltSet.full(new ExplosiveSet()),
              SettingKeyManagers.POLY_EXPLOSIVE_KEY_MANAGER)
          .blurb("Explosives causing no world damage")
          .description("A list of explosives whose explosions to not cause damage to the world.")
          .category(SettingKey.Category.BLOCKS)
          .build();
  public static final SettingKey.Unary<Boolean> DROP_EXP =
      SettingKey.Unary.builder("drop-exp", true, SettingKeyManagers.TOGGLE_KEY_MANAGER)
          .blurb("Experience drop")
          .description("When disabled, experience points are never dropped.")
          .category(SettingKey.Category.MISC)
          .build();
  public static final SettingKey.Poly<MovementSet.Movement, MovementSet> ENTRY =
      SettingKey.Poly.builder("entry", AltSet.full(new MovementSet()), SettingKeyManagers.POLY_MOVEMENT_KEY_MANAGER)
          .blurb("Host entrance restriction")
          .description("Specify which type of movement is allowed by players to enter.")
          .category(SettingKey.Category.MOVEMENT)
          .functional()
          .playerRestrictive()
          .build();
  public static final SettingKey.Unary<String> ENTRY_DENY_MESSAGE =
      SettingKey.Unary.builder("entry-deny-message",
              "You are not allowed to go there",
              SettingKeyManagers.STRING_KEY_MANAGER)
          .blurb("Message when entry is denied")
          .description("The message that is sent to a player if they are barred from entry.")
          .category(SettingKey.Category.MOVEMENT)
          .functional()
          .build();
  public static final SettingKey.Unary<String> ENTRY_DENY_SUBTITLE =
      SettingKey.Unary.builder("entry-deny-subtitle", "", SettingKeyManagers.STRING_KEY_MANAGER)
          .blurb("Subtitle when entry is denied")
          .description("The subtitle that is sent to a player if they are barred from entry.")
          .category(SettingKey.Category.MOVEMENT)
          .functional()
          .build();
  public static final SettingKey.Unary<String> ENTRY_DENY_TITLE =
      SettingKey.Unary.builder("entry-deny-title", "", SettingKeyManagers.STRING_KEY_MANAGER)
          .blurb("Title when entry is denied")
          .description("The title that is sent to a player if they are barred from entry.")
          .category(SettingKey.Category.MOVEMENT)
          .functional()
          .build();
  public static final SettingKey.Poly<MovementSet.Movement, MovementSet> EXIT =
      SettingKey.Poly.builder("exit", AltSet.full(new MovementSet()), SettingKeyManagers.POLY_MOVEMENT_KEY_MANAGER)
          .blurb("Host exit restriction")
          .description("Specify which type of movement is allowed by players to exit.")
          .category(SettingKey.Category.MOVEMENT)
          .playerRestrictive()
          .functional()
          .build();
  public static final SettingKey.Unary<String> EXIT_DENY_MESSAGE =
      SettingKey.Unary.builder("exit-deny-message",
              "You are not allowed to leave here",
              SettingKeyManagers.STRING_KEY_MANAGER)
          .blurb("Message when exit is denied")
          .description("The message that is sent to the player if they are barred from exiting.")
          .category(SettingKey.Category.MOVEMENT)
          .functional()
          .build();
  public static final SettingKey.Unary<String> EXIT_DENY_SUBTITLE =
      SettingKey.Unary.builder("exit-deny-subtitle", "", SettingKeyManagers.STRING_KEY_MANAGER)
          .blurb("Subtitle when exit is denied")
          .description("The subtitle that is sent to a player if they are barred from exiting")
          .category(SettingKey.Category.MOVEMENT)
          .functional()
          .build();
  public static final SettingKey.Unary<String> EXIT_DENY_TITLE =
      SettingKey.Unary.builder("exit-deny-title", "", SettingKeyManagers.STRING_KEY_MANAGER)
          .blurb("Title when exit is denied")
          .description("The title that is sent to a player if they are barred from exiting")
          .category(SettingKey.Category.MOVEMENT)
          .functional()
          .build();
  public static final SettingKey.Unary<String> FAREWELL =
      SettingKey.Unary.builder("farewell", "", SettingKeyManagers.STRING_KEY_MANAGER)
          .blurb("Message upon exit")
          .description("The message to a player when they leave the host.")
          .category(SettingKey.Category.MOVEMENT)
          .functional()
          .build();
  public static final SettingKey.Unary<String> FAREWELL_SUBTITLE =
      SettingKey.Unary.builder("farewell-subtitle", "", SettingKeyManagers.STRING_KEY_MANAGER)
          .blurb("Subtitle upon exit")
          .description("The subtitle that appears to a player when they leave the host.")
          .category(SettingKey.Category.MOVEMENT)
          .functional()
          .build();
  public static final SettingKey.Unary<String> FAREWELL_TITLE =
      SettingKey.Unary.builder("farewell-title", "", SettingKeyManagers.STRING_KEY_MANAGER)
          .blurb("Title upon exit")
          .description("The title that appears to a player when they leave the host.")
          .category(SettingKey.Category.MOVEMENT)
          .functional()
          .build();
  public static final SettingKey.Unary<Boolean> FIRE_EFFECT =
      SettingKey.Unary.builder("fire-effect", true, SettingKeyManagers.TOGGLE_KEY_MANAGER)
          .blurb("Fire spread/damage")
          .description("When disabled, fire does not spread or cause block damage")
          .category(SettingKey.Category.MISC)
          .build();
  public static final SettingKey.Unary<Boolean> FIRE_IGNITION =
      SettingKey.Unary.builder("fire-ignition", true, SettingKeyManagers.STATE_KEY_MANAGER)
          .blurb("Fire ignition restriction")
          .description("When disabled, fire may not be lit")
          .category(SettingKey.Category.MISC)
          .playerRestrictive()
          .build();
  public static final SettingKey.Unary<Boolean> FROSTED_ICE_FORM =
      SettingKey.Unary.builder("frosted-ice-form", true, SettingKeyManagers.TOGGLE_KEY_MANAGER)
          .blurb("Frosted ice formation")
          .description("When disabled, frost ice does not form.")
          .category(SettingKey.Category.BLOCKS)
          .build();
  public static final SettingKey.Unary<Boolean> FROSTED_ICE_MELT =
      SettingKey.Unary.builder("frosted-ice-melt", true, SettingKeyManagers.TOGGLE_KEY_MANAGER)
          .blurb("Frosted ice melt")
          .description("When disabled, frosted ice does not melt")
          .category(SettingKey.Category.BLOCKS)
          .build();
  public static final SettingKey.Unary<Boolean> GRASS_GROWTH =
      SettingKey.Unary.builder("grass-growth", true, SettingKeyManagers.TOGGLE_KEY_MANAGER)
          .blurb("Grass growth")
          .description("When disabled, grass cannot grow naturally")
          .category(SettingKey.Category.BLOCKS)
          .build();
  public static final SettingKey.Unary<String> GREETING =
      SettingKey.Unary.builder("greeting", "", SettingKeyManagers.STRING_KEY_MANAGER)
          .blurb("Message upon entry")
          .description("The message to a player when they enter")
          .category(SettingKey.Category.MOVEMENT)
          .functional()
          .build();
  public static final SettingKey.Unary<String> GREETING_SUBTITLE =
      SettingKey.Unary.builder("greeting-subtitle", "", SettingKeyManagers.STRING_KEY_MANAGER)
          .blurb("Subtitle upon entry")
          .description("The subtitle that appears to a player when they enter")
          .category(SettingKey.Category.MOVEMENT)
          .functional()
          .build();
  public static final SettingKey.Unary<String> GREETING_TITLE =
      SettingKey.Unary.builder("greeting-title", "", SettingKeyManagers.STRING_KEY_MANAGER)
          .blurb("Title upon entry")
          .description("The title that appears to a player when they enter")
          .category(SettingKey.Category.MOVEMENT)
          .functional()
          .build();
  public static final SettingKey.Poly<String, StringSet> GROWABLES =
      SettingKey.Poly.builder("growables",
              AltSet.full(new StringSet()),
              SettingKeyManagers.POLY_GROWABLE_KEY_MANAGER)
          .blurb("Growable blocks")
          .description("A list of blocks that can grow")
          .category(SettingKey.Category.BLOCKS)
          .playerRestrictive()
          .build();
  public static final SettingKey.Poly<ExplosiveSet.Explosive, ExplosiveSet> HARMFUL_EXPLOSIVES =
      SettingKey.Poly.builder("harmful-explosives",
              AltSet.full(new ExplosiveSet()),
              SettingKeyManagers.POLY_EXPLOSIVE_KEY_MANAGER)
          .blurb("Explosives causing no entity damage")
          .description("A list of explosives whose explosions do not cause damage to entities.")
          .category(SettingKey.Category.DAMAGE)
          .build();
  public static final SettingKey.Unary<Boolean> HEALTH_REGEN =
      SettingKey.Unary.builder("health-regen", true, SettingKeyManagers.TOGGLE_KEY_MANAGER)
          .blurb("Health regen")
          .description("When disabled, players do not regenerate health")
          .category(SettingKey.Category.DAMAGE)
          .playerRestrictive()
          .build();
  public static final SettingKey.Poly<String, StringSet> HOOKABLE_ENTITIES =
      SettingKey.Poly.builder("hookable-entities",
              AltSet.full(new StringSet()),
              SettingKeyManagers.POLY_ENTITY_KEY_MANAGER)
          .blurb("Entities that can be hooked")
          .description("A list of entities that can be hooked with a fishing rod")
          .category(SettingKey.Category.ENTITIES)
          .playerRestrictive()
          .build();
  public static final SettingKey.Unary<Boolean> HUNGER_DRAIN =
      SettingKey.Unary.builder("hunger-drain", true, SettingKeyManagers.TOGGLE_KEY_MANAGER)
          .blurb("Player hunger drain")
          .description("When disabled, player hunger does not drain naturally.")
          .playerRestrictive()
          .build();
  public static final SettingKey.Unary<Boolean> ICE_FORM =
      SettingKey.Unary.builder("ice-form", true, SettingKeyManagers.TOGGLE_KEY_MANAGER)
          .blurb("Ice formation")
          .description("When disabled, ice does not form.")
          .category(SettingKey.Category.BLOCKS)
          .build();
  public static final SettingKey.Unary<Boolean> ICE_MELT =
      SettingKey.Unary.builder("ice-melt", true, SettingKeyManagers.TOGGLE_KEY_MANAGER)
          .blurb("Ice melt")
          .description("When disabled, ice does not melt.")
          .category(SettingKey.Category.BLOCKS)
          .build();
  public static final SettingKey.Poly<String, StringSet> INTERACTIVE_BLOCKS =
      SettingKey.Poly.builder("interactive-blocks",
              AltSet.full(new StringSet()),
              SettingKeyManagers.POLY_BLOCK_KEY_MANAGER)
          .blurb("Interactive blocks")
          .description("A list of blocks with which that can be interacted.")
          .category(SettingKey.Category.BLOCKS)
          .playerRestrictive()
          .build();
  public static final SettingKey.Poly<String, StringSet> INTERACTIVE_ENTITIES =
      SettingKey.Poly.builder("interactive-entities",
              AltSet.full(new StringSet()),
              SettingKeyManagers.POLY_ENTITY_KEY_MANAGER)
          .blurb("Entity interactivity")
          .description("List of entities that can be interacted with.")
          .category(SettingKey.Category.DAMAGE)
          .build();
  public static final SettingKey.Poly<String, StringSet> INVINCIBLE_ENTITIES =
      SettingKey.Poly.builder("invincible-entities",
              new StringSet(),
              SettingKeyManagers.POLY_ENTITY_KEY_MANAGER)
          .blurb("Entity invincibility")
          .description("List of entities which cannot be damaged or destroyed.")
          .category(SettingKey.Category.DAMAGE)
          .build();
  public static final SettingKey.Unary<Boolean> ITEM_DROP =
      SettingKey.Unary.builder("item-drop", true, SettingKeyManagers.TOGGLE_KEY_MANAGER)
          .blurb("Item drop restriction")
          .description("When disabled, items cannot drop.")
          .playerRestrictive()
          .build();
  public static final SettingKey.Unary<Boolean> ITEM_PICKUP =
      SettingKey.Unary.builder("item-pickup", true, SettingKeyManagers.TOGGLE_KEY_MANAGER)
          .blurb("Item pickup restriction")
          .description("When disabled, items may not be picked up.")
          .playerRestrictive()
          .build();
  public static final SettingKey.Unary<Boolean> LAVA_FLOW =
      SettingKey.Unary.builder("lava-flow", true, SettingKeyManagers.TOGGLE_KEY_MANAGER)
          .blurb("Lava flow")
          .description("When disabled, lava does not spread")
          .category(SettingKey.Category.BLOCKS)
          .build();
  public static final SettingKey.Unary<Boolean> LAVA_GRIEF =
      SettingKey.Unary.builder("lava-grief", true, SettingKeyManagers.TOGGLE_KEY_MANAGER)
          .blurb("Grief caused by lava")
          .description("When disabled, lava does not break blocks")
          .category(SettingKey.Category.BLOCKS)
          .build();
  public static final SettingKey.Unary<Boolean> LEAF_DECAY =
      SettingKey.Unary.builder("leaf-decay", true, SettingKeyManagers.TOGGLE_KEY_MANAGER)
          .blurb("Leaf decay")
          .description("When disabled, leaves will not decay naturally.")
          .category(SettingKey.Category.BLOCKS)
          .build();
  public static final SettingKey.Poly<String, StringSet> LEASHABLE_ENTITIES =
      SettingKey.Poly.builder("leashable-entities",
              AltSet.full(new StringSet()),
              SettingKeyManagers.POLY_ENTITY_KEY_MANAGER)
          .blurb("Entities that can have leads")
          .description("A list of entities which can have leads attached to them")
          .category(SettingKey.Category.BLOCKS)
          .playerRestrictive()
          .build();
  public static final SettingKey.Unary<Boolean> LIGHTNING =
      SettingKey.Unary.builder("lightning", true, SettingKeyManagers.TOGGLE_KEY_MANAGER)
          .blurb("Lightning strikes")
          .description("When disabled, lightning cannot strike.")
          .build();
  public static final SettingKey.Unary<Boolean> LIGHT_NETHER_PORTAL =
      SettingKey.Unary.builder("light-nether-portal", true, SettingKeyManagers.STATE_KEY_MANAGER)
          .blurb("Lighting nether portals")
          .description("When disabled, players cannot light nether portals")
          .category(SettingKey.Category.MISC)
          .playerRestrictive()
          .build();
  public static final SettingKey.Poly<MobGriefSet.MobGrief, MobGriefSet> MOB_GRIEF =
      SettingKey.Poly.builder("mob-grief", AltSet.full(new MobGriefSet()), SettingKeyManagers.POLY_MOB_GRIEF_KEY_MANAGER)
          .blurb("Mobs that can grief")
          .description("A list of all mobs that can grief")
          .category(SettingKey.Category.ENTITIES)
          .build();
  public static final SettingKey.Poly<MovementSet.Movement, MovementSet> MOVE =
      SettingKey.Poly.builder("move", AltSet.full(new MovementSet()), SettingKeyManagers.POLY_MOVEMENT_KEY_MANAGER)
          .blurb("Movement within a host")
          .description("Specify which type of movement is allowed.")
          .category(SettingKey.Category.MOVEMENT)
          .playerRestrictive()
          .build();
  public static final SettingKey.Unary<Boolean> MYCELIUM_SPREAD =
      SettingKey.Unary.builder("mycelium-spread", true, SettingKeyManagers.TOGGLE_KEY_MANAGER)
          .blurb("Mycelium spread")
          .description("When disabled, mycelium does not spread")
          .category(SettingKey.Category.BLOCKS)
          .build();
  public static final SettingKey.Unary<Boolean> PLAYER_COLLISION =
      SettingKey.Unary.builder("player-collision", true, SettingKeyManagers.TOGGLE_KEY_MANAGER)
          .blurb("Collision between players")
          .description("When disabled, players do not collide")
          .category(SettingKey.Category.MOVEMENT)
          .build();
  public static final SettingKey.Poly<DamageCauseSet.DamageCause, DamageCauseSet> PLAYER_DAMAGE_SOURCE =
      SettingKey.Poly.builder("player-damage-source",
              AltSet.full(new DamageCauseSet()),
              SettingKeyManagers.POLY_DAMAGE_SOURCE_KEY_MANAGER)
          .blurb("Damage sources to players")
          .description("A list of damage sources that may inflict damage to players")
          .category(SettingKey.Category.DAMAGE)
          .build();
  public static final SettingKey.Unary<Boolean> RIDE =
      SettingKey.Unary.builder("ride", true, SettingKeyManagers.STATE_KEY_MANAGER)
          .blurb("Ability to ride entities")
          .description("When disabled, players may not ride entities")
          .category(SettingKey.Category.ENTITIES)
          .playerRestrictive()
          .build();
  public static final SettingKey.Unary<Boolean> SLEEP =
      SettingKey.Unary.builder("sleep", true, SettingKeyManagers.STATE_KEY_MANAGER)
          .blurb("Ability to sleep")
          .description("When disabled, players may not sleep.")
          .playerRestrictive()
          .build();
  public static final SettingKey.Poly<String, StringSet> SPAWNABLE_ENTITIES =
      SettingKey.Poly.builder("spawnable-entities",
              AltSet.full(new StringSet()),
              SettingKeyManagers.POLY_ENTITY_KEY_MANAGER)
          .blurb("Spawnable entities")
          .description("List of entities which can be spawned")
          .category(SettingKey.Category.ENTITIES)
          .playerRestrictive()
          .build();
  public static final SettingKey.Unary<Boolean> TNT_IGNITION =
      SettingKey.Unary.builder("tnt-ignition", true, SettingKeyManagers.STATE_KEY_MANAGER)
          .blurb("TNT ignition")
          .description("When disabled, TNT may not be primed.")
          .category(SettingKey.Category.BLOCKS)
          .playerRestrictive()
          .build();
  public static final SettingKey.Unary<Boolean> TRAMPLE =
      SettingKey.Unary.builder("trample", true, SettingKeyManagers.STATE_KEY_MANAGER)
          .blurb("Farmland trample restriction")
          .description("When disabled, blocks like farmland may not be trampled.")
          .category(SettingKey.Category.BLOCKS)
          .playerRestrictive()
          .build();
  public static final SettingKey.Unary<Boolean> USE_NAME_TAG =
      SettingKey.Unary.builder("use-name-tag", true, SettingKeyManagers.STATE_KEY_MANAGER)
          .blurb("use-name-tag")
          .description("When disabled, players may not use name tags")
          .playerRestrictive()
          .build();
  public static SettingKey.Unary<Boolean> WATER_FLOW =
      SettingKey.Unary.builder("water-flow", true, SettingKeyManagers.TOGGLE_KEY_MANAGER)
          .blurb("Lava flow")
          .description("When disabled, lava does not spread")
          .category(SettingKey.Category.BLOCKS)
          .build();
  public static SettingKey.Unary<Boolean> WATER_GRIEF =
      SettingKey.Unary.builder("water-grief", true, SettingKeyManagers.TOGGLE_KEY_MANAGER)
          .blurb("Grief caused by lava")
          .description("When disabled, lava does not break blocks")
          .category(SettingKey.Category.BLOCKS)
          .build();

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
