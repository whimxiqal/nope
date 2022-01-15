//
//package com.minecraftonline.nope.sponge.listenerold;
//
//import com.minecraftonline.nope.common.setting.SettingKey;
//import org.jetbrains.annotations.NotNull;
//import org.spongepowered.api.entity.living.player.Player;
//import org.spongepowered.api.event.Cancellable;
//import org.spongepowered.api.event.Event;
//
///**
// * An accessibility class for cancelling events if a player is the root cause
// * of an event and they are found to have a specific state on a specific
// * setting.
// *
// * @param <E> the event type for which to listen and cancel
// */
//public abstract class PlayeredCancelerSettingListener<E extends Event & Cancellable>
//    extends PlayeredSettingListener<E> {
//
//  public PlayeredCancelerSettingListener(@NotNull Class<E> eventClass,
//                                         @NotNull SettingKey<?> keys) {
//    super(eventClass, keys);
//  }
//
//  @Override
//  public void handle(E event, Player player) {
//    if (shouldCancel(event, player)) {
//      event.setCancelled(true);
//    }
//  }
//
//  public abstract boolean shouldCancel(E event, Player player);
//
//}
