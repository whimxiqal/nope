package com.minecraftonline.nope.control.flags;

import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.control.Region;
import com.minecraftonline.nope.control.Setting;
import com.minecraftonline.nope.control.Settings;
import com.minecraftonline.nope.control.target.TargetSet;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.entity.living.player.Player;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class FlagUtil {
  public static void serialize(@Nullable Flag<?> flag, ConfigurationNode value) throws ObjectMappingException {
    if (flag == null) {
      throw new ObjectMappingException("Cannot serialize a null flag");
    }
    value.setValue(flag.getValue());
    serializeGroup(flag, value);
  }

  public static void deserializeGroup(Flag<?> flag, ConfigurationNode value) {
    String key = (String) value.getKey();
    String group = value.getParent().getNode(key + "-group").getString();
    if (group != null) {
      try {
        flag.setGroup(Flag.TargetGroup.valueOf(group.toUpperCase()));
      } catch (IllegalArgumentException ignored) {}
    }
  }

  public static void serializeGroup(Flag<?> flag, ConfigurationNode value) {
    if (flag.getGroup() != Flag.TargetGroup.ALL) {
      String key = (String) value.getKey();
      value.getParent().getNode(key + "-group").setValue(flag.getGroup().toString());
    }
  }

  public static <T extends Flag<?>> Map.Entry<T, Region> getLastValid(List<Map.Entry<T, Region>> list, Object root) {
    for (int i = list.size() - 1; i >= 0; i--) {
      Map.Entry<T, Region> entry = list.get(i);
      if (FlagUtil.isValid(entry.getKey(), entry.getValue(), root)) {
        return entry;
      }
    }
    return null;
  }

  public static boolean isValid(Flag<?> flag, Region region, Object root) {
    switch (flag.getGroup()) {
      case ALL: return true;
      case OWNERS: return isTargeted(root, region, Settings.REGION_OWNERS);
      case MEMBERS: return isTargeted(root, region, Settings.REGION_OWNERS) || isTargeted(root, region, Settings.REGION_MEMBERS);
      case NONOWNERS: return !isTargeted(root, region, Settings.REGION_OWNERS);
      case NONMEMBERS: return !isTargeted(root, region, Settings.REGION_OWNERS) && !isTargeted(root, region, Settings.REGION_MEMBERS);
      default: {
        Nope.getInstance().getLogger().error("Missing case for enum in FlagUtil getLastValid()");
        return false;
      }
    }
  }

  private static boolean isTargeted(Object obj, Region region, Setting<TargetSet> targetSetSetting) {
    return obj instanceof Player && region.getSettingValueOrDefault(targetSetSetting).isPlayerTargeted((Player)obj);
  }
}
