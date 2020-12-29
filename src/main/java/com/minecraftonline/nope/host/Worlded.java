package com.minecraftonline.nope.host;

import java.util.UUID;

/**
 * A generic interface for anything that is associated
 * with a world.
 */
public interface Worlded {
    /**
     * Gets the world this is associated with.
     * @return UUID uuid of the world.
     */
    UUID getWorldUuid();
}
