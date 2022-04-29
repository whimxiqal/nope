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

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;
import me.pietelite.nope.common.host.Host;
import me.pietelite.nope.common.host.HostedProfile;
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
public class NopeContextCalculator implements ContextCalculator {

  @Override
  public void accumulateContexts(Cause source, Consumer<Context> accumulator) {
    Optional<ServerPlayer> player = source.first(ServerPlayer.class);
    if (!player.isPresent()) {
      return;
    }
    SpongeNope.instance().system()
        .containingHosts(SpongeUtil.reduceLocation(player.get().serverLocation()))
        .forEach(host -> {
          for (Context context : contexts(host)) {
            accumulator.accept(context);
          }
        });
  }

  private Context[] contexts(Host host) {
    Context[] contexts = new Context[host.allProfiles().size() + 1];
    contexts[0] = new Context("nope.host." + host.name(), "true");
    ArrayList<HostedProfile> profiles = host.allProfiles();
    for (int i = 0; i < profiles.size(); i++) {
      contexts[i + 1] = new Context("nope.profile." + profiles.get(i).profile().name(), "true");
    }
    return contexts;
  }
}
