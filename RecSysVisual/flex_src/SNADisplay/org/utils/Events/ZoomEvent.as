package SNADisplay.org.utils.Events
{
	import flash.events.Event;

	public class ZoomEvent extends Event
	{
		public static const ZOOM_EVENT:String = "zoomEvent"; 
		public var scale:Number;
		public function ZoomEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, true, cancelable);
		}
		
	}
}