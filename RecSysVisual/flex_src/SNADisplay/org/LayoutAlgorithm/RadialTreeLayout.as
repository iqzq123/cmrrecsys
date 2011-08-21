package SNADisplay.org.LayoutAlgorithm
{
	import flash.geom.Point;
	import flash.utils.Dictionary;
	
	import SNADisplay.org.Graph.Logical.*;
	import SNADisplay.org.utils.Geometry;
	public class RadialTreeLayout extends BaseLayout implements ILayout
	{
		private const CLASS_NAME:String = "RadialTreeLayout";
		private var _forest:Forest;
		private var _curTree:Tree;
		private var _mapNode:Dictionary;
		private var _levelRadius:Number;
		private const _factor:Number = 0.8;
		private var _mapPosition:Dictionary;
		public function RadialTreeLayout()
		{
			super();
		}
		
		override public function placement():Dictionary
		{
			var node:INode;
			var x:Number;
			var y:Number;
			var nodes:Array;
			var tree:Tree;
			nodes = _graphData.nodes;
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
			//对每一个树进行布局计算
			for each ( tree in _forest.tree ){
				_curTree = tree;
				calculateLayout(tree);
			}
			_mapNode = null;
			//对森林中的每一棵树进行位置修正
			modulateGraphs(_forest.tree, _mapPosition);
			return _mapPosition;
		}

		private function calculateLayout(tree:Tree):void {
			//计算这棵树中每个节点的宽度
			calculateWidth(tree.treeRoot);
			var level:Number;
			var maxRadius:Number;
			var position:Point = new Point;
			//计算最合适的半径
			maxRadius = Math.min( _canvas.width,  _canvas.height)*(1-2*padding)/2;
			level = tree.depth;
			//计算第一层的半径
			if ( level >= 2 )
				_levelRadius = maxRadius*(1-_factor)/(1-Math.pow(_factor,level));
			else
				_levelRadius = maxRadius;
			position.x = _center.x;
			position.y = _center.y;
			_mapPosition[tree.root] = position;
			//递归计算这棵树下的所有节点的位置
			setNodesPositon(tree.treeRoot, _center.x, _center.x, _levelRadius );
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
		
		private function setNodesPositon(root:TreeNode, rootX:Number, rootY:Number, levelRadius:Number):void{
			var totalWidth:Number = 0;
			var treeNode:TreeNode;
			var angleFrom:Number;
			var angleTo:Number;
			var childAngle:Number;
			var rootAngle:Number;
			var position:Point;
			//根节点相对于坐标原点的角度
			rootAngle = Geometry.getAngle(rootX, rootY, _center);
			//计算此根节点的孩子的总宽度
			for each ( treeNode in root.children ){
				totalWidth += _mapNode[treeNode];
			}
			//单独考虑树的根节点
			if ( root == _curTree.treeRoot ){
				angleFrom = 0 ;	
				for each ( treeNode in  root.children ){
					position = new Point;
					//每个孩子所占的角度按宽度比例计算
					childAngle = angleFrom + 2*Math.PI*_mapNode[treeNode]/totalWidth/2;
					position = Point.polar(levelRadius, childAngle);
					angleFrom += 2*Math.PI*_mapNode[treeNode]/totalWidth;
					position.x = position.x + _center.x;
					position.y = position.y + _center.y;
					_mapPosition[treeNode.node] = position;
					//递归计算当期孩子的后代节点的位置
					setNodesPositon(treeNode, position.x, position.y, levelRadius * _factor);
				}
			}
			else {
				//孩子节点的起始角度为父节点（root节点）的角度正负1/2PI的范围。
				angleFrom = rootAngle - Math.PI/2 ;
				for each ( treeNode in  root.children ){
					position = new Point;
					//每个孩子所占的角度按宽度比例计算
					childAngle = angleFrom + Math.PI*_mapNode[treeNode]/totalWidth/2;
					position = Point.polar(levelRadius, childAngle);
					angleFrom += Math.PI*_mapNode[treeNode]/totalWidth;
					position.x = position.x + rootX;
					position.y = position.y + rootY;
					_mapPosition[treeNode.node] = position;
					//递归计算当期孩子的后代节点的位置
					setNodesPositon(treeNode, position.x, position.y, levelRadius * _factor);
				}
				
			}
		}
		
		override public function get layoutName():String {
			return CLASS_NAME;
		}
		
	}
}