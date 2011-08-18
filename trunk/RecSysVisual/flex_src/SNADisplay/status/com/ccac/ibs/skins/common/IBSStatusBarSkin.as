package SNADisplay.status.com.ccac.ibs.skins.common
{
	
	import flash.display.Graphics;
	
	import mx.skins.halo.HaloBorder;
	
	public class IBSStatusBarSkin extends HaloBorder
	{
		
		public function IBSStatusBarSkin()
		{
			super();
		}
		
		override protected function updateDisplayList(w:Number, h:Number):void
		{
			var g:Graphics = this.graphics;
			var colors:Array = [0xA0C4EB, 0xE3EFFF, 0xE1EEFF, 0xDDECFF, 0xD9E9FF, 
					0xD5E7FF, 0xD0E4FF, 0xCCE1FF, 0xC8DFFF, 0xAFD2FF, 0xB0D2FF, 
					0xB1D3FF, 0xB3D4FF, 0xB4D4FF, 0xB6D5FF, 0xB8D7FF, 0xB9D8FF, 
					0xBAD8FF, 0xBCD9FF, 0xBEDAFF, 0xBFDBFF, 0xC0DBFF, 0xC0DBFF];
			var i:int = 0;
			super.updateDisplayList(w, h);
			g.clear();
			for (i = 0; i < h; i++)
			{
				g.lineStyle(1, (i < colors.length ? colors[i] : colors.length - 1), 1);
				g.moveTo(0, i);
				g.lineTo(w, i);
			}
		}
		
	}
	
}