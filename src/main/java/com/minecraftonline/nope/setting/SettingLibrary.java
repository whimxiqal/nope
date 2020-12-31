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
import com.minecraftonline.nope.util.Format;
import com.minecraftonline.nope.util.NopeTypeTokens;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class SettingLibrary {

  private static final HashMap<String, SettingKey<?>> settingMap = Maps.newHashMap();

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

  enum Movement {
    ALL,
    ONLY_TRANSLATION,
    ONLY_TELEPORTATION,
    NONE,
  }

  /**
   * Private constructor because its not supposed to be instantiated.
   */
  private SettingLibrary() {
  }

  private static void ensureInitialized() {
    if (settingMap.isEmpty()) throw new RuntimeException("The SettingLibrary must be initialized");
  }

  public static SettingKey<?> lookup(@Nonnull String id) throws NoSuchElementException {
    ensureInitialized();
    SettingKey<?> output = settingMap.get(id);
    if (output == null) {
      throw new NoSuchElementException(String.format(
              "There is no setting with id '%s'",
              id));
    }
    return output;
  }

  public static Collection<SettingKey<?>> getAll() {
    ensureInitialized();
    return settingMap.values();
  }

  public static void initialize() {
    Arrays.stream(SettingLibrary.class.getDeclaredFields())
            .filter(field -> Modifier.isStatic(field.getModifiers()))
            .filter(field -> SettingKey.class.isAssignableFrom(field.getType()))
            .forEach(field -> {
              try {
                SettingKey<?> key = (SettingKey<?>) field.get(null);
                if (settingMap.put(key.id, key) != null) {
                  throw new IllegalStateException("Settings may not have the same id: " + key.id);
                }
                for (Annotation annotation : field.getAnnotations()) {
                  if (annotation instanceof Description) {
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
    if (settingMap.isEmpty())
      throw new RuntimeException("Tried to initialize SettingLibrary, but it did not appear to work");
  }

  public static JsonElement serializeSettingAssignments(SettingMap map) {
    List<Map<String, Object>> settingList = Lists.newLinkedList();
    for (Setting<?> setting : map.entries()) {
      Map<String, Object> elem = Maps.newHashMap();
      elem.put("id", setting.getKey().getId());
      setting.getKey().getDescription().ifPresent(description -> elem.put("description", description));  // does not deserialize
      elem.put("value", setting.getKey().dataToJson(setting.getValue().getData()));
      elem.put("target", setting.getValue().getTarget());
      settingList.add(elem);
    }
    return new Gson().toJsonTree(settingList);
  }

  @SuppressWarnings("unchecked")
  public static SettingMap deserializeSettingAssignments(JsonElement json) {
    JsonElement element = json.getAsJsonObject().get("settings");
    SettingMap map = new SettingMap();

    if (element == null) {
      return map;
    }

    JsonArray serializedSettings = element.getAsJsonArray();
    for (JsonElement serializedSetting : serializedSettings) {
      JsonObject object = serializedSetting.getAsJsonObject();
      SettingKey<?> key = lookup(object.get("id").getAsString());
      SettingValue<Object> val = SettingValue.of(
              key.dataFromJson(object.get("value")),
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

//    @Override
//    public Boolean parse(String s) throws IllegalArgumentException {
//      switch(s.toLowerCase()) {
//        case "true": return true;
//        case "false": return false;
//        default: throw new IllegalArgumentException("Value must be true or false!");
//      }
//    }
  }

  public static class IntegerSetting extends SettingKey<Integer> {
    public IntegerSetting(String id, Integer defaultValue) {
      super(id, defaultValue);
    }

//    @Override
//    public Integer parse(String s) throws IllegalArgumentException {
//      try {
//        return Integer.parseInt(s);
//      } catch (NumberFormatException e) {
//        throw new IllegalArgumentException("Value must be a number", e);
//      }
//    }
  }

  public static class DoubleSetting extends SettingKey<Double> {
    public DoubleSetting(String id, Double defaultValue) {
      super(id, defaultValue);
    }
  }

  public static class StringSetting extends SettingKey<String> {
    public StringSetting(String id, String defaultValue) {
      super(id, defaultValue);
    }

//    @Override
//    public String parse(String s) throws IllegalArgumentException {
//      return s;
//    }
  }

  /* ====== */
  /* EXTRAS */
  /* ====== */

  public static final String SET_SPLIT_REGEX = "( )*,?( )*";  //"(, )|[ ,]";

  public static class StateSetting extends SettingKey<Boolean> {
    public StateSetting(String id, Boolean defaultValue) {
      super(id, defaultValue);
    }

    @Override
    public JsonElement dataToJsonGenerified(Boolean value) {
      return new JsonPrimitive(value ? "allow" : "deny");
    }

    @Override
    public Boolean dataFromJsonGenerified(JsonElement jsonElement) {
      final String s = jsonElement.getAsString();
      return parse(s);
    }

    @Override
    public Boolean parse(String s) throws IllegalArgumentException {
      switch (s) {
        case "allow":
          return true;
        case "deny":
          return false;
        default:
          throw new IllegalArgumentException("Invalid state string. Should be allow or deny. Was: " + s);
      }
    }
  }

  public static class TextSetting extends SettingKey<Text> {

    protected TextSetting(String id, Text defaultValue) {
      super(id, defaultValue);
    }

    @Override
    protected JsonElement dataToJsonGenerified(Text data) {
      try {
        return new Gson().toJsonTree(DataFormats.JSON.write(data.toContainer()));
      } catch (IOException e) {
        e.printStackTrace();
        return new Gson().toJsonTree("");
      }
    }

    @Override
    public Text dataFromJsonGenerified(JsonElement json) {
      try {
        return Sponge.getDataManager()
                .deserialize(Text.class, DataFormats.JSON.read(json.toString()))
                .orElseThrow(() -> new RuntimeException(
                        "The json for Text cannot be serialized: "
                                + json.toString()));
      } catch (IOException e) {
        e.printStackTrace();
        return Text.EMPTY;
      }
    }

    @Override
    public Text parse(String s) throws IllegalArgumentException {
      return TextSerializers.FORMATTING_CODE.deserialize(s);
    }
  }

  public static class EnumSetting<E extends Enum<E>> extends SettingKey<E> {

    private Class<E> enumClass;

    protected EnumSetting(String id, E defaultData, Class<E> enumClass) {
      super(id, defaultData);
      this.enumClass = enumClass;
    }

    @Override
    protected JsonElement dataToJsonGenerified(E data) {
      return new Gson().toJsonTree(data.name().toLowerCase());
    }

    @Override
    public E dataFromJsonGenerified(JsonElement json) {
      return parse(json.getAsString());
    }

    @Override
    public E parse(String s) throws IllegalArgumentException {
      return Enum.valueOf(enumClass, s);
    }
  }

  public static class CatalogTypeSetting<C extends CatalogType> extends SettingKey<C> {
    public CatalogTypeSetting(String id, C defaultValue) {
      super(id, defaultValue);
    }

    @Override
    public JsonElement dataToJsonGenerified(C value) {
      return new JsonPrimitive(cast(value).getId());
    }

    @Override
    public C dataFromJsonGenerified(JsonElement jsonElement) {
      final String s = jsonElement.getAsString();
      return parse(s);
    }

    @Override
    public C parse(String s) throws IllegalArgumentException {
      return Sponge.getRegistry()
              .getType(valueType(), s)
              .orElseThrow(() -> new IllegalStateException("Invalid GameMode String. Got: " + s));
    }
  }

  public static class StringSetSetting extends SettingKey<Set<String>> {
    public StringSetSetting(String id, Set<String> defaultValue) {
      super(id, defaultValue);
    }

    @Override
    public JsonElement dataToJsonGenerified(Set<String> value) {
      return new Gson().toJsonTree(value, NopeTypeTokens.STRING_SET_TOKEN.getType());
    }

    @Override
    public Set<String> dataFromJsonGenerified(JsonElement jsonElement) {
      final Set<String> set = new HashSet<>();
      jsonElement.getAsJsonArray().forEach(element -> set.add(element.getAsString()));
      return set;
    }

    @Override
    public Set<String> parse(String s) throws IllegalArgumentException {
      return Sets.newHashSet(s.split(SET_SPLIT_REGEX));
    }
  }

  public static class EntityTypeSetSetting extends SettingKey<Set<EntityType>> {
    public EntityTypeSetSetting(String id, Set<EntityType> defaultValue) {
      super(id, defaultValue);
    }

    @Override
    public JsonElement dataToJsonGenerified(Set<EntityType> value) {
      final JsonArray jsonArray = new JsonArray();
      for (EntityType entityType : value) {
        jsonArray.add(new JsonPrimitive(entityType.getId()));
      }
      return jsonArray;
    }

    @Override
    public Set<EntityType> dataFromJsonGenerified(JsonElement jsonElement) {
      return stringsToEntityTypes(Lists.newLinkedList(jsonElement.getAsJsonArray())
              .stream()
              .map(JsonElement::getAsString)
              .collect(Collectors.toList()));
    }

    @Override
    public Set<EntityType> parse(String s) throws IllegalArgumentException {
      return stringsToEntityTypes(Arrays.asList(s.split(SET_SPLIT_REGEX)));
    }

    private Set<EntityType> stringsToEntityTypes(Collection<String> strings) {
      Set<EntityType> set = new HashSet<>();
      for (String s : strings) {
        final EntityType entityType = Sponge.getRegistry()
                .getType(EntityType.class, s)
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
    public JsonElement dataToJsonGenerified(Vector3d value) {
      final JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("x", value.getX());
      jsonObject.addProperty("y", value.getY());
      jsonObject.addProperty("z", value.getZ());
      return jsonObject;
    }

    @Override
    public Vector3d dataFromJsonGenerified(JsonElement jsonElement) {
      final JsonObject jsonObject = jsonElement.getAsJsonObject();
      return Vector3d.from(
              jsonObject.get("x").getAsDouble(),
              jsonObject.get("y").getAsDouble(),
              jsonObject.get("z").getAsDouble()
      );
    }

    @Override
    public Vector3d parse(String s) throws IllegalArgumentException {
      String[] parts = s.split(SET_SPLIT_REGEX, 3);
      if (parts.length != 3) {
        throw new IllegalArgumentException("Expected 3 parts for Vector3d, got " + parts.length);
      }
      int i = 0;
      try {
        double x = Double.parseDouble(parts[i++]);
        double y = Double.parseDouble(parts[i++]);
        double z = Double.parseDouble(parts[i]);
        return Vector3d.from(x, y, z);
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Int number " + i + ", could not be parsed into a double");
      }
    }
  }

  /* ======== */
  /* SETTINGS */
  /* ======== */

  // TODO write description
  // TODO remove this? What is this?
  @NotImplemented
  public static final SettingKey<Boolean> BUILD_PERMISSIONS = new BooleanSetting(
          "build-permission-nodes-enable",
          false
  );


  @Description("Deop the player upon entering")
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

  @Description("When disabled, blocks may not be broken")
  @Category(SettingKey.CategoryType.BLOCKS)
  @NotImplemented
  public static final SettingKey<Boolean> BLOCK_BREAK = new StateSetting(
          "block-break",
          true
  );

  @Description("When disabled, blocks may not be placed")
  @Category(SettingKey.CategoryType.BLOCKS)
  @NotImplemented
  public static final SettingKey<Boolean> BLOCK_PLACE = new StateSetting(
          "block-place",
          true
  );

  @Description("When disabled, blocks like farmland may not be trampled")
  @Category(SettingKey.CategoryType.BLOCKS)
  @NotImplemented
  public static final SettingKey<Boolean> BLOCK_TRAMPLE = new StateSetting(
          "block-trample",
          true
  );

  // TODO write description. What does this do?
  public static final SettingKey<Boolean> FLAG_BUILD = new StateSetting(
          "flag-build",
          true
  );

  @Description("When disabled, players may not open chests")
  @Category(SettingKey.CategoryType.BLOCKS)
  @NotImplemented
  public static final SettingKey<Boolean> CHEST_ACCESS = new StateSetting(
          "chest-access",
          true
  );

  // TODO write description
  @NotImplemented
  public static final SettingKey<Boolean> CHORUS_FRUIT_TELEPORT = new StateSetting(
          "chorus-fruit-teleport",
          true
  );

  @Description("When disabled, coral does not fade")
  @Category(SettingKey.CategoryType.BLOCKS)
  @NotImplemented
  public static final SettingKey<Boolean> CORAL_FADE = new StateSetting(
          "coral-fade",
          true
  );

  @Description("When disabled, creepers do not cause damage")
  @NotImplemented
  public static final SettingKey<Boolean> CREEPER_EXPLOSION_DAMAGE = new StateSetting(
          "creeper-explosion-damage",
          true
  );

  @Description("When disabled, creepers do not grief when they explode")
  @NotImplemented
  public static final SettingKey<Boolean> CREEPER_EXPLOSION_GRIEF = new StateSetting(
          "creeper-explosion-grief",
          true
  );

  @Description("When disabled, crops do not grow")
  @NotImplemented
  public static final SettingKey<Boolean> CROP_GROWTH = new StateSetting(
          "crop-growth",
          true
  );

  @Description("When disabled, animals are invincible")
  @NotImplemented
  public static final SettingKey<Boolean> DAMAGE_ANIMALS = new StateSetting(
          "damage-animals",
          true
  );

  @Description("These entity types will not be allowed to spawn")
  @NotImplemented
  public static final SettingKey<Set<EntityType>> DENY_SPAWN = new EntityTypeSetSetting(
          "deny-spawn",
          Sets.newHashSet()
  );

  @Description("Enables block damage caused by the enderdragon")
  @NotImplemented
  public static final SettingKey<Boolean> ENDERDRAGON_BLOCK_DAMAGE = new StateSetting(
          "enderdragon-block-damage",
          true
  );

  @Description("When disabled, endermen do not grief blocks by picking them up")
  @NotImplemented
  public static final SettingKey<Boolean> ENDERMAN_GRIEF = new StateSetting(
          "enderman-grief",
          true
  );

  @Description("When disabled, enderpearls may not be used for teleportation")
  @NotImplemented
  public static final SettingKey<Boolean> ENDERPEARL = new StateSetting(
          "enderpearl",
          true
  );

  @Description("When disabled, armor stands may not be broken")
  @NotImplemented
  public static final SettingKey<Boolean> ARMOR_STAND_DESTROY = new StateSetting(
          "armor-stand-destroy",
          true
  );

  @Description("When disabled, item frames may not be broken")
  @NotImplemented
  public static final SettingKey<Boolean> ITEM_FRAME_DESTROY = new StateSetting(
          "item-frame-destroy",
          true
  );

  @Description("When disabled, paintings may not be broken")
  @NotImplemented
  public static final SettingKey<Boolean> PAINTING_DESTROY = new StateSetting(
          "painting-destroy",
          true
  );

  @Description("Specify which type of movement is allowed by players to enter. "
          + "Options: all, only_translation, only_teleportation, none")
  @NotImplemented
  public static final SettingKey<Movement> ENTRY = new EnumSetting<>(
          "entry",
          Movement.ALL,
          Movement.class
  );

  @Description("The message that is sent to a player if they are barred from entry")
  @NotImplemented
  public static final SettingKey<Text> ENTRY_DENY_MESSAGE = new TextSetting(
          "entry-deny-message",
          Format.error("You are not allowed to go there")
  );

  @Description("When disabled, players may not receive damage from the environment")
  @NotImplemented
  public static final SettingKey<Boolean> EVP = new StateSetting(
          "evp",
          true
  );

  @Description("Specify which type of movement is allowed by players to exit. "
          + "Options: all, only_translation, only_teleportation, none")
  @NotImplemented
  public static final SettingKey<Movement> EXIT = new EnumSetting<>(
          "exit",
          Movement.ALL,
          Movement.class
  );

  @Description("The message that is sent to the player if they are barred from exiting")
  @NotImplemented
  public static final SettingKey<Text> EXIT_DENY_MESSAGE = new TextSetting(
          "exit-deny-message",
          Format.error("You are not allowed to leave here")
  );

  @Description("When disabled, experience points are never dropped")
  @NotImplemented
  public static final SettingKey<Boolean> EXP_DROPS = new StateSetting(
          "exp-drops",
          false
  );

  @Description("When disabled, players do not experience fall damage")
  @NotImplemented
  public static final SettingKey<Boolean> FALL_DAMAGE = new StateSetting(
          "fall-damage",
          false
  );

  @Description("The message to a player when they leave")
  @NotImplemented
  public static final SettingKey<Text> FAREWELL = new TextSetting(
          "farewell",
          Text.EMPTY
  );

  @Description("The title that appears to a player when they leave")
  @NotImplemented
  public static final SettingKey<Text> FAREWELL_TITLE = new TextSetting(
          "farewell-title",
          Text.EMPTY
  );

  @Description("The amount of food restored with the feed command")
  @NotImplemented
  public static final SettingKey<Integer> FEED_AMOUNT = new IntegerSetting(
          "feed-amount",
          0
  );

  @Description("The amount of time before the feed command is used again by a player")
  @NotImplemented
  public static final SettingKey<Integer> FEED_DELAY = new IntegerSetting(
          "feed-delay",
          0
  );

  // TODO write description
  @NotImplemented
  public static final SettingKey<Double> FEED_MIN_HUNGER = new DoubleSetting(
          "feed-min-hunger",
          0D
  );

  @Description("When disabled, firework does not cause damage")
  @Category(SettingKey.CategoryType.DAMAGE)
  @NotImplemented
  public static final SettingKey<Boolean> FIREWORK_DAMAGE = new StateSetting(
          "firework-damage",
          true
  );

  @Description("When disabled, fire does not spread")
  @NotImplemented
  public static final SettingKey<Boolean> FIRE_SPREAD = new StateSetting(
          "fire-spread",
          true
  );

  @Description("When disabled, frosted ice does not form")
  @Category(SettingKey.CategoryType.BLOCKS)
  @NotImplemented
  public static final SettingKey<Boolean> FROSTED_ICE_FORM = new StateSetting(
          "frosted-ice-form",
          true
  );

  @Description("When disabled, frosted ice does not melt")
  @Category(SettingKey.CategoryType.BLOCKS)
  @NotImplemented
  public static final SettingKey<Boolean> FROSTED_ICE_MELT = new StateSetting(
          "frosted-ice-melt",
          true
  );

  @Description("The default gamemode of players")
  @NotImplemented
  public static final SettingKey<GameMode> GAME_MODE = new CatalogTypeSetting<>(
          "game-mode",
          GameModes.NOT_SET
  );

  @Description("When disabled, ghasts do not shoot fireball")
  @NotImplemented
  public static final SettingKey<Boolean> GHAST_FIREBALL = new StateSetting(
          "ghast-fireball",
          true
  );

  @Description("When disabled, grass does not grow naturally")
  @Category(SettingKey.CategoryType.BLOCKS)
  @NotImplemented
  public static final SettingKey<Boolean> GRASS_GROWTH = new StateSetting(
          "grass-growth",
          true
  );

  @Description("The message to a player when they enter")
  @NotImplemented
  public static final SettingKey<Text> GREETING = new TextSetting(
          "greeting",
          Text.EMPTY
  );

  @Description("The title that appears to a player when they enter")
  @NotImplemented
  public static final SettingKey<Text> GREETING_TITLE = new TextSetting(
          "greeting-title",
          Text.EMPTY
  );

  @Description("The amount of health restored with the heal command")
  @NotImplemented
  public static final SettingKey<Integer> HEAL_AMOUNT = new IntegerSetting(
          "heal-amount",
          0
  );

  @Description("The time delay before a player can use the heal command again")
  @NotImplemented
  public static final SettingKey<Integer> HEAL_DELAY = new IntegerSetting(
          "heal-delay",
          0
  );

  // TODO write description
  @NotImplemented
  public static final SettingKey<Double> HEAL_MAX_HEALTH = new DoubleSetting(
          "heal-max-health",
          0D
  );

  // TODO write description
  @NotImplemented
  public static final SettingKey<Double> HEAL_MAX_HUNGER = new DoubleSetting(
          "heal-max-hunger",
          0D
  );

  // TODO write description
  @NotImplemented
  public static final SettingKey<Double> HEAL_MIN_HEALTH = new DoubleSetting(
          "heal-min-health",
          0D
  );

  @Description("When disabled, ice does not form naturally")
  @NotImplemented
  public static final SettingKey<Boolean> ICE_FORM = new BooleanSetting(
          "ice-form",
          true
  );

  @Description("When disabled, players may not interact with any blocks")
  @Category(SettingKey.CategoryType.BLOCKS)
  @NotImplemented
  public static final SettingKey<Boolean> INTERACT = new BooleanSetting(
          "interact",
          true
  );

  @Description("When enabled, players cannot take damage")
  @NotImplemented
  public static final SettingKey<Boolean> INVINCIBLE = new BooleanSetting(
          "invincible",
          false
  );

  @Description("When disabled, players cannot drop items")
  @NotImplemented
  public static final SettingKey<Boolean> ITEM_DROP = new BooleanSetting(
          "item-drop",
          true
  );

  @Description("When disabled, players cannot pick up items")
  @NotImplemented
  public static final SettingKey<Boolean> ITEM_PICKUP = new BooleanSetting(
          "item-pickup",
          true
  );

  @Description("When disabled, lava does not cause fire")
  @NotImplemented
  public static final SettingKey<Boolean> LAVA_FIRE = new BooleanSetting(
          "lava-fire",
          true
  );

  @Description("When disabled, lava does not spread")
  @Category(SettingKey.CategoryType.BLOCKS)
  @NotImplemented
  public static final SettingKey<Boolean> LAVA_FLOW = new BooleanSetting(
          "lava-flow",
          true
  );

  @Description("When disabled, leaf will not decay naturally")
  @Category(SettingKey.CategoryType.BLOCKS)
  @NotImplemented
  public static final SettingKey<Boolean> LEAF_DECAY = new BooleanSetting(
          "leaf-decay",
          true
  );

  @Description("When disabled, players cannot directly light fire")
  @Category(SettingKey.CategoryType.BLOCKS)
  @NotImplemented
  public static final SettingKey<Boolean> LIGHTER = new BooleanSetting(
          "lighter",
          true
  );

  @Description("When disabled, lightning cannot strike")
  @Category(SettingKey.CategoryType.BLOCKS)
  @NotImplemented
  public static final SettingKey<Boolean> LIGHTNING = new BooleanSetting(
          "lightning",
          true
  );

  @Description("When disabled, mobs cannot take damage")
  @Category(SettingKey.CategoryType.DAMAGE)
  @NotImplemented
  public static final SettingKey<Boolean> MOB_DAMAGE = new BooleanSetting(
          "mob-damage",
          true
  );

  @Description("When disabled, mobs cannot spawn")
  @NotImplemented
  public static final SettingKey<Boolean> MOB_SPAWNING = new BooleanSetting(
          "mob-spawning",
          true
  );

  @Description("When disabled, mushrooms do not grow naturally")
  @NotImplemented
  public static final SettingKey<Boolean> MUSHROOM_GROWTH = new BooleanSetting(
          "mushroom-growth",
          true
  );

  @Description("When disabled, mycelium does not spread naturally")
  @NotImplemented
  public static final SettingKey<Boolean> MYCELIUM_SPREAD = new BooleanSetting(
          "mycelium-spread",
          true
  );

  @Description("When disabled, health does not regenerate naturally")
  @NotImplemented
  public static final SettingKey<Boolean> NATURAL_HEALTH_REGEN = new BooleanSetting(
          "natural-health-regen",
          true
  );

  @Description("When disabled, hunger does not drain naturally")
  @NotImplemented
  public static final SettingKey<Boolean> NATURAL_HUNGER_DRAIN = new BooleanSetting(
          "natural-hunger-drain",
          true
  );

  @Description("When enabled, players are notified when the enter")
  @NotImplemented
  public static final SettingKey<Boolean> NOTIFY_ENTER = new BooleanSetting(
          "notify-enter",
          false
  );

  @Description("When enabled, players are notified when they leave")
  @NotImplemented
  public static final SettingKey<Boolean> NOTIFY_LEAVE = new BooleanSetting(
          "notify-leave",
          false
  );

  // TODO write description
  @NotImplemented
  public static final SettingKey<Boolean> OTHER_EXPLOSION = new BooleanSetting(
          "other-explosion",
          true
  );

  @Description("When disabled, all events caused by player collision are cancelled")
  @NotImplemented
  public static final SettingKey<Boolean> PLAYER_COLLISION = new StateSetting(
          "player-collision",
          true
  );

  @Description("When disabled, players cannot inflict damage on entities")
  @NotImplemented
  public static final SettingKey<Boolean> PVE = new StateSetting(
          "pve",
          true
  );

  @Description("When disabled, players cannot inflict damage on other players")
  @NotImplemented
  public static final SettingKey<Boolean> PVP = new StateSetting(
          "pvp",
          true
  );

  @Description("When disabled, players cannot ride other entities")
  @NotImplemented
  public static final SettingKey<Boolean> RIDE = new StateSetting(
          "ride",
          true
  );

  @Description("When disabled, players cannot sleep")
  @NotImplemented
  public static final SettingKey<Boolean> SLEEP = new StateSetting(
          "sleep",
          true
  );

  @Description("When disabled, snowmen do not make trails")
  @Category(SettingKey.CategoryType.BLOCKS)
  @NotImplemented
  public static final SettingKey<Boolean> SNOWMAN_TRAILS = new StateSetting(
          "snowman-trails",
          true
  );

  @Description("When disabled, snow does not fall")
  @Category(SettingKey.CategoryType.BLOCKS)
  @NotImplemented
  public static final SettingKey<Boolean> SNOW_FALL = new StateSetting(
          "snow-fall",
          true
  );

  @Description("When disabled, snow does not melt")
  @Category(SettingKey.CategoryType.BLOCKS)
  @NotImplemented
  public static final SettingKey<Boolean> SNOW_MELT = new StateSetting(
          "snow-melt",
          true
  );

  // TODO write description
  @Category(SettingKey.CategoryType.BLOCKS)
  @NotImplemented
  public static final SettingKey<Boolean> SOIL_DRY = new StateSetting(
          "soil-dry",
          true
  );

  // TODO write description
  @NotImplemented
  public static final SettingKey<Vector3d> SPAWN = new Vector3DSetting(
          "spawn",
          Vector3d.ZERO
  );

  @Description("When disabled, players may not teleport in or out")
  @NotImplemented
  public static final SettingKey<Vector3d> TELEPORT_LOCATION = new Vector3DSetting(
          "teleport",
          Vector3d.ZERO
  );

  // TODO write description
  @NotImplemented
  public static final SettingKey<String> TIME_LOCK = new StringSetting(
          "time-lock",
          ""
  );

  @Description("When disabled, tnt may not be placed or activated")
  @NotImplemented
  public static final SettingKey<Boolean> FLAG_TNT = new StateSetting(
          "tnt",
          true
  );

  // TODO write description
  @NotImplemented
  public static final SettingKey<Boolean> USE = new StateSetting(
          "use",
          true
  );

  @Description("When disabled, players may not break vehicles")
  @NotImplemented
  public static final SettingKey<Boolean> VEHICLE_DESTROY = new StateSetting(
          "vehicle-destroy",
          true
  );

  @Description("When disabled, players may not place vehicles")
  @NotImplemented
  public static final SettingKey<Boolean> VEHICLE_PLACE = new StateSetting(
          "vehicle-place",
          true
  );

  @Description("When disabled, vines do not grow naturally")
  @NotImplemented
  public static final SettingKey<Boolean> VINE_GROWTH = new StateSetting(
          "vine-growth",
          true
  );

  @Description("When disabled, water cannot flow")
  @NotImplemented
  public static final SettingKey<Boolean> WATER_FLOW = new StateSetting(
          "water-flow",
          true
  );

  @Description("When disabled, the wither does not cause any damage to players")
  @NotImplemented
  public static final SettingKey<Boolean> WITHER_DAMAGE = new StateSetting(
          "wither-damage",
          true
  );

  // TODO write description
  @NotImplemented
  public static final SettingKey<Boolean> OP_PERMISSIONS = new StateSetting(
          "op-permissions",
          true
  );

  @Description("The Data Source name of the SQL database to be used if SQL is the storage type")
  @NotImplemented
  public static final SettingKey<String> SQL_DSN = new StringSetting(
          "sql-dsn",
          "jdbc:mysql://localhost/nope"
  );

  @Description("The password for the SQL database to be used if SQL is the storage type")
  @NotImplemented
  public static final SettingKey<String> SQL_PASSWORD = new StringSetting(
          "sql-password",
          "nope"
  );

  @Description("The table prefix to be placed before SQL tables if SQL is the storage type")
  @NotImplemented
  public static final SettingKey<String> SQL_TABLE_PREFIX = new StringSetting(
          "sql-table-prefix",
          "nope"
  );

  @Description("The username for the SQL database to be used if SQL is the storage type")
  @NotImplemented
  public static final SettingKey<String> SQL_USERNAME = new StringSetting(
          "sql-username",
          "nope"
  );

  enum StorageType {
    MariaDB,
    SQLite,
    HOCON
  }

  @Description("The type of storage to persist Nope server state")
  @NotImplemented
  public static final SettingKey<StorageType> STORAGE_TYPE = new EnumSetting<StorageType>(
          "storage-type",
          StorageType.HOCON,
          StorageType.class
  );

  @Description("The type of item to be used as the Nope wand")
  @NotImplemented
  public static final SettingKey<ItemType> WAND_ITEM = new CatalogTypeSetting<>(
          "wand-item",
          ItemTypes.STICK
  );

}
