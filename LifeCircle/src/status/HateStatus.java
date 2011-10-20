package status;

import lc.UserLC;

public class HateStatus implements Status {

	private byte status=StatusType.HATE;
	public byte getStatus() {
		// TODO Auto-generated method stub
		return this.status;
	}


	public Status run(UserLC lc) {
		// TODO Auto-generated method stub
		if(lc.getPre3AverPV()>Transfer.getPvThreshold()|lc.getPreFreeChptCnt()+lc.getPrePaidChptCnt()>0
				|(lc.getInterval1()<lc.getInterval2()&&lc.getInterval2()<lc.getInterval3())){
			lc.setStatus(StatusType.FAVOR);
			return Transfer.getStatusInstance(StatusType.FAVOR);
		}
		
		return Transfer.getStatusInstance(StatusType.HATE);
	}

}
