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

package me.pietelite.nope.sponge.listener.dynamic;

import java.util.HashMap;
import java.util.UUID;
import me.pietelite.nope.common.setting.SettingKeys;
import me.pietelite.nope.sponge.SpongeNope;
import me.pietelite.nope.sponge.api.event.SettingEventReport;
import me.pietelite.nope.sponge.api.event.SettingListenerRegistration;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.world.server.ServerLocation;

/**
 * Implements {@link SettingKeys#LIGHT_NETHER_PORTAL}.
 */
public class LightNetherPortalListener {

  private final HashMap<ServerLocation, UUID> fireOwnership = new HashMap<>();

  /**
   * Stage all handlers required to implement this listener.
   */
  public static void stage() {
    LightNetherPortalListener listener = new LightNetherPortalListener();
    // Listener to populate fire ownership map with the players that own certain fires
    SpongeNope.instance()
        .settingListeners()
        .stage(new SettingListenerRegistration<>(SettingKeys.LIGHT_NETHER_PORTAL,
            ChangeBlockEvent.All.class,
            SpongeNope.instance().pluginContainer(),
            context ->
                context.event().cause().first(ServerPlayer.class).ifPresent(player ->
                    context.event().transactions().forEach(transaction -> {
                      if (transaction.finalReplacement().state().type().equals(BlockTypes.FIRE.get())) {
                        transaction.finalReplacement().location().ifPresent(location ->
                            listener.fireOwnership.put(location, player.uniqueId()));
                      }
                    })),
            Order.LAST));
    // Listener to stop lighting nether portals
    SpongeNope.instance()
        .settingListeners()
        .stage(new SettingListenerRegistration<>(SettingKeys.LIGHT_NETHER_PORTAL,
            ChangeBlockEvent.All.class,
            SpongeNope.instance().pluginContainer(),
            context ->
                context.event().transactions().forEach(transaction -> {
                  if (transaction.original().state().type().equals(BlockTypes.FIRE.get())
                      && transaction.finalReplacement().state()
                      .type().equals(BlockTypes.NETHER_PORTAL.get())) {
                    transaction.finalReplacement().location().ifPresent(location -> {
                      if (!context.lookup(Sponge.server()
                          .player(listener.fireOwnership.get(location))
                          .orElse(null), location)) {
                        transaction.setValid(false);
                        context.report(SettingEventReport.restricted().build());
                      }
                      listener.fireOwnership.remove(location);
                    });
                  }
                })));
    SettingKeys.LIGHT_NETHER_PORTAL.functional(true);
  }

}
