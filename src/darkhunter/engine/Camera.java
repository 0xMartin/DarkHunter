/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package darkhunter.engine;

import darkhunter.Gun;
import java.awt.Point;
import java.awt.event.KeyEvent;

/**
 *
 * @author Krcma
 */
public class Camera implements EngineObject {

    private int life = 100;
    private final Gun gun = new Gun();

    public double getLightIntensity() {
        return 1.5d + this.gun.getLightIntensity();
    }

    private int YOffSet = 0;
    public double xPos, yPos, xDir, yDir, xPlane, yPlane;
    private double xDir_R, yDir_R, xDir_L, yDir_L;
    private boolean left = false, right = false, forward = false, backward = false;
    private final double MOVE_SPEED = 0.07f;
    private final double ROTATION_SPEED = 0.055f;

    public Camera(double x, double y, double xd, double yd, double xp, double yp) {
        this.xPos = x;
        this.yPos = y;
        this.xDir = xd;
        this.yDir = yd;
        this.xPlane = xp;
        this.yPlane = yp;
        //Only for left and right moving
        xDir_L = xDir * Math.cos(-Math.PI) - yDir * Math.sin(-Math.PI);
        yDir_L = xDir * Math.sin(-Math.PI) + yDir * Math.cos(-Math.PI);
        xDir_R = xDir * Math.cos(Math.PI) - yDir * Math.sin(Math.PI);
        yDir_R = xDir * Math.sin(Math.PI) + yDir * Math.cos(Math.PI);
    }

    /**
     * Only for forward, backward, left, right
     *
     * @param key
     */
    public void keyPressed(KeyEvent key) {
        if ((key.getKeyCode() == KeyEvent.VK_A)) {
            this.left = true;
        }
        if ((key.getKeyCode() == KeyEvent.VK_D)) {
            this.right = true;
        }
        if ((key.getKeyCode() == KeyEvent.VK_W)) {
            this.forward = true;
        }
        if ((key.getKeyCode() == KeyEvent.VK_S)) {
            this.backward = true;
        }
    }

    public void keyReleased(KeyEvent key) {
        if ((key.getKeyCode() == KeyEvent.VK_A)) {
            this.left = false;
        }
        if ((key.getKeyCode() == KeyEvent.VK_D)) {
            this.right = false;
        }
        if ((key.getKeyCode() == KeyEvent.VK_W)) {
            this.forward = false;
        }
        if ((key.getKeyCode() == KeyEvent.VK_S)) {
            this.backward = false;
        }
    }

    public void shoot() {
        this.gun.shoot();
    }

    public void rotateCamera(double speed) {
        speed = speed * ROTATION_SPEED;
        //for front view (default view)
        double oldxDir = xDir;
        xDir = xDir * Math.cos(speed) - yDir * Math.sin(speed);
        yDir = oldxDir * Math.sin(speed) + yDir * Math.cos(speed);
        double oldxPlane = xPlane;
        xPlane = xPlane * Math.cos(speed) - yPlane * Math.sin(speed);
        yPlane = oldxPlane * Math.sin(speed) + yPlane * Math.cos(speed);
        //Only for left and right moving
        oldxDir = xDir_L;
        xDir_L = xDir_L * Math.cos(-speed) - yDir_L * Math.sin(-speed);
        yDir_L = oldxDir * Math.sin(-speed) + yDir_L * Math.cos(-speed);
        oldxDir = xDir_R;
        xDir_R = xDir_R * Math.cos(-speed) - yDir_R * Math.sin(-speed);
        yDir_R = oldxDir * Math.sin(-speed) + yDir_R * Math.cos(-speed);
    }

    @Override
    public void refresh(GameMap gm, double oscilator) {
        //moving
        int[][] map = gm.getMap();
        double diference;
        if (this.forward) {
            diference = xDir * MOVE_SPEED;
            if (xPos + diference >= 0 && xPos + diference < gm.getWidth()) {
                if (map[(int) (xPos + diference)][(int) yPos] == 0) {
                    xPos += diference;
                }
            }
            diference = yDir * MOVE_SPEED;
            if (yPos + diference >= 0 && yPos + diference < gm.getHeight()) {
                if (map[(int) xPos][(int) (yPos + diference)] == 0) {
                    yPos += diference;
                }
            }
        } else if (this.backward) {
            diference = xDir * MOVE_SPEED * 0.6d;
            if (xPos - diference >= 0 && xPos - diference < gm.getWidth()) {
                if (map[(int) (xPos - diference)][(int) yPos] == 0) {
                    xPos -= diference;
                }
            }
            diference = yDir * MOVE_SPEED * 0.6d;
            if (yPos - diference >= 0 && yPos - diference < gm.getHeight()) {
                if (map[(int) xPos][(int) (yPos - diference)] == 0) {
                    yPos -= diference;
                }
            }
        }
        if (this.right) {
            diference = yDir_R * MOVE_SPEED * 0.4d;
            if (xPos + diference >= 0 && xPos + diference < gm.getWidth()) {
                if (map[(int) (xPos + diference)][(int) yPos] == 0) {
                    xPos += diference;
                }
            }
            diference = xDir_R * MOVE_SPEED * 0.4d;
            if (yPos + diference >= 0 && yPos + diference < gm.getHeight()) {
                if (map[(int) xPos][(int) (yPos + diference)] == 0) {
                    yPos += diference;
                }
            }
        } else if (this.left) {
            diference = yDir_L * MOVE_SPEED * 0.4d;
            if (xPos - diference >= 0 && xPos - diference < gm.getWidth()) {
                if (map[(int) (xPos - diference)][(int) yPos] == 0) {
                    xPos -= diference;
                }
            }
            diference = xDir_L * MOVE_SPEED * 0.4d;
            if (yPos - diference >= 0 && yPos - diference < gm.getHeight()) {
                if (map[(int) xPos][(int) (yPos - diference)] == 0) {
                    yPos -= diference;
                }
            }
        }
        double gunOSC = -100d;
        //jogging
        if (this.forward || this.backward || this.left || this.right) {
            gunOSC = oscilator;
            this.YOffSet = (int) (oscilator * 9);
        } else {
            if (this.YOffSet > 0) {
                this.YOffSet -= 1;
            } else if (this.YOffSet < 0) {
                this.YOffSet += 1;
            }
        }
        //gun
        this.gun.refresh(gunOSC);
    }

    public int getYOffSet() {
        return this.YOffSet;
    }

    @Override
    public double[] getPosition() {
        return new double[]{xPos, yPos};
    }

    @Override
    public boolean intersect(Point p) {
        return false;
    }

    @Override
    public boolean isDeath() {
        return this.life <= 0;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int[] getTexture() {
        return null;
    }

    /**
     * GUI #################################
     *
     * @param pixels Pixel of screen buffer
     * @param WIDTH Buffer width
     * @param HEIGHT Buffer height
     */
    public void renderGUI(int[] pixels, int WIDTH, int HEIGHT) {
        this.gun.render(pixels, WIDTH, HEIGHT);
    }

}
