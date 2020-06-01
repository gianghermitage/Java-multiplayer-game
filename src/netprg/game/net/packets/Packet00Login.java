package netprg.game.net.packets;

public class Packet00Login extends Packet {

	private String username;
	private int x, y;
	private String colour;

	public Packet00Login(byte[] data) {
		super(00);
		String[] dataArray = readData(data).split(",");
		this.username = dataArray[0];
		this.x = Integer.parseInt(dataArray[1]);
		this.y = Integer.parseInt(dataArray[2]);
		this.colour = dataArray[3];

	}

	public Packet00Login(String username, int x, int y, String colour) {
		super(00);
		this.username = username;
		this.x = x;
		this.y = y;
		this.colour = colour;
	}

	@Override
	public byte[] getData() {
		return ("00" + this.username + "," + x + "," + y + "," + colour).getBytes();
	}

	public String getUsername() {
		return username;
	}

	public String getColour() {
		return colour;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

}
