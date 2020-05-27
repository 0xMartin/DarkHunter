/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package darkhunter.engine;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Krcma
 */
public class GameMap implements Serializable {

    private int WIDTH, HEIGHT;

    private double[] player;

    private List<EngineObject> OBJECTS;

    private int[][] MAP, MAP_FLOOR, MAP_CEILING;
    private Texture background;

    private List<Texture> texture_list;

    public GameMap() {
        this.texture_list = new ArrayList<>();
        this.OBJECTS = new ArrayList<>();
    }

    public void addTexture(Texture texture) {
        this.texture_list.add(texture);
    }

    public void removeTexture(Texture texture) {
        this.texture_list.remove(texture);
    }

    public void setPlayerX_Y(double[] cnfg) {
        this.player = cnfg;
    }

    public double[] getPlayerX_Y() {
        return this.player;
    }

    public void setMap(int[][] _map, int[][] _map_floor, int[][] _map_ceiling, int width, int height) {
        this.MAP = _map;
        this.MAP_FLOOR = _map_floor;
        this.MAP_CEILING = _map_ceiling;
        this.WIDTH = width;
        this.HEIGHT = height;
    }

    public void setBackGround(URL f) {
        if (f == null) {
            this.background = null;
        } else {
            this.background = new Texture(f, false);
        }
    }

    public Texture getBackGround() {
        return background;
    }

    public int[][] getMap() {
        return this.MAP;
    }

    public int[][] getMapFloor() {
        return this.MAP_FLOOR;
    }

    public int[][] getMapCeiling() {
        return this.MAP_CEILING;
    }

    public List<Texture> getTextures() {
        return this.texture_list;
    }

    public List<EngineObject> getObjects() {
        return this.OBJECTS;
    }

    public void setObject(List<EngineObject> list) {
        this.OBJECTS = list;
    }

    public int getWidth() {
        return this.WIDTH;
    }

    public int getHeight() {
        return this.HEIGHT;
    }

}
