//package com.minecraftonline.nope.command.region.targetset;
//
//import com.minecraftonline.nope.command.common.CommandNode;
//import com.minecraftonline.nope.control.target.TargetSet;
//import com.minecraftonline.nope.util.Format;
//import org.spongepowered.api.command.CommandSource;
//import org.spongepowered.api.command.args.CommandContext;
//
//import java.util.concurrent.CompletableFuture;
//
//public class TargetSetClearCommand extends AbstractTargetSetSubCommand {
//  public TargetSetClearCommand(CommandNode parent, String targetSetName, Setting<TargetSet> setting) {
//    super(parent, "clear", targetSetName, "clear", setting);
//  }
//
//  @Override
//  protected CompletableFuture<TargetSet> makeChanges(CommandSource source, CommandContext args, TargetSet targetSet) {
//    source.sendMessage(Format.info("Successfully cleared region"));
//    return CompletableFuture.completedFuture(new TargetSet());
//  }
//}
