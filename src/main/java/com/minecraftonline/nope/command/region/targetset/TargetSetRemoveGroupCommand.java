package com.minecraftonline.nope.command.region.targetset;

import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.control.Setting;
import com.minecraftonline.nope.control.target.GroupTarget;
import com.minecraftonline.nope.control.target.TargetSet;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.concurrent.CompletableFuture;

public class TargetSetRemoveGroupCommand extends AbstractTargetSetSubCommand {
  public TargetSetRemoveGroupCommand(CommandNode parent, String targetSetName, Setting<TargetSet> setting) {
    super(parent, "removegroup", targetSetName, "remove a group from ", setting);

    addCommandElements(GenericArguments.onlyOne(GenericArguments.string(Text.of("group"))));
  }

  @Override
  protected CompletableFuture<TargetSet> makeChanges(CommandSource source, CommandContext args, TargetSet targetSet) {
    String group = args.<String>getOne(Text.of("group")).get();

    boolean removed = targetSet.remove(new GroupTarget(group));

    Text text = removed ? Text.of(TextColors.GREEN, "Successfully removed group: '" + group + "'")
        : Text.of(TextColors.RED, "No group '" + group + "'");

    source.sendMessage(text);

    return CompletableFuture.completedFuture(targetSet);
  }
}
