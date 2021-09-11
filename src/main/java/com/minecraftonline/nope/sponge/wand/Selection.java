package com.minecraftonline.nope.sponge.wand;

import com.minecraftonline.nope.common.Nope;
import com.minecraftonline.nope.common.math.Vector3i;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;

@RequiredArgsConstructor
public class Selection {

  @Getter
  @NonNull
  private final ResourceKey worldKey;
  private final int x1;
  private final int y1;
  private final int z1;
  private final int x2;
  private final int y2;
  private final int z2;

  public boolean isValid() {
    return x1 <= Nope.WORLD_RADIUS
        && x1 >= -Nope.WORLD_RADIUS
        && y1 <= Nope.WORLD_DEPTH
        && y1 >= 0
        && z1 <= Nope.WORLD_RADIUS
        && z1 >= -Nope.WORLD_RADIUS
        && x2 <= Nope.WORLD_RADIUS
        && x2 >= -Nope.WORLD_RADIUS
        && y2 <= Nope.WORLD_DEPTH
        && y2 >= 0
        && z2 <= Nope.WORLD_RADIUS
        && z2 >= -Nope.WORLD_RADIUS;
  }

  public Vector3i minPosition() {
    return new Vector3i(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2));
  }

  public Vector3i maxPosition() {
    return new Vector3i(Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2));
  }

  public ServerWorld world() {
    return Sponge.server()
        .worldManager()
        .world(worldKey)
        .orElseThrow(() -> new RuntimeException("Selection stored an invalid world key: " + worldKey.formatted()));
  }

  @Data
  public static final class Draft {
    @Nullable
    private Position position1;
    @Nullable
    private Position position2;

    public Draft() {
      this.position1 = null;
      this.position2 = null;
    }

    public Draft(Position position1, Position position2) {
      this.position1 = position1;
      this.position2 = position2;
    }

    public Optional<Selection> build(List<String> errors) {
      if (position1 == null || position2 == null) {
        errors.add("You must select your both positions");
        return Optional.empty();
      }

      if (!position1.worldKey.equals(position2.worldKey)) {
        errors.add("The worlds of the two selected points must be the same");
      }

      if (position1.x > Nope.WORLD_RADIUS
          || position1.x < -Nope.WORLD_RADIUS
          || position1.z > Nope.WORLD_RADIUS
          || position1.z < -Nope.WORLD_RADIUS) {
        errors.add("Your first selected point is out of the world's radius");
      }

      if (position2.x > Nope.WORLD_RADIUS
          || position2.x < -Nope.WORLD_RADIUS
          || position2.z > Nope.WORLD_RADIUS
          || position2.z < -Nope.WORLD_RADIUS) {
        errors.add("Your second selected point is out of the world's radius");
      }

      if (position1.y > Nope.WORLD_DEPTH) {
        errors.add("Your first selected point is too high");
      }

      if (position1.y < 0) {
        errors.add("Your first selected point is too low");
      }

      if (position2.y > Nope.WORLD_DEPTH) {
        errors.add("Your second selected point is too high");
      }

      if (position2.y < 0) {
        errors.add("Your second selected point is too low");
      }

      if (errors.isEmpty()) {
        return Optional.of(new Selection(position1.worldKey,
            position1.x,
            position1.y,
            position1.z,
            position2.x,
            position2.y,
            position2.z))
            .filter(Selection::isValid);
      } else {
        return Optional.empty();
      }
    }

  }

  @Data
  @AllArgsConstructor
  public static final class Position {
    @NonNull
    private ResourceKey worldKey;
    private int x;
    private int y;
    private int z;

    public Position(ServerLocation location) {
      this.worldKey = location.world().key();
      this.x = location.blockX();
      this.y = location.blockY();
      this.z = location.blockZ();
    }
  }

}
