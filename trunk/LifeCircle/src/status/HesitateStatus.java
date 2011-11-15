package status;

import lc.GlobalValue;
import lc.UserLC;

public class HesitateStatus implements Status {

	private byte status=StatusType.HESITATE;
	public byte getStatus() {
		// TODO Auto-generated method stub
		return this.status;
	}

	public Status run(UserLC lc) {
		// TODO Auto-generated method stub
		if((!lc.getStatusHis().contains(String.valueOf(StatusType.COMFIRM)))&&lc.getPreFee()>0){
			lc.setStatus(StatusType.COMFIRM);
			return Transfer.getStatusInstance(StatusType.COMFIRM);
		}
		if(lc.getPrePaidChptCnt()>0){
			lc.setStatus(StatusType.STEADY);
			return Transfer.getStatusInstance(StatusType.STEADY);
		}
		if(lc.getPre3AverPV()<GlobalValue.pvThreshold&&(lc.getInterval1()>lc.getInterval2()&&lc.getInterval2()>lc.getInterval3())){
			lc.setStatus(StatusType.HATE);
			return Transfer.getStatusInstance(StatusType.HATE);
		}
		return Transfer.getStatusInstance(StatusType.HESITATE);
	}

}
