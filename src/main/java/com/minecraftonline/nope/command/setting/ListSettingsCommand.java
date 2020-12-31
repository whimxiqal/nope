package com.minecraftonline.nope.command.setting;

import com.google.common.collect.Lists;
import com.minecraftonline.nope.command.common.CommandNode;
import com.minecraftonline.nope.command.common.LambdaCommandNode;
import com.minecraftonline.nope.permission.Permission;
import com.minecraftonline.nope.setting.SettingKey;
import com.minecraftonline.nope.setting.SettingLibrary;
import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ListSettingsCommand extends LambdaCommandNode {
  public ListSettingsCommand(CommandNode parent) {
    super(parent, Permission.of("nope.setting.list"), Text.of("Allows the user to list all the nope settings"), "list");

    addCommandElements(GenericArguments.optional(GenericArguments.integer(Text.of("pageNum"))));

    setExecutor((src, args) -> {
      PaginationService pagination = Sponge.getServiceManager().provide(PaginationService.class)
          .orElseThrow(() -> new RuntimeException("PaginationService unavailable."));

//      Map<SettingKey.CategoryType, List<SettingKey<?>>> groups = Maps.newHashMap();
//      List<SettingKey.CategoryType> categoryTypes = Lists.newArrayList(SettingKey.CategoryType.values());
//      categoryTypes.sort(Comparator.comparing(SettingKey.CategoryType::name));
//
//      categoryTypes.stream().forEach(type -> groups.put(type, Lists.newLinkedList()));
//      SettingLibrary.getAll().forEach(key -> groups.get(key.getCategory()).add(key));
//      groups.values().forEach(list -> list.sort(Comparator.comparing(SettingKey::getId)));

      List<SettingKey<?>> keys = SettingLibrary.getAll()
          .stream()
          .sorted(Comparator
              .comparing((SettingKey<?> key) -> key.getCategory().name())
              .thenComparing(SettingKey::getId))
          .collect(Collectors.toList());

      List<Text> contents = Lists.newLinkedList();
      if (keys.size() > 0) {

        contents.add(Text.of(TextColors.AQUA, keys.get(0).getCategory().name().toUpperCase()));
        for (int i = 1; i < keys.size(); i++) {
          if (!keys.get(i).getCategory().equals(keys.get(i - 1).getCategory())) {
            contents.add(Text.of(TextColors.AQUA, keys.get(i).getCategory().name().toUpperCase()));
          }
          contents.add(textOf(keys.get(i)));
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

  private Text textOf(SettingKey<?> key) {
    Text.Builder builder = Text.builder();

    Text.Builder idText = Text.builder().append(Text.of(TextColors.GREEN, key.getId()));

    Text.Builder onHover = Text.builder();

    if (!key.isImplemented()) {
      idText.style(TextStyles.STRIKETHROUGH);
      onHover.append(Text.of(TextColors.RED, "Not implemented yet!"));
      onHover.append(Text.NEW_LINE);
    }

    onHover.append(Format.keyValue("Type: ", key.valueType().getSimpleName()));
    onHover.append(Text.NEW_LINE);

    if (key.getParent().isPresent()) {
      onHover.append(Format.keyValue("Parent: ", key.getParent().get().getId()));
      onHover.append(Text.NEW_LINE);
    }

    onHover.append(Format.keyValue("Default value: ", key.getDefaultData().toString()));

    if (key.getDescription().isPresent()) {
      onHover.append(Text.NEW_LINE).append(Text.NEW_LINE);
      onHover.append(Text.of(TextColors.GRAY, key.getDescription().get()));
    }

    builder.onHover(TextActions.showText(onHover.build()));

    builder.append(idText.build());
    builder.append(Text.of(TextColors.WHITE, " - "
        + key.getDescription().orElse(key.getDescription().orElse("No description"))));

    return builder.build();
  }
}
