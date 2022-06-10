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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.api.edit.HostEditor;
import me.pietelite.nope.common.api.edit.TargetEditor;
import me.pietelite.nope.common.api.struct.Named;
import me.pietelite.nope.common.setting.SettingKey;
import me.pietelite.nope.common.setting.Target;
import me.pietelite.nope.common.storage.Expirable;
import me.pietelite.nope.common.storage.Persistent;
import me.pietelite.nope.common.struct.Container;
import me.pietelite.nope.common.struct.Location;
import me.pietelite.nope.common.util.Validate;

/**
 * A class to store Settings based on graphical locations.
 */
public abstract class Host implements Container, Named, Persistent, Expirable {

  private final ArrayList<HostedProfile> profiles = new ArrayList<>();
  protected String name;
  protected int priority;
  private boolean expired = false;

  /**
   * Default constructor.
   *
   * @param name     the name
   * @param priority the priority
   */
  public Host(String name, int priority) {
    if (Validate.invalidId(name)) {
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

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Host)) {
      return false;
    }
    Host other = (Host) obj;
    return name.equals(other.name);
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
  public final void expire() {
    this.expired = true;
  }

  @Override
  public final boolean expired() {
    return expired;
  }

  @Override
  public final void verifyExistence() throws NoSuchElementException {
    if (expired()) {
      throw new IllegalStateException("Host has expired: " + name);
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

    protected H host;

    public Editor(H host) {
      this.host = host;
    }

    @Override
    public String name() {
      host.verifyExistence();
      return host.name;
    }

    @Override
    public Map<String, List<String>> profiles() {
      host.verifyExistence();
      Map<String, List<String>> out = new HashMap<>();
      for (HostedProfile p : host.allProfiles()) {
        out.computeIfAbsent(p.profile().scope(), k -> new LinkedList<>()).add(p.profile().name());
      }
      return Collections.unmodifiableMap(out);
    }

    @Override
    public void addProfile(String scope, String name, int index) {
      Profile profile = Nope.instance().system().scope(scope).profiles().get(name);
      if (profile == null) {
        throw new NoSuchElementException("No profile exists with name " + name);
      }
      host.verifyExistence();
      if (index < 0 || index > host.hostedProfiles().size()) {
        throw new IndexOutOfBoundsException("There is no profile at index " + index);
      }
      if (host.allProfiles().stream().anyMatch(hostedProfile -> hostedProfile.profile().equals(profile))) {
        throw new IllegalArgumentException("There is already a profile added with the name " + name);
      }
      host.hostedProfiles().add(index, new HostedProfile(profile));
      Nope.instance().system().scope(scope).relateProfile(profile.name(), host);
      host.save();
    }

    @Override
    public void removeProfile(String scope, String name) {
      host.verifyExistence();
      if (!host.hostedProfiles().removeIf(hostedProfile -> hostedProfile.profile().scope().equals(scope)
          && hostedProfile.profile().name().equalsIgnoreCase(name))) {
        throw new NoSuchElementException("No profile exists named " + name);
      }
      host.save();
    }

    @Override
    public void removeProfile(int index) {
      host.verifyExistence();
      if (index < 0 || index > host.hostedProfiles().size()) {
        throw new IndexOutOfBoundsException("There is no profile at index " + index);
      }
      host.hostedProfiles().remove(index);
      host.save();
    }

    @Override
    public boolean hasTarget(int index) throws IndexOutOfBoundsException {
      host.verifyExistence();
      return host.hostedProfiles().get(index).target() != null;
    }

    @Override
    public TargetEditor editTarget(String scope, String name) throws NoSuchElementException {
      host.verifyExistence();
      HostedProfile hostedProfile = null;
      for (int i = 0; i < host.hostedProfiles().size(); i++) {
        HostedProfile p = host.hostedProfiles().get(i);
        if (p.profile().scope().equals(scope) && p.profile().name().equalsIgnoreCase(name)) {
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

    @Override
    public int priority() {
      host.verifyExistence();
      return host.priority();
    }
  }
}
