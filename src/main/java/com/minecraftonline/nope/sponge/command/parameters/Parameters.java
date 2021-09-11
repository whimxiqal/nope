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

package com.minecraftonline.nope.sponge.command.parameters;

import com.minecraftonline.nope.common.Nope;
import com.minecraftonline.nope.common.host.Host;
import com.minecraftonline.nope.common.host.Zone;
import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.common.setting.SettingKeys;
import com.minecraftonline.nope.common.setting.template.Template;
import com.minecraftonline.nope.sponge.SpongeNope;
import com.minecraftonline.nope.sponge.util.Formatter;
import com.minecraftonline.nope.sponge.util.SpongeUtil;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.exception.ArgumentParseException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.ValueUsage;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.util.Nameable;
import org.spongepowered.api.util.StartsWithPredicate;
import org.spongepowered.api.world.server.ServerWorld;

/**
 * An enumerating class to centralize different methods for giving
 * command arguments specific to Nope.
 */
public class Parameters {

  public static final Parameter.Value<Host> HOST = Parameter.builder(ParameterKeys.HOST)
      .addParser((parameterKey, reader, context) -> {
        String hostName = reader.parseString().toLowerCase();
        Host host = SpongeNope.instance().hostSystem().hosts().get(hostName);
        if (host == null) {
          throw new ArgumentParseException(Formatter.error("No hosts exist named ___", hostName),
              hostName, 0);
        }
        return Optional.of(host);
      })
      .completer((context, currentInput) -> {
        final Predicate<String> startsWith = new StartsWithPredicate(currentInput);
        return SpongeNope.instance().hostSystem()
            .hosts()
            .entrySet()
            .stream()
            .filter(entry -> startsWith.test(entry.getKey()))
            .map(entry -> CommandCompletion.of(entry.getKey(),
                Formatter.host(entry.getValue())))
            .collect(Collectors.toList());
      })
      .build();

  public static final Parameter.Value<Host> HOST_INFER = Parameter.builder(ParameterKeys.HOST)
      .optional()
      .addParser((parameterKey, reader, context) -> {
        String hostName = reader.parseString().toLowerCase();
        Host host = SpongeNope.instance().hostSystem().hosts().get(hostName);
        if (host == null) {
          return inferHost(context);
        } else {
          return Optional.of(host);
        }
      })
      .completer((context, currentInput) -> {
        final Predicate<String> startsWith = new StartsWithPredicate(currentInput);
        return SpongeNope.instance().hostSystem()
            .hosts()
            .entrySet()
            .stream()
            .filter(entry -> startsWith.test(entry.getKey()))
            .map(entry -> CommandCompletion.of(entry.getKey(),
                Formatter.host(entry.getValue())))
            .collect(Collectors.toList());
      })
      .build();

  public static final Parameter.Value<Template> TEMPLATE = Parameter.builder(ParameterKeys.TEMPLATE)
      .addParser((parameterKey, reader, context) ->
          Optional.ofNullable(Nope.instance().templateSet().get(reader.parseString())))
      .completer((context, currentInput) -> {
        final Predicate<String> startsWith = new StartsWithPredicate(currentInput);
        return Nope.instance().templateSet()
            .templates()
            .stream()
            .filter(template -> startsWith.test(template.name()))
            .map(template -> CommandCompletion.of(template.name(),
                Component.text(template.description())))
            .collect(Collectors.toList());
      })
      .build();

  public static final Parameter.Value<Integer> PRIORITY = Parameter.rangedInteger(0, 10000000)
      .key(ParameterKeys.PRIORITY)
      .build();
  public static final Parameter.Value<String> NAME = Parameter.string()
      .key(ParameterKeys.NAME)
      .build();
  public static final Parameter.Value<String> DESCRIPTION = Parameter.builder(ParameterKeys.DESCRIPTION)
      .addParser((parameterKey, reader, context) -> {
        StringBuilder builder = new StringBuilder();
        while (reader.canRead()) {
          builder.append(reader.parseString());
        }
        return Optional.of(builder.toString());
      }).build();
  public static final Parameter.Value<String> REGEX = Parameter.string().key(ParameterKeys.REGEX).build();
  public static final Parameter.Value<SettingKey<?>> SETTING_KEY = Parameter.builder(ParameterKeys.SETTING_KEY)
      .addParser((parameterKey, reader, context) -> {
        String id = reader.parseString();
        SettingKey<?> key = SettingKeys.get(id);
        if (key == null) {
          throw new ArgumentParseException(Formatter.error(
              "___ is not a setting key", id
          ), id, 0);
        }
        return Optional.of(key);
      })
      .completer((context, currentInput) -> {
        final Predicate<String> startsWith = new StartsWithPredicate(currentInput);
        return SettingKeys.keys()
            .entrySet()
            .stream()
            .filter(entry -> startsWith.test(entry.getKey()))
            .map(entry -> CommandCompletion.of(entry.getKey(),
                Formatter.accent(entry.getValue().description())))
            .collect(Collectors.toList());
      }).build();
  public static final Parameter.Value<String> SETTING_DATA = Parameter.remainingJoinedStrings()
      .key(ParameterKeys.SETTING_DATA)
      .completer((context, currentInput) -> context.requireOne(ParameterKeys.SETTING_KEY)
          .options()
          .stream()
          .map(string -> CommandCompletion.of(string, Component.empty()))
          .collect(Collectors.toList()))
      .build();
  public static final Parameter.Value<Set<CompletableFuture<GameProfile>>> PLAYER_LIST = set(name -> Sponge.server()
          .gameProfileManager()
          .profile(name),
      () -> Sponge.server().onlinePlayers().stream().map(Nameable::name).collect(Collectors.toSet()),
      ParameterKeys.PLAYER_LIST,
      "players")
      .build();
  public static final Parameter.Value<String> PERMISSION = Parameter.string().key(ParameterKeys.PERMISSION).build();
  public static final Parameter.Value<Boolean> PERMISSION_VALUE = Parameter.bool().key(ParameterKeys.PERMISSION_VALUE).build();
  public static final Parameter.Value<TargetOption> TARGET_OPTION = Parameter.enumValue(TargetOption.class)
      .key(ParameterKeys.TARGET_OPTION).build();
  public static final Parameter.Value<Zone> PARENT = Parameter.builder(ParameterKeys.PARENT)
      .addParser((parameterKey, reader, context) -> {
        String hostName = reader.parseString().toLowerCase();
        Host host = SpongeNope.instance().hostSystem().hosts().get(hostName);
        if (host == null) {
          return Optional.empty();
        }
        if (host instanceof Zone) {
          return Optional.of((Zone) host);
        } else {
          throw new ArgumentParseException(Formatter.error("Host ___ may not be a parent"),
              hostName, 0);
        }
      })
      .completer((context, currentInput) -> {
        final Predicate<String> startsWith = new StartsWithPredicate(currentInput);
        return SpongeNope.instance().hostSystem()
            .hosts()
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue() instanceof Zone)
            .filter(entry -> startsWith.test(entry.getKey()))
            .map(entry -> CommandCompletion.of(entry.getKey(),
                Formatter.host(entry.getValue())))
            .collect(Collectors.toList());
      })
      .build();

  public static final Parameter.Value<Integer> POS_X = Parameter.rangedInteger(-Nope.WORLD_RADIUS, Nope.WORLD_RADIUS)
      .key(ParameterKeys.POS_X)
      .usage(key -> "X")
      .build();

  public static final Parameter.Value<Integer> POS_Y = Parameter.rangedInteger(-Nope.WORLD_RADIUS, Nope.WORLD_RADIUS)
      .key(ParameterKeys.POS_Y)
      .usage(key -> "Y")
      .build();

  public static final Parameter.Value<Integer> POS_Z = Parameter.rangedInteger(-Nope.WORLD_RADIUS, Nope.WORLD_RADIUS)
      .key(ParameterKeys.POS_Z)
      .usage(key -> "Z")
      .build();

  public static final Parameter.Value<Integer> POS_X_1 = Parameter.rangedInteger(-Nope.WORLD_RADIUS, Nope.WORLD_RADIUS)
      .key(ParameterKeys.POS_X_1)
      .usage(key -> "first X")
      .build();
  public static final Parameter.Value<Integer> POS_Y_1 = Parameter.rangedInteger(-Nope.WORLD_DEPTH, Nope.WORLD_DEPTH)
      .key(ParameterKeys.POS_Y_1)
      .usage(key -> "first Y")
      .build();
  public static final Parameter.Value<Integer> POS_Z_1 = Parameter.rangedInteger(-Nope.WORLD_RADIUS, Nope.WORLD_RADIUS)
      .key(ParameterKeys.POS_Z_1)
      .usage(key -> "first Z")
      .build();
  public static final Parameter.Value<Integer> POS_X_2 = Parameter.rangedInteger(-Nope.WORLD_RADIUS, Nope.WORLD_RADIUS)
      .key(ParameterKeys.POS_X_2)
      .usage(key -> "second X")
      .build();
  public static final Parameter.Value<Integer> POS_Y_2 = Parameter.rangedInteger(-Nope.WORLD_DEPTH, Nope.WORLD_DEPTH)
      .key(ParameterKeys.POS_Y_2)
      .usage(key -> "second Y")
      .build();
  public static final Parameter.Value<Integer> POS_Z_2 = Parameter.rangedInteger(-Nope.WORLD_RADIUS, Nope.WORLD_RADIUS)
      .key(ParameterKeys.POS_Z_2)
      .usage(key -> "second Z")
      .build();
  public static final Parameter.Value<Double> RADIUS = Parameter.rangedDouble(0, Nope.WORLD_RADIUS)
      .key(ParameterKeys.RADIUS)
      .usage(key -> "radius")
      .build();
  public static final Parameter.Value<ServerWorld> WORLD = Parameter.world().key(ParameterKeys.WORLD).build();
  public static final Parameter.Value<?> INDEX = Parameter.integerNumber().key(ParameterKeys.INDEX).build();
  public static final Parameter.Value<?> INDEX_OPTIONAL = Parameter.integerNumber().key(ParameterKeys.INDEX).optional().build();

  public static <T> Parameter.Value.Builder<Set<T>> set(Function<String, T> parser,
                                                        Supplier<Set<String>> options,
                                                        Parameter.Key<Set<T>> key,
                                                        String pluralTypes) {
    return Parameter.builder(key)
        .addParser((parameterKey, reader, context) -> {
          Set<T> set = new HashSet<>();
          StringBuilder allValues = new StringBuilder();
          String value = reader.parseString();
          allValues.append(value);
          while (value.charAt(value.length() - 1) == ',') {
            if (value.length() == 1) {
              throw new ArgumentParseException(Formatter.error(
                  "The element in the list must not be empty"),
                  allValues.toString(),
                  allValues.toString().length() - 1);
            }
            set.add(parser.apply(value.substring(0, value.length() - 1)));
            value = reader.parseString();
            allValues.append(" ").append(value);
          }
          set.add(parser.apply(value));
          return Optional.of(set);
        }).completer((context, currentInput) -> {
          String[] tokens = currentInput.split(",");
          String last = tokens[tokens.length - 1];
          String trimmed = last.trim();
          final Predicate<String> startsWith = new StartsWithPredicate(trimmed);
          return options.get().stream().filter(startsWith)
              .map(completed ->
                  CommandCompletion.of(
                      String.join(",", Arrays.asList(tokens).subList(0, tokens.length - 1))
                          + "," + last.replace(trimmed, completed),
                      Formatter.accent(
                          "A list of ___ including ___ ", pluralTypes, completed
                      ))).collect(Collectors.toList());
        });
  }

  /**
   * Infer the desired host to manipulate depending on location.
   * If the command was sent by a player, then this host is the
   * one surrounding the player with the highest priority.
   *
   * @param context the command context
   * @return the host
   */
  public static Optional<Host> inferHost(CommandContext context) {
    if (!(context.cause().root() instanceof Player)) {
      return Optional.empty();
    }
    Player player = (Player) context.cause().root();
    Collection<Host> containing = SpongeNope.instance()
        .hostSystem()
        .collectSuperiorHosts(SpongeUtil.reduceLocation(player.serverLocation()));
    if (containing.isEmpty()) {
      return Optional.empty();
    }
    return containing.stream().max(Comparator.comparing(Host::priority));
  }

}
