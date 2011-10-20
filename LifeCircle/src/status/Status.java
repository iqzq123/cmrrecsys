package status;
import lc.UserLC;


public interface  Status {
	
	public Status run(UserLC lc);
	public byte getStatus();
}
