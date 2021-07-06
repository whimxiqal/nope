package com.minecraftonline.nope.sponge.command.general.arguments;

import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.common.setting.SettingLibrary;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.StartsWithPredicate;

/**
 * A {@link CommandElement} which parses a setting key from an argument.
 */
public class SettingKeyCommandElement extends CommandElement {

  protected SettingKeyCommandElement(@Nullable Text key) {
    super(key);
  }

  @Nullable
  @Override
  protected Object parseValue(@Nonnull CommandSource source,
                              CommandArgs args) throws ArgumentParseException {
    String s = args.next();
    try {
      return SettingLibrary.lookup(s);
    } catch (NoSuchElementException e) {
      throw new ArgumentParseException(Text.of("Unknown setting: " + s), s, 0);
    }
  }

  @Nonnull
  @Override
  public List<String> complete(@Nonnull CommandSource src,
                               CommandArgs args,
                               @Nonnull CommandContext context) {
    final Predicate<String> startsWith = new StartsWithPredicate(args.nextIfPresent().orElse(""));
    return SettingLibrary.getAll().stream()
        .filter(SettingKey::isImplemented)
        .map(SettingKey::getId)
        .filter(startsWith)
        .collect(Collectors.toList());
  }
}
