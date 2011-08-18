package SNADisplay.org.SNA.CommunityDetection.GN
{

	import SNADisplay.org.Graph.Logical.*;
	import SNADisplay.org.SNA.CommunityDetection.CountModularity;
	import SNADisplay.org.SNA.CommunityDetection.ICommunityDetection;
	
	import flash.utils.Dictionary;
	public class GN implements ICommunityDetection
	{
		private var _graphData:IGraphData;
		private var _graphDataArr:Array;
		private var _mapDis:Dictionary;		//单源最短路径算法结果中每个点与到源点距离的映射
		private var _mapPre:Dictionary;		//每个点和它先前点的映射
		private var _mapBTW:Dictionary;		//每条边与它的边介度的映射
		private var _removeEdges:Array;		//得到当前网络所删除的边的集合
		
		private var _communities:Array;		//最终的社团发现结果
		private var _results:Array;			//记录每一次产生新社团的结果
		
		public function GN(graphData:IGraphData)
		{
			_graphData = graphData;
			_graphDataArr = new Array;
			_mapDis = new Dictionary;
			_mapPre = new Dictionary;
			_mapBTW = new Dictionary;
			_removeEdges = new Array;
			_results = new Array;
			_communities = new Array;
			separateGraph();
		}
		public function separateGraph():void {
			var root:INode;
			var node:INode;
			var toNode:INode;
			var x:Number;
			var y:Number;
			var nodes:Array;
			var tree:Tree;
			var forest:Forest;
			nodes = _graphData.nodes;
			//设置根节点，如果没有指定，则默认为nodes[0]
			if ( root == null )
				root = nodes[0];
			//生成森林
			forest = new Forest(_graphData,root);
			for each ( tree in forest.tree ){
				var graphData:GraphData = new GraphData;
				var map:Dictionary = new Dictionary;
				nodes = tree.nodes;
				graphData.nodes = nodes;
				for each ( node in nodes ){
					for each ( toNode in node.edges ){
						if ( map[toNode] == null ){
							var edge:IEdge = _graphData.getEdge(node, toNode);
							graphData.edges.push(edge);
						}
					}
					map[node] = true;
				}
				_graphDataArr.push(graphData);
			}
		}
		//社团发现的主要执行函数
		public function detection():Array {
			var partGraphData:GraphData;
			var result:Array;
			for each ( partGraphData in _graphDataArr){
				var graphData:IGraphData = partGraphData.copy();	//拷贝当前的网络结构
				_results = new Array;
				//逐一从网络中减边
				while ( graphData.edges.length > 0 ){
					trace("graphData.edges.length="+graphData.edges.length);
					var selectedEdges:Array;
					var selectedEdge:IEdge;
					selectedEdges = findMaxBTWEdges(graphData);	//找出边节度最大的边的集合数组
					//注：如果一次查询结果中有多个相同的最大边节度的边，则一次都删除
					for each ( selectedEdge in selectedEdges ){	//以此从网络中减去这些边
						trace("remove edge ("+selectedEdge.fromNode.id+" to "+selectedEdge.toNode.id+")");
						_removeEdges.push(selectedEdge);	//将减去的边加入到已删除饿边集合
						graphData.deleteEdge(selectedEdge);	//从网络中删除当前边
					}
					
					//如果删除边后产生了新社团
					if ( newCommunityOccured(graphData,selectedEdges) == true ){	
						var modularity:Number;	
						var CM:CountModularity = new CountModularity(_graphData);	
						var communities:Array;
						var mapCommunities:Array;
						communities = graphData.partition();	//对当前修改过的网络进行社团划分
						//将社团结果映射到原始的网络结构，因为原始的网络结构才有完整的边结构
						mapCommunities = _graphData.mapCommunities(communities);	
						modularity = CM.count(mapCommunities);		//计算当前网络的模块度
						trace("modularity="+modularity);
						_results.push([modularity,_removeEdges.length]);
					}
					trace("===============================================================");
				}
				var i:int;
				var maxModularity:Number;
				var n:int;
				n = 0;
				if ( _results.length != 0 ){
					maxModularity = _results[0][0];
					//查找结果中模块度最大的社团划分结果
					for ( i = 0 ; i < _results.length ; i++ ){
						if ( _results[n][0] < _results[i][0] )
							n = i;
					}
					trace("maxModularity="+_results[n][0]+" index="+_results[n][1]);
					//生成目标社团划分结果所产生的对应社团
					result = generateCommunities(partGraphData.copy(),_results[n]);
					//_communities.concat(generateCommunities(_results[n]));
				}
				else {
					result = generateCommunities(partGraphData.copy(),["0",0]);
					//_communities.concat(generateCommunities(["0",0]));
				}
				_communities = _communities.concat(result.concat());
			}
			return _communities;
		}
		
		//查找边节度最大的边
		private function findMaxBTWEdges(graphData:IGraphData):Array {
			var maxBTWEdge:IEdge;
			var maxEdgeBTW:Number;
			var source:INode;
			var edge:IEdge;
			var edgeArray:Array = new Array;
			_mapBTW = new Dictionary;
			//初始化所有边的边介度为0
			for each ( edge in graphData.edges ){
				_mapBTW[edge] = 0;
			}
			var node:INode;
			//对当前网络中所有的点进行单源最短路径的计算
			for each ( source in graphData.nodes ){
				_mapDis = new Dictionary;
				_mapPre = new Dictionary;
				singleSourceShortestPath( source );
				accumulationEB(graphData);	//对当前的结果累加边介度
			}
			//初始化最大边介度为第一个边的边介度
			maxEdgeBTW = _mapBTW[graphData.edges[0]];
			//遍历所有边，把具有最大边介度的所有边保存到数组中
			for each ( edge in graphData.edges ){
				if ( maxEdgeBTW < _mapBTW[edge] ){	//发现具有更大边介度的边
					maxEdgeBTW = _mapBTW[edge];
					edgeArray = new Array;
					edgeArray.push(edge);
				}
				else if ( maxEdgeBTW == _mapBTW[edge] ){	//具有相同最大边介度的边
					edgeArray.push(edge);
				}
			}
			return edgeArray;
		}
		//判断是否产生了新的社团，当前删除的边为参数
		private function newCommunityOccured(graphData:IGraphData ,selectedEdges:Array):Boolean {
			var node:INode;
			var edge:IEdge;
			var adjNode:INode;
			var destNode:INode;
			var queue:Array;
			var mapNodeVisited:Dictionary = new Dictionary;
			var result:Boolean;
			var isSplited:Boolean;
			result = false;
			for each ( edge in selectedEdges ){
				queue = new Array;
				mapNodeVisited = new Dictionary;
				mapNodeVisited[edge.fromNode] = true;
				//删除边的一个端点为起点，另一个端点为终点
				queue.push(edge.fromNode);
				destNode = edge.toNode;
				isSplited = true;	//初始化为删除边后出现新社团
				//从起点开始遍历网络
				while ( queue.length > 0 ){
					node = queue.shift();
					for each ( adjNode in node.edges ){
						//如果邻接节点是目标终点，则说明删除当前边后，并没有产生新社团
						if ( adjNode == destNode ){
							isSplited = false;
							break;
						}
						else {	//将邻接点标记成已访问，并加入到遍历队列中
							if ( mapNodeVisited[adjNode] == undefined ){
								mapNodeVisited[adjNode] = true;
								queue.push(adjNode);
							}
							
						}
					}
					//如果已经发现没有生产新社团，就保留结果结束循环
					if ( isSplited == false )
						break;
				}
				//只要存在一个删除边产生的新社团分裂，则返回true
				if ( isSplited == true )
					return true;
			}
			return false;
		}
		//生成社团
		private function generateCommunities( graphData:IGraphData, _result:Array):Array {
			var communities:Array;
			var result:Array;
			//var graphData:IGraphData = _graphData.copy();	//拷贝当前网络结构
			var i:int;
			//依次删除需要删除的边
			for ( i = 0 ; i < _result[1] ; i++ ){
				var edge:IEdge;
				edge = graphData.getEdgeByNodeId((_removeEdges[i] as IEdge).fromNode.id, (_removeEdges[i] as IEdge).toNode.id);
				graphData.deleteEdge(edge);
			}
			communities = graphData.partition();	//社团生成
			result = _graphData.mapCommunities(communities);	//将社团结果映射到原网络
			return result;
		}
		
		//单源最短路径，边的权值为1
		private function singleSourceShortestPath(root:INode):void {
			var node:INode;
			var adjNode:INode;
			var targetEdge:IEdge;
			
			var pre:Array;
			var queue:Array = new Array;
			var distance:int = 0;
			pre = new Array;
			_mapPre[root] = pre;
			_mapDis[root] = 0;
			queue.push(root);
			//宽度优先遍历
			while ( queue.length > 0 ){
				node = queue.shift();
				distance = _mapDis[node]+1;
				for each ( adjNode in node.edges ){
					//如果当前节点没有设置最短距离或者当前距离比已有距离要短
					if ( _mapDis[adjNode] == undefined || ( _mapDis[adjNode] > distance )){
						_mapDis[adjNode] = distance;	//更新最短距离
						pre = new Array;	//初始化先前节点的数据
						pre.push(node);		//将当前node节点加入到pre数组中
						_mapPre[adjNode] = pre;	//将当前节点与先前节点数组映射起来
						queue.push(adjNode);	//将当前节点加入到宽度优先遍历的队列
					}
					else if ( _mapDis[adjNode] == distance ){	//如果有相同的最短距离，将先前节点加入到pre数组中
						(_mapPre[adjNode] as Array).push(node);
					}
				} 
			}
		}
		//累加边介度
		private function accumulationEB(graphData:IGraphData):void {
			var nodes:Array = graphData.nodes.concat();
			var node:INode;
			var mapBTWOnNode:Dictionary = new Dictionary;	//注：为了算法需要，需要建立每个点被经过的路径条数
			nodes.sort(sortOnDistance);		//将节点按照距离排序，降序排序
			
			for each ( node in nodes ){
				var dis:Number;
				var preNode:INode;
				//点的边介度初始化为0
				if ( mapBTWOnNode[node] == undefined )
					mapBTWOnNode[node] = 0;
				dis = mapBTWOnNode[node];
				dis++;
				for each ( preNode in ( _mapPre[node] as Array ) ){
					if ( mapBTWOnNode[preNode] == undefined )
						mapBTWOnNode[preNode] = 0;
					//trace("from:"+node.id+" to:"+preNode.id+" edgeBTW="+dis/( _mapPre[node] as Array ).length);
					var edge:IEdge;
					//找到连接当前点和先前点的边
					edge = graphData.getEdge(preNode, node);
					_mapBTW[edge] += dis/( _mapPre[node] as Array ).length;	//多个先前点，则取边介度的平局值		
					//先前节点的经过路径条数累加当前点经过路径条数			
					mapBTWOnNode[preNode] += dis/( _mapPre[node] as Array ).length;	
				}
			}
		}
		//降序排序
		private function sortOnDistance(a:INode, b:INode):Number {
			if ( (_mapDis[a] as int) > (_mapDis[b] as int) )
				return -1;
			else if ( (_mapDis[a] as int) == (_mapDis[b] as int) )
				return 0;
			else 
				return 1; 
		}
		private function printGraph(graphData:IGraphData):void {
			var node:INode;
			var adjNode:INode;
			for each ( node in graphData.nodes ){
				for each ( adjNode in node.edges ){
					trace("adj:"+adjNode.id);
				}
			}
		}
	}
}