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

package me.pietelite.nope.sponge.context;

import java.util.Optional;
import java.util.function.Consumer;
import me.pietelite.nope.common.host.Domain;
import me.pietelite.nope.common.host.Host;
import me.pietelite.nope.common.host.Scene;
import me.pietelite.nope.sponge.SpongeNope;
import me.pietelite.nope.sponge.util.SpongeUtil;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.context.ContextCalculator;

/**
 * A context calculator for Nope zones for the purpose of
 * context-intelligent permission use.
 */
public class ZoneContextCalculator implements ContextCalculator {

  @Override
  public void accumulateContexts(Cause source, Consumer<Context> accumulator) {
    Optional<ServerPlayer> player = source.first(ServerPlayer.class);
    if (!player.isPresent()) {
      return;
    }
    SpongeNope.instance().system()
        .collectSuperiorHosts(SpongeUtil.reduceLocation(player.get().serverLocation()))
        .forEach(host -> accumulator.accept(calculateContext(host)));
  }

  private Context calculateContext(Host host) {
    if (host instanceof Domain) {
      return new Context("nope.w." + host.name(), "true");
    } else if (host instanceof Scene) {
      return new Context("nope.z." + host.name(), "true");
    }
    return null;
  }
}
