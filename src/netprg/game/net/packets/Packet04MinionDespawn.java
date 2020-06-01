package netprg.game.net.packets;

public class Packet04MinionDespawn extends Packet {

	private int minionID;

	public Packet04MinionDespawn(byte[] data) {
		super(04);
		String dataArray = readData(data);
		this.minionID = Integer.parseInt(dataArray);

	}

	public Packet04MinionDespawn(int minionID) {
		super(04);
		this.minionID = minionID;

	}

	@Override
	public byte[] getData() {
		return ("04" + minionID).getBytes();

	}

	public int getMinionID() {
		return this.minionID;
	}

}
