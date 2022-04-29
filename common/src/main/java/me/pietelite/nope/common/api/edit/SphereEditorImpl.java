package me.pietelite.nope.common.api.edit;

import me.pietelite.nope.common.host.Domain;
import me.pietelite.nope.common.host.Scene;
import me.pietelite.nope.common.math.Sphere;

public class SphereEditorImpl extends ZoneEditorImpl<Sphere> implements SphereEditor {
  public SphereEditorImpl(Scene scene, int index) {
    super(scene, index, Sphere.class);
  }

  @Override
  public Alteration setDimensions(float x, float y, float z, float radius) {
    update(new Sphere(volume.domain(), x, y, z, radius));
    return AlterationImpl.success("Updated the dimensions of the sphere at index "
        + index + " for host " + scene.name());
  }

  @Override
  public float x() {
    return volume.posX();
  }

  @Override
  public float y() {
    return volume.posY();
  }

  @Override
  public float z() {
    return volume.posZ();
  }

  @Override
  public float radius() {
    return volume.radius();
  }

  @Override
  protected Alteration setDomainObject(Domain domain) {
    update(new Sphere(domain, volume.posX(), volume.posY(), volume.posZ(), volume.radius()));
    return AlterationImpl.success("Updated the domain of the cuboid at index " + index);
  }
}
