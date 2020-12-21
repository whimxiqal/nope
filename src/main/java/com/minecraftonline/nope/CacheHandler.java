package com.minecraftonline.nope;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A handler for dealing with various caches
 */
public class CacheHandler {
    /**
     * Caches various location and whether redstone can change
     */
    private final Map<World, Map<Modification, Boolean>> redstoneCache = new HashMap<>();

    @Nullable
    public Boolean isAllowed(World world, Vector3i loc, Object source) {
        Map<Modification, Boolean> modifications = redstoneCache.get(world);
        if (modifications == null) {
            return null;
        }
        return modifications.get(new Modification(source, loc));
    }

    public void addToCache(World world, Vector3i loc, Object source, boolean wasAllowed) {
        redstoneCache.compute(world, (k,v) -> {
            if (v == null) {
                v = new HashMap<>();
            }
            v.put(new Modification(source, loc), wasAllowed);
            return v;
        });
    }

    // TODO: only invalidate what is needed
    public void markInvalid() {
        this.redstoneCache.clear();
    }

    private static class Modification {
        private final Object source;
        private final Vector3i to;

        public Modification(Object source, Vector3i to) {
            this.source = source;
            this.to = to;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Modification that = (Modification) o;
            return source.equals(that.source) && to.equals(that.to);
        }

        @Override
        public int hashCode() {
            return Objects.hash(source, to);
        }
    }
}
