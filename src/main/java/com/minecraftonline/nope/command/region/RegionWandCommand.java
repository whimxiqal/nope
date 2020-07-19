package com.minecraftonline.nope.command.region;

import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.control.Settings;
import com.minecraftonline.nope.key.NopeKeys;
import com.minecraftonline.nope.key.regionwand.RegionWandManipulator;
import com.minecraftonline.nope.permission.Permissions;
import com.minecraftonline.nope.util.Format;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.blocks.ItemType;
import com.sk89q.worldedit.sponge.SpongeWorldEdit;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.MainPlayerInventory;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.text.Text;

public class RegionWandCommand extends CommandNode {
  public RegionWandCommand(CommandNode parent) {
    super(parent,
        Permissions.CREATE_REGION,
        Text.of("Gives the executor a wand for easy creation of regions"),
        "wand",
        "w");
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (!(src instanceof Player)) {
      src.sendMessage(Format.error("You cannot use this command as a non-player"));
      return CommandResult.empty();
    }
    Player player = (Player)src;
    Inventory inv = player.getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(MainPlayerInventory.class));
    ItemType itemType = Nope.getInstance().getGlobalHost().getSettingValue(Settings.WAND_ITEM).orElse(Settings.WAND_ITEM.getDefaultValue());
    ItemStack itemStack = SpongeWorldEdit.toSpongeItemStack(new BaseItemStack(itemType.getID(), 1));
    itemStack.offer(new RegionWandManipulator(true));
    if (!inv.canFit(itemStack)) {
      player.sendMessage(Format.error("You have no room in your inventory, make way for the magic wand and try again"));
      return CommandResult.empty();
    }
    inv.offer(itemStack);
    player.sendMessage(Format.info("Left click for first position, right click for second position"));
    return CommandResult.success();
  }
}
