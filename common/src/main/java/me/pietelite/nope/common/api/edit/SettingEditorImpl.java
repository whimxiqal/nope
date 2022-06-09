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

import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.host.Profile;
import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.common.setting.Target;
import me.pietelite.nope.common.setting.Targetable;
import org.jetbrains.annotations.Nullable;

/**
 * Implementation of the {@link SettingEditor}.
 */
public class SettingEditorImpl implements SettingEditor {

  protected final Profile profile;
  protected final String setting;

  public SettingEditorImpl(Profile profile, String setting) {
    this.profile = profile;
    this.setting = setting;
  }


  @Override
  public boolean hasValue() {
    profile.verifyExistence();
    SettingKey<?, ?, ?> key = Nope.instance().settingKeys().get(setting);
    return profile.getValue(key).isPresent();
  }

  @Override
  public boolean unsetValue() {
    profile.verifyExistence();
    SettingKey<?, ?, ?> key = Nope.instance().settingKeys().get(setting);
    return profile.removeValue(key) != null;
  }

  @Override
  public TargetEditor editTarget() {
    profile.verifyExistence();
    SettingKey<?, ?, ?> key = Nope.instance().settingKeys().get(setting);
    return new Target.Editor(new Targetable() {
      @Override
      public @Nullable Target target() {
        return profile.getTarget(key).orElse(null);
      }

      @Override
      public void target(@Nullable Target target) {
        if (target == null) {
          profile.removeTarget(key);
        } else {
          profile.setTarget(key, target);
        }
      }
    }, profile::save);
  }

  @Override
  public boolean hasTarget() {
    profile.verifyExistence();
    SettingKey<?, ?, ?> key = Nope.instance().settingKeys().get(setting);
    return profile.getTarget(key).isPresent();
  }
}
