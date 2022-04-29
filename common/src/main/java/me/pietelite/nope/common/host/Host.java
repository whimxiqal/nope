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

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.api.edit.Alteration;
import me.pietelite.nope.common.api.edit.AlterationImpl;
import me.pietelite.nope.common.api.edit.HostEditor;
import me.pietelite.nope.common.api.edit.TargetEditor;
import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.common.setting.Target;
import me.pietelite.nope.common.storage.Destructible;
import me.pietelite.nope.common.storage.Persistent;
import me.pietelite.nope.common.struct.Container;
import me.pietelite.nope.common.struct.Location;
import me.pietelite.nope.common.struct.Named;
import me.pietelite.nope.common.util.Validate;

/**
 * A class to store Settings based on graphical locations.
 */
public abstract class Host implements Container, Named, Persistent, Destructible {

  private final ArrayList<HostedProfile> profiles = new ArrayList<>();
  protected String name;
  protected int priority;

  /**
   * Default constructor.
   *
   * @param name     the name
   * @param priority the priority
   */
  public Host(String name, int priority) {
    if (Validate.invalidSettingCollectionName(name)) {
      throw new IllegalArgumentException("Invalid host name: " + name);
    }
    this.name = name;
    this.priority = priority;
  }

  /**
   * Check if a Location exists within this host.
   *
   * @param location the location
   * @return true if within the host
   */
  public abstract boolean contains(Location location);

  @Override
  public final int hashCode() {
    return name().hashCode();
  }

  public final int priority() {
    return priority;
  }

  public final ArrayList<HostedProfile> hostedProfiles() {
    return profiles;
  }

  public ArrayList<HostedProfile> allProfiles() {
    return hostedProfiles();
  }

  public boolean isSet(SettingKey<?, ?, ?> key) {
    return allProfiles().stream().anyMatch(profileItem -> profileItem.profile().isSet(key));
  }

  @Override
  public void markDestroyed() {
    // do nothing
  }

  @Override
  public boolean destroyed() {
    return false;
  }

  @Override
  public void verifyExistence() throws NoSuchElementException {
    if (destroyed()) {
      throw new IllegalStateException("Host is destroyed: " + name);
    }
  }

  @Override
  public String name() {
    return name;
  }

  /**
   * Public editor for a {@link Host}.
   */
  public abstract static class Editor<H extends Host> implements HostEditor {

    protected final H host;

    public Editor(H host) {
      this.host = host;
    }

    @Override
    public String name() {
      host.verifyExistence();
      return host.name;
    }

    @Override
    public List<String> profiles() {
      host.verifyExistence();
      return host.hostedProfiles()
          .stream()
          .map(hostedProfile -> hostedProfile.profile().name())
          .collect(Collectors.toList());
    }

    @Override
    public Alteration addProfile(String name, int index) {
      Profile profile = Nope.instance().system().profiles().get(name);
      if (profile == null) {
        throw new NoSuchElementException("No profile exists with name " + name);
      }
      host.verifyExistence();
      if (index < 0 || index > host.hostedProfiles().size()) {
        throw new IndexOutOfBoundsException("There is no profile at index " + index);
      }
      if (host.hostedProfiles().stream().anyMatch(hostedProfile -> hostedProfile.profile().equals(profile))) {
        throw new IllegalArgumentException("There is already a profile added with the name " + name);
      }
      host.hostedProfiles().add(index, new HostedProfile(profile));
      host.save();
      return AlterationImpl.success("Added profile " + profile.name() + " to host " + name);
    }

    @Override
    public Alteration removeProfile(String name) {
      host.verifyExistence();
      if (!host.hostedProfiles().removeIf(hostedProfile -> hostedProfile.profile().name().equalsIgnoreCase(name))) {
        return AlterationImpl.fail("No profile exists named " + name);
      }
      host.save();
      return AlterationImpl.success("Removed profile with name " + name + " from host " + name);
    }

    @Override
    public Alteration removeProfile(int index) {
      host.verifyExistence();
      if (index < 0 || index > host.hostedProfiles().size()) {
        throw new IndexOutOfBoundsException("There is no profile at index " + index);
      }
      HostedProfile hostedProfile = host.hostedProfiles().remove(index);
      host.save();
      return AlterationImpl.success("Removed profile " + hostedProfile.profile().name()
          + " from host " + host.name);
    }

    @Override
    public boolean hasTarget(int index) throws IndexOutOfBoundsException {
      host.verifyExistence();
      return host.hostedProfiles().get(index).target() != null;
    }

    @Override
    public TargetEditor editTarget(String name) throws NoSuchElementException {
      host.verifyExistence();
      HostedProfile hostedProfile = null;
      for (int i = 0; i < host.hostedProfiles().size(); i++) {
        HostedProfile p = host.hostedProfiles().get(i);
        if (p.profile().name().equalsIgnoreCase(name)) {
          hostedProfile = p;
          break;
        }
      }
      if (hostedProfile == null) {
        throw new NoSuchElementException("There is no profile with name " + name);
      }
      return new Target.Editor(hostedProfile, host::save);
    }

    @Override
    public TargetEditor editTarget(int index) throws IndexOutOfBoundsException {
      host.verifyExistence();
      if (index < 0 || index > host.hostedProfiles().size()) {
        throw new IndexOutOfBoundsException("There is no profile at index " + index);
      }
      HostedProfile hostedProfile = host.hostedProfiles().get(index);
      return new Target.Editor(hostedProfile, host::save);
    }

  }
}
