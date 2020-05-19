package netprg.game.net.packets;



public class Packet05MinionMove extends Packet {
	private int minionID;
	private int x,y;
	
    public Packet05MinionMove(byte[] data) {
        super(05);
        String[] dataArray = readData(data).split(",");
        this.minionID = Integer.parseInt(dataArray[0]);
        this.x = Integer.parseInt(dataArray[1]);
        this.y = Integer.parseInt(dataArray[2]);
    }
    
    public Packet05MinionMove(int minionID, int x, int y) {
        super(05);
        this.minionID = minionID;
        this.x = x;
        this.y = y;
    }



	@Override
	public byte[] getData() {
        return ("05" + this.minionID + "," + this.x + "," + this.y ).getBytes();

	}
	
    public int getMinionID() {
        return minionID;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }


}
