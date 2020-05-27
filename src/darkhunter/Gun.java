/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package darkhunter;

import darkhunter.engine.Engine;
import darkhunter.engine.Texture;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Krcma
 */
public class Gun {

    private final int scale = 2;
    private final int SIZE = 128;

    private final List<int[]> images;
    private float index = 0;

    private int XPOS, YPOS;

    private int ammo = 2;
    private boolean shoot = false;
    private int reload = 0;

    public Gun() {
        this.XPOS = 0;
        this.YPOS = 0;
        this.images = new ArrayList<>();
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 5; i++) {
                this.images.add(
                        (new Texture(this.getClass().getResource("/darkhunter/src/img/gun.png"), i * this.SIZE, j * this.SIZE, this.SIZE, this.SIZE, this.scale)).pixels
                );
            }
        }
    }

    public void setX(int x) {
        this.XPOS = x;
    }

    public void setY(int y) {
        this.YPOS = y;
    }

    public void refresh(double oscilator) {
        //jogging
        if (oscilator == -100d) {
            if (this.XPOS > 1) {
                this.XPOS -= 2;
            } else if (this.XPOS < -1) {
                this.XPOS += 2;
            }
            if (this.YPOS > 1) {
                this.YPOS -= 2;
            } else if (this.YPOS < -1) {
                this.YPOS += 2;
            }
        } else {
            this.XPOS = (int) (Math.sin(oscilator / 2) * 100);
            this.YPOS = -(int) (Math.abs(Math.sin(oscilator / 3) * 50)) + 50;
        }
        //animation
        if (this.reload > 0) {
            if (this.reload == 2) {
                this.index -= 0.15f;
                if ((int) this.index == 6f) {
                    this.reload = 1;
                }
            } else {
                this.index += 0.15f;
                if ((int) this.index == 15f) {
                    this.reload = -20;
                    this.index = 0;
                    this.ammo = 2;
                }
            }
        } else if (this.shoot) {
            this.index += 0.5f;
            if (this.index > 4) {
                this.ammo--;
                this.index = 0f;
                this.shoot = false;
                //reload ?
                if (this.ammo == 0) {
                    this.reload = 2;
                    this.index = 14f;
                    DarkHunter.engine.playSound("reload1", Engine.FX_volume);
                }
            }
        }
        //light
        if (this.intensity > 0) {
            this.intensity--;
        } else {
            this.intensity = 0d;
        }
        if (this.reload < 0) {
            this.reload++;
        }
    }

    public void shoot() {
        if (!this.shoot && this.reload == 0) {
            this.shoot = true;
            this.intensity = 9f;
            DarkHunter.engine.playSound("shoot1", Engine.FX_volume);
        }
    }

    public void render(int[] pixels, int width, int height) {
        int c = this.images.get((int) this.index)[0];
        int xoff = (int) (width * 0.7 - this.SIZE * this.scale / 2);
        int yoff = height - this.SIZE * this.scale;
        for (int x = 0; x < this.SIZE * this.scale; x++) {
            for (int y = 0; y < this.SIZE * this.scale; y++) {
                int i = this.XPOS + xoff + x + (this.YPOS + yoff + y) * width;
                if (i >= 0 && i < pixels.length) {
                    int pixel = this.images.get((int) this.index)[x + y * this.SIZE * this.scale];
                    if (c != pixel) {
                        pixels[i] = pixel;
                    }

                }
            }
        }
    }

    private double intensity = 0d;

    public double getLightIntensity() {
        return this.intensity;
    }

}
