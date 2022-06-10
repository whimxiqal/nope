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

package me.pietelite.nope.sponge.command.target.player;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import me.pietelite.nope.common.api.edit.TargetEditor;
import me.pietelite.nope.sponge.SpongeNope;
import me.pietelite.nope.sponge.command.CommandNode;
import me.pietelite.nope.sponge.command.parameters.ParameterKeys;
import me.pietelite.nope.sponge.command.parameters.Parameters;
import me.pietelite.nope.sponge.util.Formatter;
import net.kyori.adventure.identity.Identity;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.scheduler.Task;

public class RemovePlayerCommand extends CommandNode {

  private final Function<CommandContext, TargetEditor> targetEditorFunction;

  public RemovePlayerCommand(CommandNode parent,
                             Function<CommandContext, TargetEditor> targetEditorFunction,
                             String targetable) {
    super(parent, null,
        "Remove a player from the target of " + targetable,
        "remove");
    this.targetEditorFunction = targetEditorFunction;
    addParameter(Parameters.PLAYER_LIST);
  }

  @Override
  public CommandResult execute(CommandContext context) {
    TargetEditor editor = targetEditorFunction.apply(context);
    Collection<CompletableFuture<GameProfile>> players = context.requireOne(ParameterKeys.PLAYER_LIST);

    Sponge.asyncScheduler().submit(Task.builder()
        .execute(() -> {
          GameProfile profile;
          for (CompletableFuture<GameProfile> player : players) {
            try {
              profile = player.get();
            } catch (InterruptedException | ExecutionException e) {
              e.printStackTrace();
              continue;
            }
            // Schedule this back on the main thread
            GameProfile finalProfile = profile;
            Sponge.server().scheduler().submit(Task.builder()
                .execute(() -> {
                  if (editor.removePlayer(finalProfile.uuid())) {
                    context.sendMessage(Identity.nil(), Formatter.success("Removed player ___ from target",
                        finalProfile.name().orElse(finalProfile.uuid().toString())));
                  } else {
                    context.sendMessage(Identity.nil(), Formatter.error("Player was not added here"));
                  }
                })
                .plugin(SpongeNope.instance().pluginContainer())
                .build());
          }
        }).plugin(SpongeNope.instance().pluginContainer())
        .build());
    return CommandResult.success();
  }
}
