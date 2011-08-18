package SNADisplay.org.LayoutAlgorithm
{
	import flash.geom.Point;
	import flash.utils.Dictionary;
	import SNADisplay.org.Graph.Logical.INode;
	//圆环形的布局算法
	public class CircleLayout extends BaseLayout implements ILayout
	{
		private const CLASS_NAME:String = "CircleLayout";
		private var _angle:Number;
		private var _radius:Number;
		private var _mapPosition:Dictionary;
		public function CircleLayout()
		{
			super();
			
		}
	
		override public function placement():Dictionary
		{
			var node:INode;
			var nodes:Array = _graphData.nodes;
			var startAngle:Number;
			var position:Point;
			_mapPosition = new Dictionary;
			_angle = 2*Math.PI / _graphData.nodes.length;	//计算角度
			_radius = Math.min(_canvas.width, _canvas.height ) * (1-2*padding)/2; //计算合适的半径
			startAngle = 0;
			//以此计算出每个点的坐标，并赋值。
			for each ( node in nodes ){
				position = Point.polar(_radius, startAngle);
				position.x = _center.x + position.x;
				position.y = _center.y + position.y;	
				_mapPosition[node] = position;		
				startAngle += _angle;  //角度累加
			}
			return _mapPosition;
		}
		
		override public function get layoutName():String {
			return CLASS_NAME;
		}
	}
}