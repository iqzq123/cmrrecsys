package SNADisplay.org.Graph.Logical
{
	import flash.utils.Dictionary;
	
	public class GraphData implements IGraphData
	{
		private var _nodes:Array;
		private var _edges:Array;
		private var _root:INode;
		private var _isDirected:Boolean;
		private var _xmlData:XML;
		public function GraphData()
		{
			_nodes = new Array;
			_edges = new Array;
			_isDirected = false;
		}
		//复制图结构
		public function copy():IGraphData {
			var graphData:IGraphData = new GraphData;
			var node:INode;
			var edge:IEdge;
			var newNode:INode;
			var newEdge:IEdge;
			//复制节点
			for each ( node in _nodes ){
				newNode = new Node(node.id, node.name, node.dataObject);
				graphData.nodes.push(newNode);
			}
			//复制边
			for each ( edge in _edges ){
				var fromNode:INode = graphData.getNodeById(edge.fromNode.id);
				var toNode:INode = graphData.getNodeById(edge.toNode.id);
				newEdge = new Edge(edge.id, fromNode, toNode);
				graphData.edges.push(newEdge);
				fromNode.addOutEdge(toNode);
				toNode.addInEdge(fromNode);
			}
			return graphData;
		}
		//通过节点id获得节点的逻辑结构
		public function getNodeById(id:String):INode {
			var node:INode;
			for each( node in _nodes ){
				if ( node.id == id )
					return node;
			}
			return null;
		}
		//通过连接边的两个节点的逻辑结构获得边的逻辑结构
		public function getEdge(node1:INode, node2:INode):IEdge {
			var e:IEdge;
			for each ( e in _edges ){
				if ( e.fromNode == node1 && e.toNode == node2 )
					return e;
			}
			e = null;
			return e;
		}
		//通过连接边的两个节点的id获得边的逻辑结构
		public function getEdgeByNodeId(nodeId1:String, nodeId2:String):IEdge {
			var e:IEdge;
			for each ( e in _edges ){
				if ( e.fromNode.id == nodeId1 && e.toNode.id == nodeId2 )
					return e;
			}
			e = null;
			return e;
		}
		//删除点
		public function deleteNode(node:INode):void{
			var n:INode;
			var i:int;
			for ( i = 0 ; i < _nodes.length ; i++ ){
				if ( _nodes[i] == node ){
					_nodes.splice(i,1);
					break;
				}
			} 
		}
		//删除边
		public function deleteEdge(edge:IEdge):void {
			var e:IEdge;
			var node:INode;
			var i:int;
			for ( i = 0 ; i < _edges.length ; i++ ){
				if ( _edges[i] == edge ){
					_edges.splice(i,1);
					break;
				}
			}
			for each ( node in _nodes ){
				if ( node == edge.fromNode ){
					node.deleteEdge(edge.toNode);
				}
				if ( node == edge.toNode ){
					node.deleteEdge(edge.fromNode);
				}
			}
		}
		//划分图，结果为组成图的多个树，在社团发现算法中会被用到
		public function partition():Array {
			var forest:Array = new Array;
			var root:Node; 
			var mapNodeVisited:Dictionary = new Dictionary; //用于标记节点是否被访问过的辅助数据结构
			for each ( root in _nodes ) {
				if ( mapNodeVisited[root] == undefined ){
					var tree:Array = new Array;
					mapNodeVisited[root] = true;
					tree.push(root);
					var queue:Array = new Array;
					var node:INode;
					var adjNode:INode;
					queue.push(root);
					//深度优先遍历，发现树结构
					while ( queue.length > 0 ){
						node = queue.shift();
						for each ( adjNode in node.edges ){
							if ( mapNodeVisited[adjNode] == undefined ){
								mapNodeVisited[adjNode] = true;
								tree.push(adjNode);
								queue.push(adjNode);
							}
						}
					}
					forest.push(tree);
				}
			}
			return forest;
		}
		//将获得的社团信息通过节点的id名对应映射到图的数据结构中，获得本地图结构对应的社团
		public function mapCommunities(communities:Array):Array {
			var mapCommunities:Array = new Array;
			var mapCommunity:Array;
			var community:Array;
			var node:INode;
			var mapNode:INode;
			for each ( community in communities ){
				mapCommunity = new Array;
				for each ( node in community ){
					for each ( mapNode in _nodes ){
						if ( mapNode.id == node.id ){
							mapCommunity.push(mapNode);
						}
					} 
				}
				mapCommunities.push(mapCommunity);
			}
			return mapCommunities;
		}
		public function get nodes():Array {
			return _nodes;
		}
		public function set nodes(nodes:Array):void {
			_nodes = nodes;
		}
		public function get edges():Array {
			return _edges;
		}
		public function set edges(edges:Array):void {
			_edges = edges;
		}
		public function get xmlData():XML {
			return _xmlData;
		}
		public function set xmlData(xmlData:XML):void {
			_xmlData = xmlData;
		}
		public function set isDirected(b:Boolean):void {
			_isDirected = b;
		}
		public function get isDirected():Boolean {
			return _isDirected;
		}
		public function set root(n:INode):void {
			this._root = n;
		}
		public function get root():INode {
			return this._root;
		}
	}
}