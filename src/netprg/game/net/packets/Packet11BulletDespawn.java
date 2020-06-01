package netprg.game.net.packets;

public class Packet11BulletDespawn extends Packet {
	private String bulletID;

	public Packet11BulletDespawn(byte[] data) {
		super(11);
		String dataArray = readData(data);
		this.bulletID = dataArray;

	}

	public Packet11BulletDespawn(String bulletID) {
		super(11);
		this.bulletID = bulletID;

	}

	@Override
	public byte[] getData() {
		return ("11" + bulletID).getBytes();

	}

	public String getBulletID() {
		return this.bulletID;
	}

}
