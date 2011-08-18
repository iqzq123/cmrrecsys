package SNADisplay.org.SNA.CommunityDetection
{
	import flash.utils.Dictionary;
	import SNADisplay.org.Graph.Logical.IGraphData;
	import SNADisplay.org.Graph.Logical.INode;
	
	public class CountModularity
	{
		private var _graphData:IGraphData;		//保存网络结构的数据
		public function CountModularity(graphData:IGraphData)
		{
			_graphData = graphData;
		}
		
		public function count(communities:Array):Number {
			var community:Array;
			var node:INode;
			var adjNode:INode;
			var edgeNum:int;
			var Q:Number = 0;
			edgeNum = _graphData.edges.length; //网络中的总边数

			//分别对每个社团进行计算
			for each ( community in communities ){
				var mapNode:Dictionary = new Dictionary;
				var inteEdgeCount:int = 0;	//社团内部边的个数
				var inteKCount:int = 0;	//社团内部所有点的度数和
				//初始化和社团内的点有关的信息
				for each ( node in community ){
					mapNode[node] = true;	//当前社团内的点初始化标记为true
					inteKCount += node.edges.length; //社团内部所有点的度数和
				}
				//计算社团内部边的总个数
				for each ( node in community ){
					if ( mapNode[node] == true ) 
						mapNode[node] = false;
					for each ( adjNode in node.edges ){
						if ( mapNode[adjNode] == true )
							inteEdgeCount++;		//累加社团内的边数
					}
					
				}
				//related paper:<finding community structure in very large networks>
				Q += inteEdgeCount/edgeNum - inteKCount/(2*edgeNum) * inteKCount/(2*edgeNum);
			}
			//规范化社团度
			if ( Q < 0 )
				Q = 0;
			if ( Q > 1 )
				Q = 1;
			return Q;
		}
	}
}