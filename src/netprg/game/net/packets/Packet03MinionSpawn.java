package netprg.game.net.packets;

public class Packet03MinionSpawn extends Packet {
	private int minionID;
	private int x,y,speed;
	
	public Packet03MinionSpawn(byte[] data) {
        super(03);
        String[] dataArray = readData(data).split(",");
        this.minionID = Integer.parseInt(dataArray[0]);
        this.x = Integer.parseInt(dataArray[1]);
        this.y = Integer.parseInt(dataArray[2]);
        this.speed = Integer.parseInt(dataArray[3]);
	}
	
	public Packet03MinionSpawn(int minionID, int x, int y,int speed) {
        super(03);
        this.minionID = minionID;
        this.x = x;
        this.y = y;
        this.speed = speed;
	}


	@Override
	public byte[] getData() {
        return ("03" + minionID + "," + x + "," + y + "," + speed).getBytes();

	}
	
	public int getMinionID() {
		return this.minionID;
	}
	
	public int getMinionX() {
		return this.x;
	}
	public int getMinionY() {
		return this.y;
	}
	public int getMinionSpeed() {
		return this.speed;
	}

}
