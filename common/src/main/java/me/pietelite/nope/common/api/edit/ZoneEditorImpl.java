package me.pietelite.nope.common.api.edit;

import java.util.NoSuchElementException;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.host.Domain;
import me.pietelite.nope.common.host.Scene;
import me.pietelite.nope.common.math.Volume;

public abstract class ZoneEditorImpl<V extends Volume> implements ZoneEditor {

  protected final Scene scene;
  protected final int index;
  protected final V volume;

  ZoneEditorImpl(Scene scene, int index, Class<V> volumeClass) {
    scene.verifyExistence();
    this.scene = scene;
    this.index = index;
    Volume v = scene.volumes().get(index);
    if (!volumeClass.isInstance(v)) {
      throw new ClassCastException("The requested volume type was not found at index " + index);
    }
    this.volume = volumeClass.cast(v);
  }

  @Override
  public final String domain() {
    volume.verifyExistence();
    return volume.domain().name();
  }

  @Override
  public final Alteration domain(String domainName) {
    volume.verifyExistence();
    Domain domain = Nope.instance().system().domains().get(domainName);
    if (domain == null) {
      throw new NoSuchElementException("There is no domain with name " + domainName);
    }
    return setDomainObject(domain);
  }

  protected abstract Alteration setDomainObject(Domain domain);

  protected final void update(Volume newVolume) {
    scene.verifyExistence();
    volume.verifyExistence();
    volume.domain().volumes().remove(volume, false);
    volume.domain().volumes().put(newVolume, scene, true);
    volume.markDestroyed();
    scene.volumes().set(index, newVolume);
    scene.save();
  }

}
