/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package darkhunter.engine;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import javax.swing.JFrame;

/**
 *
 * @author Krcma
 */
public class Engine implements KeyListener, MouseListener, MouseMotionListener {

    private final int FPS, RPS;

    private final JFrame FRAME;
    private final RenderEngine renderEngine;
    private final PhysicsEngine physicsEngine;

    private GameMap map;

    private Robot robot;

    public static float FX_volume = 1f, MUSIC_volume = 0.7f;
    private final HashMap<String, SoundPlayer> sounds;

    /**
     * Create Engine
     *
     * @param frame JFrame for game
     * @param fps FPS (render)
     * @param rps RPS (physics)
     * @param width width of render frame
     * @param height height of render frame
     * @param scale Scale for render engine grraphics resizion
     */
    public Engine(JFrame frame, int fps, int rps, int width, int height, float scale) {
        this.FRAME = frame;
        this.FPS = fps;
        this.RPS = rps;
        this.renderEngine = new RenderEngine(width, height, scale, this);
        this.physicsEngine = new PhysicsEngine(this);
        //sounds
        this.sounds = new HashMap<>();
        this.sounds.put("shoot1", new SoundPlayer(this.getClass().getResource("/darkhunter/src/sounds/shoot1.mp3"), 3, false, Engine.FX_volume));
        this.sounds.put("reload1", new SoundPlayer(this.getClass().getResource("/darkhunter/src/sounds/reload1.mp3"), 3, false, Engine.FX_volume));
        this.sounds.put("music1", new SoundPlayer(this.getClass().getResource("/darkhunter/src/sounds/music1.mp3"), 1, true, Engine.MUSIC_volume));
    }

    public void init() throws AWTException {
        this.robot = new Robot();
        this.FRAME.add(this.renderEngine);
        this.physicsEngine.init(RPS);
        this.renderEngine.init(FPS);
        this.renderEngine.setCursor(this.renderEngine.getToolkit().createCustomCursor(
                new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0),
                "null"));
        this.renderEngine.addKeyListener(this);
        this.renderEngine.addMouseListener(this);
        this.renderEngine.addMouseMotionListener(this);
    }

    public void run() {
        this.renderEngine.start();
        this.physicsEngine.start();
        this.playSound("music1", Engine.MUSIC_volume);
    }

    public void setMap(GameMap _map) {
        //load map
        this.map = _map;
        //create camera
        double[] cnfg = this.map.getPlayerX_Y();
        Camera camera = new Camera(cnfg[0], cnfg[1], 1f, 0f, 0f, -0.76f);
        this.renderEngine.setCamera(camera);
        this.map.getObjects().add(camera);
        //init map
        this.renderEngine.initMap(this.map);
    }

    public GameMap getMap() {
        return this.map;
    }

    public Dimension getJFrameSize() {
        return this.FRAME.getSize();
    }

    public Camera getCamera() {
        return this.renderEngine.getCamera();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //none
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //camera
        Camera c = this.renderEngine.getCamera();
        if (c != null) {
            c.keyPressed(e);
        }
        //menu itemps
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //camera
        Camera c = this.renderEngine.getCamera();
        if (c != null) {
            c.keyReleased(e);
        }
        //menu itemps
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.renderEngine.getCamera().shoot();
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //none
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //none
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        //none
    }

    private int lastMX = 0;
    private int c = 0;

    @Override
    public void mouseMoved(MouseEvent e) {
        //rotate camera
        switch (c) {
            case 0:
                this.lastMX = e.getX();
                break;
            case 3:
                this.c = -1;
                if (this.renderEngine.getCamera() != null) {
                    this.renderEngine.getCamera().rotateCamera((this.lastMX - e.getX()) / 25d);
                }
                this.robot.mouseMove(this.FRAME.getWidth() / 2, this.FRAME.getHeight() / 2);
                break;
        }
        this.c++;
    }

    public void playSound(String name, float volume) {
        SoundPlayer sp = this.sounds.get(name);
        if (sp != null) {
            sp.playSurroundSound(0, volume);
        }
    }

    public void playSurroundSound(String name, Point position) {
        double[] p = this.getCamera().getPosition();
        //calc position of sound (f == 0 -> in center, f==-1 -> left, f==1 -> right)
        double y = -position.y + p[1];
        double a;
        if (y != 0f) {
            a = (position.x - p[0]) / y;
        } else {
            a = 1f;
        }
        float f = (float) Math.atan(a);
        f = (float) (f * 2 / Math.PI * (p[0] <= position.x ? -1f : 1)) + (p[0] <= position.x ? 0.5f : -0.5f);
        f = f < -1f ? -1f : f;
        f = f > 1f ? 1f : f;
        //calc volume
        float volume = (float) Math.sqrt(
                Math.pow(position.x - (p[0]), 2)
                + Math.pow(position.y - (p[1]), 2)
        );
        volume = (float) (volume != 0f ? Math.log10(10f - volume / (100f)) : 1f);
        volume = volume > 1f ? 1f : volume;
        SoundPlayer sp = this.sounds.get(name);
        if (sp != null) {
            sp.playSurroundSound(f, volume);
        }
    }

    public void stopSound(String name) {
        SoundPlayer sp = this.sounds.get(name);
        if (sp != null) {
            sp.stop();
        }
    }

    public void closeSound(String name) {
        SoundPlayer sp = this.sounds.get(name);
        if (sp != null) {
            sp.close();
        }
    }

}
