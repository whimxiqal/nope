package com.minecraftonline.nope.sponge.command.tree;

import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.sponge.command.CommandNode;
import com.minecraftonline.nope.sponge.command.FunctionlessCommandNode;
import com.minecraftonline.nope.sponge.command.parameters.Parameters;
import com.minecraftonline.nope.sponge.command.tree.template.blank.EditCommand;
import com.minecraftonline.nope.sponge.command.tree.template.blank.TemplateInfoCommand;
import com.minecraftonline.nope.sponge.command.tree.template.blank.apply.ApplyCommand;
import com.minecraftonline.nope.sponge.command.tree.template.blank.create.CreateCommand;
import com.minecraftonline.nope.sponge.command.tree.template.blank.delete.DeleteCommand;

public class TemplateCommand extends FunctionlessCommandNode {
  public TemplateCommand(CommandNode parent) {
    super(parent, Permissions.EDIT,
        "Create, delete, and edit templates",
        "template", "t");
    addChild(new ApplyCommand(this));
    addChild(new CreateCommand(this));
    addChild(new DeleteCommand(this));
    addChild(new EditCommand(this));
    addChild(new TemplateInfoCommand(this));
  }
}
