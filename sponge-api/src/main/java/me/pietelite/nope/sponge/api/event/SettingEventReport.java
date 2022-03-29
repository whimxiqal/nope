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

package me.pietelite.nope.sponge.api.event;

import org.spongepowered.api.util.Nameable;

/**
 * A user-friendly report format to inform administrators when events
 * related to settings occur.
 */
public class SettingEventReport {

  private final Action action;
  private final String source;
  private final String target;

  private SettingEventReport(Action action, String source, String target) {
    this.action = action;
    this.source = source;
    this.target = target;
  }

  public static Builder restricted() {
    return new Builder(Action.RESTRICTED);
  }

  public static Builder prevented() {
    return new Builder(Action.PREVENTED);
  }

  public Action action() {
    return action;
  }

  public String source() {
    return source;
  }

  public String target() {
    return target;
  }

  /**
   * A type of action associated with what caused this report to be generated.
   */
  public enum Action {
    RESTRICTED,
    PREVENTED
  }

  /**
   * A builder for a report.
   */
  public static class Builder {
    private final Action action;
    private String source;
    private String target;

    private Builder(Action action) {
      this.action = action;
    }

    /**
     * Set the source of the event generating this report.
     *
     * @param source the source (root cause)
     * @return the builder, for chaining
     */
    public Builder source(Object source) {
      if (source instanceof String) {
        this.source = (String) source;
      } else if (source instanceof Nameable) {
        this.source = ((Nameable) source).name();
      } else {
        this.source = source.getClass().getSimpleName();
      }
      return this;
    }

    /**
     * Set the target of the event, which is usually the thing the source of the event
     * was interacting with in the event.
     *
     * <p>For example, if a player broke a block, the block would be the target.
     *
     * @param target the target of the event
     * @return this builder, for chaining
     */
    public Builder target(String target) {
      this.target = target;
      return this;
    }

    public SettingEventReport build() {
      return new SettingEventReport(action, source, target);
    }

  }

}
