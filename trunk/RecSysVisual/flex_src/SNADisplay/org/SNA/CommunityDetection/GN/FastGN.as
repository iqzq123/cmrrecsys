package SNADisplay.org.SNA.CommunityDetection.GN
{
	import SNADisplay.org.Graph.Logical.*;
	import SNADisplay.org.SNA.CommunityDetection.ICommunityDetection;
	//相关论文 Fast algorithm for detecting community structure in networks
	public class FastGN implements ICommunityDetection
	{
		private var _graphData:IGraphData;		//保存网络数据的结构
		private var _matrix:Array;		//矩阵
		private var _aArray:Array;		//
		private var _nodes:Array;
		private var _edges:Array;
		private var _commIndex:Array;	//社团序号
		private var _communities:Array;		//最终的社团发现结果
		private var _results:Array;		//记录每一次产生新社团的结果
		
		public function FastGN(graphData:IGraphData)
		{
			_graphData = graphData;
			_nodes = _graphData.nodes;
			_edges = _graphData.edges;
			_commIndex = new Array;
			_communities = new Array;
			_matrix = new Array;
			_aArray = new Array;
			_results = new Array;
		}
		//初始化函数，初始化_matrix 和 _aArray
		private function init():void {
			var arr:Array;
			var noNode:int = _nodes.length;
			var i:int;
			var j:int;
			for ( i = 0 ; i < noNode ; i++ ){
				_commIndex.push(i);	//初始化社团数组，最开始每个节点都是一个社团
				arr = new Array(_nodes.length);	//初始化一维数组
				_matrix.push(arr);	//初始化矩阵
				//ai=ki/2m (m为网络的总边条数)
				_aArray.push((_nodes[i] as INode).edges.length/(2*_edges.length));
			}
			//遍历_matrix矩阵
			for ( i = 0 ; i < noNode ; i++ ){
				for ( j = 0 ; j < noNode ; j++ ){
					//如果vi和vj之间有边相连，则_matrix[i][j]等于1/2m (m为网络的总边条数),否则设为0
					if ( (_nodes[i] as INode).edges.indexOf(_nodes[j]) != -1 ){
						_matrix[i][j] = 1/(2*_edges.length);
					}
					else
						_matrix[i][j] = 0;
				}
			}
		}
		//主要社团发现函数
		public function detection():Array {
			var graphData:IGraphData = _graphData.copy();		//网络数据的拷贝
			var edges:Array = graphData.edges;
			var edge:IEdge;
			var targetEdge:IEdge;
			var i:int;
			var noEdge:int = _edges.length;
			var maxDeltaQ:Number = -Infinity;		//模块度的最大增益
			var deltaQ:Number;			//当前模块度的增益
			var communities:Array;		//社团划分
			var modularity:Number = 0;	//模块度
			//初始化
			init();
			//依次增加边，循环次数等于边的个数
			for ( i = 0 ; i < noEdge; i++ ){
				targetEdge = edges[0];		//目标边
				maxDeltaQ = -Infinity;		//初始化最大模块度增益
				//遍历当前所有边，找出具有最大模块度增益的变
				for each ( edge in edges ){
					deltaQ = calculateDeltaQ(graphData,edge);	//计算当前边产生的模块度增益
					if ( maxDeltaQ < deltaQ ){
						targetEdge = edge;
						maxDeltaQ = deltaQ; 
					}
				}
				//从边数组中删除被选中的边
				edges.splice(edges.indexOf(targetEdge),1);
				//更新数组
				updateMatrix(graphData,targetEdge);
				//更新模块度
				modularity += maxDeltaQ;
				var community:Array = new Array;
				//得到当前网络的社团划分结果
				community = findCommunities(_commIndex);
				_results.push([community,modularity]);
			}
			var maxModularity:Number;
			var n:int;
			n = 0;
			maxModularity = _results[0][1];
			//查找结果中模块度最大的社团划分结果
			for ( i = 0 ; i < _results.length ; i++ ){
				if ( _results[n][1] < _results[i][1] )
					n = i;
			}
			_communities = _results[n][0];
			return _communities;
		}
		//计算每次聚合和的社团度增益
		private function calculateDeltaQ(graphData:IGraphData,edge:IEdge):Number {
			var deltaQ:Number;
			var nodes:Array = graphData.nodes;
			var node1:INode;
			var index1:int;
			var node2:INode;
			var index2:int;
			var index:int;
			node1 = edge.fromNode;
			index1 = nodes.indexOf(node1);		//找到边起点的序号
			node2 = edge.toNode;
			index2 = nodes.indexOf(node2);		//找到边终点的序号
			//没查找到该点，则返回-1
			if ( index1 == -1 || index2 == -1 )
				return -1;
			//找到当前点所属社团中的序号最小的点的序号
			index1 = _commIndex[index1];	
			index2 = _commIndex[index2];
			//保证index1 小于 index2
			if ( index1 > index2 ){
				index = index1;
				index1 = index2;
				index2 = index;
			}
			deltaQ = 2*(_matrix[index1][index2] - _aArray[index1]*_aArray[index2]);
			return deltaQ;
		}
		//更新矩阵
		private function updateMatrix(graphData:IGraphData, targetEdge:IEdge):void {
			var noNode:int = _nodes.length;
			var nodes:Array = graphData.nodes;
			var deltaQ:Number;
			var node1:INode;
			var index1:int;
			var node2:INode;
			var index2:int;
			var index:int;
			var i:int;
			node1 = targetEdge.fromNode;
			index1 = nodes.indexOf(node1);		//找到边起点的序号
			node2 = targetEdge.toNode;
			index2 = nodes.indexOf(node2);		//找到边起点的序号
			//没查找到该点，则返回-1
			if ( index1 == -1 || index2 == -1 )
				return ;
			//找到当前点所属社团中的序号最小的点的序号
			index1 = _commIndex[index1];
			index2 = _commIndex[index2];
			//保证index1 小于 index2
			if ( index1 > index2 ){
				index = index1;
				index1 = index2;
				index2 = index;
			}
			
			//index2的行数据合并到index1的行数据中
			for ( i = 0 ; i < noNode ; i++ ){
				_matrix[index1][i] += _matrix[index2][i];
			}
			//通过index1行的数据更新与index1有关的列数据
			for ( i = 0 ; i < index1 ; i++ ){
				_matrix[i][index1] = _matrix[index1][i];
			}
			//将所有_commIndex[i] == index2的点都指向到index1
			for ( i = 0 ; i < noNode ; i++ ){
				if ( _commIndex[i] == index2 )
					_commIndex[i] = index1;
			}
			_aArray[index1] += _aArray[index2];
		}
		//将commIndex数组中的社团结构映射到点集数组的社团结构中
		private function findCommunities(commIndex:Array):Array{
			var communities:Array = new Array;
			var nodes:Array = _graphData.nodes;
			var noNode:int = _nodes.length;
			var i:int;
			var j:int;
			for ( i = 0 ; i < noNode ; i++ ){
				//如果commIndex[i] == i，那么序号为i的节点是它所属社团的所有节点中序号最小的节点
				if ( commIndex[i] == i ){		
					var community:Array = new Array;
					//将i以后所有commIndex[j] == i的节点都和i划分到同一个社团中
					for ( j = i ; j < noNode ; j++ ){
						if ( commIndex[j] == i ){
							community.push(nodes[j]);
						}
					}
					communities.push(community);	//将社团加入到结果中
				}
			}
			return communities;
		}
		
		private function print(a:Array):void {
			var arr:Array;
			var node:INode;
			for each ( arr in a ){
				trace("***");
				for each ( node in arr ){
					trace(node.id);
				}
				trace("***");
			}
		}
	}
}