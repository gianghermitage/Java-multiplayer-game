package netprg.game.entities;

import java.awt.Rectangle;

import netprg.game.level.Level;

public abstract class Mob extends Entity {

	protected String name;
	protected int speed;
	protected boolean isMoving;
	protected int scale = 1;

	public Mob(Level level, ObjectID id, int x, int y, int speed) {
		super(level, id);
		this.x = x;
		this.y = y;
		this.speed = speed;
	}

	public void move(int xa, int ya) {
		if (xa != 0 && ya != 0) {
			move(xa, 0);
			move(0, ya);
			return;
		}
		x += xa;
		y += ya;

	}

	public String getName() {
		return name;
	}

	public boolean isMoving() {
		return isMoving;
	}

	public void setMoving(boolean isMoving) {
		this.isMoving = isMoving;
	}

	public abstract Rectangle getBounds();

}
