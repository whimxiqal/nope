package com.minecraftonline.nope.config.serializer;

import com.google.common.reflect.TypeToken;
import com.minecraftonline.nope.control.flags.FlagEntitySet;
import com.minecraftonline.nope.control.flags.FlagUtil;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.util.TypeTokens;

import java.util.HashSet;
import java.util.Set;

public class FlagEntitySetSerializer implements TypeSerializer<FlagEntitySet> {
  @Nullable
  @Override
  public FlagEntitySet deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
    Set<EntityType> set = new HashSet<>(value.getList(TypeTokens.ENTITY_TYPE_TOKEN));
    FlagEntitySet flagEntitySet = new FlagEntitySet(set);
    FlagUtil.deserializeGroup(flagEntitySet, value);
    return flagEntitySet;
  }

  @Override
  public void serialize(@NonNull TypeToken<?> type, @Nullable FlagEntitySet obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
    FlagUtil.serialize(obj, value);
  }
}
