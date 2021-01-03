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
import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.setting.SettingKey;
import com.minecraftonline.nope.setting.SettingLibrary;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;

import javax.annotation.Nonnull;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

/**
 * A container class for all dynamically registered listeners.
 */
@SuppressWarnings("unused")
public class SettingListeners {

  public static void register() {
    Arrays.stream(SettingListeners.class.getDeclaredFields())
            .filter(field -> Modifier.isStatic(field.getModifiers()))
            .filter(field -> SettingListener.class.isAssignableFrom(field.getType()))
            .forEach(field -> {
              try {
                ((SettingListener<?>) field.get(null)).registerIfNecessary();
              } catch (IllegalAccessException e) {
                e.printStackTrace();
              }
            });
  }

  /**
   * An accessibility class for handling events where a player is
   * the root cause.
   *
   * @param <E> the type of event for which to listen
   */
  public static class PlayerRootSettingListener<E extends Event> extends SettingListener<E> {
    public PlayerRootSettingListener(@Nonnull SettingKey<Boolean> key,
                                     @Nonnull Class<E> eventClass,
                                     BiConsumer<E, Player> handler) {
      super(key, eventClass, event -> {
        Player player;
        if (!(event.getCause().root() instanceof Player)) {
          return;
        }
        handler.accept(event, (Player) event.getCause().root());
      });
    }

  }

  /**
   * An accessibility class for cancelling events if a player is the root cause
   * of an event and they are found to have a specific state on a specific
   * setting.
   *
   * @param <E> the event type for which to listen and cancel
   */
  public static class PlayerCancelConditionSettingListener<E extends Event & Cancellable>
          extends PlayerRootSettingListener<E> {

    public PlayerCancelConditionSettingListener(@Nonnull SettingKey<Boolean> key,
                                                @Nonnull Class<E> eventClass,
                                                BiPredicate<E, Player> canceler) {
      super(key,
              eventClass,
              (event, player) -> {
                if (canceler.test(event, player)) {
                  event.setCancelled(true);
                }
              });
    }
  }

  private static RuntimeException noLocation(SettingKey<?> key,
                                             Class<? extends Event> eventClass,
                                             Player player) {
    return new RuntimeException(String.format(
            "The relevant location for the dynamic event listener for "
                    + "Setting Key %s and event class %s could not be found. "
                    + "The player is %s at location (%d, %d, %d) in %s",
            key.getId(),
            eventClass.getName(),
            player.getName(),
            player.getLocation().getBlockX(),
            player.getLocation().getBlockY(),
            player.getLocation().getBlockZ(),
            player.getLocation().getExtent().getName()
    ));
  }

  public static SettingListener<ChangeBlockEvent> BUILD_PERMISSIONS_LISTENER = new PlayerCancelConditionSettingListener<>(
          SettingLibrary.BUILD_PERMISSIONS,
          ChangeBlockEvent.class,
          (event, player) -> event.getTransactions().stream().anyMatch(transaction ->
                  !Nope.getInstance().getHostTree().lookup(
                          SettingLibrary.BUILD_PERMISSIONS,
                          player,
                          transaction.getOriginal().getLocation().orElse(transaction.getFinal()
                                  .getLocation()
                                  .orElseThrow(() -> noLocation(SettingLibrary.BUILD_PERMISSIONS,
                                          ChangeBlockEvent.class,
                                          player))))));

  public static SettingListener<ChangeBlockEvent.Break> BLOCK_BREAK_LISTENER = new PlayerCancelConditionSettingListener<>(
          SettingLibrary.BLOCK_BREAK,
          ChangeBlockEvent.Break.class,
          (event, player) -> event.getTransactions().stream().anyMatch(transaction ->
                  !Nope.getInstance().getHostTree().lookup(
                          SettingLibrary.BLOCK_BREAK,
                          player,
                          transaction.getOriginal().getLocation().orElse(transaction.getFinal()
                                  .getLocation()
                                  .orElseThrow(() -> noLocation(SettingLibrary.BLOCK_BREAK,
                                          ChangeBlockEvent.Break.class,
                                          player))))));

  public static SettingListener<ChangeBlockEvent.Place> BLOCK_PLACE_LISTENER = new PlayerCancelConditionSettingListener<>(
          SettingLibrary.BLOCK_PLACE,
          ChangeBlockEvent.Place.class,
          (event, player) -> event.getTransactions().stream().anyMatch(transaction ->
                  !Nope.getInstance().getHostTree().lookup(
                          SettingLibrary.BLOCK_PLACE,
                          player,
                          transaction.getOriginal().getLocation().orElse(transaction.getFinal()
                                  .getLocation()
                                  .orElseThrow(() -> noLocation(SettingLibrary.BLOCK_PLACE,
                                          ChangeBlockEvent.Place.class,
                                          player))))));

  public static SettingListener<ChangeBlockEvent> BLOCK_TRAMPLE = new PlayerCancelConditionSettingListener<>(
          SettingLibrary.BLOCK_TRAMPLE,
          ChangeBlockEvent.class,
          (event, player) -> event.getTransactions().stream().anyMatch(transaction ->
                  !Nope.getInstance().getHostTree().lookup(
                          SettingLibrary.BLOCK_TRAMPLE,
                          player,
                          transaction.getOriginal().getLocation().orElse(transaction.getFinal()
                                  .getLocation()
                                  .orElse(player.getLocation())))
                          && transaction.getOriginal()
                          .getState()
                          .getType().equals(BlockTypes.FARMLAND)
                          && transaction.getFinal()
                          .getState()
                          .getType().equals(BlockTypes.DIRT)));

  public static SettingListener<InteractBlockEvent.Secondary> CHEST_ACCESS_LISTENER = new PlayerCancelConditionSettingListener<>(
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
                    && !Nope.getInstance().getHostTree().lookup(SettingLibrary.CHEST_ACCESS,
                    player,
                    event.getTargetBlock().getLocation()
                            .orElseThrow(() -> noLocation(SettingLibrary.CHEST_ACCESS,
                                    InteractBlockEvent.Secondary.class,
                                    player)));
          });

}
