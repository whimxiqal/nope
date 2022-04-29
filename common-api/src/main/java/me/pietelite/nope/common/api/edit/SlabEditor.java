package me.pietelite.nope.common.api.edit;

public interface SlabEditor extends ZoneEditor {

  Alteration setDimensions(float y, float height);

  float y();

  float height();

}
