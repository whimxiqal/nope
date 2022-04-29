package me.pietelite.nope.common.api.edit;

import me.pietelite.nope.common.host.Domain;
import me.pietelite.nope.common.host.Scene;
import me.pietelite.nope.common.math.Cylinder;

public class CylinderEditorImpl extends ZoneEditorImpl<Cylinder> implements CylinderEditor {
  public CylinderEditorImpl(Scene scene, int index) {
    super(scene, index, Cylinder.class);
  }

  @Override
  public Alteration setDimensions(float x, float y, float z, float radius, float height) {
    update(new Cylinder(volume.domain(), x, y, y + height, z, radius));
    return AlterationImpl.success("Updated the dimensions of the cylinder at index "
        + index + " for host " + scene.name());
  }

  @Override
  public float x() {
    return volume.posX();
  }

  @Override
  public float y() {
    return volume.minY();
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
  public float height() {
    return volume.maxY() - volume.minY();
  }

  @Override
  protected Alteration setDomainObject(Domain domain) {
    update(new Cylinder(domain, volume.posX(), volume.minY(), volume.maxY(), volume.posZ(), volume.radius()));
    return AlterationImpl.success("Updated the domain of the cylinder at index "
        + index + " for host " + scene.name());
  }
}
