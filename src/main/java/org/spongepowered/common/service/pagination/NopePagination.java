package org.spongepowered.common.service.pagination;

import com.google.common.collect.Multimap;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class NopePagination {
  public static final int LINES_PER_PAGE = 20;
  private final PaginationCalculator calculator = new PaginationCalculator(LINES_PER_PAGE);
  private final Text padding = Text.of(TextColors.DARK_GREEN, "=");
  private final List<Text> pages = new ArrayList<>();

  public NopePagination(Multimap<String, Text> contents) {
    final List<Text.Builder> inProgressPages = new ArrayList<>();

    for (Map.Entry<String, Collection<Text>> entry : contents.asMap().entrySet()) {
      Text.Builder pageBuilder = Text.builder();

      final Text header = calculator.center(Text.of(TextColors.DARK_GREEN, entry.getKey()), this.padding);
      pageBuilder.append(header);
      pageBuilder.append(Text.NEW_LINE);

      int remainingLines = LINES_PER_PAGE;
      remainingLines--; // Lost a line to top line

      for (Text content : entry.getValue()) {
        final int lines = calculator.getLines(content);
        if (lines > (LINES_PER_PAGE - 2)) {
          throw new IllegalArgumentException("A single entry of content in pagination cannot be more than 18 lines long. It will never fit!");
        }
        final int resultantLines = remainingLines - lines;
        if (resultantLines < 1) {
          // We need a line for the footer so we cannot append this.
          // Append newlines until we have none left
          for (int i = 0; i < resultantLines - 1; i++) {
            pageBuilder.append(Text.NEW_LINE);
          }
          // Reset and prepare for next page
          inProgressPages.add(pageBuilder);

          pageBuilder = Text.builder();
          remainingLines = LINES_PER_PAGE;

          pageBuilder.append(header);
          pageBuilder.append(Text.NEW_LINE);
          remainingLines--;
        }
        // Append and reduce remaining lines
        pageBuilder.append(content);
        pageBuilder.append(Text.NEW_LINE);
        remainingLines -= lines;
      }
      // Reached the end of a category fill out with blank lines
      for (int i = 0; i < remainingLines - 1; i++) {
        pageBuilder.append(Text.NEW_LINE);
      }
      inProgressPages.add(pageBuilder);
    }

    final FooterFactory footerFactory = new FooterFactory(this.padding, this.calculator);

    int totalPages = inProgressPages.size();
    for (int i = 0; i < totalPages; i++) {
      inProgressPages.get(i).append(footerFactory.make(this.pages, i, totalPages));
    }
    inProgressPages.stream().map(Text.Builder::build)
        .forEach(pages::add);
  }

  public void showPage(CommandSource source, int page) {
    source.sendMessage(this.pages.get(page));
  }

  public int pagesLength() {
    return this.pages.size();
  }

  public static class FooterFactory {
    private final Text padding;
    private final PaginationCalculator calculator;

    public FooterFactory(Text padding, PaginationCalculator calculator) {
      this.padding = padding;
      this.calculator = calculator;
    }

    public Text make(List<Text> pages, int curPage, int totalPages) {
      Text.Builder builder = Text.builder();
      if (curPage == 0) {
        builder.append(Text.of(TextColors.GREEN, "«"));
      }
      else {
        builder.append(
            Text.builder().onClick(TextActions.executeCallback(new GoToPage(pages, curPage - 1)))
            .append(Text.of(TextColors.BLUE, TextStyles.UNDERLINE, "«"))
            .build()
        );
      }

      builder.append(Text.of(TextColors.GREEN, " " + (curPage + 1) + "/" + totalPages + " "));

      if (curPage + 1 == totalPages) {
        builder.append(Text.of(TextColors.GREEN, "»"));
      }
      else {
        builder.append(
            Text.builder().onClick(TextActions.executeCallback(new GoToPage(pages, curPage + 1)))
                .append(Text.of(TextColors.BLUE, TextStyles.UNDERLINE, "»"))
                .build()
        );
      }

      return calculator.center(builder.build(), this.padding);
    }

    public static class GoToPage implements Consumer<CommandSource> {
      private final List<Text> pages;
      private final int i;

      public GoToPage(List<Text> pages, int i) {
        this.pages = pages;
        this.i = i;
      }

      @Override
      public void accept(CommandSource commandSource) {
        commandSource.sendMessage(pages.get(i));
      }
    }
  }
}
