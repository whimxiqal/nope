/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
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

package me.pietelite.nope.sponge.command.tree.profile.blank;

import me.pietelite.nope.common.host.Profile;
import me.pietelite.nope.common.permission.Permissions;
import me.pietelite.nope.sponge.command.CommandNode;
import me.pietelite.nope.sponge.command.parameters.Parameters;
import me.pietelite.nope.common.message.Formatter;
import me.pietelite.nope.sponge.command.tree.host.blank.info.SettingInfoCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;

public class ProfileInfoCommand extends CommandNode {
  public ProfileInfoCommand(CommandNode parent) {
    super(parent, Permissions.PROFILE_INFO, "Get info about a profile", "info");
    prefix(Parameters.PROFILE);
    addChild(new SettingInfoCommand(this));
  }

  @Override
  public CommandResult execute(CommandContext context) {
    Profile profile = context.requireOne(Parameters.PROFILE);

    Component header = Component.text()
        .append(Component.text("Settings")
            .color(Formatter.DULL)
            .decorate(TextDecoration.UNDERLINED))
        .build();
//    Sponge.asyncScheduler().submit(Task.builder().execute(() ->
//            Formatter.paginator(profile.name())
//                .header(header)
//                .contents(profile.settings().isEmpty()
//                    ? Collections.singleton(Component.text("None").color(Formatter.DULL))
//                    : profile.settings().stream()
//                    .flatMap(setting -> {
//                      try {
//                        return Formatter.setting(setting, profile)
//                            .get()
//                            .stream();
//                      } catch (InterruptedException | ExecutionException e) {
//                        e.printStackTrace();
//                        return Stream.empty();
//                      }
//                    }).collect(Collectors.toList()))
//                .sendTo(context.cause().audience()))
//        .plugin(SpongeNope.instance().pluginContainer())
//        .build());

    return CommandResult.success();
  }
}
