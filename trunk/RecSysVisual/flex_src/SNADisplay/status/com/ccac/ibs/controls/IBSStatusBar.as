package SNADisplay.status.com.ccac.ibs.controls
{
	
	import mx.containers.HBox;
	import mx.core.UIComponent;
	import mx.core.mx_internal;
	
	use namespace mx_internal;
	
	public class IBSStatusBar extends HBox
	{
		
		public function IBSStatusBar()
		{
			super();
			horizontalScrollPolicy = "off";
			verticalScrollPolicy = "off";
		}
		
		override public function get horizontalScrollPolicy():String
		{
			return "off";
		}
		
		override public function set horizontalScrollPolicy(value:String):void
		{
			super.horizontalScrollPolicy = "off";
		}
		
		override public function get verticalScrollPolicy():String
		{
			return "off";
		}
		
		override public function set verticalScrollPolicy(value:String):void
		{
			super.verticalScrollPolicy = "off";
		}
		
		[ArrayElementType("Object")]
		private var _panelConfig:Array = null;
		
		public function get panelConfig():Array
		{
			return _panelConfig;
		}
		
		public function set panelConfig(value:Array):void
		{
			_panelConfig = value;
			createPanels(value);
		}
		
		override protected function updateDisplayList(unscaledWidth:Number, 
				unscaledHeight:Number):void
		{
			super.updateDisplayList(unscaledWidth, unscaledHeight);
			var paddingLeft:Number = getStyle("paddingLeft");
			var paddingRight:Number = getStyle("paddingRight");
			var paddingTop:Number = getStyle("paddingTop");
			var paddingBottom:Number = getStyle("paddingBottom");
			paddingLeft = (isNaN(paddingLeft) ? 0 : paddingLeft);
			paddingRight = (isNaN(paddingLeft) ? 0 : paddingRight);
			paddingTop = (isNaN(paddingLeft) ? 0 : paddingTop);
			paddingBottom = (isNaN(paddingLeft) ? 0 : paddingBottom);
			layoutObject.updateDisplayList(unscaledWidth - paddingLeft - paddingRight, 
					unscaledHeight - paddingTop - paddingBottom);
			for (var i:int = 0; i < numChildren; i++)
			{
				var child:UIComponent = getChildAt(i) as UIComponent;
				if (child)
				{
					child.move(child.x + paddingLeft, child.y + paddingTop);
					child.height = Math.max(unscaledHeight - paddingTop - paddingBottom, 0);
				}
			}
		}
		
		public function get panelCount():int
		{
			return numChildren;
		}
		
		public function panels(index:int):IBSStatusPanel
		{
			var panel:IBSStatusPanel = null;
			var array:Array = [];
			for (var i:int = 0; i < numChildren; i++)
			{
				if (getChildAt(i) is IBSStatusPanel)
					array.push(getChildAt(i));
			}
			if (index < 0 || index > array.length - 1)
				return panel;
			return (array[index] as IBSStatusPanel);
		}
		
		public function createPanels(config:Array):void
		{
			removeAllChildren();
			if (!config)
				return;
			for (var i:int = 0; i < config.length; i++)
			{
				var item:Object = config[i] as Object;
				var percentWidth:Number = NaN;
				var width:Number = 100;
				var text:String = "";
				var icon:Class = null;
				var labelPlacement:String = "right";
				var textAlign:String = "left";
				var mode:String = "text";
				var marqueeMode:Boolean = false;
				var progressValue:Number = 0;
				var panel:IBSStatusPanel = null;
				if (!item)
					continue;
				if (item.hasOwnProperty("percentWidth"))
					percentWidth = item.percentWidth as Number;
				if (item.hasOwnProperty("width"))
					width = item.width as Number;
				if (item.hasOwnProperty("label"))
					text = item.label as String;
				if (item.hasOwnProperty("icon"))
					icon = item.icon as Class;
				if (item.hasOwnProperty("labelPlacement"))
					labelPlacement = item.labelPlacement as String;
				if (item.hasOwnProperty("textAlign"))
					textAlign = item.textAlign as String;
				if (item.hasOwnProperty("mode"))
					mode = item.mode as String;
				if (item.hasOwnProperty("marqueeMode"))
					marqueeMode = item.marqueeMode as Boolean;
				if (item.hasOwnProperty("progressValue"))
					progressValue = item.progressValue as Number;
				panel = new IBSStatusPanel();
				if (isNaN(percentWidth))
				{
					if (!isNaN(width))
						panel.width = width;
				}
				else
				{
					panel.width = NaN;
					panel.percentWidth = percentWidth;
				}
				panel.label = text;
				panel.icon = icon;
				panel.labelPlacement = labelPlacement;
				panel.textAlign = textAlign;
				panel.mode = mode;
				panel.marqueeMode = marqueeMode;
				panel.progressValue = progressValue;
				addChild(panel);
			}
			invalidateProperties();
			invalidateSize();
			invalidateDisplayList();
		}
		
	}
	
}