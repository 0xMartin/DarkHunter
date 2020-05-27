/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package darkhunter;

import darkhunter.engine.GameMap;
import darkhunter.engine.Texture;
import darkhunter.objects.Light;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Krcma
 */
public class MapEditor extends javax.swing.JFrame implements Runnable {

    private int xOff = 0, yOff = 0;
    private GameMap map;
    private int layer = 1;  //0-floor, 1-walls, 2-ceiling

    private int selectedTextureIndex;
    private List<Texture> textures;

    /**
     * Creates new form MapEditor
     */
    public MapEditor() {
        initComponents();
    }

    private int[] pixels;
    private BufferedImage img;

    public void start() {
        //set Wimdows design
        try {
            UIManager.setLookAndFeel(
                    UIManager.getInstalledLookAndFeels()[3].getClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
        }
        //show
        this.setVisible(true);
        //maximixe
        this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        this.canvas1.createBufferStrategy(3);
        new Thread(this).start();
    }

    @Override
    public void run() {
        //pixels of screen
        while (true) {
            try {
                if (this.map != null) {
                    for (int i = 0; i < this.pixels.length; i++) {
                        this.pixels[i] = 0;
                    }
                    //render
                    if (this.textures != null) {
                        //blocks
                        for (int x = 0; x < this.map.getWidth(); x++) {
                            for (int y = 0; y < this.map.getHeight(); y++) {
                                int i = 0;
                                switch (this.layer) {
                                    case 0:
                                        i = this.map.getMapFloor()[x][y] - 1;
                                        break;
                                    case 1:
                                        i = this.map.getMap()[x][y] - 1;
                                        break;
                                    case 2:
                                        i = this.map.getMapCeiling()[x][y] - 1;
                                        break;
                                    case 3:
                                        i = this.map.getMapCeiling()[x][y] - 1;
                                        if (i >= 0) {
                                            break;
                                        }
                                        i = this.map.getMap()[x][y] - 1;
                                        if (i >= 0) {
                                            break;
                                        }
                                        i = this.map.getMapFloor()[x][y] - 1;
                                        break;
                                }
                                if (i >= 0) {
                                    drawImage(
                                            x * Texture.DEF_SIZE + this.xOff,
                                            y * Texture.DEF_SIZE + this.yOff,
                                            this.textures.get(i).pixels,
                                            Texture.DEF_SIZE, Texture.DEF_SIZE
                                    );
                                }
                            }
                        }
                        //selected
                        drawImage(
                                this.img.getWidth() - Texture.DEF_SIZE - 15,
                                this.img.getHeight() - Texture.DEF_SIZE - 15,
                                this.textures.get(this.selectedTextureIndex).pixels,
                                Texture.DEF_SIZE, Texture.DEF_SIZE
                        );
                    }
                    //show
                    BufferStrategy buffer = this.canvas1.getBufferStrategy();
                    Graphics g = buffer.getDrawGraphics();
                    g.drawImage(this.img, 0, 0, this.img.getWidth(), this.img.getHeight(), null);
                    g.setColor(Color.red);
                    //selected block outline
                    g.drawRect(this.img.getWidth() - Texture.DEF_SIZE - 15, this.img.getHeight() - Texture.DEF_SIZE - 15, Texture.DEF_SIZE, Texture.DEF_SIZE);
                    //map outline
                    g.drawRect(xOff, yOff, Texture.DEF_SIZE * this.map.getWidth(), Texture.DEF_SIZE * this.map.getHeight());
                    g.setColor(Color.yellow);
                    //player
                    if (this.map != null) {
                        if (this.map.getPlayerX_Y() != null) {
                            g.fillRect(
                                    (int) (Texture.DEF_SIZE * this.map.getPlayerX_Y()[0] + this.xOff) - 10,
                                    (int) (Texture.DEF_SIZE * this.map.getPlayerX_Y()[1] + this.yOff) - 10,
                                    20,
                                    20
                            );
                        }
                    }
                    g.drawString("Layer: " + (this.layer == 0 ? "Floor" : this.layer == 1 ? "Wall" : this.layer == 2 ? "Ceiling" : "All"), 20, 20);
                    buffer.show();
                }
                Thread.sleep(20);
            } catch (Exception ex) {
                Logger.getLogger(MapEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void drawImage(int x, int y, int[] img, int width, int height) {
        int tx = 0;
        for (int j = x; j < this.img.getWidth() && tx < width; j++) {
            int ty = 0;
            for (int i = y; i < this.img.getHeight() && ty < height; i++) {
                if (j >= 0 && i >= 0) {
                    this.pixels[j + i * this.img.getWidth()] = img[tx + ty * width];
                }
                ty++;
            }
            tx++;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuItemAddLight = new javax.swing.JMenuItem();
        jMenuItemPlacePlayer = new javax.swing.JMenuItem();
        canvas1 = new java.awt.Canvas();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem14 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem13 = new javax.swing.JMenuItem();

        jMenuItemAddLight.setText("Add light");
        jMenuItemAddLight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAddLightActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItemAddLight);

        jMenuItemPlacePlayer.setText("Place player");
        jMenuItemPlacePlayer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPlacePlayerActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItemPlacePlayer);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        canvas1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                canvas1MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                canvas1MouseReleased(evt);
            }
        });
        canvas1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                canvas1ComponentResized(evt);
            }
        });
        canvas1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                canvas1MouseDragged(evt);
            }
        });
        canvas1.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                canvas1MouseWheelMoved(evt);
            }
        });

        jMenu1.setText("File");

        jMenuItem1.setText("New map");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText("Open map");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem3.setText("Save map");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");

        jMenuItem4.setText("Fill");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem4);

        jMenuItem5.setText("Clear");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem5);

        jMenuItem6.setText("Delete all objects");
        jMenu2.add(jMenuItem6);

        jMenuItem7.setText("Set player position");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem7);

        jMenu5.setText("Background");

        jMenuItem8.setText("Set");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem8);

        jMenuItem14.setText("Clear");
        jMenuItem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem14ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem14);

        jMenu2.add(jMenu5);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Textures");

        jMenuItem9.setText("Add texture");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem9);

        jMenuItem10.setText("Remove texture");
        jMenu3.add(jMenuItem10);

        jMenuItem11.setText("Add sprite texture");
        jMenu3.add(jMenuItem11);

        jMenuItem12.setText("Remove sprite texture");
        jMenu3.add(jMenuItem12);

        jMenuBar1.add(jMenu3);

        jMenu4.setText("Objects");

        jMenuItem13.setText("Add light");
        jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem13ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem13);

        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(canvas1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(canvas1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        try {
            JTextField name = new JTextField("map_1");
            JTextField width = new JTextField("50");
            JTextField height = new JTextField("50");
            Object[] message = {
                "Name:", name,
                "Width:", width,
                "Height:", height
            };

            int option = JOptionPane.showConfirmDialog(null, message, "New map", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                this.map = new GameMap();
                int w = Integer.parseInt(width.getText());
                int h = Integer.parseInt(height.getText());
                this.map.setMap(
                        new int[w][h],
                        new int[w][h],
                        new int[w][h],
                        w, h
                );
            }
        } catch (Exception ex) {
            Logger.getLogger(MapEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.toString().endsWith(".map") || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Map";
            }
        });
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                this.map = Tools.loadMap(fileChooser.getSelectedFile());
                this.textures = this.map.getTextures();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(MapEditor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(MapEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private int btn = 0, btnl = 1;
    private Point mouse;
    private void canvas1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_canvas1MousePressed
        this.btn = evt.getButton();
        this.mouse = evt.getPoint();
        if (this.btn == MouseEvent.BUTTON1) {
            if (evt.isControlDown()) {
                placeBlock(evt.getPoint().x, evt.getPoint().y, 0);
            } else {
                placeBlock(evt.getPoint().x, evt.getPoint().y, this.selectedTextureIndex + 1);
            }
        } else if (this.btn == MouseEvent.BUTTON3) {
            if (this.btnl == this.btn) {
                this.jPopupMenu1.show(this.canvas1, evt.getX(), evt.getY());
            }
        }
        this.btnl = btn;
    }//GEN-LAST:event_canvas1MousePressed

    private void canvas1ComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_canvas1ComponentResized
        try {
            this.img = new BufferedImage(this.canvas1.getWidth(), this.canvas1.getHeight(), BufferedImage.TYPE_INT_RGB);
            this.pixels = ((DataBufferInt) this.img.getRaster().getDataBuffer()).getData();
        } catch (Exception ex) {
        }
    }//GEN-LAST:event_canvas1ComponentResized

    private Point mD;
    private void canvas1MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_canvas1MouseDragged
        if (this.btn == MouseEvent.BUTTON3) {
            if (this.mD == null) {
                this.mD = evt.getPoint();
            }
            this.xOff += evt.getX() - mD.x;
            this.yOff += evt.getY() - mD.y;
            this.mD = evt.getPoint();
        } else if (this.btn == MouseEvent.BUTTON1) {
            if (evt.isControlDown()) {
                placeBlock(evt.getPoint().x, evt.getPoint().y, 0);
            } else {
                placeBlock(evt.getPoint().x, evt.getPoint().y, this.selectedTextureIndex + 1);
            }
        }
    }//GEN-LAST:event_canvas1MouseDragged

    private void canvas1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_canvas1MouseReleased
        this.mD = null;
    }//GEN-LAST:event_canvas1MouseReleased

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        for (int x = 0; x < this.map.getWidth(); x++) {
            for (int y = 0; y < this.map.getHeight(); y++) {
                this.map.getMap()[x][y] = 0;
            }
        }
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.toString().endsWith(".png") || f.toString().endsWith(".jpg") || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Image";
            }
        });
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                int option = JOptionPane.showConfirmDialog(null, "Do you want to remove background ?", "Texture background", JOptionPane.YES_NO_CANCEL_OPTION);
                Texture t = new Texture(fileChooser.getSelectedFile().toURL(), option == JOptionPane.YES_OPTION);
                this.map.addTexture(t);
                this.textures = this.map.getTextures();
                this.selectedTextureIndex = 0;
            } catch (MalformedURLException ex) {
                Logger.getLogger(MapEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void canvas1MouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_canvas1MouseWheelMoved
        if (evt.isControlDown()) {
            if (evt.getWheelRotation() > 0) {
                this.layer++;
                if (this.layer > 3) {
                    this.layer = 3;
                }
            } else {
                this.layer--;
                if (this.layer < 0) {
                    this.layer = 0;
                }
            }
        } else {
            if (evt.getWheelRotation() > 0) {
                this.selectedTextureIndex++;
                if (this.selectedTextureIndex >= this.textures.size()) {
                    this.selectedTextureIndex = 0;
                }
            } else {
                this.selectedTextureIndex--;
                if (this.selectedTextureIndex < 0) {
                    this.selectedTextureIndex = this.textures.size() - 1;
                }
            }
        }
    }//GEN-LAST:event_canvas1MouseWheelMoved

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        for (int x = 0; x < this.map.getWidth(); x++) {
            for (int y = 0; y < this.map.getHeight(); y++) {
                switch (this.layer) {
                    case 0:
                        this.map.getMapFloor()[x][y] = this.selectedTextureIndex + 1;
                        break;
                    case 1:
                        this.map.getMap()[x][y] = this.selectedTextureIndex + 1;
                        break;
                    case 2:
                        this.map.getMapCeiling()[x][y] = this.selectedTextureIndex + 1;
                        break;
                }
            }
        }
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                if (fileChooser.getSelectedFile().toString().endsWith(".map")) {
                    Tools.saveMap(fileChooser.getSelectedFile(), this.map);
                } else {
                    Tools.saveMap(new File(fileChooser.getSelectedFile() + ".map"), this.map);
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(MapEditor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MapEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        placePlayer();
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem13ActionPerformed
        addLight();
    }//GEN-LAST:event_jMenuItem13ActionPerformed

    private void jMenuItemAddLightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAddLightActionPerformed
        addLight();
    }//GEN-LAST:event_jMenuItemAddLightActionPerformed

    private void jMenuItemPlacePlayerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPlacePlayerActionPerformed
        placePlayer();
    }//GEN-LAST:event_jMenuItemPlacePlayerActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.toString().endsWith(".png") || f.toString().endsWith(".jpg") || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Image";
            }
        });
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                this.map.setBackGround(fileChooser.getSelectedFile().toURL());
            } catch (MalformedURLException ex) {
                Logger.getLogger(MapEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jMenuItem14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem14ActionPerformed
        this.map.setBackGround(null);
    }//GEN-LAST:event_jMenuItem14ActionPerformed

    private void placeBlock(int x, int y, int block_index) {
        x = (x - this.xOff) / Texture.DEF_SIZE;
        y = (y - this.yOff) / Texture.DEF_SIZE;
        if (x < 0 || y < 0 || x >= this.map.getWidth() || y >= this.map.getHeight()) {
            return;
        }
        switch (this.layer) {
            case 0:
                this.map.getMapFloor()[x][y] = block_index;
                break;
            case 1:
                this.map.getMap()[x][y] = block_index;
                break;
            case 2:
                this.map.getMapCeiling()[x][y] = block_index;
                break;
            case 3:
                this.map.getMapFloor()[x][y] = block_index;
                this.map.getMap()[x][y] = block_index;
                this.map.getMapCeiling()[x][y] = block_index;
                break;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Canvas canvas1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JMenuItem jMenuItemAddLight;
    private javax.swing.JMenuItem jMenuItemPlacePlayer;
    private javax.swing.JPopupMenu jPopupMenu1;
    // End of variables declaration//GEN-END:variables

    private void addLight() {
        try {
            Point p = getXYMap(this.mouse);
            JTextField xt = new JTextField(p.x + "");
            JTextField yt = new JTextField(p.y + "");
            JTextField intensity = new JTextField("6");
            Object[] message = {
                "X:", xt,
                "Y:", yt,
                "Intensity:", intensity
            };

            int option = JOptionPane.showConfirmDialog(null, message, "Add light", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                this.map.getObjects().add(
                        new Light(
                                Integer.parseInt(xt.getText()),
                                Integer.parseInt(yt.getText()),
                                Integer.parseInt(intensity.getText())
                        )
                );
            }
        } catch (Exception ex) {
            Logger.getLogger(MapEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void placePlayer() {
        try {
            Point p = getXYMap(this.mouse);
            JTextField xt = new JTextField(p.x + "");
            JTextField yt = new JTextField(p.y + "");
            Object[] message = {
                "X:", xt,
                "Y:", yt
            };

            int option = JOptionPane.showConfirmDialog(null, message, "New map", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                this.map.setPlayerX_Y(new double[]{Integer.parseInt(xt.getText()), Integer.parseInt(yt.getText())});
            }
        } catch (Exception ex) {
            Logger.getLogger(MapEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Point getXYMap(Point p) {
        int x = (p.x - this.xOff) / Texture.DEF_SIZE;
        int y = (p.y - this.yOff) / Texture.DEF_SIZE;
        x = x < 0 ? 0 : x;
        y = y < 0 ? 0 : y;
        x = (x >= this.map.getWidth()) ? this.map.getWidth() - 1 : x;
        y = (y >= this.map.getHeight()) ? this.map.getHeight() - 1 : y;
        return new Point(x, y);
    }

}
