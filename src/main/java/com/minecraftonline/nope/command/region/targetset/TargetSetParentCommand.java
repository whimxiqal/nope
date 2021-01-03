//package com.minecraftonline.nope.command.region.targetset;
//
//import com.minecraftonline.nope.command.common.CommandNode;
//import com.minecraftonline.nope.command.common.FunctionlessCommandNode;
//import com.minecraftonline.nope.control.target.TargetSet;
//import com.minecraftonline.nope.permission.Permission;
//import org.spongepowered.api.text.Text;
//
//public class TargetSetParentCommand extends FunctionlessCommandNode {
//
//  public TargetSetParentCommand(CommandNode parent, String targetSetName, Setting<TargetSet> setting) {
//    super(parent,
//            Permission.of("nope.region.edit"),
//            Text.of("Allows the user to edit the "
//                    + targetSetName
//                    + " of a region"), targetSetName);
//
//    addChildren(new TargetSetAddPlayerCommand(this, targetSetName, setting));
//    addChildren(new TargetSetAddGroupCommand(this, targetSetName, setting));
//
//    addChildren(new TargetSetClearCommand(this, targetSetName, setting));
//
//    addChildren(new TargetSetRemovePlayerCommand(this, targetSetName, setting));
//    addChildren(new TargetSetRemoveGroupCommand(this, targetSetName, setting));
//  }
//}
