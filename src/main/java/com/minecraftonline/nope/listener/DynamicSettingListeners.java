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

package com.minecraftonline.nope.listener;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.setting.SettingKey;
import com.minecraftonline.nope.setting.SettingLibrary;
import com.minecraftonline.nope.setting.SettingValue;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Hostile;
import org.spongepowered.api.entity.living.animal.Animal;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.world.LocatableBlock;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.explosion.Explosion;

import javax.annotation.Nullable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A container class for all dynamically registered listeners.
 */
@SuppressWarnings("unused")
public class DynamicSettingListeners {

  @DynamicSettingListener
  static SettingListener<ChangeBlockEvent> BUILD_PERMISSIONS_LISTENER =
      new PlayerCancelConditionSettingListener<>(
          SettingLibrary.BUILD_PERMISSIONS,
          ChangeBlockEvent.class,
          (event, player) -> event.getTransactions().stream().anyMatch(transaction ->
              !Nope.getInstance().getHostTree().lookup(
                  SettingLibrary.BUILD_PERMISSIONS,
                  player,
                  transaction.getOriginal().getLocation().orElse(transaction.getFinal()
                      .getLocation()
                      .orElseThrow(noLocation(SettingLibrary.BUILD_PERMISSIONS,
                          ChangeBlockEvent.class,
                          player))))));
  @DynamicSettingListener
  static SettingListener<ChangeBlockEvent.Break> BLOCK_BREAK_LISTENER =
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
  static SettingListener<ChangeBlockEvent.Place> BLOCK_PLACE_LISTENER =
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
  static SettingListener<ChangeBlockEvent> BLOCK_TRAMPLE =
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
  static SettingListener<InteractBlockEvent.Secondary> CHEST_ACCESS_LISTENER =
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
  static SettingListener<DamageEntityEvent> CREEPER_EXPLOSION_DAMAGE_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.CREEPER_EXPLOSION_DAMAGE,
          DamageEntityEvent.class,
          event -> event.getCause()
              .first(EntityDamageSource.class)
              .map(damage -> damage.getSource().getType().equals(EntityTypes.CREEPER))
              .orElse(false)
              &&
              (!(event.getTargetEntity() instanceof Subject)
                  ||
                  !Nope.getInstance().getHostTree().lookup(
                      SettingLibrary.CREEPER_EXPLOSION_DAMAGE,
                      (Subject) event.getTargetEntity(),
                      event.getTargetEntity().getLocation())));
  @DynamicSettingListener
  static SettingListener<ChangeBlockEvent.Break> CREEPER_EXPLOSION_GRIEF_BLOCK_LISTENER =
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
  static SettingListener<ChangeBlockEvent.Grow> CROP_GROWTH_LISTENER =
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
  static SettingListener<DamageEntityEvent> INVINCIBLE_ANIMALS_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.INVINCIBLE_ANIMALS,
          DamageEntityEvent.class,
          event -> event.getTargetEntity() instanceof Animal
              && !Nope.getInstance()
              .getHostTree()
              .lookupAnonymous(
                  SettingLibrary.INVINCIBLE_ANIMALS,
                  event.getTargetEntity().getLocation()));
  @DynamicSettingListener
  static SettingListener<SpawnEntityEvent> DENY_SPAWN_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.DENY_SPAWN,
          SpawnEntityEvent.class,
          event -> event.getEntities().stream().anyMatch(entity ->
              Nope.getInstance()
                  .getHostTree()
                  .lookupAnonymous(SettingLibrary.DENY_SPAWN, entity.getLocation())
                  .contains(entity.getType())));
  @DynamicSettingListener
  static SettingListener<ChangeBlockEvent.Break> ENDERDRAGON_GRIEF_BLOCK_LISTENER =
      new EntityBreakConditionSettingListener(
          SettingLibrary.ENDERDRAGON_GRIEF,
          EntityTypes.ENDER_DRAGON);

  // TODO add other listeners for other kinds of creeper grief
  @DynamicSettingListener
  static SettingListener<ChangeBlockEvent.Break> ENDERMAN_GRIEF_BLOCK_LISTENER =
      new EntityBreakConditionSettingListener(
          SettingLibrary.ENDERMAN_GRIEF,
          EntityTypes.ENDERMAN);
  @DynamicSettingListener
  static SettingListener<MoveEntityEvent.Teleport> ENDERPEARL_TELEPORT_LISTENER =
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
  static SettingListener<InteractEntityEvent.Primary> ARMOR_STAND_DESTROY_LISTENER =
      new PlayerCancelConditionSettingListener<>(
          SettingLibrary.ARMOR_STAND_DESTROY,
          InteractEntityEvent.Primary.class,
          (event, player) -> event.getTargetEntity().getType().equals(EntityTypes.ARMOR_STAND)
              &&
              !Nope.getInstance().getHostTree().lookup(SettingLibrary.ARMOR_STAND_DESTROY,
                  player,
                  event.getTargetEntity().getLocation()));
  @DynamicSettingListener
  static SettingListener<InteractEntityEvent.Primary> ITEM_FRAME_DESTROY_LISTENER =
      new PlayerCancelConditionSettingListener<>(
          SettingLibrary.ITEM_FRAME_DESTROY,
          InteractEntityEvent.Primary.class,
          (event, player) -> event.getTargetEntity().getType().equals(EntityTypes.ITEM_FRAME)
              &&
              !Nope.getInstance().getHostTree().lookup(SettingLibrary.ITEM_FRAME_DESTROY,
                  player,
                  event.getTargetEntity().getLocation()));
  @DynamicSettingListener
  static SettingListener<InteractEntityEvent.Primary> PAINTING_DESTROY_LISTENER =
      new PlayerCancelConditionSettingListener<>(
          SettingLibrary.PAINTING_DESTROY,
          InteractEntityEvent.Primary.class,
          (event, player) -> event.getTargetEntity().getType().equals(EntityTypes.PAINTING)
              &&
              !Nope.getInstance().getHostTree().lookup(SettingLibrary.PAINTING_DESTROY,
                  player,
                  event.getTargetEntity().getLocation()));
  @DynamicSettingListener
  static SettingListener<MoveEntityEvent> ENTRY_LISTENER =
      new SettingListener<>(
          Lists.newArrayList(SettingLibrary.ENTRY,
              SettingLibrary.GREETING,
              SettingLibrary.GREETING_TITLE,
              SettingLibrary.GREETING_SUBTITLE,
              SettingLibrary.ENTRY_DENY_MESSAGE),
          MoveEntityEvent.class,
          (event) ->
              thresholdHandler(SettingLibrary.ENTRY,
                  event.getToTransform().getLocation(),
                  event.getFromTransform().getLocation(),
                  SettingLibrary.GREETING,
                  SettingLibrary.GREETING_TITLE,
                  SettingLibrary.GREETING_SUBTITLE,
                  SettingLibrary.ENTRY_DENY_MESSAGE)
                  .accept(event));
  @DynamicSettingListener
  static SettingListener<MoveEntityEvent> EXIT_LISTENER =
      new SettingListener<>(
          Lists.newArrayList(SettingLibrary.EXIT,
              SettingLibrary.FAREWELL,
              SettingLibrary.FAREWELL_TITLE,
              SettingLibrary.FAREWELL_SUBTITLE,
              SettingLibrary.EXIT_DENY_MESSAGE),
          MoveEntityEvent.class,
          (event) ->
              thresholdHandler(SettingLibrary.EXIT,
                  event.getFromTransform().getLocation(),
                  event.getToTransform().getLocation(),
                  SettingLibrary.FAREWELL,
                  SettingLibrary.FAREWELL_TITLE,
                  SettingLibrary.FAREWELL_SUBTITLE,
                  SettingLibrary.EXIT_DENY_MESSAGE)
                  .accept(event));
  @DynamicSettingListener
  static SettingListener<DamageEntityEvent> EVP_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.EVP,
          DamageEntityEvent.class,
          event -> event.getCause()
              .first(DamageSource.class)
              .filter(damageSource -> Sets.newHashSet(
                  DamageTypes.CONTACT.getId(),
                  DamageTypes.DROWN.getId(),
                  DamageTypes.FALL.getId(),
                  DamageTypes.FIRE.getId(),
                  DamageTypes.HUNGER.getId(),
                  DamageTypes.MAGMA.getId(),
                  DamageTypes.SUFFOCATE.getId()).contains(damageSource.getType().getId()))
              .filter(damageSource -> event.getTargetEntity() instanceof Subject
                  ? !Nope.getInstance().getHostTree().lookup(SettingLibrary.EVP,
                  (Subject) event.getTargetEntity(),
                  event.getTargetEntity().getLocation())
                  : !Nope.getInstance().getHostTree().lookupAnonymous(SettingLibrary.EVP,
                  event.getTargetEntity().getLocation()))
              .isPresent());
  @DynamicSettingListener
  static SettingListener<DamageEntityEvent> FALL_DAMAGE_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.FALL_DAMAGE,
          DamageEntityEvent.class,
          event -> event.getCause()
              .first(DamageSource.class)
              .filter(damageSource -> damageSource.getType().getId().equals(DamageTypes.FALL.getId()))
              .filter(damageSource -> event.getTargetEntity() instanceof Subject
                  ? !Nope.getInstance().getHostTree().lookup(SettingLibrary.FALL_DAMAGE,
                  (Subject) event.getTargetEntity(),
                  event.getTargetEntity().getLocation())
                  : !Nope.getInstance().getHostTree().lookupAnonymous(SettingLibrary.FALL_DAMAGE,
                  event.getTargetEntity().getLocation()))
              .isPresent());
  @DynamicSettingListener
  static SettingListener<ChangeBlockEvent> FIRE_SPREAD_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.FIRE_SPREAD,
          ChangeBlockEvent.class,
          event -> event.getSource() instanceof LocatableBlock
              && ((LocatableBlock) event.getSource()).getBlockState()
              .getType()
              .equals(BlockTypes.FIRE)
              && event.getTransactions()
              .stream()
              .anyMatch(trans -> !Nope.getInstance()
                  .getHostTree()
                  .lookupAnonymous(SettingLibrary.FIRE_SPREAD, trans.getFinal()
                      .getLocation()
                      .orElseThrow(() -> new RuntimeException("Could not get the "
                          + "final block location during fire spread")))));
  @DynamicSettingListener
  static SettingListener<ChangeBlockEvent> FROSTED_ICE_FORM_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.FROSTED_ICE_FORM,
          ChangeBlockEvent.class,
          simpleChangeBlockCanceler(SettingLibrary.FROSTED_ICE_FORM,
              BlockTypes.WATER,
              BlockTypes.FROSTED_ICE));
  @DynamicSettingListener
  static SettingListener<ChangeBlockEvent> FROSTED_ICE_MELT_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.FROSTED_ICE_MELT,
          ChangeBlockEvent.class,
          simpleChangeBlockCanceler(SettingLibrary.FROSTED_ICE_MELT,
              BlockTypes.FROSTED_ICE,
              BlockTypes.WATER));
  @DynamicSettingListener
  static SettingListener<ChangeBlockEvent> GRASS_GROWTH_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.GRASS_GROWTH,
          ChangeBlockEvent.class,
          simpleChangeBlockCanceler(SettingLibrary.GRASS_GROWTH,
              BlockTypes.DIRT,
              BlockTypes.GRASS));
  @DynamicSettingListener
  static SettingListener<DamageEntityEvent> HVP_LISTENER =
      new CancelConditionSettingListener<>(
          SettingLibrary.HVP,
          DamageEntityEvent.class,
          event -> event.getCause()
              .first(Entity.class)
              .filter(entity -> entity instanceof Hostile)
              .filter(damageSource -> event.getTargetEntity() instanceof Subject
                  ? !Nope.getInstance().getHostTree().lookup(SettingLibrary.EVP,
                  (Subject) event.getTargetEntity(),
                  event.getTargetEntity().getLocation())
                  : !Nope.getInstance().getHostTree().lookupAnonymous(SettingLibrary.EVP,
                  event.getTargetEntity().getLocation()))
              .isPresent());

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
            : String.format(" The player is %s at location (%d, %d, %d) in %s",
            player.getName(),
            player.getLocation().getBlockX(),
            player.getLocation().getBlockY(),
            player.getLocation().getBlockZ(),
            player.getLocation().getExtent().getName())));
  }

  private static void printEvent(Event event) {
    Nope.getInstance().getLogger().info("Event...");
    Nope.getInstance().getLogger().info("Source: " + event.getSource().toString());
    event.getCause().forEach(o -> Nope.getInstance().getLogger().info("Cause: " + o.toString()));
    Nope.getInstance().getLogger().info("Context: " + event.getContext().asMap().entrySet()
        .stream()
        .map(entry -> "{key: " + entry.getKey() + ", value: " + entry.getValue())
        .collect(Collectors.joining(", ")));
  }

  static Consumer<MoveEntityEvent> thresholdHandler(
      SettingKey<SettingLibrary.Movement> dictator,
      Location<World> inside,
      Location<World> outside,
      SettingKey<Text> allowMessageKey,
      SettingKey<Text> allowTitleKey,
      SettingKey<Text> allowSubtitleKey,
      SettingKey<Text> denyMessageKey) {
    return (event) -> {
      if (!(event.getCause().root() instanceof Player)) {
        return;
      }
      Player player = (Player) event.getCause().root();
      Nope.getInstance()
          .getHostTree()
          .getContainingHosts(inside)
          .stream()
          .filter(host -> !host.encompasses(outside))
          .forEach(host -> {
            Optional<SettingValue<SettingLibrary.Movement>> movementOptional =
                host.get(dictator);
            boolean shouldCancel;
            if (!movementOptional.isPresent()
                || !movementOptional.get().getTarget().test(player)) {
              shouldCancel = false;
            } else {
              SettingLibrary.Movement movement = movementOptional.get().getData();
              switch (movement) {
                case ALL:
                  shouldCancel = false;
                  break;
                // These other ones require a little more nuance. Implement in the future
//            case NONE:
//              shouldCancel = true;
//              break;
//            case NOT_TELEPORTATION:
//              shouldCancel = event instanceof MoveEntityEvent.Teleport;
//              break;
                default:
                  shouldCancel = !(event instanceof MoveEntityEvent.Teleport);
                  break;
              }
            }
            Optional<SettingValue<Text>> message;
            if (shouldCancel) {
              event.setCancelled(true);
              message = host.get(denyMessageKey);
            } else {
              message = host.get(allowMessageKey);

              boolean showTitle = false;
              Title.Builder titleBuilder = Title.builder();
              Optional<SettingValue<Text>> title = host.get(allowTitleKey);
              Optional<SettingValue<Text>> subtitle = host.get(allowSubtitleKey);
              if (title.isPresent() && title.get().getTarget().test(player)) {
                showTitle = true;
                titleBuilder.title(title.get().getData());
              }
              if (subtitle.isPresent() && subtitle.get().getTarget().test(player)) {
                showTitle = true;
                titleBuilder.subtitle(subtitle.get().getData());
              }
              if (showTitle) {
                player.sendTitle(titleBuilder.build());
              }
            }
            if (message.isPresent() && message.get().getTarget().test(player)) {
              player.sendMessage(message.get().getData());
            }
          });
    };
  }

  static Predicate<ChangeBlockEvent> simpleChangeBlockCanceler(SettingKey<Boolean> key,
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
                trans.getFinal().getLocation().orElseThrow(() ->
                    new RuntimeException("Could not get the final block"
                        + "location during ChangeBlockEvent for key: "
                        + key.getId()))));
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface DynamicSettingListener {
    // none
  }

}
