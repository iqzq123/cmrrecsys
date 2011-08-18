package SNADisplay.org.LayoutAlgorithm
{
	import SNADisplay.org.Graph.Logical.*;
	
	import flash.geom.Point;
	import flash.utils.Dictionary;
	public class PathLayout extends BaseLayout implements ILayout
	{
		private const CLASS_NAME:String = "PathLayout";
		private var _mapNode:Dictionary;
		private var _levelDepth:Number;
		private var _forest:Forest;
		private var _mapPosition:Dictionary;
		
		public function PathLayout()
		{
			super();
		}
		
		override public function placement():Dictionary {
			var node:INode;
			var x:Number;
			var y:Number;
			var nodes:Array;
			var tree:Tree;
			nodes = _graphData.nodes;
			//设置根节点，如果没有指定，则默认为nodes[0]
			if ( _root == null )
				_root = nodes[0];
			//生成森林
			_forest = new Forest(_graphData,_root);
			_mapNode = new Dictionary; 
			_mapPosition = new Dictionary;
			//计算每棵树的布局位置
			for each ( tree in _forest.tree ){
				calculateLayout(tree);
			}
		//	Alert.show("gg2");
			//调整每棵树的位置
			modulateGraphs(_forest.tree, _mapPosition);
			return _mapPosition;
		}
		import mx.controls.Alert;
		private function calculateLayout(tree:Tree):void {
			var nodes:Array = tree.nodes;
			var node:INode;
			var fromNode:INode;
			var toNode:INode;
			for each ( node in nodes ){
				var xmlData:XML = node.dataObject as XML;
				if ( xmlData.attribute("type") == "from" )
					fromNode = node;
				if ( xmlData.attribute("type") == "to" )
					toNode = node;
			}
			if ( fromNode == null || toNode == null )
				return ;
			var width:int = fromNode.edges.length;
			var startX:int = _canvas.width*padding;
			var startY:int = _canvas.height*padding;
			var endX:int = _canvas.width*(1-padding);
			var endY:int = _canvas.height*(1-padding);
			var p:Point;
			p = new Point;
			p.x = _canvas.width/2;
			p.y = startY;
			_mapPosition[fromNode] = p;
			var i:int;
			var curNode:INode;
			var maxLength:int = 0;
			var pathArr:Array = new Array;
			var pathNodes:Array = new Array;
			
			for ( i = 0 ; i < width ; i++ ){
				curNode = fromNode.edges[i];
				pathNodes = new Array;
				while ( curNode != null ){
					pathNodes.push(curNode);
					curNode = curNode.outEdges[0];
				}
				pathArr.push(pathNodes);
				if ( maxLength < pathNodes.length )
					maxLength = pathNodes.length;
			}

			for ( i = 0 ; i < width ; i++ ){
				var X:int = _canvas.width*padding +  _canvas.width*(1-2*padding)/width*(i+0.5);
				var num:int = pathNodes.length;
				var seq:int = 1;
				pathNodes = pathArr[i];
				for each ( curNode in pathNodes ){
					p = new Point;
					p.x = X
					p.y =  startY+(endY-startY)/(maxLength+1)*seq;
					_mapPosition[curNode]= p;
					seq++;
				}
			}

		}
		
		override public function get layoutName():String
		{
			return CLASS_NAME;
		}
		
	}
}