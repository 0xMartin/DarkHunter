/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package darkhunter.engine;

import java.util.List;

/**
 *
 * @author Krcma
 */
public class PhysicsEngine implements Runnable {

    private int RPS;

    private final Engine engine;

    public PhysicsEngine(Engine _engine) {
        this.engine = _engine;
    }

    public void init(int rps) {
        this.RPS = rps;
    }

    public void start() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        final double ns = 1e9 / this.RPS;
        double delta = 0;
        while (true) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1)//Make sure update is only happening 60 times a second
            {
                delta--;
                refresh(this.engine.getMap());
            }
        }
    }

    private void refresh(GameMap gm) {
        double oscilator = Math.sin(System.nanoTime() / 8e7);
        List<EngineObject> objs = gm.getObjects();
        for (int i = 0; i < objs.size(); i++) {
            EngineObject eo = objs.get(i);
            if (eo.isDeath()) {
                objs.remove(i);
                i--;
                continue;
            }
            eo.refresh(gm, oscilator);
        }
    }

}
