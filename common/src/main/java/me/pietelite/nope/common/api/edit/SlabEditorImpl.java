package me.pietelite.nope.common.api.edit;

import me.pietelite.nope.common.host.Domain;
import me.pietelite.nope.common.host.Scene;
import me.pietelite.nope.common.math.Slab;

public class SlabEditorImpl extends ZoneEditorImpl<Slab> implements SlabEditor {
  public SlabEditorImpl(Scene scene, int index) {
    super(scene, index, Slab.class);
  }

  @Override
  public Alteration setDimensions(float y, float height) {
    update(new Slab(volume.domain(), y, y + height));
    return AlterationImpl.success("Updated the dimensions of the slab at index "
        + index + " for host " + scene.name());
  }

  @Override
  public float y() {
    return 0;
  }

  @Override
  public float height() {
    return 0;
  }

  @Override
  protected Alteration setDomainObject(Domain domain) {
    update(new Slab(domain, volume.minY(), volume.maxY()));
    return AlterationImpl.success("Updated the domain of the slab at index "
        + index + " for host " + scene.name());
  }
}
