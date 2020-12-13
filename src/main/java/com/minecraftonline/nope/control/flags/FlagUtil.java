/*
 * MIT License
 *
 * Copyright (c) 2020 MinecraftOnline
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.minecraftonline.nope.control.flags;

import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.control.Region;
import com.minecraftonline.nope.control.Setting;
import com.minecraftonline.nope.control.Settings;
import com.minecraftonline.nope.control.target.TargetSet;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.permission.Subject;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
      if (FlagUtil.appliesTo(entry.getKey(), entry.getValue(), root)) {
        return entry;
      }
    }
    return null;
  }

  public static boolean appliesTo(Flag<?> flag, Region region, Object root) {
    switch (flag.getGroup()) {
      case ALL: return true;
      case OWNERS: return isPlayerTargeted(root, region, Settings.REGION_OWNERS);
      case MEMBERS: return isPlayerTargeted(root, region, Settings.REGION_OWNERS) || isPlayerTargeted(root, region, Settings.REGION_MEMBERS);
      case NONOWNERS: return !isPlayerTargeted(root, region, Settings.REGION_OWNERS);
      case NONMEMBERS: return !isPlayerTargeted(root, region, Settings.REGION_OWNERS) && !isPlayerTargeted(root, region, Settings.REGION_MEMBERS);
      default: {
        Nope.getInstance().getLogger().error("Missing case for enum in FlagUtil getLastValid()");
        return false;
      }
    }
  }

  public static boolean isPlayerTargeted(Object obj, Region region, Setting<TargetSet> targetSetSetting) {
    return obj instanceof Player && region.getSettingValueOrDefault(targetSetSetting).isPlayerTargeted((Player)obj);
  }

  public static boolean appliesTo(Flag<?> flag, Region region, Membership membership) {
    Membership.Status status = membership.apply(region);
    switch (flag.getGroup()) {
      case ALL: return true;
      case OWNERS: return status == Membership.Status.OWNER;
      case MEMBERS: return status == Membership.Status.OWNER || status == Membership.Status.MEMBER;
      case NONMEMBERS: return !(status == Membership.Status.OWNER || status == Membership.Status.MEMBER);
      case NONOWNERS: return status != Membership.Status.OWNER;
      default: throw new IllegalStateException("More values to an enum than expected!");
    }
  }

  /**
   * Make a flag using reflection
   * @param defaultFlag flag to grab constructor from
   * @param value That is compatible, i.e instanceof T
   * @param <V> Underlying flag value
   * @return Created flag with given value
   */
  @SuppressWarnings("unchecked")
  public static <V> Flag<V> makeFlag(Flag<V> defaultFlag, Object value) {
    try {
      for (Constructor<?> constructor : defaultFlag.getClass().getConstructors()) {
        if (constructor.getParameterCount() == 1
            && constructor.getParameterTypes()[0].isAssignableFrom(value.getClass())) {
          return (Flag<V>) constructor.newInstance(value);
        }
      }
      throw new IllegalStateException("Failed to make flag, could not find applicable constructor, that had 1 arg, and that arg was the flag's value.");
    } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
      Nope.getInstance().getLogger().error("Error making flag, was there no single constructor with the value?", e);
    }
    throw new IllegalStateException("Error while making flag - no value to return");
  }
}
