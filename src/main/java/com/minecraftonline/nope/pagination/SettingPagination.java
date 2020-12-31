package com.minecraftonline.nope.pagination;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.minecraftonline.nope.setting.SettingKey;
import com.minecraftonline.nope.setting.SettingLibrary;
import com.minecraftonline.nope.util.Format;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.common.service.pagination.NopePagination;

import java.util.Arrays;
import java.util.stream.Collectors;

public class SettingPagination implements PaginationProvider {
  private NopePagination cache = null;

  @Override
  public NopePagination getOrCreatePagination() {
    if (this.cache != null) {
      return this.cache;
    }

    Multimap<String, Text> categoryContentsMap = HashMultimap.create();

    for (SettingKey<?> key : SettingLibrary.getAll()) {
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

//      onHover.append(Format.keyValue("Applicability: ", (Setting.Applicability.values().length != 0
//          ? Arrays.stream(Setting.Applicability.values())
//              .map(Setting.Applicability::name)
//              .collect(Collectors.joining(", "))
//          : "NONE")));
//      onHover.append(Text.NEW_LINE);

      onHover.append(Format.keyValue("Default value: ", key.getDefaultData().toString()));

      if (key.getDescription().isPresent()) {
        onHover.append(Text.NEW_LINE).append(Text.NEW_LINE);
        onHover.append(Text.of(TextColors.GRAY, key.getDescription().get()));
      }

      builder.onHover(TextActions.showText(onHover.build()));

      builder.append(idText.build());
      builder.append(Text.of(TextColors.WHITE, " - "
          + key.getDescription().orElse(key.getDescription().orElse("No description"))));

      categoryContentsMap.put(key.getCategory().toString(), builder.build());
    }

    // Sort them in order of the enum
    Multimap<String, Text> sortedMultimap = LinkedHashMultimap.create();
//    for (Setting.Category category : Setting.Category.values()) {
//      sortedMultimap.putAll(category.name(), categoryContentsMap.get(category.name()));
//    }

    this.cache = new NopePagination(sortedMultimap);

    return this.cache;
  }
}
