package me.pietelite.nope.common.api.edit;

@SuppressWarnings("checkstyle:MethodName")
public interface SphereEditor extends ZoneEditor {

  Alteration setDimensions(float x, float y, float z, float radius);

  float x();

  float y();

  float z();

  float radius();

}
