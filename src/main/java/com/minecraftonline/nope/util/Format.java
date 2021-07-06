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

package com.minecraftonline.nope.util;

import com.google.common.collect.Lists;
import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.command.InfoCommand;
import com.minecraftonline.nope.command.SetCommand;
import com.minecraftonline.nope.command.TargetAddCommand;
import com.minecraftonline.nope.command.UnsetCommand;
import com.minecraftonline.nope.host.Host;
import com.minecraftonline.nope.permission.Permissions;
import com.minecraftonline.nope.setting.Setting;
import com.minecraftonline.nope.setting.SettingKey;
import com.minecraftonline.nope.setting.SettingValue;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

/**
 * A utility class to store static fields and methods pertaining to
 * formatted messages with the purpose of sending meaningfully colored
 * and enhanced messages to players and other message receivers.
 */
public final class Format {

  public static final TextColor THEME = TextColors.GRAY;
  public static final TextColor ACCENT = TextColors.LIGHT_PURPLE;

  private Format() {
  }

  public static Text prefix() {
    return Text.of(THEME, "Nope ", TextColors.DARK_GRAY, "-=- ");
  }

  public static Text success(Object... message) {
    return Text.of(prefix(), TextColors.GREEN, Text.of(message));
  }

  public static Text error(Object... message) {
    return Text.of(prefix(), TextColors.RED, Text.of(message));
  }

  public static Text warn(Object... message) {
    return Text.of(prefix(), TextColors.YELLOW, Text.of(message));
  }

  public static Text info(Object... message) {
    return Text.of(prefix(), TextColors.WHITE, Text.of(message));
  }

  public static Text note(Object... message) {
    return Text.of(TextColors.GRAY, Text.of(message));
  }

  public static Text keyValue(String key, String value) {
    return keyValue(key, Text.of(value));
  }

  public static Text keyValue(String key, Text value) {
    return keyValue(ACCENT, key, value);
  }

  public static Text keyValue(TextColor keyColor, String key, String value) {
    return keyValue(keyColor, key, Text.of(value));
  }

  public static Text keyValue(TextColor keyColor, String key, Text value) {
    return Text.of(keyColor, key, " ", Format.note(value));
  }

  public static Text hover(String label, String onHover) {
    return hover(Text.of(TextStyles.ITALIC, label), Format.note(onHover));
  }

  /**
   * Generate a text object which displays text when hovered over in chat.
   *
   * @param label   the visible label shown in chat
   * @param onHover the text to show when hovered over
   * @return the entire text component
   */
  public static Text hover(Text label, Text onHover) {
    return Text.builder()
        .append(label)
        .onHover(TextActions.showText(onHover))
        .build();
  }

  /**
   * Generate a text object which opens a url when clicked.
   *
   * @param label the label to show when shown in chat
   * @param url   the url to visit
   * @return the entire text objectSTm
   */
  public static Text url(@Nonnull String label, @Nonnull String url) {
    Text.Builder textBuilder = Text.builder();
    textBuilder.append(Text.of(TextColors.BLUE, label));
    textBuilder.onHover(TextActions.showText(Text.of(url)));
    try {
      textBuilder.onClick(TextActions.openUrl(new URL(url)));
    } catch (MalformedURLException ex) {
      textBuilder.onClick(TextActions.suggestCommand(url));
      Nope.getInstance().getLogger().error("A url was not formed correctly for a"
          + " click action: " + url);
    }

    return textBuilder.build();
  }

  /**
   * Same as {@link #command(String, String, Text, boolean, boolean)},
   * but specifically accentuated and with suggestion.
   *
   * @param label        the text to show in chat
   * @param command      the command to run
   * @param hoverMessage the text to show when hovered over
   * @return the text object
   */
  public static Text commandSuggest(@Nonnull String label,
                                    @Nonnull String command,
                                    @Nullable Text hoverMessage) {
    return command(label, command, hoverMessage, true, true);
  }

  /**
   * Same as {@link #command(String, String, Text, boolean, boolean)},
   * but specifically accentuated and without suggestion.
   *
   * @param label        the text to show in chat
   * @param command      the command to run
   * @param hoverMessage the text to show when hovered over
   * @return the text object
   */
  public static Text command(@Nonnull String label,
                             @Nonnull String command,
                             @Nullable Text hoverMessage) {
    return command(label, command, hoverMessage, true, false);
  }

  /**
   * Generates a text object to help players run commands by clicking
   * on a text label in chat.
   *
   * @param label        the text to show in chat
   * @param command      the command to run
   * @param hoverMessage the text to show when hovered over
   * @param accentuate   if true, the label is accentuated in chat
   * @param suggest      if true, the command will only be suggested in the
   *                     player's chat window instead of being run immediately.
   *                     This is useful if there are further user inputs
   *                     required if the command requires it.
   * @return the text object.
   */
  public static Text command(@Nonnull String label,
                             @Nonnull String command,
                             @Nullable Text hoverMessage,
                             boolean accentuate,
                             boolean suggest) {
    Text labelText = accentuate
        ? Text.of(TextColors.GOLD, TextStyles.ITALIC, "[",
        Text.of(TextColors.LIGHT_PURPLE, label.toUpperCase()), "]")
        : Text.of(Text.of(Format.ACCENT, label));

    Text.Builder builder = Text.builder()
        .append(labelText)
        .onClick(suggest
            ? TextActions.suggestCommand(command)
            : TextActions.runCommand(command));

    if (hoverMessage != null) {
      builder.onHover(TextActions.showText(Text.of(
          hoverMessage,
          hoverMessage.isEmpty() ? Text.EMPTY : "\n",
          Format.note(command))));
    }
    return builder.build();
  }

  /**
   * Display a {@link Host} with the functionality
   * to run the info command on the host if the text is clicked.
   *
   * @param host the host
   * @return the text object
   */
  public static Text host(@Nonnull Host host) {
    String name = host.getName();
    return Format.command(
        name,
        Nope.getInstance()
            .getCommandTree()
            .findNode(InfoCommand.class)
            .orElseThrow(() ->
                new RuntimeException("Info command is not part of the command tree"))
            .getFullCommand() + " " + name,
        Text.of("Click for more details about this zone"),
        false,
        false
    );
  }

  /**
   * Display a {@link SettingKey}. Most important info is available
   * upon hovering over the text label.
   *
   * @param key     the key
   * @param verbose true to show the entire the description of the setting key
   *                or false to get just a short "blurb"
   * @param <T>     the type of value stored alongside the setting key
   * @return the generated text object
   */
  public static <T> Text settingKey(SettingKey<T> key, boolean verbose) {
    Text.Builder builder = Text.builder();

    Text.Builder idText = Text.builder().append(Text.of(Format.ACCENT, key.getId()));

    Text.Builder onHover = Text.builder()
        .append(Text.of(TextColors.AQUA, key.getId()))
        .append(Text.NEW_LINE);

    if (!key.isImplemented()) {
      idText.style(TextStyles.STRIKETHROUGH);
      onHover.append(Text.of(TextColors.RED, "Not implemented yet!"));
      onHover.append(Text.NEW_LINE);
    }

    onHover.append(Format.keyValue("Type:", key.valueType().getSimpleName()));
    onHover.append(Text.NEW_LINE);

    Text defaultData = key.print(key.getDefaultData());
    onHover.append(Format.keyValue("Default value:", defaultData.isEmpty()
        ? Text.of("(Empty)")
        : defaultData));
    onHover.append(Text.NEW_LINE);

    onHover.append(Format.keyValue("Restrictive:", String.valueOf(key.isPlayerRestrictive())));
    onHover.append(Text.NEW_LINE);

    onHover.append(Format.keyValue("Category:", key.getCategory().name().toLowerCase()));

    if (key.getDescription() != null) {
      onHover.append(Text.NEW_LINE).append(Text.NEW_LINE);
      onHover.append(Text.of(TextColors.WHITE, key.getDescription()));
    }

    builder.onHover(TextActions.showText(onHover.build()));

    builder.append(idText.build());
    if (verbose) {
      builder.append(Text.of(" ", key.getBlurb() == null
          ? (key.getDescription() == null ? "No description" : key.getDescription())
          : key.getBlurb()));
    }

    return builder.build();
  }

  /**
   * Display a {@link SettingValue}.
   *
   * @param value                the text version of the {@link SettingValue}
   * @param redundantOnDefault   true if the setting value is redundant because
   *                             the value is set to its setting's default value
   * @param redundancyController the host on which the setting value is set which
   *                             causes the setting to be redundant
   * @return the text object
   */
  private static Text settingValue(Text value,
                                   boolean redundantOnDefault,
                                   Host redundancyController) {
    Text.Builder builder = Text.builder();
    if (redundancyController != null) {
      // Redundant
      builder.append(Text.of(TextColors.GRAY, TextStyles.STRIKETHROUGH, value));
      if (redundantOnDefault) {
        builder.onHover(TextActions.showText(Text.of(
            "This setting is redundant because it is the default value,"
                + " so this setting serves no purpose.")));
      } else {
        builder.onHover(TextActions.showText(Text.of("This setting is redundant because host ",
            Format.host(redundancyController),
            " has the same setting, so this setting serves no purpose.")));
      }
    } else {
      builder.append(Text.of(TextColors.WHITE, value));
    }
    return builder.build();
  }

  /**
   * Generates a {@link CompletableFuture} to supply a list of text
   * which dictates information about a {@link Setting}, which is
   * the combination of its {@link SettingKey} and its {@link SettingValue}.
   *
   * @param setting              the setting
   * @param subject              the subject which requests the information.
   *                             This controls which commands are suggested for each setting.
   * @param host                 the host it is requested from
   * @param redundancyController the host which was found to be the controlling host
   *                             for a redundant setting value on the given host
   *                             for the key within the given setting.
   *                             This is only used to notify the requester of the
   *                             redundancy of the setting and has no further function.
   * @param <T>                  the type of value stored in the setting
   * @return the completable future to return the setting as a list of text
   */
  public static <T> CompletableFuture<List<Text>> setting(Setting<T> setting,
                                                          Subject subject,
                                                          @Nonnull Host host,
                                                          @Nullable Host redundancyController) {
    return CompletableFuture.supplyAsync(() -> {
      Text.Builder main = Text.builder();

      /* Unset Button */
      UnsetCommand unsetCommand = Nope.getInstance().getCommandTree()
          .findNode(UnsetCommand.class)
          .orElseThrow(() ->
              new RuntimeException("UnsetCommand is not set in Nope command tree!"));
      if (unsetCommand.hasPermission(subject)) {
        main.append(Format.command("UNSET",
            unsetCommand.getFullCommand() + String.format(" -z %s %s",
                host.getName(),
                setting.getKey()),
            Text.of("Unset the value of this setting on this host"),
            true,
            true)).append(Text.of(" "));
      }

      /* Set Button */
      SetCommand setCommand = Nope.getInstance().getCommandTree()
          .findNode(SetCommand.class)
          .orElseThrow(() ->
              new RuntimeException("SetCommand is not set in Nope command tree!"));
      if (unsetCommand.hasPermission(subject)) {
        main.append(Format.command("SET",
            setCommand.getFullCommand() + String.format(" -z %s %s ___",
                host.getName(),
                setting.getKey()),
            Text.of("Set this setting on this host with a value"),
            true,
            true)).append(Text.of(" "));
      }

      /* Add Target Button */
      TargetAddCommand targetAddCommand = Nope.getInstance().getCommandTree()
          .findNode(TargetAddCommand.class)
          .orElseThrow(() ->
              new RuntimeException("TargetAddCommand is not set in Nope command tree!"));
      if (targetAddCommand.hasPermission(subject)) {
        main.append(Format.command("ADD",
            targetAddCommand.getFullCommand() + String.format(" ___ -z %s %s ___",
                host.getName(),
                setting.getKey()),
            Text.of("Add a target condition to this host"),
            true,
            true)).append(Text.of(" "));
      }

      Text data = setting.getKey().print(setting.getValue().getData());
      main.append(Format.settingKey(setting.getKey(), false),
          Text.of(" = ", Format.settingValue(data.isEmpty() ? Text.of("(Empty)") : data,
              host.equals(redundancyController),
              redundancyController)));

      List<Text> list = Lists.newLinkedList();

      list.add(main.build());

      if (setting.getValue().getTarget() != null) {
        SettingValue.Target target = setting.getValue().getTarget();
        if (!target.getUsers().isEmpty()) {
          list.add(Text.of(TextColors.GREEN,
              " > ",
              Format.keyValue(target.hasWhitelist() ? "Whitelist:" : "Blacklist:",
                  target.getUsers()
                      .stream()
                      .map(uuid -> {
                        try {
                          return Sponge.getServer().getGameProfileManager()
                              .get(uuid)
                              .get().getName().orElseThrow(() ->
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
            list.add(Text.of(TextColors.GREEN,
                " > ",
                Format.keyValue(permission + ":", String.valueOf(value)))));
        if (target.isForceAffect()) {
          list.add(Text.of(TextColors.GREEN,
              " > ",
              Format.hover("FORCE AFFECT",
                  "When affect is forced, players with the "
                      + Permissions.UNRESTRICTED.get()
                      + " permission may still be targeted")));
        }
      }
      return list;
    });
  }

}
