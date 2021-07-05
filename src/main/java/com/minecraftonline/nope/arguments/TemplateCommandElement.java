/*
 * MIT License
 *
 * Copyright (c) 2021 MinecraftOnline
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

package com.minecraftonline.nope.arguments;

import com.minecraftonline.nope.setting.SettingMap;
import com.minecraftonline.nope.setting.Template;
import com.minecraftonline.nope.setting.Templates;
import java.util.List;
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
 * A command element to identify a {@link Template}
 * from an argument.
 */
public class TemplateCommandElement extends CommandElement {
  public TemplateCommandElement(@Nullable Text key) {
    super(key);
  }

  @Nullable
  @Override
  protected SettingMap parseValue(@Nonnull CommandSource source, CommandArgs args)
      throws ArgumentParseException {
    String name = args.next();
    SettingMap template = Templates.get(name);
    if (template == null) {
      throw new ArgumentParseException(Text.of("There is no template named " + name),
          name,
          0);
    }
    return template;
  }

  @Nonnull
  @Override
  public List<String> complete(@Nonnull CommandSource src,
                               @Nonnull CommandArgs args,
                               @Nonnull CommandContext context) {
    final Predicate<String> startsWith = new StartsWithPredicate(args.nextIfPresent().orElse(""));
    return Templates.getMap()
        .keySet()
        .stream()
        .filter(startsWith)
        .collect(Collectors.toList());
  }
}
