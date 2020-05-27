/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package darkhunter.engine;

import java.awt.Point;

/**
 *
 * @author Krcma
 */
public interface EngineObject {

    public double[] getPosition();

    public boolean intersect(Point p);
    
    public boolean isDeath();
    
    public int getWidth();
    
    public int getHeight();
    
    public void refresh(GameMap gm, double oscilator);

    public int[] getTexture();
    
}
