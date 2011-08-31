package SNADisplay.org.Graph.Model
{
	import SNADisplay.org.Graph.Logical.*;
	
	import flash.utils.Dictionary;
	
	import mx.containers.Canvas;
	public class FRPath extends Graph implements IGraph
	{
		private var filterPageName:String = "";
		public function FRPath(id:String, directional:Boolean=false, isFastMode:Boolean=false)
		{
			super(id, directional, isFastMode);
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
				}
			} 
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