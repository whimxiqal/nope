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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.api.edit.CuboidEditor;
import me.pietelite.nope.common.api.edit.CuboidEditorImpl;
import me.pietelite.nope.common.api.edit.CylinderEditor;
import me.pietelite.nope.common.api.edit.CylinderEditorImpl;
import me.pietelite.nope.common.api.edit.SceneEditor;
import me.pietelite.nope.common.api.edit.SlabEditor;
import me.pietelite.nope.common.api.edit.SlabEditorImpl;
import me.pietelite.nope.common.api.edit.SphereEditor;
import me.pietelite.nope.common.api.edit.SphereEditorImpl;
import me.pietelite.nope.common.api.edit.ZoneEditor;
import me.pietelite.nope.common.api.edit.ZoneEditorImpl;
import me.pietelite.nope.common.api.edit.ZoneType;
import me.pietelite.nope.common.math.Cuboid;
import me.pietelite.nope.common.math.Cylinder;
import me.pietelite.nope.common.math.Slab;
import me.pietelite.nope.common.math.Sphere;
import me.pietelite.nope.common.math.Volume;
import me.pietelite.nope.common.struct.Location;
import me.pietelite.nope.common.struct.Scoped;

/**
 * A {@link Host} that contains all points inside a group of {@link Volume}s.
 * There can be arbitrarily many of them and can be user-created.
 */
public class Scene extends Host implements Scoped {

  private final String scope;
  private List<Volume> volumes = new LinkedList<>();
  boolean destroyed;

  public Scene(String scope, String name, int priority) {
    super(name, priority);
    this.scope = scope;
    if (name.startsWith("_")) {
      throw new IllegalArgumentException("A zone name may not start with an underscore");
    }
  }

  public String scope() {
    return this.scope;
  }

  public void priority(int priority) {
    this.priority = priority;
  }

  public List<Volume> volumes() {
    return volumes;
  }

  public void volumes(List<Volume> volumes) {
    this.volumes = volumes;
  }

  @Override
  public boolean contains(Location location) {
    return volumes.stream().anyMatch(volume -> volume.containsPoint(location));
  }

  @Override
  public void save() {
    Nope.instance().data().scenes(scope).save(this);
  }

  /**
   * Implementation for a {@link SceneEditor}.
   */
  public static class Editor extends Host.Editor<Scene> implements SceneEditor {

    public Editor(Scene scene) {
      super(scene);
    }

    private Scope scope() {
      return Nope.instance().system().scope(host.scope);
    }

    @Override
    public boolean name(String name) {
      host.verifyExistence();
      if (host.name().equals(name)) {
        // No change
        return false;
      }
      // We should allow case-change of characters if it's the same name otherwise as the current host
      if (!host.name().equalsIgnoreCase(name) && Nope.instance().system().hasName(host.scope, name)) {
        throw new IllegalArgumentException("A host with the name \"" + name + "\" already exists");
      }
      if (name.startsWith("_")) {
        throw new IllegalArgumentException("Scene names cannot start with an underscore");
      }
      Scene newScene = new Scene(host.scope(), name, host.priority());
      newScene.hostedProfiles().addAll(host.hostedProfiles());
      newScene.volumes().addAll(host.volumes());
      host.expire();
      scope().scenes().remove(host.name());
      scope().scenes().put(newScene.name(), newScene);
      newScene.save();
      this.host = newScene;
      return true;
    }

    @Override
    public CuboidEditor addCuboid(String domainName,
                                  float x1, float y1, float z1,
                                  float x2, float y2, float z2) {
      host.verifyExistence();
      Domain domain = Nope.instance().system().domains().get(domainName);
      if (domain == null) {
        throw new NoSuchElementException();
      }
      Cuboid cuboid = new Cuboid(domain, x1, y1, z1, x2, y2, z2);
      int index = Nope.instance().system().addVolume(cuboid, host);
      return new CuboidEditorImpl(host, index);
    }

    @Override
    public CylinderEditor addCylinder(String domainName,
                                      float x, float y, float z,
                                      float radius, float height) {
      host.verifyExistence();
      Domain domain = Nope.instance().system().domains().get(domainName);
      if (domain == null) {
        throw new NoSuchElementException();
      }
      if (radius <= 0 || height <= 0) {
        throw new IllegalArgumentException("The radius and height of a cylinder must be positive");
      }
      Cylinder cylinder = new Cylinder(domain, x, y, y + height, z, radius);
      int index = Nope.instance().system().addVolume(cylinder, host);
      return new CylinderEditorImpl(host, index);
    }

    @Override
    public SlabEditor addSlab(String domainName, float y, float height) {
      host.verifyExistence();
      Domain domain = Nope.instance().system().domains().get(domainName);
      if (domain == null) {
        throw new NoSuchElementException();
      }
      if (height <= 0) {
        throw new IllegalArgumentException("The height of a slab must be positive");
      }
      Slab slab = new Slab(domain, y, y + height);
      int index = Nope.instance().system().addVolume(slab, host);
      return new SlabEditorImpl(host, index);
    }

    @Override
    public SphereEditor addSphere(String domainName, float x, float y, float z, float radius) {
      host.verifyExistence();
      Domain domain = Nope.instance().system().domains().get(domainName);
      if (domain == null) {
        throw new NoSuchElementException();
      }
      if (radius <= 0) {
        throw new IllegalArgumentException("The radius of a sphere must be positive");
      }
      Sphere sphere = new Sphere(domain, x, y, z, radius);
      int index = Nope.instance().system().addVolume(sphere, host);
      return new SphereEditorImpl(host, index);
    }

    @Override
    public void priority(int priority) {
      host.verifyExistence();
      if (priority < 0) {
        throw new IllegalArgumentException("Cannot set a negative priority");
      }
      host.priority = priority;
      host.save();
    }

    @Override
    public List<ZoneType> zoneTypes() {
      host.verifyExistence();
      return Collections.unmodifiableList(host.volumes().stream().map(Volume::zoneType)
          .collect(Collectors.toList()));
    }

    @Override
    public ZoneEditor editZone(int index) {
      host.verifyExistence();
      return new ZoneEditorImpl<>(host, index);
    }

    @Override
    public CuboidEditor editCuboid(int index) throws IllegalArgumentException {
      host.verifyExistence();
      return new CuboidEditorImpl(host, index);
    }

    @Override
    public CylinderEditor editCylinder(int index) throws IllegalArgumentException {
      host.verifyExistence();
      return new CylinderEditorImpl(host, index);
    }

    @Override
    public SlabEditor editSlab(int index) throws IllegalArgumentException {
      host.verifyExistence();
      return new SlabEditorImpl(host, index);
    }

    @Override
    public SphereEditor editSphere(int index) throws IllegalArgumentException {
      host.verifyExistence();
      return new SphereEditorImpl(host, index);
    }

    @Override
    public void destroy() {
      host.verifyExistence();
      Set<Domain> domains = new HashSet<>();
      host.volumes().forEach(volume -> {
        volume.domain().volumes().remove(volume, false);
        domains.add(volume.domain());
      });
      scope().scenes().remove(host.name());
      Nope.instance().data().scenes(host.scope).destroy(host);
      host.volumes().clear();
      host.allProfiles().forEach(profile ->
          scope().relatedToProfile(profile.profile().name()).remove(host));
      host.expire();
      domains.forEach(domain -> domain.volumes().construct());
    }

  }

}
