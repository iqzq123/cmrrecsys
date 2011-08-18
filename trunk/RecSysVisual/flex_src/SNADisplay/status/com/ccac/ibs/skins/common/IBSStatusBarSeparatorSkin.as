package SNADisplay.status.com.ccac.ibs.skins.common
{
	
	import flash.display.GradientType;
	import flash.geom.Matrix;
	
	import mx.skins.ProgrammaticSkin;
	
	public class IBSStatusBarSeparatorSkin extends ProgrammaticSkin
	{
		
		public function IBSStatusBarSeparatorSkin()
		{
			super();
		}
		
		override public function get measuredWidth():Number
		{
			return 2;
		}
		
		override public function get measuredHeight():Number
		{
			return 16;
		}
		
		override protected function updateDisplayList(w:Number, h:Number):void
		{
			graphics.clear();
			if (isNaN(w) || isNaN(h))
				return;
			var x1:Number = 0;
			var x2:Number = Math.ceil(w / 2);
			graphics.lineStyle(0, 0x000000, 0);
			var matrix:Matrix = new Matrix();
			matrix.createGradientBox(x2, h, Math.PI / 2, x1, 0);
			drawRoundRect(x1, 0, x2, h, 0, 
					[0xBBD0EB, 0x9DB9DD, 0x92B0D7, 0x9EB9DD, 0xACC4E4], [1, 1, 1, 1, 1], 
					matrix, GradientType.LINEAR, [0, 64, 128, 192, 255]);
			matrix.createGradientBox(w - x2, h, Math.PI / 2, x2, 0);
			drawRoundRect(x2, 0, w - x2, h, 0, 
					[0xE5F0FF, 0xDEECFF, 0xD5E7FF, 0xCBE1FF, 0xC4DEFF], [1, 1, 1, 1, 1], 
					matrix, GradientType.LINEAR, [0, 64, 128, 192, 255]);
		}
		
	}
	
}