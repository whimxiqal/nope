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

package me.pietelite.nope.sponge.storage.configurate.serializer;

import io.leangen.geantyref.TypeToken;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import me.pietelite.nope.common.setting.Target;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

public class TargetTypeSerializer implements TypeSerializer<Target> {
  @Override
  public Target deserialize(Type type, ConfigurationNode node) throws SerializationException {
    Target target = Target.all();
    if (!node.node("permissions").virtual()) {
      target.permissions().putAll(node.node("permissions").childrenMap()
          .entrySet()
          .stream()
          .collect(Collectors.toMap(entry -> (String) entry.getKey(),
              entry -> entry.getValue().getBoolean())));
    }
    if (!node.node("whitelist").virtual()) {
      target.targetNone();
      target.users().addAll(node.node("whitelist").require(new TypeToken<Set<UUID>>() {
      }));
    } else if (!node.node("blacklist").virtual()) {
      target.targetAll();
      target.users().addAll(node.node("blacklist").require(new TypeToken<Set<UUID>>() {
      }));
    }
    return target;
  }

  @Override
  public void serialize(Type type, @Nullable Target obj, ConfigurationNode node) throws SerializationException {
    if (obj == null) {
      return;
    }
    node.node("permissions").set(obj.permissions());
    if (!obj.users().isEmpty()) {
      ConfigurationNode usersNode;
      if (obj.hasWhitelist()) {
        usersNode = node.node("whitelist");
      } else {
        usersNode = node.node("blacklist");
      }
      usersNode.set(obj.users());
    }
  }
}
