/*
 * MIT License
 *
 * Copyright (c) 2021 MinecraftOnline
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

/*
 * MIT License
 *
 * Copyright (c) 2020 MinecraftOnline
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.minecraftonline.nope.sponge.command;

import com.google.common.collect.Lists;
import com.minecraftonline.nope.sponge.SpongeNope;
import com.minecraftonline.nope.sponge.command.general.CommandNode;
import com.minecraftonline.nope.sponge.key.zonewand.ZoneWandManipulator;
import com.minecraftonline.nope.common.permission.Permissions;
import com.minecraftonline.nope.common.setting.SettingLibrary;
import com.minecraftonline.nope.common.setting.SettingValue;
import com.minecraftonline.nope.sponge.util.Format;
import javax.annotation.Nonnull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.MainPlayerInventory;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * A command that retrieves a Nope wand for the purpose of selection Nope selections.
 */
public class WandCommand extends CommandNode {

  WandCommand(CommandNode parent) {
    super(parent,
        Permissions.COMMAND_CREATE,
        Text.of("Get a wand for easy creation of zones"),
        "wand",
        "w");
  }

  @Nonnull
  @Override
  public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) {
    if (!(src instanceof Player)) {
      src.sendMessage(Format.error("You cannot use this command as a non-player"));
      return CommandResult.empty();
    }
    Player player = (Player) src;

    ItemStack itemStack = ItemStack.builder()
        .itemType(Sponge.getRegistry()
            .getType(ItemType.class, SpongeNope.getInstance()
                .getHostTreeAdapter()
                .getGlobalHost()
                .get(SettingLibrary.WAND_ITEM)
                .map(SettingValue::getData)
                .orElse(SettingLibrary.WAND_ITEM.getDefaultData()))
            .orElseThrow(() -> new IllegalStateException("Storing an illegal wand id")))
        .quantity(1)
        .build();

    itemStack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.AQUA,
        "Nope Zone Wand"));
    itemStack.offer(Keys.ITEM_LORE, Lists.newArrayList(Text.of(TextColors.GRAY,
        "Select two corners of a zone with left click and right click")));

    itemStack.offer(new ZoneWandManipulator(true));

    Inventory inv = player.getInventory()
        .query(QueryOperationTypes.INVENTORY_TYPE.of(MainPlayerInventory.class));
    if (!inv.canFit(itemStack)) {
      player.sendMessage(Format.error("You have no room in your inventory! ",
          "Make way for the magic wand and try again"));
      return CommandResult.empty();
    }
    inv.offer(itemStack);
    player.sendMessage(Format.info("Left click for first position; "
        + "right click for second position"));
    return CommandResult.success();
  }
}
