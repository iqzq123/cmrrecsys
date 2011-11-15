package status;

import lc.GlobalValue;
import lc.UserLC;

public class ComfirmStatus implements Status {

	private byte status=StatusType.COMFIRM;
	public byte getStatus() {
		// TODO Auto-generated method stub
		return this.status;
	}


	public Status run(UserLC lc) {
		// TODO Auto-generated method stub
		// only confirm once
		if(lc.getPrePaidChptCnt()>0){
			lc.setStatus(StatusType.STEADY);
			return Transfer.getStatusInstance(StatusType.STEADY);
		}else if(lc.getPre3AverPV() < GlobalValue.pvThreshold & lc.getInterval1()>lc.getInterval2() & lc.getInterval2() > lc.getInterval3())
		{
			lc.setStatus(StatusType.HATE);
			return Transfer.getStatusInstance(StatusType.HATE);
		}
		else{
		
			lc.setStatus(StatusType.HESITATE);
			return Transfer.getStatusInstance(StatusType.HESITATE);
		}
		
	}

}
