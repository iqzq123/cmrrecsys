package SNADisplay.status.com.ccac.ibs.controls
{
	
	import SNADisplay.status.com.ccac.ibs.skins.common.IBSSimpleProgressBarBackgroundSkin;
	import SNADisplay.status.com.ccac.ibs.skins.common.IBSSimpleProgressBarSkin;
	
	import flash.display.DisplayObject;
	import flash.events.TimerEvent;
	import flash.utils.Timer;
	
	import mx.core.IFlexDisplayObject;
	import mx.core.IProgrammaticSkin;
	import mx.core.IUIComponent;
	import mx.core.UIComponent;
	import mx.styles.ISimpleStyleClient;
	
	[Style(name="backgroundSkin", type="Class", inherit="no")]
	[Style(name="barSkin", type="Class", inherit="no")]
	
	public class IBSSimpleProgressBar extends UIComponent
	{
		
		public function IBSSimpleProgressBar()
		{
			super();
			setStyle("backgroundSkin", IBSSimpleProgressBarBackgroundSkin);
			setStyle("barSkin", IBSSimpleProgressBarSkin);
		}
		
		protected var timer:Timer = null;
		
		protected var background:IFlexDisplayObject = null;
		
		protected var bar:IFlexDisplayObject = null;
		
		private var _marqueeMode:Boolean = false;
		private var marqueeModeChanged:Boolean = false;
		
		public function get marqueeMode():Boolean
		{
			return _marqueeMode;
		}
		
		public function set marqueeMode(value:Boolean):void
		{
			_marqueeMode = value;
			marqueeModeChanged = true;
			invalidateProperties();
			invalidateSize();
			invalidateDisplayList();
		}
		
		private var _delay:Number = 50;
		private var delayChanged:Boolean = false;
		
		public function get delay():Number
		{
			return _delay;
		}
		
		public function set delay(value:Number):void
		{
			_delay = value;
			delayChanged = true;
			invalidateProperties();
			invalidateSize();
			invalidateDisplayList();
		}
		
		private var _value:Number = 0;
		
		public function get value():Number
		{
			return _value;
		}
		
		public function set value(v:Number):void
		{
			_value = v;
			invalidateSize();
			invalidateDisplayList();
		}
		
		private var _blockGap:Number = 0.2;
		
		public function get blockGap():Number
		{
			return _blockGap;
		}
		
		public function set blockGap(value:Number):void
		{
			_blockGap = value;
			invalidateSize();
			invalidateDisplayList();
		}
		
		override protected function createChildren():void
		{
			super.createChildren();
			if (!timer)
			{
				timer = new Timer(_delay);
				timer.addEventListener(TimerEvent.TIMER, timerHandler);
			}
			createBackground();
			createBar();
		}
		
		override protected function commitProperties():void
		{
			super.commitProperties();
			if (marqueeModeChanged || delayChanged)
			{
				if (!timer)
				{
					timer = new Timer(_delay);
					timer.addEventListener(TimerEvent.TIMER, timerHandler);
				}
				if (marqueeModeChanged)
				{
					marqueeModeChanged = false;
					if (_marqueeMode)
					{
						if (!timer.running)
							timer.start();
					}
					else
					{
						if (timer.running)
							timer.stop();
					}
				}
				if (delayChanged)
				{
					delayChanged = false;
					timer.delay = _delay;
				}
			}
		}
		
		override protected function updateDisplayList(unscaledWidth:Number, 
				unscaledHeight:Number):void
		{
			super.updateDisplayList(unscaledWidth, unscaledHeight);
			graphics.clear();
			if (background)
			{
				background.setActualSize(unscaledWidth, unscaledHeight);
				background.move(0, 0);	
				if (background is IProgrammaticSkin)
					(background as IProgrammaticSkin).validateDisplayList();
			}
			var barContainerWidth:Number = Math.max(unscaledWidth - 7, 0);
			var barContainerHeight:Number = Math.max(unscaledHeight - 6, 0);
			var barContainerX:Number = 4;
			var barContainerY:Number = 3;
			var barWidth:Number = 0;
			var barX:Number = 0;
			var barName:String = null;
			if (_marqueeMode)
			{
				barWidth = Math.round(barContainerWidth / 3);
				barX = (barWidth + barContainerWidth) * _value / 100 - barWidth;
				if (barX < 0)
				{
					barName = "bar_right";
					barWidth = barWidth + barX;
					barX = 0;
				}
				else if (barX + barWidth > barContainerWidth)
				{
					barName = "bar_left";
					barWidth = barContainerWidth - barX;
				}
				else
				{
					barName = "bar_center";
				}
			}
			else
			{
				barWidth = Math.round(barContainerWidth * _value / 100);
				barX = 0;
				barName = "bar";
			}
			if (bar)
			{
				bar.name = barName + "_" + Number((isNaN(_blockGap) ? 0.2 : blockGap));
				bar.move(barContainerX + barX, barContainerY);
				bar.setActualSize(barWidth, barContainerHeight);
				setChildIndex(bar as DisplayObject, numChildren - 1);
				if (bar is IProgrammaticSkin)
					(bar as IProgrammaticSkin).validateDisplayList();
			}
		}
		
		override public function styleChanged(styleProp:String):void
		{
			var allStyles:Boolean = (styleProp == null || styleProp == "styleName");
			super.styleChanged(styleProp);
			if (allStyles || styleProp == "backgroundSkin")
			{
				if (background)
				{
					removeChild(background as DisplayObject);
					background = null;
				}
				createBackground();
			}
			if (allStyles || styleProp == "barSkin")
			{
				if (bar)
				{
					removeChild(bar as DisplayObject);
					bar = null;
				}
				createBar();
			}
		}
		
		protected function createBackground():void
		{
			if (!background)
			{
				var backgroundClass:Class = getStyle("backgroundSkin");
				if (backgroundClass != null)
				{
					background = new backgroundClass();
					background.name = "background";
					if (background is IUIComponent)
						IUIComponent(background).enabled = enabled;
					if (background is ISimpleStyleClient)
						ISimpleStyleClient(background).styleName = this;
					addChild(DisplayObject(background));
					invalidateDisplayList();
				}
			}
		}
		
		protected function createBar():void
		{
			if (!bar)
			{
				var barClass:Class = getStyle("barSkin");
				if (barClass != null)
				{
					bar = new barClass();
					background.name = "bar";
					if (bar is IUIComponent)
						IUIComponent(bar).enabled = enabled;
					if (bar is ISimpleStyleClient)
						ISimpleStyleClient(bar).styleName = this;
					addChild(DisplayObject(bar));
					invalidateDisplayList();
				}
			}
		}
		
		public function setProgress(loaded:Number, total:Number):void
		{
			if (isNaN(loaded))
				loaded = 0;
			if (isNaN(total))
				total = 100;
			if (total <= 0)
				total = 100;
			loaded = (loaded < 0 ? 0 : (loaded > total ? total : loaded));
			value = loaded / total * 100;
		}
		
		private function timerHandler(event:TimerEvent):void
		{
			if (_value < 100)
				value = _value + 5;
			else
				value = 0;
		}
		
	}
	
}