/*
 * MIT License
 *
 * Copyright (c) Pieter Svenson
 * Copyright (c) MinecraftOnline
 * Copyright (c) contributors
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

package com.minecraftonline.nope.sponge.setting.manager;

import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.common.setting.SettingKeyManagers;
import com.minecraftonline.nope.sponge.util.Groups;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityCategories;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.registry.RegistryEntry;

public class SpongeSettingKeyManagerUtil {

  private SpongeSettingKeyManagerUtil() {
  }

  public static void updateSettingKeyManagers() {
    SettingKeyManagers.POLY_ENTITY_KEY_MANAGER.elementOptions(
        () -> EntityTypes.registry().streamEntries()
            .collect(Collectors.<RegistryEntry<EntityType<? extends Entity>>, String, Object>
                toMap(entity -> entity.key().value(), entity -> entity.value().asComponent())));
    SettingKeyManagers.POLY_ENTITY_KEY_MANAGER.parser(element ->
        EntityTypes.registry().streamEntries()
            .map(entity -> entity.key().value())
            .filter(name -> name.equalsIgnoreCase(element))
            .findFirst()
            .orElseThrow(() -> new SettingKey.ParseSettingException("No entity found called " + element))
            .toLowerCase()
    );
    addEntityGroup("animals", "Land animals", entity ->
        entity.category().equals(EntityCategories.CREATURE.get()));
    addEntityGroup("monsters", "Hostile creatures", entity ->
        entity.category().equals(EntityCategories.MONSTER.get()));
    addEntityGroup("ambient", "Ambient living creatures", entity ->
        entity.category().equals(EntityCategories.AMBIENT.get())
        || entity.category().equals(EntityCategories.WATER_AMBIENT.get()));
    addEntityGroup("decorative", "Non-living decorative entities", Groups.DECORATIVE_ENTITIES::contains);
    addEntityGroup("vehicles", "Vehicles", Groups.VEHICLES::contains);

    SettingKeyManagers.POLY_BLOCK_KEY_MANAGER.elementOptions(
        () -> BlockTypes.registry().streamEntries()
            .collect(Collectors.<RegistryEntry<BlockType>, String, Object>
                toMap(entity -> entity.key().value(), entity -> entity.value().asComponent())));
    SettingKeyManagers.POLY_BLOCK_KEY_MANAGER.parser(element ->
        BlockTypes.registry().streamEntries()
            .map(block -> block.key().value())
            .filter(name -> name.equalsIgnoreCase(element))
            .findFirst()
            .orElseThrow(() -> new SettingKey.ParseSettingException("No block found called " + element))
            .toLowerCase()
    );
    SettingKeyManagers.POLY_GROWABLE_KEY_MANAGER.elementOptions(
        () -> BlockTypes.registry().streamEntries()
            .filter(entry -> entry.value().defaultState().get(Keys.GROWTH_STAGE).isPresent())
            .collect(Collectors.<RegistryEntry<BlockType>, String, Object>
                toMap(entity -> entity.key().value(), entity -> entity.value().asComponent())));
    SettingKeyManagers.POLY_GROWABLE_KEY_MANAGER.parser(element ->
        BlockTypes.registry().streamEntries()
            .filter(entry -> entry.value().defaultState().get(Keys.GROWTH_STAGE).isPresent())
            .map(block -> block.key().value())
            .filter(name -> name.equalsIgnoreCase(element))
            .findFirst()
            .orElseThrow(() -> new SettingKey.ParseSettingException("No plant found called " + element))
            .toLowerCase()
    );
  }

  private static void addEntityGroup(String id, String description, Predicate<EntityType<?>> filter) {
    SettingKeyManagers.POLY_ENTITY_KEY_MANAGER.addGroup(id,
        description,
        EntityTypes.registry().streamEntries()
            .filter(entity -> filter.test(entity.value()))
            .map(entity -> entity.key().value())
            .collect(Collectors.toSet()));
  }

}
