package SNADisplay.org.Graph.Model
{
	import SNADisplay.org.Graph.Logical.*;
	
	import flash.utils.Dictionary;
	
	import mx.containers.Canvas;
	
	import mx.controls.Alert;
	public class FRPath extends Graph implements IGraph
	{
		public function FRPath(id:String, directional:Boolean=false, isFastMode:Boolean=false)
		{
			super(id, directional, isFastMode);
		}
		override public function initGraphData(xmlData:XML, canvas:Canvas = null):void {
			_xmlData = xmlData;	
			if(_xmlData != null) {
				_graphData = initFromXML(_xmlData);
			}
			else {
				throw Error("the xmlData is null"); 
			}
			if ( canvas != null ){
				this.canvas = canvas;
			}else {
				throw Error("the canvas is null"); 
			}
			this.setTreeHorizonLayout();
		}
		
		private function  initFromXML(xmlData:XML, minUser:Number = -1 , maxUser:Number = -1 , minLength:Number = -1, maxLength:Number = -1):IGraphData {
			var graphData:IGraphData = new GraphData;
			var pathXMLList:XMLList = xmlData.descendants("Path");
			var rootNodeMap:Dictionary = new Dictionary;
			
			var newNode:INode;
			var newEdge:IEdge;
			
			var curNode:INode;
			if ( maxUser <= 0 )
				maxUser = int.MAX_VALUE;
			if ( maxLength <= 0 )
				maxLength = int.MAX_VALUE;

			//遍历所有路径
			for each ( var pathXML:XML in pathXMLList ){
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
							graphData.nodes.push(curNode);
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
							graphData.nodes.push(newNode);
							newEdge = new SimpleEdge( curNode.id+","+newNode.id , curNode, newNode);
							newEdge.weight = new Number(pathXML.attribute("pathNum").toString());
							newEdge.label = newEdge.weight.toString();
							graphData.edges.push(newEdge);
							curNode = newNode;
						}
					}
				}
			} 
			_root = graphData.nodes[0];
			_mapNodeColor = setNodeColor(graphData.nodes);
			_mapEdgeColor = setEdgeColor(graphData.edges);
			return graphData;
		}
		
		override public function filter(minUser:Number = -1,maxUser:Number = -1,minLength:Number = -1,maxLength:Number = -1):void {
			_graphData = initFromXML(_xmlData,minUser,maxUser,minLength,maxLength);
			this.updateFilteredGraph();
		}
	}
}