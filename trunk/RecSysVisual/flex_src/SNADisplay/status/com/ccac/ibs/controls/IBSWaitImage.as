package SNADisplay.status.com.ccac.ibs.controls
{
	
	import SNADisplay.status.com.ccac.ibs.skins.common.IBSWaitIconSkin;
	
	import flash.display.DisplayObject;
	
	import mx.core.UIComponent;
	import mx.events.ResizeEvent;
	import mx.skins.ProgrammaticSkin;
	
	public class IBSWaitImage extends UIComponent
	{
		
		public function IBSWaitImage()
		{
			super();
			this.width = this.measuredWidth;
			this.height = this.measuredHeight;
		}
		
		private var icon:DisplayObject = null;
		
		private var state:int = 0;
		
		override public function get measuredWidth():Number
		{
			return 16;
		}
		
		override public function get measuredHeight():Number
		{
			return 16;
		}
		
		public function update():void
		{
			state = state + 1;
			if (state > 7)
				state = 0;
			if (icon)
				icon.name = "state" + state;
			this.invalidateDisplayList();
		}
		
		override protected function createChildren():void
		{
			super.createChildren();
			icon = new IBSWaitIconSkin();
			icon.name = "state0"
			this.addChild(icon);
			setIconPosition();
			this.addEventListener(ResizeEvent.RESIZE, resizeHandler);
		}
		
		override protected function updateDisplayList(unscaledWidth:Number, 
				unscaledHeight:Number):void
		{
			super.updateDisplayList(unscaledWidth, unscaledHeight);
			if (icon)
				(icon as ProgrammaticSkin).validateDisplayList();
		}
		
		private function resizeHandler(event:ResizeEvent):void
		{
			setIconPosition();
		}
		
		private function setIconPosition():void
		{
			if (icon)
			{
				icon.x = (this.width - icon.width) / 2;
				icon.y = (this.height - icon.height) / 2;
			}
		}
		
	}
	
}