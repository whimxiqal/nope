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

package me.pietelite.nope.common.host;

import java.util.LinkedList;
import me.pietelite.nope.common.setting.SettingKey;

/**
 * The result of an in-game analysis of the value stored on a {@link SettingKey}
 * at a given location and a given subject.
 * The purpose of the evaluation is to keep track of how the value mutated as it
 * passed through various {@link Host}s.
 *
 * @param <T> the returned data type
 */
public class Evaluation<T> extends LinkedList<Evaluation<T>.EvaluationStage> {

  private final SettingKey<T, ?, ?> settingKey;

  public Evaluation(SettingKey<T, ?, ?> settingKey) {
    this.settingKey = settingKey;
  }

  /**
   * Get the final response to the request for a value.
   *
   * @return the result
   */
  public T result() {
    if (isEmpty()) {
      return settingKey.defaultData();
    }
    return getLast().value;
  }

  /**
   * Add a stage to the end of the stage list, insinuating
   * that the profile cause the overall value to mutate to the given value
   * after the evaluation passed through the profile.
   * This method is for bookkeeping and does not affect the actual result
   * when it is queried.
   *
   * @param host the host
   * @param profile  the profile
   * @param value the value after the profile was applied
   * @return the stage that was added
   */
  public EvaluationStage add(Host host, Profile profile, T value) {
    EvaluationStage stage = new EvaluationStage(host, profile, value);
    this.add(stage);
    return stage;
  }

  /**
   * A single stage in the entire process of evaluating the requested value.
   */
  public class EvaluationStage {

    private final Host host;
    private final Profile profile;
    private final T value;

    /**
     * Generic constructor.
     *
     * @param host the host
     * @param profile  the profile
     * @param value the value
     */
    public EvaluationStage(Host host, Profile profile, T value) {
      this.host = host;
      this.profile = profile;
      this.value = value;
    }

    public Host host() {
      return host;
    }

    public Profile profile() {
      return profile;
    }

    public T value() {
      return value;
    }
  }

}
