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

import java.lang.reflect.Type;
import me.pietelite.nope.common.Nope;
import me.pietelite.nope.common.host.HostedProfile;
import me.pietelite.nope.common.host.Profile;
import me.pietelite.nope.common.setting.Target;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

public class HostedProfileTypeSerializer implements TypeSerializer<HostedProfile> {

  @Override
  public HostedProfile deserialize(Type type, ConfigurationNode node) throws SerializationException {
    String profileName = node.node("name").get(String.class);
    if (profileName == null) {
      throw new SerializationException("Could not get profile name for a HostedProfile");
    }
    Profile profile = Nope.instance().system().profiles().get(node.node("name").get(String.class));
    if (profile == null) {
      throw new SerializationException("Could not find profile with name " + profileName);
    }
    Target target = node.node("target").get(Target.class);
    return new HostedProfile(profile, target);
  }

  @Override
  public void serialize(Type type, @Nullable HostedProfile obj, ConfigurationNode node) throws SerializationException {
    if (obj == null) {
      return;
    }
    node.node("name").set(obj.profile().name());
    node.node("target").set(obj.target());
  }
}
