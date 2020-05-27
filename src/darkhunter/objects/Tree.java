/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package darkhunter.objects;

import darkhunter.engine.EngineObject;
import darkhunter.engine.GameMap;
import darkhunter.engine.Texture;
import java.awt.Point;

/**
 *
 * @author Krcma
 */
public class Tree implements EngineObject {

    private final double X, Y;
    private final Texture texture;

    public Tree(double x, double y) {
        this.X = x;
        this.Y = y;
        this.texture = null;
    }

    @Override
    public double[] getPosition() {
        return new double[]{this.X, this.Y};
    }

    @Override
    public boolean intersect(Point p) {
        return false;
    }

    @Override
    public boolean isDeath() {
        return false;
    }

    @Override
    public int getWidth() {
        return this.texture.WIDTH;
    }

    @Override
    public int getHeight() {
        return this.texture.HEIGHT;
    }

    @Override
    public void refresh(GameMap gm, double oscilator) {
 
    }

    @Override
    public int[] getTexture() {
        return this.texture.pixels;
    }

}
