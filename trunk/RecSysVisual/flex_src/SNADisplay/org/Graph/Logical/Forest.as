package SNADisplay.org.Graph.Logical
{
	import flash.utils.Dictionary;
	public class Forest
	{
		private var _trees:Array;	//数结构数组
		private var _graphData:IGraphData;	//图结构
		private var _nodeMap:Dictionary;	//辅助变量，记录节点是否被考虑过了
		private var _root:INode;	//跟节点
		public function Forest(graphData:IGraphData, rootNode:INode)
		{
			if ( graphData == null || rootNode == null )
				return ;
			_trees = new Array;
			_graphData = graphData;
			_root = rootNode;
			_nodeMap = new Dictionary;
			createMap();
			create();
			_nodeMap = null;
		}
		//初始化，所有节点设置成没有被访问过
		private function createMap():void {
			var nodes:Array;
			var node:INode;
			if ( _graphData != null){
				nodes = _graphData.nodes;
				for each( node in nodes ){
					_nodeMap[node] = true;
				}
			}
		}
		//生产森林
		private function create():void {
			var nodes:Array = _graphData.nodes;
			var node:INode;
			var newTree:Tree;
			//生产第一棵树，因为根节点是选定的
			newTree = new Tree(_graphData,_root);
			//存储根节点
			_trees.push(newTree);
			//遍历标记树
			travelMark(newTree.treeRoot);
			//一次按顺序生成其他树
			for each ( node in nodes ) {
				if ( _nodeMap[node] == true ){
					newTree = new Tree(_graphData,node);
					_trees.push(newTree);
					travelMark(newTree.treeRoot);
				}
			}
			_nodeMap = null;
		}
		//遍历树，并标记以被考虑过的节点
		private function travelMark(root:TreeNode):void {
			_nodeMap[root.node] = false;
			var node:TreeNode;
			if ( root.children.length > 0 ){
				for each ( node in root.children ) {
					travelMark(node);
				}
			}
		}
		public function get tree():Array {
			return _trees;
		}
	}
}