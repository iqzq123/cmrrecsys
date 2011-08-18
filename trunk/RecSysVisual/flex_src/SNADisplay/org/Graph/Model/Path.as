package SNADisplay.org.Graph.Model
{
	import SNADisplay.org.Graph.Logical.*;
	
	import flash.utils.Dictionary;
	
	import mx.containers.Canvas;
	import mx.events.IndexChangedEvent;
	import mx.controls.Alert;
	
	public class Path extends Graph implements IGraph
	{
		public function Path(id:String, directional:Boolean=false, isFastMode:Boolean=false)
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
			var endPointsXMLList:XMLList = xmlData.child("EndPoints");
			var index:int = 0;
			var fromPageId:String;
			var curPageId:String;
			var prePageId:String;
			var newNode:INode;
			var preNode:INode;
			var newEdge:IEdge;
			var currentEdgeId:int = 0;
			for each ( var endPointXML:XML in endPointsXMLList ){
				var aggDic:Dictionary = new Dictionary;
				var filteredPathes:Array = new Array;
				
				//Alert.show(endPointXML.toString());
				
				fromPageId = endPointXML.attribute("fromPage").toString() + "(p" + index +", " + 0 + ")";
	
				newNode = new SimpleNode( fromPageId , endPointXML.attribute("fromPage").toString());
				graphData.nodes.push(newNode);
	
				var pathXMLList:XMLList = endPointXML.child("Path");//endPointXML.attribute("Path");
				
				//Alert.show("pathLength:" + pathXMLList.length());
				for each ( var pathXML:XML in pathXMLList ){
					var userNum:Number =  new Number( pathXML.attribute("userNum") );
					var pagesXMLList:XMLList = pathXML.descendants("Page");
					var length:int = pagesXMLList.length() - 1;
				 	if ( maxUser <= 0 )
						maxUser = int.MAX_VALUE;
					if ( maxLength <= 0 )
						maxLength = int.MAX_VALUE;
						
					if ( userNum >= minUser && userNum <= maxUser && length >= minLength && length <= maxLength){
						filteredPathes.push( pathXML );			
					}
				}
				
				genGraphLevel(filteredPathes, 1, newNode , index, graphData); 
				
				index ++;
			}
			//Alert.show("node:"+graphData.nodes.length+"edge:"+graphData.edges.length);
			
			_root = graphData.nodes[0];
			_mapNodeColor = setNodeColor(graphData.nodes);
			_mapEdgeColor = setEdgeColor(graphData.edges);
			
			return graphData;
		}
		
		private function genGraphLevel( pathArr:Array , level:int , preNode:INode, taskIndex:int, graphData:IGraphData):void {
			var pageDic:Dictionary = new Dictionary;
			var newNode:INode;
			var newEdge:IEdge;
			var pathXML:XML;
			var pageXMLList:XMLList;
			var s:String = "";
			s += "pathNum:"+pathArr.length+" level:"+level + "\n";
			//遍历所有路径
			for ( var i:int = 0 ; i < pathArr.length ; i++ ){
				pathXML = pathArr[i] as XML;
				pageXMLList = pathXML.child("Page");
				if ( pageXMLList.length() > level ){
					//聚合当前层页面相同的路径到pageDic中
					var curPage:String = (pageXMLList[level] as XML).attribute("pageName");
					var tempArr:Array;
					s += "path "+i+" :"+curPage + "\n";
					if ( pageDic[curPage] == null ) {
						tempArr = new Array;
						tempArr.push(pathXML);
						pageDic[curPage] = tempArr;
						s += "newArr curPage" + curPage + "\n";
					}
					else {
						tempArr = pageDic[curPage] as Array;
						tempArr.push(pathXML);
						s += "addArr curPage" + curPage +"\n";
					}
				}
				
			}

			for each ( var arr:Array in pageDic ) {
				pathXML = arr[0] as XML;
				pageXMLList = pathXML.child("Page");
				var curPageName:String = (pageXMLList[level] as XML).attribute("pageName").toString();
				s +="curPageName:"+curPageName+"prePageName:"+preNode.name + "\n";
				
				newNode = new SimpleNode( curPageName+"("+ taskIndex + "," + level+")" , curPageName);
				newEdge = new SimpleEdge( preNode.name+","+newNode.name , preNode, newNode);
				preNode.addOutEdge(newNode);
				newNode.addInEdge(preNode);
				var w:int = 0;
				for each ( pathXML in arr ){
					w += new Number(pathXML.attribute("userNum").toString())
				}
				s += w+"\n";
				newEdge.weight = w;
				newEdge.label = w.toString();
				graphData.nodes.push(newNode);
				graphData.edges.push(newEdge);
				
				genGraphLevel( arr , level + 1, newNode, taskIndex , graphData); 
				//Alert.show(s);
			}
			
		}
		
		override public function filter(minUser:Number = -1,maxUser:Number = -1,minLength:Number = -1,maxLength:Number = -1):void {
			_graphData = initFromXML(_xmlData,minUser,maxUser,minLength,maxLength);
			this.updateFilteredGraph();
		}
	}
}