package SNADisplay.org.utils.Events
{
	import flash.events.Event;

	public class NodeClickEvent extends Event
	{
		public static const SINGLE_CLICK:String = "singleClickEvent"; 
		public static const DOUBLE_CLICK:String = "doubleClickEvent";
		public var nodeId:String;
		public var nodeData:XML;
		public function NodeClickEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, true, cancelable);
		}
		
	}
}