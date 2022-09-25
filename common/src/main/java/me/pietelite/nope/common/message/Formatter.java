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
 *
 */

package me.pietelite.nope.common.message;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.api.struct.AltSet;
import me.pietelite.nope.common.host.Host;
import me.pietelite.nope.common.host.Profile;
import me.pietelite.nope.common.permission.Permissions;
import me.pietelite.nope.common.setting.Setting;
import me.pietelite.nope.common.setting.SettingCollection;
import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.common.setting.SettingValue;
import me.pietelite.nope.common.setting.Target;
import me.pietelite.nope.common.util.formatting.MinecraftCharacter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A utility class to store static fields and methods pertaining to
 * formatted messages with the purpose of sending meaningfully colored
 * and enhanced messages to players and other message receivers.
 */
public final class Formatter {

  public static final TextColor ACCENT = TextColor.color(7, 236, 186);
  public static final TextColor DARK = TextColor.color(64, 64, 64);
  public static final TextColor DULL = TextColor.color(179, 179, 179);
  public static final TextColor ERROR = TextColor.color(194, 12, 21);
  public static final String FORMAT_ACCENT_REGEX = "___";
  public static final TextColor GOLD = TextColor.color(222, 185, 0);
  public static final TextColor INFO = TextColor.color(255, 191, 245);
  public static final TextComponent SPACE = Component.text(" ");
  public static final TextColor SUCCESS = TextColor.color(11, 181, 38);
  public static final TextColor THEME = TextColor.color(161, 95, 232);
  public static final TextComponent TWO_SPACES = Component.text("  ");
  public static final TextColor URL = TextColor.color(66, 105, 224);
  public static final TextColor WARN = TextColor.color(255, 157, 10);
  public static final Component WELCOME =
      Component.text()
          .append(Component.newline())
          .append(Component.text("======================================="))
          .color(DULL)
          .append(Component.newline())
          .append(Component.text("Nope v" + Nope.proxy().version()).color(THEME))
          .append(Component.text(" by").color(DULL))
          .append(Component.text(" PietElite").color(ACCENT))
          .append(Component.newline()).append(Component.newline())
          .append(Component.text("Read the source code ")
              .color(INFO)
              .append(url("here", Nope.instance().proxy().sourceCodeLink()))
              .append(Component.text(" and submit"))
              .append(Component.newline())
              .append(Component.text("any bug reports or suggestions "))
              .append(url("here", Nope.instance().proxy().issuesLink())))
          .append(Component.newline()).append(Component.newline())
          .append(Component.text("Learn how to use Nope with the ")
              .color(DULL)
              .append(commandSuggest("help",
                  "/nope help",
                  Component.text("Learn how to use the Nope commands")
                      .color(ACCENT)
                      .append(Component.newline())
                      .append(Component.text("Tip: You can always put \"?\" or \"help\""
                          + " at the end of any command to learn how it works!"))))
              .append(Component.text(" command.").color(DULL)))
          .build();

  public static final TextColor WHITE = TextColor.color(255, 255, 255);

  /**
   * Return the plugin's standard prefix text, to prepend to some general text
   * sent on behalf of the plugin.
   *
   * @return the text component
   */
  public static Component prefix() {
    return Component.text()
        .append(Component.text("Nope ").color(THEME))
        .append(Component.text("- ").color(DARK))
        .build();
  }

  private static Component formattedMessage(TextColor color, String message,
                                            boolean prefix, Object... accented) {
    LinkedList<String> tokenList = new LinkedList<>(Arrays.asList(message.split(FORMAT_ACCENT_REGEX, -1)));

    TextComponent.Builder builder = Component.text();
    if (prefix) {
      builder.append(prefix());
    }
    for (int i = 0; i < tokenList.size() - 1; i++) {
      builder.append(Component.text(tokenList.get(i), color));
      if (accented[i] instanceof Component) {
        builder.append((Component) accented[i]);
      } else {
        builder.append(Component.text(accented[i].toString(), ACCENT));
      }
    }
    builder.append(Component.text(tokenList.getLast(), color));
    return builder.build();
  }

  public static Component success(String message) {
    return success(Component.text(message));
  }

  public static Component success(String message, Object... insertions) {
    return formattedMessage(SUCCESS, message, true, insertions);
  }

  public static Component success(Component message) {
    return prefix().append(message).color(SUCCESS);
  }

  public static Component error(String message) {
    return error(Component.text(message));
  }

  public static Component error(String message, Object... insertions) {
    return formattedMessage(ERROR, message, true, insertions);
  }

  public static Component error(Component message) {
    return prefix().append(message).color(ERROR);
  }

  public static Component warn(String message) {
    return warn(Component.text(message));
  }

  public static Component warn(String message, Object... insertions) {
    return formattedMessage(WARN, message, true, insertions);
  }

  public static Component warn(Component message) {
    return prefix().append(message).color(WARN);
  }

  public static Component info(String message) {
    return info(Component.text(message));
  }

  public static Component info(String message, Object... insertions) {
    return formattedMessage(INFO, message, true, insertions);
  }

  public static Component info(Component message) {
    return prefix().append(message).color(INFO);
  }

  public static Component accent(String message) {
    return Component.text(message).color(ACCENT);
  }

  public static Component accent(String message, Object... insertions) {
    return formattedMessage(WHITE, message, false, insertions);
  }

  public static Component keyValue(String key, String value) {
    return keyValue(key, Component.text(value));
  }

  public static Component keyValue(String key, Component value) {
    return keyValue(ACCENT, key, value);
  }

  public static Component keyValue(TextColor keyColor, String key, String value) {
    return keyValue(keyColor, key, Component.text(value));
  }

  /**
   * Format a key-value pair.
   *
   * @param keyColor the color of the key
   * @param key      the key
   * @param value    the value
   * @return the text component
   */
  public static Component keyValue(TextColor keyColor, String key, Component value) {
    return Component.text()
        .append(Component.text(key).color(keyColor))
        .append(Component.text(" "))
        .append(value)
        .build();
  }

  /**
   * Format some text to display text when the user hovers over it.
   *
   * @param label   the text to show in chat
   * @param onHover the text to display on hover
   * @return the text component
   */
  public static Component hover(String label, String onHover) {
    return hover(Component.text(label).decorate(TextDecoration.ITALIC),
        Component.text(onHover));
  }

  public static Component hover(Component label, Component onHover) {
    return label.hoverEvent(HoverEvent.showText(onHover));
  }

  /**
   * Format some text to link to a given url.
   *
   * @param label the text to show in the chat
   * @param url   the url to link to on click
   * @return the component text
   */
  public static Component url(@NotNull String label, @NotNull String url) {
    TextComponent.Builder textBuilder = Component.text();
    textBuilder.append(Component.text(label).color(URL));
    textBuilder.hoverEvent(HoverEvent.showText(Component.text(url)));
    try {
      textBuilder.clickEvent(ClickEvent.openUrl(new URL(url)));
    } catch (MalformedURLException ex) {
      textBuilder.clickEvent(ClickEvent.suggestCommand(url));
      Nope.instance().logger().error("A url was not formed correctly for a"
          + " click action: " + url);
    }

    return textBuilder.build();
  }

  public static Component commandSuggest(@NotNull String label,
                                         @NotNull String command,
                                         @Nullable Component hoverMessage) {
    return command(label, command, hoverMessage, true, true);
  }

  public static Component command(@NotNull String label,
                                  @NotNull String command,
                                  @Nullable Component hoverMessage) {
    return command(label, command, hoverMessage, true, false);
  }

  /**
   * Format text to run a command when clicked.
   *
   * @param label        the text to show in the chat
   * @param command      the command to run when the text is clicked
   * @param hoverMessage the text to show when hovered
   * @param accentuate   whether to make the text stand out in the chat window
   * @param suggest      whether to suggest the command instead of running it immediately
   * @return the text component
   */
  public static Component command(@NotNull String label,
                                  @NotNull String command,
                                  @Nullable Component hoverMessage,
                                  boolean accentuate,
                                  boolean suggest) {
    Component labelText = Component.text(label).color(ACCENT);
    if (accentuate) {
      labelText = Component.text()
          .append(Component.text("[").color(GOLD))
          .append(labelText)
          .append(Component.text("]").color(GOLD))
          .build();
    }

    TextComponent.Builder builder = Component.text()
        .append(labelText)
        .clickEvent(suggest
            ? ClickEvent.suggestCommand(command)
            : ClickEvent.runCommand(command));

    if (hoverMessage != null) {
      builder.hoverEvent(HoverEvent.showText(hoverMessage.color(ACCENT)
          .append(Component.text("\n" + command).color(DULL))));
    }

    return builder.build();
  }

  /**
   * Format a {@link Host} for showing in chat.
   *
   * @param host the host
   * @return the text component
   */
  public static Component host(@NotNull Host host) {
    return command(
        host.name(),
        String.format("/nope host %s info ", host.name()),
        Component.text("Click for more details about this host"),
        false,
        false
    );
  }

  /**
   * Format a {@link Host} for showing in chat.
   *
   * @param profile the profile
   * @return the text component
   */
  public static Component profile(@NotNull Profile profile) {
    return command(
        profile.name(),
        String.format("/nope host %s info ", profile.name()),
        Component.text("Click for more details about this profile"),
        false,
        false
    );
  }

  /**
   * Format a {@link SettingKey} to display in chat.
   *
   * @param key     the key
   * @param verbose will show more information about the setting key
   * @param <T>     the data type
   * @param <V>     the value type
   * @return the text component
   */
  public static <T, V extends SettingValue<T>> Component settingKey(SettingKey<T, V, ?> key,
                                                                    boolean verbose) {
    TextComponent.Builder idText = Component.text().append(Component.text(key.id()).color(ACCENT));

    TextComponent.Builder hoverText = Component.text()
        .append(Component.text(key.id()).color(ACCENT))
        .append(Component.newline());

    if (!key.functional()) {
      idText.decorate(TextDecoration.STRIKETHROUGH);
      hoverText.append(Component.text("Not implemented yet!").color(ERROR));
      hoverText.append(Component.newline());
    }

    hoverText.append(keyValue("Type:", key.type()));
    hoverText.append(Component.newline());

    String defaultData = key.manager().printData(key.defaultData());
    hoverText.append(keyValue("Default value:", defaultData.isEmpty()
        ? "(Empty)"
        : defaultData));
    hoverText.append(Component.newline());

    hoverText.append(keyValue("Restrictive:", String.valueOf(key.playerRestrictive())));
    hoverText.append(Component.newline());

    hoverText.append(keyValue("Category:", key.category().name().toLowerCase()));

    String description = key.description();
    String blurb = key.blurb();
    if (description != null) {
      hoverText.append(Component.newline()).append(Component.newline());
      hoverText.append(Component.text(description).color(WHITE));
    }

    TextComponent.Builder builder = Component.text().hoverEvent(HoverEvent.showText(hoverText.build()));

    builder.append(idText.build());
    if (verbose) {
      builder.append(SPACE)
          .append(Component.text(
              blurb == null
                  ? (description == null ? "No description" : description)
                  : blurb));
    }

    return builder.build();
  }

  /**
   * Format a {@link Setting} to show in chat.
   *
   * @param setting    the setting to show
   * @param collection the setting collection that stores the setting
   * @param <T>        the data type
   * @param <V>        the value type
   * @return a list of components return when the contents are available
   */
  public static <T, V extends SettingValue<T>> CompletableFuture<List<Component>> setting(
      Setting<T, V> setting,
      @NotNull SettingCollection collection) {
    return CompletableFuture.supplyAsync(() -> {
      TextComponent.Builder main = Component.text();

      String dataString = setting.value() == null
          ? "(Empty)"
          : setting.key().manager().printValue(setting.value());
      main.append(settingKey(setting.key(), false),
          Component.text(" = ").append(Component.text(dataString)));

      List<Component> list = new LinkedList<>();

      list.add(main.build());

      if (setting.target() != null) {
        Target target = setting.requireTarget();
        if (!target.users().isEmpty()) {
          list.add(Component.text(" > ").color(SUCCESS)
              .append(keyValue(target.hasWhitelist() ? "Whitelist:" : "Blacklist:",
                  target.users()
                      .stream()
                      .map(uuid -> Nope.instance().proxy().uuidToPlayer(uuid).orElseThrow(() ->
                          new RuntimeException("Failed to get user profile name "
                              + "for UUID: " + uuid)))
                      .filter(s -> !s.isEmpty())
                      .collect(Collectors.joining(", ")))));
        }
        target.permissions().forEach((permission, value) ->
            list.add(Component.text(" > ").color(SUCCESS)
                .append(keyValue(permission + ":", String.valueOf(value)))));
        if (target.indiscriminate()) {
          list.add(Component.text(" > ").color(SUCCESS)
              .append(hover("FORCE AFFECT",
                  "When affect is forced, players with the "
                      + Permissions.UNRESTRICTED.get()
                      + " permission may still be targeted")));
        }
      }
      return list;
    });
  }

  /**
   * Format a {@link SettingValue} for chat.
   *
   * @param value                a text component of a printed version of the setting value
   * @param redundantOnDefault   true if this setting value is the same as the default value,
   *                             and therefore redundant
   * @param redundancyController the host that is in a configuration such that it has a
   *                             value that makes this given value redundant.
   *                             If it is redundant by the default value, this host should just
   *                             be the host with the given value
   * @return the text component
   */
  public static Component settingValue(Component value,
                                       boolean redundantOnDefault,
                                       Host redundancyController) {
    TextComponent.Builder builder = Component.text();
    if (redundancyController != null) {
      // Redundant
      builder.append(Component.text().append(value).color(DULL).decorate(TextDecoration.STRIKETHROUGH));
      if (redundantOnDefault) {
        builder.hoverEvent(HoverEvent.showText(Component.text(
            "This setting is redundant because it is the default value,"
                + " so this setting serves no purpose.")));
      } else {
        builder.hoverEvent(HoverEvent.showText(Component.text()
            .append(Component.text("This setting is redundant because host "))
            .append(host(redundancyController))
            .append(Component.text(" has the same setting, so this setting serves no purpose."))));
      }
    } else {
      builder.append(value);
    }
    return builder.build();
  }
//
//  /**
//   * Get a paginator specific to this plugin, using a specific title string.
//   *
//   * @param title the title of the pages
//   * @return the paginator builder
//   */
//  public static PaginationList.Builder paginator(String title) {
//    return paginator(Component.text(title).color(Formatter.GOLD));
//  }
//
//  /**
//   * Get a paginator specific to this plugin, using a specific title component.
//   *
//   * @param title the title of the pages
//   * @return the paginator builder
//   */
//  public static PaginationList.Builder paginator(Component title) {
//    return Sponge.serviceProvider().paginationService()
//        .builder()
//        .padding(Component.text("=").color(Formatter.THEME))
//        .title(title);
//  }

  public static Component dull(String s) {
    return formattedMessage(DULL, s, false);
  }

  /**
   * Send a {@link Setting} editor panel to an {@link Audience} member.
   *
   * @param audience the audience to receive the text panel
   * @param profile  the profile on which to set settings
   * @param page     the page to send of the paginator
   */
  public static void sendSettingEditor(Audience audience, Profile profile, int page) {
    List<Component> contents = new LinkedList<>();
    Map<String, SettingKey<?, ?, ?>> map = Nope.instance().settingKeys().keys();
    int maxIdPixelSize = MinecraftCharacter.longestPixelLength(map.keySet());
    map.values()
        .stream()
        .sorted()
        .filter(profile::canHave)
        .forEach(key -> {
          if (key instanceof SettingKey.Poly) {
            contents.addAll(editableSettingPoly(profile, (SettingKey.Poly<?, ?>) key, maxIdPixelSize));
          } else if (key instanceof SettingKey.Unary) {
            contents.addAll(editableSettingUnary(profile, (SettingKey.Unary<?>) key, maxIdPixelSize));
          }
        });
//    paginator(formattedMessage(GOLD, "___ Settings", false, profile(profile)))
//        .contents(contents)
//        .header(Component.text("Click on values to apply them. ").color(DULL)
//            .append(Component.text("View ")))
//        .build()
//        .sendTo(audience, page);
  }

  /**
   * Get a list of chat components that allow graphical interfacing with
   * a {@link SettingKey.Unary} type {@link Setting}.
   *
   * @param profile               the profile to edit
   * @param key                   the key of the setting to edit
   * @param firstColumnPixelWidth the pixel width of the first column
   * @param <T>                   the data type
   * @return the list of chat components
   */
  @SuppressWarnings("unchecked")
  public static <T> List<Component> editableSettingUnary(Profile profile, SettingKey.Unary<T> key,
                                                         int firstColumnPixelWidth) {
    TextComponent.Builder line = Component.text()
        .append(settingKey(key, false))
        .append(Component.text(MinecraftCharacter.spacesRequiredToExtend(key.id(), firstColumnPixelWidth)));
    Optional<Setting<T, SettingValue.Unary<T>>> setting = profile.get(key);
    if (setting.isPresent()) {
      line.append(SPACE.append(Component.text("unset")
          .color(INFO)
          .decorate(TextDecoration.UNDERLINED)
          .hoverEvent(HoverEvent.showText(accent("Unset ___ from ___", key.id(), profile.name())))
          .clickEvent(ClickEvent.runCommand(String.format("/nope host %s edit setting %s value -e unset",
              profile.name(), key.id())))));
    } else {
      line.append(Component.text(" unset").color(DARK));
    }

    List<Map.Entry<String, Object>> suggestions = new LinkedList<>(key.manager()
        .elementSuggestions()
        .entrySet());
    if (suggestions.isEmpty()) {
      // No suggestions, so have to allow user to type it in themselves
      if (setting.isPresent() && setting.get().value() != null) {
        line.append(TWO_SPACES.append(Component.text("[")
            .color(DULL)
            .append(Component.text(key.manager().printData(setting.get().value().get())))
            .append(Component.text("]").color(DULL))
            .hoverEvent(HoverEvent.showText(accent("Update ___ on ___", key.id(), profile.name())))
            .clickEvent(ClickEvent.suggestCommand(String.format("/nope host %s edit setting %s value -e set ",
                profile.name(), key.id())))));
      } else {
        line.append(TWO_SPACES.append(Component.text("set")
            .color(INFO)
            .decorate(TextDecoration.UNDERLINED)
            .hoverEvent(HoverEvent.showText(accent("Set ___ on ___", key.id(), profile.name())))
            .clickEvent(ClickEvent.suggestCommand(String.format("/nope host %s edit setting %s value -e set ",
                profile.name(), key.id())))));
      }
    } else {
      if (Comparable.class.isAssignableFrom(key.manager().dataType())) {
        suggestions.sort(Comparator.comparing(entry ->
            (Comparable<Object>) key.manager().parseData(entry.getKey())));
      } else {
        suggestions.sort(Map.Entry.comparingByKey());
      }
      for (Map.Entry<String, Object> entry : suggestions) {
        if (setting.isPresent() && key.manager()
            .printData(setting.get().value().get())
            .equalsIgnoreCase(entry.getKey())) {
          // the value is set to this
          T value = setting.get().value().get();
          line.append(TWO_SPACES.append(Component.text(entry.getKey())
              .color((value instanceof Boolean)
                  ? ((Boolean) value) ? SUCCESS : ERROR
                  : GOLD)));
        } else {
          line.append(TWO_SPACES.append(Component.text(entry.getKey())
              .color(INFO)
              .decorate(TextDecoration.UNDERLINED)
              .hoverEvent(HoverEvent.showText(castToComponent(entry.getValue())))
              .clickEvent(ClickEvent.runCommand(String.format("/nope host %s edit setting %s value -e set %s",
                  profile.name(), key.id(), entry.getKey())))));
        }
      }
    }

    return Collections.singletonList(line.build());
  }

  /**
   * Get a list of chat components that allow graphical interfacing with
   * a {@link SettingKey.Poly} type {@link Setting}.
   *
   * @param profile               the profile to edit
   * @param key                   the key of the setting to edit
   * @param firstColumnPixelWidth the pixel width of the first column
   * @param <T>                   the data type, which is a set
   * @param <S>                   the element in the set
   * @return the list of chat components
   */
  public static <T, S extends AltSet<T>> List<Component> editableSettingPoly(Profile profile,
                                                                             SettingKey.Poly<T, S> key,
                                                                             int firstColumnPixelWidth) {
    TextComponent.Builder line = Component.text()
        .append(settingKey(key, false))
        .append(Component.text(MinecraftCharacter.spacesRequiredToExtend(key.id(), firstColumnPixelWidth)));
    TextComponent secondLine = null;
    Optional<Setting<S, SettingValue.Poly<T, S>>> setting = profile.get(key);
    if (setting.isPresent() && setting.get().value() != null) {
      line.append(SPACE.append(Component.text("unset")
          .color(INFO)
          .decorate(TextDecoration.UNDERLINED)
          .hoverEvent(HoverEvent.showText(accent("Unset ___ from ___", key.id(), profile.name())))
          .clickEvent(ClickEvent.runCommand(String.format("/nope host %s edit setting %s value -e unset",
              profile.name(), key.id())))));
      SettingValue.Poly<T, S> value = setting.get().value();
      if (value.declarative()) {
        if (value.additive().isEmpty()) {
          line.append(TWO_SPACES.append(Component.text("none").color(GOLD)));
        } else {
          line.append(TWO_SPACES.append(Component.text("none")
              .color(INFO)
              .decorate(TextDecoration.UNDERLINED)
              .hoverEvent(HoverEvent.showText(accent("Set ___ on ___", key.id(), profile.name())))
              .clickEvent(ClickEvent.runCommand(String.format("/nope host %s edit setting "
                      + "%s value -e setnone",
                  profile.name(), key.id())))));
        }
        if (value.additive().isFull()) {
          line.append(TWO_SPACES.append(Component.text("all").color(GOLD)));
        } else {
          line.append(TWO_SPACES.append(Component.text("all")
              .color(INFO)
              .decorate(TextDecoration.UNDERLINED)
              .hoverEvent(HoverEvent.showText(accent("Set ___ on ___", key.id(), profile.name())))
              .clickEvent(ClickEvent.runCommand(String.format("/nope host %s edit setting %s value -e setall",
                  profile.name(), key.id())))));
        }
        if (!value.additive().isEmpty() && !value.additive().isFull()) {
          line.append(TWO_SPACES.append(Component.text("some").color(GOLD)));
        } else {
          line.append(TWO_SPACES.append(Component.text("some")
              .color(INFO)
              .hoverEvent(HoverEvent.showText(Component.text("Set some values using the buttons below")))));
        }
        secondLine = TWO_SPACES.append(Component.text("[").color(DARK))
            .append(Component.text("D").color(GOLD).decorate(TextDecoration.BOLD))
            .append(Component.text("/").color(DULL))
            .append(Component.text("M").color(INFO).decorate(TextDecoration.UNDERLINED)
                .hoverEvent(HoverEvent.showText(accent("Change value type of ___ to ___",
                    key.id(),
                    "Manipulative")))
                .clickEvent(ClickEvent.suggestCommand(String.format("/nope host %s edit setting "
                        + "%s value -e --additive set ",
                    profile.name(), key.id()))))
            .append(Component.text("]").color(DARK))
            .append(TWO_SPACES)
            .append(Component.text("[").color(DARK))
            .append(Component.text("+").color(SUCCESS)
                .hoverEvent(HoverEvent.showText(accent("Add values to ___ on ___",
                    key.id(),
                    profile.name())))
                .clickEvent(ClickEvent.suggestCommand(String.format("/nope host "
                        + "%s edit setting %s value -e add ",
                    profile.name(), key.id()))))
            .append(Component.text("]").color(DARK))
            .append(Component.text("[").color(DARK))
            .append(Component.text("-").color(ERROR)
                .hoverEvent(HoverEvent.showText(accent("Remove values from ___ on ___",
                    key.id(),
                    profile.name())))
                .clickEvent(ClickEvent.suggestCommand(String.format("/nope host %s edit setting "
                        + "%s value -e remove ",
                    profile.name(), key.id()))))
            .append(Component.text("]").color(DARK));
      } else {
        if (value.subtractive().isFull()) {
          line.append(TWO_SPACES.append(Component.text("none").color(GOLD)));
        } else {
          line.append(TWO_SPACES.append(Component.text("none")
              .color(INFO)
              .decorate(TextDecoration.UNDERLINED)
              .hoverEvent(HoverEvent.showText(accent("Set ___ on ___", key.id(), profile.name())))
              .clickEvent(ClickEvent.runCommand(String.format("/nope host %s edit setting "
                      + "%s value -e setnone",
                  profile.name(), key.id())))));
        }
        if (value.additive().isFull()) {
          line.append(TWO_SPACES.append(Component.text("all").color(GOLD)));
        } else {
          line.append(TWO_SPACES.append(Component.text("all")
              .color(INFO)
              .decorate(TextDecoration.UNDERLINED)
              .hoverEvent(HoverEvent.showText(accent("Set ___ on ___", key.id(), profile.name())))
              .clickEvent(ClickEvent.runCommand(String.format("/nope host %s edit setting "
                      + "%s value -e setall",
                  profile.name(), key.id())))));
        }
        if (!value.subtractive().isFull() && !value.additive().isFull()) {
          line.append(TWO_SPACES.append(Component.text("some").color(GOLD)));
        } else {
          line.append(TWO_SPACES.append(Component.text("some")
              .color(INFO)
              .hoverEvent(HoverEvent.showText(Component.text("Set some values using the buttons below")))));
        }
        secondLine = TWO_SPACES
            .append(Component.text("[").color(DARK))
            .append(Component.text("D").color(INFO).decorate(TextDecoration.UNDERLINED)
                .hoverEvent(HoverEvent.showText(accent("Change value type of ___ to ___",
                    key.id(),
                    "Declarative")))
                .clickEvent(ClickEvent.runCommand(String.format("/nope host %s edit setting "
                        + "%s value -e setdefault",
                    profile.name(), key.id()))))
            .append(Component.text("/").color(DULL))
            .append(Component.text("M").color(GOLD).decorate(TextDecoration.BOLD))
            .append(Component.text("]").color(DARK))
            .append(SPACE)
            .append(Component.text("add"))
            .append(SPACE)
            .append(Component.text("[").color(DARK))
            .append(Component.text("+").color(SUCCESS)
                .hoverEvent(HoverEvent.showText(accent("Add values to ___ set of ___ on ___",
                    "additive", key.id(),
                    profile.name())))
                .clickEvent(ClickEvent.suggestCommand(String.format("/nope host %s edit setting "
                        + "%s value -e --additive add ",
                    profile.name(), key.id()))))
            .append(Component.text("]").color(DARK))
            .append(Component.text("[").color(DARK))
            .append(Component.text("-").color(ERROR)
                .hoverEvent(HoverEvent.showText(accent("Remove values from ___ set of ___ on ___",
                    "additive",
                    key.id(),
                    profile.name())))
                .clickEvent(ClickEvent.suggestCommand(String.format("/nope host %s edit setting "
                        + "%s value -e --additive remove ",
                    profile.name(), key.id()))))
            .append(Component.text("]").color(DARK))
            .append(Component.text("[").color(DARK))
            .append(Component.text("x").color(WARN)
                .hoverEvent(HoverEvent.showText(accent("Clear all from ___ set of ___ on ___",
                    "additive",
                    key.id(),
                    profile.name())))
                .clickEvent(ClickEvent.runCommand(String.format("/nope host %s edit setting "
                        + "%s value -e --additive setnone ",
                    profile.name(), key.id()))))
            .append(Component.text("]").color(DARK))
            .append(Component.text("[").color(DARK))
            .append(Component.text("o").color(WHITE)
                .hoverEvent(HoverEvent.showText(accent("Set all possible values in ___ set of ___ on ___",
                    "additive",
                    key.id(),
                    profile.name())))
                .clickEvent(ClickEvent.runCommand(String.format("/nope host %s edit setting "
                        + "%s value -e --additive setall ",
                    profile.name(), key.id()))))
            .append(Component.text("]").color(DARK))
            .append(SPACE)
            .append(Component.text("sub"))
            .append(SPACE)
            .append(Component.text("[").color(DARK))
            .append(Component.text("+").color(SUCCESS)
                .hoverEvent(HoverEvent.showText(accent("Add values to ___ set of ___ on ___",
                    "subtractive",
                    key.id(),
                    profile.name())))
                .clickEvent(ClickEvent.suggestCommand(String.format("/nope host %s edit setting "
                        + "%s value -e --subtractive add ",
                    profile.name(), key.id()))))
            .append(Component.text("]").color(DARK))
            .append(Component.text("[").color(DARK))
            .append(Component.text("-").color(ERROR)
                .hoverEvent(HoverEvent.showText(accent("Remove values from ___ set of ___ on ___",
                    "subtractive",
                    key.id(),
                    profile.name())))
                .clickEvent(ClickEvent.suggestCommand(String.format("/nope host %s edit setting "
                        + "%s value -e --subtractive remove ",
                    profile.name(), key.id()))))
            .append(Component.text("]").color(DARK))
            .append(Component.text("[").color(DARK))
            .append(Component.text("x").color(WARN)
                .hoverEvent(HoverEvent.showText(accent("Clear all from ___ set of ___ on ___",
                    "subtractive",
                    key.id(),
                    profile.name())))
                .clickEvent(ClickEvent.runCommand(String.format("/nope host %s edit setting "
                        + "%s value -e --subtractive setnone",
                    profile.name(), key.id()))))
            .append(Component.text("]").color(DARK))
            .append(Component.text("[").color(DARK))
            .append(Component.text("o").color(WHITE)
                .hoverEvent(HoverEvent.showText(accent("Set all possible values in ___ set of ___ on ___",
                    "subtractive",
                    key.id(),
                    profile.name())))
                .clickEvent(ClickEvent.runCommand(String.format("/nope host %s edit setting "
                        + "%s value -e --subtractive setall",
                    profile.name(), key.id()))))
            .append(Component.text("]").color(DARK));
      }
    } else {
      line.append(SPACE.append(Component.text("unset").color(DARK)));
      line.append(TWO_SPACES.append(Component.text("set")
          .color(INFO)
          .decorate(TextDecoration.UNDERLINED)
          .hoverEvent(HoverEvent.showText(accent("Set ___ on ___", key.id(), profile.name())))
          .clickEvent(ClickEvent.runCommand(String.format("/nope host %s edit setting %s value -e setdefault",
              profile.name(), key.id())))));
    }
    if (secondLine == null) {
      return Collections.singletonList(line.build());
    } else {
      List<Component> list = new LinkedList<>();
      list.add(line.build());
      list.add(secondLine);
      return list;
    }
  }

  /**
   * Cast an object to a component. It can either be a {@link Component}
   * or a {@link String}.
   *
   * @param o the object to cast
   * @return the text component
   */
  public static Component castToComponent(Object o) {
    if (o instanceof Component) {
      return (Component) o;
    } else if (o instanceof String) {
      return Component.text((String) o);
    } else {
      throw new IllegalArgumentException("Unknown type tried to cast to Component: "
          + o.getClass().getSimpleName());
    }
  }

}
