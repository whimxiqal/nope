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
import java.util.List;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.api.edit.TargetEditor;
import me.pietelite.nope.common.struct.Location;

/**
 * Class for managing the single "global" server-wide {@link Host} in the {@link HostSystem}.
 */
public class Global extends Host {

  Profile globalProfile;

  public Global(String name) {
    super(name, -2);
  }

  @Override
  public boolean contains(Location location) {
    return true;
  }

  public void save() {
    Nope.instance().data().universe().save(this);
  }

  public void globalProfile(Profile globalProfile) {
    this.globalProfile = globalProfile;
  }

  public Profile globalProfile() {
    return globalProfile;
  }

  @Override
  public ArrayList<HostedProfile> allProfiles() {
    ArrayList<HostedProfile> out = new ArrayList<>(hostedProfiles().size() + 1);
    out.add(new HostedProfile(globalProfile(), null));
    out.addAll(hostedProfiles());
    return out;
  }

  public static class Editor extends Host.Editor<Global> {

    public Editor() {
      super(Nope.instance().system().global());
    }

    @Override
    public List<String> profiles() {
      List<String> superProfiles = super.profiles();
      ArrayList<String> out = new ArrayList<>(superProfiles.size() + 1);
      out.add(host.globalProfile().name());
      out.addAll(superProfiles);
      return Collections.unmodifiableList(out);
    }

    @Override
    public void addProfile(String name, int index) {
      if (index == 0) {
        throw new IllegalArgumentException();
      }
      super.addProfile(name, index - 1);
    }

    @Override
    public void removeProfile(String name) {
      if (name.equalsIgnoreCase(Nope.GLOBAL_ID)) {
        throw new IllegalArgumentException();
      }
      super.removeProfile(name);
    }

    @Override
    public void removeProfile(int index) {
      if (index == 0) {
        throw new IllegalArgumentException();
      }
      super.removeProfile(index - 1);
    }

    @Override
    public boolean hasTarget(int index) throws IndexOutOfBoundsException {
      if (index == 0) {
        return false;
      }
      return super.hasTarget(index - 1);
    }

    @Override
    public TargetEditor editTarget(int index) throws IndexOutOfBoundsException {
      if (index == 0) {
        throw new IllegalArgumentException();
      }
      return super.editTarget(index - 1);
    }
  }

}
