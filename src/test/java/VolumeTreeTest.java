/*
 * MIT License
 *
 * Copyright (c) 2020 MinecraftOnline
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
 *
 */

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minecraftonline.nope.structures.Volume;
import com.minecraftonline.nope.structures.VolumeTree;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import lombok.Data;
import org.junit.jupiter.api.Test;

/**
 * A test class for the {@link VolumeTree}.
 */
public class VolumeTreeTest {

  private static final int WORLD_X_WIDTH = 10000;
  private static final int WORLD_Y_WIDTH = 256;
  private static final int WORLD_Z_WIDTH = 10000;
  private static final int ZONE_MIN_X_WIDTH = 10;
  private static final int ZONE_MIN_Y_WIDTH = 10;
  private static final int ZONE_MIN_Z_WIDTH = 10;
  private static final int ZONE_MAX_X_WIDTH = 100;
  private static final int ZONE_MAX_Y_WIDTH = 100;
  private static final int ZONE_MAX_Z_WIDTH = 100;
  private static final int TEST_POINT_COUNT = 10000;
  private static final int ZONE_COUNT = 1000;
  private static final boolean DEBUG = false;

  @Data
  static class TestVolume implements Volume {

    final int minX;
    final int maxX;
    final int minY;
    final int maxY;
    final int minZ;
    final int maxZ;

    @Override
    public int getMinX() {
      return minX;
    }

    @Override
    public int getMaxX() {
      return maxX;
    }

    @Override
    public int getMinY() {
      return minY;
    }

    @Override
    public int getMaxY() {
      return maxY;
    }

    @Override
    public int getMinZ() {
      return minZ;
    }

    @Override
    public int getMaxZ() {
      return maxZ;
    }
  }

  private void surround(char[][] board, int x, int z, char c) {
    for (int i = -1; i <= 1; i++) {
      for (int j = -1; j <= 1; j++) {
        if (i == 0 && j == 0) {
          continue;
        }
        board[x + i][z + j] = c;
      }
    }
  }

  private char[][] constructBoard(Collection<? extends Volume> volumes, int xsize, int ysize) {
    char[][] board = new char[xsize][ysize];
    for (int x = 0; x < xsize; x++) {
      for (int y = 0; y < ysize; y++) {
        board[x][y] = '0';
      }
    }
    for (Volume volume : volumes) {
      for (int x = volume.getMinX(); x <= volume.getMaxX(); x++) {
        for (int z = volume.getMinZ(); z <= volume.getMaxZ(); z++) {
          if (board[x][z] == '9' || board[x][z] == '+') {
            board[x][z] = '+';
          } else {
            board[x][z]++;
          }
        }
      }
    }
    for (int x = 0; x < xsize; x++) {
      for (int z = 0; z < ysize; z++) {
        if (board[x][z] == '0') {
          board[x][z] = ' ';
        }
      }
    }
    return board;
  }

  private void printBoard(char[][] board, int xsize, int ysize) {
    for (int y = 0; y < ysize; y++) {
      for (int x = 0; x < xsize; x++) {
        System.out.print(board[x][y]);
      }
      System.out.println();
    }
  }

  private int[][][] findAnswers(Collection<? extends Volume> volumes, int xsize, int ysize, int zsize) {
    int[][][] answers = new int[xsize][ysize][zsize];
    for (int z = 0; z < zsize; z++) {
      for (int y = 0; y < ysize; y++) {
        for (int x = 0; x < xsize; x++) {
          answers[x][y][z] = 0;
        }
      }
    }
    for (Volume v : volumes) {
      for (int z = v.getMinZ(); z <= v.getMaxZ(); z++) {
        for (int y = v.getMinY(); y <= v.getMaxY(); y++) {
          for (int x = v.getMinX(); x <= v.getMaxX(); x++) {
            answers[x][y][z]++;
          }
        }
      }
    }
    return answers;
  }

  private int[][][] findSolutions(VolumeTree<?, ?> tree, int xsize, int ysize, int zsize) {
    int[][][] solutions = new int[xsize][ysize][zsize];
    for (int z = 0; z < zsize; z++) {
      for (int y = 0; y < ysize; y++) {
        for (int x = 0; x < xsize; x++) {
          solutions[x][y][z] = tree.containersOf(x, y, z).size();
        }
      }
    }
    return solutions;
  }

  private void checkAnswers(Collection<? extends Volume> volumes,
                            VolumeTree<?, ?> tree,
                            int boardSizeX, int boardSizeY, int boardSizeZ,
                            boolean print) throws RuntimeException {
    int[][][] answers = findAnswers(volumes, boardSizeX, boardSizeY, boardSizeZ);
    int[][][] solutions = findSolutions(tree, boardSizeX, boardSizeY, boardSizeZ);

    int succeeded = 0;
    int failed = 0;
    for (int z = 0; z < boardSizeZ; z++) {
      for (int y = 0; y < boardSizeY; y++) {
        for (int x = 0; x < boardSizeX; x++) {
          if (answers[x][y][z] != solutions[x][y][z]) {
            System.out.printf("Wrong answer at (%d, %d, %d) -- Answer: %d, Solution: %d%n",
                x, y, z, answers[x][y][z], solutions[x][y][z]);
            failed++;
          } else {
            succeeded++;
          }
        }
      }
    }
    if (print) {
      tree.print();
    }
    System.out.printf("Successfully identified zones: %d\n", succeeded);
    System.out.printf("Failed identified zones: %d\n", failed);
    if (failed > 0) {
      throw new RuntimeException("Answers != Solutions");
    }
  }

  @Test
  public void testRandom() {
    Random random = new Random();

    Map<Integer, Volume> zones = Maps.newHashMap();

    int locationX;
    int locationY;
    int locationZ;
    int sizeX;
    int sizeY;
    int sizeZ;
    for (int i = 0; i < ZONE_COUNT; i++) {
      locationX = random.nextInt(WORLD_X_WIDTH - ZONE_MAX_X_WIDTH + 1);
      locationY = random.nextInt(WORLD_Y_WIDTH - ZONE_MAX_Y_WIDTH + 1);
      locationZ = random.nextInt(WORLD_Z_WIDTH - ZONE_MAX_Z_WIDTH + 1);
      sizeX = random.nextInt(ZONE_MAX_X_WIDTH - ZONE_MIN_X_WIDTH + 1) + ZONE_MIN_X_WIDTH;
      sizeY = random.nextInt(ZONE_MAX_Y_WIDTH - ZONE_MIN_Y_WIDTH + 1) + ZONE_MIN_Y_WIDTH;
      sizeZ = random.nextInt(ZONE_MAX_Z_WIDTH - ZONE_MIN_Z_WIDTH + 1) + ZONE_MIN_Z_WIDTH;

      zones.put(i, new TestVolume(
          locationX, locationX + sizeX - 1,
          locationY, locationY + sizeY - 1,
          locationZ, locationZ + sizeZ - 1));
    }

    // Build board
    char[][] board = constructBoard(zones.values(), WORLD_X_WIDTH, WORLD_Z_WIDTH);

    // print board
    if (DEBUG) {
      System.out.println("Entire Board:");
      printBoard(board, WORLD_X_WIDTH, WORLD_Z_WIDTH);
    }

    // Now actually testing
    VolumeTree<Integer, Volume> tree = new VolumeTree<>();
    long constructionElapse = System.currentTimeMillis();
    tree.addAll(zones);
    constructionElapse = System.currentTimeMillis() - constructionElapse;

    // Get answers (should take a long time, comparatively) and solution
    HashMap<Integer, Set<String>> answers = Maps.newHashMap();
    HashMap<Integer, Set<String>> solutions = Maps.newHashMap();
    int[] xpoints = new int[TEST_POINT_COUNT];
    int[] ypoints = new int[TEST_POINT_COUNT];
    int[] zpoints = new int[TEST_POINT_COUNT];

    for (int i = 0; i < TEST_POINT_COUNT; i++) {
      int x = random.nextInt(WORLD_X_WIDTH - 2) + 1;
      int y = random.nextInt(WORLD_Y_WIDTH);
      int z = random.nextInt(WORLD_Z_WIDTH - 2) + 1;
      surround(board, x, z, '.');
      xpoints[i] = x;
      ypoints[i] = y;
      zpoints[i] = z;
      answers.put(i, Sets.newHashSet());
      solutions.put(i, Sets.newHashSet());
    }

    if (DEBUG) {
      System.out.println("Print board with special zones");
      printBoard(board, WORLD_X_WIDTH, WORLD_Z_WIDTH);
    }

    long traditionalElapse = System.currentTimeMillis();
    for (int i = 0; i < TEST_POINT_COUNT; i++) {
      for (Volume volume : zones.values()) {
        if (volume.contains(xpoints[i], ypoints[i], zpoints[i])) {
          answers.get(i).add(String.format("{[%d, %d], [%d, %d], [%d, %d]}",
              volume.getMinX(),
              volume.getMaxX(),
              volume.getMinY(),
              volume.getMaxY(),
              volume.getMinZ(),
              volume.getMaxZ()));
        }
      }
    }
    traditionalElapse = System.currentTimeMillis() - traditionalElapse;

    long treeElapse = System.currentTimeMillis();
    for (int i = 0; i < TEST_POINT_COUNT; i++) {
      Set<String> dump = solutions.get(i);
      tree.containersOf(xpoints[i], ypoints[i], zpoints[i])
          .stream()
          .map(volume ->
              String.format("{[%d, %d], [%d, %d], [%d, %d]}",
                  volume.getMinX(),
                  volume.getMaxX(),
                  volume.getMinY(),
                  volume.getMaxY(),
                  volume.getMinZ(),
                  volume.getMaxZ()))
          .forEach(dump::add);
    }
    treeElapse = System.currentTimeMillis() - treeElapse;

    System.out.printf("Time to complete traditional: %d ms\n", traditionalElapse);
    System.out.printf("Time to complete volume tree: %d ms\n", treeElapse);
    System.out.printf("Time saved: %f%%\n", (1.0 - (double) treeElapse / (double) traditionalElapse) * 100);
    System.out.printf("Time to construct tree: %d ms\n", constructionElapse);

    int succeeded = 0;
    int failed = 0;
    for (int i = 0; i < TEST_POINT_COUNT; i++) {
      if (answers.get(i).size() != solutions.get(i).size() || !answers.get(i).containsAll(solutions.get(i))) {
        failed++;
        System.out.printf("Wrong Answer: Point %d -- Answer: %s, Solution: %s\n%n",
            i,
            Arrays.toString(answers.get(i).toArray()),
            Arrays.toString(solutions.get(i).toArray()));
      } else {
        succeeded++;
      }
    }
    System.out.printf("Successfully identified zones: %d\n", succeeded);
    System.out.printf("Failed identified zones: %d\n", failed);
    if (failed > 0) {
      throw new RuntimeException("See above for incorrect point calculations");
    }

    System.out.println("Statistics:");
    System.out.printf("Height: %d, Size: %d\n", tree.getHeight(), tree.size());
  }

  @Test
  public void test2Nesting() {
    Map<Integer, Volume> map = Maps.newHashMap();
    map.put(0, new TestVolume(2, 2, 2, 2, 2, 2));
    map.put(1, new TestVolume(1, 3, 1, 3, 1, 3));

    VolumeTree<Integer, Volume> tree = new VolumeTree<>();
    tree.addAll(map);

    checkAnswers(map.values(), tree, 4, 4, 4, false);
  }

  @Test
  public void testZigurat() {
    int height = 100;
    Map<Integer, Volume> map = Maps.newHashMap();
    for (int i = 0; i < height; i++) {
      map.put(i, new TestVolume(height - i, height + i, 0, 0, i, i));
    }

    VolumeTree<Integer, Volume> tree = new VolumeTree<>();
    tree.addAll(map);

    checkAnswers(map.values(), tree, height * 2, 1, height, false);
  }


  @Test
  public void testPointGrid2D() {
    testPointGrid2dHelper(2, 1, 2, 1, 0, 1, false);
    testPointGrid2dHelper(2, 1, 2, 1, 0, 0, false);
    testPointGrid2dHelper(2, 1, 1, 2, -1, 0, false);
    testPointGrid2dHelper(2, 3, 1, 1, -2, 0, false);
    testPointGrid2dHelper(2, 2, 2, 2, 0, 0, false);
  }

  /**
   * A helper class to create 2d volumes with specific standard
   * sizes and spacings.
   *
   * @param size the side length of each 2d volume
   * @param countX the count of 2d volumes in the x direction
   * @param countY the count of 2d volumes in the y direction
   * @param countZ the count of 2d volumes in the z direction
   * @param spacing the spacing between 2d volumes
   * @param borderSpacing the spacing on the borders of 2d volumes
   * @param print whether to print these 2d volumes or not (used for debugging small cases)
   */
  public void testPointGrid2dHelper(int size,
                                    int countX, int countY, int countZ,
                                    int spacing,
                                    int borderSpacing,
                                    boolean print) {

    Map<Integer, Volume> map = Maps.newHashMap();

    for (int z = 0; z < countZ; z++) {
      for (int y = 0; y < countY; y++) {
        for (int x = 0; x < countX; x++) {
          int id = z * countY * countX + y * countX + x;
          map.put(id, new TestVolume(
              x * (size + spacing) + borderSpacing,
              x * (size + spacing) + size + borderSpacing - 1,
              y * (size + spacing) + borderSpacing,
              y * (size + spacing) + size + borderSpacing - 1,
              z * (size + spacing) + borderSpacing,
              z * (size + spacing) + size + borderSpacing - 1));
        }
      }
    }

    VolumeTree<Integer, Volume> tree = new VolumeTree<>();
    tree.addAll(map);

    int boardSizeX = countX * (size + spacing) - spacing + borderSpacing * 2;
    int boardSizeY = countY * (size + spacing) - spacing + borderSpacing * 2;
    int boardSizeZ = countZ * (size + spacing) - spacing + borderSpacing * 2;
    char[][] board = constructBoard(map.values(),
        boardSizeX,
        boardSizeZ);
    if (print) {
      printBoard(board, boardSizeX, boardSizeZ);
    }
    checkAnswers(map.values(), tree, boardSizeX, boardSizeY, boardSizeZ, false);
  }

}
