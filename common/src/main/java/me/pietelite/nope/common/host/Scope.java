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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.api.edit.ProfileEditor;
import me.pietelite.nope.common.api.edit.SceneEditor;
import me.pietelite.nope.common.api.edit.ScopeEditor;
import me.pietelite.nope.common.api.struct.Named;
import me.pietelite.nope.common.struct.IgnoreCaseStringHashMap;
import org.jetbrains.annotations.NotNull;

public class Scope implements Named {

  private final String name;
  private final IgnoreCaseStringHashMap<Scene> scenes = new IgnoreCaseStringHashMap<>();
  private final IgnoreCaseStringHashMap<Profile> profiles = new IgnoreCaseStringHashMap<>();
  private final Map<String, Set<Host>> profileBackwardsMap = new HashMap<>();

  public Scope(String name) {
    this.name = name;
  }

  public String name() {
    return name;
  }

  public IgnoreCaseStringHashMap<Scene> scenes() {
    return scenes;
  }

  public IgnoreCaseStringHashMap<Profile> profiles() {
    return profiles;
  }

  public void relateProfile(String profileName, Host scene) {
    profileBackwardsMap.computeIfAbsent(profileName, k -> new HashSet<>()).add(scene);
  }

  public void unrelateProfile(String profileName) {
    profileBackwardsMap.remove(profileName);
  }

  @NotNull
  public Set<Host> relatedToProfile(String profileName) {
    Set<Host> out = profileBackwardsMap.get(profileName);
    if (out == null) {
      return Collections.emptySet();
    }
    return out;
  }

  public static class Editor implements ScopeEditor {
    private final Scope scope;

    public Editor(Scope scope) {
      this.scope = scope;
    }

    @Override
    public Set<String> scenes() {
      return scope.scenes().realKeys();
    }

    @Override
    public SceneEditor editScene(String name) throws NoSuchElementException {
      Scene scene = scope.scenes().get(name);
      if (scene == null) {
        throw new NoSuchElementException();
      }
      return new Scene.Editor(scene);
    }

    @Override
    public SceneEditor createScene(String name, int priority) {
      if (Nope.instance().system().hasName(scope.name, name)) {
        throw new IllegalArgumentException("A host already exists with the name " + name);
      }
      Scene scene = new Scene(scope.name, name, priority);
      scope.scenes().put(name, scene);
      scene.save();
      return new Scene.Editor(scene);
    }

    @Override
    public Set<String> profiles() {
      return scope.profiles().realKeys();
    }

    @Override
    public ProfileEditor editProfile(String name) throws NoSuchElementException {
      Profile profile = scope.profiles().get(name.toLowerCase());
      if (profile == null) {
        throw new NoSuchElementException("There is no profile with name: " + name);
      }
      return new Profile.Editor(profile);
    }

    @Override
    public ProfileEditor createProfile(String name) {
      Profile existingProfile = scope.profiles().get(name.toLowerCase());
      if (existingProfile != null) {
        throw new IllegalArgumentException("A host already exists with the name " + existingProfile.name());
      }
      Profile profile = new Profile(scope.name, name);
      scope.profiles().put(name.toLowerCase(), profile);
      profile.save();
      return new Profile.Editor(profile);
    }
  }

}
