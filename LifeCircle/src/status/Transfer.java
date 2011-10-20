package status;

import lc.UserLC;

public  class Transfer {
	
	static private Status []statusArray=null;
	static private int pvThreshold=20;
	static public Status getStatusInstance(byte type){
		return statusArray[type];
	}
	public Transfer(){
		statusArray=new Status[8];
		statusArray[0]=new IntialStatus();
		statusArray[1]=new FavorStatus();
		statusArray[2]=new HateStatus();
		statusArray[3]=new HesitateStatus();
		statusArray[4]=new ComfirmStatus();
		statusArray[5]=new SteadyStatus();
		statusArray[6]=new CrazyStatus();
		statusArray[7]=new LapsedStatus();
	}
	static public void initial(){
		statusArray=new Status[8];
		statusArray[0]=new IntialStatus();
		statusArray[1]=new FavorStatus();
		statusArray[2]=new HateStatus();
		statusArray[3]=new HesitateStatus();
		statusArray[4]=new ComfirmStatus();
		statusArray[5]=new SteadyStatus();
		statusArray[6]=new CrazyStatus();
		statusArray[7]=new LapsedStatus();
	}
	static public byte getStatus(UserLC lc){
		
		Status curStatus=statusArray[lc.getStatus()];
		Status nextStatus=null;
		do{
			curStatus=statusArray[lc.getStatus()];
			nextStatus=curStatus.run(lc);
			
		}while(nextStatus.getStatus()!=curStatus.getStatus());
		
		return curStatus.getStatus();
	}
	public static int getPvThreshold() {
		return pvThreshold;
	}
	public static void setPvThreshold(int pvThreshold) {
		Transfer.pvThreshold = pvThreshold;
	}
}
