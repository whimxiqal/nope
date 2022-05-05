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

package me.pietelite.nope.sponge.listener.always;

import java.util.Optional;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.sponge.SpongeNope;
import me.pietelite.nope.sponge.util.EffectsUtil;
import me.pietelite.nope.sponge.util.SpongeUtil;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;

public class InteractiveVolumeListener {

  @Listener(order = Order.FIRST)
  public void onInteract(InteractEvent event) {
    if (!(event.source() instanceof ServerPlayer)) {
      return;
    }
    ServerPlayer player = (ServerPlayer) event.source();
    if (event instanceof InteractItemEvent.Primary) {
      onResizeVolume(player, player.sneaking().get());
      return;
    }
    if (event instanceof InteractBlockEvent.Primary.Start) {
      onResizeVolume(player, player.sneaking().get());
    }
    if (!(event instanceof Cancellable)) {
      return;
    }
    Cancellable cancellable = (Cancellable) event;

    if (Nope.instance().interactiveVolumeHandler().hasSession(player.uniqueId())) {
      cancellable.setCancelled(true);
    }
  }

  private void onResizeVolume(ServerPlayer player, boolean contract) {
    if (!Nope.instance().interactiveVolumeHandler().hasSession(player.uniqueId())) {
      return;
    }
    if (contract) {
      Nope.instance().interactiveVolumeHandler().contract(player.uniqueId(),
          SpongeUtil.toDirection(player.headDirection()));
    } else {
      Nope.instance().interactiveVolumeHandler().expand(player.uniqueId(),
          SpongeUtil.toDirection(player.headDirection()));
    }
  }

  @Listener(order = Order.FIRST)
  public void onLogOut(ServerSideConnectionEvent.Disconnect event) {
    if (Nope.instance().interactiveVolumeHandler().hasSession(event.player().uniqueId())) {
      Nope.instance().interactiveVolumeHandler().finishSession(event.player().uniqueId());
    }
  }

}
