/*
 *
 * MIT License
 *
 * Copyright (c) 2022 Pieter Svenson
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

package com.minecraftonline.nope.sponge.listener;

import com.minecraftonline.nope.common.setting.SettingKey;
import com.minecraftonline.nope.common.setting.SettingKeys;
import com.minecraftonline.nope.sponge.SpongeNope;
import com.minecraftonline.nope.sponge.api.event.SettingEventListener;
import com.minecraftonline.nope.sponge.api.event.SettingListenerRegistration;
import com.minecraftonline.nope.sponge.listener.dynamic.BlockChangeListener;
import com.minecraftonline.nope.sponge.listener.dynamic.DestructiveExplosivesBlockListener;
import com.minecraftonline.nope.sponge.listener.dynamic.DestructiveExplosivesExplosionListener;
import com.minecraftonline.nope.sponge.listener.dynamic.DropExpListener;
import com.minecraftonline.nope.sponge.listener.dynamic.FireEffectListener;
import com.minecraftonline.nope.sponge.listener.dynamic.GrowablesListener;
import com.minecraftonline.nope.sponge.listener.dynamic.HarmfulExplosivesDamageListener;
import com.minecraftonline.nope.sponge.listener.dynamic.HarmfulExplosivesExplosionListener;
import com.minecraftonline.nope.sponge.listener.dynamic.HookableEntitiesListener;
import com.minecraftonline.nope.sponge.listener.dynamic.InteractiveBlocksListener;
import com.minecraftonline.nope.sponge.listener.dynamic.InteractiveEntitiesListener;
import com.minecraftonline.nope.sponge.listener.dynamic.InvincibleEntitiesListener;
import com.minecraftonline.nope.sponge.listener.dynamic.ItemDropListener;
import com.minecraftonline.nope.sponge.listener.dynamic.ItemPickupListener;
import com.minecraftonline.nope.sponge.listener.dynamic.PlayerCollisionListener;
import com.minecraftonline.nope.sponge.listener.dynamic.SpawnableEntitiesListener;
import com.minecraftonline.nope.sponge.listener.dynamic.SpecificBlockChangeListener;
import com.minecraftonline.nope.sponge.util.Groups;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.action.FishingEvent;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.CollideEntityEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.world.ExplosionEvent;

public class NopeSettingListeners {

  private NopeSettingListeners() {
  }

  public static void register() {
    one(SettingKeys.BLOCK_CHANGE,
        ChangeBlockEvent.All.class,
        new BlockChangeListener());
    one(SettingKeys.CONCRETE_SOLIDIFICATION,
        ChangeBlockEvent.All.class,
        SpecificBlockChangeListener.manyToMany(Groups.CONCRETE_POWDER, Groups.CONCRETE));
    one(SettingKeys.DESTRUCTIVE_EXPLOSIVES,
        ChangeBlockEvent.All.class,
        new DestructiveExplosivesBlockListener());
    one(SettingKeys.DESTRUCTIVE_EXPLOSIVES,
        ExplosionEvent.Pre.class,
        new DestructiveExplosivesExplosionListener());
    one(SettingKeys.DROP_EXP,
        SpawnEntityEvent.Pre.class,
        new DropExpListener());
    one(SettingKeys.FIRE_EFFECT,
        ChangeBlockEvent.All.class,
        new FireEffectListener());
    one(SettingKeys.FIRE_IGNITION,
        ChangeBlockEvent.All.class,
        SpecificBlockChangeListener.forFinal(BlockTypes.FIRE.get()));
    one(SettingKeys.FROSTED_ICE_FORM,
        ChangeBlockEvent.All.class,
        SpecificBlockChangeListener.oneToOne(BlockTypes.WATER.get(),
            BlockTypes.FROSTED_ICE.get()));
    one(SettingKeys.FROSTED_ICE_MELT,
        ChangeBlockEvent.All.class,
        SpecificBlockChangeListener.oneToOne(BlockTypes.FROSTED_ICE.get(),
            BlockTypes.WATER.get(),
            true));
    one(SettingKeys.GRASS_GROWTH,
        ChangeBlockEvent.All.class,
        SpecificBlockChangeListener.oneToOne(BlockTypes.DIRT.get(), BlockTypes.GRASS_BLOCK.get()));
    one(SettingKeys.GROWABLES,
        ChangeBlockEvent.All.class,
        new GrowablesListener());
    one(SettingKeys.HOOKABLE_ENTITIES,
        FishingEvent.HookEntity.class,
        new HookableEntitiesListener());
    one(SettingKeys.ICE_FORM,
        ChangeBlockEvent.All.class,
        SpecificBlockChangeListener.oneToOne(BlockTypes.WATER.get(),
            BlockTypes.ICE.get()));
    one(SettingKeys.ICE_MELT,
        ChangeBlockEvent.All.class,
        SpecificBlockChangeListener.oneToOne(BlockTypes.ICE.get(),
            BlockTypes.WATER.get(),
            true));
    one(SettingKeys.INTERACTIVE_BLOCKS,
        InteractBlockEvent.Secondary.class,
        new InteractiveBlocksListener());
    one(SettingKeys.INTERACTIVE_ENTITIES,
        InteractEntityEvent.Secondary.class,
        new InteractiveEntitiesListener());
    one(SettingKeys.INVINCIBLE_ENTITIES,
        DamageEntityEvent.class,
        new InvincibleEntitiesListener());
    one(SettingKeys.ITEM_DROP,
        DropItemEvent.Pre.class,
        new ItemDropListener.DropItemPre());
    one(SettingKeys.ITEM_PICKUP,
        ChangeInventoryEvent.Pickup.class,
        new ItemPickupListener());
    one(SettingKeys.HARMFUL_EXPLOSIVES,
        DamageEntityEvent.class,
        new HarmfulExplosivesDamageListener());
    one(SettingKeys.HARMFUL_EXPLOSIVES,
        ExplosionEvent.Pre.class,
        new HarmfulExplosivesExplosionListener());
    one(SettingKeys.MYCELIUM_SPREAD,
        ChangeBlockEvent.All.class,
        SpecificBlockChangeListener.oneToOne(BlockTypes.DIRT.get(), BlockTypes.MYCELIUM.get()));
    one(SettingKeys.PLAYER_COLLISION,
        CollideEntityEvent.class,
        new PlayerCollisionListener());
    one(SettingKeys.SPAWNABLE_ENTITIES,
        SpawnEntityEvent.Pre.class,
        new SpawnableEntitiesListener());
  }

  private static <T, E extends Event> void one(SettingKey<? extends T, ?, ?> settingKey,
                                               Class<E> eventClass,
                                               SettingEventListener<T, E> settingEventListener) {
    SpongeNope.instance()
        .settingListeners()
        .stage(new SettingListenerRegistration<>(settingKey,
            eventClass,
            SpongeNope.instance().pluginContainer(),
            settingEventListener));
    settingKey.functional(true);
  }

}
