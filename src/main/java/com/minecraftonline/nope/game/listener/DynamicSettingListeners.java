/*
 * MIT License
 *
 * Copyright (c) 2021 MinecraftOnline
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

package com.minecraftonline.nope.game.listener;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.setting.SettingKey;
import com.minecraftonline.nope.setting.SettingLibrary;
import com.minecraftonline.nope.util.Format;
import net.minecraft.entity.monster.EntitySnowman;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.hanging.ItemFrame;
import org.spongepowered.api.entity.hanging.Painting;
import org.spongepowered.api.entity.living.Agent;
import org.spongepowered.api.entity.living.ArmorStand;
import org.spongepowered.api.entity.living.Hostile;
import org.spongepowered.api.entity.living.animal.Animal;
import org.spongepowered.api.entity.living.monster.Creeper;
import org.spongepowered.api.entity.living.monster.Ghast;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.weather.Lightning;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.action.SleepingEvent;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.data.ChangeDataHolderEvent;
import org.spongepowered.api.event.entity.*;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.world.LocatableBlock;
import org.spongepowered.api.world.explosion.Explosion;

import javax.annotation.Nullable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A container class for all dynamically registered listeners.
 */
@SuppressWarnings("unused")
public final class DynamicSettingListeners {

  private DynamicSettingListeners() {
  }

  @DynamicSettingListener
  static final SettingListener<AttackEntityEvent> ARMOR_STAND_ATTACK_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.ARMOR_STAND_DESTROY,
          AttackEntityEvent.class,
          entityVersusEntityCanceller(SettingLibrary.ARMOR_STAND_DESTROY, Player.class, ArmorStand.class));
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent.Break> BLOCK_BREAK_LISTENER =
      new PlayerCancelConditionSettingListener<>(
          SettingLibrary.BLOCK_BREAK,
          ChangeBlockEvent.Break.class,
          (event, player) -> event.getTransactions().stream().anyMatch(transaction ->
              !Nope.getInstance().getHostTree().lookup(
                  SettingLibrary.BLOCK_BREAK,
                  player,
                  transaction.getOriginal().getLocation().orElse(transaction.getFinal()
                      .getLocation()
                      .orElseThrow(noLocation(SettingLibrary.BLOCK_BREAK,
                          ChangeBlockEvent.Break.class,
                          player))))));
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent.Place> BLOCK_PLACE_LISTENER =
      new PlayerCancelConditionSettingListener<>(
          SettingLibrary.BLOCK_PLACE,
          ChangeBlockEvent.Place.class,
          (event, player) -> event.getTransactions().stream().anyMatch(transaction ->
              !Nope.getInstance().getHostTree().lookup(
                  SettingLibrary.BLOCK_PLACE,
                  player,
                  transaction.getOriginal().getLocation().orElse(transaction.getFinal()
                      .getLocation()
                      .orElseThrow(noLocation(SettingLibrary.BLOCK_PLACE,
                          ChangeBlockEvent.Place.class,
                          player))))));
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent> BLOCK_TRAMPLE =
      new PlayerCancelConditionSettingListener<>(
          SettingLibrary.BLOCK_TRAMPLE,
          ChangeBlockEvent.class,
          (event, player) -> event.getTransactions().stream().anyMatch(transaction ->
              !Nope.getInstance().getHostTree().lookup(
                  SettingLibrary.BLOCK_TRAMPLE,
                  player,
                  transaction.getOriginal().getLocation().orElse(transaction.getFinal()
                      .getLocation()
                      .orElse(player.getLocation())))
                  &&
                  transaction.getOriginal()
                      .getState()
                      .getType().equals(BlockTypes.FARMLAND)
                  &&
                  transaction.getFinal()
                      .getState()
                      .getType().equals(BlockTypes.DIRT)));
  @DynamicSettingListener
  static final SettingListener<InteractBlockEvent.Secondary> CHEST_ACCESS_LISTENER =
      new PlayerCancelConditionSettingListener<>(
          SettingLibrary.CHEST_ACCESS,
          InteractBlockEvent.Secondary.class,
          (event, player) -> {
            List<BlockType> chests = Lists.newArrayList(
                BlockTypes.CHEST,
                BlockTypes.ENDER_CHEST,
                BlockTypes.TRAPPED_CHEST,
                BlockTypes.BLACK_SHULKER_BOX,
                BlockTypes.BLUE_SHULKER_BOX,
                BlockTypes.BROWN_SHULKER_BOX,
                BlockTypes.CYAN_SHULKER_BOX,
                BlockTypes.GRAY_SHULKER_BOX,
                BlockTypes.GREEN_SHULKER_BOX,
                BlockTypes.LIGHT_BLUE_SHULKER_BOX,
                BlockTypes.LIME_SHULKER_BOX,
                BlockTypes.MAGENTA_SHULKER_BOX,
                BlockTypes.ORANGE_SHULKER_BOX,
                BlockTypes.PINK_SHULKER_BOX,
                BlockTypes.PURPLE_SHULKER_BOX,
                BlockTypes.RED_SHULKER_BOX,
                BlockTypes.SILVER_SHULKER_BOX,
                BlockTypes.WHITE_SHULKER_BOX,
                BlockTypes.YELLOW_SHULKER_BOX);
            return chests.contains(event.getTargetBlock().getState().getType())
                &&
                !Nope.getInstance().getHostTree().lookup(SettingLibrary.CHEST_ACCESS,
                    player,
                    event.getTargetBlock().getLocation()
                        .orElseThrow(noLocation(SettingLibrary.CHEST_ACCESS,
                            InteractBlockEvent.Secondary.class,
                            player)));
          });
  @DynamicSettingListener
  static final SettingListener<DamageEntityEvent> CREEPER_EXPLOSION_DAMAGE_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.CREEPER_EXPLOSION_DAMAGE,
          DamageEntityEvent.class,
          entityVersusEntityCanceller(SettingLibrary.CREEPER_EXPLOSION_DAMAGE, Creeper.class, Entity.class));
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent.Break> CREEPER_EXPLOSION_GRIEF_BLOCK_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.CREEPER_EXPLOSION_GRIEF,
          ChangeBlockEvent.Break.class,
          event -> {
            Explosion explosion;
            if (!(event.getCause().root() instanceof Explosion)) {
              return false;
            }
            explosion = (Explosion) event.getCause().root();
            if (!explosion.getSourceExplosive()
                .filter(explosive -> explosive.getType().equals(EntityTypes.CREEPER))
                .isPresent()) {
              return false;
            }
            return !Nope.getInstance()
                .getHostTree()
                .lookupAnonymous(SettingLibrary.CREEPER_EXPLOSION_GRIEF,
                    explosion.getLocation())
                ||
                event.getTransactions().stream().anyMatch(transaction ->
                    !Nope.getInstance().getHostTree().lookupAnonymous(
                        SettingLibrary.CREEPER_EXPLOSION_GRIEF,
                        transaction.getOriginal().getLocation().orElseThrow(noLocation(
                            SettingLibrary.CREEPER_EXPLOSION_GRIEF,
                            ChangeBlockEvent.Break.class,
                            null)))
                        ||
                        !Nope.getInstance().getHostTree().lookupAnonymous(
                            SettingLibrary.CREEPER_EXPLOSION_GRIEF,
                            transaction.getFinal().getLocation().orElseThrow(noLocation(
                                SettingLibrary.CREEPER_EXPLOSION_GRIEF,
                                ChangeBlockEvent.Break.class,
                                null))));
          });
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent.Grow> CROP_GROWTH_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.CROP_GROWTH,
          ChangeBlockEvent.Grow.class,
          event -> event.getTransactions().stream().anyMatch(transaction ->
              !Nope.getInstance().getHostTree().lookupAnonymous(SettingLibrary.CROP_GROWTH,
                  transaction.getOriginal().getLocation()
                      .orElseThrow(noLocation(SettingLibrary.CROP_GROWTH,
                          ChangeBlockEvent.Grow.class,
                          null)))
                  ||
                  !Nope.getInstance().getHostTree().lookupAnonymous(SettingLibrary.CROP_GROWTH,
                      transaction.getFinal().getLocation()
                          .orElseThrow(noLocation(SettingLibrary.CROP_GROWTH,
                              ChangeBlockEvent.Grow.class,
                              null)))));
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent.Break> ENDERDRAGON_GRIEF_BLOCK_LISTENER =
      new EntityBreakConditionSettingListener(
          SettingLibrary.ENDERDRAGON_GRIEF,
          EntityTypes.ENDER_DRAGON);
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent.Break> ENDERMAN_GRIEF_BLOCK_LISTENER =
      new EntityBreakConditionSettingListener(
          SettingLibrary.ENDERMAN_GRIEF,
          EntityTypes.ENDERMAN);
  @DynamicSettingListener
  static final SettingListener<MoveEntityEvent.Teleport> ENDERPEARL_TELEPORT_LISTENER =
      new PlayerCancelConditionSettingListener<>(
          SettingLibrary.ENDERPEARL_TELEPORT,
          MoveEntityEvent.Teleport.class,
          (event, player) -> event.getCause()
              .first(EntityTypes.ENDER_PEARL.getEntityClass())
              .isPresent()
              &&
              (!Nope.getInstance().getHostTree().lookup(SettingLibrary.ENDERPEARL_TELEPORT,
                  player,
                  event.getFromTransform().getLocation())
                  ||
                  !Nope.getInstance().getHostTree().lookup(SettingLibrary.ENDERPEARL_TELEPORT,
                      player,
                      event.getToTransform().getLocation())));

  @DynamicSettingListener
  static final SettingListener<DamageEntityEvent> EVP_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.EVP,
          DamageEntityEvent.class,
          event -> event.getCause()
              .first(DamageSource.class)
              .filter(damageSource -> Sets.newHashSet(
                  DamageTypes.CONTACT,
                  DamageTypes.DROWN,
                  DamageTypes.FALL,
                  DamageTypes.FIRE,
                  DamageTypes.HUNGER,
                  DamageTypes.MAGMA,
                  DamageTypes.SUFFOCATE).contains(damageSource.getType()))
              .isPresent()
              && event.getTargetEntity() instanceof Player
              && !Nope.getInstance().getHostTree().lookup(SettingLibrary.EVP,
              (Player) event.getTargetEntity(),
              event.getTargetEntity().getLocation()));

  @DynamicSettingListener
  static final SettingListener<DamageEntityEvent> FALL_DAMAGE_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.FALL_DAMAGE,
          DamageEntityEvent.class,
          event -> event.getCause()
              .first(DamageSource.class)
              .filter(damageSource ->
                  damageSource.getType().getId().equals(DamageTypes.FALL.getId()))
              .filter(damageSource ->
                  event.getTargetEntity() instanceof User
                      ? !Nope.getInstance()
                      .getHostTree()
                      .lookup(SettingLibrary.FALL_DAMAGE,
                          (User) event.getTargetEntity(),
                          event.getTargetEntity().getLocation())
                      : !Nope.getInstance()
                      .getHostTree()
                      .lookupAnonymous(SettingLibrary.FALL_DAMAGE,
                          event.getTargetEntity().getLocation()))
              .isPresent());
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent> FIRE_EFFECT_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.FIRE_EFFECT,
          ChangeBlockEvent.class,
          event -> event.getSource() instanceof LocatableBlock
              && ((LocatableBlock) event.getSource()).getBlockState()
              .getType()
              .equals(BlockTypes.FIRE)
              && event.getTransactions()
              .stream()
              .anyMatch(trans -> !Nope.getInstance()
                  .getHostTree()
                  .lookupAnonymous(SettingLibrary.FIRE_EFFECT, trans.getFinal()
                      .getLocation()
                      .orElseThrow(noLocation(SettingLibrary.FIRE_EFFECT,
                          ChangeBlockEvent.class,
                          null)))));
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent> FIRE_IGNITION_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.FIRE_IGNITION,
          ChangeBlockEvent.class,
          event -> event.getSource() instanceof Player
              && event.getTransactions()
              .stream()
              .anyMatch(trans -> trans.getFinal().getState().getType().equals(BlockTypes.FIRE)
                  && !Nope.getInstance()
                  .getHostTree()
                  .lookup(SettingLibrary.FIRE_IGNITION,
                      (Player) event.getSource(),
                      trans.getFinal()
                          .getLocation()
                          .orElseThrow(noLocation(SettingLibrary.FIRE_IGNITION,
                              ChangeBlockEvent.class,
                              (Player) event.getSource())))));
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent> FIRE_NATURAL_IGNITION_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.FIRE_NATURAL_IGNITION,
          ChangeBlockEvent.class,
          event -> !(event.getSource() instanceof Player)
              && event.getTransactions()
              .stream()
              .anyMatch(trans -> trans.getFinal().getState().getType().equals(BlockTypes.FIRE)
                  && !Nope.getInstance()
                  .getHostTree()
                  .lookupAnonymous(SettingLibrary.FIRE_NATURAL_IGNITION, trans.getFinal()
                      .getLocation()
                      .orElseThrow(noLocation(SettingLibrary.FIRE_NATURAL_IGNITION,
                          ChangeBlockEvent.class,
                          null)))));
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent> FROSTED_ICE_FORM_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.FROSTED_ICE_FORM,
          ChangeBlockEvent.class,
          simpleChangeBlockCanceler(SettingLibrary.FROSTED_ICE_FORM,
              BlockTypes.WATER,
              BlockTypes.FROSTED_ICE));
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent> FROSTED_ICE_MELT_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.FROSTED_ICE_MELT,
          ChangeBlockEvent.class,
          simpleChangeBlockCanceler(SettingLibrary.FROSTED_ICE_MELT,
              BlockTypes.FROSTED_ICE,
              BlockTypes.WATER));
  @DynamicSettingListener
  static final SettingListener<SpawnEntityEvent> GHAST_FIREBALL_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.GHAST_FIREBALL,
          SpawnEntityEvent.class,
          event -> event.getSource() instanceof Ghast
              && event.getEntities().stream()
              .filter(entity -> entity.getType().equals(EntityTypes.FIREBALL))
              .anyMatch(entity -> !Nope.getInstance()
                  .getHostTree()
                  .lookupAnonymous(SettingLibrary.GHAST_FIREBALL, entity.getLocation())));
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent> GRASS_GROWTH_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.GRASS_GROWTH,
          ChangeBlockEvent.class,
          simpleChangeBlockCanceler(SettingLibrary.GRASS_GROWTH,
              BlockTypes.DIRT,
              BlockTypes.GRASS));
  @DynamicSettingListener
  static final SettingListener<DamageEntityEvent> HVP_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.HVP,
          DamageEntityEvent.class,
          entityVersusEntityCanceller(SettingLibrary.HVP, Hostile.class, Player.class));
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent> ICE_FORM_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.ICE_FORM,
          ChangeBlockEvent.class,
          simpleChangeBlockCanceler(SettingLibrary.ICE_FORM,
              BlockTypes.WATER,
              BlockTypes.ICE));
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent> ICE_MELT_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.ICE_MELT,
          ChangeBlockEvent.class,
          simpleChangeBlockCanceler(SettingLibrary.ICE_MELT,
              BlockTypes.ICE,
              BlockTypes.WATER));
  @DynamicSettingListener
  static final SettingListener<InteractBlockEvent.Secondary> INTERACT_LISTENER =
      new PlayerCancelConditionSettingListener<>(
          SettingLibrary.INTERACT,
          InteractBlockEvent.Secondary.class,
          (event, player) -> !Nope.getInstance()
              .getHostTree()
              .lookup(SettingLibrary.INTERACT,
                  player,
                  event.getTargetBlock()
                      .getLocation()
                      .orElseThrow(noLocation(SettingLibrary.INTERACT,
                          InteractBlockEvent.Secondary.class,
                          player)))
              || !Nope.getInstance()
              .getHostTree()
              .lookup(SettingLibrary.INTERACT,
                  player,
                  player.getLocation()));
  @DynamicSettingListener
  static final SettingListener<DamageEntityEvent> INVINCIBLE_ANIMALS_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.INVINCIBLE_ANIMALS,
          DamageEntityEvent.class,
          event -> event.getTargetEntity() instanceof Animal
              && Nope.getInstance()
              .getHostTree()
              .lookupAnonymous(
                  SettingLibrary.INVINCIBLE_ANIMALS,
                  event.getTargetEntity().getLocation()));
  @DynamicSettingListener
  static final SettingListener<DamageEntityEvent> INVINCIBLE_MOBS_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.INVINCIBLE_MOBS,
          DamageEntityEvent.class,
          event -> event.getTargetEntity() instanceof Agent
              && !(event.getTargetEntity() instanceof Player)
              && Nope.getInstance()
              .getHostTree()
              .lookupAnonymous(
                  SettingLibrary.INVINCIBLE_MOBS,
                  event.getTargetEntity().getLocation()));
  @DynamicSettingListener
  static final SettingListener<DamageEntityEvent> INVINCIBLE_PLAYERS_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.INVINCIBLE_PLAYERS,
          DamageEntityEvent.class,
          event -> event.getTargetEntity() instanceof Player
              && Nope.getInstance()
              .getHostTree()
              .lookup(
                  SettingLibrary.INVINCIBLE_PLAYERS,
                  (Player) event.getTargetEntity(),
                  event.getTargetEntity().getLocation()));
  @DynamicSettingListener
  static final SettingListener<ClickInventoryEvent.Creative> ITEM_DROP_CREATIVE_LISTENER =
      new PlayerRootSettingListener<>(
          SettingLibrary.ITEM_DROP,
          ClickInventoryEvent.Creative.class,
          (event, player) -> {
            boolean cancel = !Nope.getInstance().getHostTree().lookup(SettingLibrary.ITEM_DROP,
                player,
                player.getLocation());
            if (cancel) {
              player.sendMessage(Format.warn(Format.hover("Don't drop anything! Hover to see more.",
                  "Item drop is disabled here. "
                      + "Due to a Sponge bug, "
                      + "clicking outside of your Creative inventory "
                      + "will destroy the item!")));
            }
          });
  @DynamicSettingListener
  static final SettingListener<DropItemEvent.Dispense> ITEM_DROP_LISTENER =
      new PlayerCancelConditionSettingListener<>(
          SettingLibrary.ITEM_DROP,
          DropItemEvent.Dispense.class,
          (event, player) -> !Nope.getInstance()
              .getHostTree()
              .lookup(SettingLibrary.ITEM_DROP,
                  player,
                  player.getLocation()));
  @DynamicSettingListener
  static final SettingListener<AttackEntityEvent> ITEM_FRAME_ATTACK_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.ITEM_FRAME_DESTROY,
          AttackEntityEvent.class,
          entityVersusEntityCanceller(SettingLibrary.ITEM_FRAME_DESTROY,
              Player.class,
              ItemFrame.class));
  @DynamicSettingListener
  static final SettingListener<ChangeInventoryEvent.Pickup.Pre> ITEM_PICKUP_LISTENER =
      new PlayerCancelConditionSettingListener<>(
          SettingLibrary.ITEM_PICKUP,
          ChangeInventoryEvent.Pickup.Pre.class,
          (event, player) -> !Nope.getInstance().getHostTree()
              .lookup(SettingLibrary.ITEM_PICKUP,
                  player,
                  event.getTargetEntity().getLocation()));
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent> LAVA_FLOW_LISTENER =
      new SingleSettingListener<>(
          SettingLibrary.LAVA_FLOW,
          ChangeBlockEvent.class,
          event ->
              liquidFlowHandler(SettingLibrary.LAVA_FLOW, BlockTypes.FLOWING_LAVA)
                  .accept(event));
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent> LEAF_DECAY_2_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.LEAF_DECAY,
          ChangeBlockEvent.class,
          simpleChangeBlockCanceler(SettingLibrary.LEAF_DECAY,
              BlockTypes.LEAVES2,
              BlockTypes.AIR));
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent> LEAF_DECAY_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.LEAF_DECAY,
          ChangeBlockEvent.class,
          simpleChangeBlockCanceler(SettingLibrary.LEAF_DECAY,
              BlockTypes.LEAVES,
              BlockTypes.AIR));
  @DynamicSettingListener
  static final SettingListener<SpawnEntityEvent> LIGHTNING_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.LIGHTNING,
          SpawnEntityEvent.class,
          event -> event.getEntities().stream().anyMatch(spawned ->
              spawned instanceof Lightning
                  && !Nope.getInstance()
                  .getHostTree()
                  .lookupAnonymous(SettingLibrary.LIGHTNING,
                      spawned.getLocation())));
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent> MUSHROOM_GROWTH_BROWN_BLOCK_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.MUSHROOM_GROWTH,
          ChangeBlockEvent.class,
          simpleChangeBlockCanceler(SettingLibrary.MUSHROOM_GROWTH,
              BlockTypes.BROWN_MUSHROOM,
              BlockTypes.BROWN_MUSHROOM_BLOCK));
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent> MUSHROOM_GROWTH_BROWN_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.MUSHROOM_GROWTH,
          ChangeBlockEvent.class,
          simpleChangeBlockCanceler(SettingLibrary.MUSHROOM_GROWTH,
              BlockTypes.AIR,
              BlockTypes.BROWN_MUSHROOM));
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent> MUSHROOM_GROWTH_RED_BLOCK_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.MUSHROOM_GROWTH,
          ChangeBlockEvent.class,
          simpleChangeBlockCanceler(SettingLibrary.MUSHROOM_GROWTH,
              BlockTypes.RED_MUSHROOM,
              BlockTypes.RED_MUSHROOM_BLOCK));
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent> MUSHROOM_GROWTH_RED_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.MUSHROOM_GROWTH,
          ChangeBlockEvent.class,
          simpleChangeBlockCanceler(SettingLibrary.MUSHROOM_GROWTH,
              BlockTypes.AIR,
              BlockTypes.RED_MUSHROOM));
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent> MYCELIUM_SPREAD_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.MYCELIUM_SPREAD,
          ChangeBlockEvent.class,
          simpleChangeBlockCanceler(SettingLibrary.MYCELIUM_SPREAD,
              BlockTypes.DIRT,
              BlockTypes.MYCELIUM));
  @DynamicSettingListener
  static final SettingListener<ChangeDataHolderEvent.ValueChange> NATURAL_HEALTH_REGEN =
      new CancelConditionSettingListener<>(
          SettingLibrary.NATURAL_HEALTH_REGEN,
          ChangeDataHolderEvent.ValueChange.class,
          event -> event.getSource() instanceof Player
              && event.getTargetHolder() instanceof Player
              && ((Player) event.getSource())
              .getUniqueId()
              .equals(((Player) event.getTargetHolder()).getUniqueId())
              && event.getOriginalChanges()
              .getSuccessfulData()
              .stream()
              .anyMatch(data -> data.getKey().equals(Keys.HEALTH))
              && !Nope.getInstance()
              .getHostTree()
              .lookup(SettingLibrary.NATURAL_HEALTH_REGEN,
                  event.getTargetHolder() instanceof Player
                      ? (Player) event.getTargetHolder()
                      : null,
                  ((Player) event.getTargetHolder()).getLocation()));
  @DynamicSettingListener
  static final SettingListener<AttackEntityEvent> PAINTING_DESTROY_ATTACK_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.PAINTING_DESTROY,
          AttackEntityEvent.class,
          entityVersusEntityCanceller(SettingLibrary.PAINTING_DESTROY,
              Player.class,
              Painting.class));
  @DynamicSettingListener
  static final SettingListener<MoveEntityEvent> PLAYER_COLLISION_LISTENER =
      new SingleSettingListener<>(
          SettingLibrary.PLAYER_COLLISION,
          MoveEntityEvent.class,
          event -> {
            if (!(event.getTargetEntity() instanceof Player)) {
              return;
            }
            if (Nope.getInstance().getHostTree().lookup(SettingLibrary.PLAYER_COLLISION,
                (Player) event.getTargetEntity(),
                event.getTargetEntity().getLocation())) {
              Nope.getInstance()
                  .getCollisionHandler()
                  .enableCollision((Player) event.getTargetEntity());
            } else {
              Nope.getInstance()
                  .getCollisionHandler()
                  .disableCollision((Player) event.getTargetEntity());
            }
          });
  @DynamicSettingListener
  static final SettingListener<DamageEntityEvent> PVA_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.PVA,
          DamageEntityEvent.class,
          entityVersusEntityCanceller(SettingLibrary.PVA, Player.class, Animal.class));
  @DynamicSettingListener
  static final SettingListener<DamageEntityEvent> PVH_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.PVH,
          DamageEntityEvent.class,
          entityVersusEntityCanceller(SettingLibrary.PVH, Player.class, Hostile.class));
  @DynamicSettingListener
  static final SettingListener<DamageEntityEvent> PVP_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.PVH,
          DamageEntityEvent.class,
          entityVersusEntityCanceller(SettingLibrary.PVP, Player.class, Player.class));
  @DynamicSettingListener
  static final SettingListener<RideEntityEvent.Mount> RIDE_LISTENER =
      new PlayerCancelConditionSettingListener<>(
          SettingLibrary.RIDE,
          RideEntityEvent.Mount.class,
          (event, player) -> !Nope.getInstance()
              .getHostTree()
              .lookup(SettingLibrary.RIDE,
                  player,
                  player.getLocation())
              || !Nope.getInstance()
              .getHostTree()
              .lookupAnonymous(SettingLibrary.RIDE,
                  event.getTargetEntity().getLocation()));
  @DynamicSettingListener
  static final SettingListener<SleepingEvent.Pre> SLEEP =
      new PlayerCancelConditionSettingListener<>(
          SettingLibrary.SLEEP,
          SleepingEvent.Pre.class,
          (event, player) -> (!Nope.getInstance().getHostTree()
              .lookup(SettingLibrary.SLEEP,
                  player,
                  player.getLocation())
              || !Nope.getInstance().getHostTree()
              .lookupAnonymous(SettingLibrary.SLEEP,
                  event.getBed()
                      .getLocation()
                      .orElseThrow(noLocation(SettingLibrary.SLEEP,
                          SleepingEvent.Pre.class,
                          player)))));
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent> SNOWMAN_TRAIL_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.SNOWMAN_TRAILS,
          ChangeBlockEvent.class,
          event -> event.getSource() instanceof EntitySnowman
              && event.getTransactions().stream()
              .anyMatch(trans -> trans.getFinal()
                  .getState()
                  .getType()
                  .equals(BlockTypes.SNOW_LAYER)
                  && !Nope.getInstance()
                  .getHostTree()
                  .lookupAnonymous(SettingLibrary.SNOWMAN_TRAILS,
                      trans.getFinal()
                          .getLocation()
                          .orElseThrow(noLocation(SettingLibrary.SNOWMAN_TRAILS,
                              ChangeBlockEvent.class,
                              null)))));
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent> SNOW_ACCUMULATION_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.SNOW_ACCUMULATION,
          ChangeBlockEvent.class,
          event -> !(event.getSource() instanceof Entity)
              && simpleChangeBlockCanceler(SettingLibrary.SNOW_ACCUMULATION,
              BlockTypes.AIR,
              BlockTypes.SNOW_LAYER).test(event));
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent> SNOW_MELT_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.SNOW_MELT,
          ChangeBlockEvent.class,
          event -> !(event.getSource() instanceof Entity)
              && simpleChangeBlockCanceler(SettingLibrary.SNOW_MELT,
              BlockTypes.SNOW_LAYER,
              BlockTypes.AIR).test(event));
  @DynamicSettingListener
  static final SettingListener<SpawnEntityEvent> SPAWN_ANIMAL_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.SPAWN_ANIMAL,
          SpawnEntityEvent.class,
          event -> event.getEntities().stream().anyMatch(entity ->
              (entity instanceof Animal)
                  && !Nope.getInstance()
                  .getHostTree()
                  .lookupAnonymous(SettingLibrary.SPAWN_ANIMAL, entity.getLocation())));
  @DynamicSettingListener
  static final SettingListener<SpawnEntityEvent> SPAWN_HOSTILE_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.SPAWN_HOSTILE,
          SpawnEntityEvent.class,
          event -> event.getEntities().stream().anyMatch(entity ->
              (entity instanceof Hostile)
                  && !Nope.getInstance()
                  .getHostTree()
                  .lookupAnonymous(SettingLibrary.SPAWN_HOSTILE, entity.getLocation())));
  @DynamicSettingListener
  static final SettingListener<SpawnEntityEvent> SPAWN_MOB_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.SPAWN_MOB,
          SpawnEntityEvent.class,
          event -> event.getEntities().stream().anyMatch(entity ->
              (entity instanceof Agent)
                  && !(entity instanceof Player)
                  && !Nope.getInstance()
                  .getHostTree()
                  .lookupAnonymous(SettingLibrary.SPAWN_MOB, entity.getLocation())));
  @DynamicSettingListener
  static final SettingListener<InteractEntityEvent.Secondary> TNT_CART_IGNITION_LISTENER =
      new PlayerCancelConditionSettingListener<>(
          SettingLibrary.TNT_IGNITION,
          InteractEntityEvent.Secondary.class,
          (event, player) -> event.getTargetEntity()
              .getType()
              .equals(EntityTypes.TNT_MINECART)
              && !Nope.getInstance()
              .getHostTree()
              .lookup(SettingLibrary.TNT_IGNITION,
                  player,
                  event.getTargetEntity().getLocation()));
  @DynamicSettingListener
  static final SettingListener<SpawnEntityEvent> TNT_CART_PLACEMENT_LISTENER =
      new PlayerCancelConditionSettingListener<>(
          SettingLibrary.TNT_PLACEMENT,
          SpawnEntityEvent.class,
          (event, player) -> event.getEntities()
              .stream()
              .anyMatch(entity ->
                  entity.getType().equals(EntityTypes.TNT_MINECART)
                      && !Nope.getInstance()
                      .getHostTree()
                      .lookup(SettingLibrary.TNT_PLACEMENT,
                          player,
                          entity.getLocation())));
  @DynamicSettingListener
  static final SettingListener<InteractBlockEvent.Secondary> TNT_IGNITION_LISTENER =
      new PlayerCancelConditionSettingListener<>(
          SettingLibrary.TNT_IGNITION,
          InteractBlockEvent.Secondary.class,
          (event, player) -> event.getTargetBlock()
              .getState()
              .getType()
              .equals(BlockTypes.TNT)
              && !Nope.getInstance()
              .getHostTree()
              .lookup(SettingLibrary.TNT_IGNITION,
                  player,
                  event.getTargetBlock()
                      .getLocation()
                      .orElseThrow(noLocation(SettingLibrary.TNT_IGNITION,
                          InteractBlockEvent.Secondary.class,
                          player))));
  @DynamicSettingListener
  static final SettingListener<ConstructEntityEvent.Post> TNT_SPAWN_LISTENER =
      new SingleSettingListener<>(
          SettingLibrary.TNT_IGNITION,
          ConstructEntityEvent.Post.class,
          (event) -> {
            if (event.getTargetEntity().getType().equals(EntityTypes.PRIMED_TNT)) {
              if (!Nope.getInstance().getHostTree().lookup(SettingLibrary.TNT_IGNITION,
                  event.getCause().first(User.class).orElseGet(() -> {
                        if (event.getContext().get(EventContextKeys.OWNER).isPresent()) {
                          return event.getContext().get(EventContextKeys.OWNER).get();
                        }
                        return null;
                      }
                  ),
                  event.getTargetEntity().getLocation())) {
                event.getTargetEntity().remove();
              }
            }
          });
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent.Place> TNT_PLACEMENT_LISTENER =
      new PlayerCancelConditionSettingListener<>(
          SettingLibrary.TNT_PLACEMENT,
          ChangeBlockEvent.Place.class,
          (event, player) -> event.getTransactions()
              .stream()
              .anyMatch(trans -> trans.getFinal()
                  .getState()
                  .getType()
                  .equals(BlockTypes.TNT)
                  && !Nope.getInstance()
                  .getHostTree()
                  .lookup(SettingLibrary.TNT_PLACEMENT,
                      player,
                      trans.getFinal()
                          .getLocation()
                          .orElseThrow(noLocation(SettingLibrary.TNT_PLACEMENT,
                              ChangeBlockEvent.Place.class,
                              player)))));
  @DynamicSettingListener
  static final SettingListener<SpawnEntityEvent> UNSPAWNABLE_MOBS_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.UNSPAWNABLE_MOBS,
          SpawnEntityEvent.class,
          event -> event.getEntities().stream().anyMatch(entity ->
              Nope.getInstance()
                  .getHostTree()
                  .lookupAnonymous(SettingLibrary.UNSPAWNABLE_MOBS, entity.getLocation())
                  .contains(entity.getType())));
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent> VINE_GROWTH_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.VINE_GROWTH,
          ChangeBlockEvent.class,
          simpleChangeBlockCanceler(SettingLibrary.VINE_GROWTH,
              BlockTypes.AIR,
              BlockTypes.VINE));
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent> WATER_FLOW_LISTENER =
      new SingleSettingListener<>(
          SettingLibrary.WATER_FLOW,
          ChangeBlockEvent.class,
          event ->
              liquidFlowHandler(SettingLibrary.WATER_FLOW, BlockTypes.FLOWING_WATER)
                  .accept(event));
  private static Set<EntityType> VEHICLES = Sets.newHashSet(EntityTypes.BOAT,
      EntityTypes.CHESTED_MINECART,
      EntityTypes.COMMANDBLOCK_MINECART,
      EntityTypes.FURNACE_MINECART,
      EntityTypes.HOPPER_MINECART,
      EntityTypes.MOB_SPAWNER_MINECART,
      EntityTypes.RIDEABLE_MINECART,
      EntityTypes.TNT_MINECART);
  @DynamicSettingListener
  static final SettingListener<InteractEntityEvent.Primary> VEHICLE_DESTROY_LISTENER =
      new PlayerCancelConditionSettingListener<>(
          SettingLibrary.VEHICLE_DESTROY,
          InteractEntityEvent.Primary.class,
          (event, player) -> VEHICLES.contains(event.getTargetEntity().getType())
              &&
              (!Nope.getInstance().getHostTree().lookup(SettingLibrary.VEHICLE_DESTROY,
                  player,
                  player.getLocation())
                  || !Nope.getInstance().getHostTree().lookup(SettingLibrary.VEHICLE_DESTROY,
                  player,
                  event.getTargetEntity().getLocation())));
  @DynamicSettingListener
  static final SettingListener<SpawnEntityEvent> VEHICLE_PLACE_LISTENER =
      new PlayerCancelConditionSettingListener<>(
          SettingLibrary.VEHICLE_PLACE,
          SpawnEntityEvent.class,
          (event, player) -> event.getEntities().stream().anyMatch(spawned ->
              VEHICLES.contains(spawned.getType())
                  &&
                  (!Nope.getInstance().getHostTree().lookup(SettingLibrary.VEHICLE_PLACE,
                      player,
                      player.getLocation())
                      || !Nope.getInstance().getHostTree().lookup(SettingLibrary.VEHICLE_PLACE,
                      player,
                      spawned.getLocation()))));

  private static Consumer<ChangeBlockEvent> liquidFlowHandler(SettingKey<Boolean> key,
                                                              BlockType flowingType) {
    return event -> event.getTransactions().stream()
        .filter(trans ->
            trans.getFinal().getState().getType().equals(flowingType))
        .forEach(trans ->
            trans.setValid((event.getSource() instanceof Player)
                || Nope.getInstance()
                .getHostTree()
                .lookupAnonymous(key,
                    trans.getFinal()
                        .getLocation()
                        .orElseThrow(noLocation(key,
                            ChangeBlockEvent.class,
                            null)))));
  }

  /**
   * Get all {@link SettingListener}s in the class that are
   * annotated with {@link DynamicSettingListener} and attempt
   * to register it using its {@link SettingListener#registerIfNecessary()}
   * method.
   */
  public static void register() {
    Arrays.stream(DynamicSettingListeners.class.getDeclaredFields())
        .filter(field -> Modifier.isStatic(field.getModifiers()))
        .filter(field -> SettingListener.class.isAssignableFrom(field.getType()))
        .filter(field -> Arrays.stream(field.getAnnotations()).anyMatch(annotation ->
            annotation instanceof DynamicSettingListener))
        .forEach(field -> {
          try {
            ((SettingListener<?>) field.get(null)).registerIfNecessary();
          } catch (IllegalAccessException e) {
            e.printStackTrace();
          }
        });
  }

  private static Supplier<RuntimeException> noLocation(SettingKey<?> key,
                                                       Class<? extends Event> eventClass,
                                                       @Nullable Player player) {
    return () -> new RuntimeException(String.format(
        "The relevant location for the dynamic event listener for "
            + "Setting Key %s and event class %s could not be found.",
        key.getId(),
        eventClass.getName())
        +
        (player == null
            ? ""
            : String.format(" The player is %s at position (%d, %d, %d) in world %s",
            player.getName(),
            player.getLocation().getBlockX(),
            player.getLocation().getBlockY(),
            player.getLocation().getBlockZ(),
            player.getLocation().getExtent().getName())));
  }

  static void printEvent(Event event) {
    Nope.getInstance().getLogger().info("Event... (" + event.getClass().getSimpleName() + ")");
    Nope.getInstance().getLogger().info("Source: " + event.getSource().toString());
    event.getCause().forEach(o -> Nope.getInstance().getLogger().info("Cause: " + o.toString()));
    Nope.getInstance().getLogger().info("Context: " + event.getContext().asMap().entrySet()
        .stream()
        .map(entry -> "{key: " + entry.getKey() + ", value: " + entry.getValue())
        .collect(Collectors.joining(", ")));
  }

  private static <T extends TargetEntityEvent> Predicate<T> entityVersusEntityCanceller(
      SettingKey<Boolean> key,
      Class<? extends Entity> sourceClass,
      Class<? extends Entity> sinkClass) {
    return (event) -> {
      if (!sinkClass.isInstance(event.getTargetEntity())) {
        return false;
      }
      Entity sink = sinkClass.cast(event.getTargetEntity());
      Entity source = event.getCause().first(EntityDamageSource.class)
          .map(EntityDamageSource::getSource)
          .filter(sourceClass::isInstance)
          .orElse(event.getCause().first(IndirectEntityDamageSource.class)
              .map(IndirectEntityDamageSource::getIndirectSource)
              .filter(sourceClass::isInstance)
              .orElse(null));
      if (source == null) {
        return false;
      }
      return !Nope.getInstance()
          .getHostTree()
          .lookup(key,
              (source instanceof Player)
                  ? (Player) source
                  :
                  (sink instanceof Player)
                      ? (Player) sink
                      : null,
              source.getLocation())
          || !Nope.getInstance()
          .getHostTree()
          .lookup(key,
              (sink instanceof Player)
                  ? (Player) sink
                  :
                  (source instanceof Player)
                      ? (Player) source
                      : null,
              sink.getLocation());
    };
  }

  private static Predicate<ChangeBlockEvent> simpleChangeBlockCanceler(SettingKey<Boolean> key,
                                                                       BlockType first,
                                                                       BlockType last) {
    return event -> event.getTransactions().stream()
        .filter(trans ->
            trans.getOriginal().getState().getType().equals(first))
        .filter(trans ->
            trans.getFinal().getState().getType().equals(last))
        .anyMatch(trans -> !Nope.getInstance()
            .getHostTree()
            .lookupAnonymous(key,
                trans.getFinal().getLocation().orElseThrow(noLocation(key,
                    ChangeBlockEvent.class,
                    null))));
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface DynamicSettingListener {
    // none
  }

}
