/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
 * Copyright (c) MinecraftOnline
 * Copyright (c) contributors
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

package me.pietelite.nope.sponge.util;

import java.net.URL;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import me.pietelite.nope.common.util.Proxy;
import me.pietelite.nope.sponge.SpongeNope;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Nameable;

public class SpongeProxy implements Proxy {
  @Override
  public boolean hasPermission(UUID playerUuid, String permission) {
    if (playerUuid == null) {
      return false;
    } else {
      return Sponge.server().player(playerUuid).map(player -> player.hasPermission(permission)).orElse(false);
    }
  }

  @Override
  public void scheduleAsyncIntervalTask(Runnable runnable, int interval, TimeUnit intervalUnit) {
    Sponge.asyncScheduler().submit(Task.builder()
        .execute(runnable)
        .interval(interval, intervalUnit)
        .plugin(SpongeNope.instance().pluginContainer())
        .build());
  }

  @Override
  public String version() {
    return SpongeNope.instance().pluginContainer().metadata().version().toString();
  }

  @Override
  public String sourceCodeLink() {
    return SpongeNope.instance().pluginContainer().metadata()
        .links()
        .source()
        .map(URL::toExternalForm)
        .orElseThrow(() -> new RuntimeException("Sponge Nope does not have a source link"));
  }

  @Override
  public Optional<String> uuidToPlayer(UUID uuid) {
    try {
      return Sponge.server().gameProfileManager()
          .profile(uuid)
          .get().name();
    } catch (InterruptedException | ExecutionException e) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<String> uuidToOnlinePlayer(UUID uuid) {
    return Sponge.server().player(uuid).map(Nameable::name);
  }
}
