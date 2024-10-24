package backend.academy.maze.factory.unPerfect;

import backend.academy.maze.Cell;
import backend.academy.maze.Coordinate;
import backend.academy.maze.Edge;
import backend.academy.maze.Maze;
import backend.academy.maze.factory.Distribution;
import backend.academy.maze.factory.MazeGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PrimCicleMazeGenerator implements MazeGenerator {
        private final Random random = new Random();
        private static final int HUNDRED = 100;
        private static final int CICLE_P = 10;
        private Distribution distribution = new Distribution();

        @Override
        public Maze generateMaze(int height, int width) {
            if (height % 2 == 0 || width % 2 == 0) {
                throw new IllegalArgumentException("Height and width must be odd numbers to create a proper maze.");
            }

            Cell[][] grid = new Cell[height][width];

            // Инициализация всех клеток как стен
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    grid[row][col] = new Cell(row, col, Cell.Type.WALL, 0);
                }
            }

            // Выбираем случайную начальную клетку
            int startRow = random.nextInt(height / 2) * 2 + 1;
            int startCol = random.nextInt(width / 2) * 2 + 1;
            grid[startRow][startCol] = new Cell(startRow, startCol, Cell.Type.PASSAGE, distribution.generateWeight());

            // Список возможных ребер (фронт)
            List<Edge> frontiers = new ArrayList<>();
            addFrontierCells(startRow, startCol, height, width, frontiers);

            // Алгоритм Прима с добавлением циклов
            while (!frontiers.isEmpty()) {
                // Выбираем случайное ребро из фронта
                Edge edge = frontiers.remove(random.nextInt(frontiers.size()));
                Coordinate from = edge.getFrom();
                Coordinate to = edge.getTo();

                // Если целевая клетка непосещена, соединяем ее с текущей
                if (grid[to.row()][to.col()].type() == Cell.Type.WALL) {
                    // Удаляем стену между клетками
                    int wallRow = (from.row() + to.row()) / 2;
                    int wallCol = (from.col() + to.col()) / 2;
                    grid[wallRow][wallCol] = new Cell(wallRow, wallCol,
                        Cell.Type.PASSAGE, distribution.generateWeight());

                    // Целевая клетка становится проходом
                    grid[to.row()][to.col()] = new Cell(to.row(), to.col(),
                        Cell.Type.PASSAGE, distribution.generateWeight());

                    // Добавляем новые соседние клетки к фронту
                    addFrontierCells(to.row(), to.col(), height, width, frontiers);
                }

                // Добавляем случайные циклы с вероятностью 10%
                if (random.nextInt(HUNDRED) < CICLE_P) { // Вероятность цикла 10%
                    addRandomCycle(height, width, grid);
                }
            }

            return new Maze(height, width, grid);
        }

        // Добавление случайного цикла в лабиринт
        private void addRandomCycle(int height, int width, Cell[][] grid) {
            while (true) {
                // Выбираем случайную точку лабиринта
                int row = random.nextInt(height / 2) * 2 + 1;
                int col = random.nextInt(width / 2) * 2 + 1;

                // Проверяем соседние клетки, если есть возможность создать цикл
                List<Coordinate> neighbors = new ArrayList<>();
                if (row - 2 > 0 && grid[row - 2][col].type() == Cell.Type.PASSAGE) {
                    neighbors.add(new Coordinate(row - 2, col));
                }
                if (row + 2 < height && grid[row + 2][col].type() == Cell.Type.PASSAGE) {
                    neighbors.add(new Coordinate(row + 2, col));
                }
                if (col - 2 > 0 && grid[row][col - 2].type() == Cell.Type.PASSAGE) {
                    neighbors.add(new Coordinate(row, col - 2));
                }
                if (col + 2 < width && grid[row][col + 2].type() == Cell.Type.PASSAGE) {
                    neighbors.add(new Coordinate(row, col + 2));
                }

                if (!neighbors.isEmpty()) {
                    // Выбираем случайного соседа и соединяем
                    Coordinate neighbor = neighbors.get(random.nextInt(neighbors.size()));
                    int wallRow = (row + neighbor.row()) / 2;
                    int wallCol = (col + neighbor.col()) / 2;
                    grid[wallRow][wallCol] = new Cell(wallRow, wallCol,
                        Cell.Type.PASSAGE, distribution.generateWeight());
                    break; // Завершаем цикл, как только добавили новый путь
                }
            }
        }

        // Добавление непосещенных соседних клеток в фронт
        private void addFrontierCells(int row, int col, int height, int width, List<Edge> frontiers) {
            if (col - 2 >= 0) {
                frontiers.add(new Edge(new Coordinate(row, col),
                    new Coordinate(row, col - 2), distribution.generateWeight()));
            }
            if (col + 2 < width) {
                frontiers.add(new Edge(new Coordinate(row, col),
                    new Coordinate(row, col + 2), distribution.generateWeight()));
            }
            if (row - 2 >= 0) {
                frontiers.add(new Edge(new Coordinate(row, col),
                    new Coordinate(row - 2, col), distribution.generateWeight()));
            }
            if (row + 2 < height) {
                frontiers.add(new Edge(new Coordinate(row, col),
                    new Coordinate(row + 2, col), distribution.generateWeight()));
            }
        }
    }

