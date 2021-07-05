package com.minecraftonline.nope.config.configurate.serializer;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.minecraftonline.nope.util.NopeTypeTokens;
import java.util.Map;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Serializer for JSON.
 */
public class JsonElementSerializer implements TypeSerializer<JsonElement> {

  private final JsonPrimitiveSerializer primitiveSerializer = new JsonPrimitiveSerializer();

  @Nullable
  @Override
  public JsonElement deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value)
      throws ObjectMappingException {
    final Object obj = value.getValue();
    if (obj == null) {
      return null;
    }
    final JsonElement element = new Gson().toJsonTree(obj);
    if (element.isJsonPrimitive()) {
      return primitiveSerializer.deserialize(NopeTypeTokens.JSON_PRIM_TT, value);
    }
    if (value.isList()) {
      JsonArray array = new JsonArray();
      for (ConfigurationNode node : value.getChildrenList()) {
        // Recursive - keep going down until we get the full "tree"

        array.add(node.getValue(NopeTypeTokens.JSON_ELEM_TT));
      }
      return array;
    } else if (value.isMap()) {
      JsonObject object = new JsonObject();
      for (Map.Entry<Object, ? extends ConfigurationNode> entry
          : value.getChildrenMap().entrySet()) {
        final String key = entry.getKey().toString();
        final JsonElement jsonElement = entry.getValue()
            .getValue(NopeTypeTokens.JSON_ELEM_TT);
        object.add(key, jsonElement);
      }
      return object;
    }
    throw new IllegalStateException("Unable to deserialize node with value: "
        + obj
        + ", class: "
        + obj.getClass().getName());
  }

  @Override
  public void serialize(@NonNull TypeToken<?> type,
                        @Nullable JsonElement obj,
                        @NonNull ConfigurationNode value) throws ObjectMappingException {
    if (obj == null) {
      return;
    }
    if (obj.isJsonPrimitive()) {
      // Parse primitive types.
      final JsonPrimitive jsonPrimitive = obj.getAsJsonPrimitive();
      primitiveSerializer.serialize(NopeTypeTokens.JSON_PRIM_TT, jsonPrimitive, value);
    } else if (obj.isJsonArray()) {
      final JsonArray jsonArray = obj.getAsJsonArray();
      value.setValue(null); // Clear any current items in list to avoid adding duplicates.
      for (JsonElement element : jsonArray) {
        value.appendListNode().setValue(NopeTypeTokens.JSON_ELEM_TT, element);
      }
    } else if (obj.isJsonObject()) {
      final JsonObject jsonObject = obj.getAsJsonObject();
      for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
        value.getNode(entry.getKey())
            .setValue(NopeTypeTokens.JSON_ELEM_TT, entry.getValue());
      }
    } else {
      throw new IllegalStateException("Unexpected extra type of JsonElement "
          + "found while serializing: Class"
          + obj.getClass()
          + ". Json:"
          + obj.getAsString());
    }
  }

  private static class JsonPrimitiveSerializer implements TypeSerializer<JsonPrimitive> {
    @Override
    public @Nullable JsonPrimitive deserialize(@NonNull TypeToken<?> type,
                                               @NonNull ConfigurationNode value) {
      final Object obj = value.getValue();
      if (obj == null) {
        return null;
      }
      final JsonPrimitive jsonPrimitive;

      if (obj instanceof Boolean) {
        jsonPrimitive = new JsonPrimitive((Boolean) obj);
      } else if (obj instanceof Number) {
        jsonPrimitive = new JsonPrimitive((Number) obj);
      } else if (obj instanceof String) {
        jsonPrimitive = new JsonPrimitive((String) obj);
      } else if (obj instanceof Character) {
        jsonPrimitive = new JsonPrimitive((Character) obj);
      } else {
        throw new IllegalStateException("Config Node Value: "
            + obj.getClass().getName()
            + ", "
            + obj);
      }
      return jsonPrimitive;
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type,
                          @Nullable JsonPrimitive obj,
                          @NonNull ConfigurationNode value) {
      if (obj == null) {
        return;
      }
      final Object jsonValue;
      if (obj.isBoolean()) {
        jsonValue = obj.getAsBoolean();
      } else if (obj.isNumber()) {
        jsonValue = obj.getAsNumber();
      } else if (obj.isString()) {
        jsonValue = obj.getAsString();
      } else {
        throw new IllegalStateException("Unknown primitive type: " + obj.getAsString());
      }
      value.setValue(jsonValue);
    }
  }
}
