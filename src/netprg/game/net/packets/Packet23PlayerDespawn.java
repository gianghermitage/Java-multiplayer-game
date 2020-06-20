package netprg.game.net.packets;

public class Packet23PlayerDespawn extends Packet {

	private String username;

	public Packet23PlayerDespawn(byte[] data) {
		super(23);
		this.username = readData(data);
	}

	public Packet23PlayerDespawn(String username) {
		super(23);
		this.username = username;
	}

	@Override
	public byte[] getData() {
		return ("23" + this.username).getBytes();
	}

	public String getUsername() {
		return username;
	}

}
