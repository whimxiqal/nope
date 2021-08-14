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

package com.minecraftonline.nope.sponge.listener.dynamic;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.minecraftonline.nope.sponge.SpongeNope;
import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.common.setting.SettingLibrary;
import com.minecraftonline.nope.sponge.listener.CancelConditionSettingListener;
import com.minecraftonline.nope.sponge.listener.EntityBreakConditionSettingListener;
import com.minecraftonline.nope.sponge.listener.PlayerCauseCancelConditionSettingListener;
import com.minecraftonline.nope.sponge.listener.PlayerRootCancelConditionSettingListener;
import com.minecraftonline.nope.sponge.listener.PlayerRootSettingListener;
import com.minecraftonline.nope.sponge.listener.SettingListener;
import com.minecraftonline.nope.sponge.listener.SingleSettingListener;
import com.minecraftonline.nope.sponge.listener.StaticSettingListeners;
import com.minecraftonline.nope.sponge.util.Extra;
import com.minecraftonline.nope.sponge.util.Format;
import com.minecraftonline.nope.sponge.util.Groups;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.projectile.EntityTippedArrow;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.ExperienceOrb;
import org.spongepowered.api.entity.explosive.Explosive;
import org.spongepowered.api.entity.hanging.ItemFrame;
import org.spongepowered.api.entity.hanging.Painting;
import org.spongepowered.api.entity.living.Agent;
import org.spongepowered.api.entity.living.ArmorStand;
import org.spongepowered.api.entity.living.Hostile;
import org.spongepowered.api.entity.living.Squid;
import org.spongepowered.api.entity.living.animal.Animal;
import org.spongepowered.api.entity.living.monster.Ghast;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.weather.Lightning;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.action.FishingEvent;
import org.spongepowered.api.event.action.SleepingEvent;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.block.NotifyNeighborBlockEvent;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.data.ChangeDataHolderEvent;
import org.spongepowered.api.event.entity.AttackEntityEvent;
import org.spongepowered.api.event.entity.ConstructEntityEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.IgniteEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.entity.LeashEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.RideEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.entity.TargetEntityEvent;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.event.world.ExplosionEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.LocatableBlock;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.explosion.Explosion;

/**
 * A container class for all dynamically registered listeners.
 *
 * @see StaticSettingListeners
 */
@SuppressWarnings("unused")
public final class DynamicSettingListeners {

  @DynamicSettingListener
  static final SettingListener<AttackEntityEvent> ARMOR_STAND_ATTACK_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.ARMOR_STAND_DESTROY,
          AttackEntityEvent.class,
          entityVersusEntityCanceller(SettingLibrary.ARMOR_STAND_DESTROY,
              Player.class,
              ArmorStand.class));
  @DynamicSettingListener
  static final SettingListener<InteractEntityEvent.Secondary> ARMOR_STAND_INTERACT_LISTENER =
      new PlayerRootCancelConditionSettingListener<>(
          SettingLibrary.ARMOR_STAND_INTERACT,
          InteractEntityEvent.Secondary.class,
          (event, player) -> event.getTargetEntity().getType().equals(EntityTypes.ARMOR_STAND)
              && !SpongeNope.getInstance().getHostTreeAdapter().lookup(SettingLibrary.ARMOR_STAND_INTERACT,
              player,
              event.getTargetEntity().getLocation()));
  @DynamicSettingListener
  static final SettingListener<SpawnEntityEvent> ARMOR_STAND_PLACE_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.ARMOR_STAND_PLACE,
          SpawnEntityEvent.class,
          spawnEntityCanceler(SettingLibrary.ARMOR_STAND_PLACE, ArmorStand.class));
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent.Break> BLOCK_BREAK_LISTENER =
      new PlayerCauseCancelConditionSettingListener<>(
          SettingLibrary.BLOCK_BREAK,
          ChangeBlockEvent.Break.class,
          (event, player) -> event.getTransactions().stream().anyMatch(transaction ->
              !SpongeNope.getInstance().getHostTreeAdapter().lookup(
                  SettingLibrary.BLOCK_BREAK,
                  player,
                  transaction.getOriginal().getLocation().orElse(transaction.getFinal()
                      .getLocation()
                      .orElseThrow(Extra.noLocation(SettingLibrary.BLOCK_BREAK,
                          ChangeBlockEvent.Break.class,
                          player))))));
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent.Place> BLOCK_PLACE_LISTENER =
      new PlayerCauseCancelConditionSettingListener<>(
          SettingLibrary.BLOCK_PLACE,
          ChangeBlockEvent.Place.class,
          (event, player) -> event.getTransactions().stream().anyMatch(transaction ->
              !SpongeNope.getInstance().getHostTreeAdapter().lookup(
                  SettingLibrary.BLOCK_PLACE,
                  player,
                  transaction.getOriginal().getLocation().orElse(transaction.getFinal()
                      .getLocation()
                      .orElseThrow(Extra.noLocation(SettingLibrary.BLOCK_PLACE,
                          ChangeBlockEvent.Place.class,
                          player))))));
  @DynamicSettingListener
  static final SettingListener<NotifyNeighborBlockEvent> BLOCK_PROPAGATE_LISTENER =
      new SettingListener<>(
          Lists.newArrayList(SettingLibrary.BLOCK_PROPAGATE_ACROSS,
              SettingLibrary.BLOCK_PROPAGATE_WITHIN,
              SettingLibrary.TNT_IGNITION),
          NotifyNeighborBlockEvent.class,
          event -> {
            Player player = event.getCause().first(Player.class).orElse(null);
            Location<World> notifier = event.getCause().first(LocatableBlock.class)
                .orElseThrow(() ->
                    new RuntimeException("A NotifyNeighborBlockEvent needs a block cause"))
                .getLocation();

            // A filter to determine whether a notification should be canceled in some direction
            Predicate<Direction> directionsFilter = (direction -> {
              Location<World> recipient = notifier.add(direction.asBlockOffset());

              if (recipient.getBlock().getType().equals(BlockTypes.TNT)
                  && !SpongeNope.getInstance()
                  .getHostTreeAdapter()
                  .lookup(SettingLibrary.TNT_IGNITION,
                      player,
                      recipient)) {
                return true;
              }

              Settee fromAcross = SpongeNope.getInstance().getHostTreeAdapter()
                  .lookupDictator(SettingLibrary.BLOCK_PROPAGATE_ACROSS, player, notifier);
              boolean fromAcrossData = fromAcross == null
                  ? SettingLibrary.BLOCK_PROPAGATE_ACROSS.getDefaultData()
                  : fromAcross.getData(SettingLibrary.BLOCK_PROPAGATE_ACROSS, player);
              Settee toAcross = SpongeNope.getInstance().getHostTreeAdapter()
                  .lookupDictator(SettingLibrary.BLOCK_PROPAGATE_ACROSS, player, recipient);
              boolean toAcrossData = toAcross == null
                  ? SettingLibrary.BLOCK_PROPAGATE_ACROSS.getDefaultData()
                  : toAcross.getData(SettingLibrary.BLOCK_PROPAGATE_ACROSS, player);
              Settee fromWithin = SpongeNope.getInstance().getHostTreeAdapter()
                  .lookupDictator(SettingLibrary.BLOCK_PROPAGATE_WITHIN, player, notifier);
              boolean fromWithinData = fromWithin == null
                  ? SettingLibrary.BLOCK_PROPAGATE_WITHIN.getDefaultData()
                  : fromWithin.getData(SettingLibrary.BLOCK_PROPAGATE_WITHIN, player);
              Settee toWithin = SpongeNope.getInstance().getHostTreeAdapter()
                  .lookupDictator(SettingLibrary.BLOCK_PROPAGATE_WITHIN, player, recipient);

              return (!(Objects.equals(fromAcross, toAcross)) && (!fromAcrossData || !toAcrossData))
                  || (Objects.equals(fromWithin, toWithin) && (!fromWithinData));
            });
            event.getNeighbors().keySet().removeIf(directionsFilter);
          }
      );
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent> BLOCK_TRAMPLE =
      new PlayerCauseCancelConditionSettingListener<>(
          SettingLibrary.BLOCK_TRAMPLE,
          ChangeBlockEvent.class,
          (event, player) -> event.getTransactions().stream().anyMatch(transaction ->
              !SpongeNope.getInstance().getHostTreeAdapter().lookup(
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
  static final SettingListener<ChangeBlockEvent> CONCRETE_SOLIDIFICATION_LISTENER =
      new SingleSettingListener<>(
          SettingLibrary.CONCRETE_SOLIDIFICATION,
          ChangeBlockEvent.class,
          event -> event.getTransactions().forEach(transaction -> {
            if (transaction.getOriginal().getState().getType().equals(BlockTypes.CONCRETE_POWDER)
                && transaction.getFinal().getState().getType().equals(BlockTypes.CONCRETE)) {
              if (!SpongeNope.getInstance().getHostTreeAdapter()
                  .lookupAnonymous(SettingLibrary.CONCRETE_SOLIDIFICATION,
                      transaction.getFinal().getLocation()
                          .orElseThrow(Extra.noLocation(SettingLibrary.CONCRETE_SOLIDIFICATION,
                              ChangeBlockEvent.class,
                              null)))) {
                transaction.setValid(false);
              }
            }
          }));
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent.Grow> CROP_GROWTH_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.CROP_GROWTH,
          ChangeBlockEvent.Grow.class,
          event -> event.getTransactions().stream().anyMatch(transaction ->
              !SpongeNope.getInstance().getHostTreeAdapter().lookupAnonymous(SettingLibrary.CROP_GROWTH,
                  transaction.getOriginal().getLocation()
                      .orElseThrow(Extra.noLocation(SettingLibrary.CROP_GROWTH,
                          ChangeBlockEvent.Grow.class,
                          null)))
                  ||
                  !SpongeNope.getInstance().getHostTreeAdapter().lookupAnonymous(SettingLibrary.CROP_GROWTH,
                      transaction.getFinal().getLocation()
                          .orElseThrow(Extra.noLocation(SettingLibrary.CROP_GROWTH,
                              ChangeBlockEvent.Grow.class,
                              null)))));
  @DynamicSettingListener
  static final SettingListener<SpawnEntityEvent> DROP_EXP_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.DROP_EXP,
          SpawnEntityEvent.class,
          spawnEntityCanceler(SettingLibrary.DROP_EXP, ExperienceOrb.class));
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
      new PlayerRootCancelConditionSettingListener<>(
          SettingLibrary.ENDERPEARL_TELEPORT,
          MoveEntityEvent.Teleport.class,
          (event, player) -> event.getCause()
              .first(EntityTypes.ENDER_PEARL.getEntityClass())
              .isPresent()
              &&
              (!SpongeNope.getInstance().getHostTreeAdapter().lookup(SettingLibrary.ENDERPEARL_TELEPORT,
                  player,
                  event.getFromTransform().getLocation())
                  ||
                  !SpongeNope.getInstance().getHostTreeAdapter().lookup(SettingLibrary.ENDERPEARL_TELEPORT,
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
              && !SpongeNope.getInstance().getHostTreeAdapter().lookup(SettingLibrary.EVP,
              (Player) event.getTargetEntity(),
              event.getTargetEntity().getLocation()));
  @DynamicSettingListener
  static final SettingListener<ExplosionEvent.Pre> EXPLOSION_DAMAGE_LISTENER =
      new SingleSettingListener<>(
          SettingLibrary.EXPLOSION_DAMAGE_BLACKLIST,
          ExplosionEvent.Pre.class,
          event -> {
            Explosion explosion = event.getExplosion();
            if (!explosion.getSourceExplosive().isPresent()) {
              return;
            }
            Explosive cause = explosion.getSourceExplosive().get();
            if (SpongeNope.getInstance().getHostTreeAdapter()
                .lookupAnonymous(
                    SettingLibrary.EXPLOSION_DAMAGE_BLACKLIST,
                    explosion.getLocation())
                .stream()
                .anyMatch(enu -> enu.getExplosive().isInstance(cause))) {
              // Disable entity damage if explosion occurs in safe zone
              event.setExplosion(Explosion.builder()
                  .from(explosion)
                  .shouldDamageEntities(false).build());
            } else {
              // Disable entity damage if any nearby entities are in safe zone
              for (Entity nearby : explosion.getLocation()
                  .getExtent()
                  .getNearbyEntities(
                      explosion.getLocation().getPosition(),
                      explosion.getRadius())) {
                if (SpongeNope.getInstance().getHostTreeAdapter()
                    .lookupAnonymous(
                        SettingLibrary.EXPLOSION_DAMAGE_BLACKLIST,
                        nearby.getLocation())
                    .stream()
                    .anyMatch(enu -> enu.getExplosive().isInstance(cause))) {
                  event.setExplosion(Explosion.builder()
                      .from(explosion)
                      .shouldDamageEntities(false).build());
                  return;
                }
              }
            }
          }
      );
  @DynamicSettingListener
  static final SettingListener<ExplosionEvent.Pre> EXPLOSION_GRIEF_LISTENER =
      new SingleSettingListener<>(
          SettingLibrary.EXPLOSION_GRIEF_BLACKLIST,
          ExplosionEvent.Pre.class,
          event -> {
            Explosion explosion = event.getExplosion();
            if (!explosion.getSourceExplosive().isPresent()) {
              return;
            }
            Explosive cause = explosion.getSourceExplosive().get();
            if (SpongeNope.getInstance().getHostTreeAdapter()
                .lookupAnonymous(
                    SettingLibrary.EXPLOSION_GRIEF_BLACKLIST,
                    explosion.getLocation())
                .stream()
                .anyMatch(enu -> enu.getExplosive().isInstance(cause))) {
              // Disable entity damage if explosion occurs in safe zone
              event.setExplosion(Explosion.builder()
                  .from(explosion)
                  .shouldBreakBlocks(false).build());
            } else {
              // Disable entity damage if any nearby entities are in safe zone
              int locX = explosion.getLocation().getBlockX();
              int locY = explosion.getLocation().getBlockY();
              int locZ = explosion.getLocation().getBlockZ();
              int radius = (int) Math.ceil(explosion.getRadius());
              for (int x = locX - radius; x <= locX + radius; x++) {
                for (int y = locY - radius; y <= locY + radius; y++) {
                  for (int z = locZ - radius; z <= locZ + radius; z++) {
                    if (SpongeNope.getInstance().getHostTreeAdapter().lookupAnonymous(
                        SettingLibrary.EXPLOSION_GRIEF_BLACKLIST,
                        new Location<>(explosion.getWorld(), x, y, z))
                        .stream()
                        .anyMatch(enu -> enu.getExplosive().isInstance(cause))) {
                      event.setExplosion(Explosion.builder()
                          .from(explosion)
                          .shouldBreakBlocks(false).build());
                      return;
                    }
                  }
                }
              }
            }
          }
      );
  /**
   * This listener ideally would implement the explosion grief setting. The benefit
   * to use this one is that the blocks that break will only be blocks within
   * Zones that don't have grief disabled. That way, an explosion could happen outside
   * of a Zone with grief disabled and blocks outside the Zone would break but not blocks
   * inside the Zone. The problem is that by disabling transactions, some blocks that
   * are dependent on other blocks, like redstone wires, fully break off before the game
   * identifies that it was part of a prior transaction so items get duplicated.
   * So, until a better fix is found, this is disabled (No annotation).
   */
  static final SettingListener<ChangeBlockEvent.Break> EXPLOSION_GRIEF_TRANSACTION_LISTENER =
      new SingleSettingListener<>(
          SettingLibrary.EXPLOSION_GRIEF_BLACKLIST,
          ChangeBlockEvent.Break.class,
          event -> {
            if (!(event.getCause().root() instanceof Explosion)) {
              return;
            }
            Explosion explosion = (Explosion) event.getCause().root();
            if (!explosion.getSourceExplosive().isPresent()) {
              return;
            }
            Explosive cause = explosion.getSourceExplosive().get();
            if (SpongeNope.getInstance().getHostTreeAdapter()
                .lookupAnonymous(SettingLibrary.EXPLOSION_GRIEF_BLACKLIST, explosion.getLocation())
                .stream()
                .anyMatch(enu -> enu.getExplosive().isInstance(cause))) {
              event.setCancelled(true);
              return;
            }
            event.getTransactions().stream().filter(Transaction::isValid).forEach(transaction -> {
              if (
                  SpongeNope.getInstance().getHostTreeAdapter()
                      .lookupAnonymous(SettingLibrary.EXPLOSION_GRIEF_BLACKLIST,
                          transaction.getOriginal()
                              .getLocation()
                              .orElseThrow(Extra.noLocation(
                                  SettingLibrary.EXPLOSION_GRIEF_BLACKLIST,
                                  ChangeBlockEvent.Break.class,
                                  null)))
                      .stream()
                      .anyMatch(enu -> enu.getExplosive().isInstance(cause))
                      ||
                      SpongeNope.getInstance().getHostTreeAdapter()
                          .lookupAnonymous(SettingLibrary.EXPLOSION_GRIEF_BLACKLIST,
                              transaction.getFinal()
                                  .getLocation()
                                  .orElseThrow(Extra.noLocation(
                                      SettingLibrary.EXPLOSION_GRIEF_BLACKLIST,
                                      ChangeBlockEvent.Break.class,
                                      null)))
                          .stream()
                          .anyMatch(enu -> enu.getExplosive().isInstance(cause))) {
                transaction.setValid(false);
              }
            });
          });
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
                      ? !SpongeNope.getInstance()
                      .getHostTreeAdapter()
                      .lookup(SettingLibrary.FALL_DAMAGE,
                          (User) event.getTargetEntity(),
                          event.getTargetEntity().getLocation())
                      : !SpongeNope.getInstance()
                      .getHostTreeAdapter()
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
              .anyMatch(trans -> !SpongeNope.getInstance()
                  .getHostTreeAdapter()
                  .lookupAnonymous(SettingLibrary.FIRE_EFFECT, trans.getFinal()
                      .getLocation()
                      .orElseThrow(Extra.noLocation(SettingLibrary.FIRE_EFFECT,
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
                  && !SpongeNope.getInstance()
                  .getHostTreeAdapter()
                  .lookup(SettingLibrary.FIRE_IGNITION,
                      (Player) event.getSource(),
                      trans.getFinal()
                          .getLocation()
                          .orElseThrow(Extra.noLocation(SettingLibrary.FIRE_IGNITION,
                              ChangeBlockEvent.class,
                              (Player) event.getSource())))));
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent> FIRE_NATURAL_IGNITION_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.FIRE_NATURAL_IGNITION,
          ChangeBlockEvent.class,
          event -> !(
              event.getSource() instanceof Player
                  || event.getContext().get(EventContextKeys.PLUGIN).isPresent())
              && event.getTransactions()
              .stream()
              .anyMatch(trans -> trans.getFinal().getState().getType().equals(BlockTypes.FIRE)
                  && !SpongeNope.getInstance()
                  .getHostTreeAdapter()
                  .lookupAnonymous(SettingLibrary.FIRE_NATURAL_IGNITION, trans.getFinal()
                      .getLocation()
                      .orElseThrow(Extra.noLocation(SettingLibrary.FIRE_NATURAL_IGNITION,
                          ChangeBlockEvent.class,
                          null)))));
  @DynamicSettingListener
  static final SettingListener<InteractBlockEvent.Secondary> FLOWER_POT_INTERACT_LISTENER =
      new PlayerRootCancelConditionSettingListener<>(
          SettingLibrary.FLOWER_POT_INTERACT,
          InteractBlockEvent.Secondary.class,
          (event, player) -> event.getTargetBlock()
              .getState()
              .getType()
              .equals(BlockTypes.FLOWER_POT)
              && !SpongeNope.getInstance().getHostTreeAdapter()
              .lookup(SettingLibrary.FLOWER_POT_INTERACT,
                  player,
                  event.getTargetBlock().getLocation()
                      .orElseThrow(Extra.noLocation(SettingLibrary.INTERACT,
                          InteractBlockEvent.Secondary.class,
                          player))));
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
              .anyMatch(entity -> !SpongeNope.getInstance()
                  .getHostTreeAdapter()
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
  static final SettingListener<FishingEvent.HookEntity.HookEntity> HOOK_ENTITY_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.HOOK_ENTITY,
          FishingEvent.HookEntity.HookEntity.class,
          event -> {
            Optional<User> owner = event.getContext().get(EventContextKeys.OWNER);
            return (
                owner.isPresent()
                    && owner.get().getPlayer().isPresent()
                    && !SpongeNope.getInstance().getHostTreeAdapter().lookup(SettingLibrary.HOOK_ENTITY,
                    owner.get(),
                    owner.get().getPlayer().get().getLocation()))
                || !SpongeNope.getInstance().getHostTreeAdapter().lookup(SettingLibrary.HOOK_ENTITY,
                owner.orElse(null),
                event.getTargetEntity().getLocation());
          });
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
      new PlayerRootCancelConditionSettingListener<>(
          SettingLibrary.INTERACT,
          InteractBlockEvent.Secondary.class,
          (event, player) -> !SpongeNope.getInstance()
              .getHostTreeAdapter()
              .lookup(SettingLibrary.INTERACT,
                  player,
                  event.getTargetBlock()
                      .getLocation()
                      .orElseThrow(Extra.noLocation(SettingLibrary.INTERACT,
                          InteractBlockEvent.Secondary.class,
                          player)))
              || !SpongeNope.getInstance()
              .getHostTreeAdapter()
              .lookup(SettingLibrary.INTERACT,
                  player,
                  player.getLocation()));
  @DynamicSettingListener
  static final SettingListener<DamageEntityEvent> INVINCIBLE_ANIMALS_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.INVINCIBLE_ANIMALS,
          DamageEntityEvent.class,
          event -> event.getTargetEntity() instanceof Animal
              && SpongeNope.getInstance()
              .getHostTreeAdapter()
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
              && SpongeNope.getInstance()
              .getHostTreeAdapter()
              .lookupAnonymous(
                  SettingLibrary.INVINCIBLE_MOBS,
                  event.getTargetEntity().getLocation()));
  @DynamicSettingListener
  static final SettingListener<DamageEntityEvent> INVINCIBLE_PLAYERS_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.INVINCIBLE_PLAYERS,
          DamageEntityEvent.class,
          event -> event.getTargetEntity() instanceof Player
              && SpongeNope.getInstance()
              .getHostTreeAdapter()
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
            boolean cancel = !SpongeNope.getInstance().getHostTreeAdapter().lookup(SettingLibrary.ITEM_DROP,
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
      new PlayerRootCancelConditionSettingListener<>(
          SettingLibrary.ITEM_DROP,
          DropItemEvent.Dispense.class,
          (event, player) -> !SpongeNope.getInstance()
              .getHostTreeAdapter()
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
  static final SettingListener<InteractEntityEvent.Secondary> ITEM_FRAME_INTERACT_LISTENER =
      new PlayerRootCancelConditionSettingListener<>(
          SettingLibrary.ITEM_FRAME_INTERACT,
          InteractEntityEvent.Secondary.class,
          (event, player) -> event.getTargetEntity().getType().equals(EntityTypes.ITEM_FRAME)
              && !SpongeNope.getInstance()
              .getHostTreeAdapter()
              .lookup(SettingLibrary.ITEM_FRAME_INTERACT,
                  player,
                  event.getTargetEntity().getLocation()));
  @DynamicSettingListener
  static final SettingListener<SpawnEntityEvent> ITEM_FRAME_PLACE_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.ITEM_FRAME_PLACE,
          SpawnEntityEvent.class,
          spawnEntityCanceler(SettingLibrary.ITEM_FRAME_PLACE, ItemFrame.class));
  @DynamicSettingListener
  static final SettingListener<ChangeInventoryEvent.Pickup.Pre> ITEM_PICKUP_LISTENER =
      new PlayerRootCancelConditionSettingListener<>(
          SettingLibrary.ITEM_PICKUP,
          ChangeInventoryEvent.Pickup.Pre.class,
          (event, player) -> !SpongeNope.getInstance().getHostTreeAdapter()
              .lookup(SettingLibrary.ITEM_PICKUP,
                  player,
                  event.getTargetEntity().getLocation()));
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent.Break> LAVA_FLOW_GRIEF_LISTENER =
      new SingleSettingListener<>(
          SettingLibrary.LAVA_GRIEF,
          ChangeBlockEvent.Break.class,
          event -> event.getCause().first(LocatableBlock.class).ifPresent(block -> {
            if (block.getBlockState().getType().equals(BlockTypes.LAVA)
                || block.getBlockState().getType().equals(BlockTypes.FLOWING_LAVA)) {
              event.getTransactions().forEach(transaction -> {
                if (!transaction.isValid()) {
                  return;
                }
                if (Groups.LIQUID_GRIEFABLE.contains(transaction.getOriginal()
                    .getState()
                    .getType())) {
                  return;
                }
                if (!SpongeNope.getInstance().getHostTreeAdapter().lookupAnonymous(SettingLibrary.LAVA_GRIEF,
                    transaction.getFinal().getLocation().orElseThrow(Extra.noLocation(
                        SettingLibrary.LAVA_GRIEF,
                        ChangeBlockEvent.Break.class,
                        null)))) {
                  transaction.setValid(false);
                }
              });
            }
          })
      );
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent> LAVA_FLOW_LISTENER =
      new SingleSettingListener<>(
          SettingLibrary.LAVA_FLOW,
          ChangeBlockEvent.class,
          event -> {
            for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
              if (transaction.isValid()) {
                if (transaction.getFinal()
                    .getState()
                    .getType()
                    .equals(BlockTypes.FLOWING_LAVA)) {
                  if (!SpongeNope.getInstance()
                      .getHostTreeAdapter()
                      .lookupAnonymous(SettingLibrary.LAVA_FLOW,
                          transaction.getFinal()
                              .getLocation()
                              .orElseThrow(Extra.noLocation(SettingLibrary.LAVA_FLOW,
                                  ChangeBlockEvent.class,
                                  null)))) {
                    transaction.setValid(false);
                  }
                }
              }
            }
          });
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
  static final SettingListener<LeashEntityEvent> LEASH_LISTENER =
      new PlayerRootCancelConditionSettingListener<>(
          SettingLibrary.LEASH,
          LeashEntityEvent.class,
          (event, player) -> !SpongeNope.getInstance().getHostTreeAdapter().lookup(SettingLibrary.LEASH,
              player,
              event.getTargetEntity().getLocation()));
  @DynamicSettingListener
  static final SettingListener<SpawnEntityEvent> LIGHTNING_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.LIGHTNING,
          SpawnEntityEvent.class,
          event -> event.getEntities().stream().anyMatch(spawned ->
              spawned instanceof Lightning
                  && !SpongeNope.getInstance()
                  .getHostTreeAdapter()
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
              && !SpongeNope.getInstance()
              .getHostTreeAdapter()
              .lookup(SettingLibrary.NATURAL_HEALTH_REGEN,
                  event.getTargetHolder() instanceof Player
                      ? (Player) event.getTargetHolder()
                      : null,
                  ((Player) event.getTargetHolder()).getLocation()));
  @DynamicSettingListener
  static final SettingListener<AttackEntityEvent> PAINTING_ATTACK_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.PAINTING_DESTROY,
          AttackEntityEvent.class,
          entityVersusEntityCanceller(SettingLibrary.PAINTING_DESTROY,
              Player.class,
              Painting.class));
  @DynamicSettingListener
  static final SettingListener<SpawnEntityEvent> PAINTING_PLACE_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.PAINTING_PLACE,
          SpawnEntityEvent.class,
          spawnEntityCanceler(SettingLibrary.PAINTING_PLACE, Painting.class));
  @DynamicSettingListener
  static final SettingListener<MoveEntityEvent> PLAYER_COLLISION_LISTENER =
      new SingleSettingListener<>(
          SettingLibrary.PLAYER_COLLISION,
          MoveEntityEvent.class,
          event -> {
            if (!(event.getTargetEntity() instanceof Player)) {
              return;
            }
            if (SpongeNope.getInstance().getHostTreeAdapter().lookup(SettingLibrary.PLAYER_COLLISION,
                (Player) event.getTargetEntity(),
                event.getTargetEntity().getLocation())) {
              SpongeNope.getInstance()
                  .getCollisionHandler()
                  .enableCollision((Player) event.getTargetEntity());
            } else {
              SpongeNope.getInstance()
                  .getCollisionHandler()
                  .disableCollision((Player) event.getTargetEntity());
            }
          });
  @DynamicSettingListener
  static final SettingListener<IgniteEntityEvent> PLAYER_ROOT_IGNITE_ENTITY_EVENT_LISTENER =
      new SettingListener<>(
          Lists.newArrayList(SettingLibrary.PVA, SettingLibrary.PVH, SettingLibrary.PVP),
          IgniteEntityEvent.class,
          event -> {
            Optional<Player> player = event.getCause()
                .first(EntityTippedArrow.class)
                .map(arrow -> arrow.shootingEntity)
                .flatMap(entity -> entity instanceof Player
                    ? Optional.of((Player) entity)
                    : Optional.empty());
            if (player.isPresent() && ((
                (event.getTargetEntity() instanceof Player)
                    && (!SpongeNope.getInstance().getHostTreeAdapter().lookup(SettingLibrary.PVP,
                    player.get(),
                    player.get().getLocation())
                    || !SpongeNope.getInstance().getHostTreeAdapter().lookup(SettingLibrary.PVP,
                    player.get(),
                    event.getTargetEntity().getLocation())))
                || (
                (event.getTargetEntity() instanceof Animal
                    || event.getTargetEntity() instanceof Squid)
                    &&
                    (!SpongeNope.getInstance().getHostTreeAdapter().lookup(SettingLibrary.PVA,
                        player.get(),
                        player.get().getLocation())
                        ||
                        !SpongeNope.getInstance().getHostTreeAdapter().lookup(SettingLibrary.PVA,
                            player.get(),
                            event.getTargetEntity().getLocation())))
                ||
                ((event.getTargetEntity() instanceof Hostile)
                    && (!SpongeNope.getInstance().getHostTreeAdapter().lookup(SettingLibrary.PVH,
                    player.get(),
                    player.get().getLocation())
                    ||
                    !SpongeNope.getInstance().getHostTreeAdapter().lookup(SettingLibrary.PVH,
                        player.get(),
                        event.getTargetEntity().getLocation()))))) {
              event.setCancelled(true);
            }
          }
      );
  @DynamicSettingListener
  static final SettingListener<DamageEntityEvent> PVA_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.PVA,
          DamageEntityEvent.class,
          event -> entityVersusEntityCanceller(SettingLibrary.PVA,
              Player.class,
              Animal.class).test(event)
              ||
              entityVersusEntityCanceller(SettingLibrary.PVA,
                  Player.class,
                  Squid.class).test(event));
  @DynamicSettingListener
  static final SettingListener<DamageEntityEvent> PVH_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.PVH,
          DamageEntityEvent.class,
          entityVersusEntityCanceller(SettingLibrary.PVH,
              Player.class,
              Hostile.class));
  @DynamicSettingListener
  static final SettingListener<DamageEntityEvent> PVP_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.PVP,
          DamageEntityEvent.class,
          entityVersusEntityCanceller(SettingLibrary.PVP, Player.class, Player.class));
  @DynamicSettingListener
  static final SettingListener<RideEntityEvent.Mount> RIDE_MOUNT_LISTENER =
      new PlayerRootCancelConditionSettingListener<>(
          SettingLibrary.RIDE,
          RideEntityEvent.Mount.class,
          (event, player) -> !SpongeNope.getInstance()
              .getHostTreeAdapter()
              .lookup(SettingLibrary.RIDE,
                  player,
                  player.getLocation())
              || !SpongeNope.getInstance()
              .getHostTreeAdapter()
              .lookupAnonymous(SettingLibrary.RIDE,
                  event.getTargetEntity().getLocation()));
  @DynamicSettingListener
  static final SettingListener<SleepingEvent.Pre> SLEEP =
      new PlayerRootCancelConditionSettingListener<>(
          SettingLibrary.SLEEP,
          SleepingEvent.Pre.class,
          (event, player) -> (!SpongeNope.getInstance().getHostTreeAdapter()
              .lookup(SettingLibrary.SLEEP,
                  player,
                  player.getLocation())
              || !SpongeNope.getInstance().getHostTreeAdapter()
              .lookupAnonymous(SettingLibrary.SLEEP,
                  event.getBed()
                      .getLocation()
                      .orElseThrow(Extra.noLocation(SettingLibrary.SLEEP,
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
                  && !SpongeNope.getInstance()
                  .getHostTreeAdapter()
                  .lookupAnonymous(SettingLibrary.SNOWMAN_TRAILS,
                      trans.getFinal()
                          .getLocation()
                          .orElseThrow(Extra.noLocation(SettingLibrary.SNOWMAN_TRAILS,
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
          spawnEntityCanceler(SettingLibrary.SPAWN_ANIMAL, Animal.class));
  @DynamicSettingListener
  static final SettingListener<SpawnEntityEvent> SPAWN_HOSTILE_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.SPAWN_HOSTILE,
          SpawnEntityEvent.class,
          spawnEntityCanceler(SettingLibrary.SPAWN_HOSTILE, Hostile.class));
  @DynamicSettingListener
  static final SettingListener<SpawnEntityEvent> SPAWN_MOB_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.SPAWN_MOB,
          SpawnEntityEvent.class,
          spawnEntityCanceler(SettingLibrary.SPAWN_MOB, Agent.class));
  @DynamicSettingListener
  static final SettingListener<InteractEntityEvent.Secondary> TNT_CART_IGNITION_LISTENER =
      new PlayerRootCancelConditionSettingListener<>(
          SettingLibrary.TNT_IGNITION,
          InteractEntityEvent.Secondary.class,
          (event, player) -> event.getTargetEntity()
              .getType()
              .equals(EntityTypes.TNT_MINECART)
              && !SpongeNope.getInstance()
              .getHostTreeAdapter()
              .lookup(SettingLibrary.TNT_IGNITION,
                  player,
                  event.getTargetEntity().getLocation()));
  @DynamicSettingListener
  static final SettingListener<SpawnEntityEvent> TNT_CART_PLACEMENT_LISTENER =
      new PlayerRootCancelConditionSettingListener<>(
          SettingLibrary.TNT_PLACEMENT,
          SpawnEntityEvent.class,
          (event, player) -> event.getEntities()
              .stream()
              .anyMatch(entity ->
                  entity.getType().equals(EntityTypes.TNT_MINECART)
                      && !SpongeNope.getInstance()
                      .getHostTreeAdapter()
                      .lookup(SettingLibrary.TNT_PLACEMENT,
                          player,
                          entity.getLocation())));
  @DynamicSettingListener
  static final SettingListener<InteractBlockEvent.Secondary> TNT_IGNITION_LISTENER =
      new PlayerRootCancelConditionSettingListener<>(
          SettingLibrary.TNT_IGNITION,
          InteractBlockEvent.Secondary.class,
          (event, player) -> event.getTargetBlock()
              .getState()
              .getType()
              .equals(BlockTypes.TNT)
              && !SpongeNope.getInstance()
              .getHostTreeAdapter()
              .lookup(SettingLibrary.TNT_IGNITION,
                  player,
                  event.getTargetBlock()
                      .getLocation()
                      .orElseThrow(Extra.noLocation(SettingLibrary.TNT_IGNITION,
                          InteractBlockEvent.Secondary.class,
                          player))));
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent.Place> TNT_PLACEMENT_LISTENER =
      new PlayerRootCancelConditionSettingListener<>(
          SettingLibrary.TNT_PLACEMENT,
          ChangeBlockEvent.Place.class,
          (event, player) -> event.getTransactions()
              .stream()
              .anyMatch(trans -> trans.getFinal()
                  .getState()
                  .getType()
                  .equals(BlockTypes.TNT)
                  && !SpongeNope.getInstance()
                  .getHostTreeAdapter()
                  .lookup(SettingLibrary.TNT_PLACEMENT,
                      player,
                      trans.getFinal()
                          .getLocation()
                          .orElseThrow(Extra.noLocation(SettingLibrary.TNT_PLACEMENT,
                              ChangeBlockEvent.Place.class,
                              player)))));
  @DynamicSettingListener
  static final SettingListener<ConstructEntityEvent.Post> TNT_SPAWN_LISTENER =
      new SingleSettingListener<>(
          SettingLibrary.TNT_IGNITION,
          ConstructEntityEvent.Post.class,
          (event) -> {
            if (event.getTargetEntity().getType().equals(EntityTypes.PRIMED_TNT)) {
              if (!SpongeNope.getInstance().getHostTreeAdapter().lookup(SettingLibrary.TNT_IGNITION,
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
  static final SettingListener<SpawnEntityEvent> UNSPAWNABLE_MOBS_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.UNSPAWNABLE_MOBS,
          SpawnEntityEvent.class,
          event -> event.getEntities().stream().anyMatch(entity ->
              SpongeNope.getInstance()
                  .getHostTreeAdapter()
                  .lookupAnonymous(SettingLibrary.UNSPAWNABLE_MOBS, entity.getLocation())
                  .contains(entity.getType())));
  @DynamicSettingListener
  static final SettingListener<UseItemStackEvent.Finish> USE_CHORUS_FRUIT_LISTENER =
      new SingleSettingListener<>(
          SettingLibrary.CHORUS_FRUIT_TELEPORT,
          UseItemStackEvent.Finish.class,
          event -> {
            Optional<Player> player = event.getCause().first(Player.class);
            if (!player.isPresent()) {
              return;
            }
            if (event.getItemStackInUse()
                .getType()
                .equals(ItemTypes.CHORUS_FRUIT)) {
              SpongeNope.getInstance()
                  .getPlayerMovementHandler()
                  .cancelNextTeleportIf(player.get().getUniqueId(),
                      teleportEvent ->
                          !SpongeNope.getInstance()
                              .getHostTreeAdapter()
                              .lookup(SettingLibrary.CHORUS_FRUIT_TELEPORT,
                                  player.get(),
                                  teleportEvent.getFromTransform().getLocation())
                              ||
                              !SpongeNope.getInstance()
                                  .getHostTreeAdapter()
                                  .lookup(SettingLibrary.CHORUS_FRUIT_TELEPORT,
                                      player.get(),
                                      teleportEvent.getToTransform().getLocation()),
                      10000);
            }
          });
  @DynamicSettingListener
  static final SettingListener<InteractEntityEvent.Secondary> USE_NAME_TAG_LISTENER =
      new PlayerRootCancelConditionSettingListener<>(
          SettingLibrary.USE_NAME_TAG,
          InteractEntityEvent.Secondary.class,
          (event, player) -> player.getItemInHand(event.getHandType())
              .filter(stack -> stack.getType().equals(ItemTypes.NAME_TAG))
              .isPresent()
              &&
              (!SpongeNope.getInstance()
                  .getHostTreeAdapter()
                  .lookup(SettingLibrary.USE_NAME_TAG,
                      player,
                      player.getLocation())
                  || !SpongeNope.getInstance()
                  .getHostTreeAdapter()
                  .lookup(SettingLibrary.USE_NAME_TAG,
                      player,
                      event.getTargetEntity().getLocation())));
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent> VINE_GROWTH_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.VINE_GROWTH,
          ChangeBlockEvent.class,
          simpleChangeBlockCanceler(SettingLibrary.VINE_GROWTH,
              BlockTypes.AIR,
              BlockTypes.VINE));
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent.Break> WATER_FLOW_GRIEF_LISTENER =
      new SingleSettingListener<>(
          SettingLibrary.WATER_GRIEF,
          ChangeBlockEvent.Break.class,
          event -> event.getCause().first(LocatableBlock.class).ifPresent(block -> {
            if (block.getBlockState().getType().equals(BlockTypes.WATER)
                || block.getBlockState().getType().equals(BlockTypes.FLOWING_WATER)) {
              event.getTransactions().forEach(transaction -> {
                if (!transaction.isValid()) {
                  return;
                }
                if (Groups.LIQUID_GRIEFABLE.contains(transaction.getOriginal()
                    .getState()
                    .getType())) {
                  return;
                }
                if (!SpongeNope.getInstance().getHostTreeAdapter().lookupAnonymous(SettingLibrary.WATER_GRIEF,
                    transaction.getFinal().getLocation().orElseThrow(Extra.noLocation(
                        SettingLibrary.WATER_GRIEF,
                        ChangeBlockEvent.Break.class,
                        null)))) {
                  transaction.setValid(false);
                }
              });
            }
          })
      );
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent> WATER_FLOW_LISTENER =
      new SingleSettingListener<>(
          SettingLibrary.WATER_FLOW,
          ChangeBlockEvent.class,
          event -> {
            if (event.getSource() instanceof Player) {
              return;  // Player caused - player likely placed this (not flow)
            }
            for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
              if (transaction.isValid()) {
                if (transaction.getFinal().getState()
                    .getType()
                    .equals(BlockTypes.FLOWING_WATER)) {
                  if (!SpongeNope.getInstance().getHostTreeAdapter()
                      .lookupAnonymous(SettingLibrary.WATER_FLOW,
                          transaction.getFinal().getLocation()
                              .orElseThrow(Extra.noLocation(SettingLibrary.WATER_FLOW,
                                  ChangeBlockEvent.class,
                                  null)))) {
                    transaction.setValid(false);
                  }
                }
              }
            }
          });
  @DynamicSettingListener
  static final SettingListener<ChangeBlockEvent.Break> ZOMBIE_GRIEF_BLOCK_LISTENER =
      new EntityBreakConditionSettingListener(
          SettingLibrary.ZOMBIE_GRIEF,
          EntityTypes.ZOMBIE);
  private static final List<BlockType> CHEST_TYPES = Lists.newArrayList(
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
  @DynamicSettingListener
  static final SettingListener<InteractBlockEvent.Secondary> CHEST_ACCESS_LISTENER =
      new PlayerRootCancelConditionSettingListener<>(
          SettingLibrary.CHEST_ACCESS,
          InteractBlockEvent.Secondary.class,
          (event, player) -> CHEST_TYPES.contains(event.getTargetBlock().getState().getType())
              &&
              !SpongeNope.getInstance().getHostTreeAdapter().lookup(SettingLibrary.CHEST_ACCESS,
                  player,
                  event.getTargetBlock().getLocation()
                      .orElseThrow(Extra.noLocation(SettingLibrary.CHEST_ACCESS,
                          InteractBlockEvent.Secondary.class,
                          player))));
  private static final Set<EntityType> VEHICLES = Sets.newHashSet(EntityTypes.BOAT,
      EntityTypes.CHESTED_MINECART,
      EntityTypes.COMMANDBLOCK_MINECART,
      EntityTypes.FURNACE_MINECART,
      EntityTypes.HOPPER_MINECART,
      EntityTypes.MOB_SPAWNER_MINECART,
      EntityTypes.RIDEABLE_MINECART,
      EntityTypes.TNT_MINECART);
  @DynamicSettingListener
  static final SettingListener<InteractEntityEvent.Primary> VEHICLE_DESTROY_LISTENER =
      new PlayerRootCancelConditionSettingListener<>(
          SettingLibrary.VEHICLE_DESTROY,
          InteractEntityEvent.Primary.class,
          (event, player) -> VEHICLES.contains(event.getTargetEntity().getType())
              &&
              (!SpongeNope.getInstance().getHostTreeAdapter().lookup(SettingLibrary.VEHICLE_DESTROY,
                  player,
                  player.getLocation())
                  || !SpongeNope.getInstance().getHostTreeAdapter().lookup(SettingLibrary.VEHICLE_DESTROY,
                  player,
                  event.getTargetEntity().getLocation())));
  @DynamicSettingListener
  static final SettingListener<SpawnEntityEvent> VEHICLE_PLACE_LISTENER =
      new PlayerRootCancelConditionSettingListener<>(
          SettingLibrary.VEHICLE_PLACE,
          SpawnEntityEvent.class,
          (event, player) -> event.getEntities().stream().anyMatch(spawned ->
              VEHICLES.contains(spawned.getType())
                  &&
                  (!SpongeNope.getInstance().getHostTreeAdapter().lookup(SettingLibrary.VEHICLE_PLACE,
                      player,
                      player.getLocation())
                      || !SpongeNope.getInstance().getHostTreeAdapter().lookup(SettingLibrary.VEHICLE_PLACE,
                      player,
                      spawned.getLocation()))));

  private DynamicSettingListeners() {
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

  static void printEvent(Event event) {
    SpongeNope.getInstance().getLogger().info("Event... (" + event.getClass().getSimpleName() + ")");
    SpongeNope.getInstance().getLogger().info("Source: " + event.getSource());
    event.getCause().forEach(o -> SpongeNope.getInstance().getLogger().info("Cause: " + o.toString()));
    SpongeNope.getInstance().getLogger().info("Context: " + event.getContext().asMap().entrySet()
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
      return !SpongeNope.getInstance()
          .getHostTreeAdapter()
          .lookup(key,
              (source instanceof Player)
                  ? (Player) source
                  :
                  (sink instanceof Player)
                      ? (Player) sink
                      : null,
              source.getLocation())
          || !SpongeNope.getInstance()
          .getHostTreeAdapter()
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

  private static <T extends SpawnEntityEvent> Predicate<T> spawnEntityCanceler(
      SettingKey<Boolean> settingKey,
      Class<? extends Entity> deniedType) {
    return event -> event.getEntities().stream().anyMatch(entity ->
        (deniedType.isInstance(entity))
            && !SpongeNope.getInstance()
            .getHostTreeAdapter()
            .lookup(settingKey,
                event.getCause().first(Player.class).orElse(null),
                entity.getLocation()));
  }

  private static Predicate<ChangeBlockEvent> simpleChangeBlockCanceler(SettingKey<Boolean> key,
                                                                       BlockType first,
                                                                       BlockType last) {
    return event -> event.getTransactions().stream()
        .filter(trans ->
            trans.getOriginal().getState().getType().equals(first))
        .filter(trans ->
            trans.getFinal().getState().getType().equals(last))
        .anyMatch(trans -> !SpongeNope.getInstance()
            .getHostTreeAdapter()
            .lookupAnonymous(key,
                trans.getFinal().getLocation().orElseThrow(Extra.noLocation(key,
                    ChangeBlockEvent.class,
                    null))));
  }

  /**
   * An annotation to mark a method as a dynamic {@link SettingListener}.
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface DynamicSettingListener {
    // none
  }

}
