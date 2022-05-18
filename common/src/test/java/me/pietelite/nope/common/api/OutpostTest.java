package me.pietelite.nope.common.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import me.pietelite.nope.common.api.edit.MultipleValueSettingEditor;
import me.pietelite.nope.common.api.edit.ProfileEditor;
import me.pietelite.nope.common.api.edit.SceneEditor;
import me.pietelite.nope.common.api.edit.ScopeEditor;
import me.pietelite.nope.common.api.setting.BlockChange;
import me.pietelite.nope.common.setting.SettingKeys;
import me.pietelite.nope.common.setting.sets.BlockChangeSet;
import org.junit.jupiter.api.Test;

/**
 * Outpost is a conceptual plugin that will manage state of "outposts" of players.
 * The idea is to use Nope as the engine for a Towny/Faction type server.
 */
public class OutpostTest extends ApiTest {

  static final String OUTPOST_SCOPE = "outpost";

  static ScopeEditor outpostScopeEditor() {
    return NopeServiceProvider.service().editSystem().editScope(OUTPOST_SCOPE);
  }

  static class Outpost {
    String name;
    String id;
    Map<UUID, Set<Role>> members;

    public Outpost(String name, String id) {
      this.name = name;
      this.id = id;
      this.members = new HashMap<>();
    }

    public String sceneName() {
      return "outpost-" + id;
    }

    public SceneEditor sceneEditor() {
      return outpostScopeEditor().editScene(sceneName());
    }

    public boolean assignRole(UUID uuid, Role role) {
      boolean addedToMap = this.members.computeIfAbsent(uuid, k -> new HashSet<>()).add(role);
      boolean addedToProfile = outpostScopeEditor().editProfile(role.profileName()).editTarget().addPlayer(uuid);
      return addedToMap && addedToProfile;
    }

    public boolean unassignRole(UUID uuid, Role role) {
      if (!members.containsKey(uuid)) {
        return false;
      }
      boolean removedFromMap = members.get(uuid).remove(role);
      if (members.get(uuid).isEmpty()) {
        members.remove(uuid);
      }
      boolean removedFromProfile = outpostScopeEditor().editProfile(role.profileName()).editTarget().removePlayer(uuid);
      return removedFromMap && removedFromProfile;
    }

  }

  static class Role {
    String name;

    Role(String name) {
      this.name = name;
    }

    public String profileName() {
      return "outpost-role-" + name;
    }
  }

  static Role OWNER = new Role("owner");
  static Role SHERIFF = new Role("sheriff");
  static Role GARDENER = new Role("gardener");
  static Role NAVIGATOR = new Role("navigator");

  @Test
  void outpostCaseStudy() {
    NopeServiceProvider.service().editSystem().registerScope(OUTPOST_SCOPE);

    // ===============
    // Create profiles
    // ===============

    // General "outpost" profile
    ProfileEditor editor = outpostScopeEditor().createProfile("outpost");
    BlockChangeSet blockChangeSet = new BlockChangeSet();
    blockChangeSet.add(BlockChange.BREAK);
    blockChangeSet.add(BlockChange.PLACE);
    blockChangeSet.add(BlockChange.MODIFY);
    // People cannot break, place, or modify blocks in the outpost
    editor.editMultipleValueSetting(SettingKeys.BLOCK_CHANGE.name(), BlockChange.class).setManipulative(blockChangeSet,
        MultipleValueSettingEditor.ManipulativeType.SUBTRACTIVE);
    editor.editMultipleValueSetting(SettingKeys.INTERACTIVE_BLOCKS.name(), String.class).setDeclarative(
        SettingKeys.INTERACTIVE_BLOCKS.manager().emptySet());
  }

}
