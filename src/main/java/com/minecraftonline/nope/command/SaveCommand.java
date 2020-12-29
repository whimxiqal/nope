package com.minecraftonline.nope.command;

import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.command.common.LambdaCommandNode;
import com.minecraftonline.nope.permission.Permission;
import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.text.Text;

public class SaveCommand extends LambdaCommandNode {
    public SaveCommand(CommandNode parent) {
        super(parent, Permission.of("nope.save"), Text.of("Saves Nope's config, discarding any out of game config changes. This is what happens when the server shuts down. ",
                Text.NEW_LINE,
                "See also: ",
                Format.command("reload help", "/nope reload help", Format.note("See help for /nope reload"))
                ), "save", true);

        setExecutor((src, args) -> {
            Nope.getInstance().getHostTree().save();
            src.sendMessage(Format.success("Saved config"));
            return CommandResult.success();
        });
    }
}
