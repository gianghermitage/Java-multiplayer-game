package netprg.game.net.packets;

import netprg.game.net.GameClient;
import netprg.game.net.GameServer;

public abstract class Packet {

	public static enum PacketTypes {
		INVALID(-1), LOGIN(00), DISCONNECT(01), MOVE(02), MINIONSPAWN(03), MINIONDESPAWN(04), MINIONMOVE(05),
		BULLETSPAWN(10), BULLETDESPAWN(11), BULLETMOVE(12), INCREASESCORE(20), READY(21);

		private int packetId;

		private PacketTypes(int packetId) {
			this.packetId = packetId;
		}

		public int getId() {
			return packetId;
		}
	}

	public byte packetId;

	public Packet(int packetId) {
		this.packetId = (byte) packetId;
	}

	public String readData(byte[] data) {
		String message = new String(data).trim();
		return message.substring(2);
	}

	public void writeData(GameClient client) {
		client.sendData(getData());
	}

	public void writeData(GameServer server) {
		server.sendDataToAllClients(getData());
	}

	public abstract byte[] getData();

	public static PacketTypes lookupPacket(String packetId) {
		try {
			return lookupPacket(Integer.parseInt(packetId));
		} catch (NumberFormatException e) {
			return PacketTypes.INVALID;
		}
	}

	public static PacketTypes lookupPacket(int id) {
		for (PacketTypes p : PacketTypes.values()) {
			if (p.getId() == id) {
				return p;
			}
		}
		return PacketTypes.INVALID;
	}
}
