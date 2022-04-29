/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
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

package me.pietelite.nope.common.api.edit;

import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public class AlterationImpl implements Alteration {

  private final Result result;
  private final String message;

  public static AlterationImpl of(Result result) {
    return new AlterationImpl(result, null);
  }

  public static AlterationImpl of(Result result, String message) {
    return new AlterationImpl(result, message);
  }

  public static AlterationImpl success() {
    return new AlterationImpl(Result.SUCCESS, null);
  }

  public static AlterationImpl success(String message) {
    return new AlterationImpl(Result.SUCCESS, message);
  }

  public static AlterationImpl warn(String message) {
    return new AlterationImpl(Result.WARNING, message);
  }

  public static AlterationImpl fail(String message) {
    return new AlterationImpl(Result.FAILURE, message);
  }

  public static AlterationImpl nameDoesntExist(String name) {
    return new AlterationImpl(Result.FAILURE, "The name " + name + " doesn't exist");
  }

  private AlterationImpl(Result result, String message) {
    this.result = result;
    this.message = message;
  }

  @Override
  public Result result() {
    return result;
  }

  @Nullable
  @Override
  public Optional<String> message() {
    return Optional.ofNullable(message);
  }
}
