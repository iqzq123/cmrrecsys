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
		if(lc.getPrePaidChptCnt() > 0 || lc.getPreFee() > 0)
		{
			int index1 = lc.getStatusHis().indexOf(new StringBuilder().append(StatusType.COMFIRM).charAt(0));
			int index2 = lc.getStatusHis().indexOf(new StringBuilder().append(StatusType.HATE).charAt(0));
			if(index1 == -1 || index1 < index2)
			{
				lc.setStatus(StatusType.COMFIRM);
				return Transfer.getStatusInstance(StatusType.COMFIRM);
			}
			else
			{
				lc.setStatus(StatusType.STEADY);
				return Transfer.getStatusInstance(StatusType.STEADY);
			}
		}
		if(lc.getPre3AverPV()<GlobalValue.pvThreshold && lc.getPreFreeChptCnt() == 0 && lc.getPreDLCnt() == 0){
			lc.setStatus(StatusType.HATE);
			return Transfer.getStatusInstance(StatusType.HATE);
		}
		return Transfer.getStatusInstance(StatusType.HESITATE);
	}

}
