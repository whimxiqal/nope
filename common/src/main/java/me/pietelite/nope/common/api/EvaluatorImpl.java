/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
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

package me.pietelite.nope.common.api;

import java.util.NoSuchElementException;
import java.util.UUID;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.api.evaluate.Evaluator;
import me.pietelite.nope.common.api.struct.AltSet;
import me.pietelite.nope.common.host.Domain;
import me.pietelite.nope.common.struct.Location;
import org.jetbrains.annotations.Nullable;

/**
 * Implementation of the {@link Evaluator}.
 */
public class EvaluatorImpl implements Evaluator {

  private Domain domain(String domainName) {
    Domain domain = Nope.instance().system().domains().get(domainName.toLowerCase());
    if (domain == null) {
      throw new NoSuchElementException("There is no domain with name " + domainName);
    }
    return domain;
  }

  @Override
  public <T> AltSet<T> polySetting(String setting, float x, float y, float z,
                                   String domain, Class<T> type) {
    return Nope.instance().system()
        .lookup(Nope.instance().settingKeys().getPolySetting(setting, type),
            null,
            new Location(x, y, z, domain(domain)))
        .result();
  }

  @Override
  public <T> AltSet<T> polySetting(String setting, float x, float y, float z,
                                   String domain, @Nullable UUID player, Class<T> type) {
    return Nope.instance().system()
        .lookup(Nope.instance().settingKeys().getPolySetting(setting, type),
            player,
            new Location(x, y, z, domain(domain)))
        .result();
  }


  @Override
  public <T> AltSet<T> polySettingBlock(String setting, int x, int y, int z,
                                        String domain, Class<T> type) {
    return Nope.instance().system()
        .lookupBlock(Nope.instance().settingKeys().getPolySetting(setting, type),
            null,
            domain(domain), x, y, z)
        .result();
  }

  @Override
  public <T> AltSet<T> polySettingBlock(String setting, int x, int y, int z,
                                        String domain, @Nullable UUID player, Class<T> type) {
    return Nope.instance().system()
        .lookupBlock(Nope.instance().settingKeys().getPolySetting(setting, type),
            player,
            domain(domain), x, y, z)
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
  public <T> T unarySetting(String setting, float x, float y, float z, String domain, Class<T> type) {
    return Nope.instance().system()
        .lookup(Nope.instance().settingKeys().getUnarySetting(setting, type),
            null,
            new Location(x, y, z, domain(domain)))
        .result();
  }

  @Override
  public <T> T unarySetting(String setting, float x, float y, float z,
                            String domain, @Nullable UUID player, Class<T> type) {
    return Nope.instance().system()
        .lookup(Nope.instance().settingKeys().getUnarySetting(setting, type),
            player,
            new Location(x, y, z, domain(domain)))
        .result();
  }

  @Override
  public <T> T unarySettingBlock(String setting, int x, int y, int z, String domain, Class<T> type) {
    return Nope.instance().system()
        .lookupBlock(Nope.instance().settingKeys().getUnarySetting(setting, type),
            null,
            domain(domain), x, y, z)
        .result();
  }

  @Override
  public <T> T unarySettingBlock(String setting, int x, int y, int z,
                                 String domain, @Nullable UUID player, Class<T> type) {
    return Nope.instance().system()
        .lookupBlock(Nope.instance().settingKeys().getUnarySetting(setting, type),
            player,
            domain(domain), x, y, z)
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
