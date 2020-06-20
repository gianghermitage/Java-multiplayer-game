package netprg.game.entities;

import java.net.InetAddress;

import netprg.game.InputHandler;
import netprg.game.level.Level;

public class PlayerMP extends Player {

	public InetAddress ipAddress;
	public int port;

	public PlayerMP(Level level, int x, int y, InputHandler input, String username, String colourString,
			InetAddress ipAddress, int port) {
		super(level, x, y, input, username, colourString);
		this.ipAddress = ipAddress;
		this.port = port;
	}

	public PlayerMP(Level level, int x, int y, String username, String colourString, InetAddress ipAddress, int port) {
		super(level, x, y, null, username, colourString);
		this.ipAddress = ipAddress;
		this.port = port;
	}

	@Override
	public void tick() {
		super.tick();
	}

}
