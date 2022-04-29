package me.pietelite.nope.common.api;

import java.util.NoSuchElementException;
import java.util.UUID;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.api.evaluate.Evaluator;
import me.pietelite.nope.common.api.struct.AltSet;
import me.pietelite.nope.common.host.Domain;
import me.pietelite.nope.common.struct.Location;
import org.jetbrains.annotations.Nullable;

public class EvaluatorImpl implements Evaluator {

  private Domain domain(String domainName) {
    Domain domain = Nope.instance().system().domains().get(domainName.toLowerCase());
    if (domain == null) {
      throw new NoSuchElementException("There is no domain with name " + domainName);
    }
    return domain;
  }

  @Override
  public <T> AltSet<T> polySetting(String setting, double x, double y, double z,
                                   String domain, Class<T> type) {
    return Nope.instance().system()
        .lookup(Nope.instance().settingKeys().getPolySetting(setting, type),
            null,
            new Location(x, y, z, domain(domain)))
        .result();
  }

  @Override
  public <T> AltSet<T> polySetting(String setting, double x, double y, double z,
                                   String domain, @Nullable UUID player, Class<T> type) {
    return Nope.instance().system()
        .lookup(Nope.instance().settingKeys().getPolySetting(setting, type),
            player,
            new Location(x, y, z, domain(domain)))
        .result();
  }

  @Override
  public <T> AltSet<T> polySettingGlobal(String setting, Class<T> type) {
    return Nope.instance().system()
        .lookupGlobal(Nope.instance().settingKeys().getPolySetting(setting, type), null)
        .result();
  }

  @Override
  public <T> AltSet<T> polySettingGlobal(String setting, @Nullable UUID player, Class<T> type) {
    return Nope.instance().system()
        .lookupGlobal(Nope.instance().settingKeys().getPolySetting(setting, type), player)
        .result();
  }

  @Override
  public <T> T unarySetting(String setting, double x, double y, double z, String domain, Class<T> type) {
    return Nope.instance().system()
        .lookup(Nope.instance().settingKeys().getUnarySetting(setting, type),
            null,
            new Location(x, y, z, domain(domain)))
        .result();
  }

  @Override
  public <T> T unarySetting(String setting, double x, double y, double z,
                            String domain, @Nullable UUID player, Class<T> type) {
    return Nope.instance().system()
        .lookup(Nope.instance().settingKeys().getUnarySetting(setting, type),
            player,
            new Location(x, y, z, domain(domain)))
        .result();
  }

  @Override
  public <T> T unarySettingGlobal(String setting, Class<T> type) {
    return Nope.instance().system()
        .lookupGlobal(Nope.instance().settingKeys().getUnarySetting(setting, type), null)
        .result();
  }

  @Override
  public <T> T unarySettingGlobal(String setting, @Nullable UUID player, Class<T> type) {
    return Nope.instance().system()
        .lookupGlobal(Nope.instance().settingKeys().getUnarySetting(setting, type), player)
        .result();
  }
}
