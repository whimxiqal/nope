package com.minecraftonline.nope.control.flags;

import com.flowpowered.math.vector.Vector3d;

public class FlagVector3d extends Flag<Vector3d> {
  public FlagVector3d(Vector3d value) {
    super(value, Vector3d.class);
  }

  public FlagVector3d(Vector3d value, TargetGroup group) {
    super(value, Vector3d.class, group);
  }
}
