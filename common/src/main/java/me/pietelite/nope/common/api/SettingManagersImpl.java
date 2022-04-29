package me.pietelite.nope.common.api;

import me.pietelite.nope.common.api.register.SettingKeyBuilder;
import me.pietelite.nope.common.api.register.SettingManagers;
import me.pietelite.nope.common.api.register.data.BlockChange;
import me.pietelite.nope.common.api.register.data.DamageCause;
import me.pietelite.nope.common.api.register.data.Explosive;
import me.pietelite.nope.common.api.register.data.Movement;
import me.pietelite.nope.common.setting.SettingKeyManagers;

public class SettingManagersImpl implements SettingManagers {

  @Override
  public SettingKeyBuilder.Unary<Boolean, ?> booleanKeyBuilder(String id) {
    return SettingKeyManagers.BOOLEAN_KEY_MANAGER.keyBuilder(id);
  }

  @Override
  public SettingKeyBuilder.Unary<Boolean, ?> stateKeyBuilder(String id) {
    return SettingKeyManagers.STATE_KEY_MANAGER.keyBuilder(id);
  }

  @Override
  public SettingKeyBuilder.Unary<Boolean, ?> toggleKeyBuilder(String id) {
    return SettingKeyManagers.TOGGLE_KEY_MANAGER.keyBuilder(id);
  }

  @Override
  public SettingKeyBuilder.Unary<Integer, ?> integerKeyBuilder(String id) {
    return SettingKeyManagers.INTEGER_KEY_MANAGER.keyBuilder(id);
  }

  @Override
  public SettingKeyBuilder.Unary<String, ?> stringKeyBuilder(String id) {
    return SettingKeyManagers.STRING_KEY_MANAGER.keyBuilder(id);
  }

  @Override
  public SettingKeyBuilder.Poly<String, ?, ?> entitiesManager(String id) {
    return SettingKeyManagers.POLY_ENTITY_KEY_MANAGER.keyBuilder(id);
  }

  @Override
  public SettingKeyBuilder.Poly<String, ?, ?> growablesManager(String id) {
    return SettingKeyManagers.POLY_GROWABLE_KEY_MANAGER.keyBuilder(id);
  }

  @Override
  public SettingKeyBuilder.Poly<String, ?, ?> pluginsManager(String id) {
    return SettingKeyManagers.POLY_PLUGIN_MANAGER.keyBuilder(id);
  }

  @Override
  public SettingKeyBuilder.Poly<BlockChange, ?, ?> blockChangesManager(String id) {
    return SettingKeyManagers.POLY_BLOCK_CHANGE_KEY_MANAGER.keyBuilder(id);
  }

  @Override
  public SettingKeyBuilder.Poly<DamageCause, ?, ?> damageCausesManager(String id) {
    return SettingKeyManagers.POLY_DAMAGE_SOURCE_KEY_MANAGER.keyBuilder(id);
  }

  @Override
  public SettingKeyBuilder.Poly<Explosive, ?, ?> explosivesManager(String id) {
    return SettingKeyManagers.POLY_EXPLOSIVE_KEY_MANAGER.keyBuilder(id);
  }

  @Override
  public SettingKeyBuilder.Poly<Movement, ?, ?> movementsManager(String id) {
    return SettingKeyManagers.POLY_MOVEMENT_KEY_MANAGER.keyBuilder(id);
  }

}
