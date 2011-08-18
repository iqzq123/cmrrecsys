package SNADisplay.status.com.ccac.ibs.skins.common
{

	import flash.display.GradientType;	
	import flash.display.Graphics;
	import flash.geom.Matrix;
	
	import mx.skins.ProgrammaticSkin;
	
	public class IBSSimpleProgressBarBackgroundSkin extends ProgrammaticSkin
	{
		
		public function IBSSimpleProgressBarBackgroundSkin()
		{
			super();
		}
		
		override public function get measuredWidth():Number
		{
			return 90;
		}
		
		override public function get measuredHeight():Number
		{
			return 16;
		}
		
		override protected function updateDisplayList(w:Number, h:Number):void
		{
			var g:Graphics = graphics;
			var matrix:Matrix = new Matrix();
			g.clear();
			if (isNaN(w) || isNaN(h))
				return;
			g.lineStyle(0, 0x000000, 0);
			drawRect(g, 0xEFEFEF, 1, 2, 2, w - 3, h - 3);
			drawRect(g, 0xFFFFFF, 1, 3, 3, w - 5, h - 5);
			drawRect(g, 0xEFEFEF, 1, 3, 3, 1, 1);
			drawRect(g, 0xEFEFEF, 1, w - 3, 3, 1, 1);
			drawRect(g, 0xEFEFEF, 1, 3, h - 3, 1, 1);
			drawRect(g, 0xEFEFEF, 1, w - 3, h - 3, 1, 1);
			drawRect(g, 0xBEBEBE, 1, 2, 2, 1, 1);
			drawRect(g, 0xBEBEBE, 1, w - 2, 2, 1, 1);
			drawRect(g, 0xEFEFEF, 1, 2, h - 2, 1, 1);
			drawRect(g, 0x777777, 1, w - 2, h - 2, 1, 1);
			drawRect(g, 0xACABA7, 0.5, 1, 0, 1, 1);
			drawRect(g, 0xACABA6, 0.5, 0, 1, 1, 1);
			drawRect(g, 0x7F7E7D, 1, 2, 0, 1, 1);
			drawRect(g, 0x777777, 1, 1, 1, 1, 1);
			drawRect(g, 0x7F7E7D, 1, 0, 2, 1, 1);
			drawRect(g, 0xACABA7, 0.5, 0, h - 2, 1, 1);
			drawRect(g, 0xACABA6, 0.5, 1, h - 1, 1, 1);
			drawRect(g, 0x7F7E7D, 1, 0, h - 3, 1, 1);
			drawRect(g, 0x777777, 1, 1, h - 2, 1, 1);
			drawRect(g, 0x7F7E7D, 1, 2, h - 1, 1, 1);
			drawRect(g, 0xEFEFEF, 0.2, w - 1, 0, 1, 1);
			drawRect(g, 0xB3B3B3, 0.4, w - 1, 1, 1, 1);
			drawRect(g, 0x818181, 1, w - 1, 2, 1, 1);
			drawRect(g, 0xACABA6, 0.5, w - 1, h - 2, 1, 1);
			drawRect(g, 0xACABA7, 0.5, w - 2, h - 1, 1, 1);
			drawRect(g, 0x7F7E7D, 1, w - 1, h - 3, 1, 1);
			drawRect(g, 0x777777, 1, w - 2, h - 2, 1, 1);
			drawRect(g, 0x7F7E7D, 1, w - 3, h - 1, 1, 1);
			drawRect(g, 0x686868, 1, 3, 0, w - 4, 1);
			drawRect(g, 0x686868, 1, 0, 3, 1, h - 6);
			drawRect(g, 0x686868, 1, 3, h - 1, w - 6, 1);
			drawRect(g, 0x686868, 1, w - 1, 3, 1, h - 6);
			drawRect(g, 0xBEBEBE, 1, 2, 1, w - 4, 1);
			drawRect(g, 0xBEBEBE, 1, 1, 2, 1, h - 4);
			drawRect(g, 0x686868, 1, w - 2, 1, 1, 1);
			matrix.createGradientBox(1, h - 7, Math.PI / 2, w - 4, 4);
			drawRoundRect(w - 4, 4, 1, h - 7, 0, [0xFBFBFB, 0xEFEFEF, 0xF5F5F5], [1, 1, 1], 
					matrix, GradientType.LINEAR, [0, 204, 255]);
		}
		
		private function drawRect(g:Graphics, color:uint, alpha:Number, x:Number, y:Number, 
				w:Number, h:Number):void
		{
			g.beginFill(color, alpha);
			g.drawRect(x, y, w, h);
			g.endFill();
		}
		
	}
	
}