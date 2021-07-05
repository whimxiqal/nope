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

package com.minecraftonline.nope.command;

import com.google.common.collect.Lists;
import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.command.common.LambdaCommandNode;
import com.minecraftonline.nope.permission.Permissions;
import com.minecraftonline.nope.setting.SettingKey;
import com.minecraftonline.nope.setting.SettingLibrary;
import com.minecraftonline.nope.util.Format;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * A command to list all Nope settings.
 */
public class SettingsCommand extends LambdaCommandNode {

  SettingsCommand(CommandNode parent) {
    super(parent,
        Permissions.COMMAND_SETTING,
        Text.of("List all Nope settings"),
        "settings");
    addCommandElements(GenericArguments.optional(GenericArguments.string(Text.of("category"))));
    setExecutor((src, args) -> {

      List<String> categories = Arrays.stream(SettingKey.CategoryType.values())
          .map(type -> type.name().toLowerCase())
          .collect(Collectors.toList());
      categories.add("all");

      PaginationService pagination = Sponge.getServiceManager().provide(PaginationService.class)
          .orElseThrow(() -> new RuntimeException("PaginationService unavailable."));

      PaginationList.Builder builder = pagination.builder();
      List<Text> contents = Lists.newLinkedList();

      if (args.hasAny("category")) {
        Predicate<SettingKey<?>> filter;
        if (args.<String>requireOne("category").equalsIgnoreCase("all")) {
          filter = key -> true;
        } else {
          Optional<SettingKey.CategoryType> categoryType = Arrays.stream(SettingKey.CategoryType.values())
              .filter(type -> type.name().equalsIgnoreCase(args.requireOne("category")))
              .findFirst();
          if (!categoryType.isPresent()) {
            src.sendMessage(Format.error("Valid categories are: ", Text.joinWith(
                Text.of(TextColors.GREEN, ", "),
                categories.stream().map(Format::note).collect(Collectors.toList()))));
            return CommandResult.empty();
          }
          filter = key -> key.getCategory().equals(categoryType.get());
        }
        contents.addAll(SettingLibrary.getAll()
            .stream()
            .filter(filter)
            .sorted(Comparator.comparing(SettingKey::getId))
            .map(key -> Format.settingKey(key, true))
            .collect(Collectors.toList()));

        builder.title(Text.of("Nope Settings - ",
            TextColors.AQUA,
            args.<String>requireOne("category").toUpperCase()));
        builder.header(Text.of(TextColors.DARK_GRAY, "<< ", Format.command("Back to Categories",
            getFullCommand(),
            Text.of("Return to category menu")),
            TextColors.DARK_GRAY, " >>\n-----------------------"));
      } else {
        contents.add(Text.of("Choose a category"));

        contents.addAll(categories.stream()
            .sorted()
            .map(category ->
                Text.of(TextColors.GRAY, "- ", Format.command(category,
                    this.getFullCommand() + " " + category,
                    Text.of("Search settings under this category"))))
            .collect(Collectors.toList()));
        builder.title(Text.of("Nope Settings"));
      }

      builder.padding(Text.of(Format.ACCENT, "="))
          .contents(contents)
          .build()
          .sendTo(src);

      return CommandResult.success();
    });
  }

}
