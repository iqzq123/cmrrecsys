package SNADisplay.org.utils
{
	import flash.events.Event;
	import flash.events.MouseEvent;
	
	import mx.containers.Panel;
	import mx.core.SpriteAsset;
	import mx.core.UIComponent;
	import mx.events.FlexEvent;
	import mx.styles.StyleManager;

	public class DragPanel extends Panel
	{
		// Add the creationCOmplete event handler.
		public function DragPanel()
		{
			super();
			addEventListener(FlexEvent.CREATION_COMPLETE, creationCompleteHandler);
		}
		// Expose the title bar property for draggin and dropping.
		[Bindable]
		public var myTitleBar:UIComponent ;
					
		private function creationCompleteHandler(event:Event):void
		{
			this.setStyle("titleBackgroundSkin",StyleManager.getStyleDeclaration(".titWindow").getStyle("titleBackgroundSkin"));
			this.isPopUp = true;
			includeInLayout = true;
			myTitleBar = titleBar;			
			// Add the resizing event handler.	
			addEventListener(MouseEvent.MOUSE_DOWN, resizeHandler);
		}

		protected var minShape:SpriteAsset;
		protected var restoreShape:SpriteAsset;

		override protected function createChildren():void
		{
				super.createChildren();
			
			// Create the SpriteAsset's for the min/restore icons and 
			// add the event handlers for them.
			minShape = new SpriteAsset();
			minShape.addEventListener(MouseEvent.MOUSE_DOWN, minPanelSizeHandler);
			titleBar.addChild(minShape);

			restoreShape = new SpriteAsset();
			restoreShape.addEventListener(MouseEvent.MOUSE_DOWN, restorePanelSizeHandler);
			titleBar.addChild(restoreShape);
		}
			
		override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void
		{
			super.updateDisplayList(unscaledWidth, unscaledHeight);
			
			// Create invisible rectangle to increase the hit area of the min icon.
			minShape.graphics.clear();
			minShape.graphics.lineStyle(0, 0, 0);
			minShape.graphics.beginFill(0xFFFFFF, 0.0);
			minShape.graphics.drawRect(unscaledWidth - 35, 12, 8, 8);

			// Draw min icon.
			minShape.graphics.lineStyle(2);
			minShape.graphics.beginFill(0xFFFFFF, 0.0);
			minShape.graphics.drawRect(unscaledWidth - 35, 18, 8, 2);
				
			// Draw restore icon.
			restoreShape.graphics.clear();
			restoreShape.graphics.lineStyle(2);
			restoreShape.graphics.beginFill(0xFFFFFF, 0.0);
			restoreShape.graphics.drawRect(unscaledWidth - 20, 12, 8, 8);
			restoreShape.graphics.moveTo(unscaledWidth - 20, 15);
			restoreShape.graphics.lineTo(unscaledWidth - 12, 15);

			// Draw resize graphics if not minimzed.				
			graphics.clear()
			if (isMinimized == false)
			{
				graphics.lineStyle(2);
				graphics.moveTo(unscaledWidth - 6, unscaledHeight - 1)
				graphics.curveTo(unscaledWidth - 3, unscaledHeight - 3, unscaledWidth - 1, unscaledHeight - 6);						
				graphics.moveTo(unscaledWidth - 6, unscaledHeight - 4)
				graphics.curveTo(unscaledWidth - 5, unscaledHeight - 5, unscaledWidth - 4, unscaledHeight - 6);						
			}
		}
					
		public var myRestoreHeight:int;
		public var myRestoreWidth:int;
		public var myRestoreX:int;
		public var myRestoreY:int;
		public var isMinimized:Boolean = false; 
		
		import mx.core.Application;
					
		// Minimize panel event handler.
		private function minPanelSizeHandler(event:Event):void
		{
			if (isMinimized != true)
			{
				myRestoreHeight = height;	
				myRestoreWidth = width;
				myRestoreX = x;
				myRestoreY = y;
				height = titleBar.height;
				width = 100;
				//x = Application.application.width - titleBar.width;
				x = Application.application.width - width;
				y = Application.application.height - height;
				isMinimized = true;	
				// Don't allow resizing when in the minimized state.
				removeEventListener(MouseEvent.MOUSE_DOWN, resizeHandler);
			}				
		}
		
		// Restore panel event handler.
		private function restorePanelSizeHandler(event:Event):void
		{
			if (isMinimized == true)
			{
				height = myRestoreHeight;
				width = myRestoreWidth;
				x = myRestoreX;
				y = myRestoreY;
				isMinimized = false;	
				// Allow resizing in restored state.				
				addEventListener(MouseEvent.MOUSE_DOWN, resizeHandler);
			}
		}

		// Define static constant for event type.
		public static const RESIZE_CLICK:String = "resizeClick";

		protected var initX:Number;
		protected var initY:Number;
		// Resize panel event handler.
		import mx.core.Application;
		public  function resizeHandler(event:MouseEvent):void
		{
			// Determine if the mouse pointer is in the lower right 7x7 pixel
			// area of the panel. Initiate the resize if so.
			
			// Lower left corner of panel
			var lowerLeftX:Number = x + width; 
			var lowerLeftY:Number = y + height;
				
			// Upper left corner of 7x7 hit area
			var upperLeftX:Number = lowerLeftX-7;
			var upperLeftY:Number = lowerLeftY-7;
				
			// Mouse positionin Canvas
			var panelRelX:Number = event.localX + x;
			var panelRelY:Number = event.localY + y;

			// See if the mousedown is in the lower right 7x7 pixel area
			// of the panel.
			if (upperLeftX <= panelRelX && panelRelX <= lowerLeftX)
			{
				if (upperLeftY <= panelRelY && panelRelY <= lowerLeftY)
				{		
					event.stopPropagation();	
					initX = event.stageX;
					initY = event.stageY;	
//					var rbEvent:MouseEvent = new MouseEvent(RESIZE_CLICK, true);
					// Pass stage coords to so all calculations using global coordinates.
					systemManager.addEventListener(MouseEvent.MOUSE_MOVE, mouseMoveHandler, true);
					systemManager.addEventListener(MouseEvent.MOUSE_UP, mouseUpHandler, true);
				}
			}				
		}
		protected function mouseMoveHandler(event:MouseEvent):void
			{
					event.stopImmediatePropagation();		
					this.height = this.height + event.stageY - initY;  
					this.width = this.width + event.stageX - initX;
					initX = event.stageX;
					initY = event.stageY;
					if (this.height <= 50)
					{
						this.height = 50;  
					}
					if (this.width <= 150)
					{
						this.width = 150;				
					}					
			}
			
			// Sizes the panel to the size of the rubber band when the 
			// user releases the mouse key. 
			// Also removes the event handlers from the SystemManager.
			protected function mouseUpHandler(event:MouseEvent):void
			{
				event.stopImmediatePropagation();		
				systemManager.removeEventListener(MouseEvent.MOUSE_MOVE, mouseMoveHandler, true);
				systemManager.removeEventListener(MouseEvent.MOUSE_UP, mouseUpHandler, true	);
			}		
	}
}