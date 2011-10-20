package status;

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
		if(!lc.getStatusHis().contains("4")){
			lc.setStatus(StatusType.COMFIRM);
			return Transfer.getStatusInstance(StatusType.COMFIRM);
		}
		if(lc.getPrePaidChptCnt()>0){
			lc.setStatus(StatusType.STEADY);
			return Transfer.getStatusInstance(StatusType.STEADY);
		}else{
		
			lc.setStatus(StatusType.HESITATE);
			return Transfer.getStatusInstance(StatusType.HESITATE);
		}
		
	}

}
