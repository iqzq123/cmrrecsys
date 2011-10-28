package status;

import lc.UserLC;

public class CrazyStatus implements Status {

	private byte status=StatusType.CRAZY;
	public byte getStatus() {
		// TODO Auto-generated method stub
		return this.status;
	}

	public Status run(UserLC lc) {
		// TODO Auto-generated method stub
		if(lc.getPrePaidChptCnt()==0){
			lc.setStatus(StatusType.HESITATE);
			return Transfer.getStatusInstance(StatusType.HESITATE);
		}
		if(lc.getPaidChptIncre1()+lc.getPaidChptIncre2()+lc.getPaidChptIncre3()>0){
			lc.setStatus(StatusType.STEADY);
			return Transfer.getStatusInstance(StatusType.STEADY);
		}
		return Transfer.getStatusInstance(StatusType.CRAZY);
	}

}
