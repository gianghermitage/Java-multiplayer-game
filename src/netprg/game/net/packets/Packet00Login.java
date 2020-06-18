package netprg.game.net.packets;

public class Packet00Login extends Packet {

	private String username;
	private int x, y;
	private String colour;
	private int isServer;

	public Packet00Login(byte[] data) {
		super(00);
		String[] dataArray = readData(data).split(",");
		this.username = dataArray[0];
		this.x = Integer.parseInt(dataArray[1]);
		this.y = Integer.parseInt(dataArray[2]);
		this.colour = dataArray[3];
		this.isServer = Integer.parseInt(dataArray[4]);
	}

	public Packet00Login(String username, int x, int y, String colour, int isServer) {
		super(00);
		this.username = username;
		this.x = x;
		this.y = y;
		this.colour = colour;
		this.isServer = isServer;
	}

	@Override
	public byte[] getData() {
		return ("00" + this.username + "," + x + "," + y + "," + colour + "," + isServer).getBytes();
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
	
	public int getServerStatus() {
		return isServer;
	}

}
