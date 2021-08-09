package com.minecraftonline.nope.common.util;

import com.minecraftonline.nope.common.host.Host;
import com.minecraftonline.nope.common.setting.Setting;
import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.common.setting.SettingValue;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Formatter<T, C> {



  T prefix();

  T success(String message);

  T success(String message, Object... insertions);

  T success(T message);

  T error(String message);

  T error(String message, Object... insertions);

  T error(T message);

  T warn(String message);

  T warn(String message, Object... insertions);

  T warn(T message);

  T info(String message);

  T info(String message, Object... insertions);

  T info(T message);

  T accent(String message);

  T accent(String message, Object... insertions);

  T keyValue(String key, String value);

  T keyValue(String key, T value);

  T keyValue(C keyColor, String key, String value);

  T keyValue(C keyColor, String key, T value);

  T hover(String label, String onHover);

  /**
   * Generate a text object which displays text when hovered over in chat.
   *
   * @param label   the visible label shown in chat
   * @param onHover the text to show when hovered over
   * @return the entire text component
   */
  T hover(T label, T onHover);

  /**
   * Generate a text object which opens a url when clicked.
   *
   * @param label the label to show when shown in chat
   * @param url   the url to visit
   * @return the entire text objectSTm
   */
  T url(@Nonnull String label, @Nonnull String url);

  /**
   * Same as {@link #command(String, String, T, boolean, boolean)},
   * but specifically accentuated and with suggestion.
   *
   * @param label        the text to show in chat
   * @param command      the command to run
   * @param hoverMessage the text to show when hovered over
   * @return the text object
   */
  T commandSuggest(@Nonnull String label,
                   @Nonnull String command,
                   @Nullable T hoverMessage);

  /**
   * Same as {@link #command(String, String, T, boolean, boolean)},
   * but specifically accentuated and without suggestion.
   *
   * @param label        the text to show in chat
   * @param command      the command to run
   * @param hoverMessage the text to show when hovered over
   * @return the text object
   */
  T command(@Nonnull String label,
            @Nonnull String command,
            @Nullable T hoverMessage);

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
  T command(@Nonnull String label,
            @Nonnull String command,
            @Nullable T hoverMessage,
            boolean accentuate,
            boolean suggest);

  /**
   * Display a {@link Host} with the functionality
   * to run the info command on the host if the text is clicked.
   *
   * @param host the host
   * @return the text object
   */
  T host(@Nonnull Host host);

  /**
   * Display a {@link SettingKey}. Most important info is available
   * upon hovering over the text label.
   *
   * @param key     the key
   * @param verbose true to show the entire the description of the setting key
   *                or false to get just a short "blurb"
   * @param <S>     the type of value stored alongside the setting key
   * @return the generated text object
   */
  <S> T settingKey(SettingKey<S> key, boolean verbose);

  /**
   * Generates a {@link CompletableFuture} to supply a list of text
   * which dictates information about a {@link Setting}, which is
   * the combination of its {@link SettingKey} and its {@link SettingValue}.
   *
   * @param setting              the setting
   * @param subject              the uuid of the subject which requests the information.
   *                             This controls which commands are suggested for each setting.
   * @param host                 the host it is requested from
   * @param redundancyController the host which was found to be the controlling host
   *                             for a redundant setting value on the given host
   *                             for the key within the given setting.
   *                             This is only used to notify the requester of the
   *                             redundancy of the setting and has no further function.
   * @param <S>                  the type of value stored in the setting
   * @param <P>                  the type of subject which may have permissions, null if no subject
   * @return the completable future to return the setting as a list of text
   */
  <S> CompletableFuture<List<T>> setting(Setting<S> setting,
                                         @Nullable UUID subject,
                                         @Nonnull Host host,
                                         @Nullable Host redundancyController);

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
  T settingValue(T value,
                 boolean redundantOnDefault,
                 Host redundancyController);
}
