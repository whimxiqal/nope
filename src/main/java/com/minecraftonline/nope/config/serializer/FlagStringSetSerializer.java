package com.minecraftonline.nope.config.serializer;

import com.google.common.reflect.TypeToken;
import com.minecraftonline.nope.control.flags.FlagStringSet;
import com.minecraftonline.nope.control.flags.FlagUtil;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.util.TypeTokens;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FlagStringSetSerializer implements TypeSerializer<FlagStringSet> {
  @Nullable
  @Override
  public FlagStringSet deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
    Set<String> stringSet = new HashSet<>(value.getList(TypeTokens.STRING_TOKEN));
    FlagStringSet flagStringSet = new FlagStringSet(stringSet);
    FlagUtil.deserializeGroup(flagStringSet, value);
    return flagStringSet;
  }

  @Override
  public void serialize(@NonNull TypeToken<?> type, @Nullable FlagStringSet obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
    FlagUtil.serialize(obj, value);
  }
}
