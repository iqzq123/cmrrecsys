package SNADisplay.org.Graph.Model
{
	import SNADisplay.org.Graph.Logical.*;
	
	import flash.utils.Dictionary;
	
	import mx.containers.Canvas;
	
	public class FRPath extends Graph implements IGraph
	{
		private var filterPageName:String = "";
		private var closedFRPath:Boolean = false;	
		public function FRPath(id:String, directional:Boolean=false, isFastMode:Boolean=false, closedFRPath:Boolean = true)
		{
			super(id, directional, isFastMode);
			this.closedFRPath = closedFRPath;
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
		private function  initFromXML(xmlData:XML, minUser:Number = -1 , maxUser:Number = -1 , minLength:Number = -1, maxLength:Number = -1):IGraphData {
			if ( this.closedFRPath == false ){
				return this.genFRPathGraphData(xmlData, minUser, maxUser, minLength, maxLength);
			}
			else {
				return this.genClosedFRPathGraphData(xmlData, minUser, maxUser, minLength, maxLength);
			}
		}
			
		private function  genFRPathGraphData(xmlData:XML, minUser:Number = -1 , maxUser:Number = -1 , minLength:Number = -1, maxLength:Number = -1):IGraphData {
			var graphFullData:IGraphData = new GraphData;
			var pathXMLList:XMLList = xmlData.descendants("Path");
			var rootNodeMap:Dictionary = new Dictionary;
			var filteredPathArr:Array = new Array;
			var newNode:INode;
			var newEdge:IEdge;
			
			var curNode:INode;
			if ( maxUser <= 0 )
				maxUser = int.MAX_VALUE;
			if ( maxLength <= 0 )
				maxLength = int.MAX_VALUE;
			filteredPathArr = filterPathByNode(pathXMLList, this.filterPageName);
			var pathId:int = 0;
			//遍历所有路径
			for each ( var pathXML:XML in filteredPathArr ){
				var pagesXMLList:XMLList = pathXML.child("Page");
				var rootPageId:String = "";
				//遍历路径中的page
				var userNum:Number = new Number(pathXML.attribute("pathNum").toString());
				if ( userNum < minUser || userNum > maxUser ) 
					continue;
				if ( pagesXMLList.length()-1 < minLength || pagesXMLList.length()-1 > maxLength )
					continue;

				for ( var i:int = 0 ; i <pagesXMLList.length() ; i++ ){
					var pageXML:XML = pagesXMLList[i];
					var pageName:String = pageXML.attribute("pageName");
					//处理树的根节点
					if ( i == 0 ) {
						if ( rootNodeMap[pageName] == null ){
							curNode = new SimpleNode(pageName , pageName );
							graphFullData.nodes.push(curNode);
							rootNodeMap[pageName] = curNode;
						}
						else {
							curNode = rootNodeMap[pageName ];
						}
						rootPageId = curNode.id;
					}
					//非根节点，处理节点和边
					else {
						var childNodesArr:Array = curNode.outEdges;
						var targetNode:INode = null;
						if ( i < pagesXMLList.length() - 1 ){
							for each ( var node:INode in childNodesArr ){
								if ( node.id == pageName+"("+rootPageId+","+i+")" ){
									targetNode = node;
									curNode = targetNode;
									break;
								}
							}
							if ( targetNode == null ){
								newNode = new SimpleNode(pageName+"("+rootPageId+","+i+")", pageName );
								curNode.addOutEdge(newNode);
								newNode.addInEdge(curNode);
								graphFullData.nodes.push(newNode);
								newEdge = new SimpleEdge( curNode.id+","+newNode.id , curNode, newNode);
								newEdge.weight = new Number(pathXML.attribute("pathNum").toString());
								newEdge.label = newEdge.weight.toString();
								graphFullData.edges.push(newEdge);
								curNode = newNode;
							}
						}
						else {
							newNode = new SimpleNode(pageName+"("+rootPageId+","+pathId+","+i+")", pageName );
							curNode.addOutEdge(newNode);
							newNode.addInEdge(curNode);
							graphFullData.nodes.push(newNode);
							newEdge = new SimpleEdge( curNode.id+","+newNode.id , curNode, newNode);
							newEdge.weight = new Number(pathXML.attribute("pathNum").toString());
							newEdge.label = newEdge.weight.toString();
							graphFullData.edges.push(newEdge);
							curNode = newNode;
						}
					}
				}
				pathId ++;
			} 
			if ( graphFullData.nodes.length != 0 )
				_root = graphFullData.nodes[0];
			this.createForest(graphFullData, _root);
			_mapNodeColor = setNodeColor(graphFullData.nodes);
			_mapEdgeColor = setEdgeColor(graphFullData.edges);
			return graphFullData;
		}
		
		private function genClosedFRPathGraphData(xmlData:XML, minUser:Number = -1 , maxUser:Number = -1 , minLength:Number = -1, maxLength:Number = -1):IGraphData {
			var graphFullData:IGraphData = new GraphData;
			
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
			var filteredPathArr:Array = new Array;
			pathIndex = 0;
			pathXMLList = xmlData.child("Path");
			filteredPathArr = filterPathByNode(pathXMLList, this.filterPageName);
			//遍历一对endpoint中的所有路径
			for each ( var pathXML:XML in filteredPathArr ){
				var userNum:Number = new Number(pathXML.attribute("pathNum").toString());
				var pagesXMLList:XMLList = pathXML.descendants("Page");
				var length:int = pagesXMLList.length() - 1;
				//筛选满足条件的path
				if ( userNum >= minUser && userNum <= maxUser && length >= minLength && length <= maxLength){
					//遍历一条路径
					for ( var i:int = 0 ; i <pagesXMLList.length() ; i++ ){
						var pageXML:XML = pagesXMLList[i];
						var pageName:String = pageXML.attribute("pageName");
						//处理树的根节点
						if ( i == 0 ) {
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

			//Alert.show(s);
			if ( graphFullData.nodes.length != 0 )
				_root = graphFullData.nodes[0];
			this.createForest(graphFullData, _root);
			_mapNodeColor = setNodeColor(graphFullData.nodes);
			_mapEdgeColor = setEdgeColor(graphFullData.edges);
			return graphFullData;
		}
		
		private function filterPathByNode(pathXMLList:XMLList, filterNodeId:String):Array {
			var filterPathArr:Array = new Array;
			for each ( var pathXML:XML in pathXMLList ) {
				var pagesXMLList:XMLList = pathXML.child("Page");
				for each ( var pageXML:XML in pagesXMLList ) {
					var pageName:String = pageXML.attribute("pageName");
					if ( pageName == filterNodeId || filterNodeId == ""){
						filterPathArr.push(pathXML);
						break;
					}
				}
			}
			return filterPathArr;
		}
		
		override public function filter(minUser:Number = -1,maxUser:Number = -1,minLength:Number = -1,maxLength:Number = -1):void {
			_graphFullData = initFromXML(_xmlData,minUser,maxUser,minLength,maxLength);
			_graphData.nodes = _graphFullData.nodes.concat();
			_graphData.edges = _graphFullData.edges.concat();
			this.updateFilteredGraph();
		}
		
		public function setFilterPageName(n:String):void {
			this.filterPageName = n;
		}
	}
}