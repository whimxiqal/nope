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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.minecraftonline.nope.util.NopeTypeTokens;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;

import java.util.HashSet;
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

    public JsonElement serialize(T value) {
      return new Gson().toJsonTree(value);
    }

    public abstract T deserialize(JsonElement jsonElement);

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

    @Override
    public Boolean deserialize(JsonElement jsonElement) {
      return jsonElement.getAsBoolean();
    }
  }

  public static class IntegerSetting extends Setting<Integer> {
    public IntegerSetting(Info info, Integer defaultValue) {
      super(info, defaultValue, Integer.class);
    }

    @Override
    public Integer deserialize(JsonElement jsonElement) {
      return jsonElement.getAsInt();
    }
  }

  public static class StringSetting extends Setting<String> {
    public StringSetting(Info info, String defaultValue) {
      super(info, defaultValue, String.class);
    }

    @Override
    public String deserialize(JsonElement jsonElement) {
      return jsonElement.getAsString();
    }
  }

  /* ====== */
  /* EXTRAS */
  /* ====== */


  public static class StateSetting extends Setting<Boolean> {
    public StateSetting(Info info, Boolean defaultValue) {
      super(info, defaultValue, Boolean.class);
    }

    @Override
    public JsonElement serialize(Boolean value) {
      return new JsonPrimitive(value ? "allow" : "deny");
    }

    @Override
    public Boolean deserialize(JsonElement jsonElement) {
      final String s = jsonElement.getAsString();
      switch (s) {
        case "allow": return true;
        case "deny": return false;
        default: throw new IllegalStateException("Invalid state string. Should be allow or deny. Was: " + s);
      }
    }
  }

  public static class GameModeSetting extends Setting<GameMode> {
    public GameModeSetting(Info info, GameMode defaultValue) {
      super(info, defaultValue, GameMode.class);
    }

    @Override
    public JsonElement serialize(GameMode value) {
      return new JsonPrimitive(value.getId());
    }

    @Override
    public GameMode deserialize(JsonElement jsonElement) {
      final String s = jsonElement.getAsString();
      Sponge.getRegistry().getType(GameMode.class, s)
          .orElseThrow(() -> new IllegalStateException("Invalid GameMode String. Got: " + s));
    }
  }

  public static class StringSetSetting extends Setting<Set<String>> {
    @SuppressWarnings("unchecked")
    public StringSetSetting(Info info, Set<String> defaultValue) {
      super(info, defaultValue, (Class<Set<String>>) NopeTypeTokens.STRING_SET_TOKEN.getRawType());
    }

    @Override
    public JsonElement serialize(Set<String> value) {
      return new Gson().toJsonTree(value, NopeTypeTokens.STRING_SET_TOKEN.getType());
    }

    @Override
    public Set<String> deserialize(JsonElement jsonElement) {
      final Set<String> set = new HashSet<>();
      jsonElement.getAsJsonArray().forEach(element -> set.add(element.getAsString()));
      return set;
    }
  }


  public static class EntityTypeSetSetting extends Setting<Set<EntityType>> {
    @SuppressWarnings("unchecked")
    public EntityTypeSetSetting(Info info, Set<EntityType> defaultValue) {
      super(info, defaultValue, (Class<Set<EntityType>>) NopeTypeTokens.ENTITY_TYPE_SET_TOKEN.getRawType());
    }

    @Override
    public JsonElement serialize(Set<EntityType> value) {
      final JsonArray jsonArray = new JsonArray();
      for (EntityType entityType : value) {
        jsonArray.add(new JsonPrimitive(entityType.getId()));
      }
    }

    @Override
    public Set<EntityType> deserialize(JsonElement jsonElement) {
      final Set<EntityType> set = new HashSet<>();
      for (JsonElement element : jsonElement.getAsJsonArray()) {
        final String s = element.getAsString();
        final EntityType entityType = Sponge.getRegistry().getType(EntityType.class,s )
            .orElseThrow(() -> new IllegalStateException("Unknown EntityType: " + s));
        set.add(entityType);
      }
      return set;
    }
  }

  public static class Vector3DSetting extends Setting<Vector3d> {
    public Vector3DSetting(Info info, Vector3d defaultValue) {
      super(info, defaultValue, Vector3d.class);
    }

    @Override
    public JsonElement serialize(Vector3d value) {
      final JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("x", value.getX());
      jsonObject.addProperty("y", value.getY());
      jsonObject.addProperty("z", value.getZ());
      return jsonObject;
    }

    @Override
    public Vector3d deserialize(JsonElement jsonElement) {
      final JsonObject jsonObject = jsonElement.getAsJsonObject();
      return Vector3d.from(
          jsonObject.get("x").getAsDouble(),
          jsonObject.get("y").getAsDouble(),
          jsonObject.get("z").getAsDouble()
      );
    }
  }

  /* ======== */
  /* SETTINGS */
  /* ======== */


  public static final BooleanSetting BUILD_PERMISSIONS = new BooleanSetting(
      Setting.Info.builder()
          .id("build-permission-nodes-enable")
          .path("build-permission-nodes-enable")
          .implemented(false)
          .build(),
      false);

}
