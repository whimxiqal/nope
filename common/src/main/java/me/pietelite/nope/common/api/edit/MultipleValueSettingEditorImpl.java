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

import java.util.NoSuchElementException;
import java.util.Optional;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.api.struct.AltSet;
import me.pietelite.nope.common.host.Profile;
import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.common.setting.SettingValue;

public class MultipleValueSettingEditorImpl<T> extends SettingEditorImpl implements MultipleValueSettingEditor<T> {

  private final Class<T> type;

  public MultipleValueSettingEditorImpl(Profile profile, String setting, Class<T> type) {
    super(profile, setting);
    this.type = type;
  }

  private SettingKey.Poly<T, ?> key() {
    profile.verifyExistence();
    return Nope.instance().settingKeys().getPolySetting(setting, type);
  }

  @Override
  public void setAll() {
    setAllGeneric(key());
    profile.save();
  }

  private <X, Y extends AltSet<X>> void setAllGeneric(SettingKey.Poly<X, Y> key) {
    profile.setValue(key, SettingValue.Poly.declarative(key.manager().fullSet()));
  }

  @Override
  public void setNone() {
    setNoneGeneric(key());
    profile.save();
  }

  private <X, Y extends AltSet<X>> void setNoneGeneric(SettingKey.Poly<X, Y> key) {
    profile.setValue(key, SettingValue.Poly.declarative(key.manager().emptySet()));
  }

  @Override
  public boolean isDeclarative() {
    return profile.getValue(key()).map(SettingValue.Poly::declarative).orElse(false);
  }

  private <X, Y extends AltSet<X>> void setDeclarativeGeneric(SettingKey.Poly<X, Y> key, AltSet<X> values) {
    Y newSet = key.manager().emptySet();
    newSet.addAll(values);
    profile.setValue(key, SettingValue.Poly.declarative(newSet));
  }

  @Override
  public AltSet<T> getDeclarative() {
    SettingKey.Poly<T, ?> key = key();
    AltSet<T> out = key.manager().emptySet();
    SettingValue.Poly<T, ?> value = profile.getValue(key).orElseThrow(NoSuchElementException::new);
    if (!value.declarative()) {
      throw new NoSuchElementException();
    }
    out.addAll(value.additive());
    return out;
  }

  @Override
  public void setDeclarative(AltSet<T> values) {
    setDeclarativeGeneric(key(), values);
    profile.save();
  }

  @Override
  public boolean isManipulative() {
    return profile.getValue(key()).map(SettingValue.Poly::manipulative).orElse(false);
  }

  @Override
  public void setManipulative(AltSet<T> set, ManipulativeType manipulativeType) {
    setManipulativeGeneric(key(), set, manipulativeType);
    profile.save();
  }

  private <X, Y extends AltSet<X>> void setManipulativeGeneric(SettingKey.Poly<X, Y> key,
                                                               AltSet<X> set,
                                                               ManipulativeType manipulativeType) {
    Optional<SettingValue.Poly<X, Y>> value = profile.getValue(key);
    Y newAdditive;
    Y newSubtractive;
    switch (manipulativeType) {
      case ADDITIVE:
        newAdditive = key.manager().emptySet();
        newAdditive.addAll(set);
        if (value.isPresent() && value.get().manipulative()) {
          newSubtractive = key.manager().copySet(value.get().subtractive());
        } else {
          newSubtractive = key.manager().emptySet();
        }
        break;
      case SUBTRACTIVE:
        newSubtractive = key.manager().emptySet();
        newSubtractive.addAll(set);
        if (value.isPresent() && value.get().manipulative()) {
          newAdditive = key.manager().copySet(value.get().additive());
        } else {
          newAdditive = key.manager().emptySet();
        }

        break;
      default:
        throw new RuntimeException();
    }
    if (newSubtractive.removeAll(newAdditive)) {
      // The two sets are overlapping! We can't have that
      throw new IllegalArgumentException();
    }
    profile.setValue(key, SettingValue.Poly.manipulative(newAdditive, newSubtractive));
  }

  @Override
  public AltSet<T> getManipulative(ManipulativeType manipulativeType) {
    SettingKey.Poly<T, ?> key = key();
    AltSet<T> out = key.manager().emptySet();
    SettingValue.Poly<T, ?> value = profile.getValue(key).orElseThrow(NoSuchElementException::new);
    if (!value.manipulative()) {
      throw new NoSuchElementException();
    }
    switch (manipulativeType) {
      case ADDITIVE:
        out.addAll(value.additive());
        break;
      case SUBTRACTIVE:
        out.addAll(value.subtractive());
        break;
      default:
        throw new RuntimeException();
    }
    return out;
  }

}
