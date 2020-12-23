package com.minecraftonline.nope.config.configurate.serializer;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonPrimitive;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class JsonPrimitiveSerializer implements TypeSerializer<JsonPrimitive> {
    @Override
    public @Nullable JsonPrimitive deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        final Object obj = value.getValue();

        if (obj == null) {
            return null;
        }

        final JsonPrimitive jsonPrimitive;

        if (obj instanceof Boolean) {
            jsonPrimitive = new JsonPrimitive((Boolean) obj);
        }
        else if (obj instanceof Number) {
            jsonPrimitive = new JsonPrimitive((Number) obj);
        }
        else if (obj instanceof String) {
            jsonPrimitive = new JsonPrimitive((String) obj);
        }
        else if (obj instanceof Character) {
            jsonPrimitive = new JsonPrimitive((Character) obj);
        }
        else {
            throw new IllegalStateException("Config Node Value: " + obj.getClass().getName() + ", " + obj);
        }
        return jsonPrimitive;
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable JsonPrimitive obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        if (obj == null) {
            return;
        }

        final Object jsonValue;
        if (obj.isBoolean()) {
            jsonValue = obj.getAsBoolean();
        }
        else if (obj.isNumber()) {
            jsonValue = obj.getAsNumber();
        }
        else if (obj.isString()) {
            jsonValue = obj.getAsString();
        }
        else {
            throw new IllegalStateException("Unknown primitive type: " + obj.getAsString());
        }
        value.setValue(jsonValue);
    }
}
