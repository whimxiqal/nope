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

package me.pietelite.nope.sponge.command.parameters;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.api.register.SettingCategory;
import me.pietelite.nope.common.host.Host;
import me.pietelite.nope.common.host.HostedProfile;
import me.pietelite.nope.common.host.Profile;
import me.pietelite.nope.common.host.Scene;
import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.common.util.ContainsInOrderPredicate;
import me.pietelite.nope.common.util.Validate;
import me.pietelite.nope.sponge.SpongeNope;
import me.pietelite.nope.sponge.util.Formatter;
import me.pietelite.nope.sponge.util.SpongeUtil;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.exception.ArgumentParseException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.util.Nameable;
import org.spongepowered.api.util.StartsWithPredicate;
import org.spongepowered.api.world.server.ServerWorld;

/**
 * An enumerating class to centralize different methods for giving
 * command arguments specific to Nope.
 */
public class Parameters {

  public static final Parameter.Value<String> DESCRIPTION = Parameter.builder(ParameterKeys.DESCRIPTION)
      .addParser((parameterKey, reader, context) -> {
        StringBuilder builder = new StringBuilder();
        while (reader.canRead()) {
          builder.append(reader.parseString());
        }
        return Optional.of(builder.toString());
      }).build();
  public static final Parameter.Value<Host> HOST = Parameter.builder(ParameterKeys.HOST)
      .addParser((parameterKey, reader, context) -> {
        String hostName = reader.parseString().toLowerCase();
        Host host = SpongeNope.instance().system().hosts(Nope.NOPE_SCOPE).get(hostName);
        if (host == null) {
          throw new ArgumentParseException(Formatter.error("No host exists named ___", hostName),
              hostName, 0);
        }
        return Optional.of(host);
      })
      .completer((context, currentInput) -> {
        final Predicate<String> inOrder = new ContainsInOrderPredicate(currentInput);
        return SpongeNope.instance().system()
            .hosts(Nope.NOPE_SCOPE)
            .entrySet()
            .stream()
            .filter(entry -> inOrder.test(entry.getKey()))
            .map(entry -> CommandCompletion.of(entry.getKey(),
                Formatter.host(entry.getValue())))
            .collect(Collectors.toList());
      })
      .build();
  public static final Parameter.Value<Host> HOST_INFER = Parameter.builder(ParameterKeys.HOST)
      .optional()
      .addParser((parameterKey, reader, context) -> {
        String hostName = reader.parseString().toLowerCase();
        Host host = SpongeNope.instance().system().hosts(Nope.NOPE_SCOPE).get(hostName);
        if (host == null) {
          return inferHost(context);
        } else {
          return Optional.of(host);
        }
      })
      .completer((context, currentInput) -> {
        final Predicate<String> startsWith = new StartsWithPredicate(currentInput);
        return SpongeNope.instance().system()
            .hosts(Nope.NOPE_SCOPE)
            .entrySet()
            .stream()
            .filter(entry -> startsWith.test(entry.getKey()))
            .map(entry -> CommandCompletion.of(entry.getKey(),
                Formatter.host(entry.getValue())))
            .collect(Collectors.toList());
      })
      .build();
  public static final Parameter.Value<Scene> SCENE = Parameter.builder(ParameterKeys.SCENE)
      .addParser((parameterKey, reader, context) -> {
        String hostName = reader.parseString().toLowerCase();
        Scene scene = SpongeNope.instance().system().scope(Nope.NOPE_SCOPE).scenes().get(hostName);
        if (scene == null) {
          throw new ArgumentParseException(Formatter.error("No scene exists named ___", hostName),
              hostName, 0);
        }
        return Optional.of(scene);
      })
      .completer((context, currentInput) -> {
        final Predicate<String> inOrder = new ContainsInOrderPredicate(currentInput);
        return SpongeNope.instance().system().scope(Nope.NOPE_SCOPE)
            .scenes()
            .entrySet()
            .stream()
            .filter(entry -> inOrder.test(entry.getKey()))
            .map(entry -> CommandCompletion.of(entry.getKey(),
                Formatter.host(entry.getValue())))
            .collect(Collectors.toList());
      })
      .build();
  public static final Parameter.Value<String> ID = Parameter.builder(ParameterKeys.ID)
      .addParser(
          (parameterKey, reader, context) -> {
            String name = reader.parseString();
            if (Validate.invalidId(name)) {
              throw new ArgumentParseException(
                  Formatter.error("Names can only have numbers, "
                      + "letters, and some special characters"),
                  name,
                  0);
            }
            if (name.equalsIgnoreCase("help")) {
              throw new ArgumentParseException(Formatter.error("You cannot name something \"help\""),
                  name,
                  0);
            }
            return Optional.of(name);
          }

      )
      .build();
  public static final Parameter.Value<Profile> PROFILE = Parameter.builder(ParameterKeys.PROFILE)
      .addParser((parameterKey, reader, context) -> {
        String profileName = reader.parseString().toLowerCase();
        Profile profile = SpongeNope.instance().system().scope(Nope.NOPE_SCOPE).profiles().get(profileName.toLowerCase());
        if (profile == null) {
          throw new ArgumentParseException(Formatter.error("No profile exists named ___", profileName),
              profileName, 0);
        }
        return Optional.of(profile);
      })
      .completer((context, currentInput) -> {
        final Predicate<String> inOrder = new ContainsInOrderPredicate(currentInput);
        return SpongeNope.instance().system().scope(Nope.NOPE_SCOPE)
            .profiles()
            .entrySet()
            .stream()
            .filter(entry -> inOrder.test(entry.getValue().name()))
            .map(entry -> CommandCompletion.of(entry.getValue().name(),
                Formatter.profile(entry.getValue())))
            .collect(Collectors.toList());
      })
      .build();
  public static final Parameter.Value<Profile> PROFILE_ON_HOST = Parameter.builder(ParameterKeys.PROFILE)
      .addParser((parameterKey, reader, context) -> {
        String profileName = reader.parseString().toLowerCase();
        Profile profile = SpongeNope.instance().system().scope(Nope.NOPE_SCOPE).profiles().get(profileName.toLowerCase());
        if (profile == null) {
          throw new ArgumentParseException(Formatter.error("No profile exists named ___", profileName),
              profileName, 0);
        }
        return Optional.of(profile);
      })
      .completer((context, currentInput) -> {
        final Predicate<String> inOrder = new ContainsInOrderPredicate(currentInput);
        Host host = context.requireOne(HOST);
        return host.allProfiles()
            .stream()
            .map(HostedProfile::profile)
            .filter(profile -> inOrder.test(profile.name()))
            .map(profile -> CommandCompletion.of(profile.name(),
                Formatter.profile(profile)))
            .collect(Collectors.toList());
      })
      .build();
  public static final Parameter.Value<String> PERMISSION = Parameter.string()
      .key(ParameterKeys.PERMISSION)
      .build();
  public static final Parameter.Value<Boolean> PERMISSION_VALUE = Parameter.bool()
      .key(ParameterKeys.PERMISSION_VALUE)
      .build();
  public static final Parameter.Value<ServerPlayer> PLAYER = Parameter.player()
      .key(ParameterKeys.PLAYER)
      .build();
  public static final Parameter.Value<Set<CompletableFuture<GameProfile>>> PLAYER_LIST = set(name ->
          Sponge.server().gameProfileManager().profile(name),
      () -> Sponge.server().onlinePlayers().stream().map(Nameable::name).collect(Collectors.toSet()),
      ParameterKeys.PLAYER_LIST,
      "players")
      .build();
  public static final Parameter.Value<ServerPlayer> PLAYER_OPTIONAL = Parameter.player()
      .key(ParameterKeys.PLAYER_OPTIONAL)
      .optional()
      .terminal()
      .completer((context, currentInput) -> {
        final Predicate<String> startsWith = new StartsWithPredicate(currentInput);
        return Sponge.server().onlinePlayers()
            .stream()
            .map(Nameable::name)
            .filter(startsWith)
            .map(CommandCompletion::of)
            .collect(Collectors.toList());
      })
      .build();
  public static final Parameter.Value<Double> POS_X = Parameter.rangedDouble(-Nope.WORLD_RADIUS,
          Nope.WORLD_RADIUS)
      .key(ParameterKeys.POS_X)
      .usage(key -> "X")
      .build();
  public static final Parameter.Value<Double> POS_X_1 = Parameter.rangedDouble(-Nope.WORLD_RADIUS,
          Nope.WORLD_RADIUS)
      .key(ParameterKeys.POS_X_1)
      .usage(key -> "first X")
      .build();
  public static final Parameter.Value<Double> POS_X_2 = Parameter.rangedDouble(-Nope.WORLD_RADIUS,
          Nope.WORLD_RADIUS)
      .key(ParameterKeys.POS_X_2)
      .usage(key -> "second X")
      .build();
  public static final Parameter.Value<Double> POS_Y = Parameter.rangedDouble(-Nope.WORLD_RADIUS,
          Nope.WORLD_RADIUS)
      .key(ParameterKeys.POS_Y)
      .usage(key -> "Y")
      .build();
  public static final Parameter.Value<Double> POS_Y_1 = Parameter.rangedDouble(-Nope.WORLD_DEPTH,
          Nope.WORLD_DEPTH)
      .key(ParameterKeys.POS_Y_1)
      .usage(key -> "first Y")
      .build();
  public static final Parameter.Value<Double> POS_Y_2 = Parameter.rangedDouble(-Nope.WORLD_DEPTH,
          Nope.WORLD_DEPTH)
      .key(ParameterKeys.POS_Y_2)
      .usage(key -> "second Y")
      .build();
  public static final Parameter.Value<Double> POS_Z = Parameter.rangedDouble(-Nope.WORLD_RADIUS,
          Nope.WORLD_RADIUS)
      .key(ParameterKeys.POS_Z)
      .usage(key -> "Z")
      .build();
  public static final Parameter.Value<Double> POS_Z_1 = Parameter.rangedDouble(-Nope.WORLD_RADIUS,
          Nope.WORLD_RADIUS)
      .key(ParameterKeys.POS_Z_1)
      .usage(key -> "first Z")
      .build();
  public static final Parameter.Value<Double> POS_Z_2 = Parameter.rangedDouble(-Nope.WORLD_RADIUS,
          Nope.WORLD_RADIUS)
      .key(ParameterKeys.POS_Z_2)
      .usage(key -> "second Z")
      .build();
  public static final Parameter.Value<Integer> PRIORITY = Parameter.rangedInteger(0, 10000000)
      .key(ParameterKeys.PRIORITY)
      .build();
  public static final Parameter.Value<Double> RADIUS = Parameter.rangedDouble(0, Nope.WORLD_RADIUS)
      .key(ParameterKeys.RADIUS)
      .usage(key -> "radius")
      .build();
  public static final Parameter.Value<String> REGEX = Parameter.string().key(ParameterKeys.REGEX).build();
  public static final Parameter.Value<?> SETTING_CATEGORY = Parameter.enumValue(SettingCategory.class)
      .key(ParameterKeys.SETTING_CATEGORY)
      .optional()
      .build();
  public static final Parameter.Value<SettingKey<?, ?, ?>> SETTING_KEY =
      Parameter.builder(ParameterKeys.SETTING_KEY)
          .addParser((parameterKey, reader, context) -> {
            String id = reader.parseString();
            SettingKey<?, ?, ?> key = SpongeNope.instance().settingKeys().get(id);
            if (key == null) {
              throw new ArgumentParseException(Formatter.error(
                  "___ is not a setting key", id
              ), id, 0);
            }
            return Optional.of(key);
          })
          .completer((context, currentInput) -> {
            Optional<Host> host = context.one(HOST);
            boolean showGlobal = host.isPresent()
                && host.get().equals(SpongeNope.instance().system().global());
            final Predicate<String> inOrder = new ContainsInOrderPredicate(currentInput);
            return SpongeNope.instance().settingKeys()
                .keys()
                .entrySet()
                .stream()
                .filter(entry -> inOrder.test(entry.getKey()))
                .filter(entry -> showGlobal || !entry.getValue().global())
                .map(entry -> CommandCompletion.of(entry.getKey(),
                    Formatter.accent(entry.getValue().description())))
                .collect(Collectors.toList());
          }).build();
  public static final Parameter.Value<String> SETTING_VALUE = Parameter.remainingJoinedStrings()
      .key(ParameterKeys.SETTING_VALUE)
      .optional()
      .terminal()
      .completer((context, currentInput) -> {
        SettingKey<?, ?, ?> settingKey = context.requireOne(ParameterKeys.SETTING_KEY);
        Map<String, Object> options = settingKey.manager().elementSuggestions();
        final Predicate<String> startsWith;
        if (settingKey instanceof SettingKey.Poly) {
          String[] tokensSoFar = currentInput.split(SettingKey.Manager.Poly.SET_SPLIT_REGEX, -1);
          List<String> completedTokens = new LinkedList<>();
          if (tokensSoFar.length > 0) {
            for (int i = 0; i < tokensSoFar.length - 1; i++) {
              if (!options.containsKey(tokensSoFar[i].toLowerCase())) {
                return Collections.emptyList();
              }
              completedTokens.add(tokensSoFar[i]);
            }
            startsWith = new ContainsInOrderPredicate(tokensSoFar[tokensSoFar.length - 1]);
          } else {
            // I don't think this should ever happen
            startsWith = new StartsWithPredicate("");
          }
          return options.entrySet().stream()
              .filter(entry -> startsWith.test(entry.getKey()))
              .map(entry -> {
                final Object value = entry.getValue();
                final String preface = completedTokens.stream()
                    .map(s -> s + ", ")
                    .collect(Collectors.joining(""));
                return CommandCompletion.of(preface + entry.getKey(),
                    value instanceof Component
                        ? (Component) value
                        : Formatter.accent(value.toString()));
              })
              .collect(Collectors.toList());
        } else {
          startsWith = new ContainsInOrderPredicate(currentInput);
          return options.entrySet().stream()
              .filter(entry -> startsWith.test(entry.getKey()))
              .map(entry -> {
                Object value = entry.getValue();
                return CommandCompletion.of(entry.getKey(),
                    value instanceof Component
                        ? (Component) value
                        : Formatter.accent(value.toString()));
              })
              .collect(Collectors.toList());
        }
      })
      .build();
  public static final Parameter.Value<ParameterValueTypes.SettingValueAlterType> SETTING_VALUE_ALTER_TYPE =
      Parameter.builder(ParameterKeys.SETTING_VALUE_ALTER_TYPE)
          .completer((context, currentInput) -> {
            SettingKey<?, ?, ?> settingKey = context.requireOne(ParameterKeys.SETTING_KEY);
            ParameterValueTypes.SettingValueAlterType[] options;
            if (settingKey instanceof SettingKey.Poly) {
              options = ParameterValueTypes.SettingValueAlterType.values();
            } else {
              options = new ParameterValueTypes.SettingValueAlterType[]{
                  ParameterValueTypes.SettingValueAlterType.SET
              };
            }
            final Predicate<String> startsWith = new StartsWithPredicate(currentInput);
            return Arrays.stream(options)
                .filter(option -> startsWith.test(option.command()))
                .map(option -> CommandCompletion.of(option.command(), Formatter.accent(option.description())))
                .collect(Collectors.toList());
          })
          .addParser((parameterKey, reader, context) -> {
            SettingKey<?, ?, ?> settingKey = context.requireOne(ParameterKeys.SETTING_KEY);
            String token = reader.parseString();
            ParameterValueTypes.SettingValueAlterType alterType =
                Arrays.stream(ParameterValueTypes.SettingValueAlterType.values())
                    .filter(v -> v.command().equalsIgnoreCase(token))
                    .findFirst()
                    .orElseThrow(() -> new ArgumentParseException(Formatter.error("Invalid command"),
                        token,
                        0));
            if (!(settingKey instanceof SettingKey.Poly)
                && (alterType != ParameterValueTypes.SettingValueAlterType.SET)) {
              throw new ArgumentParseException(
                  Formatter.error("You may only use the set command with this type of setting"),
                  token,
                  0);
            } else {
              return Optional.of(alterType);
            }
          })
          .build();
  public static final Parameter.Value<String> STRING = Parameter.string().key(ParameterKeys.VALUE).build();
  public static final Parameter.Value<TargetOption> TARGET_OPTION = Parameter.enumValue(TargetOption.class)
      .key(ParameterKeys.TARGET_OPTION).build();
  public static final Parameter.Value<Integer> VOLUME_INDEX = Parameter.integerNumber()
      .key(ParameterKeys.VOLUME_INDEX)
      .completer((context, currentInput) -> {
        final Predicate<String> startsWith = new StartsWithPredicate(currentInput);
        Host host = context.requireOne(HOST);
        if (host instanceof Scene) {
          Scene scene = (Scene) host;
          return IntStream.range(0, scene.volumes().size()).boxed()
              .filter(integer -> startsWith.test(String.valueOf(integer)))
              .map(integer -> CommandCompletion.of(String.valueOf(integer)
                  // TODO add an info method to volumes to give insight as to what they are
                  /*, scene.volumes().get(integer).info */))
              .collect(Collectors.toList());
        } else {
          return Collections.emptyList();
        }
      })
      .build();
  public static final Parameter.Value<Integer> VOLUME_INDEX_OPTIONAL = Parameter.integerNumber()
      .key(ParameterKeys.VOLUME_INDEX)
      .optional()
      .build();
  public static final Parameter.Value<ServerWorld> WORLD = Parameter.world().key(ParameterKeys.WORLD).build();
  public static final Parameter.Multi CUBOID = Parameter.seqBuilder(Parameters.WORLD)
      .then(Parameters.POS_X_1)
      .then(Parameters.POS_Y_1)
      .then(Parameters.POS_Z_1)
      .then(Parameters.POS_X_2)
      .then(Parameters.POS_Y_2)
      .then(Parameters.POS_Z_2)
      .optional().build();
  public static final Parameter.Multi CYLINDER = Parameter.seqBuilder(Parameters.WORLD)
      .then(Parameters.POS_X)
      .then(Parameters.POS_Y_1)
      .then(Parameters.POS_Y_2)
      .then(Parameters.POS_Z)
      .then(Parameters.RADIUS)
      .optional().build();
  public static final Parameter.Multi SPHERE = Parameter.seqBuilder(Parameters.WORLD)
      .then(Parameters.POS_X)
      .then(Parameters.POS_Y)
      .then(Parameters.POS_Z)
      .then(Parameters.RADIUS)
      .optional().build();
  public static final Parameter.Multi SLAB = Parameter.seqBuilder(Parameters.WORLD)
      .then(Parameters.POS_Y_1)
      .then(Parameters.POS_Y_2)
      .optional().build();

  /**
   * A static generator for a {@link Parameter.Value.Builder} in charge of handling a set of values,
   * given functionality to parse each value.
   *
   * @param parser      the parser for individual items
   * @param options     all possible options for the values in the set, used for completion
   * @param key         the parameter key
   * @param pluralTypes the plural form of the type being parsed here
   * @param <T>         the type of values parsed
   * @return the builder of a value parameter
   */
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
    if (!(context.cause().root() instanceof ServerPlayer)) {
      return Optional.empty();
    }
    ServerPlayer player = (ServerPlayer) context.cause().root();
    Collection<Host> containing = SpongeNope.instance()
        .system()
        .containingHosts(SpongeUtil.reduceLocation(player.serverLocation()));
    if (containing.isEmpty()) {
      return Optional.empty();
    }
    return containing.stream().max(Comparator.comparing(Host::priority));
  }

}
