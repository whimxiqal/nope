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
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CreateSettingsDescriptionFile {
  public static final String FILE_LOC = "Settings.md";

  @Test
  public void createFile() {
    Settings.load();

    Map<Setting.Applicability, ConfigPathNode> configPaths = new HashMap<>();

    Collection<Setting<?>> settings = Settings.REGISTRY_MODULE.getAll();
    for (Setting<?> setting : settings) {
      for (Setting.Applicability applicability : Setting.Applicability.values()) {
        if (setting.isConfigurable() && setting.isApplicable(applicability)) {
          String[] path = setting.getConfigurationPath().get().split("[.]");
          configPaths.compute(applicability, (k, v) -> {
            ConfigPathNode node;
            if (k == null || v == null) {
              node = new ConfigPathNode("");
            }
            else {
              node = v;
            }
            node.addChild(path, 0, setting);
            return node;
          });
        }
      }
    }

    MDStringBuilder builder = new MDStringBuilder();

    int titleSize = 1;
    builder.appendTitleLine("Nope Setting summary", titleSize++);
    builder.append("This is an automagically generated description of all settings for easy reference.").append("\n");
    builder.append("Contains a breakdown by Region, World and global applicable settings, some settings applicable to more than one").append('\n');
    builder.append("The type of data is show by the name in brackets, if they start with Flag, then simply ignore the flag to get the more recognisable type. FlagState is allow/deny, whereas FlagBoolean is true/false.");
    builder.append("Region flags are unique: they can be only applied to a certain group, eg. no pvp only applies to non-members, etc.").append('\n');

    for (Map.Entry<Setting.Applicability, ConfigPathNode> entry : configPaths.entrySet()) {
      builder.appendTitleLine(entry.getKey().toString(), titleSize++);
      // Ignore initial ConfigPathNode
      // We want childless children first, then grouped ones later
      for (ConfigPathNode node : entry.getValue().getChildlessChildren()) {
        parseConfigPath(node, builder, titleSize);
      }
      for (ConfigPathNode node : entry.getValue().getChildrenWithChildren()) {
        parseConfigPath(node, builder, titleSize);
      }
      titleSize--;
    }

    File file = new File(FILE_LOC);
    try {
      file.createNewFile();
      BufferedWriter writer = new BufferedWriter(new FileWriter(file));
      writer.write(builder.build());
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public void parseConfigPath(ConfigPathNode node, MDStringBuilder builder, int titleSize) {
    if (!node.getChildren().isEmpty()) {
      // Has children?
      builder.appendTitleLine(node.getKey(), titleSize);
      // We want childless children first for ordering purposes
      for (ConfigPathNode childNode : node.getChildlessChildren()) {
        parseConfigPath(childNode, builder, titleSize + 1);
      }
      for (ConfigPathNode childNode : node.getChildrenWithChildren()) {
        parseConfigPath(childNode, builder, titleSize + 1);
      }
    }
    else {
      Setting<?> setting = node.getSetting().get();
      builder.appendBullet(node.getKey());
      builder.append(" (").append(setting.getTypeClass().getSimpleName()).append(")");
      builder.append(" - ").append(setting.getDescription().orElse(setting.getComment().orElse("No description")));
      builder.append("\n");
    }
  }
}
