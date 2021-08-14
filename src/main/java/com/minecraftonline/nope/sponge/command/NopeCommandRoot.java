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

package com.minecraftonline.nope.sponge.command;

import com.minecraftonline.nope.common.host.Domain;
import com.minecraftonline.nope.common.struct.FlexibleHashQueueZoneTree;
import com.minecraftonline.nope.sponge.SpongeNope;
import com.minecraftonline.nope.sponge.command.general.CommandNode;
import com.minecraftonline.nope.sponge.util.SpongeFormatter;
import java.util.Objects;
import javax.annotation.Nonnull;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

/**
 * An extension of a command node to represent Nope's root
 * of its command tree.
 */
public class NopeCommandRoot extends CommandNode {

  /**
   * Default constructor.
   */
  public NopeCommandRoot() {
    super(null,
        null,
        "All commands pertaining to Nope",
        "nope");
    addChildren(new ApplyCommand(this));
    addChildren(new ClearCommand(this));
    addChildren(new CreateCommand(this));
    addChildren(new DestroyCommand(this));
    addChildren(new InfoCommand(this));
    addChildren(new ListAllCommand(this));
    addChildren(new ListCommand(this));
    addChildren(new MoveCommand(this));
    addChildren(new Position1Command(this));
    addChildren(new Position2Command(this));
    addChildren(new ReloadCommand(this));
    addChildren(new SelectCommand(this));
    addChildren(new SetCommand(this));
    addChildren(new SetPriorityCommand(this));
    addChildren(new SettingsCommand(this));
    addChildren(new ShowsCommand(this));
    addChildren(new ShowCommand(this));
    addChildren(new TargetCommand(this));
    addChildren(new TeleportCommand(this));
    addChildren(new UnsetCommand(this));
    addChildren(new WandCommand(this));
  }

  @Nonnull
  @Override
  public CommandResult execute(CommandContext context) {
    Audience audience = context.cause().audience();
    audience.sendMessage(Component.text("==========================").color(SpongeFormatter.DULL));
    audience.sendMessage(Component.text("Nope v" + SpongeNope.instance().getPluginContainer().metadata().version())
        .color(SpongeFormatter.THEME)
        .decorate(TextDecoration.BOLD)
        .append(Component.text(" by MinecraftOnline")
            .color(SpongeFormatter.DULL)
            .decorate(TextDecoration.ITALIC)));
    audience.sendMessage(formatter().keyValue("Authors:", "PietElite, tyhdefu"));
    audience.sendMessage(Component.text()
        .append(Component.text("Check out the "))
        .append(formatter().url("website", "https://www.minecraftonline.com/"))
        .append(Component.text(" or "))
        .append(formatter().url("source", "https://github.com/pietelite/nope/"))
        .build());
    audience.sendMessage(Component.text()
        .append(Component.text("Try the "))
        .append(formatter().command("help", Objects.requireNonNull(this.getHelpCommand()).getFullCommand(), Component.text("Show a helpful menu of commands")))
        .build());
    showCacheSize(context);
    return CommandResult.success();
  }

  void showCacheSize(CommandContext context) {
    if (context.cause().root() instanceof Player) {
      Settee worldSettee = SpongeNope.instance()
          .getHostSystemAdapter()
          .getWorldHost(((Player) context.cause().root()).serverLocation().world().key().asString());
      if (worldSettee instanceof Domain) {
        if (((Domain) worldSettee).getZoneTree()
            instanceof FlexibleHashQueueZoneTree) {
          context.cause().audience().sendMessage(Component.text("Cache size: "
              + ((FlexibleHashQueueZoneTree<?, ?>)
              ((Domain) worldSettee).getZoneTree()).getCacheSize()).color(SpongeFormatter.DULL));
        }
      }
    }
  }

}
