package netprg.game.level;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import netprg.game.Game;
import netprg.game.entities.Bullet;
import netprg.game.entities.Entity;
import netprg.game.entities.Minion;
import netprg.game.entities.Player;
import netprg.game.entities.PlayerMP;
import netprg.game.gfx.Font;
import netprg.game.gfx.Screen;
import netprg.game.level.tiles.Tile;
import netprg.game.net.packets.Packet03MinionSpawn;
import netprg.game.net.packets.Packet10BulletSpawn;

public class Level {

    private byte[] tiles;
    public int width;
    public int height;
    private List<Entity> entities = new ArrayList<Entity>();
    private String imagePath;
    private BufferedImage image;
    Random random = new Random();

    private int delaySpawnTick = 200;
    private int bulletDelayTick = 0;
    private int bulletNumber = 0;
    private int minionTimer = 0;

    
    public Level(String imagePath) {
        if (imagePath != null) {
            this.imagePath = imagePath;
            this.loadLevelFromFile();
        } else {
            this.width = 64;
            this.height = 64;
            tiles = new byte[width * height];
            this.generateLevel();
        }
    }

    private void loadLevelFromFile() {
        try {
            this.image = ImageIO.read(Level.class.getResource(this.imagePath));
            this.width = this.image.getWidth();
            this.height = this.image.getHeight();
            tiles = new byte[width * height];
            this.loadTiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTiles() {
        int[] tileColours = this.image.getRGB(0, 0, width, height, null, 0, width);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tileCheck: for (Tile t : Tile.tiles) {
                    if (t != null && t.getLevelColour() == tileColours[x + y * width]) {
                        this.tiles[x + y * width] = t.getId();
                        break tileCheck;
                    }
                }
            }
        }
    }

    @SuppressWarnings("unused")
    private void saveLevelToFile() {
        try {
            ImageIO.write(image, "png", new File(Level.class.getResource(this.imagePath).getFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void alterTile(int x, int y, Tile newTile) {
        this.tiles[x + y * width] = newTile.getId();
        image.setRGB(x, y, newTile.getLevelColour());
    }

    public void generateLevel() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x * y % 10 < 7) {
                    tiles[x + y * width] = Tile.GRASS.getId();
                } else {
                    tiles[x + y * width] = Tile.STONE.getId();
                }
            }
        }
    }

    public synchronized List<Entity> getEntities() {
        return this.entities;
    }

    public void tick() {

        for (int i = 0; i < entities.size();i++) {
            Entity e  = entities.get(i);
            e.tick();
        }
        if(Game.game.player.isGameStart()) {
            if(Game.game.socketServer != null) {
           	 spawnMinion();
           }
      	 	if(Game.game.player.isAlive()) spawnBullet();
        }

        for (Tile t : Tile.tiles) {
            if (t == null) {
                break;
            }
            t.tick();
        }
    }

    public void renderTiles(Screen screen, int xOffset, int yOffset) {
        if (xOffset < 0)
            xOffset = 0;
        if (xOffset > ((width << 3) - screen.width))
            xOffset = ((width << 3) - screen.width);
        if (yOffset < 0)
            yOffset = 0;
        if (yOffset > ((height << 3) - screen.height))
            yOffset = ((height << 3) - screen.height);

        screen.setOffset(xOffset, yOffset);

        for (int y = (yOffset >> 3); y < (yOffset + screen.height >> 3) + 1; y++) {
            for (int x = (xOffset >> 3); x < (xOffset + screen.width >> 3) + 1; x++) {
                getTile(x, y).render(screen, this, x << 3, y << 3);
            }
        }
    }

    public void renderEntities(Screen screen) {
        for (int i = 0; i < entities.size();i++) {
            Entity e  = entities.get(i);
            e.render(screen);
        }
        if(!Game.game.player.isAlive())
            Font.render("Game over", screen, Game.WIDTH/2 - (("Game over".length() - 1) / 2 * 8), Game.HEIGHT/2,
                    Game.game.player.getColour(), 1);
    }
    
    public Tile getTile(int x, int y) {
        if (0 > x || x >= width || 0 > y || y >= height)
            return Tile.VOID;
        return Tile.tiles[tiles[x + y * width]];
    }

    public synchronized void addEntity(Entity entity) {
        this.getEntities().add(entity);
    }

    public synchronized void removePlayerMP(String username) {
        this.getEntities().remove(getPlayerMP(username));
    }
    
    public synchronized void removeMinion(int minionID) {
        this.getEntities().remove(getMinion(minionID));
    }

    public PlayerMP getPlayerMP(String username) {
        for (int i = 0; i < entities.size();i++) {
            Entity e  = entities.get(i);
            if (e instanceof PlayerMP && ((PlayerMP) e).getUsername().equals(username)) {
                return (PlayerMP)e;
            }
        }
        return null;
    }
    
    public Minion getMinion(int minionID) {
        for (int i = 0; i < entities.size();i++) {
            Entity e  = entities.get(i);
            if (e instanceof Minion && ((Minion) e).getMinionID() == minionID) {
                return (Minion)e;
            }
        }
        return null;
	}
    
    public Bullet getBullet(String bulletID) {
        for (int i = 0; i < entities.size();i++) {
            Entity e  = entities.get(i);
            if (e instanceof Bullet && ((Bullet) e).getBulletID().equals(bulletID)) {
                return (Bullet)e;
            }
        } 
        return null;
	}
    


    public synchronized void movePlayer(String username, int x, int y) {
        PlayerMP player = getPlayerMP(username);
        if(player.isAlive()) {
        	 player.x = x;
             player.y = y;
        }
       
    }
    
    public synchronized void moveMinion(int minionID, int x, int y) {
        Minion minion = getMinion(minionID);
        minion.x = x;
        minion.y = y;
    }
    
    public synchronized void spawnBullet() {
    	bulletDelayTick--;
    	if(bulletDelayTick <= 0 ) {
    		Player tempPlayer = Game.game.player;
    		String bulletID = tempPlayer.getUsername() + bulletNumber;
    		bulletNumber++;
    		Packet10BulletSpawn packet10BulletSpawn = new Packet10BulletSpawn(bulletID, tempPlayer.getX() - 1,tempPlayer.getY(),tempPlayer.getColourString());
    		packet10BulletSpawn.writeData(Game.game.socketClient);
        	bulletDelayTick = 30;
    	}
    }

	public synchronized void spawnMinion() {
    	delaySpawnTick--;
    	minionTimer++;
    	if(delaySpawnTick == 0) {
    		if(minionTimer <= 600) {
    			Packet03MinionSpawn packet03MinionSpawn = new Packet03MinionSpawn(Game.game.socketServer.getMinionID(),random.nextInt(150),0,1);
        		packet03MinionSpawn.writeData(Game.game.socketClient);
        		delaySpawnTick = 50;
    		}
    		if(minionTimer > 600 && minionTimer <= 1200) {
    			Packet03MinionSpawn packet03MinionSpawn = new Packet03MinionSpawn(Game.game.socketServer.getMinionID(),random.nextInt(150),0,1);
        		packet03MinionSpawn.writeData(Game.game.socketClient);
        		delaySpawnTick = 40;
    		}
    		if(minionTimer > 1200 && minionTimer <= 1800) {
    			Packet03MinionSpawn packet03MinionSpawn = new Packet03MinionSpawn(Game.game.socketServer.getMinionID(),random.nextInt(150),0,1);
        		packet03MinionSpawn.writeData(Game.game.socketClient);
        		delaySpawnTick = 30;
    		}
    		if(minionTimer > 1800 && minionTimer <= 2400) {
    			Packet03MinionSpawn packet03MinionSpawn = new Packet03MinionSpawn(Game.game.socketServer.getMinionID(),random.nextInt(150),0,random.nextInt(2) + 1);
        		packet03MinionSpawn.writeData(Game.game.socketClient);
        		delaySpawnTick = 20;
    		}
    		if(minionTimer > 2400 && minionTimer <= 3600) {
    			Packet03MinionSpawn packet03MinionSpawn = new Packet03MinionSpawn(Game.game.socketServer.getMinionID(),random.nextInt(150),0,random.nextInt(3) + 1);
        		packet03MinionSpawn.writeData(Game.game.socketClient);
        		delaySpawnTick = 10;
    		}
    		if(minionTimer > 3600) {
    			Packet03MinionSpawn packet03MinionSpawn = new Packet03MinionSpawn(Game.game.socketServer.getMinionID(),random.nextInt(150),0,random.nextInt(3) + 1);
        		packet03MinionSpawn.writeData(Game.game.socketClient);
        		delaySpawnTick = 5;
    		}
    		
    	}

    }

	public synchronized void moveBullet(String bulletID, int bulletX, int bulletY) {
        Bullet bullet = getBullet(bulletID);
        bullet.x = bulletX;
        bullet.y = bulletY;
		
	}

	public synchronized void removeBullet(String bulletID) {
        this.getEntities().remove(getBullet(bulletID));
		
	}
}
