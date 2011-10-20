package status;

import lc.GlobalValue;
import lc.UserLC;

public class FavorStatus implements Status {
	private byte status=StatusType.FAVOR;
	public byte getStatus() {
		// TODO Auto-generated method stub
		return this.status;
	}

	public Status run(UserLC lc) {
		// TODO Auto-generated method stub
		if(lc.getFeeAomut()>0|lc.getPrePaidChptCnt()>0){
			lc.setStatus(StatusType.COMFIRM);
			return Transfer.getStatusInstance(StatusType.COMFIRM);
		}
		int averPV=0;
		if(lc.getVisitAmout()>3){
			averPV+=lc.getPrePvCnt();
			int prePV1=lc.getPrePvCnt()-lc.getPvIncre1();
			int prePV2=prePV1-lc.getPvIncre2();
			int prePV3=prePV2-lc.getPvIncre3();
			averPV+=prePV1;
			averPV+=prePV2;
			averPV+=prePV3;
			averPV=averPV/4;
			if(averPV>GlobalValue.pvThreshold|lc.getFreeChptAmout()>0){
				lc.setStatus(StatusType.HESITATE);
				return Transfer.getStatusInstance(StatusType.HESITATE);
			}else{
				lc.setStatus(StatusType.HATE);
				return Transfer.getStatusInstance(StatusType.HATE);
			}
		}
		
		 return Transfer.getStatusInstance(StatusType.FAVOR);
	}

}
