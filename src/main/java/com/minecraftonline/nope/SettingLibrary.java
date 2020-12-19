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
 *
 */

package com.minecraftonline.nope;

import com.flowpowered.math.vector.Vector3d;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;

import java.util.Set;

public class SettingLibrary {

  private SettingLibrary() {
  }

  @Data
  public abstract static class Setting<T> {

    // This order of these affects how they will be sorted in
    // /nope setting list, so keep MISC last
    public enum Category {
      BLOCKS,
      MOVEMENT,
      DAMAGE,
      MISC,
    }

    protected final Info info;
    protected final T defaultValue;
    protected final Class<T> valueType;

    JsonElement serialize() {
      return new Gson().toJsonTree(defaultValue);
    }

    @Builder
    @Getter
    public static class Info {
      /* Header */
      private final String id;
      private final String path;
      private final Category category;

      /* Context */
      private final String description;
      private final String comment;

      /* Implementation State */
      private final boolean implemented;
    }

  }

  /* ========== */
  /* PRIMITIVES */
  /* ========== */


  public static class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting(Info info, Boolean defaultValue) {
      super(info, defaultValue, Boolean.class);
    }
  }

  public static class IntegerSetting extends Setting<Integer> {
    public IntegerSetting(Info info, Integer defaultValue) {
      super(info, defaultValue, Integer.class);
    }
  }

  public static class StringSetting extends Setting<String> {
    public StringSetting(Info info, String defaultValue) {
      super(info, defaultValue, String.class);
    }
  }

  /* ====== */
  /* EXTRAS */
  /* ====== */


  public static class StateSetting extends Setting<Boolean> {
    public StateSetting(Info info, Boolean defaultValue) {
      super(info, defaultValue, Boolean.class);
    }
  }

  public static class GameModeSetting extends Setting<GameMode> {
    public GameModeSetting(Info info, GameMode defaultVaule) {
      super(info, defaultVaule, GameMode.class);
    }
  }

  public static class StringSetSetting extends Setting<Set<String>> {
    @SuppressWarnings("unchecked")
    public StringSetSetting(Info info, Set<String> defaultValue) {
      super(info, defaultValue, (Class<Set<String>>) (Class<?>) Set.class);
    }
  }


  public static class EntitySetSetting extends Setting<Set<Entity>> {
    @SuppressWarnings("unchecked")
    public EntitySetSetting(Info info, Set<Entity> defaultValue) {
      super(info, defaultValue, (Class<Set<Entity>>) (Class<?>) Set.class);
    }
  }

  public static class Vector3DSetting extends Setting<Vector3d> {
    public Vector3DSetting(Info info, Vector3d defaultValue) {
      super(info, defaultValue, Vector3d.class);
    }
  }

  /* ======== */
  /* SETTINGS */
  /* ======== */


  public static final BooleanSetting BUILD_PERMISSIONS = new BooleanSetting(
      Setting.Info.builder()
          .id("build-permission-nodes-enable")
          .implemented(false)
          .build(),
      false);

}
