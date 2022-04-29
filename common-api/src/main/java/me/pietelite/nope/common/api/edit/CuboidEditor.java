package me.pietelite.nope.common.api.edit;

public interface CuboidEditor extends ZoneEditor {

  Alteration setDimensions(float x1, float y1, float z1, float x2, float y2, float z2);

  float minX();

  float minY();

  float minZ();

  float maxX();

  float maxY();

  float maxZ();

}
