package SNADisplay.org.Graph.Visual
{
	import SNADisplay.org.Graph.Logical.IEdge;
	import SNADisplay.org.utils.Colour;
	import flash.events.MouseEvent;
	import mx.containers.Canvas;
	public class VisualEdge extends Canvas
	{
		private var _width:Number; //边的宽度
		private var _edge:IEdge;	//边的逻辑数据结构
		public function VisualEdge()
		{
			super();
			//选取边响应操作
			this.addEventListener(MouseEvent.MOUSE_DOWN, edgeClick);
			_width = 8;
		}
		//初始化，设置边的宽高，旋转，使边落在响应的位置上
		public function init(fromX:Number,fromY:Number,toX:Number,toY:Number):void{
			var angle:Number;
			var degree:Number;
			var length:Number = Math.sqrt((toY-fromY)*(toY-fromY) + (toX-fromX)*(toX-fromX));
			angle = Math.atan2(toY-fromY, toX-fromX);
			degree = 180*angle/Math.PI;
			this.height = _width;
			this.width = length;
			this.x = fromX + _width/2*Math.sin(angle);
			this.y = fromY - _width/2*Math.cos(angle);
			this.graphics.beginFill(Colour.BLUE,1);
			this.graphics.drawRect(0,this.height/2-1,this.width,2);
			this.rotation += degree;
		}
		private function edgeClick(evt:MouseEvent):void {
			evt.stopImmediatePropagation();
		}
		//鼠标移到边上的响应函数
		public function mouseOver():void {
			this.graphics.clear();
			this.graphics.beginFill(Colour.RED,0.8);
			this.graphics.drawRect(0,0,this.width,this.height);
			this.graphics.beginFill(Colour.BLUE,1);
			this.graphics.drawRect(0,this.height/2-1,this.width,2);
		}
		//鼠标移出边的响应函数
		public function mouseOut():void {
			this.graphics.clear();
			this.graphics.beginFill(Colour.BLUE,1);
			this.graphics.drawRect(0,this.height/2-1,this.width,2);
		}
		public function set edge(e:IEdge):void {
			_edge = e;
		}
		public function get edge():IEdge {
			return _edge;
		}
	}
}