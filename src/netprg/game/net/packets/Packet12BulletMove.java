package netprg.game.net.packets;

public class Packet12BulletMove extends Packet {

	private String bulletID;
	private int x, y;

	public Packet12BulletMove(byte[] data) {
		super(12);
		String[] dataArray = readData(data).split(",");
		this.bulletID = dataArray[0];
		this.x = Integer.parseInt(dataArray[1]);
		this.y = Integer.parseInt(dataArray[2]);
	}

	public Packet12BulletMove(String bulletID, int x, int y) {
		super(12);
		this.bulletID = bulletID;
		this.x = x;
		this.y = y;
	}

	@Override
	public byte[] getData() {
		// TODO Auto-generated method stub
		return ("12" + bulletID + "," + x + "," + y).getBytes();
	}

	public String getBulletID() {
		return this.bulletID;
	}

	public int getBulletX() {
		return this.x;
	}

	public int getBulletY() {
		return this.y;
	}
}
