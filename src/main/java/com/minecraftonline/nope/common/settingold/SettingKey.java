///*
// * MIT License
// *
// * Copyright (c) 2020 MinecraftOnline
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in all
// * copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// * SOFTWARE.
// *
// */
//
//package com.minecraftonline.nope.common.settingold;
//
//import com.google.gson.Gson;
//import com.minecraftonline.nope.common.setting.value.SettingValue;
//import java.util.Collections;
//import java.util.List;
//import java.util.Objects;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.Setter;
//import lombok.experimental.Accessors;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//public abstract class SettingKey<T> {
//  @Getter
//  @Accessors(fluent = true)
//  @NotNull
//  final String id;
//  @Getter
//  @Accessors(fluent = true)
//  @NotNull
//  final Class<T> type;
//  @Getter
//  @Setter
//  @Accessors(fluent = true)
//  @NotNull
//  String description = null;
//  @Getter
//  @Setter
//  @Accessors(fluent = true)
//  @NotNull
//  String blurb = null;
//  @Getter
//  @Setter
//  @Accessors(fluent = true)
//  @NotNull
//  private CategoryType category = CategoryType.MISC;
//  @Getter
//  @Setter
//  private boolean implemented = true;
//  @Getter
//  @Setter
//  private boolean unnaturalDefault = false;
//  @Getter
//  @Setter
//  private boolean global = false;
//  @Getter
//  @Setter
//  private boolean playerRestrictive = false;
//
//  protected SettingKey(String id, @NotNull T defaultData) {
//    this.id = id;
//    this.defaultData = Objects.requireNonNull(defaultData);
//    this.type = (Class<T>) defaultData.getClass();
//  }
//
//  protected SettingKey(String id, @Nullable T defaultData, @NotNull Class<T> type) {
//    this.id = id;
//    this.defaultData = defaultData;
//    this.type = Objects.requireNonNull(type);
//  }
//
//  public final Object serializeData(Object data) {
//    return serializeDataGenerified(cast(data));
//  }
//
//  protected Object serializeDataGenerified(T data) {
//    return data;
//  }
//
//  public final Object deserializeData(Object serialized) throws ParseSettingException {
//    return deserializeDataGenerified(serialized);
//  }
//
//  public T deserializeDataGenerified(Object serialized) throws ParseSettingException {
//    return cast(serialized);
//  }
//
//  /**
//   * Create a readable String version of the data.
//   *
//   * @param data the data to print
//   * @return data in a readable form
//   */
//  @NotNull
//  public String print(@NotNull T data) {
//    return serializeDataGenerified(data).toString();
//  }
//
//  /**
//   * Parse some data in some custom format.
//   * Used for dealing with data from in-game usages of
//   * declaring data.
//   *
//   * @param data string representation of data
//   * @return the data object
//   * @throws ParseSettingException if data cannot be parsed
//   */
//  public abstract T parse(String data) throws ParseSettingException;
//
//  /**
//   * Get a list of all parsable strings for data stored
//   * under this SettingKey.
//   *
//   * @return a list of parsable strings only if there are finite possibilities
//   */
//  public List<String> options() {
//    return Collections.emptyList();
//  }
//
//  public final <X> boolean isType(Class<X> type) {
//    return type.isAssignableFrom(type());
//  }
//
//  /**
//   * Cast the object to this object's generic type.
//   *
//   * @param object the object to convert
//   * @return the cast value
//   */
//  public final T cast(Object object) {
//    if (!type().isInstance(object)) {
//      throw new IllegalArgumentException(String.format(
//          "input %s must be of type %s",
//          object.getClass().getName(),
//          type().getName()));
//    }
//    return type().cast(object);
//  }
//
//  public Setting<T> getDefaultSetting() {
//    return Setting.of(this, this.defaultData);
//  }
//
//  @Override
//  public int hashCode() {
//    return this.id.hashCode();
//  }
//
//  @Override
//  public boolean equals(Object other) {
//    return (other instanceof SettingKey) && ((SettingKey<?>) other).id.equals(this.id);
//  }
//
//  @Override
//  public String toString() {
//    return this.id;
//  }
//
//  /**
//   * Type of {@link SettingKey} for ordering purposes.
//   */
//  public enum CategoryType {
//    BLOCKS,
//    DAMAGE,
//    ENTITIES,
//    MISC,
//    MOVEMENT,
//    GLOBAL,
//  }
//
//  /**
//   * An exception to throw when a setting could not be parsed.
//   */
//  public static class ParseSettingException extends IllegalArgumentException {
//    public ParseSettingException() {
//      super();
//    }
//
//    public ParseSettingException(String s) {
//      super(s);
//    }
//  }
//}
