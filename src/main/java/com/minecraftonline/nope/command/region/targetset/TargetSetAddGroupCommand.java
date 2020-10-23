package com.minecraftonline.nope.command.region.targetset;

import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.control.Setting;
import com.minecraftonline.nope.control.target.GroupTarget;
import com.minecraftonline.nope.control.target.Target;
import com.minecraftonline.nope.control.target.TargetSet;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.concurrent.CompletableFuture;

public class TargetSetAddGroupCommand extends AbstractTargetSetSubCommand {
  public TargetSetAddGroupCommand(CommandNode parent, String targetSetName, Setting<TargetSet> setting) {
    super(parent, "addgroup", targetSetName, "add a group to", setting);

    addCommandElements(GenericArguments.onlyOne(GenericArguments.string(Text.of("group"))));
  }

  @Override
  protected CompletableFuture<TargetSet> makeChanges(CommandSource source, CommandContext args, TargetSet targetSet) {
    String group = args.<String>getOne(Text.of("group")).get();

    Target target = new GroupTarget(group);
    targetSet.add(target);

    source.sendMessage(Text.of(TextColors.GREEN, "Successfully added group '" + group + "'"));

    return CompletableFuture.completedFuture(targetSet);
  }
}
