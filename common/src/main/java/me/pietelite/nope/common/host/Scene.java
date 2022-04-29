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
import me.pietelite.nope.common.api.edit.Alteration;
import me.pietelite.nope.common.api.edit.AlterationImpl;
import me.pietelite.nope.common.api.edit.CuboidEditor;
import me.pietelite.nope.common.api.edit.CuboidEditorImpl;
import me.pietelite.nope.common.api.edit.CylinderEditor;
import me.pietelite.nope.common.api.edit.CylinderEditorImpl;
import me.pietelite.nope.common.api.edit.SceneEditor;
import me.pietelite.nope.common.api.edit.SlabEditor;
import me.pietelite.nope.common.api.edit.SlabEditorImpl;
import me.pietelite.nope.common.api.edit.SphereEditor;
import me.pietelite.nope.common.api.edit.SphereEditorImpl;
import me.pietelite.nope.common.api.edit.ZoneType;
import me.pietelite.nope.common.math.Cuboid;
import me.pietelite.nope.common.math.Cylinder;
import me.pietelite.nope.common.math.Slab;
import me.pietelite.nope.common.math.Sphere;
import me.pietelite.nope.common.math.Volume;
import me.pietelite.nope.common.struct.Location;

/**
 * A {@link Host} that contains all points inside a group of {@link Volume}s.
 * There can be arbitrarily many of them and can be user-created.
 */
public class Scene extends Host {

  protected final List<Volume> volumes = new LinkedList<>();
  boolean destroyed;

  public Scene(String name, int priority) {
    super(name, priority);
    if (name.startsWith("_")) {
      throw new IllegalArgumentException("A zone name may not start with an underscore");
    }
  }

  public void priority(int priority) {
    this.priority = priority;
  }

  public List<Volume> volumes() {
    return new LinkedList<>(volumes);
  }

  /**
   * Remove a {@link Volume}.
   *
   * @param index the index at which to remove the zone
   * @return the removed volume
   */
  public Volume remove(int index) {
    Volume volume = volumes.remove(index);
    save();
    volume.domain().volumes().remove(volume, true);
    return volume;
  }

  /**
   * Remove a {@link Volume}.
   *
   * @param volume the specific volume to move
   * @return true if it was removed
   */
  public boolean remove(Volume volume) {
    boolean removed = volumes.remove(volume);
    save();
    volume.domain().volumes().remove(volume, true);
    return removed;
  }

  @Override
  public boolean contains(Location location) {
    return volumes.stream().anyMatch(volume -> volume.containsPoint(location));
  }

  @Override
  public void markDestroyed() {
    destroyed = true;
  }

  @Override
  public void save() {
    Nope.instance().data().scenes().save(this);
  }

  public static class Editor extends Host.Editor<Scene> implements SceneEditor {

    public Editor(Scene scene) {
      super(scene);
    }

    private static void updatePriority(Scene scene, int newPriority, UpdatePrioritiesResult result) {
      if (newPriority < 0) {
        throw new IllegalArgumentException("Cannot set a negative priority");
      }
      if (newPriority >= Integer.MAX_VALUE) {
        result.failChangedCount++;
        return;
      }
      scene.priority = newPriority;
      result.successfullyChangedCount++;
      scene.save();
      scene.volumes().forEach(volume -> volume.domain().volumes()
          .intersecting(scene)
          .stream()
          .filter(other -> !other.equals(scene))
          .filter(other -> scene.priority() == other.priority())
          .forEach(zone -> updatePriority(zone, scene.priority + 1, result)));
    }

    @Override
    public Alteration name(String name) {
      host.verifyExistence();
      if (Nope.instance().system().host(name).isPresent()) {
        throw new IllegalArgumentException("A host with the name \"" + name + "\" already exists");
      }
      if (name.startsWith("_")) {
        throw new IllegalArgumentException("Scene names cannot start with an underscore");
      }
      Scene newScene = new Scene(name, host.priority());
      newScene.hostedProfiles().addAll(host.hostedProfiles());
      newScene.volumes().addAll(host.volumes());
      host.markDestroyed();
      Nope.instance().system().scenes().remove(host.name());
      Nope.instance().system().scenes().put(newScene.name(), newScene);
      newScene.save();
      return AlterationImpl.success("Renamed scene \"" + host.name() + "\" to \"" + name + "\"");
    }

    @Override
    public Alteration addCuboid(String domainName, float x1, float y1, float z1, float x2, float y2, float z2) {
      host.verifyExistence();
      Domain domain = Nope.instance().system().domains().get(domainName);
      if (domain == null) {
        throw new IllegalArgumentException();
      }
      Cuboid cuboid = new Cuboid(domain, x1, y1, z1, x2, y2, z2);
      host.volumes().add(cuboid);
      domain.volumes().put(cuboid, host, true);
      host.save();
      return AlterationImpl.success("A box has been added to scene " + host.name());
    }

    @Override
    public Alteration addCylinder(String domainName, float x, float y, float z, float radius, float height) {
      host.verifyExistence();
      Domain domain = Nope.instance().system().domains().get(domainName);
      if (domain == null) {
        return AlterationImpl.fail("The domain " + domainName + " doesn't exist");
      }
      Cylinder cylinder = new Cylinder(domain, x, y, y + height, z, radius);
      host.volumes().add(cylinder);
      domain.volumes().put(cylinder, host, true);
      host.save();
      return AlterationImpl.success("A box has been added to scene " + host.name());
    }

    @Override
    public Alteration addSlab(String domainName, float y, float height) {
      host.verifyExistence();
      Domain domain = Nope.instance().system().domains().get(domainName);
      if (domain == null) {
        return AlterationImpl.fail("The domain " + domainName + " doesn't exist");
      }
      Slab slab = new Slab(domain, y, y + height);
      host.volumes().add(slab);
      domain.volumes().put(slab, host, true);
      host.save();
      return AlterationImpl.success("A box has been added to scene " + host.name());
    }

    @Override
    public Alteration addSphere(String domainName, float x, float y, float z, float radius) {
      host.verifyExistence();
      Domain domain = Nope.instance().system().domains().get(domainName);
      if (domain == null) {
        return AlterationImpl.fail("The domain " + domainName + " doesn't exist");
      }
      Sphere sphere = new Sphere(domain, x, y, z, radius);
      host.volumes().add(sphere);
      domain.volumes().put(sphere, host, true);
      host.save();
      return AlterationImpl.success("A box has been added to scene " + host.name());
    }

    @Override
    public int priority() {
      host.verifyExistence();
      return host.priority;
    }

    @Override
    public Alteration priority(int priority) {
      host.verifyExistence();
      if (priority < 0) {
        throw new IllegalArgumentException("Cannot set a negative priority");
      }
      UpdatePrioritiesResult result = new UpdatePrioritiesResult();
      updatePriority(host, priority, result);
      if (result.failChangedCount > 0) {
        return AlterationImpl.warn(result.successfullyChangedCount
            + " scenes' priorities were updated, but "
            + result.failChangedCount
            + " scenes could not be updated. This may lead to unexpected problems");
      }
      return AlterationImpl.success("Updated " + result.successfullyChangedCount + " scenes' priorities");
    }

    @Override
    public List<ZoneType> zoneTypes() {
      host.verifyExistence();
      return Collections.unmodifiableList(host.volumes().stream().map(Volume::zoneType)
          .collect(Collectors.toList()));
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
    public Alteration destroy() {
      host.verifyExistence();
      if (Nope.instance().system().scenes().remove(host.name) == null) {
        throw new NoSuchElementException("There is not host with name " + name());
      }
      Set<Domain> domains = new HashSet<>();
      host.volumes.forEach(volume -> {
        volume.domain().volumes().remove(volume, false);
        domains.add(volume.domain());
      });
      Nope.instance().data().scenes().destroy(host);
      host.volumes.clear();
      profiles().clear();
      host.markDestroyed();
      domains.forEach(domain -> domain.volumes().construct());
      return AlterationImpl.success("Removed scene " + host.name);
    }

    private static class UpdatePrioritiesResult {
      int successfullyChangedCount;
      int failChangedCount;
    }

  }

}
