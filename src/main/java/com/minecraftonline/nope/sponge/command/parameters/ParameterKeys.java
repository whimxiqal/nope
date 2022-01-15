package com.minecraftonline.nope.sponge.command.parameters;

import com.minecraftonline.nope.common.host.Host;
import com.minecraftonline.nope.common.host.Zone;
import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.common.setting.template.Template;
import com.minecraftonline.nope.common.math.Cuboid;
import com.minecraftonline.nope.common.math.Cylinder;
import com.minecraftonline.nope.common.math.Sphere;
import io.leangen.geantyref.TypeToken;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.world.server.ServerWorld;

public class ParameterKeys {

  public static final Parameter.Key<Host> HOST = Parameter.key("host", Host.class);
  public static final Parameter.Key<Template> TEMPLATE = Parameter.key("template", Template.class);
  public static final Parameter.Key<Integer> PRIORITY = Parameter.key("priority", Integer.class);
  public static final Parameter.Key<String> NAME = Parameter.key("name", String.class);
  public static final Parameter.Key<String> DESCRIPTION = Parameter.key("parameter", String.class);
  public static final Parameter.Key<String> REGEX = Parameter.key("regex", String.class);
  public static final Parameter.Key<SettingKey<?, ?>> SETTING_KEY = Parameter.key("setting-key", new TypeToken<SettingKey<?, ?>>(){});
  public static final Parameter.Key<String> SETTING_DATA = Parameter.key("setting-value", String.class);
  public static final Parameter.Key<Set<CompletableFuture<GameProfile>>> PLAYER_LIST = Parameter.key("player-list", new TypeToken<Set<CompletableFuture<GameProfile>>>(){});
  public static final Parameter.Key<String> PERMISSION = Parameter.key("permission", String.class);
  public static final Parameter.Key<Boolean> PERMISSION_VALUE = Parameter.key("permission-value", Boolean.class);
  public static final Parameter.Key<TargetOption> TARGET_OPTION = Parameter.key("target-option", TargetOption.class);
  public static final Parameter.Key<Zone> PARENT = Parameter.key("parent", Zone.class);
  public static final Parameter.Key<Zone> ZONE = Parameter.key("zone", Zone.class);
  public static final Parameter.Key<Cuboid> CUBOID = Parameter.key("cuboid", Cuboid.class);
  public static final Parameter.Key<Cylinder> CYLINDER = Parameter.key("cylinder", Cylinder.class);
  public static final Parameter.Key<Sphere> SPHERE = Parameter.key("sphere", Sphere.class);
  public static final Parameter.Key<Integer> POS_X = Parameter.key("position-x", Integer.class);
  public static final Parameter.Key<Integer> POS_Y = Parameter.key("position-y", Integer.class);
  public static final Parameter.Key<Integer> POS_Z = Parameter.key("position-z", Integer.class);
  public static final Parameter.Key<Integer> POS_X_1 = Parameter.key("position-x-1", Integer.class);
  public static final Parameter.Key<Integer> POS_Y_1 = Parameter.key("position-y-1", Integer.class);
  public static final Parameter.Key<Integer> POS_Z_1 = Parameter.key("position-z-1", Integer.class);
  public static final Parameter.Key<Integer> POS_X_2 = Parameter.key("position-x-2", Integer.class);
  public static final Parameter.Key<Integer> POS_Y_2 = Parameter.key("position-y-2", Integer.class);
  public static final Parameter.Key<Integer> POS_Z_2 = Parameter.key("position-z-2", Integer.class);
  public static final Parameter.Key<Double> RADIUS = Parameter.key("radius", Double.class);
  public static final Parameter.Key<ServerWorld> WORLD = Parameter.key("world", ServerWorld.class);
  public static final Parameter.Key<Integer> INDEX = Parameter.key("index", Integer.class);
}
