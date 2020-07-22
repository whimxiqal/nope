package com.minecraftonline.nope.control.flags;

import com.minecraftonline.nope.config.GlobalConfigManager;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.Optional;

/**
 * Represents a WG Flag
 * @param <T> Value of the flag, that must, if not default-ly serializable
 *           by configurate, have a serializer added to {@link GlobalConfigManager#getTypeSerializers()}
 */
public class Flag<T> implements Serializable {
  public enum TargetGroup {
    ALL,
    OWNERS,
    MEMBERS,
    NONMEMBERS,
    NONOWNERS
  }

  private TargetGroup group;
  private T value;
  private Class<T> clazz;

  public Flag(T value, Class<T> clazz) {
    this(value, clazz, TargetGroup.ALL);
  }

  public Flag(T value, Class<T> clazz, TargetGroup group) {
    this.value = value;
    this.clazz = clazz;
    this.group = group;
  }

  public Class<T> getFlagType() {
    return this.clazz;
  }

  public T getValue() {
    return value;
  }

  public void setValue(T value) {
    this.value = value;
  }

  public TargetGroup getGroup() {
    return group;
  }

  public void setGroup(TargetGroup group) {
    this.group = group;
  }

  /**
   * Whether {@link #serialize(Flag)} should be used for serializing
   * into configurate, or if not - use typeserializers
   * @return If {@link #serialize(Flag)} should be used for configurate
   */
  public boolean shouldUseSerializeForConfigurate() {
    return false;
  }

  /**
   * Serializes value into a human readable format for display, and
   * possibly also configurate depending on {@link #shouldUseSerializeForConfigurate()}
   * <b>IMPORTANT</b>: always use the default setting value to access this function.
   * This is because the value will not have the function overriden since it will be just
   * a flag with the correct generic.
   * @return Value serialized as a string
   */
  public String serialize(Flag<T> flag) {
    return flag.value.toString();
  }

  /**
   * Deserializes value. Only to be ever used if {@link #shouldUseSerializeForConfigurate()}
   * returns true.
   * <b>IMPORTANT</b>: always use the default setting value to access this function.
   * @throws UnsupportedOperationException if deserialize is not overriden. Check {@link #shouldUseSerializeForConfigurate()} first.
   * @return T value to be used to construct a new flag
   */
  public T deserialize(String s) {
    throw new UnsupportedOperationException("This flag should not be deserialized this way!");
  }
}
