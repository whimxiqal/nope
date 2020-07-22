package com.minecraftonline.nope.listener.flag;

import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.control.Region;
import com.minecraftonline.nope.control.Setting;
import com.minecraftonline.nope.control.flags.Flag;
import com.minecraftonline.nope.control.flags.FlagUtil;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Map;

public class FlagListener {
  public <T extends Flag<?>> boolean shouldCancel(Setting<T> setting, Location<World> location, Object cause) {
    List<Map.Entry<T, Region>> states = Nope.getInstance().getGlobalHost().getRegions(location).getSettingValue(setting);
    return FlagUtil.getLastValid(states, cause) != null;
  }
}
