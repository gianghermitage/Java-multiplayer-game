package netprg.game.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import netprg.game.Game;
import netprg.game.entities.Bullet;
import netprg.game.entities.Minion;
import netprg.game.entities.ObjectID;
import netprg.game.entities.PlayerMP;
import netprg.game.net.packets.Packet;
import netprg.game.net.packets.Packet.PacketTypes;
import netprg.game.net.packets.Packet00Login;
import netprg.game.net.packets.Packet01Disconnect;
import netprg.game.net.packets.Packet02Move;
import netprg.game.net.packets.Packet03MinionSpawn;
import netprg.game.net.packets.Packet04MinionDespawn;
import netprg.game.net.packets.Packet05MinionMove;
import netprg.game.net.packets.Packet10BulletSpawn;
import netprg.game.net.packets.Packet11BulletDespawn;
import netprg.game.net.packets.Packet12BulletMove;
import netprg.game.net.packets.Packet20IncreaseScore;

public class GameServer extends Thread {

	private DatagramSocket socket;
	private Game game;
	private List<PlayerMP> connectedPlayers = new ArrayList<PlayerMP>();
	private List<Minion> minionList = new ArrayList<Minion>();
	private List<Bullet> bulletList = new ArrayList<Bullet>();
	private int minionID = 0;

	public GameServer(Game game) {
		this.game = game;
		try {
			this.socket = new DatagramSocket(1331);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
		}
	}

	private void parsePacket(byte[] data, InetAddress address, int port) {
		String message = new String(data).trim();
		PacketTypes type = Packet.lookupPacket(message.substring(0, 2));
		Packet packet = null;
		switch (type) {
		default:
		case INVALID:
			break;
		case LOGIN:
			packet = new Packet00Login(data);
			System.out.println("[" + address.getHostAddress() + ":" + port + "] "
					+ ((Packet00Login) packet).getUsername() + " has connected...");
			PlayerMP player = new PlayerMP(game.level, ((Packet00Login) packet).getX(), ((Packet00Login) packet).getY(), ((Packet00Login) packet).getUsername(),
					((Packet00Login) packet).getColour(), address, port);
			this.addConnection(player, (Packet00Login) packet);
			break;
		case DISCONNECT:
			packet = new Packet01Disconnect(data);
//			System.out.println("[" + address.getHostAddress() + ":" + port + "] "
//					+ ((Packet01Disconnect) packet).getUsername() + " has left...");
			this.removeConnection((Packet01Disconnect) packet);
			break;
		case MOVE:
			packet = new Packet02Move(data);
			this.handleMove(((Packet02Move) packet));
			break;
		case MINIONSPAWN:
			packet = new Packet03MinionSpawn(data);
			Minion minion = new Minion(game.level, ObjectID.Minion, ((Packet03MinionSpawn) packet).getMinionID(),
					((Packet03MinionSpawn) packet).getMinionX(), ((Packet03MinionSpawn) packet).getMinionY(),
					((Packet03MinionSpawn) packet).getMinionSpeed());
			this.addMinion(minion, (Packet03MinionSpawn) packet);
			break;
		case MINIONDESPAWN:
			packet = new Packet04MinionDespawn(data);
			removeMinion((Packet04MinionDespawn) packet);
			break;
		case MINIONMOVE:
			packet = new Packet05MinionMove(data);
			this.handleMinionMove(((Packet05MinionMove) packet));
			break;
		case BULLETSPAWN:
			packet = new Packet10BulletSpawn(data);
			Bullet bullet = new Bullet(game.level, ObjectID.Bullet, ((Packet10BulletSpawn) packet).getBulletID(),
					((Packet10BulletSpawn) packet).getBulletColour(), ((Packet10BulletSpawn) packet).getBulletX(),
					((Packet10BulletSpawn) packet).getBulletY(), 1);
			this.addBullet(bullet, (Packet10BulletSpawn) packet);
			break;
		case BULLETDESPAWN:
			packet = new Packet11BulletDespawn(data);
			removeBullet((Packet11BulletDespawn) packet);
			break;
		case BULLETMOVE:
			packet = new Packet12BulletMove(data);
			this.handleBulletMove(((Packet12BulletMove) packet));
			break;
		case INCREASESCORE:
			packet = new Packet20IncreaseScore(data);
			this.handleScoring(((Packet20IncreaseScore) packet));
			break;

		}
	}
	
	public void sendData(byte[] data, InetAddress ipAddress, int port) {
		if (!game.isApplet) {

			DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
			try {
				this.socket.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void sendDataToAllClients(byte[] data) {
		for (int i = 0; i < connectedPlayers.size(); i++) {
			PlayerMP p = connectedPlayers.get(i);
			sendData(data, p.ipAddress, p.port);
		}
	}
	
	public void addConnection(PlayerMP player, Packet00Login packet) {
		boolean alreadyConnected = false;
		for (int i = 0; i < connectedPlayers.size(); i++) {
			PlayerMP p = connectedPlayers.get(i);
			if (player.getUsername().equalsIgnoreCase(p.getUsername())) {
				if (p.ipAddress == null) {
					p.ipAddress = player.ipAddress;
				}
				if (p.port == -1) {
					p.port = player.port;
				}
				alreadyConnected = true;
			} else {
				// relay to the current connected player that there is a new
				// player
				sendData(packet.getData(), p.ipAddress, p.port);

				// relay to the new player that the currently connect player
				// exists
                if(getPlayerMP(p.getUsername()).isAlive()) {
                	Packet00Login packetTemp = new Packet00Login(p.getUsername(), p.x, p.y,p.getColourString());
                    sendData(packetTemp.getData(), player.ipAddress, player.port);
                }
				// add existing minion to new connected client
				for (int j = 0; j < minionList.size(); j++) {
					Minion tempMinion = minionList.get(j);
					Packet03MinionSpawn minionPacket = new Packet03MinionSpawn(tempMinion.getMinionID(),
							tempMinion.getX(), tempMinion.getY(), tempMinion.getMinionSpeed());
					sendData(minionPacket.getData(), player.ipAddress, player.port);
				}
				// add existing bullet to new connected client
				for (int j = 0; j < bulletList.size(); j++) {
					Bullet tempBullet = bulletList.get(j);
					Packet10BulletSpawn bulletPacket = new Packet10BulletSpawn(tempBullet.getBulletID(),
							tempBullet.getX(), tempBullet.getY(), tempBullet.getBulletColourString());
					sendData(bulletPacket.getData(), player.ipAddress, player.port);
				}
			}
		}
		if (!alreadyConnected) {
			this.connectedPlayers.add(player);
		}
	}

	public void removeConnection(Packet01Disconnect packet) {
		PlayerMP p = getPlayerMP(packet.getUsername());
		p.setAlive(false);
		// this.connectedPlayers.remove(getPlayerMP(packet.getUsername()));
		packet.writeData(this);
	}
	
	private void handleMove(Packet02Move packet) {
		PlayerMP tempPlayer = getPlayerMP(packet.getUsername());

		if (tempPlayer != null) {
			// update connectedPlayer attr
			tempPlayer.x = packet.getX();
			tempPlayer.y = packet.getY();
			packet.writeData(this);
		}
	}

	private void handleScoring(Packet20IncreaseScore packet) {
		PlayerMP tempPlayer = getPlayerMP(packet.getUsername());
		if (tempPlayer != null) {
			// update connectedPlayer attr
			// tempPlayer.increaseScore();
			// System.out.println(tempPlayer.getUsername() + " " + tempPlayer.getScore());

			packet.writeData(this);
		}
	}

	public void addMinion(Minion minion, Packet03MinionSpawn packet) {
		minionList.add(minion);
		packet.writeData(this);
	}

	private void removeMinion(Packet04MinionDespawn packet) {
		this.minionList.remove(getMinion(packet.getMinionID()));
		packet.writeData(this);
	}

	private void handleMinionMove(Packet05MinionMove packet) {
		if (getMinion(packet.getMinionID()) != null) {
			Minion tempMinion = getMinion(packet.getMinionID());
			tempMinion.x = packet.getX();
			tempMinion.y = packet.getY();
			packet.writeData(this);
		}
	}

	public void addBullet(Bullet bullet, Packet10BulletSpawn packet) {
		bulletList.add(bullet);
		packet.writeData(this);
	}

	private void removeBullet(Packet11BulletDespawn packet) {
		this.bulletList.remove(getBullet(packet.getBulletID()));
		packet.writeData(this);
	}

	private void handleBulletMove(Packet12BulletMove packet) {
		if (getBullet(packet.getBulletID()) != null) {
			Bullet tempBullet = getBullet(packet.getBulletID());
			tempBullet.x = packet.getBulletX();
			tempBullet.y = packet.getBulletY();
			packet.writeData(this);
		}
	}

	public int getMinionID() {
		return minionID++;
	}
	
	public PlayerMP getPlayerMP(String username) {
		for (int i = 0; i < connectedPlayers.size(); i++) {
			PlayerMP player = connectedPlayers.get(i);
			if (player.getUsername().equals(username)) {
				return player;
			}
		}
		return null;
	}

	public Minion getMinion(int minionID) {
		for (int i = 0; i < minionList.size(); i++) {
			Minion minion = minionList.get(i);
			if (minion.getMinionID() == minionID) {
				return minion;
			}
		}
		return null;
	}

	public Bullet getBullet(String bulletID) {
		for (int i = 0; i < bulletList.size(); i++) {
			Bullet bullet = bulletList.get(i);
			if (bullet.getBulletID().equals(bulletID)) {
				return bullet;
			}
		}
		return null;
	}

}
