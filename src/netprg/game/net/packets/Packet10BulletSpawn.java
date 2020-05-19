package netprg.game.net.packets;

public class Packet10BulletSpawn extends Packet {
	private String bulletID;
	private int x,y;
	private String colourString;
	

	public Packet10BulletSpawn(byte [] data) {
		super(10);
        String[] dataArray = readData(data).split(",");
        this.bulletID = dataArray[0];
        this.x = Integer.parseInt(dataArray[1]);
        this.y = Integer.parseInt(dataArray[2]);
        this.colourString = dataArray[3];

	}
	
	public Packet10BulletSpawn(String bulletID, int x , int y, String colourString) {
		super(10);
        this.bulletID = bulletID;
        this.x = x;
        this.y = y;
        this.colourString = colourString;
	}

	@Override
	public byte[] getData() {
		// TODO Auto-generated method stub
        return ("10" + bulletID + "," + x + "," + y + "," + colourString).getBytes();
	}
	
	public String getBulletID() {
		return this.bulletID;
	}
	
	public String getBulletColour() {
		return this.colourString;
	}
	
	
	public int getBulletX() {
		return this.x;
	}
	public int getBulletY() {
		return this.y;
	}

}
