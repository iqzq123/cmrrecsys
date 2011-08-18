package SNADisplay.org.LayoutAlgorithm
{
	import flash.geom.Point;
	import flash.utils.Dictionary;
	import SNADisplay.org.Graph.Logical.*;
	public class TreeLayoutHorizon extends BaseLayout implements ILayout
	{
		private const CLASS_NAME:String = "TreeLayoutHorizon";
		private var _mapNode:Dictionary;
		private var _levelDepth:Number;
		private var _forest:Forest;
		private var _mapPosition:Dictionary;
		
		public function TreeLayoutHorizon()
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
			//调整每棵树的位置
			modulateGraphs(_forest.tree, _mapPosition);
			return _mapPosition;
		}
		
		private function calculateLayout(tree:Tree):void {
			calculateWidth(tree.treeRoot);
			var fromY:Number = _canvas.height*padding;
			var toY:Number = _canvas.height*(1-padding);
			var X:Number = _canvas.width*padding;
			var position:Point = new Point;
			//如果根节点有孩子
			if ( tree.treeRoot.children.length > 0 ){
				//计算根节点的位置
				position.x = X;
				position.y = (fromY + toY)/2;
				_mapPosition[tree.root] = position;
				//计算一棵树的每层高度
				_levelDepth = _canvas.width*(1-2*padding) / tree.depth;
				setNodesPositon(tree.treeRoot, fromY, toY, X+_levelDepth);
			}
			//如果根节点没有孩子
			else {
				position = _center.clone();
				_mapPosition[tree.root] = position;
			}
		}
		
		private function calculateWidth(root:TreeNode):int {
			//节点如果没有孩子，那么宽度为1
			if ( root.children.length == 0 ) {
				_mapNode[root] = 1;
				return 1;
			}
			//如果节点有孩子，那么宽度初始化为0
			else
				_mapNode[root] = 0;
			var treeNode:TreeNode;
			//递归计算所有孩子的宽度
			for each ( treeNode in root.children ){
				_mapNode[root] += calculateWidth(treeNode);
			}
			return _mapNode[root];
		}
		
		private function setNodesPositon(root:TreeNode, fromY:Number, toY:Number, X:Number):void{
			var y:Number;
			var startY:Number = fromY;  //起始横坐标
			var totalHeight:Number = 0;
			var treeNode:TreeNode;
			var band:Number = toY - fromY;  //孩子的宽度范围
			var position:Point;
			var i:int;
			//计算当期根节点的孩子的总宽度
			for each ( treeNode in root.children ){
				totalHeight += _mapNode[treeNode];
			}
			i = 0;
			//计算当期孩子们的位置
			for each ( treeNode in  root.children ){
				position = new Point;
				y = startY + band*_mapNode[treeNode]/totalHeight/2;
				position.y = y;
				//同一层的孩子的x坐标相同
				position.x = X;
				_mapPosition[treeNode.node] = position;
				//递归计算当期孩子的后代节点坐标
				setNodesPositon(treeNode, startY, startY + band*_mapNode[treeNode]/totalHeight, X+_levelDepth);
				startY += band*_mapNode[treeNode]/totalHeight;
				i++;
			}
		}
		
		override public function get layoutName():String
		{
			return CLASS_NAME;
		}
		
	}
}