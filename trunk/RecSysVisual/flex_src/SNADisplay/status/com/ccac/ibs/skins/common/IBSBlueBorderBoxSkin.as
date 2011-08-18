package SNADisplay.status.com.ccac.ibs.skins.common
{
	
	import flash.display.Graphics;
	
	import mx.graphics.RectangularDropShadow;
	import mx.skins.Border;
	
	public class IBSBlueBorderBoxSkin extends Border
	{
		
		public function IBSBlueBorderBoxSkin()
		{
			super();
		}
		
		private var dropShadow:RectangularDropShadow = null;
		
		override protected function updateDisplayList(w:Number, h:Number):void
		{
			var g:Graphics = this.graphics;
			var topHeight:Number = (h - 2) * 0.4;
			var backgroundColor:Number = this.getStyle("backgroundColor");
			if (isNaN(backgroundColor))
				backgroundColor = 0xFFFFFF;
			g.clear();
			g.lineStyle(0, 0x000000, 0);
			g.beginFill(0x6593CF, 1);
			g.drawRect(0, 0, w, h);
			g.endFill();
			g.beginFill(0xE3EFFC, 1);
			g.drawRect(1, 1, w - 2, topHeight);
			g.endFill();
			g.beginFill(0xBED7F8, 1);
			g.drawRect(1, topHeight + 1, w - 2, h - 2 - topHeight);
			g.endFill();
			g.beginFill(0xA3BAD9, 1);
			g.drawRect(3, 3, w - 6, h - 6);
			g.endFill();
			g.beginFill(backgroundColor, 1);
			g.drawRect(4, 4, w - 8, h - 8);
			g.endFill();
			if (getStyle("dropShadowEnabled") == true)
			{
				if (!dropShadow)
					dropShadow = new RectangularDropShadow();
				with (dropShadow)
				{
					distance = getStyle("shadowDistance");
					angle = 45;
					color = 0x000000;
					alpha = 0.3;
					tlRadius = 0;
					trRadius = 0;
					blRadius = 0;
					brRadius = 0;
				}
				dropShadow.drawShadow(g, 0, 0, w, h);
			}
		}
		
	}
	
}