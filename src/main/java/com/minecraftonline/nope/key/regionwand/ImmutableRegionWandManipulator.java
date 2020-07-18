package com.minecraftonline.nope.key.regionwand;

import com.minecraftonline.nope.key.NopeKeys;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableBooleanData;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

public class ImmutableRegionWandManipulator extends AbstractImmutableBooleanData<ImmutableRegionWandManipulator, RegionWandManipulator> {
  public ImmutableRegionWandManipulator(boolean value) {
    super(NopeKeys.REGION_WAND, value, false);
  }

  public ImmutableValue<Boolean> isWand() {
    return this.getValueGetter();
  }

  @Override
  public RegionWandManipulator asMutable() {
    return new RegionWandManipulator(getValue());
  }

  @Override
  public int getContentVersion() {
    return 0;
  }
}
