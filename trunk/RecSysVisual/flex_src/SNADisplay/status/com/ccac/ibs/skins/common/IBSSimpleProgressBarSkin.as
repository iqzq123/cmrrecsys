package SNADisplay.status.com.ccac.ibs.skins.common
{
	
	import flash.display.GradientType;
	import flash.geom.Matrix;
	
	import mx.skins.ProgrammaticSkin;
	
	public class IBSSimpleProgressBarSkin extends ProgrammaticSkin
	{
		
		public function IBSSimpleProgressBarSkin()
		{
			super();
		}
		
		override protected function updateDisplayList(w:Number, h:Number):void
		{
			graphics.clear();
			if (isNaN(w) || isNaN(h))
				return;
			if (w == 0 || h == 0)
				return;
			var matrix:Matrix = new Matrix();
			var gap:Number = Number(name.substr(name.lastIndexOf("_") + 1).replace("P", "."));
			var blockSize:Number = Math.round(h * 0.6);
			var count:Number = 0;
			var x:Number = 0;
			var i:int = 0;
			if (blockSize == 0)
				blockSize = h;
			if (isNaN(gap))
				gap = 0.2;
			if (gap < 0 || gap > 1)
				gap = 0.2;
			gap = Math.round(h * gap);
			count = Math.ceil(w / (blockSize + gap));
			if (name.indexOf("center") >= 0)
			{
				for (i = 0; i < count; i++)
				{
					if (x + blockSize >= w)
						break;
					matrix.createGradientBox(blockSize, h, Math.PI / 2, x, 0);
					drawRoundRect(x, 0, blockSize, h, 0, [0xB7CDEC, 0x3F74BC, 0xA6C7F5], [1, 1, 1], 
							matrix, GradientType.LINEAR, [0, 120, 255]);
					x = x + blockSize + gap;
				}
			}
			else if (name.indexOf("right") >= 0)
			{
				x = w - blockSize;
				for (i = 0; i < count; i++)
				{
					if (x < 0)
					{
						blockSize = blockSize + x;
						x = 0;	
					}
					matrix.createGradientBox(blockSize, h, Math.PI / 2, x, 0);
					drawRoundRect(x, 0, blockSize, h, 0, [0xB7CDEC, 0x3F74BC, 0xA6C7F5], [1, 1, 1], 
							matrix, GradientType.LINEAR, [0, 120, 255]);
					x = x - gap - blockSize;
				}
			}
			else
			{
				for (i = 0; i < count; i++)
				{
					if (x + blockSize - w > 0)
						blockSize = w - x;
					matrix.createGradientBox(blockSize, h, Math.PI / 2, x, 0);
					drawRoundRect(x, 0, blockSize, h, 0, [0xB7CDEC, 0x3F74BC, 0xA6C7F5], [1, 1, 1], 
							matrix, GradientType.LINEAR, [0, 120, 255]);
					x = x + blockSize + gap;
				}
			}
		}
		
	}
	
}