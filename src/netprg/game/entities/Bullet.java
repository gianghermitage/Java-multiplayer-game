package netprg.game.entities;

import java.awt.Rectangle;

import netprg.game.Game;
import netprg.game.gfx.Colours;
import netprg.game.gfx.Screen;
import netprg.game.level.Level;
import netprg.game.net.packets.Packet11BulletDespawn;
import netprg.game.net.packets.Packet12BulletMove;

public class Bullet extends Mob {

	private int colour;
	private String colourString;
	private int scale = 1;
	private String bulletID;
	private int speed = 4;

	public Bullet(Level level, ObjectID ID, String bulletID, String colourString, int x, int y, int speed) {
		super(level, ID, x, y, speed);
		this.colourString = colourString;
		setColour(colourString);
		this.bulletID = bulletID;
	}

	@Override
	public Rectangle getBounds() {
		return new Rectangle(x, y, 10, 12);
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		if(Game.game.socketServer != null) {
			move(0, -speed);
			Packet12BulletMove packet = new Packet12BulletMove(bulletID, x, y);
			packet.writeData(Game.game.socketClient);
			if (y < 0) {
				Packet11BulletDespawn packet11BulletDespawn = new Packet11BulletDespawn(bulletID);
				packet11BulletDespawn.writeData(Game.game.socketClient);
			}
		}

	}

	@Override
	public void render(Screen screen) {
		// TODO Auto-generated method stub
		int xTile = 4;
		int yTile = 28;

		int modifier = 1 * scale;
		int xOffset = x - modifier / 2;
		int yOffset = y - modifier / 2;
		screen.render(xOffset, yOffset, xTile + yTile * 32, colour, scale);
		screen.render(xOffset + modifier, yOffset, (xTile + 1) + yTile * 32, colour, scale);

		screen.render(xOffset, yOffset + modifier, xTile + (yTile + 1) * 32, colour, scale);
		screen.render(xOffset + modifier, yOffset + modifier, (xTile + 1) + (yTile + 1) * 32, colour, scale);
	}


	public String getBulletID() {
		return this.bulletID;
	}

	public String getBulletColourString() {
		return this.colourString;
	}
	
	public void setColour(String colour) {
		if (colour.equals("w")) {
			this.colour = Colours.get(-1, 0, 0, 555);
		} else if (colour.equals("r")) {
			this.colour = Colours.get(-1, 0, 0, 500);
		} else if (colour.equals("g")) {
			this.colour = Colours.get(-1, 0, 0, 050);
		} else if (colour.equals("b")) {
			this.colour = Colours.get(-1, 0, 0, 005);
		} else {
			this.colour = Colours.get(-1, 0, 0, 050);
		}
	}

}
