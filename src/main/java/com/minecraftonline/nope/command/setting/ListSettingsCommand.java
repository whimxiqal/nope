package com.minecraftonline.nope.command.setting;

import com.google.common.collect.Lists;
import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.command.common.LambdaCommandNode;
import com.minecraftonline.nope.permission.Permissions;
import com.minecraftonline.nope.setting.SettingKey;
import com.minecraftonline.nope.setting.SettingLibrary;
import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ListSettingsCommand extends LambdaCommandNode {

  ListSettingsCommand(CommandNode parent) {
    super(parent,
        Permissions.COMMAND_SETTING,
        Text.of("Allows the user to list all the nope settings"),
        "list");

    addCommandElements(GenericArguments.optional(GenericArguments.integer(Text.of("pageNum"))));

    setExecutor((src, args) -> {
      PaginationService pagination = Sponge.getServiceManager().provide(PaginationService.class)
          .orElseThrow(() -> new RuntimeException("PaginationService unavailable."));

      List<SettingKey<?>> keys = SettingLibrary.getAll()
          .stream()
          .sorted(Comparator
              .comparing((SettingKey<?> key) -> key.getCategory().name())
              .thenComparing(SettingKey::getId))
          .collect(Collectors.toList());

      List<Text> contents = Lists.newLinkedList();
      if (keys.size() > 0) {

        contents.add(Text.of(TextColors.AQUA, keys.get(0).getCategory().name().toUpperCase()));
        contents.add(Format.settingKey(keys.get(0), true));
        for (int i = 1; i < keys.size(); i++) {
          if (!keys.get(i).getCategory().equals(keys.get(i - 1).getCategory())) {
            contents.add(Text.of(TextColors.AQUA, keys.get(i).getCategory().name().toUpperCase()));
          }
          contents.add(Format.settingKey(keys.get(i), true));
        }

        pagination.builder()
            .title(Text.of("Nope Settings"))
            .padding(Text.of(TextColors.DARK_GREEN, "="))
            .contents(contents)
            .build()
            .sendTo(src);
      }

      return CommandResult.success();
//      // Reduce given page by one.
//      int page = args.<Integer>getOne("pageNum").map(i -> i - 1).orElse(0);
//      NopePagination pagination = NopePaginations.SETTING_PAGINATION.getOrCreatePagination();
//
//      if (page < 0 || page >= pagination.pagesLength()) {
//        src.sendMessage(Format.error("Invalid page number, expected between 1-" + pagination.pagesLength()));
//        return CommandResult.success();
//      }
//
//      pagination.showPage(src, page);
//
//      return CommandResult.success();
    });
  }

}
