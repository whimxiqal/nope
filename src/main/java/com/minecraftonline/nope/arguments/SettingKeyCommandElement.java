package com.minecraftonline.nope.arguments;

import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.setting.SettingKey;
import com.minecraftonline.nope.setting.SettingLibrary;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.StartsWithPredicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
        .map(SettingKey::getId)
        .filter(startsWith)
        .collect(Collectors.toList());
  }
}
