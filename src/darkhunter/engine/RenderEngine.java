/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package darkhunter.engine;

import darkhunter.objects.Light;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Krcma
 */
public class RenderEngine extends Canvas {

    public static final double MAX_VALUE = 10000;

    private int FPS;
    private final int WIDTH, HEIGHT;
    private final float scale;

    private final Thread t1;

    private final Engine engine;

    private BufferedImage buferImage;
    private int[] pixels;
    private double[] zBuffer;

    private Camera camera;

    public void setCamera(Camera _camera) {
        this.camera = _camera;
    }

    public Camera getCamera() {
        return this.camera;
    }

    /**
     * Create render engine
     *
     * @param width Width of render
     * @param height Height of render
     * @param _scale Scale
     * @param _engine Engine
     */
    public RenderEngine(int width, int height, float _scale, Engine _engine) {
        super();
        this.WIDTH = width;
        this.HEIGHT = height;
        this.scale = _scale;
        this.setSize(_engine.getJFrameSize().width, _engine.getJFrameSize().height);
        this.engine = _engine;
        this.t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    thread1_loop();
                } catch (Exception ex) {
                    Logger.getLogger(RenderEngine.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    public void init(int fps) {
        this.FPS = fps;
        this.createBufferStrategy(3);
        this.buferImage = new BufferedImage(this.WIDTH, this.HEIGHT, BufferedImage.TYPE_INT_RGB);
        this.zBuffer = new double[this.WIDTH * this.HEIGHT];
        this.pixels = ((DataBufferInt) this.buferImage.getRaster().getDataBuffer()).getData();
    }

    public void start() {
        this.t1.start();
    }

    private int[][] map, map_floor, map_ceiling;
    private int map_width, map_height;
    private List<Texture> textures;
    private Texture background;
    private List<EngineObject> obj;

    public void initMap(GameMap GM) {
        this.map = GM.getMap();
        this.map_floor = GM.getMapFloor();
        this.map_ceiling = GM.getMapCeiling();
        this.background = GM.getBackGround();
        this.textures = GM.getTextures();
        this.map_width = GM.getWidth();
        this.map_height = GM.getHeight();
        this.obj = engine.getMap().getObjects();
    }

    /**
     * THREAD 1 [WALLS, BACKGROUND, FLOORS ...]
     * #############################################################################################################
     */
    int c = 0;
    double l = 0;

    public void thread1_loop() {
        long lastTime = System.nanoTime();
        final double ns = 1e9 / this.FPS;
        double delta = 0;
        requestFocus();
        BufferStrategy buffer = getBufferStrategy();
        while (true) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1)//Make sure update is only happening 60 times a second
            {
                if (System.nanoTime() - l >= 1e9) {
                    System.out.println("FPS: " + c);
                    c = 0;
                    l = System.nanoTime();
                }
                delta--;
                c++;
                List<EngineObject> objects = new ArrayList<>();
                for (int i = 0; i < obj.size(); i++) {
                    EngineObject o = obj.get(i);
                    if (Math.sqrt(Math.pow(camera.xPos - o.getPosition()[0], 2) + Math.pow(camera.yPos - o.getPosition()[1], 2)) < 10) {
                        objects.add(obj.get(i));
                    }
                }
                //render map
                renderMap(objects);
                //render camera gui
                this.engine.getCamera().renderGUI(this.pixels, this.WIDTH, this.HEIGHT);
                //show buffer
                Graphics2D g = (Graphics2D) buffer.getDrawGraphics();
                g.scale(scale, scale);
                g.drawImage(this.buferImage, 0, 0, this.buferImage.getWidth(), this.buferImage.getHeight(), null);
                buffer.show();
            }
        }
    }

    public void renderMap(List<EngineObject> objects) {
        for (int i = 0; i < this.zBuffer.length; i++) {
            this.zBuffer[i] = MAX_VALUE;
        }
        //for jogging (walking effect -> in textures: walls, floors, ceiling)
        int yoff = this.camera.getYOffSet();
        //for backgound
        int xoff = 0;
        if (background != null) {
            xoff = (int) ((Math.atan(-camera.yDir / camera.xDir) * background.WIDTH) / Math.PI);
        }
        //walls, floors, ceilings
        double xPos = camera.xPos;
        double yPos = camera.yPos;
        double xDir = camera.xDir;
        double yDir = camera.yDir;
        double xPlane = camera.xPlane;
        double yPlane = camera.yPlane;
        for (int x = 0; x < this.WIDTH; x = x + 1) {
            boolean render_wall = true;
            double cameraX = 2 * x / (double) (this.WIDTH) - 1;
            double rayDirX = xDir + xPlane * cameraX;
            double rayDirY = yDir + yPlane * cameraX;
            //Map position
            int mapX = (int) xPos;
            int mapY = (int) yPos;
            //length of ray from current position to next x or y-side
            double sideDistX, sideDistY;
            //Length of ray from one side to next in map
            double deltaDistX = Math.sqrt(1 + (rayDirY * rayDirY) / (rayDirX * rayDirX));
            double deltaDistY = Math.sqrt(1 + (rayDirX * rayDirX) / (rayDirY * rayDirY));
            //Direction to go in x and y
            int stepX, stepY;
            int side = 0;//was the wall vertical or horizontal
            //Figure out the step direction and initial distance to a side
            if (rayDirX < 0) {
                stepX = -1;
                sideDistX = (xPos - mapX) * deltaDistX;
            } else {
                stepX = 1;
                sideDistX = (mapX + 1.0 - xPos) * deltaDistX;
            }
            if (rayDirY < 0) {
                stepY = -1;
                sideDistY = (yPos - mapY) * deltaDistY;
            } else {
                stepY = 1;
                sideDistY = (mapY + 1.0 - yPos) * deltaDistY;
            }
            //Loop to find where the ray hits a wall
            Object[] postRnder = null;
            while (true) {
                //Jump to next square (map[x][y])
                if (sideDistX < sideDistY) {
                    sideDistX += deltaDistX;
                    mapX += stepX;
                    side = 0;
                } else {
                    sideDistY += deltaDistY;
                    mapY += stepY;
                    side = 1;
                }
                //continue on XLOOP if is out
                if (mapX < 0 || mapY < 0 || mapX >= this.map_width || mapY >= this.map_height) {
                    render_wall = false;
                    for (int y = 0; y < this.HEIGHT; y++) {
                        this.zBuffer[x + y * this.WIDTH] = MAX_VALUE;
                    }
                    break;
                }
                //Check if ray has hit a wall
                if (map[mapX][mapY] > 0) {
                    if (textures.get(map[mapX][mapY] - 1).removeBG) {
                        postRnder = new Object[]{x, mapX, mapY, xPos, yPos, stepX, stepY, rayDirX, rayDirY, side, yoff};
                    } else {
                        break;
                    }
                }
            }
            if (render_wall) {
                renderWall(x, mapX, mapY, xPos, yPos, stepX, stepY, rayDirX, rayDirY, side, yoff, false, objects);
            }
            if (postRnder != null) {
                renderWall((int) postRnder[0], (int) postRnder[1], (int) postRnder[2],
                        (double) postRnder[3], (double) postRnder[4], (int) postRnder[5],
                        (int) postRnder[6], (double) postRnder[7], (double) postRnder[8],
                        (int) postRnder[9], (int) postRnder[10], true, objects);
            }
            //FLOOR ###########################################################
            //Calculate distance of perpendicular ray (Euclidean distance will give fisheye effect!)
            double perpWallDist;
            if (side == 0) {
                perpWallDist = (mapX - xPos + (1 - stepX) / 2) / rayDirX;
            } else {
                perpWallDist = (mapY - yPos + (1 - stepY) / 2) / rayDirY;
            }
            //Calculate height of line to draw on screen
            int lineHeight = (int) (this.HEIGHT / perpWallDist);
            //calculate lowest and highest pixel to fill in current stripe
            int drawEnd = lineHeight / 2 + this.HEIGHT / 2;
            if (drawEnd >= this.HEIGHT) {
                drawEnd = this.HEIGHT - 1;
            }
            //calculate value of wallX
            double wallX; //where exactly the wall was hit
            if (side == 0) {
                wallX = yPos + perpWallDist * rayDirY;
            } else {
                wallX = xPos + perpWallDist * rayDirX;
            }
            wallX -= Math.floor((wallX));
            double floorXWall, floorYWall;
            //4 different wall directions possible
            if (side == 0 && rayDirX > 0) {
                floorXWall = mapX;
                floorYWall = mapY + wallX;
            } else if (side == 0 && rayDirX < 0) {
                floorXWall = mapX + 1.0d;
                floorYWall = mapY + wallX;
            } else if (side == 1 && rayDirY > 0) {
                floorXWall = mapX + wallX;
                floorYWall = mapY;
            } else {
                floorXWall = mapX + wallX;
                floorYWall = mapY + 1.0d;
            }
            double currentDist;
            for (int y = drawEnd; y < this.HEIGHT; y++) {
                //you could make a small lookup table for this instead
                currentDist = this.HEIGHT / (2.0 * y - this.HEIGHT);
                double weight = currentDist / perpWallDist;
                double currentFloorX = weight * floorXWall + (1.0 - weight) * xPos;
                double currentFloorY = weight * floorYWall + (1.0 - weight) * yPos;
                int floorTexX = (int) (currentFloorX * 64) % 64;
                int floorTexY = (int) (currentFloorY * 64) % 64;
                //canculate shadow
                double shc = shadow(0.8d - currentDist / this.camera.getLightIntensity(), currentFloorX, currentFloorY, objects);
                //index of pixel in texture
                int pixel_index = Texture.DEF_SIZE * floorTexY + floorTexX;
                //if is out of bounds then continue
                if (pixel_index < 0 || pixel_index >= Texture.DEF_SIZE * textures.get(0).WIDTH) {
                    continue;
                }
                if ((int) currentFloorX < 0 || (int) currentFloorY < 0 || (int) currentFloorX >= this.map_width || (int) currentFloorY >= this.map_height) {
                    continue;
                }
                //floor
                if (getZBuffer(x + (y + yoff) * this.WIDTH) > currentDist) {
                    int Num = map_floor[(int) currentFloorX][(int) currentFloorY] - 1;
                    if (Num >= 0) {
                        Color c = new Color(textures.get(Num).pixels[pixel_index]);
                        setPixel(
                                x + (y + yoff) * this.WIDTH,
                                ((int) (c.getRed() * shc) << 16)
                                | ((int) (c.getGreen() * shc) << 8)
                                | (int) (c.getBlue() * shc)
                        );
                        setZBuffer(x + (y + yoff) * this.WIDTH, currentDist);
                    } else {
                        setZBuffer(x + (y + yoff) * this.WIDTH, MAX_VALUE);
                    }
                }
                if (getZBuffer(x + (this.HEIGHT - y + yoff) * this.WIDTH) > currentDist) {
                    //ceiling
                    int Num = map_ceiling[(int) currentFloorX][(int) currentFloorY] - 1;
                    if (Num >= 0) {
                        Color c = new Color(textures.get(Num).pixels[pixel_index]);
                        setPixel(
                                x + (this.HEIGHT - y + yoff) * this.WIDTH,
                                ((int) (c.getRed() * shc) << 16)
                                | ((int) (c.getGreen() * shc) << 8)
                                | (int) (c.getBlue() * shc)
                        );
                        setZBuffer(x + (this.HEIGHT - y + yoff) * this.WIDTH, currentDist);
                    } else {
                        setZBuffer(x + (this.HEIGHT - y + yoff) * this.WIDTH, MAX_VALUE);
                    }
                }
            }
        }
        //backgrund
        //down
        for (int i = 0; i < pixels.length; i++) {
            if (this.zBuffer[i] == MAX_VALUE) {
                this.pixels[i] = Color.black.getRGB();
            }
        }
        //up     
        if (background != null) {
            for (int x = 0; x < this.WIDTH; x++) {
                for (int y = 0; y < background.HEIGHT; y++) {
                    int bi = x + xoff;
                    while (bi >= background.WIDTH) {
                        bi -= background.WIDTH;
                    }
                    while (bi <= background.WIDTH) {
                        bi += background.WIDTH;
                    }
                    bi = bi + y * background.WIDTH;
                    if (bi < background.pixels.length && bi >= 0) {
                        if (this.zBuffer[x + y * this.WIDTH] == MAX_VALUE) {
                            this.pixels[x + y * this.WIDTH] = background.pixels[bi];
                        }
                    }
                }
            }
        }
        //ENTITIES ###########################################################
        //after sorting the sprites, do the projection and draw them
        for (int i = 0; i < objects.size(); i++) {
            //translate sprite position to relative to camera
            double spriteX = objects.get(i).getPosition()[0] - xPos;
            double spriteY = objects.get(i).getPosition()[1] - yPos;
            //transform sprite with the inverse camera matrix
            // [ planeX   dirX ] -1                                       [ dirY      -dirX ]
            // [               ]       =  1/(planeX*dirY-dirX*planeY) *   [                 ]
            // [ planeY   dirY ]                                          [ -planeY  planeX ]
            double invDet = 1.0 / (xPlane * yDir - xDir * yPlane); //required for correct matrix multiplication
            double transformX = invDet * (yDir * spriteX - xDir * spriteY);
            double transformY = invDet * (-yPlane * spriteX + xPlane * spriteY); //this is actually the depth inside the screen, that what Z is in 3D
            int spriteScreenX = (int) (this.WIDTH / 2 * (1 + transformX / transformY));
            //calculate height of the sprite on screen
            int spriteHeight = Math.abs((int) (this.HEIGHT / (transformY))); //using "transformY" instead of the real distance prevents fisheye
            //calculate lowest and highest pixel to fill in current stripe
            int drawStartY = -spriteHeight / 2 + this.HEIGHT / 2;
            if (drawStartY < 0) {
                drawStartY = 0;
            }
            int drawEndY = spriteHeight / 2 + this.HEIGHT / 2;
            if (drawEndY >= this.HEIGHT) {
                drawEndY = this.HEIGHT - 1;
            }
            //calculate width of the sprite
            int spriteWidth = Math.abs((int) (this.HEIGHT / (transformY)));
            int drawStartX = -spriteWidth / 2 + spriteScreenX;
            if (drawStartX < 0) {
                drawStartX = 0;
            }
            int drawEndX = spriteWidth / 2 + spriteScreenX;
            if (drawEndX >= this.WIDTH) {
                drawEndX = this.WIDTH - 1;
            }

            int texWidth = objects.get(i).getWidth();
            int texHeight = objects.get(i).getHeight();
            int[] texture = objects.get(i).getTexture();
            if (texWidth == 0 || texHeight == 0 || texture == null) {
                continue;
            }
            int aplha = texture[0];
            //loop through every vertical stripe of the sprite on screen
            for (int stripe = drawStartX; stripe < drawEndX; stripe++) {
                int texX = (int) (256 * (stripe - (-spriteWidth / 2 + spriteScreenX)) * texWidth / spriteWidth) / 256;
                //the conditions in the if are:
                //1) it's in front of camera plane so you don't see things behind you
                //2) it's on the screen (left)
                //3) it's on the screen (right)
                //4) ZBuffer, with perpendicular distance
                int t_index;
                if (transformY > 0 && stripe > 0 && stripe < this.WIDTH) {
                    double dist = 0.8d - Math.sqrt(Math.pow(objects.get(i).getPosition()[0] - xPos, 2) + Math.pow(objects.get(i).getPosition()[1] - yPos, 2)) / this.camera.getLightIntensity();
                    for (int y = drawStartY; y < drawEndY; y++) //for every pixel of the current stripe
                    {
                        if (transformY < this.zBuffer[stripe + y * this.WIDTH]) {
                            int d = (y) * 256 - this.HEIGHT * 128 + spriteHeight * 128; //256 and 128 factors to avoid floats
                            int texY = ((d * texHeight) / spriteHeight) / 256;
                            t_index = texWidth * texY + texX;
                            if (t_index >= 0 && t_index < texture.length) {
                                Color c = new Color(texture[t_index]); //get current color from the texture
                                if (c.getRGB() != aplha) {
                                    double shc = shadow(dist, objects.get(i).getPosition()[0], objects.get(i).getPosition()[1], objects);
                                    setPixel(this.WIDTH * (y + yoff) + stripe, ((int) (c.getRed() * shc) << 16)
                                            | ((int) (c.getGreen() * shc) << 8)
                                            | (int) (c.getBlue() * shc));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void setPixel(int index, int color) {
        if (index >= 0 && index < this.pixels.length) {
            this.pixels[index] = color;
        }
    }

    public void setZBuffer(int index, double value) {
        if (index >= 0 && index < this.zBuffer.length) {
            this.zBuffer[index] = value;
        }
    }

    /**
     * Calculate shadow intensity
     *
     * @param light_intensity Default light intensity
     * @param x X position of pixel
     * @param y Y position of pixel
     * @param objects EngineObjects list (fing lights)
     * @return
     */
    private double shadow(double light_intensity, double x, double y, List<EngineObject> objects) {
        for (int i = 0; i < objects.size(); i++) {
            EngineObject eo = objects.get(i);
            if (eo instanceof Light) {
                double dist = Math.sqrt(Math.pow(eo.getPosition()[0] - x, 2) + Math.pow(eo.getPosition()[1] - y, 2));
                light_intensity = Math.max(
                        light_intensity,
                        0.8d - dist / ((Light) eo).getIntensity()
                );
            }
        }
        return light_intensity < 0d ? 0d : light_intensity;
    }

    private void renderWall(int x, int mapX, int mapY, double xPos,
            double yPos, int stepX, int stepY,
            double rayDirX, double rayDirY, int side,
            int yoff, boolean postRender, List<EngineObject> objects) {
        //WALLS ###########################################################
        //Calculate distance to the point of impact
        double perpWallDist;
        if (side == 0) {
            perpWallDist = Math.abs((mapX - xPos + (1 - stepX) / 2) / rayDirX);
        } else {
            perpWallDist = Math.abs((mapY - yPos + (1 - stepY) / 2) / rayDirY);
        }
        //Calculate the height of the wall based on the distance from the camera
        int lineHeight;
        if (perpWallDist > 0) {
            lineHeight = Math.abs((int) (this.HEIGHT / perpWallDist));
        } else {
            lineHeight = this.HEIGHT;
        }
        //calculate lowest and highest pixel to fill in current stripe
        int drawStart = -lineHeight / 2 + this.HEIGHT / 2;
        if (drawStart < 0) {
            drawStart = 0;
        }
        int drawEnd = lineHeight / 2 + this.HEIGHT / 2;
        if (drawEnd >= this.HEIGHT) {
            drawEnd = this.HEIGHT - 1;
        }
        //add a textures
        double wallX;//Exact position of where wall was hit
        if (side == 1) {//If its a y-axis wall
            wallX = (xPos + ((mapY - yPos + (1 - stepY) / 2) / rayDirY) * rayDirX);
        } else {//X-axis wall
            wallX = (yPos + ((mapX - xPos + (1 - stepX) / 2) / rayDirX) * rayDirY);
        }
        wallX -= Math.floor(wallX);
        //x coordinate on the texture
        int texX = (int) (wallX * Texture.DEF_SIZE);
        if (side == 0 && rayDirX > 0) {
            texX = Texture.DEF_SIZE - texX - 1;
        }
        if (side == 1 && rayDirY < 0) {
            texX = Texture.DEF_SIZE - texX - 1;
        }
        double shc = 0d;
        int texNum = map[mapX][mapY] - 1;
        int n = textures.get(texNum).pixels[0];
        //calculate y coordinate on texture
        for (int y = drawStart; y < drawEnd; y++) {
            int texY = (((y * 2 - this.HEIGHT + lineHeight) << 6) / lineHeight) / 2;
            Color c = new Color(textures.get(texNum).pixels[texX + (texY * Texture.DEF_SIZE)]);
            if (textures.get(texNum).removeBG) {
                if (c.getRGB() == n) {
                    if (!postRender) {
                        setZBuffer(x + (y + yoff) * this.WIDTH, MAX_VALUE);
                    }
                    continue;
                }
            }
            //canculate shadow
            if (side == 1) {//If its a y-axis wall
                shc = shadow(0.8d - perpWallDist / this.camera.getLightIntensity(), mapX + (double) texX / (double) textures.get(texNum).WIDTH, mapY, objects);
            } else {
                shc = shadow(0.8d - perpWallDist / this.camera.getLightIntensity(), mapX, mapY + (double) texY / textures.get(texNum).WIDTH, objects);
            }
            setPixel(
                    x + (y + yoff) * this.WIDTH,
                    ((int) (c.getRed() * shc) << 16)
                    | ((int) (c.getGreen() * shc) << 8)
                    | (int) (c.getBlue() * shc)
            );
            setZBuffer(x + (y + yoff) * this.WIDTH, perpWallDist);
        }
    }

    private double getZBuffer(int i) {
        if (i >= this.zBuffer.length || i < 0) {
            return MAX_VALUE;
        }
        return this.zBuffer[i];
    }

}
