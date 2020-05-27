/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package darkhunter.engine;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Krcma
 */
public class Texture implements Serializable {

    public static int DEF_SIZE = 64;

    public int WIDTH, HEIGHT;

    public int[] pixels;

    public final boolean removeBG;

    public Texture(URL location, boolean removeBG) {
        this.removeBG = removeBG;
        try {
            BufferedImage image = ImageIO.read(location);
            this.WIDTH = image.getWidth();
            this.HEIGHT = image.getHeight();

            BufferedImage out = new BufferedImage(this.WIDTH, this.HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = (Graphics2D) out.getGraphics();
            g2.drawImage(image, 0, 0, this.WIDTH, this.HEIGHT, null);

            this.pixels = new int[this.WIDTH * this.HEIGHT];
            out.getRGB(0, 0, this.WIDTH, this.HEIGHT, this.pixels, 0, this.WIDTH);

        } catch (IOException e) {
            Logger.getLogger(Texture.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public Texture(URL location, int x, int y, int width, int height, int size) {
        this.removeBG = false;
        try {
            BufferedImage image = ImageIO.read(location);
            image = image.getSubimage(x, y, width, height);
            this.WIDTH = width * size;
            this.HEIGHT = height * size;
            BufferedImage out = new BufferedImage(this.WIDTH, this.HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = (Graphics2D) out.getGraphics();
            this.pixels = new int[this.WIDTH * this.HEIGHT];
            g2.drawImage(image, 0, 0, this.WIDTH, this.HEIGHT, null);
            out.getRGB(0, 0, this.WIDTH, this.HEIGHT, this.pixels, 0, this.WIDTH);
        } catch (IOException e) {
            Logger.getLogger(Texture.class.getName()).log(Level.SEVERE, null, e);
        }
    }

}
