package status;

import lc.UserLC;

public class LapsedStatus implements Status {

	private byte status=StatusType.LAPSED;
	public byte getStatus() {
		// TODO Auto-generated method stub
		return this.status;
	}

	public Status run(UserLC lc) {
		// TODO Auto-generated method stub
		return Transfer.getStatusInstance(StatusType.INITIAl);
	}

}
