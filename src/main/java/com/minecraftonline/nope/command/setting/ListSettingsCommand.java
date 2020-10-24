package com.minecraftonline.nope.command.setting;

import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.command.common.LambdaCommandNode;
import com.minecraftonline.nope.pagination.NopePaginations;
import com.minecraftonline.nope.permission.Permission;
import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;
import org.spongepowered.common.service.pagination.NopePagination;

public class ListSettingsCommand extends LambdaCommandNode {
  public ListSettingsCommand(CommandNode parent) {
    super(parent, Permission.of("nope.setting.list"), Text.of("Allows the user to list all the nope settings"), "list");

    addCommandElements(GenericArguments.optional(GenericArguments.integer(Text.of("pageNum"))));

    setExecutor((src, args) -> {
      // Reduce given page by one.
      int page = args.<Integer>getOne("pageNum").map(i -> i - 1).orElse(0);
      NopePagination pagination = NopePaginations.SETTING_PAGINATION.getOrCreatePagination();

      if (page < 0 || page >= pagination.pagesLength()) {
        src.sendMessage(Format.error("Invalid page number, expected between 1-" + pagination.pagesLength()));
        return CommandResult.success();
      }

      pagination.showPage(src, page);

      return CommandResult.success();
    });
  }
}
