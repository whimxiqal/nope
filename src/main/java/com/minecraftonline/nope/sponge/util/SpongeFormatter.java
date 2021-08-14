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
import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.common.util.Formatter;
import com.minecraftonline.nope.sponge.SpongeNope;
import com.minecraftonline.nope.sponge.command.InfoCommand;
import com.minecraftonline.nope.sponge.command.SetCommand;
import com.minecraftonline.nope.sponge.command.TargetAddCommand;
import com.minecraftonline.nope.sponge.command.UnsetCommand;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.util.HSVLike;
import org.spongepowered.api.Sponge;

/**
 * A utility class to store static fields and methods pertaining to
 * formatted messages with the purpose of sending meaningfully colored
 * and enhanced messages to players and other message receivers.
 */
public final class SpongeFormatter implements Formatter<Component, TextColor> {

  public static final String FORMAT_ACCENT_REGEX = "___";
  public static final TextColor THEME = TextColor.color(HSVLike.of(257, 68, 65));
  public static final TextColor ACCENT = TextColor.color(HSVLike.of(344, 68, 65));
  public static final TextColor SEPARATOR = TextColor.color(HSVLike.of(0, 0, 25));
  public static final TextColor SUCCESS = TextColor.color(HSVLike.of(139, 99, 71));
  public static final TextColor ERROR = TextColor.color(HSVLike.of(357, 94, 76));
  public static final TextColor WARN = TextColor.color(HSVLike.of(36, 96, 100));
  public static final TextColor INFO = TextColor.color(HSVLike.of(171, 41, 100));
  public static final TextColor DULL = TextColor.color(HSVLike.of(0, 0, 70));
  public static final TextColor URL = TextColor.color(HSVLike.of(224, 100, 84));
  public static final TextColor GOLD = TextColor.color(HSVLike.of(50, 100, 87));
  public static final TextColor WHITE = TextColor.color(HSVLike.of(0, 0, 100));

  public static final Component SPACE = Component.text(" ");

  @Override
  public Component prefix() {
    return Component.text()
        .append(Component.text("Nope ").color(THEME))
        .append(Component.text("-=- ").color(SEPARATOR))
        .build();
  }

  private Component formattedMessage(TextColor color, String message, boolean prefix, Object... accented) {
    String[] tokens = message.split(FORMAT_ACCENT_REGEX);
    TextComponent.Builder builder = Component.text();
    if (prefix) {
      builder.append(prefix());
    }
    for (int i = 0; i < tokens.length - 1; i++) {
      builder.append(Component.text(tokens[i]).color(color));
      if (accented[i] instanceof Component) {
        builder.append((Component) accented[i]);
      } else {
        builder.append(Component.text(accented[i].toString()).color(ACCENT));
      }
    }
    builder.append(Component.text(tokens[tokens.length - 1]).color(color));
    return builder.build();
  }

  @Override
  public Component success(String message) {
    return success(Component.text(message));
  }

  @Override
  public Component success(String message, Object... insertions) {
    return formattedMessage(SUCCESS, message, true, insertions);
  }

  @Override
  public Component success(Component message) {
    return prefix().append(message).color(SUCCESS);
  }

  @Override
  public Component error(String message) {
    return error(Component.text(message));
  }

  @Override
  public Component error(String message, Object... insertions) {
    return formattedMessage(ERROR, message, true, insertions);
  }

  @Override
  public Component error(Component message) {
    return prefix().append(message).color(ERROR);
  }

  @Override
  public Component warn(String message) {
    return warn(Component.text(message));
  }

  @Override
  public Component warn(String message, Object... insertions) {
    return formattedMessage(SUCCESS, message, true, insertions);
  }

  @Override
  public Component warn(Component message) {
    return prefix().append(message).color(WARN);
  }

  @Override
  public Component info(String message) {
    return info(Component.text(message));
  }

  @Override
  public Component info(String message, Object... insertions) {
    return formattedMessage(SUCCESS, message, true, insertions);
  }

  @Override
  public Component info(Component message) {
    return prefix().append(message).color(INFO);
  }

  @Override
  public Component accent(String message) {
    return Component.text(message).color(ACCENT);
  }

  @Override
  public Component accent(String message, Object... insertions) {
    return formattedMessage(WHITE, message, false, insertions);
  }

  @Override
  public Component keyValue(String key, String value) {
    return keyValue(key, Component.text(value));
  }

  @Override
  public Component keyValue(String key, Component value) {
    return keyValue(ACCENT, key, value);
  }

  @Override
  public Component keyValue(TextColor keyColor, String key, String value) {
    return keyValue(keyColor, key, Component.text(value));
  }

  @Override
  public Component keyValue(TextColor keyColor, String key, Component value) {
    return Component.text()
        .append(Component.text(key).color(keyColor))
        .append(Component.text(" "))
        .append(value)
        .build();
  }

  @Override
  public Component hover(String label, String onHover) {
    return hover(Component.text(label).decorate(TextDecoration.ITALIC),
        Component.text(onHover));
  }

  @Override
  public Component hover(Component label, Component onHover) {
    return label.hoverEvent(HoverEvent.showText(onHover));
  }

  @Override
  public Component url(@Nonnull String label, @Nonnull String url) {
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

  @Override
  public Component commandSuggest(@Nonnull String label,
                                  @Nonnull String command,
                                  @Nullable Component hoverMessage) {
    return command(label, command, hoverMessage, true, true);
  }

  @Override
  public Component command(@Nonnull String label,
                           @Nonnull String command,
                           @Nullable Component hoverMessage) {
    return command(label, command, hoverMessage, true, false);
  }

  @Override
  public Component command(@Nonnull String label,
                           @Nonnull String command,
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

  @Override
  public Component host(@Nonnull Host<?> host) {
    String name = host.getName();
    return command(
        name,
        SpongeNope.instance()
            .getCommandTree()
            .findNode(InfoCommand.class)
            .orElseThrow(() ->
                new RuntimeException("Info command is not part of the command tree"))
            .getFullCommand() + " " + name,
        Component.text("Click for more details about this zone"),
        false,
        false
    );
  }

  @Override
  public <T> Component settingKey(SettingKey<T> key, boolean verbose) {
    TextComponent.Builder idText = Component.text().append(Component.text(key.getId()).color(ACCENT));

    TextComponent.Builder hoverText = Component.text()
        .append(Component.text(key.getId()).color(ACCENT))
        .append(NEW_LINE);

    if (!key.isImplemented()) {
      idText.decorate(TextDecoration.STRIKETHROUGH);
      hoverText.append(Component.text("Not implemented yet!").color(ERROR));
      hoverText.append(NEW_LINE);
    }

    hoverText.append(keyValue("Type:", key.valueType().getSimpleName()));
    hoverText.append(NEW_LINE);

    String defaultData = key.print(key.getDefaultData());
    hoverText.append(keyValue("Default value:", defaultData.isEmpty()
        ? "(Empty)"
        : defaultData));
    hoverText.append(NEW_LINE);

    hoverText.append(keyValue("Restrictive:", String.valueOf(key.isPlayerRestrictive())));
    hoverText.append(NEW_LINE);

    hoverText.append(keyValue("Category:", key.getCategory().name().toLowerCase()));

    if (key.getDescription() != null) {
      hoverText.append(NEW_LINE).append(NEW_LINE);
      hoverText.append(Component.text(key.getDescription()).color(WHITE));
    }

    TextComponent.Builder builder = Component.text().hoverEvent(HoverEvent.showText(hoverText.build()));

    builder.append(idText.build());
    if (verbose) {
      builder.append(SPACE)
          .append(Component.text(
              key.getBlurb() == null
                  ? (key.getDescription() == null ? "No description" : key.getDescription())
                  : key.getBlurb()));
    }

    return builder.build();
  }

  @Override
  public <T> CompletableFuture<List<Component>> setting(Setting<T> setting,
                                                        UUID subject,
                                                        @Nonnull Host<?> host,
                                                        @Nullable Host<?> redundancyController) {
    return CompletableFuture.supplyAsync(() -> {
      TextComponent.Builder main = Component.text();

      /* Unset Button */
      UnsetCommand unsetCommand = SpongeNope.instance().getCommandTree()
          .findNode(UnsetCommand.class)
          .orElseThrow(() ->
              new RuntimeException("UnsetCommand is not set in Nope command tree!"));
      if (unsetCommand.hasPermission(subject)) {
        main.append(command("UNSET",
            unsetCommand.getFullCommand() + String.format(" -z %s %s",
                host.getName(),
                setting.getKey()),
            Component.text("Unset the value of this setting on this host"),
            true,
            true)).append(SPACE);
      }

      /* Set Button */
      SetCommand setCommand = SpongeNope.instance().getCommandTree()
          .findNode(SetCommand.class)
          .orElseThrow(() ->
              new RuntimeException("SetCommand is not set in Nope command tree!"));
      if (unsetCommand.hasPermission(subject)) {
        main.append(command("SET",
            setCommand.getFullCommand() + String.format(" -z %s %s ___",
                host.getName(),
                setting.getKey()),
            Component.text("Set this setting on this host with a value"),
            true,
            true)).append(SPACE);
      }

      /* Add Target Button */
      TargetAddCommand targetAddCommand = SpongeNope.instance().getCommandTree()
          .findNode(TargetAddCommand.class)
          .orElseThrow(() ->
              new RuntimeException("TargetAddCommand is not set in Nope command tree!"));
      if (targetAddCommand.hasPermission(subject)) {
        main.append(command("ADD",
            targetAddCommand.getFullCommand() + String.format(" ___ -z %s %s ___",
                host.getName(),
                setting.getKey()),
            Component.text("Add a target condition to this host"),
            true,
            true)).append(SPACE);
      }

      String data = setting.getKey().print(setting.getValue().getData());
      main.append(settingKey(setting.getKey(), false),
          Component.text(" = ").append(settingValue(data.isEmpty()
                  ? Component.text("(Empty)")
                  : Component.text(data),
              host.equals(redundancyController),
              redundancyController)));

      List<Component> list = Lists.newLinkedList();

      list.add(main.build());

      if (setting.getValue().getTarget() != null) {
        SettingValue.Target target = setting.getValue().getTarget();
        if (!target.getUsers().isEmpty()) {
          list.add(Component.text(" > ").color(SUCCESS)
              .append(keyValue(target.hasWhitelist() ? "Whitelist:" : "Blacklist:",
                  target.getUsers()
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
        target.forEach((permission, value) ->
            list.add(Component.text(" > ").color(SUCCESS)
                .append(keyValue(permission + ":", String.valueOf(value)))));
        if (target.isForceAffect()) {
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

  @Override
  public Component settingValue(Component value,
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
