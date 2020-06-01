package netprg.game.net.packets;

public class Packet20IncreaseScore extends Packet {
	private String username;

	public Packet20IncreaseScore(byte[] data) {
		super(20);
		String[] dataArray = readData(data).split(",");
		this.username = dataArray[0];

	}

	public Packet20IncreaseScore(String username) {
		super(20);
		this.username = username;
	}

	@Override
	public byte[] getData() {
		// TODO Auto-generated method stub
		return ("20" + this.username).getBytes();
	}

	public String getUsername() {
		return this.username;
	}

}
