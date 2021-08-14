/*
 * MIT License
 *
 * Copyright (c) 2020 MinecraftOnline
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

package com.minecraftonline.nope.sponge.command.general.arguments;

import com.minecraftonline.nope.common.host.Host;
import com.minecraftonline.nope.sponge.SpongeNope;
import com.minecraftonline.nope.sponge.wand.Selection;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.exception.ArgumentParseException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.util.StartsWithPredicate;
import org.spongepowered.api.world.server.ServerWorld;

/**
 * An enumerating class to centralize different methods for giving
 * command arguments specific to Nope.
 */
public class NopeParameters {

  public static final Parameter HOST = Parameter.builder(NopeParameterKeys.HOST)
      .optional()
      .addParser((parameterKey, reader, context) -> {
        String hostName = reader.parseString().toLowerCase();
        Settee settee = SpongeNope.instance().getHostSystemAdapter().getHosts().get(hostName);
        return Optional.ofNullable(settee);
      })
      .completer((context, currentInput) -> {
        final Predicate<String> startsWith = new StartsWithPredicate(currentInput);
        return SpongeNope.instance().getHostSystemAdapter()
            .getHosts()
            .entrySet()
            .stream()
            .filter(entry -> startsWith.test(entry.getKey()))
            .map(entry -> CommandCompletion.of(entry.getKey(),
                SpongeNope.instance().formatter().host(entry.getValue())))
            .collect(Collectors.toList());
      })
      .build();

  public static final Parameter HOST_INFER = Parameter.builder(NopeParameterKeys.HOST)
      .optional()
      .addParser((parameterKey, reader, context) -> {
        String hostName = reader.parseString().toLowerCase();
        Settee settee = SpongeNope.instance().getHostSystemAdapter().getHosts().get(hostName);
        if (settee == null) {
          return inferHost(context);
        } else {
          return Optional.of(settee);
        }
      })
      .completer((context, currentInput) -> {
        final Predicate<String> startsWith = new StartsWithPredicate(currentInput);
        return SpongeNope.instance().getHostSystemAdapter()
            .getHosts()
            .entrySet()
            .stream()
            .filter(entry -> startsWith.test(entry.getKey()))
            .map(entry -> CommandCompletion.of(entry.getKey(),
                SpongeNope.instance().formatter().host(entry.getValue())))
            .collect(Collectors.toList());
      })
      .build();

  public static final Parameter TEMPLATE = Parameter.builder(NopeParameterKeys.TEMPLATE)
      .addParser((parameterKey, reader, context) -> Optional.ofNullable(Templates.get(reader.parseString())))
      .completer((context, currentInput) -> {
        final Predicate<String> startsWith = new StartsWithPredicate(currentInput);
        return Templates.getMap()
            .entrySet()
            .stream()
            .filter(entry -> startsWith.test(entry.getKey()))
            .map(entry -> CommandCompletion.of(entry.getKey(),
                Component.text(entry.getValue().getDescription())))
            .collect(Collectors.toList());
      })
      .build();

  public static final Parameter SELECTION = Parameter.builder(NopeParameterKeys.SELECTION)
      .optional()
      .usage(key -> "[<world> <x1> <y1> <z1> <x2> <y2> <z2>]")
      .addParser((parameterKey, reader, context) -> {
        if (!reader.canRead()) {
          if (context.cause().root() instanceof Player) {
            Player player = (Player) context.cause().root();
            List<String> errors = new LinkedList<>();
            Optional<Selection> selection = SpongeNope.instance()
                .getSelectionHandler()
                .draft(player.uniqueId())
                .build(errors);
            if (!errors.isEmpty()) {
              throw new ArgumentParseException(Component.text(errors.get(0)), reader.peekString(), 0);
            }
            return selection;
          } else {
            return Optional.empty();
          }
        }

        String worldName = reader.parseString();
        Optional<ServerWorld> world = Sponge.server().worldManager().world(ResourceKey.minecraft(worldName));
        if (!world.isPresent()) {
          return Optional.empty();
        }

        int x1 = reader.parseInt();
        int y1 = reader.parseInt();
        int z1 = reader.parseInt();
        int x2 = reader.parseInt();
        int y2 = reader.parseInt();
        int z2 = reader.parseInt();

        for (int pos : new int[]{x1, z1, x2, z2}) {
          if (pos < -SpongeNope.WORLD_RADIUS || pos > SpongeNope.WORLD_RADIUS) {
            throw new ArgumentParseException(Component.text("Value " + pos + " is out of range!"),
                String.valueOf(pos),
                0);
          }
        }

        for (int pos : new int[]{y1, y2}) {
          if (pos < 0 || pos > SpongeNope.WORLD_DEPTH) {
            throw new ArgumentParseException(Component.text("Value " + pos + " is out of range!"),
                String.valueOf(pos),
                0);
          }
        }

        return Optional.of(new Selection(world.get().key(), x1, y1, z1, x2, y2, z2));
      })
      .build();

  public static final Parameter PRIORITY = Parameter.builder(NopeParameterKeys.PRIORITY)
      .addParser((parameterKey, reader, context) -> {
        try {
          return Optional.of(reader.parseInt());
        } catch (ArgumentParseException e) {
          if (context.cause().root() instanceof Player) {
            return Optional.of(SpongeNope.instance()
                .getHostSystemAdapter()
                .collectSuperiorHosts(AdapterUtil.adaptLocation(((Player) context.cause().root())
                    .serverLocation()))
                .stream().max(Comparator.comparingInt(Host::getPriority))
                .map(host -> host.getPriority() + 1).orElse(0));
          } else {
            throw e;
          }
        }
      })
      .optional()
      .build();

  public static final Parameter NAME = Parameter.builder(NopeParameterKeys.NAME).build();

  public static final Parameter REGEX = Parameter.builder(NopeParameterKeys.REGEX).build();

  /**
   * Infer the desired host to manipulate depending on location.
   * If the command was sent by a player, then this host is the
   * one surrounding the player with the highest priority.
   *
   * @param context the command context
   * @return the host
   */
  public static Optional<Settee> inferHost(CommandContext context) {
    if (!(context.cause().root() instanceof Player)) {
      return Optional.empty();
    }
    Player player = (Player) context.cause().root();
    Collection<Settee> containing = SpongeNope.instance()
        .getHostSystemAdapter()
        .collectSuperiorHosts(AdapterUtil.adaptLocation(player.serverLocation()));
    if (containing.isEmpty()) {
      return Optional.empty();
    }
    return containing.stream().max(Comparator.comparing(Host::getPriority));
  }
}
