package com.minecraftonline.nope.common.host;

public abstract class Domained<H extends Host<?>> extends Host<H> {

  public Domained(String name, H parent, int priority) {
    super(name, parent, priority);
  }

}
