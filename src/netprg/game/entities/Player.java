package netprg.game.entities;

import java.awt.Rectangle;

import netprg.game.Game;
import netprg.game.InputHandler;
import netprg.game.gfx.Colours;
import netprg.game.gfx.Font;
import netprg.game.gfx.Screen;
import netprg.game.level.Level;
import netprg.game.net.packets.Packet02Move;
import netprg.game.net.packets.Packet22Input;

public class Player extends Mob {

	private InputHandler input;
	private int colour;
	private int scale = 1;
	private String username;
	private String colourString;
	private int score;
	private int speed = 1;
	private boolean gameStart = false;
	private boolean alive = true;
	private boolean isServer = false;
	private int xa = 0;
	private int ya = 0;

	public Player(Level level, int x, int y, InputHandler input, String username, String colourString) {
		super(level, ObjectID.Player, x, y, 1);
		this.input = input;
		this.username = username;
		if (colourString == null)
			this.colourString = "g";
		else
			this.colourString = colourString;
		setColour(this.colourString);
		this.score = 0;
	}

	@Override
	public void tick() {
		if (alive && !isServer) {
			xa = 0;
			ya = 0;
			if (input != null) {
				if (input.up.isPressed()) {
					Packet22Input packet = new Packet22Input(username, "up");
					packet.writeData(Game.game.socketClient);

				}
				if (input.down.isPressed()) {
					Packet22Input packet = new Packet22Input(username, "down");
					packet.writeData(Game.game.socketClient);

				}
				if (input.left.isPressed()) {
					Packet22Input packet = new Packet22Input(username, "left");
					packet.writeData(Game.game.socketClient);

				}
				if (input.right.isPressed()) {
					Packet22Input packet = new Packet22Input(username, "right");
					packet.writeData(Game.game.socketClient);
				}
			}
		}
	}

	@Override
	public void render(Screen screen) {
		if (alive && !isServer) {
			int xTile = 0;
			int yTile = 28;

			int modifier = 8 * scale;
			int xOffset = x - modifier / 2;
			int yOffset = y - modifier / 2;

			screen.render(xOffset, yOffset, xTile + yTile * 32, colour, scale);
			screen.render(xOffset + modifier, yOffset, (xTile + 1) + yTile * 32, colour, scale);

			screen.render(xOffset, yOffset + modifier, xTile + (yTile + 1) * 32, colour, scale);
			screen.render(xOffset + modifier, yOffset + modifier, (xTile + 1) + (yTile + 1) * 32, colour, scale);
			if (username != null) {
				Font.render(username, screen, xOffset - ((username.length() - 1) / 2 * 8), yOffset - 10, colour, 1);
			}
		}

	}

	public boolean isAlive() {
		return this.alive;
	}

	public void setAlive(boolean status) {
		this.alive = status;
	}

	public boolean isServer() {
		return this.isServer;
	}

	public void setServer(boolean status) {
		this.isServer = status;
	}

	public int getScore() {
		return this.score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public synchronized void increaseScore() {
		this.score = this.score + 1;
	}

	public boolean isGameStart() {
		return this.gameStart;
	}

	public void setGameStart(boolean status) {
		this.gameStart = status;
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

	public String getUsername() {
		return this.username;
	}

	public String getColourString() {
		return this.colourString;
	}

	public int getColour() {
		return this.colour;
	}

	@Override
	public Rectangle getBounds() {
		return new Rectangle(x, y, 16, 16);

	}

	public boolean checkCollision(float x, float y, Rectangle myRect, Rectangle otherRect) {
		myRect.x = (int) x;
		myRect.y = (int) y;
		return myRect.intersects(otherRect);
	}

	public synchronized void collision() {
		// collision with screen
		if (this.x + xa <= 0 || this.x + xa + 8 >= Game.WIDTH) {
			this.x += -xa;
		}
		if (this.y + ya <= 0 || this.y + ya + 8 >= Game.HEIGHT) {
			this.y += -ya;
		}
	}

	public void handleInput(String direction) {
		xa = 0;
		ya = 0;
		switch (direction) {
		case "up":
			ya = -speed;
			break;
		case "down":
			ya = +speed;
			break;
		case "left":
			xa = -speed;
			break;
		case "right":
			xa = +speed;
			break;
		default:
			break;
		}
		move(xa, ya);
		collision();
		Packet02Move packet = new Packet02Move(this.getUsername(), this.x, this.y);
		packet.writeData(Game.game.socketClient);
	}
}
