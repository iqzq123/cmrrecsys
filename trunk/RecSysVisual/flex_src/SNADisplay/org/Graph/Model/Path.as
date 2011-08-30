package SNADisplay.org.Graph.Model
{
	import SNADisplay.org.Graph.Logical.*;
	
	import flash.utils.Dictionary;
	
	import mx.containers.Canvas;
	
	public class Path extends Graph implements IGraph
	{
		private var mergeRoot:Boolean = false;	
		public function Path(id:String, directional:Boolean=false, isFastMode:Boolean=false, mergeRoot:Boolean = false)
		{
			super(id, directional, isFastMode);
			this.mergeRoot = mergeRoot;
		}
		
		override public function initGraphData(xmlData:XML, canvas:Canvas = null):void {
			_xmlData = xmlData;	
			if(_xmlData != null) {
				_graphFullData = initFromXML(_xmlData);
				_graphData.nodes = _graphFullData.nodes.concat();
				_graphData.edges = _graphFullData.edges.concat();
			}
			else {
				throw Error("the xmlData is null"); 
			}
			if ( canvas != null ){
				this.canvas = canvas;
			}else {
				throw Error("the canvas is null"); 
			}
			setCanvasPageComp(this.canvas);
			this.setTreeHorizonLayout();
		}
		import mx.controls.Alert;
		private function  initFromXML(xmlData:XML, minUser:Number = -1 , maxUser:Number = -1 , minLength:Number = -1, maxLength:Number = -1):IGraphData {
			var graphFullData:IGraphData = new GraphData;
			var endPointsXMLList:XMLList = xmlData.child("EndPoints");
			
			var pathXMLList:XMLList;// = endPointsXMLList.child("Path");
			var rootNodeMap:Dictionary = new Dictionary;
			
			var newNode:INode;
			var newEdge:IEdge;
			var tempEdge:IEdge;
			var curNode:INode;
			var root:INode;
			var map:Dictionary = new Dictionary;
			var s:String = "";
			if ( maxUser <= 0 )
				maxUser = int.MAX_VALUE;
			if ( maxLength <= 0 )
				maxLength = int.MAX_VALUE;
			var endPointsIndex:int = 0;
			var pathIndex:int = 0;
			for each ( var endPointXML:XML in endPointsXMLList ){
				pathIndex = 0;
				pathXMLList = endPointXML.child("Path");
				for each ( var pathXML:XML in pathXMLList ){
					var userNum:Number =  new Number( pathXML.attribute("userNum") );
					var pagesXMLList:XMLList = pathXML.descendants("Page");
					var length:int = pagesXMLList.length() - 1;
					if ( userNum >= minUser && userNum <= maxUser && length >= minLength && length <= maxLength){
						//便利一条路径
						for ( var i:int = 0 ; i <pagesXMLList.length() ; i++ ){
							var pageXML:XML = pagesXMLList[i];
							var pageName:String = pageXML.attribute("pageName");
							//处理树的根节点
							if ( i == 0 ) {
								if ( this.mergeRoot == false ){
									var rootNodeId:String = pageName+"("+endPointsIndex+")";
									if ( rootNodeMap[rootNodeId] == null ){
										s += rootNodeId+","+"\n";
										curNode = new SimpleNode(rootNodeId , pageName );
										graphFullData.nodes.push(curNode);
										rootNodeMap[rootNodeId] = curNode;
									}
									else {
										curNode = rootNodeMap[rootNodeId];
									}
								}
								else {
									if ( root == null ){
										root = new SimpleNode("root" , "根节点" );
										graphFullData.nodes.push(root);
									}
									curNode = root;
								}
							}
							//非根节点，处理节点和边
							else {
								var childNodesArr:Array = curNode.outEdges;
								var targetNode:INode = null;
								var nodeId:String = pageName+"("+endPointsIndex+","+pathIndex+","+i+")";

								for each ( var node:INode in childNodesArr ){
									if ( node.id.split("(")[0] == pageName ){
										tempEdge = graphFullData.getEdge(curNode,node);
										tempEdge.weight += userNum;
										tempEdge.label = tempEdge.weight.toString();
										targetNode = node;
										curNode = targetNode;
										break;
									}
								}
								if ( targetNode == null ){
									newNode = new SimpleNode(nodeId, pageName );
									if ( map[newNode.id] == null ){
										map[newNode.id] = true;
									}
									else {
										s += "bug:"+newNode.id + "\n";
									}
									
									curNode.addOutEdge(newNode);
									newNode.addInEdge(curNode);
									graphFullData.nodes.push(newNode);
									newEdge = new SimpleEdge( curNode.id+","+newNode.id , curNode, newNode);
									newEdge.weight = userNum;
									newEdge.label = newEdge.weight.toString();
									graphFullData.edges.push(newEdge);
									curNode = newNode;
								}
							}
						}			
					}
					pathIndex ++;
				}
				endPointsIndex++;
			}
			//Alert.show(s);
			if ( graphFullData.nodes.length != 0 )
				_root = graphFullData.nodes[0];
			this.createForest(graphFullData, _root);
			_mapNodeColor = setNodeColor(graphFullData.nodes);
			_mapEdgeColor = setEdgeColor(graphFullData.edges);
			return graphFullData;
		}
		
		override public function filter(minUser:Number = -1,maxUser:Number = -1,minLength:Number = -1,maxLength:Number = -1):void {
			_graphFullData = initFromXML(_xmlData,minUser,maxUser,minLength,maxLength);
			//_graphFullData = initFromXML(_xmlData);
			_graphData.nodes = _graphFullData.nodes.concat();
			_graphData.edges = _graphFullData.edges.concat();
			this.updateFilteredGraph();
		}
		
	}
}