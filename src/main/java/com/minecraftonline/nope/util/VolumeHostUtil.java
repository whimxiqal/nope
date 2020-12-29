package com.minecraftonline.nope.util;

import com.flowpowered.math.vector.Vector3i;
import com.minecraftonline.nope.Nope;
import com.minecraftonline.nope.host.Host;
import com.minecraftonline.nope.host.HostTree;
import com.minecraftonline.nope.host.VolumeHost;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.world.World;

import java.util.UUID;

public class VolumeHostUtil {
    /**
     * Removes and re-adds a region.
     * This should be only be used when the region cannot be mutated as this
     * has to remove the region before adding the new one.
     * @param src Source to send error messages etc to
     * @param host Host to delete and add similar one
     * @param newName New name of the host
     * @param world New world of the host
     * @param min New corner of region
     * @param max New corner of region
     */
    public static Host remakeRegion(CommandSource src, Host host, String newName, World world, Vector3i min, Vector3i max) throws IllegalArgumentException {
        HostTree hostTree = Nope.getInstance().getHostTree();

        hostTree.removeRegion(host.getName());

        try {
            Host newHost = hostTree.addRegion(world.getUniqueId(),
                    newName,
                    min,
                    max,
                    host.getPriority()
            );
            newHost.putAll(host.getAll());
            return newHost;
        } catch (IllegalArgumentException e) {
            // Add back the region we removed.
            VolumeHost volumeHost = (VolumeHost) host;
            UUID worldUUID = volumeHost.getWorldUuid();
            try {
                hostTree.addRegion(worldUUID,
                        host.getName(),
                        min,
                        max,
                        host.getPriority());
                throw e;
            } catch (IllegalArgumentException e2) {
                // UH OH!
                src.sendMessage(Format.error("Failed moving region, and then failed adding back the original region."));
                src.sendMessage(Format.error("If you wish to keep this region please make a backup of the config before restarting!!"));
                src.sendMessage(Format.error("Logging circumstances now. Please report this asap!"));
                Vector3i fromMin = Vector3i.from(volumeHost.xMin(), volumeHost.yMin(), volumeHost.zMin());
                Vector3i fromMax = Vector3i.from(volumeHost.xMax(), volumeHost.yMax(), volumeHost.zMax());
                String worldName = Sponge.getServer().getWorld(worldUUID).map(World::getName).orElse(worldUUID.toString());

                Logger logger = Nope.getInstance().getLogger();
                logger.error("Failed moving region " + host.getName() + ", from: " + fromMin + " " + fromMax + ", world: " + worldName);
                logger.error("To: " + min + " " + max + " world: " + world.getName());
                logger.error("Due to error: ", e);
                logger.error("Then failed to add back the region!", e2);
            }
            return null;
        }
    }
}
