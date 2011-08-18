package SNADisplay.org.LayoutAlgorithm
{
	import SNADisplay.org.Graph.Logical.*;
	
	import flash.geom.Point;
	import flash.utils.Dictionary;
	
	import mx.containers.Canvas;
	
	public class BaseLayout implements ILayout
	{
		private const PADDING:Number = 0.08;
		private const CLASS_NAME:String = "BaseLayout";
		protected var _graphData:IGraphData;
		protected var _canvas:Canvas;
		protected var _center:Point;
		protected var _root:INode;	//根节点
		private var  _padding:Number;	//四周留出的空白宽度
		
		public function BaseLayout()
		{
			
			_padding = PADDING;
			_center = new Point(0,0);
		}

		public function init(graphData:IGraphData,canvas:Canvas):void {
			_graphData = graphData;
			_canvas = canvas;
			_center.x = canvas.width/2;
			_center.y = canvas.height/2;
		}
		//随机布局，将节点随机的画在画布上
		public function placement():Dictionary {
			var node:INode;
			var position:Point;
			var mapPosition:Dictionary = new Dictionary;
			for each (node in _graphData.nodes){
				position = new Point;
				position.x = _canvas.width*_padding + Math.round(Math.random()*_canvas.width*(1-2*_padding));
				position.y = _canvas.height*_padding + Math.round(Math.random()*_canvas.height*(1-2*_padding));
				mapPosition[node] = position;
			}
			return mapPosition;
		}
		import mx.controls.Alert;
		//对于森林结构的网络，进行调整
		protected function modulateGraphs(trees:Array, mapPosition:Dictionary):void {
			var tree:Tree;
			var width:Number = _canvas.width;
			var height:Number = _canvas.height;
			var offset:Point = new Point;
			//最多只有1棵树的情况
			if ( trees.length <= 1 )
				return ;
			//有两棵树的情况，并列一排显示
			if ( trees.length == 2 ){
				zoom(0.5,1,mapPosition);
				offset.x = -width/4;
				offset.y = 0;
				travelOffset((trees[0] as Tree).treeRoot,offset,mapPosition);
				offset.x = width/4;
				offset.y = 0;
				travelOffset((trees[1] as Tree).treeRoot,offset,mapPosition);
			}
			//多余两棵树的情况，每排显示3棵树
			else {
				var i:int;
				var totalRow:int = (trees.length-1) / 3;
				var r:int;
				var l:int;
				//不多于两行树的时候，一个画布将所有树都显示出来
				//多余两行树的时候，一个画布面积被分为9块，超出画布的面积在画布下方显示。用户通过垂直滑条看其余的图
				//按比例缩放每个树结构子图
				if ( totalRow <= 2 )
					zoom(1/3,1/(totalRow+1),mapPosition);
				else
					zoom(1/3,1/3,mapPosition);
				//按照各个图的具体位置，平移子图的坐标
				for ( i = 0 ; i < trees.length ; i++ ){
					r = i / 3;
					l = i % 3;
					offset.x = width/3*l + width/6 - _center.x;
					if ( totalRow <= 2 )
						offset.y = height/(totalRow+1)*r + height/(totalRow+1)/2 - _center.y;
					else
						offset.y = height/3*r + height/6 - _center.y;
					trace("r="+r+" l="+l+" "+offset);
					travelOffset((trees[i] as Tree).treeRoot,offset,mapPosition);
				}
				
			}
		}
		//缩放图像的尺寸
		private function zoom(scaleX:Number,scaleY:Number, mapPosition:Dictionary):void {
			var node:INode;
			var center:Point;
			var position:Point;
			center = _center;
			for each (node in _graphData.nodes){
				position = mapPosition[node] as Point;
				position.x = (position.x - center.x)*scaleX + center.x;
				position.y = (position.y - center.y)*scaleY + center.y;
			}
		}
		//平移一个树的图像，递归结构实现
		private function travelOffset(root:TreeNode , offset:Point, mapPosition:Dictionary):void {
			var treeNode:TreeNode;
			(mapPosition[root.node] as Point).x += offset.x;
			(mapPosition[root.node] as Point).y += offset.y;
			if ( root.children.length > 0 ){
				for each ( treeNode in root.children ) {
					travelOffset(treeNode , offset,mapPosition);
				}
			}
		}
		public function get padding():Number {
			return _padding;
		}
		
		public function set root(r:INode):void {
			_root = r;
		}
		
		public function stop():void {
			
		}
		
		public function start():void {
			
		}
		
		public function get finished():Boolean {
			return true;
		}
		
		public function get isRun():Boolean {
			return false;
		}
		
		public function get layoutName():String {
			return CLASS_NAME;
		}

	}
}