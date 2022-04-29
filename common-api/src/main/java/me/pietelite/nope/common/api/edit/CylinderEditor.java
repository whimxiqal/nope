package me.pietelite.nope.common.api.edit;

@SuppressWarnings("checkstyle:MethodName")
public interface CylinderEditor extends ZoneEditor {

  Alteration setDimensions(float x, float y, float z, float radius, float height);

  float x();

  float y();

  float z();

  float radius();

  float height();

}
