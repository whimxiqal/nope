package com.minecraftonline.nope.sponge.listener;

import com.minecraftonline.nope.common.setting.SettingKey;
import java.util.function.BiConsumer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;

/**
 * An accessibility class for handling events where a player is
 * the root cause.
 *
 * @param <E> the type of event for which to listen
 */
public abstract class PlayeredSettingListener<E extends Event> extends SettingListener<E> {
  public PlayeredSettingListener(@NotNull Class<E> eventClass,
                                 @NotNull SettingKey<?>... keys) {
    super(eventClass, keys);
  }


  @Override
  public void handle(E event) {
    if (!(event.cause().root() instanceof Player)) {
      return;
    }
    handle(event, (Player) event.cause().root());
  }

  public abstract void handle(E event, Player player);

}
