package SNADisplay.org.LayoutAlgorithm
{
	import flash.geom.Point;
	import flash.utils.Dictionary;
	
	import SNADisplay.org.Graph.Logical.*;
	//中心发散同心圆的布局算法
	public class ConcentricRadialLayout extends BaseLayout implements ILayout
	{
		private const CLASS_NAME:String = "ConcentricRadialLayout";
		private var _forest:Forest;
		private var _mapNodeWidth:Dictionary;
		private var _levelRadius:Number;
		private const _factor:Number = 0.9;
		private var _mapPosition:Dictionary;
		public function ConcentricRadialLayout()
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
			//根节点如果没有设置，默认为nodes[0]
			if ( _root == null ){
				if ( nodes.length != 0 )
					_root = nodes[0];
				else 
					return _mapPosition;
			}
			//建立森林
			_forest = new Forest(_graphData,_root);
			_mapNodeWidth = new Dictionary; 
			_mapPosition = new Dictionary;
			//计算森林中每个树的布局坐标。
			for each ( tree in _forest.tree ){
				calculateLayout(tree);
			}
			_mapNodeWidth = null;
			//对于森林结构的图进行调整
			modulateGraphs(_forest.tree, _mapPosition);
			return _mapPosition;
		}

		private function calculateLayout(tree:Tree):void {
			var level:Number;
			var maxRadius:Number;
			var position:Point = new Point;
			//树中每个下最终生成的叶子个数为Width，计算每个节点的Width
			calculateWidth(tree.treeRoot);
			//计算最合适的半径长度
			maxRadius = Math.min( _canvas.width,  _canvas.height)*(1-2*padding)/2;
			level = tree.depth;
			//计算第一层节点的半径长度
			if ( level >= 2 )
				_levelRadius = maxRadius*(1-_factor)/(1-Math.pow(_factor,level));
			else
				_levelRadius = maxRadius;
			position.x = _center.x;
			position.y = _center.y;
			_mapPosition[tree.root] = position;
			//递归计算根节点以外的其他节点的位置
			if ( tree.treeRoot.children.length > 0 )
				setNodesPositon(tree.treeRoot, 0 , 2*Math.PI, _levelRadius );
		}
		
		private function calculateWidth(root:TreeNode):int {
			//节点如果没有孩子，那么宽度为1
			if ( root.children.length == 0 ) {
				_mapNodeWidth[root] = 1;
				return 1;
			}
			//如果节点有孩子，那么宽度初始化为0
			else
				_mapNodeWidth[root] = 0;
			var treeNode:TreeNode;
			//递归计算累加父节点的宽度
			for each ( treeNode in root.children ){
				_mapNodeWidth[root] += calculateWidth(treeNode);
			}
			return _mapNodeWidth[root];
		}
		//递归计算每个节点的位置
		private function setNodesPositon(root:TreeNode, angleFrom:Number, angleTo:Number, levelRadius:Number):void{
			var totalWidth:Number = 0;
			var treeNode:TreeNode;
			var angleRange:Number;
			var childAngle:Number;
			var position:Point;
			angleRange = angleTo - angleFrom;
			//修正角度
			if ( angleRange < 0 || angleRange > 2*Math.PI )
				angleRange = ( angleRange + 2*Math.PI )%(2*Math.PI);
			//计算此节点下的孩子的总宽度
			for each ( treeNode in root.children ){
				totalWidth += _mapNodeWidth[treeNode];
			}
			for each ( treeNode in  root.children ){
					position = new Point;
					//按比例计算每个孩子所占的角度宽度
					childAngle = angleFrom + angleRange*_mapNodeWidth[treeNode]/totalWidth/2;
					position = Point.polar(levelRadius, childAngle);
					position.x = position.x + _center.x;
					position.y = position.y + _center.y;
					_mapPosition[treeNode.node] = position;
					//递归每个孩子的后代
					setNodesPositon(treeNode, angleFrom, angleFrom + angleRange*_mapNodeWidth[treeNode]/totalWidth, levelRadius + _levelRadius*Math.pow(_factor,treeNode.level));
					//计算下一个孩子的起始角度
					angleFrom += angleRange*_mapNodeWidth[treeNode]/totalWidth;
			}
		}
		
		override public function get layoutName():String {
			return CLASS_NAME;
		}
	}
}