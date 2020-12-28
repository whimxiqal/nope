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

package com.minecraftonline.nope.setting;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.minecraftonline.nope.util.NopeTypeTokens;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;
import java.util.*;

public class SettingLibrary {

  private static final HashMap<String, SettingKey<?>> settingMap = Maps.newHashMap();

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface Comment {
    String value();
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface Description {
    String value();
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface Category {
    SettingKey.CategoryType value();
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface NotImplemented {
    // Empty
  }

  /**
   * Private constructor because its not supposed to be instantiated.
   */
  private SettingLibrary() {
  }

  public static SettingKey<?> lookup(@Nonnull String id) throws NoSuchElementException {
    if (settingMap.isEmpty()) throw new RuntimeException("The SettingLibrary must be initialized");
    SettingKey<?> output = settingMap.get(id);
    if (output == null) {
      throw new NoSuchElementException(String.format(
              "There is no setting with id '%s'",
              id));
    }
    return output;
  }

  public static void initialize() {
    Arrays.stream(SettingLibrary.class.getDeclaredFields())
            .filter(field -> Modifier.isStatic(field.getModifiers()))
            .filter(field -> Setting.class.isAssignableFrom(field.getType()))
            .forEach(field -> {
              try {
                SettingKey<?> key = (SettingKey<?>) field.get(null);
                if (settingMap.put(key.id, key) != null) {
                  throw new IllegalStateException("Settings may not have the same id: " + key.id);
                }
                for (Annotation annotation : field.getAnnotations()) {
                  if (annotation instanceof Comment) {
                    key.comment = ((Comment) annotation).value();
                  } else if (annotation instanceof Description) {
                    key.description = ((Description) annotation).value();
                  } else if (annotation instanceof Category) {
                    key.category = ((Category) annotation).value();
                  } else if (annotation instanceof NotImplemented) {
                    key.implemented = false;
                  }
                }
              } catch (IllegalAccessException e) {
                e.printStackTrace();
              }
            });
  }

  public static JsonElement serializeSettingAssignments(SettingMap map) {
    List<Map<String, Object>> settingList = Lists.newLinkedList();
    for (Setting<?> setting : map.entries()) {
      Map<String, Object> elem = Maps.newHashMap();
      elem.put("id", setting.getKey().getId());
      setting.getKey().getComment().ifPresent(comment -> elem.put("comment", comment));  // does not deserialize
      setting.getKey().getDescription().ifPresent(description -> elem.put("description", description));  // does not deserialize
      elem.put("value", setting.getKey().encodeData(setting.getValue().getData()));
      elem.put("target", setting.getValue().getTarget());
      settingList.add(elem);
    }
    return new Gson().toJsonTree(settingList);
  }

  @SuppressWarnings("unchecked")
  public static SettingMap deserializeSettingAssignments(JsonElement json) {
    JsonArray serializedSettings = json.getAsJsonObject().get("settings").getAsJsonArray();
    SettingMap map = new SettingMap();
    for (JsonElement serializedSetting : serializedSettings) {
      JsonObject object = serializedSetting.getAsJsonObject();
      SettingKey<?> key = lookup(object.get("id").getAsString());
      SettingValue<Object> val = SettingValue.of(
              key.parseData(object.get("value")),
              new Gson().fromJson(object.get("target"), SettingValue.Target.class));
      map.put(Setting.of((SettingKey<Object>) key, val));
    }
    return map;
  }

  /* ========== */
  /* PRIMITIVES */
  /* ========== */


  public static class BooleanSetting extends SettingKey<Boolean> {
    public BooleanSetting(String id, Boolean defaultValue) {
      super(id, defaultValue);
    }
  }

  public static class IntegerSetting extends SettingKey<Integer> {
    public IntegerSetting(String id, Integer defaultValue) {
      super(id, defaultValue);
    }
  }

  public static class StringSetting extends SettingKey<String> {
    public StringSetting(String id, String defaultValue) {
      super(id, defaultValue);
    }
  }

  /* ====== */
  /* EXTRAS */
  /* ====== */


  public static class StateSetting extends SettingKey<Boolean> {
    public StateSetting(String id, Boolean defaultValue) {
      super(id, defaultValue);
    }

    @Override
    public JsonElement encodeGenerifiedData(Boolean value) {
      return new JsonPrimitive(value ? "allow" : "deny");
    }

    @Override
    public Boolean parseGenerifiedData(JsonElement jsonElement) {
      final String s = jsonElement.getAsString();
      switch (s) {
        case "allow":
          return true;
        case "deny":
          return false;
        default:
          throw new IllegalStateException("Invalid state string. Should be allow or deny. Was: " + s);
      }
    }
  }

  public static class GameModeSetting extends SettingKey<GameMode> {
    public GameModeSetting(String id, GameMode defaultValue) {
      super(id, defaultValue);
    }

    @Override
    public JsonElement encodeGenerifiedData(GameMode value) {
      return new JsonPrimitive(cast(value).getId());
    }

    @Override
    public GameMode parseGenerifiedData(JsonElement jsonElement) {
      final String s = jsonElement.getAsString();
      return Sponge.getRegistry().getType(GameMode.class, s)
              .orElseThrow(() -> new IllegalStateException("Invalid GameMode String. Got: " + s));
    }
  }

  public static class StringSetSetting extends SettingKey<Set<String>> {
    public StringSetSetting(String id, Set<String> defaultValue) {
      super(id, defaultValue);
    }

    @Override
    public JsonElement encodeGenerifiedData(Set<String> value) {
      return new Gson().toJsonTree(value, NopeTypeTokens.STRING_SET_TOKEN.getType());
    }

    @Override
    public Set<String> parseGenerifiedData(JsonElement jsonElement) {
      final Set<String> set = new HashSet<>();
      jsonElement.getAsJsonArray().forEach(element -> set.add(element.getAsString()));
      return set;
    }
  }


  public static class EntityTypeSetSetting extends SettingKey<Set<EntityType>> {
    public EntityTypeSetSetting(String id, Set<EntityType> defaultValue) {
      super(id, defaultValue);
    }

    @Override
    public JsonElement encodeGenerifiedData(Set<EntityType> value) {
      final JsonArray jsonArray = new JsonArray();
      for (EntityType entityType : value) {
        jsonArray.add(new JsonPrimitive(entityType.getId()));
      }
      return jsonArray;
    }

    @Override
    public Set<EntityType> parseGenerifiedData(JsonElement jsonElement) {
      final Set<EntityType> set = new HashSet<>();
      for (JsonElement element : jsonElement.getAsJsonArray()) {
        final String s = element.getAsString();
        final EntityType entityType = Sponge.getRegistry().getType(EntityType.class, s)
                .orElseThrow(() -> new IllegalStateException("Unknown EntityType: " + s));
        set.add(entityType);
      }
      return set;
    }
  }

  public static class Vector3DSetting extends SettingKey<Vector3d> {
    public Vector3DSetting(String id, Vector3d defaultValue) {
      super(id, defaultValue);
    }

    @Override
    public JsonElement encodeGenerifiedData(Vector3d value) {
      final JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("x", value.getX());
      jsonObject.addProperty("y", value.getY());
      jsonObject.addProperty("z", value.getZ());
      return jsonObject;
    }

    @Override
    public Vector3d parseGenerifiedData(JsonElement jsonElement) {
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


  @NotImplemented
  public static final SettingKey<Boolean> BUILD_PERMISSIONS = new BooleanSetting(
          "build-permission-nodes-enable",
          false
  );


  @Comment("Set to true will deop any player when they enter")
  @Description("If this setting is applied globally, then anytime "
                  + "and op-ed player joins the server, their op status is removed. "
                  + "If this setting is applied to just a world, then only "
                  + "when they join that specific world do they get de-opped.")
  @NotImplemented
  public static final SettingKey<Boolean> DEOP_ON_ENTER = new BooleanSetting(
          "deop-on-enter",
          false
  );

  @Description("Enables all plugin functionality. Can only be set globally.")
  @NotImplemented
  public static final SettingKey<Boolean> ENABLE_PLUGIN = new BooleanSetting(
          "enable-plugin",
          true
  );

  @NotImplemented
  public static final SettingKey<Set<String>> ALLOWED_COMMANDS = new StringSetSetting(
          "allowed-cmds",
          Sets.newHashSet()
  );

  @NotImplemented
  public static final SettingKey<Set<String>> BLOCKED_COMMANDS = new StringSetSetting(
          "blocked-cmds",
          Sets.newHashSet()
  );

  @Category(SettingKey.CategoryType.BLOCKS)
  @NotImplemented
  public static final SettingKey<Boolean> BLOCK_BREAK = new StateSetting(
          "block-break",
          true
  );

  @Category(SettingKey.CategoryType.BLOCKS)
  @NotImplemented
  public static final SettingKey<Boolean> BLOCK_PLACE = new StateSetting(
          "block-place",
          true
  );

  @Category(SettingKey.CategoryType.BLOCKS)
  @NotImplemented
  public static final SettingKey<Boolean> BLOCK_TRAMPLE = new StateSetting(
          "block-trample",
          true
  );

  public static final SettingKey<Boolean> FLAG_BUILD = new StateSetting(
          "flag-build",
          true
  );

  @Category(SettingKey.CategoryType.BLOCKS)
  @NotImplemented
  public static final SettingKey<Boolean> CHEST_ACCESS = new StateSetting(
          "chest-access",
          true
  );
}
