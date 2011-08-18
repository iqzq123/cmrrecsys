package SNADisplay.org.Graph.Logical
{
	public interface IGraphData
	{
		function get nodes():Array;
		function set nodes(nodes:Array):void;
		function get edges():Array;
		function set edges(edges:Array):void;
		function get xmlData():XML;
		function set xmlData(xmlData:XML):void;
		function get isDirected():Boolean;
		function set isDirected(b:Boolean):void;
	
		function getNodeById(id:String):INode;
		function copy():IGraphData;
		function deleteEdge(edge:IEdge):void;
		function deleteNode(node:INode):void;
		function getEdge(node1:INode, node2:INode):IEdge;
		function getEdgeByNodeId(nodeId1:String, nodeId2:String):IEdge;
		//从图结构中发现树结构的数组
		function partition():Array;
		//通过节点的id名将获得的社团划分映射到本地图结构
		function mapCommunities(communities:Array):Array;
	}
}