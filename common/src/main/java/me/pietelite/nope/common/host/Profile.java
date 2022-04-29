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

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.api.edit.Alteration;
import me.pietelite.nope.common.api.edit.AlterationImpl;
import me.pietelite.nope.common.api.edit.MultipleValueSettingEditor;
import me.pietelite.nope.common.api.edit.MultipleValueSettingEditorImpl;
import me.pietelite.nope.common.api.edit.ProfileEditor;
import me.pietelite.nope.common.api.edit.SingleValueSettingEditor;
import me.pietelite.nope.common.api.edit.SingleValueSettingEditorImpl;
import me.pietelite.nope.common.api.edit.TargetEditor;
import me.pietelite.nope.common.setting.SettingCollection;
import me.pietelite.nope.common.setting.Target;
import me.pietelite.nope.common.setting.Targetable;
import me.pietelite.nope.common.storage.Destructible;
import me.pietelite.nope.common.storage.Persistent;
import me.pietelite.nope.common.struct.Named;
import org.jetbrains.annotations.Nullable;

public class Profile extends SettingCollection implements Named, Persistent, Destructible, Targetable {
  private final Set<Host> hosts = new HashSet<>();
  private String name;
  private Target target;
  private boolean destroyed;

  public Profile(String name) {
    this(name, null);
  }

  public Profile(String name, Target target) {
    this.name = name;
    this.target = target;
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
  public void markDestroyed() {
    this.destroyed = true;
  }

  @Override
  public boolean destroyed() {
    return destroyed;
  }

  @Override
  public void verifyExistence() throws NoSuchElementException {
    if (destroyed()) {
      throw new IllegalStateException("Profile is destroyed: " + name);
    }
  }

  @Override
  public void save() {
    Nope.instance().data().profiles().save(this);
  }

  public static class Editor implements ProfileEditor {

    private final Profile profile;

    public Editor(Profile profile) {
      this.profile = profile;
    }

    @Override
    public String name() {
      profile.verifyExistence();
      return profile.name();
    }

    @Override
    public Alteration name(String name) {
      profile.verifyExistence();

      // Remove all references of old name
      Nope.instance().system().profiles().remove(profile.name());
      Nope.instance().data().profiles().destroy(profile);

      // Change name and add references back in
      profile.name = name;
      Nope.instance().system().profiles().put(profile.name.toLowerCase(), profile);
      profile.save();
      return AlterationImpl.success("Renamed profile \"" + profile.name() + "\" to \"" + name + "\"");
    }

    @Override
    public TargetEditor editTarget() {
      profile.verifyExistence();
      return new Target.Editor(profile, profile::save);
    }

    @Override
    public <T> SingleValueSettingEditor<T> editSingleValueSetting(String setting, Class<T> type) {
      profile.verifyExistence();
      return new SingleValueSettingEditorImpl<>(profile, setting, type);
    }

    @Override
    public <T> MultipleValueSettingEditor<T> editMultipleValueSetting(String setting, Class<T> type) {
      profile.verifyExistence();
      return new MultipleValueSettingEditorImpl<>(profile, setting, type);
    }

    @Override
    public Alteration destroy() {
      profile.verifyExistence();
      if (Nope.instance().system().profiles().remove(profile.name) == null) {
        throw new NoSuchElementException("There is not host with name " + profile.name());
      }
      profile.hosts.forEach(host ->
          host.hostedProfiles().removeIf(hostedProfile ->
              hostedProfile.profile().name().equalsIgnoreCase(profile.name())));
      Nope.instance().data().profiles().destroy(profile);
      profile.hosts.clear();
      profile.markDestroyed();
      return AlterationImpl.success("Removed scene " + profile.name);
    }

  }
}
