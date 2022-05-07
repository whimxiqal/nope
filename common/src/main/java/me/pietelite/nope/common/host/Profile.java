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

import java.util.NoSuchElementException;
import java.util.Set;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.api.edit.MultipleValueSettingEditor;
import me.pietelite.nope.common.api.edit.MultipleValueSettingEditorImpl;
import me.pietelite.nope.common.api.edit.ProfileEditor;
import me.pietelite.nope.common.api.edit.SettingEditor;
import me.pietelite.nope.common.api.edit.SettingEditorImpl;
import me.pietelite.nope.common.api.edit.SingleValueSettingEditor;
import me.pietelite.nope.common.api.edit.SingleValueSettingEditorImpl;
import me.pietelite.nope.common.api.edit.TargetEditor;
import me.pietelite.nope.common.api.struct.Named;
import me.pietelite.nope.common.setting.SettingCollection;
import me.pietelite.nope.common.setting.Target;
import me.pietelite.nope.common.setting.Targetable;
import me.pietelite.nope.common.storage.Expirable;
import me.pietelite.nope.common.storage.Persistent;
import me.pietelite.nope.common.struct.Scoped;
import org.jetbrains.annotations.Nullable;

public class Profile extends SettingCollection implements Named, Persistent, Expirable, Targetable, Scoped {

  private final String scope;
  private String name;
  private Target target;
  private boolean destroyed;

  public Profile(String scope, String name) {
    this(scope, name, null);
  }

  public Profile(String scope, String name, Target target) {
    this.scope = scope;
    this.name = name;
    this.target = target;
  }

  @Override
  public String scope() {
    return this.scope;
  }

  @Override
  public Target target() {
    return this.target;
  }

  @Override
  public void target(@Nullable Target target) {
    this.target = target;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public void expire() {
    this.destroyed = true;
  }

  @Override
  public boolean expired() {
    return destroyed;
  }

  @Override
  public void verifyExistence() throws NoSuchElementException {
    if (expired()) {
      throw new IllegalStateException("Profile is destroyed: " + name);
    }
  }

  @Override
  public void save() {
    Nope.instance().data().profiles(scope).save(this);
  }

  public static class Editor implements ProfileEditor {

    private final Profile profile;

    public Editor(Profile profile) {
      this.profile = profile;
    }

    private Scope scope() {
      return Nope.instance().system().scope(profile.scope);
    }

    @Override
    public String name() {
      profile.verifyExistence();
      return profile.name();
    }

    @Override
    public boolean name(String name) {
      profile.verifyExistence();
      if (profile.name().equals(name)) {
        // No change
        return false;
      }
      // We should allow case-change of characters if it's the same name otherwise as the current host
      if (!profile.name().equalsIgnoreCase(name) && Nope.instance().system().hasName(profile.scope, name)) {
        throw new IllegalArgumentException("A host with the name \"" + name + "\" already exists");
      }
      if (name.startsWith("_")) {
        throw new IllegalArgumentException("Scene names cannot start with an underscore");
      }
      // Remove all references of old name
      scope().profiles().remove(profile.name());
      Nope.instance().data().profiles(profile.scope()).destroy(profile);

      // Switch backwards-related references of profiles to hosts
      Set<Host> relatedHosts = scope().relatedToProfile(profile.name());
      scope().unrelateProfile(profile.name());
      relatedHosts.forEach(host -> scope().relateProfile(profile.name(), host));

      // Change name and add references back in
      profile.name = name;
      scope().profiles().put(profile.name.toLowerCase(), profile);
      profile.save();
      return true;
    }

    @Override
    public TargetEditor editTarget() {
      profile.verifyExistence();
      return new Target.Editor(profile, profile::save);
    }

    @Override
    public SettingEditor editSetting(String setting) {
      profile.verifyExistence();
      if (!Nope.instance().settingKeys().containsId(setting)) {
        throw new NoSuchElementException("There is no setting with the name " + setting);
      }
      return new SettingEditorImpl(profile, setting);
    }

    @Override
    public <T> SingleValueSettingEditor<T> editSingleValueSetting(String setting, Class<T> type) {
      profile.verifyExistence();
      if (!Nope.instance().settingKeys().containsId(setting)) {
        throw new NoSuchElementException();
      }
      return new SingleValueSettingEditorImpl<>(profile, setting, type);
    }

    @Override
    public <T> MultipleValueSettingEditor<T> editMultipleValueSetting(String setting, Class<T> type) {
      profile.verifyExistence();
      if (!Nope.instance().settingKeys().containsId(setting)) {
        throw new NoSuchElementException();
      }
      return new MultipleValueSettingEditorImpl<>(profile, setting, type);
    }

    @Override
    public void destroy() {
      profile.verifyExistence();
      if (scope().profiles().remove(profile.name) == null) {
        throw new NoSuchElementException("There is not host with name " + profile.name());
      }
      for (Host host : scope().relatedToProfile(profile.name())) {
        host.hostedProfiles().removeIf(hostedProfile -> hostedProfile.profile().equals(profile));
        host.save();
      }
      scope().unrelateProfile(profile.name());
      Nope.instance().data().profiles(profile.scope).destroy(profile);
      profile.expire();
    }

  }
}
