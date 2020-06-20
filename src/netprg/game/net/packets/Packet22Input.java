package netprg.game.net.packets;

public class Packet22Input extends Packet {

	private String username;
	private String direction;


	public Packet22Input(byte[] data) {
		super(22);
		String[] dataArray = readData(data).split(",");
		this.username = dataArray[0];
		this.direction = dataArray[1];

	}

	public Packet22Input(String username, String direction) {
		super(22);
		this.username = username;
		this.direction = direction;
	}

	@Override
	public byte[] getData() {
		return ("22" + this.username + "," + this.direction).getBytes();

	}

	public String getUsername() {
		return username;
	}

	public String getDirection() {
		return direction;
	}

}