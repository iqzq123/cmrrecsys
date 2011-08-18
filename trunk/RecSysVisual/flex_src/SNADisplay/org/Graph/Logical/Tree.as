package SNADisplay.org.Graph.Logical
{
	import flash.utils.Dictionary;
	public class Tree
	{
		private var _graphData:IGraphData;  //图结构
		private var _root:TreeNode;	//根节点
		private var _depth:int;	//树的深度
		private var _nodeMap:Dictionary;	//辅助变量
		
		public function Tree(graphData:IGraphData, rootNode:INode)
		{
			if ( graphData == null || rootNode == null )
				return ;
			
			_graphData = graphData;
			_root = new TreeNode;
			_root.node = rootNode;
			_nodeMap = new Dictionary;
			createMap();
			_nodeMap[_root.node] = false;
			create(_root);
			_nodeMap = null;
		}
		
		public function get root():INode {
			return _root.node;
		}
		public function get treeRoot():TreeNode {
			return _root;
		}
		public function get depth():int {
			return _depth;
		}
		public function set root(rootNode:INode):void {
			if ( _graphData != null)
				return ;
			_root = new TreeNode;
			_root.node = rootNode;
			_nodeMap = new Dictionary;
			createMap();
			_nodeMap[_root.node] = false;
			create(_root);
			_nodeMap = null;
		}
		//初始化变量，标记所有节点都没有被访问过。
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
        //生出树
		private function create(treeNode:TreeNode):void {
			var queue:Array = new Array();
			//var inEdges:Array = new Array;
			//var outEdges:Array = new Array;
			var edges:Array = new Array;
			var curNode:INode;
			var curTreeNode:TreeNode;
			var newTreeNode:TreeNode;
			_depth = 0;
			treeNode.level = 0;
			queue.push(treeNode);
			trace("===============");
			while ( queue.length > 0 ) {
				curTreeNode = queue.shift();
				trace("curNode="+curTreeNode.node.id);
				edges = curTreeNode.node.edges;
				trace("child:");
				for each( curNode in edges) {
					if ( _depth < curTreeNode.level+1 ){
						_depth = curTreeNode.level+1;
					}
					if ( _nodeMap[curNode] == true ){
						newTreeNode = new TreeNode;
						newTreeNode.node = curNode;
						newTreeNode.level = curTreeNode.level+1;
						queue.push(newTreeNode);
						curTreeNode.children.push(newTreeNode);
						trace(newTreeNode.node.id);
						_nodeMap[curNode] = false;
					}
				}
				trace("child end");
			}
		}
		public function get nodes():Array {
			var arr:Array = new Array;
			var treeNode:TreeNode;
			var tempTreeNode:TreeNode;
			var result:Array = new Array;
			arr.push(_root);
			while ( arr.length > 0 ){
				treeNode = arr.shift();
				result.push(treeNode.node);
				for each ( tempTreeNode in treeNode.children)
					arr.push(tempTreeNode);	
			}
			var node:INode;
			trace("+++++++++");
			for each ( node in result ){
				trace(node.id);
			}
			trace("=========");
			return result;
		}
	}
}