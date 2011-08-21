package SNADisplay.org.LayoutAlgorithm
{
	import flash.geom.Point;
	import flash.utils.Dictionary;
	
	import SNADisplay.org.Graph.Logical.*;
	public class TreeLayout extends BaseLayout implements ILayout
	{
		private const CLASS_NAME:String = "TreeLayout";
		private var _mapNode:Dictionary;
		private var _levelDepth:Number;
		private var _forest:Forest;
		private var _mapPosition:Dictionary;
		public function TreeLayout()
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
			if ( _root == null ){
				if ( nodes.length != 0 )
					_root = nodes[0];
				else 
					return _mapPosition;
			}
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
			var fromX:Number = _canvas.width*padding;
			var toX:Number = _canvas.width*(1-padding);
			var Y:Number = _canvas.height*padding;
			var position:Point = new Point;
			//如果根节点有孩子
			if ( tree.treeRoot.children.length > 0 ){
				//计算根节点的位置
				position.x = (fromX + toX)/2;
				position.y = Y;
				_mapPosition[tree.root] = position;
				//计算一棵树的每层高度
				_levelDepth = _canvas.height*(1-2*padding) / tree.depth;
				setNodesPositon(tree.treeRoot, fromX, toX, Y+_levelDepth);
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
		
		private function setNodesPositon(root:TreeNode, fromX:Number, toX:Number, Y:Number):void{
			var x:Number;
			var startX:Number = fromX;  //起始横坐标
			var totalWidth:Number = 0;
			var treeNode:TreeNode;
			var band:Number = toX - fromX;  //孩子的宽度范围
			var position:Point;
			var i:int;
			//计算当期根节点的孩子的总宽度
			for each ( treeNode in root.children ){
				totalWidth += _mapNode[treeNode];
			}
			i = 0;
			//计算当期孩子们的位置
			for each ( treeNode in  root.children ){
				position = new Point;
				x = startX + band*_mapNode[treeNode]/totalWidth/2;
				position.x = x;
				//同一层的孩子的y坐标一次递减
				position.y = Y - i/root.children.length*_levelDepth*0.3;
				_mapPosition[treeNode.node] = position;
				//递归计算当期孩子的后代节点坐标
				setNodesPositon(treeNode, startX, startX + band*_mapNode[treeNode]/totalWidth, Y+_levelDepth);
				startX += band*_mapNode[treeNode]/totalWidth;
				i++;
			}
		}
		
		override public function get layoutName():String {
			return CLASS_NAME;
		}
	}
}