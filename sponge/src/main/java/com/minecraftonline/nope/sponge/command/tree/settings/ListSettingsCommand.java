/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
 * Copyright (c) MinecraftOnline
 * Copyright (c) contributors
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

package com.minecraftonline.nope.sponge.command.tree.settings;

import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.sponge.SpongeNope;
import com.minecraftonline.nope.sponge.command.CommandNode;
import com.minecraftonline.nope.sponge.command.parameters.ParameterKeys;
import com.minecraftonline.nope.sponge.command.parameters.Parameters;
import com.minecraftonline.nope.sponge.util.Formatter;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

public class ListSettingsCommand extends CommandNode {
  public ListSettingsCommand(CommandNode parent) {
    super(parent, Permissions.INFO,
        "List all possible settings",
        "list");
    addParameter(Parameters.SETTING_CATEGORY);
  }

  @Override
  public CommandResult execute(CommandContext context) throws CommandException {
    Optional<SettingKey.Category> category = context.one(ParameterKeys.SETTING_CATEGORY);
    if (category.isPresent()) {
      sendSettings(context, category.get());
    } else {
      if (context.cause().root() instanceof ServerPlayer) {
        Sponge.serviceProvider().paginationService().builder()
            .title(Component.text("Setting Categories").color(Formatter.GOLD))
            .header(Formatter.accent("Choose a category"))
            .contents(Arrays.stream(SettingKey.Category.values())
                .map(c -> c.name().toLowerCase())
                .map(c -> Formatter.command(c,
                    this.fullCommand(context) + " " + c,
                    Formatter.accent("View settings in category ___", c)))
                .collect(Collectors.toList()))
            .sendTo(context.cause().audience());
      } else {
        context.sendMessage(Identity.nil(), Formatter.error("Please specify a category"));
      }
    }
    return CommandResult.success();
  }

  private void sendSettings(CommandContext context, SettingKey.Category category) {
    Sponge.serviceProvider().paginationService().builder()
        .title(Component.text(category.name().toUpperCase() + " Settings").color(Formatter.GOLD))
        .contents(SpongeNope.instance().settingKeys()
            .keys()
            .values()
            .stream()
            .filter(settingKey -> settingKey.category().equals(category))
            .map(settingKey -> Formatter.settingKey(settingKey, true))
            .collect(Collectors.toList()))
        .sendTo(context.cause().audience());
  }
}
