package netprg.game.entities;

import java.awt.Rectangle;

import netprg.game.Game;
import netprg.game.gfx.Colours;
import netprg.game.gfx.Screen;
import netprg.game.level.Level;
import netprg.game.net.packets.Packet01Disconnect;
import netprg.game.net.packets.Packet04MinionDespawn;
import netprg.game.net.packets.Packet05MinionMove;
import netprg.game.net.packets.Packet11BulletDespawn;

public class Minion extends Mob {

	
    private int colour = Colours.get(-1, 0, 0, 555);
    private int scale = 1;
	private int minionID;
	public Minion(Level level, ObjectID id,int minionID, int x, int y, int speed) {
		super(level, id, x, y, speed);
		this.minionID = minionID;
	}

	@Override
	public Rectangle getBounds() {
    	return new Rectangle(x, y, 16, 12);
	}

	@Override
	public void tick() {
		move(0, speed);
		Packet05MinionMove packet = new Packet05MinionMove(minionID, x, y);
		packet.writeData(Game.game.socketClient);
		collision();
		if(y > Game.HEIGHT && Game.game.socketServer != null) {
			Packet04MinionDespawn packet04MinionDespawn = new Packet04MinionDespawn(minionID);
    		packet04MinionDespawn.writeData(Game.game.socketClient);
		}
	}

	private synchronized void collision() {
		if(Game.game.player.isGameStart()) {
	       	for(int i = 0; i < level.getEntities().size();i++) {
	       		Entity tempObj = level.getEntities().get(i);
	       		if(tempObj.getObjectID() == ObjectID.Player && ((Player)tempObj).isAlive() && ((Player)tempObj).isGameStart() ) {
	       			if(getBounds().intersects(((Player)tempObj).getBounds())) {
	       				Packet04MinionDespawn packet04MinionDespawn = new Packet04MinionDespawn(minionID);
	       	    		packet04MinionDespawn.writeData(Game.game.socketClient);
	       	    		Packet01Disconnect packet = new Packet01Disconnect(((Player)tempObj).getUsername());
	       	    		packet.writeData(Game.game.socketClient);
	       			}
	       		}
	       		if(tempObj.getObjectID() == ObjectID.Bullet) {
	       			if(getBounds().intersects(((Bullet)tempObj).getBounds())) {
	       				Packet04MinionDespawn packet04MinionDespawn = new Packet04MinionDespawn(minionID);
	       	    		packet04MinionDespawn.writeData(Game.game.socketClient);
	       				Packet11BulletDespawn packet11BulletDespawn = new Packet11BulletDespawn(((Bullet)tempObj).getBulletID());
	       				packet11BulletDespawn.writeData(Game.game.socketClient);
	       				//Game.game.player.increaseScore();
	       			}
	       		}
	       	}
		}
	}

	@Override
	public void render(Screen screen) {
        int xTile = 2;
        int yTile = 28;

        int modifier = 8 * scale;
        int xOffset = x - modifier / 2;
        int yOffset = y - modifier / 2;

        screen.render(xOffset, yOffset, xTile + yTile * 32, colour, scale);
        screen.render(xOffset + modifier, yOffset, (xTile + 1) + yTile * 32, colour,
                scale);

            screen.render(xOffset, yOffset + modifier, xTile + (yTile + 1) * 32, colour, scale);
            screen.render(xOffset + modifier, yOffset + modifier, (xTile + 1) + (yTile + 1)
                    * 32, colour, scale);
	}
	public int getMinionID() {
		return this.minionID;
	}
	public int getMinionSpeed() {
		return this.speed;
	}


}
