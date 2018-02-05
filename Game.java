import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.*;
import javax.imageio.*;

public class Game {
    public static final int ROWS = 10;
    public static final int COLS = 10;

    private JFrame frame;
    private JButton buttons[][];

    private State initialState;
    private State currentState;

    private boolean hasWon = false;

    private HashMap<String, ImageIcon> iconMap = new HashMap<String, ImageIcon>();

    private String direction;

    public Game() {
        this.direction = "Front";
        this.initializeIconMap();
        this.loadFile();
        this.initializeUI();
        this.render();
        this.checkWin();
    }

// ceate hashmap if tile -> specific icon
    public void initializeIconMap() {
        String prefix = "PNG/";

//arraylist exists bec icons not exact to the tiles, gravel then paint over

        ArrayList<String> boxFilenames = new ArrayList<String>(
            Arrays.asList(
                prefix + "GroundGravel_Concrete.png",
                prefix + "CrateDark_Gray.png"
            )
        );

        iconMap.put(State.BOX, combineIcon(boxFilenames));

        ArrayList<String> boxStorageFilenames = new ArrayList<String>(
            Arrays.asList(
                prefix + "GroundGravel_Concrete.png",
                prefix + "CrateDark_Blue.png"
            )
        );

        iconMap.put(State.BOX_STORAGE, combineIcon(boxStorageFilenames));

        iconMap.put(State.FLOOR, new ImageIcon(prefix + "GroundGravel_Concrete.png"));

        ArrayList<String> keeperFrontFilenames = new ArrayList<String>(
            Arrays.asList(
                prefix + "GroundGravel_Concrete.png",
                prefix + "Character_Front.png"
            )
        );

        iconMap.put(State.KEEPER + "Front", combineIcon(keeperFrontFilenames));

        ArrayList<String> keeperBackFilenames = new ArrayList<String>(
            Arrays.asList(
                prefix + "GroundGravel_Concrete.png",
                prefix + "Character_Back.png"
            )
        );

        iconMap.put(State.KEEPER + "Back", combineIcon(keeperBackFilenames));

        ArrayList<String> keeperLeftFilenames = new ArrayList<String>(
            Arrays.asList(
                prefix + "GroundGravel_Concrete.png",
                prefix + "Character_Left.png"
            )
        );

        iconMap.put(State.KEEPER + "Left", combineIcon(keeperLeftFilenames));

        ArrayList<String> keeperRightFilenames = new ArrayList<String>(
            Arrays.asList(
                prefix + "GroundGravel_Concrete.png",
                prefix + "Character_Right.png"
            )
        );

        iconMap.put(State.KEEPER + "Right", combineIcon(keeperRightFilenames));

        ArrayList<String> storageFilenames = new ArrayList<String>(
            Arrays.asList(
                prefix + "GroundGravel_Concrete.png",
                prefix + "EndPoint_Blue.png"
            )
        );

        iconMap.put(State.STORAGE, combineIcon(storageFilenames));

        ArrayList<String> wallFilenames = new ArrayList<String>(
            Arrays.asList(
                prefix + "GroundGravel_Concrete.png",
                prefix + "Wall_Gray.png"
            )
        );

        iconMap.put(State.WALL, combineIcon(wallFilenames));

        iconMap.put(State.NONE, new ImageIcon(prefix + "GroundGravel_Grass.png"));

        ArrayList<String> keeperStorageFrontFilenames = new ArrayList<String>(
            Arrays.asList(
                prefix + "GroundGravel_Concrete.png",
                prefix + "EndPoint_Blue.png",
                prefix + "Character_Front.png"
            )
        );

        iconMap.put(State.KEEPER_STORAGE + "Front", combineIcon(keeperStorageFrontFilenames));

        ArrayList<String> keeperStorageBackFilenames = new ArrayList<String>(
            Arrays.asList(
                prefix + "GroundGravel_Concrete.png",
                prefix + "EndPoint_Blue.png",
                prefix + "Character_Back.png"
            )
        );

        iconMap.put(State.KEEPER_STORAGE + "Back", combineIcon(keeperStorageBackFilenames));

        ArrayList<String> keeperStorageLeftFilenames = new ArrayList<String>(
            Arrays.asList(
                prefix + "GroundGravel_Concrete.png",
                prefix + "EndPoint_Blue.png",
                prefix + "Character_Left.png"
            )
        );

        iconMap.put(State.KEEPER_STORAGE + "Left", combineIcon(keeperStorageLeftFilenames));

        ArrayList<String> keeperStorageRightFilenames = new ArrayList<String>(
            Arrays.asList(
                prefix + "GroundGravel_Concrete.png",
                prefix + "EndPoint_Blue.png",
                prefix + "Character_Right.png"
            )
        );

        iconMap.put(State.KEEPER_STORAGE + "Right", combineIcon(keeperStorageRightFilenames));
    }

    public void initializeUI() {
        frame = new JFrame("Sokoban");

        buttons = new JButton[Game.ROWS][Game.COLS];

        Container pane = frame.getContentPane();

        pane.setLayout(new GridLayout(10, 10));

        // initialize all grid buttons
        for (int i = 0; i < Game.ROWS; i++) {
            for (int j = 0; j < Game.COLS; j++) {
                JButton button = new JButton();

                button.setPreferredSize(new Dimension(64, 64)); //tile size

                pane.add(button);

                buttons[i][j] = button;
            }
        }

        frame.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent k) {
                int key = k.getKeyCode();

                if (!hasWon) {
                    if (key == KeyEvent.VK_UP) {
                        moveUp();
                    }
                    if (key == KeyEvent.VK_DOWN) {
                        moveDown();
                    }
                    if (key == KeyEvent.VK_LEFT) {
                        moveLeft();
                    }
                    if (key == KeyEvent.VK_RIGHT) {
                        moveRight();
                    }

                    // saveFile();

                    render();

                    checkWin();
                }
            }

            public void keyTyped(KeyEvent k) {}
            public void keyReleased(KeyEvent k) {}
        });

        frame.setResizable(false);
        frame.setFocusable(true);
        frame.pack();
        frame.setVisible(true);
    }

    public void render() {
        // update values of buttons from currentState
        for (int i = 0; i < Game.ROWS; i++) {
            for (int j = 0; j < Game.COLS; j++) {
                String currentValue = this.currentState.getValue(i, j);

// append direction for the direction icons
                if (currentValue.equals(State.KEEPER) || currentValue.equals(State.KEEPER_STORAGE)) {
                    currentValue += this.direction;
                }

                buttons[i][j].setLabel("");

                ImageIcon icon = iconMap.get(currentValue);

                buttons[i][j].setIcon(icon);
            }
        }
        checkActions();
        this.currentState.getPreviousAction();
    }

    public void loadFile() {
      System.out.println("called load");
        // load data from puzzle.in and save it to currentState
        String filename = "puzzle.in";

        String[][] contents = new String[Game.ROWS][Game.COLS];

        try {
            FileReader fileReader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = "";

            for (int i = 0; (line = bufferedReader.readLine()) != null; i++) {
                for (int j = 0; j < line.length(); j++) {
                    String c = Character.toString(line.charAt(j));

                    contents[i][j] = c;
                }
            }

            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + filename + "'");
        }
        catch(IOException ex) {
            System.out.println("Error reading file '" + filename + "'");
        }
        this.currentState = new State(contents);
    }

    public void saveFile() {
        // save currentState to puzzle.in
        String filename = "puzzle.in";

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

            writer.write(currentState.toString());

            writer.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + filename + "'");
        }
        catch(IOException ex) {
            System.out.println("Error writing file '" + filename + "'");
        }
    }

    public void checkActions(){
      this.currentState.checkActions();
    }
    public void moveUp() {
        this.currentState.moveUp();
        this.direction = "Back";
    }

    public void moveDown() {
        this.currentState.moveDown();
        this.direction = "Front";
    }

    public void moveLeft() {
        this.currentState.moveLeft();
        this.direction = "Left";
    }


    public void moveRight() {
        this.currentState.moveRight();
        this.direction = "Right";
    }

    public void checkWin() {
        if (currentState.isWin()) {
            JOptionPane.showMessageDialog(frame, "Win!");
            this.hasWon = true;
        }
    }

    private ImageIcon combineIcon(ArrayList<String> filenames) {
        ArrayList<File> files = new ArrayList<File>();
        ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();

        Image img = null;

        try { // load file
            for (String filename : filenames) {
                files.add(new File(filename));
            }

            for (File file : files) { //get image from each file
                BufferedImage tmp = ImageIO.read(file);

                BufferedImage newImage = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB); //make new buffered image with correct size

                Graphics g = newImage.getGraphics();
                g.drawImage( // put tmp then draw over with newImage
                    tmp,
                    tmp.getWidth() < 64 ? 32 - tmp.getWidth() / 2 : 0,
                    tmp.getHeight() < 64 ? 32 - tmp.getHeight() / 2 : 0,
                    null
                );
                g.dispose();

                images.add(newImage);// images: list of update buffered images
            }

            img = new BufferedImage( // img : combined all pic per tile
                images.get(0).getWidth(),
                images.get(0).getHeight(),
                BufferedImage.TYPE_INT_RGB
            );

            Graphics g2 = img.getGraphics();

            for (int i = 0; i < images.size(); i++) { //draw the map
                BufferedImage image = images.get(i);

                g2.drawImage(image, 0, 0, null);

            }

            g2.dispose();
        }
        catch(Exception e) {
            System.out.println("Something went wrong.");
        }

        return new ImageIcon(img);
    }
}
