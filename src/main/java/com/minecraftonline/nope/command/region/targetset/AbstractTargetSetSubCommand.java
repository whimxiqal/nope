package com.minecraftonline.nope.command.region.targetset;

import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.arguments.NopeArguments;
import com.minecraftonline.nope.arguments.RegionWrapper;
import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.command.common.LambdaCommandNode;
import com.minecraftonline.nope.control.Region;
import com.minecraftonline.nope.control.Setting;
import com.minecraftonline.nope.control.target.TargetSet;
import com.minecraftonline.nope.permission.Permission;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;

import java.util.concurrent.CompletableFuture;

public abstract class AbstractTargetSetSubCommand extends LambdaCommandNode {

  /**
   * Eases the creation of sub commands for target set sub commands.
   *
   * @param parent Parent node
   * @param alias Alias, name to call the command by
   * @param targetSetName Name of the target set setting, e.g owners, members
   * @param action Action string for the descriptor, see constructor for details
   * @param setting Setting to change
   */
  public AbstractTargetSetSubCommand(CommandNode parent, String alias, String targetSetName, String action, Setting<TargetSet> setting) {
    super(parent, Permission.of("nope.region.edit." + targetSetName + "." + alias), Text.of("Allows the user to " + action + " the " + targetSetName + " of a region"), alias);

    addCommandElements(GenericArguments.onlyOne(NopeArguments.regionWrapper(Text.of("region"))));

    setExecutor((src, args) -> {
      RegionWrapper regionWrapper = args.<RegionWrapper>getOne(Text.of("region")).get();
      Region region = regionWrapper.getRegion();

      makeChanges(src, args, region.getSettingValueOrDefault(setting)).whenComplete((targetSet, throwable) -> {
        if (throwable != null) {
          Nope.getInstance().getLogger().error("Error changing TargetSet '" + targetSetName + "' on region '" + regionWrapper.getRegionName() + "'");
          return;
        }
        region.set(setting, targetSet);
      });
      return CommandResult.success();
    });
  }

  protected abstract CompletableFuture<TargetSet> makeChanges(CommandSource source, CommandContext args, TargetSet targetSet);
}
