package SNADisplay.status.com.ccac.ibs.controls
{
	
	import flash.display.DisplayObject;
	import flash.events.TimerEvent;
	import flash.text.TextLineMetrics;
	import flash.utils.Timer;
	
	import mx.controls.Button;
	import mx.core.IFlexDisplayObject;
	import mx.core.IProgrammaticSkin;
	import mx.core.IUIComponent;
	import mx.core.UITextField;
	import mx.core.mx_internal;
	import mx.styles.ISimpleStyleClient;
	
	use namespace mx_internal;
	
	[Style(name="separatorSkin", type="Class", inherit="no")]
	
	public class IBSStatusPanel extends Button
	{
		
		public function IBSStatusPanel()
		{
			super();
			focusEnabled = false;
		}
		
		private var timer:Timer = null;
		
		private var waitImage:IBSWaitImage = null;
		
		private var progressBar:IBSSimpleProgressBar = null;
		
		private var separator:IFlexDisplayObject = null;
		
		private var _mode:String = IBSStatusPanelMode.TEXT;
		private var modeChanged:Boolean = false;
		
		override public function set label(value:String):void
		{
			if (value == null || value == "")
				value = " ";
			super.label = value;
		}
		
		override public function get focusEnabled():Boolean
		{
			return false;
		}
		
		override public function set focusEnabled(value:Boolean):void
		{
			super.focusEnabled = false;
		}
		
		public function get mode():String
		{
			return _mode;
		}
		
		public function set mode(value:String):void
		{
			_mode = value;
			modeChanged = true;
			invalidateProperties();
			invalidateSize();
			invalidateDisplayList();
		}
		
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
		
		private var _progressValue:Number = 0;
		private var progressValueChanged:Boolean = false;
		
		public function get progressValue():Number
		{
			return _progressValue;
		}
		
		public function set progressValue(value:Number):void
		{
			_progressValue = value;
			progressValueChanged = true;
			invalidateProperties();
			invalidateSize();
			invalidateDisplayList();
		}
		
		public function get icon():Class
		{
			return getStyle("icon");
		}
		
		public function set icon(value:Class):void
		{
			setStyle("icon", value);
		}
		
		public function get textAlign():String
		{
			return getStyle("textAlign");
		}
		
		public function set textAlign(value:String):void
		{
			setStyle("textAlign", value);
		}
		
		override protected function createChildren():void
		{
			super.createChildren();
			createTimer();
			createWaitImage();
			createProgressBar();
			createSeparator();
		}
		
		override protected function commitProperties():void
		{
			super.commitProperties();
			if (modeChanged)
			{
				modeChanged = false;
				createTimer();
				createWaitImage();
				createProgressBar();
				createSeparator();
			}
			if (marqueeModeChanged)
			{
				marqueeModeChanged = false;
				createProgressBar();
				progressBar.marqueeMode = _marqueeMode;
			}
			if (progressValueChanged)
			{
				progressValueChanged = false;
				createProgressBar();
				progressBar.value = _progressValue;
			}
		}
		
		override protected function updateDisplayList(unscaledWidth:Number, 
				unscaledHeight:Number):void
		{
			super.updateDisplayList(unscaledWidth, unscaledHeight);
			switch (_mode)
			{
				case (IBSStatusPanelMode.WAITING):
					textField.visible = true;
					if (currentIcon)
						currentIcon.visible = false;
					timer.start();
					waitImage.visible = true;
					progressBar.visible = false;
					setChildIndex(waitImage, numChildren - 1);
					setChildIndex(textField as DisplayObject, numChildren - 1);
					break;
				case (IBSStatusPanelMode.PROGRESSBAR):
					textField.visible = false;
					if (currentIcon)
						currentIcon.visible = false;
					timer.stop();
					waitImage.visible = false;
					progressBar.visible = true;
					setChildIndex(progressBar, numChildren - 1);
					break;
				default:
					textField.visible = true;
					if (currentIcon)
						currentIcon.visible = true;
					timer.stop();
					waitImage.visible = false;
					progressBar.visible = false;
					if (currentIcon)
						setChildIndex(currentIcon as DisplayObject, numChildren - 1);
					setChildIndex(textField as DisplayObject, numChildren - 1);
					break;
			}
			var paddingLeft:Number = getStyle("paddingLeft");
			var paddingRight:Number = getStyle("paddingRight");
			var paddingTop:Number = getStyle("paddingTop");
			var paddingBottom:Number = getStyle("paddingBottom");
			var horizontalGap:Number = getStyle("horizontalGap");
			if (_mode == IBSStatusPanelMode.WAITING)
			{
				if (waitImage)
				{
					var metrics:TextLineMetrics = null;
					var textWidth:Number = 0;
					var textHeight:Number = 0;
					metrics = (label ? measureText(label) : measureText("Wj"));
					textWidth = metrics.width + TEXT_WIDTH_PADDING;
					textHeight = metrics.height + UITextField.TEXT_HEIGHT_PADDING;
					waitImage.setActualSize(waitImage.measuredWidth, waitImage.measuredHeight);
					waitImage.move(paddingLeft, paddingTop + (unscaledHeight - 
							paddingTop - paddingBottom - waitImage.height) / 2);
					textField.setActualSize(Math.max(Math.min(unscaledWidth - 
							paddingLeft - paddingRight - waitImage.width - horizontalGap, 
							textWidth), 0), textHeight);
					textField.move(waitImage.x + waitImage.width + horizontalGap, paddingTop + 
							(unscaledHeight - paddingTop - paddingBottom - textField.height) / 2 + 1);
					waitImage.invalidateDisplayList();
				}
			}
			else if (_mode == IBSStatusPanelMode.PROGRESSBAR)
			{
				if (progressBar)
				{
					progressBar.setActualSize(Math.max(unscaledWidth - paddingLeft - paddingRight, 0), 
							Math.max(unscaledHeight - paddingTop - paddingBottom, 0));
					progressBar.move(paddingLeft, paddingTop);
					progressBar.invalidateDisplayList();
				}
			}
			if (separator)
			{
				setChildIndex(separator as DisplayObject, numChildren - 1);
				separator.setActualSize(separator.measuredWidth, unscaledHeight);
				separator.move(unscaledWidth - separator.measuredWidth, 0);
				if (separator is IProgrammaticSkin)
					(separator as IProgrammaticSkin).validateDisplayList();
			}
		}
		
		override public function styleChanged(styleProp:String):void
		{
			var allStyles:Boolean = (styleProp == null || styleProp == "styleName");
			super.styleChanged(styleProp);
			if (allStyles || styleProp == "separatorSkin")
			{
				if (separator)
				{
					removeChild(separator as DisplayObject);
					separator = null;
				}
				createSeparator();
			}
		}
		
		protected function createTimer():void
		{
			if (!timer)
			{
				timer = new Timer(200);
				timer.addEventListener(TimerEvent.TIMER, timerHandler);
			}
		}
		
		protected function createWaitImage():void
		{
			if (!waitImage)
			{
				waitImage = new IBSWaitImage();
				addChild(waitImage);
			}
		}
		
		protected function createProgressBar():void
		{
			if (!progressBar)
			{
				progressBar = new IBSSimpleProgressBar();
				addChild(progressBar);
			}
		}
		
		protected function createSeparator():void
		{
			if (!separator)
			{
				var separatorClass:Class = getStyle("separatorSkin");
				if (separatorClass != null)
				{
					separator = new separatorClass();
					separator.name = "separator";
					if (separator is IUIComponent)
						IUIComponent(separator).enabled = enabled;
					if (separator is ISimpleStyleClient)
						ISimpleStyleClient(separator).styleName = this;
					addChild(DisplayObject(separator));
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
			progressValue = loaded / total * 100;
		}
		
		private function timerHandler(event:TimerEvent):void
		{
			if (waitImage)
				waitImage.update();
		}
		
	}
	
}