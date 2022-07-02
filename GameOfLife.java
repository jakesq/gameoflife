import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class GameOfLife extends JFrame {
    int gens = 0;
    long speed = 5;

    static JPanel panel = new JPanel();
    JButton nextButton = new JButton("Next Generation");
    JButton toggleGridButton = new JButton("Toggle Grid");
//    JSlider speedSlider = new JSlider();
    public int gameHeight = 400;
    public int gameWidth = 400;
    boolean showGrid = true;
    int[][] matrix;
    int[][] tempMatrix = new int[gameWidth / 10][gameHeight / 10];

    int widthOffset = 1;
    int heightOffset = 31;

    private Graphics2D g;

    //sets pointer position
    int[][] intMatrix = {{-1,-1}, {0,-1}, {1,-1},
                         {-1,0},          {1,0}, //(1,1) isn't present as the current position shouldn't be evaluated
                         {-1,1},  {0,1},  {1,1}};

    public GameOfLife() {
        super("Game of Life");
        matrix = new int[gameHeight / 10][gameWidth / 10];
        setupWindow();
        add(panel);
        setResizable(false); //resizing causes the app to freeze
    }

    public static void main(String[] args) {
        GameOfLife gameOfLife = new GameOfLife();
        gameOfLife.setVisible(true);
    }

    private void setupWindow() {
        setSize(gameWidth + widthOffset, gameHeight + heightOffset);
        panel.setLayout(new BoxLayout(panel, 0));
        panel.add(nextButton);
        panel.add(toggleGridButton);
//        panel.add(speedSlider);
        nextButton.setAlignmentY(1.0F);
        toggleGridButton.setAlignmentY(1.0F);
//        speedSlider.setAlignmentY(1.0F);
//        speedSlider.setValue(Integer.valueOf((int) speed*5));

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                System.out.println(e.getPoint());
                Point point = e.getPoint();

                if (matrix[(point.y+heightOffset)/10][(point.x+widthOffset)/10] != 1)
                    matrix[(point.y+heightOffset)/10][(point.x+widthOffset)/10] = 1;
                else
                    matrix[(point.y+heightOffset)/10][(point.x+widthOffset)/10] = 0;

                repaint();
            }
        });

        nextButton.addActionListener(e -> {
            generation(g, 1, speed, showGrid);
            repaint();
        });

        toggleGridButton.addActionListener(e -> {
            if (showGrid)
                showGrid = false;
            else
                showGrid = true;

            clearGrid(g);
            repaint();
        });

//        speedSlider.addChangeListener(e -> {
//            speed = speedSlider.getValue();
//        });
    }

    public void setGlider(int offset) {
        matrix[22 + offset][20 + offset] = 1;
        matrix[23 + offset][21 + offset] = 1;
        matrix[21 + offset][22 + offset] = 1;
        matrix[22 + offset][22 + offset] = 1;
        matrix[23 + offset][22 + offset] = 1;
    }

    public void setBeacon() {
        matrix[10][10] = 1;
        matrix[11][10] = 1;
        matrix[10][11] = 1;
        matrix[11][11] = 1;
        matrix[12][12] = 1;
        matrix[13][12] = 1;
        matrix[12][13] = 1;
        matrix[13][13] = 1;
    }

    public int birth(int x, int y) {
        int neighbors = 0;

        for (int i = 0; i < intMatrix.length; i++) {
            try {
                if (tempMatrix[x+intMatrix[i][0]][y+intMatrix[i][1]] == 1) {
                    neighbors++;
                }
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }

        if (neighbors == 3) {
            matrix[x][y] = 1;
        }

        return neighbors;
    }

    public int kill(int x, int y) {
        int neighbors = 0;

        for (int i = 0; i < intMatrix.length; i++) {
            try {
                if (tempMatrix[x+intMatrix[i][0]][y+intMatrix[i][1]] == 1) {
                    neighbors++;
                }
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }

        if (neighbors > 3 || neighbors < 2) {
            matrix[x][y] = 0;
        }

        return neighbors;
    }

    public void runState(Graphics g, boolean showGrid) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                tempMatrix[i][j] = matrix[i][j];
            }
        }

        for (int i = 0; i < tempMatrix.length; i++) {
            for (int j = 0; j < tempMatrix[i].length; j++) {
                if (tempMatrix[j][i] == 1) {
                    kill(j, i);
                } else {
                    birth(j, i);
                }
            }
        }

        smartCellRedraw(g, matrix, tempMatrix, showGrid);
    }

    public void generation(Graphics g, int generations, long speed, boolean showGrid) {
        gens = generations;
        for (int x = 0; x < generations; x++) {// try/catch necessary for thread sleep
            gens--;
            System.out.println(gens);
            try {
                runState(g, showGrid);
                Thread.sleep(speed * 10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void clearGrid(Graphics g) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                g.clearRect(j * 10, i * 10, 10, 10);
            }
        }
    }

    public void drawGrid(Graphics g, boolean showGrid) {
        clearGrid(g);

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] == 1) {
                    g.fillRect(j * 10, i * 10, 10, 10);
                }

                if (showGrid) {
                    g.drawRect(j * 10, i * 10, 10, 10);
                }
            }
        }
    }


    public void smartCellRedraw(Graphics g, int[][] oldMatrix, int[][] newMatrix, boolean showGrid) {
        for (int i = 0; i < oldMatrix.length; i++) {
            for (int j = 0; j < oldMatrix[i].length; j++) {
                if (oldMatrix[i][j] != newMatrix[i][j]) {
                    if (newMatrix[i][j] == 1) {
                        g.clearRect(j * 10, i * 10, 10, 10);
                        if (showGrid) {
                            g.drawRect(j * 10, i * 10, 10, 10);
                        }
                    } else if (oldMatrix[i][j] == 1) {
                        g.fillRect(j * 10, i * 10, 10, 10);
                    }
                }
            }
        }
    }


    @Override
    public void paint(Graphics graphics) {
        if (g == null) {
            g = (Graphics2D) graphics;
            g.setClip(0, 30, gameWidth + 1, gameHeight + 31);
            setGlider(0);
            setGlider(4);
            setGlider(8);
            setGlider(12);
            setBeacon();
            drawGrid(g, showGrid);
        } else {
            g = (Graphics2D) graphics;
            drawGrid(g, showGrid);
//            g.dispose(); //cell artifacts remain if not disposed
        }
    }
}