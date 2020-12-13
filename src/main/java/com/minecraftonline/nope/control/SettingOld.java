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
//package com.minecraftonline.nope.control;
//
//import com.google.common.base.Preconditions;
//import org.spongepowered.api.service.context.Context;
//
//import java.io.Serializable;
//import java.util.Map;
//import javax.annotation.Nonnull;
//
//@Deprecated
//public class Setting<T extends Serializable> implements Map.Entry<Parameter<T>, T> {
//
//  private Parameter<T> parameter;
//  private T value;
//
//  /**
//   * Generic constructor.
//   *
//   * @param parameter The parameter holding information about this setting
//   * @param value     The value set for this instance of Setting
//   */
//  public Setting(@Nonnull Parameter<T> parameter,
//                 @Nonnull T value) {
//    Preconditions.checkNotNull(parameter);
//    Preconditions.checkNotNull(value);
//    this.parameter = parameter;
//    this.value = value;
//  }
//
//  @Override
//  public Parameter<T> getKey() {
//    return parameter;
//  }
//
//  @Override
//  public T getValue() {
//    return value;
//  }
//
//  @Override
//  public T setValue(T value) {
//    T old = this.value;
//    this.value = value;
//    return old;
//  }
//
//  public Class<T> getTypeClass() {
//    return parameter.getTypeClass();
//  }
//
//  public Context toContext() {
//    return new Context(parameter.getId(), value.toString());
//  }
//
//}
