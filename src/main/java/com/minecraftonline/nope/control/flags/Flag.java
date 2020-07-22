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
 */

package com.minecraftonline.nope.control.flags;

import com.minecraftonline.nope.config.GlobalConfigManager;
import javafx.util.Pair;
import jdk.internal.jline.internal.Nullable;

import java.io.Serializable;
import java.util.Optional;

/**
 * Represents a WG Flag
 * @param <T> Value of the flag, that must, if not default-ly serializable
 *           by configurate, have a serializer added to {@link GlobalConfigManager#getTypeSerializers()}
 */
public abstract class Flag<T> implements Serializable {
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

  /**
   * Deserialize value from ingame input. This must be implemented
   * but is free to just call {@link #deserialize(String s)}, provided
   * that is also implemented.
   * @param s String to deserialize
   * @return T value to be used to construct a new flag, or null if input is invalid
   */
  @Nullable
  public abstract T deserializeIngame(String s);
}
