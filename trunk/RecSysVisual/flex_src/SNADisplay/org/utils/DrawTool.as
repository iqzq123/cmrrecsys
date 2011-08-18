package SNADisplay.org.utils
{
	import SNADisplay.org.Graph.Logical.SimpleNode;
	
	import flash.geom.Point;
	
	import mx.core.UIComponent;
	import mx.controls.Alert;
	public class DrawTool
	{
		public static function drawNode(line:UIComponent,node:SimpleNode,position:Point,size:Number,color:int = Colour.RED,alpha:Number = 1):void {
			var radius:int = 5;
			if ( alpha < 0 ){
				alpha = 0;
			}
			if ( alpha > 1 ){
				alpha = 1;
			}
			line.graphics.beginFill(color,alpha);
			line.graphics.lineStyle(1,Colour.BLACK,1);
			line.graphics.drawCircle(position.x,position.y,size/2);
			line.graphics.endFill();
		}

	}
}