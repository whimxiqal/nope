package com.minecraftonline.nope.pagination;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.minecraftonline.nope.control.Setting;
import com.minecraftonline.nope.control.Settings;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.common.service.pagination.NopePagination;

import java.util.Collection;

public class SettingPagination implements PaginationProvider {
  private NopePagination cache = null;

  @Override
  public NopePagination getOrCreatePagination() {
    if (this.cache != null) {
      return this.cache;
    }

    Multimap<String, Text> categoryContentsMap = HashMultimap.create();

    for (Setting<?> setting : Settings.REGISTRY_MODULE.getAll()) {
      Text.Builder builder = Text.builder();

      Text.Builder idText = Text.builder().append(Text.of(TextColors.GREEN, setting.getId()));

      Text inlineDescription = Text.of(TextColors.WHITE, " - " +
          setting.getComment().orElse(setting.getComment().orElse("No description")));

      Text.Builder onHover = Text.builder();

      if (!setting.isImplemented()) {
        idText.style(TextStyles.STRIKETHROUGH);
        onHover.append(Text.of(TextColors.RED, "Not implemented yet!"));
        onHover.append(Text.NEW_LINE);
      }

      onHover.append(Text.of(TextColors.GRAY, "Type: " + setting.getTypeClass().getSimpleName()))
          .append(Text.NEW_LINE);

      if (setting.getParent().isPresent()) {
        onHover.append(Text.of(TextColors.GRAY, "Parent: " + setting.getParent().get().getId()));
        onHover.append(Text.NEW_LINE);
      }

      Text.Builder applicabilityText = Text.builder().append(Text.of(TextColors.GRAY, "Applicability: "));

      StringBuilder applicabilityBuilder = new StringBuilder();
      for (Setting.Applicability applicability : Setting.Applicability.values()) {
        applicabilityBuilder.append(applicability.name()).append(',');
      }

      if (applicabilityBuilder.length() == 0) {
        applicabilityBuilder.append("NONE");
      }
      else {
        // Delete trailing comma
        applicabilityBuilder.deleteCharAt(applicabilityBuilder.length() - 1);
      }

      applicabilityText.append(Text.of(TextColors.GRAY, applicabilityBuilder.toString()));

      onHover.append(applicabilityText.build());
      onHover.append(Text.NEW_LINE);

      onHover.append(Text.of(TextColors.GRAY, "Default value: " + setting.getDefaultValue()));

      if (setting.getDescription().isPresent()) {
        onHover.append(Text.NEW_LINE).append(Text.NEW_LINE);
        onHover.append(Text.of(TextColors.GRAY, setting.getDescription().get()));
      }
      else if (setting.getComment().isPresent()) {
        onHover.append(Text.NEW_LINE).append(Text.NEW_LINE);
        onHover.append(Text.of(TextColors.GRAY, setting.getComment().get()));
      }

      builder.onHover(TextActions.showText(onHover.build()));

      builder.append(idText.build());
      builder.append(inlineDescription);

      categoryContentsMap.put(setting.getCategory().toString(), builder.build());
    }

    // Sort them in order of the enum
    Multimap<String, Text> sortedMultimap = LinkedHashMultimap.create();
    for (Setting.Category category : Setting.Category.values()) {
      sortedMultimap.putAll(category.name(), categoryContentsMap.get(category.name()));
    }

    this.cache = new NopePagination(sortedMultimap);

    return this.cache;
  }
}
