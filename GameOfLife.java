import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GameOfLife extends JFrame { 
    JPanel panel = new JPanel();
    JButton refresh = new JButton("Next Generation");
    public int gameHeight = 900;
    public int gameWidth = 900;

    int[][] matrix = new int[gameHeight/10][gameWidth/10];

    public GameOfLife() {
        super("Game of Life");
        add(panel);
        setupWindow();
    }

    public static void main (String[] args) {
        GameOfLife gameOfLife = new GameOfLife();
        gameOfLife.setVisible(true);
    }

    private void setupWindow() {
        setSize(gameWidth+1, gameHeight+31);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(refresh);
        refresh.setAlignmentY(Component.BOTTOM_ALIGNMENT);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
    }

    public void setGlider(int offset) {
        matrix[22+offset][20+offset] = 1;
        matrix[23+offset][21+offset] = 1;
        matrix[21+offset][22+offset] = 1;
        matrix[22+offset][22+offset] = 1;
        matrix[23+offset][22+offset] = 1;
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

    public void alive(int[][] tempMatrix, int x, int y) {
        if ((x > 3 && y > 3) && (x < gameWidth/10-3 && y < gameHeight/10-3)) {
            if (tempMatrix[x][y] != 1) {
                int neighbors = 0;

                if (tempMatrix[x-1][y-1] == 1) { //row above cell
                    neighbors ++;
                } if (tempMatrix[x][y-1] == 1) {
                    neighbors ++;
                } if (tempMatrix[x+1][y-1] == 1) {
                    neighbors ++;
                }
    
                if (tempMatrix[x-1][y] == 1) { //cell's current row
                    neighbors ++;
                }  if (tempMatrix[x+1][y] == 1) {
                    neighbors ++; 
                }
    
                if (tempMatrix[x-1][y+1] == 1) { //row below cell
                    neighbors ++;
                } if (tempMatrix[x][y+1] == 1) {
                    neighbors ++;
                } if (tempMatrix[x+1][y+1] == 1) {
                    neighbors ++;
                }
    
                if (neighbors == 3) {
                    matrix[x][y] = 1;
                }
            }
        }
    }

    public void dead(int[][] tempMatrix, int x, int y) {
        if ((x > 3 && y > 3) && (x < gameWidth/10-3 && y < gameHeight/10-3)) {
            if (tempMatrix[x][y] == 1) {
                int neighbors = 0;

                if (tempMatrix[x-1][y-1] == 1) { //row above cell
                    neighbors ++;
                } if (tempMatrix[x][y-1] == 1) {
                    neighbors ++;
                } if (tempMatrix[x+1][y-1] == 1) {
                    neighbors ++;
                }
    
                if (tempMatrix[x-1][y] == 1) { //cell's current row
                    neighbors ++;
                }  if (tempMatrix[x+1][y] == 1) {
                    neighbors ++; 
                }
    
                if (tempMatrix[x-1][y+1] == 1) { //row below cell
                    neighbors ++;
                } if (tempMatrix[x][y+1] == 1) {
                    neighbors ++;
                } if (tempMatrix[x+1][y+1] == 1) {
                    neighbors ++;
                }
    
                if (neighbors > 3 || neighbors < 2) {
                    matrix[x][y] = 0;
                }
            }
        }
    }

    public void runState(Graphics g) {
        int[][] tempMatrix = new int[gameWidth/10][gameHeight/10];
        for(int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                tempMatrix[i][j] = matrix[i][j];
            }
        }
        for (int i = 0; i < tempMatrix.length; i++) {
            for (int j = 0; j < tempMatrix[i].length; j++) {
                dead(tempMatrix, j, i);
                alive(tempMatrix, j, i);
            }
        }
        smartCellRedraw(g, matrix, tempMatrix);
    }

    public void generation(Graphics g, int generations) {
        Thread thread = new Thread();
        try {
            for (int x = 0; x < generations; x++) {
                thread.sleep(1000);
                runState(g);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void clearGrid(Graphics g) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                g.clearRect(j, i, 10, 10);
            }
        }
    }

    public void drawGrid(Graphics g) {
        clearGrid(g);
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                g.drawRect(j*10, i*10, 10, 10);
                if (matrix[i][j] == 1) {
                    g.fillRect(j*10, i*10, 10, 10);
                }
            }
        }
    }

    public void smartCellRedraw(Graphics g, int[][] oldMatrix, int[][] newMatrix) {
        for (int i = 0; i < oldMatrix.length; i++) {
            for (int j = 0; j < oldMatrix[i].length; j++) {
                if (oldMatrix[i][j] != newMatrix[i][j]) {
                    if (newMatrix[i][j] == 1) {
                        g.clearRect(j*10, i*10, 10, 10);
                        g.drawRect(j*10, i*10, 10, 10);
                    } else if (oldMatrix[i][j] == 1) {
                        g.fillRect(j*10, i*10, 10, 10);
                    }
                }
            }
        } 
    }
    
    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setClip(0, 30, gameWidth+1, gameHeight+31);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        
        setGlider(0);
        setGlider(4);
        setGlider(8);
        setGlider(12);
        setGlider(12);
        setBeacon();
        drawGrid(g2); 
        generation(g2, 500);
        refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Refreshing");
                generation(g, 10);
            }
        });
    }
}