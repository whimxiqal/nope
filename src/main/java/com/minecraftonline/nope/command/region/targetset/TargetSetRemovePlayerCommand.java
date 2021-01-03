//package com.minecraftonline.nope.command.region.targetset;
//
//import com.minecraftonline.nope.arguments.NopeArguments;
//import com.minecraftonline.nope.command.common.CommandNode;
//import com.minecraftonline.nope.control.target.PlayerTarget;
//import com.minecraftonline.nope.control.target.TargetSet;
//import com.minecraftonline.nope.util.Format;
//import org.spongepowered.api.command.CommandSource;
//import org.spongepowered.api.command.args.CommandContext;
//import org.spongepowered.api.command.args.GenericArguments;
//import org.spongepowered.api.profile.GameProfile;
//import org.spongepowered.api.text.Text;
//
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ExecutionException;
//
//public class TargetSetRemovePlayerCommand extends AbstractTargetSetSubCommand {
//  public TargetSetRemovePlayerCommand(CommandNode parent, String targetSetName, Setting<TargetSet> setting) {
//    super(parent, "removeplayer", targetSetName, "remove player from ", setting);
//
//    addCommandElements(GenericArguments.onlyOne(NopeArguments.playerOrPlayerUUID(Text.of("player"))));
//  }
//
//  @Override
//  protected CompletableFuture<TargetSet> makeChanges(CommandSource source, CommandContext args, TargetSet targetSet) {
//    CompletableFuture<TargetSet> result = new CompletableFuture<>();
//    CompletableFuture<GameProfile> future = args.<CompletableFuture<GameProfile>>getOne(Text.of("player")).get();
//
//    future.whenComplete((profile, throwable) -> {
//      if (throwable instanceof ExecutionException) {
//        // No profile
//        source.sendMessage(Format.error("Account for uuid does not exist!"));
//        result.complete(targetSet);
//        return;
//      }
//
//      if (!profile.getName().isPresent()) {
//        source.sendMessage(Format.error("Username for uuid: '" + profile.getUniqueId() + "' did not exist! Is it a real account?"));
//        source.sendMessage(Format.error("Making no changes."));
//        result.complete(targetSet);
//        return;
//      }
//
//      boolean removed = targetSet.remove(new PlayerTarget(profile.getUniqueId()));
//      result.complete(targetSet);
//
//      Text text = removed ? Format.info("Successfully removed player: '" + profile.getName().get() + "'")
//          : Format.error("No player named '" + profile.getName().get() + "' to remove");
//
//      source.sendMessage(text);
//    });
//
//    return result;
//  }
//}
