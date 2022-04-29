package me.pietelite.nope.common.api.edit;

import java.util.List;
import java.util.NoSuchElementException;

public interface SceneEditor extends HostEditor {

  Alteration name(String name);

  Alteration addCuboid(String domain, float x1, float y1, float z1, float x2, float y2, float z2);

  Alteration addCylinder(String domain, float x, float y, float z, float radius, float height);

  Alteration addSlab(String domain, float y, float height);

  Alteration addSphere(String domain, float x, float y, float z, float radius);

  int priority();

  Alteration priority(int priority);

  List<ZoneType> zoneTypes();

  CuboidEditor editCuboid(int index) throws IllegalArgumentException;

  CylinderEditor editCylinder(int index) throws IllegalArgumentException;

  SlabEditor editSlab(int index) throws IllegalArgumentException;

  SphereEditor editSphere(int index) throws IllegalArgumentException;

  Alteration destroy() throws NoSuchElementException;

}
