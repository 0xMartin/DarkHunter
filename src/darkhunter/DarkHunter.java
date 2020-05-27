/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package darkhunter;

import darkhunter.engine.Engine;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author Krcma
 */
public class DarkHunter extends JFrame {

    public static Engine engine;

    /**
     * DarkHunter jframe
     *
     */
    public DarkHunter() {
        super("Dark hunter");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setUndecorated(true);
    }

    /**
     * Init textures, sounds, engine ...
     *
     * @param width Width of Render
     * @param height Height of Render
     * @param scale Scale for graphics resize
     * @throws java.awt.AWTException
     */
    public void init(int width, int height, float scale) throws AWTException {
        System.out.println(width + "," + height);
        this.setVisible(true);
        DarkHunter.engine = new Engine(this, 60, 60, width, height, scale);
        DarkHunter.engine.init();
        //load map
        try {
            DarkHunter.engine.setMap(Tools.loadMap(new File("map1.map")));
        } catch (IOException ex) {
            Logger.getLogger(DarkHunter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DarkHunter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Run game
     */
    public void run() {
        DarkHunter.engine.run();
    }

    /**
     * @param args the command line arguments
     * @throws java.awt.AWTException
     */
    public static void main(String[] args) throws AWTException {
        if (args.length != 0) {
            if (args[0].equals("map")) {
                //map editor
                (new MapEditor()).start();
                return;
            }
        }
        //game
        final int width = 640;
        Dimension screen_size = Toolkit.getDefaultToolkit().getScreenSize();
        DarkHunter darkHunter = new DarkHunter();
        float scale = (float) screen_size.width / (float) (float) screen_size.height;
        //preferend width of render if 640 and height is calculat
        darkHunter.init(width, (int) (1f / scale * (float) width), (float) screen_size.width / (float) width);
        darkHunter.run();
    }

}
