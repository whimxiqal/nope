package me.pietelite.nope.common.api.setting;

public interface SettingManagers {


  /**
   * Returns a {@link SettingManager} that can make a {@link SettingKeyBuilder} to build a key
   * for settings with {@link Boolean} values.
   * The values are set with the terms "true" and "false". Due to these bland-sounding terms,
   * the {@link #toggleManager} is generally preferred.
   *
   * @return the builder
   */
  SettingManager.Unary<Boolean> booleanManager();

  /**
   * Returns a {@link SettingManager} that can make a {@link SettingKeyBuilder} to build a key
   * for settings with {@link Boolean} values.
   * The values are set with the terms "allow" and "deny", so it is best used for settings that
   * represent the ability for a player or entity to do something.
   *
   * <p>For example, "ride" is a setting with this type because a player either is allowed to
   * ride an entity or not ride an entity.
   *
   * @return the builder
   */
  SettingManager.Unary<Boolean> stateManager();

  /**
   * Returns a {@link SettingManager} that can make a {@link SettingKeyBuilder} to build a key
   * for settings with {@link Boolean} values.
   * The values are set with the terms "on" and "off", so it is best used for settings that
   * represent a simple stand-alone processes of the server that can either happen or simply not happen.
   *
   * <p>For example, "leaf-decay" is a setting with this type because it is something the server does,
   * irrespective of player intervention.
   *
   * @return the builder
   */
  SettingManager.Unary<Boolean> toggleManager();

  /**
   * Returns a {@link SettingManager} that can make a {@link SettingKeyBuilder} to build a key
   * for settings with {@link Integer} values.
   *
   * @return the builder
   */
  SettingManager.Unary<Integer> integerManager();

  /**
   * Returns a {@link SettingManager} that can make a {@link SettingKeyBuilder} to build a key
   * for settings with {@link String} values.
   *
   * @return the builder
   */
  SettingManager.Unary<String> stringManager();

  /**
   * Returns a {@link SettingManager} that can make a {@link SettingKeyBuilder} to build a key
   * for settings with {@link String} values
   * that represent types of Minecraft entities.
   *
   * @return the builder
   */
  SettingManager.Poly<String> entityManager();

  /**
   * Returns a {@link SettingManager} that can make a {@link SettingKeyBuilder} to build a key
   * for settings with {@link String} values
   * that represent types of Minecraft blocks that are able to grow.
   *
   * @return the builder
   */
  SettingManager.Poly<String> growablesManager();

  /**
   * Returns a {@link SettingManager} that can make a {@link SettingKeyBuilder} to build a key
   * for settings with {@link String} values,
   * which are each the name of a plugin.
   *
   * @return the builder
   */
  SettingManager.Poly<String> pluginsManager();

  /**
   * Returns a {@link SettingManager} that can make a {@link SettingKeyBuilder} to build a key
   * for settings with {@link BlockChange} values.
   *
   * @return the builder
   */
  SettingManager.Poly<BlockChange> blockChangesManager();

  /**
   * Returns a {@link SettingManager} that can make a {@link SettingKeyBuilder} to build a key
   * for settings with {@link DamageCause} values.
   *
   * @return the builder
   */
  SettingManager.Poly<DamageCause> damageCausesManager();

  /**
   * Returns a {@link SettingManager} that can make a {@link SettingKeyBuilder} to build a key
   * for settings with {@link Explosive} values.
   *
   * @return the builder
   */
  SettingManager.Poly<Explosive> explosivesManager();

  /**
   * Returns a {@link SettingManager} that can make a {@link SettingKeyBuilder} to build a key
   * for settings with {@link Movement} values.
   *
   * @param id the identifier of the setting
   * @return the builder
   */
  SettingManager.Poly<Movement> movementsManager();


}
