package SNADisplay.org.utils.Events
{
	import flash.events.Event;
	
	public class DataSureEvent extends flash.events.Event
	{	
		public static var DATA_SURE: String = "datasure";
		public function DataSureEvent(type:String)
		{
			super(type);
		}
		public var bookID:String;
		
	}
}
