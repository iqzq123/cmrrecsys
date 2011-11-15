package status;

import lc.GlobalValue;
import lc.UserLC;

public class FavorStatus implements Status
{
	private byte status = StatusType.FAVOR;

	public byte getStatus()
	{
		// TODO Auto-generated method stub
		return this.status;
	}

	public Status run(UserLC lc)
	{
		// TODO Auto-generated method stub
		if (lc.getPreFee() > 0 | lc.getPrePaidChptCnt() > 0)
		{
			lc.setStatus(StatusType.COMFIRM);
			return Transfer.getStatusInstance(StatusType.COMFIRM);
		}

		if (lc.getPre3AverPV() > GlobalValue.pvThreshold
				| lc.getFreeChptAmout() > 0 | lc.getPreDLCnt() > 0)
		{
			lc.setStatus(StatusType.HESITATE);
			return Transfer.getStatusInstance(StatusType.HESITATE);
		} else
		{
			lc.setStatus(StatusType.HATE);
			return Transfer.getStatusInstance(StatusType.HATE);
		}
	}

}
