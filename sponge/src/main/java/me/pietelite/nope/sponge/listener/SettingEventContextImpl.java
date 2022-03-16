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

package me.pietelite.nope.sponge.listener;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.common.struct.Location;
import me.pietelite.nope.sponge.SpongeNope;
import me.pietelite.nope.sponge.api.event.SettingEventContext;
import me.pietelite.nope.sponge.api.event.SettingEventReport;
import me.pietelite.nope.sponge.util.Formatter;
import me.pietelite.nope.sponge.util.SpongeUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.world.server.ServerLocation;

public class SettingEventContextImpl<T, E extends Event> implements SettingEventContext<T, E> {

  final E event;
  final SettingKey<? extends T, ?, ?> settingKey;

  public SettingEventContextImpl(E event, SettingKey<? extends T, ?, ?> settingKey) {
    this.event = event;
    this.settingKey = settingKey;
  }

  @Override
  public E event() {
    return event;
  }

  @Override
  public T lookup(ServerLocation location) {
    return lookup(event.source(), location);
  }

  @Override
  public T lookup(Object rootCause, ServerLocation location) {
    return SpongeUtil.valueFor(settingKey, rootCause, location);
  }

  @Override
  public T lookup(UUID userUuid, Location location) {
    return SpongeNope.instance().hostSystem().lookup(settingKey, userUuid, location).result();
  }

  @Override
  public void report(SettingEventReport report) {
    Set<UUID> recipients = Nope.instance().debugManager().watchersOf(report.source(),
        settingKey,
        report.target());
    for (UUID recipient : recipients) {
      Sponge.server().player(recipient).ifPresent(player -> {
        switch (report.action()) {
          case RESTRICTED:
            player.sendMessage(Formatter.info("___ (___): restricted ___",
                    Component.text("Report").color(Formatter.GOLD).decorate(TextDecoration.BOLD),
                    settingKey.id(),
                    Optional.ofNullable(report.source()).orElse(event.source().toString()))
                .hoverEvent(HoverEvent.showText(
                    Component.text("Report").color(Formatter.GOLD)
                        .append(Component.newline())
                        .append(Formatter.accent("Event Class: ___",
                            event.getClass().getSimpleName()))
                        .append(Optional.ofNullable(report.target())
                            .map(target -> Component.newline().append(Formatter.accent("Target: ___", target)))
                            .orElse(Component.empty()))
                )));
            break;
          case PREVENTED:
            player.sendMessage(Formatter.info("___ (___): prevented",
                    Component.text("Report").color(Formatter.GOLD).decorate(TextDecoration.BOLD),
                    settingKey.id())
                .hoverEvent(HoverEvent.showText(
                    Component.text("Report").color(Formatter.GOLD)
                        .append(Component.newline())
                        .append(Formatter.accent("Event Class: ___",
                            event.getClass().getSimpleName()))
                        .append(Optional.ofNullable(report.target())
                            .map(target -> Component.newline().append(Formatter.accent("Target: ___", target)))
                            .orElse(Component.empty()))
                )));
            break;
          default:
            Sponge.server().sendMessage(Component.text("Report: unknown report type: "
                + report.action().name()));
        }
      });
    }
  }

}
