package SNADisplay.org.utils.Events
{
	import flash.events.Event;

	public class ConfirmEvent extends Event
	{
		public static const CONFIRMTYPE:String = "comfirm";
		public var text:String;
		public var rsc:Boolean; //false本地,true远端
		public function ConfirmEvent(type:String, info:String, t:Boolean, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
			text = info;
			rsc = t;
		}
		
	}
}