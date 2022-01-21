package com.minecraftonline.nope.sponge.storage.configurate;

import com.minecraftonline.nope.common.host.Domain;
import com.minecraftonline.nope.common.host.HostSystem;
import com.minecraftonline.nope.common.host.Universe;
import com.minecraftonline.nope.common.host.Zone;
import com.minecraftonline.nope.common.setting.SettingKeys;
import com.minecraftonline.nope.common.storage.DataHandler;
import com.minecraftonline.nope.common.storage.DomainDataHandler;
import com.minecraftonline.nope.common.storage.TemplateDataHandler;
import com.minecraftonline.nope.common.storage.UniverseDataHandler;
import com.minecraftonline.nope.common.storage.ZoneDataHandler;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.spongepowered.api.Sponge;

@AllArgsConstructor
public abstract class ConfigurateDataHandler implements DataHandler {

  private final UniverseConfigurateDataHandler universeDataHandler;
  private final DomainConfigurateDataHandler domainDataHandler;
  private final ZoneConfigurateDataHandler zoneDataHandler;
  private final TemplateConfigurateDataHandler templateConfigurateDataHandler;

  @Override
  public UniverseDataHandler universe() {
    return universeDataHandler;
  }

  @Override
  public DomainDataHandler domains() {
    return domainDataHandler;
  }

  @Override
  public ZoneDataHandler zones() {
    return zoneDataHandler;
  }

  @Override
  public TemplateDataHandler templates() {
    return templateConfigurateDataHandler;
  }

  @Override
  public HostSystem loadSystem() {
    Universe universe = universeDataHandler.load();
    List<Domain> domains = Sponge.server()
        .worldManager()
        .worlds()
        .stream()
        .map(world -> new Domain(world.key().formatted().replace(":", "-"),
            world.key().formatted(),
            universe.getValueOrDefault(SettingKeys.CACHE_SIZE).get()))
        .collect(Collectors.toList());
    domains.forEach(domainDataHandler::load);
    return new HostSystem(universe, domains);
  }

}
