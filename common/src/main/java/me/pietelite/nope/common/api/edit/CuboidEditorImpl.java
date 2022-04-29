package me.pietelite.nope.common.api.edit;

import me.pietelite.nope.common.host.Domain;
import me.pietelite.nope.common.host.Scene;
import me.pietelite.nope.common.math.Cuboid;

public class CuboidEditorImpl extends ZoneEditorImpl<Cuboid> implements CuboidEditor {

  public CuboidEditorImpl(Scene scene, int index) {
    super(scene, index, Cuboid.class);
  }

  @Override
  public Alteration setDimensions(float x1, float y1, float z1, float x2, float y2, float z2) {
    update(new Cuboid(volume.domain(), x1, y1, z1, x2, y2, z2));
    return AlterationImpl.success("Updated the dimensions of the cuboid at index " + index);
  }

  @Override
  public float minX() {
    return volume.minX();
  }

  @Override
  public float minY() {
    return volume.minY();
  }

  @Override
  public float minZ() {
    return volume.minZ();
  }

  @Override
  public float maxX() {
    return volume.maxX();
  }

  @Override
  public float maxY() {
    return volume.maxY();
  }

  @Override
  public float maxZ() {
    return volume.maxZ();
  }

  @Override
  public Alteration setDomainObject(Domain domain) {
    update(new Cuboid(domain, volume.minX(), volume.minY(), volume.minZ(),
        volume.maxX(), volume.maxY(), volume.maxZ()));
    return AlterationImpl.success("Updated the domain of the cuboid at index " + index);
  }

}
