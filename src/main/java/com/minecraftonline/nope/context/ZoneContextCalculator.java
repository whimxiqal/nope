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

package com.minecraftonline.nope.context;

import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.host.Host;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.permission.Subject;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.Set;

public class ZoneContextCalculator implements ContextCalculator<Subject> {

  @Override
  public void accumulateContexts(@Nonnull Subject target, @Nonnull Set<Context> accumulator) {
    if (!target.getCommandSource().isPresent()) {
      return;
    }
    if (!(target.getCommandSource().get() instanceof Player)) {
      return;
    }
    Nope.getInstance().getHostTree()
        .getContainingHosts(((Player) target.getCommandSource().get()).getLocation())
        .forEach(host -> accumulator.add(host.getContext()));
  }

  @Override
  public boolean matches(@Nonnull Context context, @Nonnull Subject target) {
    if (!target.getCommandSource().isPresent()) {
      return false;
    }
    if (!(target.getCommandSource().get() instanceof Player)) {
      return false;
    }
    return Host.contextKeyToName(context.getKey())
        .flatMap(name -> Optional.of(Nope.getInstance().getHostTree().getHosts().get(name)))
        .filter(host -> host.encompasses((Player) target))
        .isPresent();
  }
}
