package netprg.game.net.packets;

public class Packet21Ready extends Packet {

	private String username;

	public Packet21Ready(byte[] data) {
		super(21);
		this.username = readData(data);
	}

	public Packet21Ready(String username) {
		super(21);
		this.username = username;
	}

	@Override
	public byte[] getData() {
		return ("21" + this.username).getBytes();
	}

	public String getUsername() {
		return username;
	}

}
