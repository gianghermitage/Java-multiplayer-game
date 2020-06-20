package netprg.game.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

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
import netprg.game.net.packets.Packet21Ready;
import netprg.game.net.packets.Packet22Input;
import netprg.game.net.packets.Packet23PlayerDespawn;

public class GameClient extends Thread {

	private InetAddress ipAddress;
	private DatagramSocket socket;
	private Game game;

	public GameClient(Game game, String ipAddress) {
		this.game = game;
		try {
			this.socket = new DatagramSocket();
			this.ipAddress = InetAddress.getByName(ipAddress);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
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
		PlayerMP playerMP = null;
		switch (type) {
		default:
		case INVALID:
			break;
		case LOGIN:
			packet = new Packet00Login(data);
			handleLogin((Packet00Login) packet, address, port);
			break;
		case DISCONNECT:
			packet = new Packet01Disconnect(data);
			playerMP = game.level.getPlayerMP(((Packet01Disconnect) packet).getUsername());
			playerMP.setAlive(false);
			game.level.getEntities().remove(playerMP);
			break;
		case MOVE:
			packet = new Packet02Move(data);
			handleMove((Packet02Move) packet);
			break;
		case PLAYERDESPAWN:
			packet = new Packet23PlayerDespawn(data);
			playerMP = game.level.getPlayerMP(((Packet23PlayerDespawn) packet).getUsername());
			playerMP.setAlive(false);
			break;
		case MINIONSPAWN:
			packet = new Packet03MinionSpawn(data);
			addMinion((Packet03MinionSpawn) packet);
			break;
		case MINIONDESPAWN:
			packet = new Packet04MinionDespawn(data);
			game.level.removeMinion(((Packet04MinionDespawn) packet).getMinionID());
			break;
		case MINIONMOVE:
			packet = new Packet05MinionMove(data);
			handleMinionMove(((Packet05MinionMove) packet));
			break;
		case BULLETSPAWN:
			packet = new Packet10BulletSpawn(data);
			addBullet((Packet10BulletSpawn) packet);
			break;
		case BULLETDESPAWN:
			packet = new Packet11BulletDespawn(data);
			game.level.removeBullet(((Packet11BulletDespawn) packet).getBulletID());
			break;
		case INCREASESCORE:
			packet = new Packet20IncreaseScore(data);
			handleScoring(((Packet20IncreaseScore) packet));
			break;
		case BULLETMOVE:
			packet = new Packet12BulletMove(data);
			handleBulletMove(((Packet12BulletMove) packet));
			break;
		case READY:
			packet = new Packet21Ready(data);
			handlePlayerReady(((Packet21Ready) packet));
			break;
		case INPUT:
			packet = new Packet22Input(data);
			handleInput(((Packet22Input) packet));

		}
	}

	public void sendData(byte[] data) {
		if (!game.isApplet) {
			DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, 1331);
			try {
				socket.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void handleLogin(Packet00Login packet, InetAddress address, int port) {
		PlayerMP player = new PlayerMP(game.level, packet.getX(), packet.getY(), packet.getUsername(),
				packet.getColour(), address, port);
		if (packet.getServerStatus() == 1) {
			player.setServer(true);
			player.setGameStart(true);
		}
		if (packet.getServerStatus() == 0) {
			player.setServer(false);
		}

		game.level.addEntity(player);
	}

	private void handleMove(Packet02Move packet) {
		this.game.level.movePlayer(packet.getUsername(), packet.getX(), packet.getY());
	}

	private void handleInput(Packet22Input packet) {
		// TODO Auto-generated method stub
		this.game.level.handleInput(packet.getUsername(), packet.getDirection());
	}

	private void handleScoring(Packet20IncreaseScore packet) {
		this.game.level.increaseScore(packet.getUsername());
	}

	private void handlePlayerReady(Packet21Ready packet) {
		this.game.level.playerReady(packet.getUsername());
	}

	private void addMinion(Packet03MinionSpawn packet) {
		Minion minion = new Minion(game.level, ObjectID.Minion, packet.getMinionID(), packet.getMinionX(),
				packet.getMinionY(), packet.getMinionSpeed());
		game.level.addEntity(minion);
	}

	private void addBullet(Packet10BulletSpawn packet) {
		Bullet bullet = new Bullet(game.level, ObjectID.Bullet, packet.getBulletID(), packet.getBulletColour(),
				packet.getBulletX(), packet.getBulletY(), 1);
		game.level.addEntity(bullet);
	}

	private void handleMinionMove(Packet05MinionMove packet) {
		this.game.level.moveMinion(packet.getMinionID(), packet.getX(), packet.getY());
	}

	private void handleBulletMove(Packet12BulletMove packet) {
		this.game.level.moveBullet(packet.getBulletID(), packet.getBulletX(), packet.getBulletY());
	}
}
