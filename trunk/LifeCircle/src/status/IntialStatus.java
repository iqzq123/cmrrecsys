package status;

import lc.GlobalValue;
import lc.UserLC;

public class IntialStatus implements Status {
	
	private byte status=StatusType.INITIAl;

	public byte getStatus() {
		// TODO Auto-generated method stub
		return this.status;
	}

	public Status run(UserLC lc) {
		// TODO Auto-generated method stub
		
		if(lc.getPreFee()>0|lc.getPrePaidChptCnt()>0){
			lc.setStatus(StatusType.COMFIRM);
			return Transfer.getStatusInstance(StatusType.COMFIRM);
		}
		if(lc.getPrePvCnt()>GlobalValue.pvThreshold|lc.getPreFreeChptCnt()>0|lc.getPreDLCnt()>0){
			lc.setStatus(StatusType.FAVOR);
			return Transfer.getStatusInstance(StatusType.FAVOR);
		}else{
			lc.setStatus(StatusType.HATE);
			return Transfer.getStatusInstance(StatusType.HATE);
		}
		
	} 

}
