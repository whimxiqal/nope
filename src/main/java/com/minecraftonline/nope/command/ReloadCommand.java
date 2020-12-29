package com.minecraftonline.nope.command;

import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.command.common.LambdaCommandNode;
import com.minecraftonline.nope.permission.Permission;
import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.text.Text;

public class ReloadCommand extends LambdaCommandNode {
    public ReloadCommand(CommandNode parent) {
        super(parent, Permission.of("nope.reload"),
                Text.of("Reloads config, discarding all unsaved changed. The same as doing /sponge plugins reload, but only targets Nope.",
                    Text.NEW_LINE,
                    "See also: ",
                    Format.command("save help", "/nope save help", Format.note("See help for /nope save"))
                    ),
                "reload", true);

        setExecutor((src, args) -> {
            Nope.getInstance().reload();
            src.sendMessage(Format.success("Successfully reloaded"));
            return CommandResult.success();
        });
    }
}
