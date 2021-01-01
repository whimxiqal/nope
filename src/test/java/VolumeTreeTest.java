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
import com.minecraftonline.nope.structures.VolumeTree;
import lombok.Data;
import org.junit.Test;

import java.util.*;

public class VolumeTreeTest {

  int WORLD_X_WIDTH = 10000;
  int WORLD_Y_WIDTH = 256;
  int WORLD_Z_WIDTH = 10000;

  int REGION_MIN_X_WIDTH = 10;
  int REGION_MIN_Y_WIDTH = 10;
  int REGION_MIN_Z_WIDTH = 10;

  int REGION_MAX_X_WIDTH = 100;
  int REGION_MAX_Y_WIDTH = 100;
  int REGION_MAX_Z_WIDTH = 100;

  int TEST_POINT_COUNT = 10000;

  int REGION_COUNT = 1000;

  boolean DEBUG = false;

  @Data
  static class TestVolume implements VolumeTree.Volume {

    final int xmin;
    final int xmax;
    final int ymin;
    final int ymax;
    final int zmin;
    final int zmax;

    @Override
    public int getMinX() {
      return xmin;
    }

    @Override
    public int getMaxX() {
      return xmax;
    }

    @Override
    public int getMinY() {
      return ymin;
    }

    @Override
    public int getMaxY() {
      return ymax;
    }

    @Override
    public int getMinZ() {
      return zmin;
    }

    @Override
    public int getMaxZ() {
      return zmax;
    }
  }

  private void surround(char[][] board, int x, int z, char c) {
    for (int i = -1; i <= 1; i++) {
      for (int j = -1; j <= 1; j++) {
        if (i == 0 && j == 0) continue;
        board[x + i][z + j] = c;
      }
    }
  }

  private char[][] constructBoard(Collection<? extends VolumeTree.Volume> volumes, int xSize, int ySize) {
    char[][] board = new char[xSize][ySize];
    for (int x = 0; x < xSize; x++) {
      for (int y = 0; y < ySize; y++) {
        board[x][y] = '0';
      }
    }
    for (VolumeTree.Volume volume : volumes) {
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
    for (int x = 0; x < xSize; x++) {
      for (int z = 0; z < ySize; z++) {
        if (board[x][z] == '0') {
          board[x][z] = ' ';
        }
      }
    }
    return board;
  }

  private void printBoard(char[][] board, int xSize, int ySize) {
    for (int y = 0; y < ySize; y++) {
      for (int x = 0; x < xSize; x++) {
        System.out.print(board[x][y]);
      }
      System.out.println();
    }
  }

  private int[][][] findAnswers(Collection<? extends VolumeTree.Volume> volumes, int xSize, int ySize, int zSize) {
    int[][][] answers = new int[xSize][ySize][zSize];
    for (int z = 0; z < zSize; z++) {
      for (int y = 0; y < ySize; y++) {
        for (int x = 0; x < xSize; x++) {
          answers[x][y][z] = 0;
        }
      }
    }
    for (VolumeTree.Volume v : volumes) {
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

  private int[][][] findSolutions(VolumeTree<?, ?> tree, int xSize, int ySize, int zSize) {
    int[][][] solutions = new int[xSize][ySize][zSize];
    for (int z = 0; z < zSize; z++) {
      for (int y = 0; y < ySize; y++) {
        for (int x = 0; x < xSize; x++) {
          solutions[x][y][z] = tree.containingVolumes(x, y, z).size();
        }
      }
    }
    return solutions;
  }

  private void checkAnswers(Collection<? extends VolumeTree.Volume> volumes,
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
            System.out.println(String.format(
                    "Wrong answer at (%d, %d, %d) -- Answer: %d, Solution: %d",
                    x, y, z, answers[x][y][z], solutions[x][y][z]));
            failed++;
          } else {
            succeeded++;
          }
        }
      }
    }
    if (print) tree.print();
    System.out.printf("Successfully identified regions: %d\n", succeeded);
    System.out.printf("Failed identified regions: %d\n", failed);
    if (failed > 0) {
      throw new RuntimeException("Answers != Solutions");
    }
  }

  @Test
  public void testRandom() {
    Random random = new Random();

    Map<Integer, VolumeTree.Volume> regions = Maps.newHashMap();

    int xLocation;
    int yLocation;
    int zLocation;
    int xSize;
    int ySize;
    int zSize;
    for (Integer i = 0; i < REGION_COUNT; i++) {
      xLocation = random.nextInt(WORLD_X_WIDTH - REGION_MAX_X_WIDTH + 1);
      yLocation = random.nextInt(WORLD_Y_WIDTH - REGION_MAX_Y_WIDTH + 1);
      zLocation = random.nextInt(WORLD_Z_WIDTH - REGION_MAX_Z_WIDTH + 1);
      xSize = random.nextInt(REGION_MAX_X_WIDTH - REGION_MIN_X_WIDTH + 1) + REGION_MIN_X_WIDTH;
      ySize = random.nextInt(REGION_MAX_Y_WIDTH - REGION_MIN_Y_WIDTH + 1) + REGION_MIN_Y_WIDTH;
      zSize = random.nextInt(REGION_MAX_Z_WIDTH - REGION_MIN_Z_WIDTH + 1) + REGION_MIN_Z_WIDTH;

      regions.put(i, new TestVolume(
              xLocation, xLocation + xSize - 1,
              yLocation, yLocation + ySize - 1,
              zLocation, zLocation + zSize - 1));
    }

    // Build board
    char[][] board = constructBoard(regions.values(), WORLD_X_WIDTH, WORLD_Z_WIDTH);

    // print board
    if (DEBUG) {
      System.out.println("Entire Board:");
      printBoard(board, WORLD_X_WIDTH, WORLD_Z_WIDTH);
    }

    // Now actually testing
    VolumeTree<Integer, VolumeTree.Volume> tree = new VolumeTree<>();
    long constructionElapse = System.currentTimeMillis();
    tree.pushAll(regions);
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
      System.out.println("Print board with special regions");
      printBoard(board, WORLD_X_WIDTH, WORLD_Z_WIDTH);
    }

    long traditionalElapse = System.currentTimeMillis();
    for (int i = 0; i < TEST_POINT_COUNT; i++) {
      for (VolumeTree.Volume volume : regions.values()) {
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
      tree.containingVolumes(xpoints[i], ypoints[i], zpoints[i])
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
        System.out.println(String.format("Wrong Answer: Point %d -- Answer: %s, Solution: %s\n",
                i,
                Arrays.toString(answers.get(i).toArray()),
                Arrays.toString(solutions.get(i).toArray())));
      } else {
        succeeded++;
      }
    }
    System.out.printf("Successfully identified regions: %d\n", succeeded);
    System.out.printf("Failed identified regions: %d\n", failed);
    if (failed > 0) {
      throw new RuntimeException("See above for incorrect point calculations");
    }

    System.out.println("Statistics:");
    System.out.printf("Height: %d, Size: %d\n", tree.getHeight(), tree.getSize());
  }

  @Test
  public void test2Nesting() {
    Map<Integer, VolumeTree.Volume> map = Maps.newHashMap();
    map.put(0, new TestVolume(2, 2, 2, 2, 2, 2));
    map.put(1, new TestVolume(1, 3, 1, 3, 1, 3));

    VolumeTree<Integer, VolumeTree.Volume> tree = new VolumeTree<>();
    tree.pushAll(map);

    checkAnswers(map.values(), tree, 4, 4, 4, false);
  }

  @Test
  public void testZigurat() {
    int height = 100;
    Map<Integer, VolumeTree.Volume> map = Maps.newHashMap();
    for (int i = 0; i < height; i++) {
      map.put(i, new TestVolume(height - i, height + i, 0, 0, i, i));
    }

    VolumeTree<Integer, VolumeTree.Volume> tree = new VolumeTree<>();
    tree.pushAll(map);

    checkAnswers(map.values(), tree, height * 2, 1, height, false);
  }


  @Test
  public void testPointGrid2D() {
    testPointGrid2DHelper(2, 1, 2, 1, 0, 1, false);
    testPointGrid2DHelper(2, 1, 2, 1, 0, 0, false);
    testPointGrid2DHelper(2, 1, 1, 2, -1, 0, false);
    testPointGrid2DHelper(2, 3, 1, 1, -2, 0, false);
    testPointGrid2DHelper(2, 2, 2, 2, 0, 0, false);
  }

  public void testPointGrid2DHelper(int size,
                                    int countX, int countY, int countZ,
                                    int spacing,
                                    int borderSpacing,
                                    boolean print) {

    Map<Integer, VolumeTree.Volume> map = Maps.newHashMap();

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

    VolumeTree<Integer, VolumeTree.Volume> tree = new VolumeTree<>();
    tree.pushAll(map);

    int boardSizeX = countX * (size + spacing) - spacing + borderSpacing * 2;
    int boardSizeY = countY * (size + spacing) - spacing + borderSpacing * 2;
    int boardSizeZ = countZ * (size + spacing) - spacing + borderSpacing * 2;
    char[][] board = constructBoard(map.values(),
            boardSizeX,
            boardSizeZ);
    if (print) printBoard(board, boardSizeX, boardSizeZ);
    checkAnswers(map.values(), tree, boardSizeX, boardSizeY, boardSizeZ, false);
  }

}
