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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * The key object associated inside a {@link Setting}.
 * One or more {@link SettingValue} is normally associated with
 * a single {@link SettingKey} instantiated in multiple {@link Setting}s.
 * <p>
 * This object is abstract because every different type of data stored
 * in the {@link SettingValue} may have unique serializing logic, so
 * every different generic type will require its own implementation.
 *
 * @param <T> the type of data which is ultimately keyed under this key
 */
public abstract class SettingKey<T> {

  /**
   * The global unique identifier for this key.
   */
  @Getter
  final String id;

  /**
   * The default data of a setting keyed with this object.
   * This is what determines the behavior associated with this key
   * if the user does not specify something different.
   */
  @Getter
  final T defaultData;

  /**
   * Type of {@link SettingKey} for ordering purposes.
   */
  public enum CategoryType {
    BLOCKS,
    MOVEMENT,
    DAMAGE,
    MISC,
  }

  protected SettingKey(String id, T defaultData) {
    this.id = id;
    this.defaultData = defaultData;
  }

  String comment = null;
  String description = null;
  @Nonnull
  CategoryType category = CategoryType.MISC;
  boolean implemented = true;

  /**
   * Convert some data into a Json structure.
   * Data must be of the type of this object's generic type.
   * Uses {@link #encodeGenerifiedData(Object)}.
   *
   * @param data the encoding data
   * @return Json structure representing data
   */
  public final JsonElement encodeData(Object data) {
    return encodeGenerifiedData(cast(data));
  }

  /**
   * Convert some data into a Json structure.
   *
   * @param data the encoding data
   * @return json structure representing data
   */
  protected JsonElement encodeGenerifiedData(T data) {
    return new Gson().toJsonTree(data);
  }

  /**
   * Parse some data from a Json structure.
   * Data will be of the type of this object's generic type.
   * Uses {@link #parseGenerifiedData(JsonElement)}.
   *
   * @param json Json structure
   * @return the data represented by the Json structure
   */
  public final Object parseData(JsonElement json) {
    return parseGenerifiedData(json);
  }

  /**
   * Parse some data from a Json structure.
   *
   * @param json Json structure
   * @return the data represented by the Json structure
   */
  public T parseGenerifiedData(JsonElement json) {
    return cast(new Gson().fromJson(json, defaultData.getClass()));
  }

  /**
   * Get the class type of this object's generic type.
   *
   * @return the generic class
   */
  public final Class<T> valueType() {
    // TODO verify that this cast works. If it does, then just add @SuppressWarnings("unchecked").
    return (Class<T>) defaultData.getClass();
  }

  /**
   * Cast the object to this object's generic type.
   *
   * @param object the object to convert
   * @return the cast value
   */
  public final T cast(Object object) {
    if (!valueType().isInstance(object)) {
      throw new IllegalArgumentException(String.format(
              "input %s must be of type %s",
              object.getClass().getName(),
              valueType().getName()));
    }
    return valueType().cast(object);
  }

  @Nonnull
  public final Optional<String> getComment() {
    return Optional.ofNullable(comment);
  }

  @Nonnull
  public final Optional<String> getDescription() {
    return Optional.ofNullable(description);
  }

  @Nonnull
  public final CategoryType getCategory() {
    return this.category;
  }

  public final boolean isImplemented() {
    return implemented;
  }

  @Override
  public int hashCode() {
    return this.id.hashCode();
  }

  @Override
  public boolean equals(Object other) {
    return (other instanceof SettingKey) && ((SettingKey<?>) other).id.equals(this.id);
  }

  @Override
  public String toString() {
    return this.id;
  }
}
