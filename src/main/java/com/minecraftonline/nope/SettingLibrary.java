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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.minecraftonline.nope.control.flags.Flag;
import com.minecraftonline.nope.util.NopeTypeTokens;
import lombok.Getter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;

import java.lang.annotation.*;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import java.lang.reflect.Modifier;
import java.util.*;

public class SettingLibrary {

  private static final HashMap<String, Setting<?>> settingMap = Maps.newHashMap();

  /**
   * Private constructor because its not supposed to be instantiated.
   */
  private SettingLibrary() {
  }

  public static Setting<?> lookup(@Nonnull String id) throws NoSuchElementException {
    if (settingMap.isEmpty()) throw new RuntimeException("The SettingLibrary must be initialized");
    Setting<?> output = settingMap.get(id);
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
            .filter(field -> field.getType().equals(Setting.class))
            .forEach(field -> {
              try {
                Setting<?> setting = (Setting<?>) field.get(null);
                if (settingMap.put(setting.id, setting) != null) {
                  throw new IllegalStateException("Settings may not have the same id: " + setting.id);
                }
                for (Annotation annotation : field.getAnnotations()) {
                  if (annotation instanceof Setting.Comment) {
                    setting.comment = ((Setting.Comment) annotation).comment();
                  } else if (annotation instanceof Setting.Description) {
                    setting.description = ((Setting.Description) annotation).description();
                  } else if (annotation instanceof Setting.Category) {
                    setting.category = ((Setting.Category) annotation).category();
                  } else if (annotation instanceof Setting.NotImplemented) {
                    setting.implemented = false;
                  }
                }
              } catch (IllegalAccessException e) {
                e.printStackTrace();
              }
            });
  }

  public static JsonElement serializeSettingAssignments(Map<Setting<?>, Object> assignments) {
    List<Map<String, Object>> settingList = Lists.newLinkedList();
    for (Map.Entry<Setting<?>, Object> assignment : assignments.entrySet()) {
      Setting<?> setting = assignment.getKey();
      Map<String, Object> elem = Maps.newHashMap();
      elem.put("id", setting.getId());
      setting.getComment().ifPresent(comment -> elem.put("comment", comment));  // does not deserialize
      setting.getDescription().ifPresent(description -> elem.put("description", description));  // does not deserialize
      elem.put("value", setting.encodeValue(assignment.getValue()));
      settingList.add(elem);
    }
    return new Gson().toJsonTree(settingList);
  }

  public static Map<Setting<?>, Object> deserializeSettingAssignments(JsonElement json) {
    JsonArray serializedSettings = json.getAsJsonObject().get("settings").getAsJsonArray();
    Map<Setting<?>, Object> assignments = Maps.newHashMap();
    for (JsonElement serializedSetting : serializedSettings) {
      JsonObject object = serializedSetting.getAsJsonObject();
      Setting<?> setting = lookup(object.get("id").getAsString());
      assignments.put(setting, setting.parseValue(object.get("value")));
    }
    return assignments;
  }

  public abstract static class Setting<T> {

    // This order of these affects how they will be sorted in
    // /nope setting list, so keep MISC last
    public enum CategoryType {
      BLOCKS,
      MOVEMENT,
      DAMAGE,
      MISC,
    }

    @Getter
    private final String id;
    @Getter
    private final T defaultValue;

    private final Class<T> valueType;

    public Setting(String id, T defaultValue, Class<T> valueType) {
      this.id = id;
      this.defaultValue = defaultValue;
      this.valueType = valueType;
    }

    private String comment = null;
    private String description = null;
    private CategoryType category = CategoryType.MISC;
    private boolean implemented = true;

    public final JsonElement encodeValue(Object value) {
      return encodeGenerifiedValue(castValue(value));
    }

    public JsonElement encodeGenerifiedValue(T value) {
      return new Gson().toJsonTree(value);
    }

    public final Object parseValue(JsonElement json) {
      return parseGenerifiedValue(json);
    }

    public T parseGenerifiedValue(JsonElement json) {
      return new Gson().fromJson(json, valueType);
    }

    public final T castValue(Object object) {
      if (!valueType.isInstance(object)) {
        throw new IllegalArgumentException(String.format(
                "input %s must be of type %s",
                object.getClass().getName(),
                valueType.getName()));
      }
      return valueType.cast(object);
    }

    /* Reflections */

    public final Optional<String> getComment() {
      return Optional.of(comment);
    }

    public final Optional<String> getDescription() {
      return Optional.of(description);
    }

    public final boolean isImplemented() {
      return implemented;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Comment {
      String comment();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Description {
      String description();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Category {
      CategoryType category();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface NotImplemented {
      // Empty
    }

  }

  /* ========== */
  /* PRIMITIVES */
  /* ========== */


  public static class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting(String id, Boolean defaultValue, CategoryType category) {
      super(id, defaultValue, Boolean.class);
    }
  }

  public static class IntegerSetting extends Setting<Integer> {
    public IntegerSetting(String id, Integer defaultValue, CategoryType category) {
      super(id, defaultValue, Integer.class);
    }
  }

  public static class StringSetting extends Setting<String> {
    public StringSetting(String id, String defaultValue, CategoryType category) {
      super(id, defaultValue, String.class);
    }
  }

  /* ====== */
  /* EXTRAS */
  /* ====== */


  public static class StateSetting extends Setting<Boolean> {
    public StateSetting(String id, Boolean defaultValue, CategoryType category) {
      super(id, defaultValue, Boolean.class);
    }

    @Override
    public JsonElement encodeGenerifiedValue(Boolean value) {
      return new JsonPrimitive(value ? "allow" : "deny");
    }

    @Override
    public Boolean parseGenerifiedValue(JsonElement jsonElement) {
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

  public static class GameModeSetting extends Setting<GameMode> {
    public GameModeSetting(String id, GameMode defaultValue, CategoryType category) {
      super(id, defaultValue, GameMode.class);
    }

    @Override
    public JsonElement encodeGenerifiedValue(GameMode value) {
      return new JsonPrimitive(castValue(value).getId());
    }

    @Override
    public GameMode parseGenerifiedValue(JsonElement jsonElement) {
      final String s = jsonElement.getAsString();
      return Sponge.getRegistry().getType(GameMode.class, s)
              .orElseThrow(() -> new IllegalStateException("Invalid GameMode String. Got: " + s));
    }
  }

  public static class StringSetSetting extends Setting<Set<String>> {
    @SuppressWarnings("unchecked")
    public StringSetSetting(String id, Set<String> defaultValue, CategoryType category) {
      super(id, defaultValue, (Class<Set<String>>) NopeTypeTokens.STRING_SET_TOKEN.getRawType());
    }

    @Override
    public JsonElement encodeGenerifiedValue(Set<String> value) {
      return new Gson().toJsonTree(value, NopeTypeTokens.STRING_SET_TOKEN.getType());
    }

    @Override
    public Set<String> parseGenerifiedValue(JsonElement jsonElement) {
      final Set<String> set = new HashSet<>();
      jsonElement.getAsJsonArray().forEach(element -> set.add(element.getAsString()));
      return set;
    }
  }


  public static class EntityTypeSetSetting extends Setting<Set<EntityType>> {
    @SuppressWarnings("unchecked")
    public EntityTypeSetSetting(String id, Set<EntityType> defaultValue, CategoryType category) {
      super(id, defaultValue, (Class<Set<EntityType>>) NopeTypeTokens.ENTITY_TYPE_SET_TOKEN.getRawType());
    }

    @Override
    public JsonElement encodeGenerifiedValue(Set<EntityType> value) {
      final JsonArray jsonArray = new JsonArray();
      for (EntityType entityType : value) {
        jsonArray.add(new JsonPrimitive(entityType.getId()));
      }
      return jsonArray;
    }

    @Override
    public Set<EntityType> parseGenerifiedValue(JsonElement jsonElement) {
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

  public static class Vector3DSetting extends Setting<Vector3d> {
    public Vector3DSetting(String id, Vector3d defaultValue, CategoryType category) {
      super(id, defaultValue, Vector3d.class);
    }

    @Override
    public JsonElement encodeGenerifiedValue(Vector3d value) {
      final JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("x", value.getX());
      jsonObject.addProperty("y", value.getY());
      jsonObject.addProperty("z", value.getZ());
      return jsonObject;
    }

    @Override
    public Vector3d parseGenerifiedValue(JsonElement jsonElement) {
      final JsonObject jsonObject = jsonElement.getAsJsonObject();
      return Vector3d.from(
              jsonObject.get("x").getAsDouble(),
              jsonObject.get("y").getAsDouble(),
              jsonObject.get("z").getAsDouble()
      );
    }
  }

  public static class FlagSetting extends Setting<Flag<?>> {

    public FlagSetting(String id, Flag<?> defaultValue, Class<Flag<?>> valueType) {
      super(id, defaultValue, valueType);
    }
  }

  /* ======== */
  /* SETTINGS */
  /* ======== */


  @Setting.NotImplemented
  public static final BooleanSetting BUILD_PERMISSIONS = new BooleanSetting(
          "build-permission-nodes-enable",
          false,
          Setting.CategoryType.MISC);


  @Setting.Comment(comment = "Set to true will deop any player when they enter")
  @Setting.Description(description =
          "If this setting is applied globally, then anytime "
                  + "and op-ed player joins the server, their op status is removed. "
                  + "If this setting is applied to just a world, then only "
                  + "when they join that specific world do they get de-opped.")
  @Setting.NotImplemented
  public static final BooleanSetting DEOP_ON_ENTER = new BooleanSetting(
          "deop-on-enter",
          false,
          Setting.CategoryType.MISC);
}
