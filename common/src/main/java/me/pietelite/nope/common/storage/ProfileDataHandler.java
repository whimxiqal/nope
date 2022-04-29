package me.pietelite.nope.common.storage;

import java.util.Collection;
import me.pietelite.nope.common.host.Profile;

public interface ProfileDataHandler {

  void destroy(Profile profile);

  void save(Profile profile);

  Collection<Profile> load();

}
