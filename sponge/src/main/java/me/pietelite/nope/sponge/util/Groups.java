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

package me.pietelite.nope.sponge.util;

import com.google.common.collect.Sets;
import java.util.Set;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;

/**
 * A utility class to hold useful groups of {@link BlockTypes}.
 */
public final class Groups {

  public static final Set<BlockType> LIQUID_GRIEFABLE = Sets.newHashSet(
      BlockTypes.BEETROOTS.get(),
      BlockTypes.BROWN_MUSHROOM.get(),
      BlockTypes.CARROTS.get(),
      BlockTypes.DEAD_BUSH.get(),
      BlockTypes.MELON_STEM.get(),
      BlockTypes.NETHER_WART.get(),
      BlockTypes.POTATOES.get(),
      BlockTypes.PUMPKIN_STEM.get(),
      BlockTypes.POPPY.get(),
      BlockTypes.RED_MUSHROOM.get(),
      BlockTypes.ACACIA_SAPLING.get(),
      BlockTypes.BAMBOO_SAPLING.get(),
      BlockTypes.BIRCH_SAPLING.get(),
      BlockTypes.JUNGLE_SAPLING.get(),
      BlockTypes.DARK_OAK_SAPLING.get(),
      BlockTypes.OAK_SAPLING.get(),
      BlockTypes.SNOW.get(),
      BlockTypes.TALL_GRASS.get(),
      BlockTypes.LILY_PAD.get(),
      BlockTypes.WHEAT.get(),
      BlockTypes.DANDELION.get()
  );
  public static final Set<EntityType<?>> DECORATIVE_ENTITIES = Sets.newHashSet(
      EntityTypes.PAINTING.get(),
      EntityTypes.ARMOR_STAND.get()
  );
  public static final Set<EntityType<?>> VEHICLES = Sets.newHashSet(
      EntityTypes.BOAT.get(),
      EntityTypes.MINECART.get()
  );
  public static final Set<BlockType> CONCRETE_POWDER = Sets.newHashSet(
      BlockTypes.WHITE_CONCRETE_POWDER.get(),
      BlockTypes.ORANGE_CONCRETE_POWDER.get(),
      BlockTypes.MAGENTA_CONCRETE_POWDER.get(),
      BlockTypes.LIGHT_BLUE_CONCRETE_POWDER.get(),
      BlockTypes.YELLOW_CONCRETE_POWDER.get(),
      BlockTypes.LIME_CONCRETE_POWDER.get(),
      BlockTypes.PINK_CONCRETE_POWDER.get(),
      BlockTypes.GRAY_CONCRETE_POWDER.get(),
      BlockTypes.LIGHT_GRAY_CONCRETE_POWDER.get(),
      BlockTypes.CYAN_CONCRETE_POWDER.get(),
      BlockTypes.PURPLE_CONCRETE_POWDER.get(),
      BlockTypes.BLUE_CONCRETE_POWDER.get(),
      BlockTypes.BROWN_CONCRETE_POWDER.get(),
      BlockTypes.GREEN_CONCRETE_POWDER.get(),
      BlockTypes.RED_CONCRETE_POWDER.get(),
      BlockTypes.BLACK_CONCRETE_POWDER.get()
  );
  public static final Set<BlockType> CONCRETE = Sets.newHashSet(
      BlockTypes.WHITE_CONCRETE.get(),
      BlockTypes.ORANGE_CONCRETE.get(),
      BlockTypes.MAGENTA_CONCRETE.get(),
      BlockTypes.LIGHT_BLUE_CONCRETE.get(),
      BlockTypes.YELLOW_CONCRETE.get(),
      BlockTypes.LIME_CONCRETE.get(),
      BlockTypes.PINK_CONCRETE.get(),
      BlockTypes.GRAY_CONCRETE.get(),
      BlockTypes.LIGHT_GRAY_CONCRETE.get(),
      BlockTypes.CYAN_CONCRETE.get(),
      BlockTypes.PURPLE_CONCRETE.get(),
      BlockTypes.BLUE_CONCRETE.get(),
      BlockTypes.BROWN_CONCRETE.get(),
      BlockTypes.GREEN_CONCRETE.get(),
      BlockTypes.RED_CONCRETE.get(),
      BlockTypes.BLACK_CONCRETE.get()
  );

  private Groups() {
  }

}
