package netprg.game.level.tiles;

import netprg.game.gfx.Screen;
import netprg.game.level.Level;

public class BasicTile extends Tile {

	protected int tileId;
	protected int tileColour;

	public BasicTile(int id, int x, int y, int tileColour, int levelColour) {
		super(id, false, false, levelColour);
		this.tileId = x + y * 32;
		this.tileColour = tileColour;
	}

	@Override
	public void tick() {
	}

	@Override
	public void render(Screen screen, Level level, int x, int y) {
		screen.render(x, y, tileId, tileColour, 1);
	}

}
