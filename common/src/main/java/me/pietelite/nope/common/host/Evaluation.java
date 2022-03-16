/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
 * Copyright (c) MinecraftOnline
 * Copyright (c) contributors
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

package me.pietelite.nope.common.host;

import java.util.LinkedList;
import me.pietelite.nope.common.setting.SettingKey;

public class Evaluation<T> extends LinkedList<Evaluation<T>.EvaluationStage> {

  private final SettingKey<T, ?, ?> settingKey;

  public Evaluation(SettingKey<T, ?, ?> settingKey) {
    this.settingKey = settingKey;
  }

  public T result() {
    if (isEmpty()) {
      return settingKey.defaultData();
    }
    return getLast().value;
  }

  public EvaluationStage add(Host host, T value) {
    EvaluationStage stage = new EvaluationStage(host, value);
    this.add(stage);
    return stage;
  }

  public class EvaluationStage {

    private final Host host;
    private final T value;

    public EvaluationStage(Host host, T value) {
      this.host = host;
      this.value = value;
    }

    public Host host() {
      return host;
    }

    public T value() {
      return value;
    }
  }

}
