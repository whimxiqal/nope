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

package com.minecraftonline.nope.sponge.util;

import com.google.common.collect.Lists;
import com.minecraftonline.nope.common.Nope;
import com.minecraftonline.nope.common.host.Host;
import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.common.setting.Setting;
import com.minecraftonline.nope.common.setting.SettingCollection;
import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.common.setting.SettingValue;
import com.minecraftonline.nope.common.setting.Target;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.permission.Subject;

/**
 * A utility class to store static fields and methods pertaining to
 * formatted messages with the purpose of sending meaningfully colored
 * and enhanced messages to players and other message receivers.
 */
public final class Formatter {

  public static final String FORMAT_ACCENT_REGEX = "___";
  public static final TextColor THEME = TextColor.color(85, 53, 166);
  public static final TextColor ACCENT = TextColor.color(7, 236, 186);
  public static final TextColor SEPARATOR = TextColor.color(64, 64, 64);
  public static final TextColor SUCCESS = TextColor.color(11, 181, 38);
  public static final TextColor ERROR = TextColor.color(194, 12, 21);
  public static final TextColor WARN = TextColor.color(255, 157, 10);
  public static final TextColor INFO = TextColor.color(255, 191, 245);
  public static final TextColor DULL = TextColor.color(179, 179, 179);
  public static final TextColor URL = TextColor.color(0, 57, 214);
  public static final TextColor GOLD = TextColor.color(222, 185, 0);
  public static final TextColor WHITE = TextColor.color(255, 255, 255);

  public static final Component SPACE = Component.text(" ");


  public static Component prefix() {
    return Component.text()
        .append(Component.text("Nope ").color(THEME))
        .append(Component.text("- ").color(SEPARATOR))
        .build();
  }

  private static Component formattedMessage(TextColor color, String message, boolean prefix, Object... accented) {
    LinkedList<String> tokenList = new LinkedList<>(Arrays.asList(message.split(FORMAT_ACCENT_REGEX)));

    // Make sure that things can be inserted at the beginning and end (split doesn't cover those cases)
//    if (message.startsWith(FORMAT_ACCENT_REGEX)) {
//      tokenList.addFirst("");
//    }
    if (message.endsWith(FORMAT_ACCENT_REGEX)) {
      tokenList.addLast("");
    }

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


  public static Component keyValue(TextColor keyColor, String key, Component value) {
    return Component.text()
        .append(Component.text(key).color(keyColor))
        .append(Component.text(" "))
        .append(value)
        .build();
  }


  public static Component hover(String label, String onHover) {
    return hover(Component.text(label).decorate(TextDecoration.ITALIC),
        Component.text(onHover));
  }


  public static Component hover(Component label, Component onHover) {
    return label.hoverEvent(HoverEvent.showText(onHover));
  }


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
      builder.hoverEvent(HoverEvent.showText(hoverMessage.append(Component.text("\n" + command))));
    }

    return builder.build();
  }


  public static Component host(@NotNull Host host) {
    String name = host.name();
    return command(
        name,
        "/nope",
        Component.text("Click for more details about this zone"),
        false,
        false
    );
  }


  public static <T, V extends SettingValue<T>> Component settingKey(SettingKey<T, V> key, boolean verbose) {
    TextComponent.Builder idText = Component.text().append(Component.text(key.id()).color(ACCENT));

    TextComponent.Builder hoverText = Component.text()
        .append(Component.text(key.id()).color(ACCENT))
        .append(Component.newline());

    if (!key.functional()) {
      idText.decorate(TextDecoration.STRIKETHROUGH);
      hoverText.append(Component.text("Not implemented yet!").color(ERROR));
      hoverText.append(Component.newline());
    }

    hoverText.append(keyValue("Type:", key.manager().valueType().getSimpleName()));
    hoverText.append(Component.newline());

    String defaultData = key.manager().printValue(key.defaultValue());
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


  public static <T, V extends SettingValue<T>> CompletableFuture<List<Component>> setting(Setting<T, V> setting,
                                                               Subject subject,
                                                               @NotNull SettingCollection collection) {
    return CompletableFuture.supplyAsync(() -> {
      TextComponent.Builder main = Component.text();

      String dataString = setting.value() == null
          ? "(Empty)"
          : setting.key().manager().printValue(setting.value());
      if (collection instanceof Host) {
        Host host = (Host) collection;
        Host redundancy = Nope.instance().hostSystem().findIdenticalSuperior(host, setting.key())
            .orElse(null);
        main.append(settingKey(setting.key(), false),
            Component.text(" = ").append(settingValue(
                Component.text(dataString),
                collection.equals(redundancy),
                redundancy)));
      } else {
        main.append(settingKey(setting.key(), false),
            Component.text(" = ").append(Component.text(dataString)));
      }

      List<Component> list = Lists.newLinkedList();

      list.add(main.build());

      if (setting.target() != null) {
        Target target = setting.requireTarget();
        if (!target.users().isEmpty()) {
          list.add(Component.text(" > ").color(SUCCESS)
              .append(keyValue(target.isWhitelist() ? "Whitelist:" : "Blacklist:",
                  target.users()
                      .stream()
                      .map(uuid -> {
                        try {
                          return Sponge.server().gameProfileManager()
                              .profile(uuid)
                              .get().name().orElseThrow(() ->
                                  new RuntimeException("Failed to get user profile name "
                                      + "for UUID: " + uuid));
                        } catch (InterruptedException | ExecutionException e) {
                          e.printStackTrace();
                          return "";
                        }
                      })
                      .filter(s -> !s.isEmpty())
                      .collect(Collectors.joining(", ")))));
        }
        target.permissions().forEach((permission, value) ->
            list.add(Component.text(" > ").color(SUCCESS)
                .append(keyValue(permission + ":", String.valueOf(value)))));
        if (target.isIndiscriminate()) {
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

}
