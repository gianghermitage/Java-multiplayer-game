package netprg.game;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import netprg.game.entities.Player;
import netprg.game.entities.PlayerMP;
import netprg.game.gfx.Screen;
import netprg.game.gfx.SpriteSheet;
import netprg.game.level.Level;
import netprg.game.net.GameClient;
import netprg.game.net.GameServer;
import netprg.game.net.packets.Packet00Login;

public class Game extends Canvas implements Runnable {

	private static final long serialVersionUID = 1L;

	public static final int WIDTH = 120;
	public static final int HEIGHT = WIDTH / 9 * 16;
	public static final int SCALE = 3;
	public static final String NAME = "Game";
	public static final Dimension DIMENSIONS = new Dimension(WIDTH * SCALE, HEIGHT * SCALE);
	public static Game game;

	public JFrame frame;

	private Thread thread;

	public boolean running = false;
	public int tickCount = 0;

	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	private int[] colours = new int[6 * 6 * 6];

	private Screen screen;
	public InputHandler input;
	public WindowHandler windowHandler;
	public Level level;
	public Player player;

	public GameClient socketClient;
	public GameServer socketServer;

	public boolean debug = true;
	public boolean isApplet = false;

	public void init() {
		game = this;
		int index = 0;
		for (int r = 0; r < 6; r++) {
			for (int g = 0; g < 6; g++) {
				for (int b = 0; b < 6; b++) {
					int rr = (r * 255 / 5);
					int gg = (g * 255 / 5);
					int bb = (b * 255 / 5);

					colours[index++] = rr << 16 | gg << 8 | bb;
				}
			}
		}
		screen = new Screen(WIDTH, HEIGHT, new SpriteSheet("/sprite_sheet.png"));
		input = new InputHandler(this);
		level = new Level("/levels/water_test_level.png");

		Packet00Login loginPacket;
		// if server
		if (socketServer != null) {
			player = new PlayerMP(level, WIDTH / 2, HEIGHT - 30, input, "server", "w", null, -1);
			player.setServer(true);
			loginPacket = new Packet00Login(player.getUsername(), player.x, player.y, player.getColourString(), 1);
			loginPacket.writeData(socketClient);
		}
		// if client
		else {
			player = new PlayerMP(level, WIDTH / 2, HEIGHT - 30, input, JOptionPane.showInputDialog(this, "Username"),
					JOptionPane.showInputDialog(this, "Choose your color (w, r, g, b)"), null, -1);
			player.setServer(false);
			loginPacket = new Packet00Login(player.getUsername(), player.x, player.y, player.getColourString(), 0);
			loginPacket.writeData(socketClient);
		}
		level.addEntity(player);

	}

	public synchronized void start() {
		running = true;

		thread = new Thread(this, NAME + "_main");
		thread.start();
		if (!isApplet) {
			if (JOptionPane.showConfirmDialog(this, "Run server?") == 0) {
				socketServer = new GameServer(this);
				socketServer.start();
				InetAddress inetAddress = null;
				try {
					inetAddress = InetAddress.getLocalHost();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (inetAddress != null) {
					System.out.println("Server IP Address:- " + inetAddress.getHostAddress());
					socketClient = new GameClient(this, inetAddress.getHostAddress());
					socketClient.start();
				}
			} else {
				String hostIP = JOptionPane.showInputDialog(this, "Server IP address (default: localhost)");
				if (hostIP == null || hostIP.length() == 0)
					hostIP = "localhost";
				System.out.println("Client IP Address:- " + hostIP);
				socketClient = new GameClient(this, hostIP);
				socketClient.start();
			}

		}
	}

	public synchronized void stop() {
		running = false;

		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		long lastTime = System.nanoTime();
		double nsPerTick = 1000000000D / 60D;

		int ticks = 0;
		int frames = 0;

		long lastTimer = System.currentTimeMillis();
		double delta = 0;

		init();

		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / nsPerTick;
			lastTime = now;

			while (delta >= 1) {
				ticks++;
				tick();
				render();
				delta -= 1;
			}

			if (System.currentTimeMillis() - lastTimer >= 1000) {
				lastTimer += 1000;
				frames = 0;
				ticks = 0;
			}
		}
	}

	public void tick() {
		tickCount++;
		level.tick();
	}

	public void render() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}

		int xOffset = 0;
		int yOffset = 0;

		level.renderTiles(screen, xOffset, yOffset);
		level.renderEntities(screen);

		for (int y = 0; y < screen.height; y++) {
			for (int x = 0; x < screen.width; x++) {
				int colourCode = screen.pixels[x + y * screen.width];
				if (colourCode < 255)
					pixels[x + y * WIDTH] = colours[colourCode];
			}
		}

		Graphics g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		g.dispose();
		bs.show();
	}

}
