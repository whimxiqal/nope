package me.pietelite.nope.common.api.evaluate;

import java.util.UUID;
import me.pietelite.nope.common.api.struct.AltSet;
import org.jetbrains.annotations.Nullable;

public interface Evaluator {

  <T> AltSet<T> polySetting(String setting, double x, double y, double z, String domain, Class<T> type);

  <T> AltSet<T> polySetting(String setting, double x, double y, double z, String domain, @Nullable UUID player, Class<T> type);

  <T> AltSet<T> polySettingGlobal(String setting, Class<T> type);

  <T> AltSet<T> polySettingGlobal(String setting, @Nullable UUID player, Class<T> type);

  <T> T unarySetting(String setting, double x, double y, double z, String domain, Class<T> type);

  <T> T unarySetting(String setting, double x, double y, double z, String domain, @Nullable UUID player, Class<T> type);

  <T> T unarySettingGlobal(String setting, Class<T> type);

  <T> T unarySettingGlobal(String setting, @Nullable UUID player, Class<T> type);

}
