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

package com.minecraftonline.nope;

import com.minecraftonline.nope.control.Setting;
import com.minecraftonline.nope.control.Settings;
import com.minecraftonline.nope.util.MDStringBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

public class CreateSettingsDescriptionFile {
  public final String outputLocation;

  public CreateSettingsDescriptionFile(String outputLocation) {
    this.outputLocation = outputLocation;
  }

  public void createFile() throws IOException {
    Settings.load();

    // Use LinkedHashMap for a stable output
    Map<Setting.Applicability, ConfigPathNode> configPaths = new LinkedHashMap<>();

    Collection<Setting<?>> settings = Settings.REGISTRY_MODULE.getAll();
    for (Setting<?> setting : settings) {
      for (Setting.Applicability applicability : Setting.Applicability.values()) {
        if (setting.isConfigurable() && setting.isApplicable(applicability)) {
          String[] path = setting.getConfigurationPath().get().split("[.]");
          configPaths.compute(applicability, (k, v) -> {
            ConfigPathNode node;
            if (k == null || v == null) {
              node = new ConfigPathNode("");
            } else {
              node = v;
            }
            node.addChild(path, 0, setting);
            return node;
          });
        }
      }
    }

    MDStringBuilder builder = new MDStringBuilder();
    builder.appendTitleLine("Nope com.minecraftonline.nope.setting.Setting summary", 1);
    builder.append(
        "This is an automagically generated description of all settings for easy reference.\n\r" +
        "Contains a breakdown by Region, World and global applicable settings, some settings applicable to more than one.\n\r" +
        "The type of data is show by the name in brackets, if they start with Flag, then simply ignore the flag to get the more recognisable type. FlagState is allow/deny, whereas FlagBoolean is true/false.\n\r" +
        "Region flags are unique: they can be only applied to a certain group, eg. no pvp only applies to non-members, etc.\n\r"
    );

    configPaths.forEach((category, pathNode) -> {
      builder.appendTitleLine(category.toString(), 2);
      // Ignore initial ConfigPathNode
      // We want childless children first, then grouped ones later
      Stream.concat(pathNode.getChildlessChildren().stream(), pathNode.getChildrenWithChildren().stream())
          .forEach(node -> parseConfigPath(node, builder, 3));
    });

    Files.write(Paths.get(outputLocation), builder.build().getBytes());
  }

  public void parseConfigPath(ConfigPathNode node, MDStringBuilder builder, int titleSize) {
    if (!node.getChildren().isEmpty()) {
      // Has children?
      builder.appendTitleLine(node.getKey(), titleSize);
      // We want childless children first for ordering purposes
      Stream.concat(node.getChildlessChildren().stream(), node.getChildrenWithChildren().stream())
          .forEach(childNode -> parseConfigPath(childNode, builder, titleSize + 1));
    } else {
      Setting<?> setting = node.getSetting().get();
      builder.appendBullet(node.getKey());
      builder.append(" (").append(setting.getTypeClass().getSimpleName()).append(")");
      builder.append(" - ").append(setting.getDescription().orElse(setting.getComment().orElse("No description")));
      builder.append("\n\r");
    }
  }

  public static void main(String... args) {
    try {
      new CreateSettingsDescriptionFile(args[0]).createFile();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
