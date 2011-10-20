package status;

import lc.UserLC;

public class SteadyStatus implements Status {

	private byte status=StatusType.STEADY;
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
		//连续三次增长， increase in last three time.
		if(lc.getPaidChptIncre1()>0&&lc.getPaidChptIncre2()>0&&lc.getPaidChptIncre3()>0){
			lc.setStatus(StatusType.CRAZY);
			return Transfer.getStatusInstance(StatusType.CRAZY);
		}
		return Transfer.getStatusInstance(StatusType.STEADY);
	}

}
