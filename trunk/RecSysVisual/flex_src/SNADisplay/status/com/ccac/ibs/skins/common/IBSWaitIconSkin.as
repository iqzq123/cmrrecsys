package SNADisplay.status.com.ccac.ibs.skins.common
{
	
	import flash.display.Graphics;
	
	import mx.skins.ProgrammaticSkin;
	
	public class IBSWaitIconSkin extends ProgrammaticSkin
	{
		
		public function IBSWaitIconSkin()
		{
			super();
		}
		
		override public function get measuredWidth():Number
		{
			return 16;
		}
		
		override public function get measuredHeight():Number
		{
			return 16;
		}
		
		override protected function updateDisplayList(w:Number, h:Number):void
		{
			graphics.clear();
			graphics.lineStyle(0, 0x000000, 0);
			drawWaitIcon(graphics, int(name.replace("state", "")));
		}
		
		protected function drawWaitIcon(g:Graphics, state:int):void
		{
			var color:uint = 0xE7E7E7;
			var alpha:Number = 0.35;
			var colors:Array = [0x6C7577, 0xC5CED0, 0xB9C1C3, 0xADB5B7, 
					0x9FA8AA, 0x929B9D, 0x868F90, 0x798284];
			var alphas:Array = [0.7, 0.7, 0.7, 0.7, 0.7, 0.7, 0.7, 0.7];
			var newColors:Array = [0, 0, 0, 0, 0, 0, 0, 0];
			var newAlphas:Array = [0, 0, 0, 0, 0, 0, 0, 0];
			var positions:Array = [{x: 0, y: 6}, {x: 2, y: 2}, {x: 6, y: 0}, {x: 10, y: 2}, 
					{x: 12, y: 6}, {x: 10, y: 10}, {x: 6, y: 12}, {x: 2, y: 10}];
			var i:int = 0;
			var index:int = 0;
			var x:Number = 0;
			var y:Number = 0;
			for (i = 0; i < 8; i++)
			{
				index = i + state;
				index = (index >= 8 ? index - 8 : index);
				newColors[index] = colors[i];
				newAlphas[index] = alphas[i];
			}
			for (i = 0; i < 8; i++)
			{
				x = positions[i].x;
				y = positions[i].y;
				g.beginFill(color, alpha);
				g.drawRect(x, y, 4, 4);
				g.endFill();
				g.beginFill(newColors[i], newAlphas[i]);
				g.drawRect(x, y + 1, 4, 2);
				g.endFill();
				g.beginFill(newColors[i], newAlphas[i]);
				g.drawRect(x + 1, y, 2, 4);
				g.endFill();
			}
		}
		
	}
	
}