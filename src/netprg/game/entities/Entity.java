package netprg.game.entities;

import netprg.game.gfx.Screen;
import netprg.game.level.Level;

public abstract class Entity {

	public int x, y;
	protected Level level;
    protected ObjectID ID;


	public Entity(Level level, ObjectID ID) {
		init(level);
		this.ID = ID;
	}

	public final void init(Level level) {
		this.level = level;
	}

	public abstract void tick();

	public abstract void render(Screen screen);
	
	public ObjectID getObjectID() {
		return this.ID;
	}
	public int getX() {
		return this.x;
	}
	public int getY() {
		return this.y;
	}
}
