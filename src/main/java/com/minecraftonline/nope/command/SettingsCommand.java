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
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SettingsCommand extends LambdaCommandNode {

  SettingsCommand(CommandNode parent) {
    super(parent,
        Permissions.COMMAND_SETTING,
        Text.of("List all Nope settings"),
        "settings");
    addCommandElements(GenericArguments.optional(GenericArguments.string(Text.of("regex"))));
    setExecutor((src, args) -> {
      PaginationService pagination = Sponge.getServiceManager().provide(PaginationService.class)
          .orElseThrow(() -> new RuntimeException("PaginationService unavailable."));

      List<SettingKey<?>> keys = SettingLibrary.getAll()
          .stream()
          .filter(key -> !args.hasAny("regex")
              || Pattern.compile(args.requireOne("regex")).matcher(key.getId()).find())
          .sorted(Comparator
              .comparing((SettingKey<?> key) -> key.getCategory().name())
              .thenComparing(SettingKey::getId))
          .collect(Collectors.toList());

      List<Text> contents = Lists.newLinkedList();
      if (keys.size() > 0) {

        contents.add(Text.of(TextColors.AQUA, keys.get(0).getCategory().name().toUpperCase()));
        contents.add(Format.settingKey(keys.get(0), true));
        for (int i = 1; i < keys.size(); i++) {
          if (!keys.get(i).getCategory().equals(keys.get(i - 1).getCategory())) {
            contents.add(Text.of(TextColors.AQUA, keys.get(i).getCategory().name().toUpperCase()));
          }
          contents.add(Format.settingKey(keys.get(i), true));
        }

        pagination.builder()
            .title(Text.of("Nope Settings"))
            .padding(Text.of(TextColors.DARK_GREEN, "="))
            .contents(contents)
            .build()
            .sendTo(src);
      }

      return CommandResult.success();
    });
  }

}
