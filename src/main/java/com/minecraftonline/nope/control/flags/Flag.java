package com.minecraftonline.nope.control.flags;

import com.minecraftonline.nope.config.GlobalConfigManager;
import javafx.util.Pair;

import java.io.Serializable;

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
}
