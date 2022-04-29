package me.pietelite.nope.common.api.edit;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public interface HostEditor {

  String name();

  List<String> profiles();

  Alteration addProfile(String name, int index) throws IndexOutOfBoundsException, NoSuchElementException, IllegalArgumentException;

  Alteration removeProfile(String name) throws IllegalArgumentException;

  Alteration removeProfile(int index) throws IllegalArgumentException;

  boolean hasTarget(int index) throws IndexOutOfBoundsException, IllegalArgumentException;

  TargetEditor editTarget(String name) throws NoSuchElementException, IllegalArgumentException;

  TargetEditor editTarget(int index) throws IndexOutOfBoundsException, IllegalArgumentException;

}
