package me.pietelite.nope.common.api.edit;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import me.pietelite.nope.common.api.struct.AltSet;

public interface SystemEditor {

  HostEditor editGlobal();

  Set<String> domains();

  HostEditor editDomain(String name) throws NoSuchElementException;

  Set<String> scenes();

  SceneEditor editScene(String name) throws NoSuchElementException;

  Alteration createScene(String name, int priority) throws IllegalArgumentException;

  Set<String> profiles();

  ProfileEditor editProfile(String name) throws NoSuchElementException;

  Alteration createProfile(String name);

}
