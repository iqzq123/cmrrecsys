package SNADisplay.org.Graph.Model
{
	import SNADisplay.org.Graph.Logical.*;
	import SNADisplay.org.Graph.Visual.VisualNode;
	import SNADisplay.org.LayoutAlgorithm.*;
	import SNADisplay.org.SNA.CommunityDetection.CliquePercolation.CliquePercolation;
	import SNADisplay.org.SNA.CommunityDetection.Default.DefaultCommunities;
	import SNADisplay.org.SNA.CommunityDetection.GN.FastGN;
	import SNADisplay.org.SNA.CommunityDetection.GN.GN;
	import SNADisplay.org.SNA.CommunityDetection.ICommunityDetection;
	import SNADisplay.org.SNA.ConvexHull.ConvexHull;
	import SNADisplay.org.utils.Colour;
	import SNADisplay.org.utils.DrawTool;
	import SNADisplay.org.utils.Events.*;
	import SNADisplay.org.utils.Geometry;
	
	import flash.events.KeyboardEvent;
	import flash.events.MouseEvent;
	import flash.events.TimerEvent;
	import flash.geom.Point;
	import flash.geom.Rectangle;
	import flash.utils.Dictionary;
	import flash.utils.Timer;
	
	import mx.containers.Canvas;
	import mx.controls.Button;
	import mx.controls.Label;
	import mx.controls.Text;
	import mx.core.UIComponent;
	import mx.events.ResizeEvent;
	
	public class Graph implements IGraph
	{
		private const MAX_SCALE:Number = 4;
		private const MIN_SCALE:Number = 0.5;
		private var _isFastMode:Boolean = false; //是否是快速模式
		private var _id:String;  	//图的i
		protected var _graphFullData:IGraphData;		//图信息的数据结构
		protected var _graphData:IGraphData;		//图信息的数据结构
		protected var _graphParts:Array;		//图信息的数据结构
		private var _curPart:int = 0;		//当前的现实部分序号
		private var _graphCanvas:Canvas;		//画布
		
		private var _drawCommunities:UIComponent;		//绘制社团的句柄
		
		private var _compDrawNode:UIComponent;	//绘制节点
		private var _compDrawEdge:UIComponent;	//绘制边
		private var _compDraw:UIComponent;	//绘制其他
		
		private var _dragCursorStartX:Number;	//拖动鼠标时的起始横坐标
		private var	_dragCursorStartY:Number;	//拖动鼠标的其实纵坐标

		private var _targetNode:INode; //目标节点
		private var _center:Point;		//画布的中心坐标
		private var _netRegion:Rectangle;		//记录画布尺寸的矩形变量
		private var _layout:ILayout;		//布局算法的变量
		protected var _root:INode;		//当前网络的根节点
		private var _communityDectection:ICommunityDetection //社团发现的变量
		private var _mapPositionFrom:Dictionary;		//网络图像动画转变时的起始坐标映射
		private var _mapPositionTo:Dictionary;			//网络图像动画转变时的终点坐标映射
		private var _mapPosition:Dictionary;		//当前网络的各个节点的坐标映射
		protected var _xmlData:XML;		//存储网络结构的xml变量，当前仅作存储用，没用产生实际作用
		private var _time:int;	//为区分鼠标单击和点下拖动而设的时间变量
		private var _communities:Array;		//存储网络社团结构的数组
		private var _convexHulls:Array;		//存储网络社团结构凸包的数组
		
		private var _directional:Boolean;	//网络是否为有向网络的标记值
		private var _showCommunities:Boolean;	//是否显示社团结构的标记值
		private var _showEdgeDirection:Boolean;		//是否显示边的有向箭头的标记值
		private var _showEdgeLabel:Boolean;			//是否显示边的标签的标记值
		private var _nodeStyle:Boolean;				//节点的显示模式false为绘制，true为贴图
		private var _showPath:Boolean;	//是否显示路径
		private var _pathStyle:Boolean;	//显示路径模式false为直接显示，true为动态显示
		
		private var _communitiesStyle:Boolean;			//社团显示模式，false为色点，true为凸包
		private var _timer:Timer;				//网络图像动画转变时需要用到的计时器变量
		//private var _stretchStyle:Boolean;		//双击节点是展开子社团（true）还是根节点改变(false)的标记值
		
		private var _multiSelectStyle:Boolean;	//多选节点的标记值true为多选，false为单选
		private var _ctrlKey:Boolean;			//标记ctrl键是否被按下的标记值，true为按下，false为没有被按下
		private var _multiSelectResult:Array;	//记录多选节点的结果数组
		private const CLICK_INTERVAL:int = 250;	//单击鼠标操作的鼠标按下的时间上限值
		private const DOUBLE_CLICK_INTERVAL:int = 100; //双击鼠标两次点击的间隔判定时间
		private var _selectCanvas:Canvas;	//多选节点用到的选择框
		private var _zoomScale:Number;		//缩放尺度
		private var _localMagnify:Boolean;	//局部放大
		private var _rootChangeAction:Boolean;	//双击节点后改变根节点更新布局的动画。
		private var _disableClick:Boolean;  //点击节点的判定值
		private var _isDoubleClick:Boolean; //判定点击节点是否是双击
		
		private var _maxNodeWeight:Number = 0;
		private var _maxEdgeWeight:Number = 0;
		protected var _mapNodeColor:Dictionary;
		protected var _mapEdgeColor:Dictionary;
		private var _mapNodeCommunity:Dictionary;
		private var _curPathIndex:int = 0;
		private var _path:Array;
		
		//初始化网络
		public function Graph(id:String, directional:Boolean = false, isFastMode:Boolean = false)
		{
			if(id == null)
				throw Error("id string must not be null");
			if(id.length == 0)
				throw Error("id string must not be empty");

			_id = id;
			_graphData = new GraphData();
			_graphParts = new Array;
			_isFastMode = isFastMode;

			_nodeStyle = false;
			_pathStyle = false;
			_directional = directional;
			_graphCanvas = null;
			
			_drawCommunities = new UIComponent();
			_compDrawNode = new UIComponent();
			_compDrawEdge = new UIComponent();
			_compDraw = new UIComponent;
			
			_center = new Point(0,0);
			_netRegion = new Rectangle();
			_mapPositionFrom = new Dictionary;
			_mapPositionTo = new Dictionary;
			_mapPosition = new Dictionary;
			_mapNodeCommunity = new Dictionary;
			_communities = new Array;
			_showCommunities = false;
			_showEdgeDirection = false;
			_showEdgeLabel = false;
			_rootChangeAction = true;
			_ctrlKey = false;
			_convexHulls = new Array();
			
			_zoomScale = 1;
			_disableClick = false;
			_isDoubleClick = false;
			
		}
		
		public function initGraphData(xmlData:XML, canvas:Canvas = null):void {
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
			this.setCircleLayout();
		}
		//从XML文件初始化网络数据
		private function initFromXML(xmlData:XML,minNodeW:Number = -1,maxNodeW:Number = -1,minEdgeW:Number = -1,maxEdgeW:Number = -1):IGraphData {
			var graphFullData:IGraphData = new GraphData;
			var xnode:XML;
			var xedge:XML;
			
			var newNode:INode;
			var newEdge:IEdge;
			
			var fromNodeId:String;
			var toNodeId:String;
			
			var currentEdgeId:int = 0;
			var fromNode:INode;
			var toNode:INode;
			var string:String;
			var s:String = "";

			graphFullData.isDirected = _directional;

			if ( this._isFastMode == false ){ //一般模式下建立数据结构
				for each(xnode in xmlData.descendants("Node")) {
					newNode = new Node(xnode.@id , xnode.@name, xnode);
					newNode.dataObject = xnode;
					newNode.visualNode.doubleClickEnabled = true;
					newNode.visualNode.toolTip = xnode.@description;
					newNode.visualNode.addEventListener(MouseEvent.MOUSE_DOWN,nodeMouseDown);
					newNode.visualNode.addEventListener(MouseEvent.CLICK,nodeClick);
					newNode.visualNode.addEventListener(MouseEvent.DOUBLE_CLICK,nodeDoubleClick);
					//鼠标移到节点上和移出节点的响应事件
					
					newNode.visualNode.addEventListener(MouseEvent.MOUSE_OVER, mouseOver);
					newNode.visualNode.addEventListener(MouseEvent.MOUSE_OUT, mouseOut);
					graphFullData.nodes.push(newNode);
				}
				for each(xedge in xmlData.descendants("Edge")) {
					fromNodeId = xedge.attribute("fromID");
					toNodeId = xedge.attribute("toID");
					fromNode = graphFullData.getNodeById(fromNodeId);
					toNode = graphFullData.getNodeById(toNodeId);
					newEdge = new Edge(currentEdgeId.toString(), fromNode, toNode);
					graphFullData.edges.push(newEdge);
					
					fromNode.addOutEdge(toNode);
					toNode.addInEdge(fromNode);
					currentEdgeId++;
				}
			}
			else { //快速模式下建立数据结构
				for each(xnode in xmlData.descendants("Node")) {
					newNode = new SimpleNode(xnode.@id , xnode.@name, xnode);
					//Graph中每个节点的单独设计
					newNode.name = xnode.@id;//+"("+ xnode.attribute("clickNum").toString()+","+ xnode.attribute("time").toString()+")";
					newNode.weight = 0;//new Number(xnode.attribute("clickNum"));
					newNode.dataObject = xnode;
					if ( minNodeW == -1 && maxNodeW == -1 ){
						graphFullData.nodes.push(newNode);
						//s += newNode.id+'\n';
						if ( _maxNodeWeight < newNode.weight )
							_maxNodeWeight = newNode.weight;
					}else if ( minNodeW >= 0 && ( minNodeW <= maxNodeW || maxNodeW == -1 )){
						if ( maxNodeW == -1 )
							maxNodeW = int.MAX_VALUE
						if ( newNode.weight >= minNodeW && newNode.weight <= maxNodeW ){
							graphFullData.nodes.push(newNode);
							//s += newNode.id+'\n';
							if ( _maxNodeWeight < newNode.weight )
								_maxNodeWeight = newNode.weight;
						}
					}
				}
				for each(xedge in xmlData.descendants("Edge")) {
					fromNodeId = xedge.attribute("fromID");
					toNodeId = xedge.attribute("toID");
					fromNode = graphFullData.getNodeById(fromNodeId);
					toNode = graphFullData.getNodeById(toNodeId);
					if ( fromNode == null || toNode == null )
						continue;
					newEdge = new SimpleEdge(currentEdgeId.toString(), fromNode, toNode);
					newEdge.label = xedge.attribute("weight");
					newEdge.weight = new Number(xedge.attribute("weight"));
					
					if ( minEdgeW == -1 && maxEdgeW == -1 ){
						graphFullData.edges.push(newEdge);
						fromNode.addOutEdge(toNode);
						toNode.addInEdge(fromNode);
						currentEdgeId++;
						if ( _maxEdgeWeight < newEdge.weight )
							_maxEdgeWeight = newEdge.weight;
					}
					else if ( minEdgeW >= 0 && ( minEdgeW <= maxEdgeW || maxEdgeW == -1 )){
						if ( maxEdgeW == -1 )
							maxEdgeW = int.MAX_VALUE;
						if ( newEdge.weight >= minEdgeW && newEdge.weight <= maxEdgeW ){
							graphFullData.edges.push(newEdge);
							fromNode.addOutEdge(toNode);
							toNode.addInEdge(fromNode);
							currentEdgeId++;
							if ( _maxEdgeWeight < newEdge.weight )
								_maxEdgeWeight = newEdge.weight;
						}
					}
					
				}
			}
			if ( graphFullData.nodes.length != 0 )
				_root = graphFullData.nodes[0];
			createForest(graphFullData, _root);
			_mapNodeColor = setNodeColor(graphFullData.nodes);
			_mapEdgeColor = setEdgeColor(graphFullData.edges);
			return graphFullData;
		}
		
		protected function setNodeColor(nodes:Array):Dictionary {
			var tempNode:INode;
			var mapNodeColor:Dictionary = new Dictionary;
			var arr:Array = nodes.concat();
			var s:String;
			var i:int;
			var j:int;
			for ( i = 0 ; i < arr.length - 1 ; i++ ){
				for ( j = 0 ; j < arr.length - 1 - i ; j++ ){
					if ( (arr[j] as INode).weight > (arr[j+1] as INode).weight ){
						tempNode = arr[j];
						arr[j] = arr[j+1];
						arr[j+1] = tempNode;
					}
				}
			}
			mapNodeColor[arr[0]] = 0;
			var d:Number;
			var delta:int;
			var total:Number;
			var curNum:int;
			const COLOR_RANGE:int = 510;
			const DISTANCE:int = 100;
			//Alert.show((_maxNodeWeight/arr.length).toString());
			delta = _maxNodeWeight/arr.length;
			s = arr.length+" "+new int(mapNodeColor[arr[0]]) + ",";
			curNum = 0;
			for ( i = 1 ; i < arr.length ; i++ ){
				d = (arr[i] as INode).weight - (arr[i-1] as INode).weight;
				if ( d == 0 ){
					mapNodeColor[arr[i]] = mapNodeColor[arr[i-1]];
					curNum += DISTANCE;
				}else {
					if ( curNum > mapNodeColor[arr[i-1]] )
						mapNodeColor[arr[i]] = curNum + (2-delta/(d+delta))*DISTANCE;//mapNodeColor[arr[i-1]] + (2-delta/(d+delta))*100;
					else {
						mapNodeColor[arr[i]] = mapNodeColor[arr[i-1]] + (2-delta/(d+delta))*DISTANCE;
					}
					curNum = mapNodeColor[arr[i]];
				}
				total += mapNodeColor[arr[i]] as Number;
			}
			for ( i = 0 ; i < arr.length ; i++ ){
				mapNodeColor[arr[i]] = new int(mapNodeColor[arr[i]]/mapNodeColor[arr[arr.length-1]]*COLOR_RANGE);
			}
			//Alert.show(s);
			return mapNodeColor;
		}
		
		protected function setEdgeColor(edges:Array):Dictionary {
			var tempEdge:IEdge;
			var mapEdgeColor:Dictionary = new Dictionary;
			var arr:Array = edges.concat();
			var s:String;
			var i:int;
			var j:int;
			for ( i = 0 ; i < arr.length - 1 ; i++ ){
				for ( j = 0 ; j < arr.length - 1 - i ; j++ ){
					if ( (arr[j] as IEdge).weight > (arr[j+1] as IEdge).weight ){
						tempEdge = arr[j];
						arr[j] = arr[j+1];
						arr[j+1] = tempEdge;
					}
				}
			}
			mapEdgeColor[arr[0]] = 0;
			var d:Number;
			var delta:int;
			var total:Number;
			var curNum:int;
			const COLOR_RANGE:int = 510;
			const DISTANCE:int = 100;
			delta = _maxEdgeWeight/arr.length;
			s = arr.length+" "+new int(mapEdgeColor[arr[0]]) + ",";
			curNum = 0;
			for ( i = 1 ; i < arr.length ; i++ ){
				d = (arr[i] as IEdge).weight - (arr[i-1] as IEdge).weight;
				if ( d == 0 ){
					mapEdgeColor[arr[i]] = mapEdgeColor[arr[i-1]];
					curNum += DISTANCE;
				}else {
					if ( curNum > mapEdgeColor[arr[i-1]] )
						mapEdgeColor[arr[i]] = curNum + (2-delta/(d+delta))*DISTANCE;//mapNodeColor[arr[i-1]] + (2-delta/(d+delta))*100;
					else {
						mapEdgeColor[arr[i]] = mapEdgeColor[arr[i-1]] + (2-delta/(d+delta))*DISTANCE;
					}
					curNum = mapEdgeColor[arr[i]];
				}
				total += mapEdgeColor[arr[i]] as Number;
			}
			for ( i = 0 ; i < arr.length ; i++ ){
				mapEdgeColor[arr[i]] = new int(mapEdgeColor[arr[i]]/mapEdgeColor[arr[arr.length-1]]*COLOR_RANGE);
			}
			//Alert.show(s);
			return mapEdgeColor;
		}
		//按键被按下的事件的响应函数
		private function keyDown(evt:KeyboardEvent):void {
			
			if ( String.fromCharCode(evt.charCode) == 'z' || String.fromCharCode(evt.charCode) == 'Z'){
				_graphCanvas.removeEventListener(MouseEvent.MOUSE_WHEEL,changeSize);
				_graphCanvas.addEventListener(MouseEvent.MOUSE_WHEEL,rotationHandler);
			}
			_ctrlKey = evt.ctrlKey;
			
			
		}
		//按键弹起的事件响应函数
		private function keyUp(evt:KeyboardEvent):void {
			trace("keyUp");
			_graphCanvas.removeEventListener(MouseEvent.MOUSE_WHEEL,rotationHandler);
			_graphCanvas.addEventListener(MouseEvent.MOUSE_WHEEL,changeSize);
			_ctrlKey = evt.ctrlKey;
		}
		//旋转图像的操作函数
		public function rotation(n:Number):void {
			calNetRegion();
			rotationFit(n);
			drawNodes();
			drawEdges();
			drawCommunities();
		}
		//和鼠标滑轮及z键关联的旋转图像的事件响应函数
		private function rotationHandler(evt:MouseEvent):void {
			calNetRegion();
			rotationFit(evt.delta);
			drawNodes();
			drawEdges();
			drawCommunities();
		}
		//旋转图像的主要执行函数，delta>0时逆时针旋转，delta<0时顺时针旋转
		public function rotationFit(delta:Number):void {
			var node:INode;
			var angle:Number;
			var position:Point = new Point;
			var radius:Number;
			//FastMode
			for each ( node in _graphData.nodes ){
				position.x = _mapPosition[node].x;//node.visualNode.x + node.visualNode.width/2;
				position.y = _mapPosition[node].y;//node.visualNode.y + node.visualNode.nodeIcon.height/2;
				angle = Geometry.getAngle(position.x, position.y, _center);
				radius = Math.sqrt(Math.pow((position.y - _center.y),2)+Math.pow((position.x - _center.x),2));
				if ( delta > 0 ){
					position = Point.polar(radius, angle - Math.PI/45 );
					_mapPosition[node].x = position.x + _center.x;
					_mapPosition[node].y = position.y + _center.y;
				}
				else if ( delta < 0 ){
					position = Point.polar(radius, angle + Math.PI/45 );
					_mapPosition[node].x = position.x + _center.x;
					_mapPosition[node].y = position.y + _center.y;
				}
				else 
					break;
			}
		}
		//缩放函数
		public function zoom(degree:Number):void {
			if ( _netRegion.width < 10 || _netRegion.height < 10 )
					return ;
			else
				zoomFit(degree/_zoomScale);
			_zoomScale = degree;	//更新当前的缩放度
			drawNodes();
			drawEdges();
			drawCommunities();
			calNetRegion();
			var event:ZoomEvent = new ZoomEvent(ZoomEvent.ZOOM_EVENT);
			event.scale = _zoomScale;
			_graphCanvas.dispatchEvent(event);
		}
		//和鼠标滑轮相关联的事件响应缩放函数
		private function changeSize(evt:MouseEvent):void {
			if ( evt.delta > 0 ){
				zoomFit((_zoomScale+0.1)/_zoomScale);
				_zoomScale += 0.1;	//更新当前的缩放度
			}else if ( evt.delta < 0 && _zoomScale > MIN_SCALE ){
				zoomFit((_zoomScale-0.1)/_zoomScale);
				_zoomScale -= 0.1;	//更新当前的缩放度
			}
			drawNodes();
			drawEdges();
			drawCommunities();
			calNetRegion();
			var event:ZoomEvent = new ZoomEvent(ZoomEvent.ZOOM_EVENT);
			event.scale = _zoomScale;
			_graphCanvas.dispatchEvent(event);
		}
		//缩放的主要执行函数
		public function zoomFit(degree:Number):void {
			var node:INode;
			for each(node in _graphData.nodes){
				(_mapPosition[node] as Point).x = _center.x + ((_mapPosition[node] as Point).x - _center.x)*degree;
				(_mapPosition[node] as Point).y = _center.y + ((_mapPosition[node] as Point).y - _center.y)*degree;
			}
		}

		//绘制网络的主要函数
		public function draw():void {
			var node:INode;
			var position:Point;
			var s:String = "";
			this.filterByGraphDataPart(this._graphParts[this._curPart]);
			//this.filterByGraphDataPart(this._graphParts[1]);
			_layout.root = _root;
			var i:int = 0;
			if ( _graphData != null && _graphCanvas != null ){
				if ( _layout.layoutName != "ForceDirectedLayout" ){
					_drawCommunities.graphics.clear();
					_zoomScale = 1;
					_mapPosition = _layout.placement();
					s += _graphData.nodes.length+"\n";
					for each ( node in _graphData.nodes ){
						//Alert.show( node.id );
						position = new Point;			
						//s += (_mapPosition[node] as Point).x +" "+(_mapPosition[node] as Point).x+"\n";
						position.x = (_mapPosition[node] as Point).x;
						position.y = (_mapPosition[node] as Point).y;
						//Alert.show( node.id +":"+position.x+","+position.y);
						s += i+":"+node.id +":\n"+position.x+","+position.y+"\n";
						i++;
						//实现动画效果，初始节点坐标都为图的中心
						(_mapPosition[node] as Point).x = _center.x;
						(_mapPosition[node] as Point).y = _center.y;
						_mapPositionTo[node] = position;
					}
					//Alert.show(s);
					drawNodes();
					//Alert.show("a2");
					drawEdges();
					//Alert.show("a3");
					drawCommunities();
					calNetRegion();
					flash.utils.setTimeout(layoutAnimation, 15, 1);
				}
				else {
					_layout.placement();
				}
			}
			//drawPath(_graphData.nodes);
		}
		//更新当前网络
		public function refresh():void {
			_zoomScale = 1;
			drawNodes();
			drawEdges();
			drawCommunities();
			calNetRegion();
		}
		import mx.controls.Alert;
		private function layoutAnimation():void {
			var node:INode;
			var position:Point = new Point;
			var targetP:Point = new Point;
			var step:int;
			var totalStep:int = 10;
			var m:int;
			var n:int;
			//记录当前的周期次数
			step = int(arguments[0]);
			for each ( node in _graphData.nodes ){
				position = new Point;
				targetP = new Point((_mapPositionTo[node] as Point).x, (_mapPositionTo[node] as Point).y);

				m = Math.pow(2,totalStep);
				n = Math.pow(2,totalStep-step);
				if ( n != 0 ){
					position.x = _center.x + ( targetP.x - _center.x)*(m-n)/m;
					position.y = _center.y + ( targetP.y - _center.y)*(m-n)/m;
				}
				else {
					position.x = targetP.x;
					position.y = targetP.y;
				}
				
				_mapPosition[node] = position;
			}
			drawNodes();
			drawEdges();
			drawCommunities();
			step++;
			if ( step <= totalStep ){
				flash.utils.setTimeout(layoutAnimation,15, step); //step为当前周期次数的补充向量
			}else{
				calNetRegion();
			}
		}
		//绘制节点
		public function drawNodes():void {
			if ( _isFastMode == false ){
				drawComponentNodes();
			}
			else {
				drawSimpleNodes();
			}
			drawNodeLabel();
		}
		//绘制节点
		public function drawComponentNodes():void {
			var node:INode;
			for each( node in _graphData.nodes ) {
				//FastMode
				if ( _mapPosition[node] as Point != null ){
					node.visualNode.x = (_mapPosition[node] as Point).x - node.visualNode.width/2;
					node.visualNode.y = (_mapPosition[node] as Point).y - node.visualNode.nodeIcon.height/2;
				}
				else {
					_mapPosition[node] = new Point(0,0);
					node.visualNode.x = 0;
					node.visualNode.y = 0;
				}
			}
		}
		
		public function drawSimpleNodes():void {
			var node:SimpleNode;
			_compDrawNode.graphics.clear();
			var s:String;
			for each( node in _graphData.nodes ) {
				if ( _mapPosition[node] == null ){					
					_mapPosition[node] = new Point(0,0);
				}
				DrawTool.drawNode(_compDrawNode,node,_mapPosition[node],node.size,Colour.getWeightColour(this._mapNodeColor[node]));
				}
		}
		
		private function drawNodeLabel():void {
			var node:INode;
		
			for each( node in _graphData.nodes ) {
				if ( _mapPosition[node] == null ){					
					_mapPosition[node] = new Point(0,0);
				}
				node.label.x = _mapPosition[node].x - node.label.textWidth/2;
				node.label.y = _mapPosition[node].y+5;
			}
		}
		
		//绘制边
		public function drawEdges():void {
			var edge:IEdge;
			var fromNode:INode;
			var toNode:INode;
			var fromX:Number;
			var fromY:Number;
			var toX:Number;
			var toY:Number;
			var arrowPosition:Array = new Array;
			var arrowX:Number;
			var arrowY:Number
			var component:UIComponent;

			for each ( component in _graphCanvas.getChildren() ){
				if ( component.id != null ){
					if ( component.id.substr(0,5) == "label")
						_graphCanvas.removeChild(component);
				}
			}
			_compDrawEdge.graphics.clear();
			var i:int = 0;
			//Alert.show("edge.length:"+_graphData.edges.length);
			for each(edge in _graphData.edges){
				//Alert.show(i+" "+edge.fromNode.id+" "+edge.toNode.id);
				//Alert.show(edge.fromNode.id+" "+edge.toNode.id);
				_compDrawEdge.graphics.lineStyle(1,Colour.BLUE,1);
				fromNode = edge.fromNode;
				toNode = edge.toNode;
				//Alert.show("e");
				if ( this._isFastMode == false ){
					fromX = fromNode.visualNode.x + fromNode.visualNode.width/2;
					fromY = fromNode.visualNode.y + fromNode.visualNode.nodeIcon.height/2;
					toX = toNode.visualNode.x + toNode.visualNode.width/2;
					toY = toNode.visualNode.y + toNode.visualNode.nodeIcon.height/2;
				}
				else {
					//Alert.show("s:"+fromNode.id);
					fromX = (_mapPosition[fromNode] as Point).x;
					//Alert.show("s1");
					fromY = (_mapPosition[fromNode] as Point).y;
					toX = (_mapPosition[toNode] as Point).x;
					toY = (_mapPosition[toNode] as Point).y;
				}
				//Alert.show("e1");
				if ( fromX != toX || fromY != toY ){
					_compDrawEdge.graphics.moveTo(fromX, fromY);
					_compDrawEdge.graphics.lineTo(toX, toY);
				}
				else {
					_compDrawEdge.graphics.drawCircle(fromX, fromY - 10, 10);
				}
				//Alert.show("e2");
				//绘制边上面的箭头
				if ( _showEdgeDirection == true ){
					_compDrawEdge.graphics.lineStyle(1,Colour.getWeightColour(this._mapEdgeColor[edge]),1);
					//添加颜色
					if ( fromX != toX || fromY != toY ){
						arrowX = fromX + (toX - fromX)*3/4;
						arrowY = fromY + (toY - fromY)*3/4;
						arrowPosition = Geometry.arrowPosition(fromX, fromY, arrowX, arrowY);
						_compDrawEdge.graphics.moveTo(arrowX , arrowY);
						_compDrawEdge.graphics.lineTo((arrowPosition[0] as Point).x, (arrowPosition[0] as Point).y);
						_compDrawEdge.graphics.moveTo(arrowX , arrowY);
						_compDrawEdge.graphics.lineTo((arrowPosition[1] as Point).x, (arrowPosition[1] as Point).y);
					}
					else {
						arrowX = fromX;
						arrowY = fromY;
						arrowPosition = Geometry.arrowPosition(fromX - 10, fromY - 5, arrowX, arrowY);
						_compDrawEdge.graphics.moveTo(arrowX , arrowY);
						_compDrawEdge.graphics.lineTo((arrowPosition[0] as Point).x, (arrowPosition[0] as Point).y);
						_compDrawEdge.graphics.moveTo(arrowX , arrowY);
						_compDrawEdge.graphics.lineTo((arrowPosition[1] as Point).x, (arrowPosition[1] as Point).y);
					}
				}
				//Alert.show("e3");
				//绘制边的标签
				if ( _showEdgeLabel == true ){
					var label:Text = new Text;
					label.id = "label"+edge.label;
					label.text = edge.label;
					if ( fromX != toX || fromY != toY ){
						label.x = fromX + (toX - fromX)*3/5;
						label.y = fromY + (toY - fromY)*3/5;
					}
					else {
						label.x = fromX;
						label.y = fromY-20;
					}
					_graphCanvas.addChildAt(label,2);//_graphCanvas.numChildren-1);
				}
				//Alert.show(i+" end");
				i++;
			}
			refreshPath(_graphData.nodes);
			//Alert.show("drawEdge End");
		}
		
		private function drawPath(path:Array):void {
			_compDraw.graphics.clear();
			if ( path.length < 2 )
				return ;
			_curPathIndex = 0;
			if ( _pathStyle == false ){
				_curPathIndex = path.length-1;
				refreshPath(path);
			}
			else
				flash.utils.setTimeout(pathAnimation, 50, path, 0);
		}
		
		private function refreshPath(path:Array):void {
			var fromNode:INode;
			var toNode:INode;
			var fromX:Number;
			var fromY:Number;
			var toX:Number;
			var toY:Number;
			var i:int;
			
			_compDraw.graphics.clear();
			_compDraw.graphics.lineStyle(2,Colour.RED,1);
			
			for ( i = 0 ; i < _curPathIndex ; i++ ){
				
				fromNode = path[i];
				toNode = path[i+1];
				
				if ( this._isFastMode == false ){
					fromX = fromNode.visualNode.x + fromNode.visualNode.width/2;
					fromY = fromNode.visualNode.y + fromNode.visualNode.nodeIcon.height/2;
					toX = toNode.visualNode.x + toNode.visualNode.width/2;
					toY = toNode.visualNode.y + toNode.visualNode.nodeIcon.height/2;
				}
				else {
					fromX = (_mapPosition[fromNode] as Point).x;
					fromY = (_mapPosition[fromNode] as Point).y;
					toX = (_mapPosition[toNode] as Point).x;
					toY = (_mapPosition[toNode] as Point).y;
				}
				
				if ( fromX != toX || fromY != toY ){
					_compDraw.graphics.moveTo(fromX, fromY);
					_compDraw.graphics.lineTo(toX, toY);
				}
				else {
					_compDraw.graphics.drawCircle(fromX, fromY - 10, 10);
				}
			}
		}
		private function pathAnimation(path:Array, curNodeIndex:int):void {
			var fromNode:INode;
			var toNode:INode;
			var fromX:Number;
			var fromY:Number;
			var toX:Number;
			var toY:Number;
			if ( this._showPath == false )
				return ;
			_compDraw.graphics.lineStyle(2,Colour.RED,1);
			fromNode = path[_curPathIndex];
			toNode = path[_curPathIndex+1];
			if ( this._isFastMode == false ){
				fromX = fromNode.visualNode.x + fromNode.visualNode.width/2;
				fromY = fromNode.visualNode.y + fromNode.visualNode.nodeIcon.height/2;
				toX = toNode.visualNode.x + toNode.visualNode.width/2;
				toY = toNode.visualNode.y + toNode.visualNode.nodeIcon.height/2;
			}
			else {
				fromX = (_mapPosition[fromNode] as Point).x;
				fromY = (_mapPosition[fromNode] as Point).y;
				toX = (_mapPosition[toNode] as Point).x;
				toY = (_mapPosition[toNode] as Point).y;
			}
			if ( fromX != toX || fromY != toY ){
				_compDraw.graphics.moveTo(fromX, fromY);
				_compDraw.graphics.lineTo(toX, toY);
			}
			else {
				_compDraw.graphics.drawCircle(fromX, fromY - 10, 10);
			}
			
			curNodeIndex++;

			_curPathIndex = curNodeIndex;
			if ( curNodeIndex == path.length - 1 ){
				return 
			}
			else {
				flash.utils.setTimeout(pathAnimation, 1000, path, curNodeIndex);
			}
		}
		private function centerAjust(evt:ResizeEvent):void {
			_center.x = _graphCanvas.width/2;
			_center.y = _graphCanvas.height/2;
		}
			
			
		//鼠标在组建上方停留时的响应函数
		private function mouseOver(evt:MouseEvent):void {
			//FastMode
			var visualNode:VisualNode = evt.target as VisualNode;
			visualNode.mouseOver();
		}
		//鼠标移出组件上方的事件响应函数
		private function mouseOut(evt:MouseEvent):void {
			//FastMode
			var visualNode:VisualNode = evt.target as VisualNode;
			visualNode.mouseOut();
		}
		
	    //鼠标点击节点的事件响应函数
		public function nodeMouseDown(evt:MouseEvent):void {
			//FastMode
			var visualNode:Canvas;
			var date:Date = new Date();
			_time = date.time;
			
			evt.stopImmediatePropagation();
			_targetNode = (evt.target as VisualNode).node;
			
			_dragCursorStartX = evt.stageX;
			_dragCursorStartY = evt.stageY;
			
			_graphCanvas.removeEventListener(MouseEvent.MOUSE_MOVE, backgroundDragContinue);
			_graphCanvas.addEventListener(MouseEvent.MOUSE_MOVE, nodeDragContinue);
			_graphCanvas.addEventListener(MouseEvent.MOUSE_UP,nodeMouseUp);
		}
		
		//拖动节点的事件响应函数
		private function nodeDragContinue(evt:MouseEvent):void {
			if ( _isFastMode == false ){
				_targetNode.visualNode.x += evt.stageX - _dragCursorStartX;
				_targetNode.visualNode.y += evt.stageY - _dragCursorStartY;
				(_mapPosition[_targetNode] as Point).x += evt.stageX - _dragCursorStartX;
				(_mapPosition[_targetNode] as Point).y += evt.stageY - _dragCursorStartY;
				drawNodeLabel();
			}
			else {
				(_mapPosition[_targetNode] as Point).x += evt.stageX - _dragCursorStartX;
				(_mapPosition[_targetNode] as Point).y += evt.stageY - _dragCursorStartY;
				drawSimpleNodes();
				drawNodeLabel();
			}

			_dragCursorStartX = evt.stageX;
			_dragCursorStartY = evt.stageY;
			drawEdges();
			drawCommunities();
		}
	
		//鼠标在节点上弹起的事件响应函数
		private function nodeMouseUp(evt:MouseEvent):void {
			
			evt.stopImmediatePropagation();
			_graphCanvas.removeEventListener(MouseEvent.MOUSE_MOVE, nodeDragContinue);
			_graphCanvas.removeEventListener(MouseEvent.MOUSE_UP,nodeMouseUp);
			
			if ( _multiSelectStyle == false )
				_graphCanvas.addEventListener(MouseEvent.MOUSE_DOWN,mouseDown);
			var date:Date = new Date();
			var timeInterval:int;
			//记录鼠标点下的时长
			timeInterval = date.time - _time;
			if ( timeInterval > CLICK_INTERVAL ){
				//Alert.show("timeInterval="+timeInterval+"\nCLICK_INTERVAL="+CLICK_INTERVAL);
				_disableClick = true;
			}

		}
		//节点单击事件的响应函数
		private function nodeClick(evt:MouseEvent):void {
			//FastMode
			_targetNode = (evt.target as VisualNode).node;
			_isDoubleClick = false;
			var timer:Timer = new Timer(260,1);
			timer.addEventListener(TimerEvent.TIMER_COMPLETE, nodeClickOrDouble);
			timer.start();
		}
		private function nodeDoubleClick(evt:MouseEvent):void {
			//FastMode
			_targetNode = (evt.target as VisualNode).node;
			_isDoubleClick = true;
		}
		private function nodeClickOrDouble(evt:TimerEvent):void {
			if ( _disableClick == true ){
				//Alert.show("disable!");
				_disableClick = false;
				return ;
			}
			if ( _isDoubleClick ){
				//Alert.show("doubleClick");
				nodeDoubleClickHandle();
			}
			else {
				//Alert.show("click");
				nodeClickHandle();
			}
		}
		private function nodeClickHandle():void {
			//FastMode
			var event:NodeClickEvent = new NodeClickEvent(NodeClickEvent.SINGLE_CLICK);
			event.nodeId = _targetNode.id;
			event.nodeData = _targetNode.dataObject as XML;
			_graphCanvas.dispatchEvent(event);
			if ( _multiSelectStyle == true ){	//多选模式下的选择操作
					var currentSelect:Boolean = _targetNode.visualNode.select;
					if ( currentSelect == false )
						_targetNode.visualNode.seleted();
					else 
						_targetNode.visualNode.unSeleted();

			}
		}
		//节点双击的事件响应函数
		private function nodeDoubleClickHandle():void {
			var node:INode;
			var position:Point;
	
			//FastMode
			if ( _rootChangeAction == true ){
				_mapPositionFrom = new Dictionary;
				for each ( node in _graphData.nodes ){
					position = new Point;				
					position.x = this._mapPosition[node].x;
					position.y = this._mapPosition[node].y;
					_mapPositionFrom[node] = position;
				}
				
				//设置新的根节点 current action is to change a root and redraw a new net
				if ( _root != _targetNode ){
					_root = _targetNode;
					_zoomScale = 1;
					_layout.root = _root;
				}
				_mapPosition = _layout.placement();
				//Alert.show("change root");
				for each ( node in _graphData.nodes ){
					position = new Point;				
					position.x = (_mapPosition[node] as Point).x;
					position.y = (_mapPosition[node] as Point).y;
					_mapPositionTo[node] = position;
				}
				flash.utils.setTimeout(animation, 50, 1);
			}
			var event:NodeClickEvent = new NodeClickEvent(NodeClickEvent.DOUBLE_CLICK);
			event.nodeId = _targetNode.id;
			event.nodeData = _targetNode.dataObject as XML;
			_graphCanvas.dispatchEvent(event);		
		}
	    //网络图像的动画转换函数
		private function animation():void {
			var node:INode;
			var position:Point = new Point;
			var angleFrom:Number;
			var angleTo:Number;
			var angleIn:Number;
			var radiusFrom:Number;
			var radiusTo:Number;
			var radiusIn:Number;
			var step:int;
			//记录当前的周期次数
			step = int(arguments[0]);
			//动画效果分为8步，每次移动角度的八分之一，半径距离的八分之一
			for each ( node in _graphData.nodes ){
				angleFrom = Geometry.getAngle((_mapPositionFrom[node] as Point).x, (_mapPositionFrom[node] as Point).y , _center);
				radiusFrom = Math.sqrt(Math.pow(((_mapPositionFrom[node] as Point).y - _center.y),2)+Math.pow(((_mapPositionFrom[node] as Point).x - _center.x),2));
				angleTo = Geometry.getAngle((_mapPositionTo[node] as Point).x, (_mapPositionTo[node] as Point).y , _center);
				radiusTo = Math.sqrt(Math.pow(((_mapPositionTo[node] as Point).y - _center.y),2)+Math.pow(((_mapPositionTo[node] as Point).x - _center.x),2));
				radiusIn = radiusFrom + (radiusTo - radiusFrom)*step/8;

				if ( angleFrom >= 0 && angleFrom <= Math.PI ){
					if ( (angleTo <= angleFrom+Math.PI) && (angleTo >= angleFrom) ){
						angleIn = angleFrom + (angleTo - angleFrom + 2*Math.PI )%(2*Math.PI)*step/8;
					}
					else{
						angleIn = angleFrom - (angleFrom - angleTo + 2*Math.PI )%(2*Math.PI)*step/8;
					}
				}
				else {
					if ( angleTo >= (angleFrom + 3*Math.PI ) % (2*Math.PI) && angleTo <= angleFrom ){
						angleIn = angleFrom - (angleFrom - angleTo + 2*Math.PI )%(2*Math.PI)*step/8;
					}
					else{
						angleIn = angleFrom + (angleTo - angleFrom + 2*Math.PI )%(2*Math.PI)*step/8;
					}
				}
				position = Point.polar(radiusIn, angleIn);
				position.x = position.x + _center.x;
				position.y = position.y + _center.y;
				_mapPosition[node] = position;
			}
			drawNodes();
			drawEdges();
			drawCommunities();
			step++;
			if ( step <= 8 ){
				flash.utils.setTimeout(animation,30, step); //step为当前周期次数的补充向量
			}else{
				calNetRegion();
			}
		}
		private function click(evt:MouseEvent):void {
			var x:Number = evt.localX;
			var y:Number = evt.localY;
			var node:INode;
			for each ( node in _graphData.nodes ){
				if ( x < this._mapPosition[node].x+node.size/2 && x > this._mapPosition[node].x - node.size/2
					&& y < this._mapPosition[node].y+node.size/2 && y > this._mapPosition[node].y - node.size/2){
						_targetNode = node;

						_isDoubleClick = false;
						var timer:Timer = new Timer(260,1);
						timer.addEventListener(TimerEvent.TIMER_COMPLETE, nodeClickOrDouble);
						timer.start();
						return;
					}
			}
		}
		private function doubleClick(evt:MouseEvent):void {
			var x:Number = evt.localX;
			var y:Number = evt.localY;
			var node:INode;
			for each ( node in _graphData.nodes ){
				if ( x < this._mapPosition[node].x+node.size/2 && x > this._mapPosition[node].x - node.size/2
					&& y < this._mapPosition[node].y+node.size/2 && y > this._mapPosition[node].y - node.size/2){
						_targetNode = node;		
						_isDoubleClick = true;
						return;
					}
			}
		}
		private function mouseDown(evt:MouseEvent):void {
			var x:Number = evt.localX;
			var y:Number = evt.localY;
			var node:INode;
			var date:Date = new Date();
			_time = date.time;
			for each ( node in _graphData.nodes ){
				if ( x < this._mapPosition[node].x+node.size/2 && x > this._mapPosition[node].x - node.size/2
					&& y < this._mapPosition[node].y+node.size/2 && y > this._mapPosition[node].y - node.size/2){
						_targetNode = node;
						
						_dragCursorStartX = evt.stageX;
						_dragCursorStartY = evt.stageY;
						_graphCanvas.removeEventListener(MouseEvent.MOUSE_MOVE, backgroundDragContinue);
						
						_graphCanvas.addEventListener(MouseEvent.MOUSE_MOVE, nodeDragContinue);
						_graphCanvas.addEventListener(MouseEvent.MOUSE_UP,nodeMouseUp);

						return;
					}
			}
			backgroundDragBegin(evt);
		}
		//整个网络拖动的响应函数（拖动开始）
		private function backgroundDragBegin(evt:MouseEvent):void {
			trace("bkgDragBegin");
			evt.stopImmediatePropagation();
			_dragCursorStartX = evt.stageX;
			_dragCursorStartY = evt.stageY;
			_graphCanvas.addEventListener(MouseEvent.MOUSE_MOVE, backgroundDragContinue);
			_graphCanvas.addEventListener(MouseEvent.MOUSE_UP,backgroundDragEnd);
		}
		//整个网络拖动的响应函数（拖动中）
		private function backgroundDragContinue(evt:MouseEvent):void { 
			trace("bkgDragContinue");
			var node:INode;
			var moveX:Number;
			var moveY:Number;
			moveX = evt.stageX - _dragCursorStartX;
			moveY = evt.stageY - _dragCursorStartY;
			this;
			for each ( node in _graphData.nodes ) {
				(this._mapPosition[node] as Point).x += moveX;
				(this._mapPosition[node] as Point).y += moveY;
			}
			
			drawNodes();
			drawEdges();
			
			drawCommunities();
			_dragCursorStartX = evt.stageX;
			_dragCursorStartY = evt.stageY;
	
		}
		//整个网络拖动的响应函数（拖动结束）
		private function backgroundDragEnd(evt:MouseEvent):void {
			evt.stopImmediatePropagation();
			_graphCanvas.removeEventListener(MouseEvent.MOUSE_MOVE, backgroundDragContinue);	
			_graphCanvas.removeEventListener(MouseEvent.MOUSE_UP,backgroundDragEnd);		
		}
		//多选节点的选择响应函数（选择开始）
		private function multiSelectBegin(evt:MouseEvent):void {
			evt.stopImmediatePropagation();
			//创建多选框
			_selectCanvas = new Canvas;
			_selectCanvas.name = "selectCanvas";
			_selectCanvas.x = evt.localX;
			_selectCanvas.y = evt.localY;
			_dragCursorStartX = evt.stageX;
			_dragCursorStartY = evt.stageY;
			_selectCanvas.setStyle("backgroundColor","#3A5FCD");
			_selectCanvas.alpha = 0.3;
			_graphCanvas.addChild(_selectCanvas);
			_graphCanvas.addEventListener(MouseEvent.MOUSE_MOVE, multiSelectContinue);
			_graphCanvas.addEventListener(MouseEvent.MOUSE_UP, multiSelectEnd);
		}
		//多选节点的选择响应函数（选择中）
		private function multiSelectContinue(evt:MouseEvent):void {
			//修改多选框的尺寸
			_selectCanvas.width = evt.stageX - _dragCursorStartX;
			_selectCanvas.height = evt.stageY - _dragCursorStartY;
			//检查节点是否被选中
			checkSelectedNode(_selectCanvas);
		}
		//多选节点的选择响应函数（选择结束）
		private function multiSelectEnd(evt:MouseEvent):void {
			_graphCanvas.removeChild(_selectCanvas);
			_graphCanvas.removeEventListener(MouseEvent.MOUSE_MOVE, multiSelectContinue);
			_graphCanvas.removeEventListener(MouseEvent.MOUSE_UP, multiSelectEnd);
		}
		//多选节点的选择响应函数，判断当前节点是否在选择范围内
		private function checkSelectedNode(selectCanvas:Canvas):void {
			var node:INode;
			var position:Point;
			var leftX:Number;
			var rightX:Number;
			var topY:Number;
			var bottomY:Number;
			//获得多选框的坐标范围
			leftX = Math.min(selectCanvas.x, selectCanvas.x + selectCanvas.width);
			rightX = Math.max(selectCanvas.x, selectCanvas.x + selectCanvas.width);
			topY = Math.min(selectCanvas.y, selectCanvas.y + selectCanvas.height);
			bottomY = Math.max(selectCanvas.y, selectCanvas.y + selectCanvas.height);
			//遍历节点，检查节点是否落在多选框中
			for each ( node in _graphData.nodes ){
				position = _mapPosition[node] as Point;
				//如果节点超出范围
				if ( (position.x + node.visualNode.width) > rightX || 
					 (position.y + node.visualNode.height) > bottomY ||
					 position.x < leftX || 
					 position.y  < topY ){
					//如果ctrl键没有被按下，那么超出范围的节点都标记为未选中
					if ( _ctrlKey == false )
						node.visualNode.unSeleted();
				}
				//如果节点落在多选框范围内
				else {
					node.visualNode.seleted();
				}
			}
		}
		//鼠标移出画布的事件响应函数
		private function mouseOutCanvasHandle(evt:MouseEvent):void {
			var pt:Point = new Point(evt.stageX, evt.stageY);
			pt = _graphCanvas.globalToLocal(pt);
			if ( pt.x < 0 || pt.y < 0 || pt.x > _graphCanvas.width || pt.y > _graphCanvas.height ){
				//如果当前是多选模式，用户在使用多选框
				if ( _graphCanvas.getChildByName("selectCanvas") != null ){
					_graphCanvas.removeChild(_selectCanvas);
					_graphCanvas.removeEventListener(MouseEvent.MOUSE_MOVE, multiSelectContinue);
					_graphCanvas.removeEventListener(MouseEvent.MOUSE_UP, multiSelectEnd);
				}
			}
			
		}
		//从映射mapPosition中更新最新的节点位置到graphData中
		private function updatePosition():void {
			var node:INode;
			for each( node in _graphData.nodes ) {
				node.visualNode.x = (_mapPosition[node] as Point).x;
				node.visualNode.y = (_mapPosition[node] as Point).y;
			}
		}
		
		//从graphData中更新最新的节点位置到映射mapPosition中
		public function updateMapPosition():void {
			var node:INode;
			for each ( node in _graphData.nodes ){
				(_mapPosition[node] as Point).x = node.visualNode.x + node.visualNode.width/2;
				(_mapPosition[node] as Point).y = node.visualNode.y + node.visualNode.nodeIcon.height/2;
			}
		}
		
	    //计算当前网络的尺寸，用一个矩形变量来表示
		public function calNetRegion():void {
			var top:Number;
			var left:Number;
			var node:INode;
			var position:Point;
			if ( _graphData.nodes.length == 0 )
				return ;
			top = 0;
			left = 0;
			_netRegion.x = _graphCanvas.width;
			_netRegion.y = _graphCanvas.height;
			//遍历所有节点，找出最左，最右，最上，最下点的边界坐标
			for each (node in _graphData.nodes) {
				position = this._mapPosition[node] as Point;
				if ( position.x > left )
					left = position.x;
				if ( position.y < _netRegion.y )
					_netRegion.y = position.y;
				if ( position.x < _netRegion.x )
					_netRegion.x = position.x;
				if ( position.y > top )
					top = position.y;
			}
			
			_netRegion.height = top - _netRegion.y;
			_netRegion.width = left - _netRegion.x;

		}
		public function findCommunities():void {
			_communities =  _communityDectection.detection();
			var community:Array;
			var i:int = 0;
			var node:INode;
			_mapNodeCommunity = new Dictionary;
			for each ( community in _communities ){
				for each ( node in community ){
					_mapNodeCommunity[node] = i;
				}
				i++;
			}
			drawCommunities();
		}
		public function get mapNodeCommunity():Dictionary {
			return _mapNodeCommunity;
		}
		private function drawCommunities():void {
			//clearCommunities();
			if ( _showCommunities == false || _communities.length <= 0){
				_drawCommunities.graphics.clear();
				return ;
			}
			drawConvexHull();
		}
		private function clearCommunities():void {
			var node:INode;
			//清楚当前画布上的社团阴影
			_drawCommunities.graphics.clear();
			//FastMode
			for each ( node in _graphData.nodes ){
				node.visualNode.colorDefault();
			}
			
		}
		private function drawColorNode():void {
			var community:Array;		//单个社团的节点数组
			var node:INode;
			var colorNum:int = 0;
			var color:int;
			if ( _showCommunities == false || _communities.length <= 0)
				return ;
			for each ( community in _communities ){
				color = Colour.getColour(colorNum);
				//FastMode
				for each ( node in community ) {
					node.visualNode.color = color;
				}
				colorNum++;		//选择下一个颜色
			}
		}
		//用凸包绘制社团
		private function drawConvexHull():void {
			var community:Array;		//单个社团的节点数组
			var convexHull:Array;		//当个社团的凸包数组
			var convexHullArray:Array = new Array();	//所有社团凸包的数组
			var node:INode;
			var CH:ConvexHull;		//凸包的功能类
			var colorNum:int = 0;
			var radius:Number = 20;		//圆弧角的半径
			var rectangle:Array;
			var controlPoint:Point;		//画曲线的辅助点
			var fromPoint:Point;
			var toPoint:Point;
			//清楚当前画布上的社团阴影
			_drawCommunities.graphics.clear();
			//如果当前不是社团模式或者当前社团长度小于1个
			if ( _showCommunities == false || _communities.length <= 0)
				return ;
			_convexHulls = new Array;
			//分别计算每个社团的凸包
			for each ( community in _communities ){
				CH = new ConvexHull(this,community);
				convexHull = CH.find();
				_convexHulls.push(convexHull);
			}
			//分别绘制每个社团的凸包
			for each ( convexHull in _convexHulls ) {
				if ( convexHull.length == 1 ){	//如果凸包集只有一个点
					_drawCommunities.graphics.beginFill(Colour.getColour(colorNum),0.5);
					node = convexHull[0] as INode;
					//画一个园，把单节点社团包起来
					_drawCommunities.graphics.drawCircle((_mapPosition[node] as Point).x, (_mapPosition[node] as Point).y, radius); 	
					_drawCommunities.graphics.endFill();
				}
				else if ( convexHull.length == 2 ){	//如果凸包集有两个点
					var point:Point;
					var center:Point;
					var angle:Number;
					rectangle = Geometry.line2rectangle((_mapPosition[convexHull[0]] as Point), (_mapPosition[convexHull[1]] as Point), radius);
					_drawCommunities.graphics.beginFill(Colour.getColour(colorNum),0.5);
					//画一个圆角矩形将两个节点的社团包起来
					//移动到起始点
					point = (rectangle[0] as Point);
					_drawCommunities.graphics.moveTo(point.x, point.y);
			
					//画一条直边
					point = (rectangle[3] as Point);
					_drawCommunities.graphics.lineTo(point.x, point.y);
					
					//画圆角曲线
					point = (_mapPosition[convexHull[1]] as Point);
					angle = Math.atan2( (rectangle[3] as Point).y - (rectangle[0] as Point).y, (rectangle[3] as Point).x - (rectangle[0] as Point).x);
					controlPoint = new Point;
					controlPoint = Point.polar(radius*2, angle);
					controlPoint.x += point.x;
					controlPoint.y += point.y;
					_drawCommunities.graphics.curveTo(controlPoint.x, controlPoint.y, (rectangle[2] as Point).x, (rectangle[2] as Point).y );
					
					//画另一条直边
					point = (rectangle[1] as Point);
					_drawCommunities.graphics.lineTo(point.x, point.y);
					
					//画另一条圆角曲线
					angle = Math.atan2( (rectangle[1] as Point).y - (rectangle[2] as Point).y, (rectangle[1] as Point).x - (rectangle[2] as Point).x);
					controlPoint = new Point;
					controlPoint = Point.polar(radius*2, angle);
					point = (_mapPosition[convexHull[0]] as Point);
					controlPoint.x += point.x;
					controlPoint.y += point.y;
					_drawCommunities.graphics.curveTo(controlPoint.x, controlPoint.y, (rectangle[0] as Point).x, (rectangle[0] as Point).y );
					//填充颜色
					_drawCommunities.graphics.endFill();
				}
				else if ( convexHull.length > 2 ){	//如果凸包集中大于2个节点
					var lastPoint:Point;
					var curPoint:Point;
					var i:int;

					//绘制凸包
					_drawCommunities.graphics.beginFill(Colour.getColour(colorNum),0.5);
					//找到当前的起始点和终点
					toPoint = _mapPosition[convexHull[0]] as Point;
					fromPoint = _mapPosition[convexHull[convexHull.length-1]] as Point;
					//将直线转换成以直线为中心线的矩形
					rectangle = Geometry.line2rectangle(fromPoint, toPoint, radius);
					//当前绘制直线的终点为矩形的一个角
					curPoint = rectangle[3] as Point;
					_drawCommunities.graphics.moveTo(curPoint.x, curPoint.y);
					
					for ( i = 0 ; i < convexHull.length ; i++ ){
						//当前的起始点
						fromPoint =  (_mapPosition[convexHull[i]] as Point);
						//当前的终点，如果当前的起始点是数组的最后一个点，那么终点就是整个集合的起始点
						if ( i == convexHull.length - 1 )
							toPoint =  _mapPosition[convexHull[0]] as Point;
						else
							toPoint =  _mapPosition[convexHull[i+1]] as Point;
						//将直线转换成以直线为中心线的矩形
						rectangle = Geometry.line2rectangle( fromPoint, toPoint, radius);
						//找到圆角的曲线辅助点
						controlPoint = Geometry.getControlPoint( fromPoint, curPoint, (rectangle[0] as Point));
						if ( controlPoint == null ) 
							throw Error("controlPoint == null");
						//先绘制圆角曲线
						_drawCommunities.graphics.curveTo(controlPoint.x, controlPoint.y, (rectangle[0] as Point).x, (rectangle[0] as Point).y );
						//再沿矩形的外边绘制一条直线
						_drawCommunities.graphics.lineTo((rectangle[3] as Point).x, (rectangle[3] as Point).y);
						//记录当前画笔的坐标
						curPoint = (rectangle[3] as Point).clone();
					}
					//填充颜色
					_drawCommunities.graphics.endFill();
					
				}
				colorNum++;		//选择下一个颜色
			}
			
		}
		
		public function filter(minNodeW:Number = -1,maxNodeW:Number = -1,minEdgeW:Number = -1,maxEdgeW:Number = -1):void {
			_graphData = initFromXML(_xmlData,minNodeW,maxNodeW,minEdgeW,maxEdgeW);
			updateFilteredGraph();
		}
		
		protected function updateFilteredGraph():void {
			var i:int = 0;
			var node:INode;
			var component:UIComponent;
			var nodeArr:Array = new Array;

/* 			for each ( node in _graphData.nodes ){
				if ( node.edges.length != 0 ) {
					nodeArr.push(node);
				}
			}
			_graphData.nodes = nodeArr; */
			for each ( component in _graphCanvas.getChildren() ){
				if ( component.id != null ){
					if ( component.id.substr(0,9) == "nodeLabel")
						_graphCanvas.removeChild(component);
				}
			}
			if ( _graphCanvas != null ){
				for each (node in _graphData.nodes){
					node.label.id = "nodeLabel"+i;
					_graphCanvas.addChildAt(node.label,_graphCanvas.numChildren-1);
					i++;
				}
			}
		}
		
		public function singleNodefilter(nodeId:String, depth:int, direction:Boolean = true):void {
			var arrNodes:Array = new Array;
			var arrEdges:Array = new Array;
			var queue:Array = new Array;
			var newQeueu:Array = new Array;
			var mapVisit:Dictionary = new Dictionary;
			var node:INode;
			var curNode:INode;
			var adjNode:INode;
	
			var i:int = 0;
			filter(-1, -1, -1, -1);
			curNode = _graphData.getNodeById(nodeId);
			if ( curNode == null )
				return ;
			_root = curNode;
			arrNodes.push(curNode);
			mapVisit[curNode] = true;
			queue.push(_graphData.getNodeById(nodeId));
			var s:String = "";
			for ( i = 0 ; i < depth ; i++) {
				//s += i+":\n";
				for each ( curNode in queue ){
					//s += "***"+curNode.id+"***\n";
					for each ( adjNode in curNode.outEdges ){
						if ( mapVisit[adjNode] == null){
							arrNodes.push(adjNode);
							newQeueu.push(adjNode);
							mapVisit[adjNode] = true;
							//s += adjNode.id+"\n";
						}
						arrEdges.push(_graphData.getEdgeByNodeId(curNode.id,adjNode.id));
					}
					if ( direction == false ){
						for each ( adjNode in curNode.inEdges ){
							if ( mapVisit[adjNode] == null){
								arrNodes.push(adjNode);
								newQeueu.push(adjNode);
								mapVisit[adjNode] = true;
								
								//s += adjNode.id+"\n";
							}
							arrEdges.push(_graphData.getEdgeByNodeId(adjNode.id,curNode.id));
						}
					}
 					 
				}
				//s += "==================\n";
				queue = newQeueu.concat();
				newQeueu = new Array;
			}
			//Alert.show(s);
			_graphData.nodes = arrNodes;
			_graphData.edges = arrEdges;
			
			//更新画布上的节点标签
			var component:UIComponent;
			for each ( component in _graphCanvas.getChildren() ){
				if ( component.id != null ){
					if ( component.id.substr(0,9) == "nodeLabel")
						_graphCanvas.removeChild(component);
				}
			}
			if ( _graphCanvas != null ){
				for each (node in _graphData.nodes){
					node.label.id = "nodeLabel"+i;
					_graphCanvas.addChildAt(node.label,_graphCanvas.numChildren-1);
					i++;
				}
			}
		}
		
		
		//复制GraphData
		public function copyGraphData():IGraphData {
			var graphData:IGraphData = new GraphData();
			graphData.nodes = _graphData.nodes.concat();
			graphData.edges = _graphData.edges.concat();
			return graphData;
		}
		
		public function nodeIcon(image:Class):void {
			var node:INode;
			//FastMode
			for each ( node in _graphData.nodes ){
				node.visualNode.image = image;
			}
			drawEdges();
		}
		public function stopLayout():void {
			if ( _layout != null ) 
				_layout.stop();
		}
		public function startLayout():void {
			if ( _layout != null ) 
				_layout.start();
		}
		public function setBaseLayout():void {
			stopLayout();
			_layout = new BaseLayout();
			_layout.init(_graphData,_graphCanvas);
		}
		public function setCircleLayout():void {
			stopLayout();
			_layout = new CircleLayout();
			_layout.init(_graphData,_graphCanvas);
		}
		public function setConcentricRadialLayout():void {
			stopLayout();
			_layout = new ConcentricRadialLayout();
			_layout.init(_graphData,_graphCanvas);
		}
		public function setRadialTreeLayout():void {
			stopLayout();
			_layout = new RadialTreeLayout();
			_layout.init(_graphData,_graphCanvas);
		}
		public function setTreeLayout():void {
			stopLayout();
			_layout = new TreeLayout();
			_layout.init(_graphData,_graphCanvas);
		}
		public function setTreeHorizonLayout():void {
			stopLayout();
			_layout = new TreeLayoutHorizon();
			_layout.init(_graphData,_graphCanvas);
		}
		public function setForceDirectedLayout():void {
			stopLayout();
			_layout = new ForceDirectedLayout();
			_layout.init(_graphData,_graphCanvas);
			(_layout as ForceDirectedLayout).graph = this;
		}

		public function setCliquePercolation():void {
			_communityDectection = new CliquePercolation(_graphData);
			findCommunities();
		}
		public function setGN():void {
			_communityDectection = new GN(_graphData);
			findCommunities();
		}
		public function setFastGN():void {
			_communityDectection = new FastGN(_graphData);
			findCommunities();
		}
		public function setDefaultCommunities():void {
			_communityDectection = new DefaultCommunities(_graphData);
			findCommunities();
		}
		
		
		
		//配置画布的各个参数和响应函数
		public function set canvas(canvas:Canvas):void {
			_graphCanvas = canvas;
			_graphCanvas.doubleClickEnabled = true;
			if ( _multiSelectStyle == true ){	//如果是多选模式，去掉拖拽画布的事件，加入多选开始的事件
				_graphCanvas.removeEventListener(MouseEvent.MOUSE_DOWN,mouseDown);
				_graphCanvas.addEventListener(MouseEvent.MOUSE_DOWN,multiSelectBegin);
			}
			else { //如果不是多选模式，去掉多选开始的事件，加入拖拽画布的事件
				_graphCanvas.removeEventListener(MouseEvent.MOUSE_DOWN,multiSelectBegin);
				_graphCanvas.addEventListener(MouseEvent.MOUSE_DOWN,mouseDown);
			}
			_graphCanvas.addEventListener(MouseEvent.CLICK,click);
			_graphCanvas.addEventListener(MouseEvent.DOUBLE_CLICK,doubleClick);
			_graphCanvas.addEventListener(MouseEvent.MOUSE_WHEEL,changeSize);	//鼠标滑轮的缩放响应事件
			_graphCanvas.stage.addEventListener(KeyboardEvent.KEY_DOWN,keyDown);	//z键按下与旋转功能的响应事件
			_graphCanvas.stage.addEventListener(KeyboardEvent.KEY_UP,keyUp);		//z键弹起与旋转功能的响应事件
			_graphCanvas.addEventListener(MouseEvent.MOUSE_OUT, mouseOutCanvasHandle);	//鼠标滑出画布的响应事件
			_graphCanvas.addEventListener(ResizeEvent.RESIZE, centerAjust);
			_center.x = canvas.width/2;
			_center.y = canvas.height/2;
			var node:INode;
			_graphCanvas.addChildAt(_compDrawNode,0);		//绘制图像的组件
			_graphCanvas.addChildAt(_compDraw,0);
			_graphCanvas.addChildAt(_compDrawEdge,0);		//绘制图像的组件
			_graphCanvas.addChildAt(_drawCommunities,0);		//绘制社团的组件
			//FastMode
			if ( _isFastMode == false ){
				for each (node in _graphData.nodes){
					_graphCanvas.addChildAt(node.visualNode,_graphCanvas.numChildren-1);
				}	
			}
			var i:int = 0;
			for each (node in _graphData.nodes){
				node.label.id = "nodeLabel"+i;
				_graphCanvas.addChildAt(node.label,_graphCanvas.numChildren-1);
				i++;
			}
			//实例化当前画布
			_graphCanvas.validateNow();
			
		}
		private var prePageBtn:Button;
		private var nextPageBtn:Button;
		private var pageTip:Label;
		protected function setCanvasPageComp(canvas:Canvas):void {
			var height:int = canvas.height - 30;
			var padding:int = 20;
			var btnWidth:int = 50;
			var tipWidth:int = 20;
			prePageBtn = new Button;
			nextPageBtn = new Button;
			pageTip = new Label;
			canvas.addChild(prePageBtn);
			canvas.addChild(nextPageBtn);
			canvas.addChild(pageTip);
			
			prePageBtn.label = "上一页";
			nextPageBtn.label = "下一页";
			pageTip.text = "1/" + this._graphParts.length;
		
			prePageBtn.x = 0;
			prePageBtn.y = height;
			pageTip.x = btnWidth + padding;
			pageTip.y = height;
			nextPageBtn.x = btnWidth + padding + tipWidth + padding;
			nextPageBtn.y = height;
			
			prePageBtn.addEventListener(MouseEvent.CLICK, prePageBtnClick);
			nextPageBtn.addEventListener(MouseEvent.CLICK, nextPageBtnClick);
		}
		//_curPart
		private function prePageBtnClick(evt:MouseEvent):void {
			evt.stopImmediatePropagation();
			if ( this._curPart > 0 ){
				this._curPart --;
				pageTip.text = (this._curPart+1) + "/" + this._graphParts.length;
				this.draw();
			}
			
		}
		private function nextPageBtnClick(evt:MouseEvent):void {
			evt.stopImmediatePropagation();
			var partsCnt:int = this._graphParts.length;
			if ( this._curPart < partsCnt - 1 ){
				this._curPart ++;
				pageTip.text = (this._curPart+1) + "/" + this._graphParts.length;
				this.draw();
			}
			
		}
		public function get canvas():Canvas {
			return _graphCanvas;
		}
		public function set layout(layout:ILayout):void {
			if ( _layout != null ) 
				_layout.stop();
			_layout = layout;
			_layout.init(_graphData, _graphCanvas);
		}
		public function get layout():ILayout {
			return _layout;
		}
		public function get layoutName():String {
			if ( _layout != null )
				return _layout.layoutName;
			else
				return null;
		}
		public function get nodes():Array {
			return _graphData.nodes;
		}
		public function set isFastMode(b:Boolean):void {
			_isFastMode = b;
		}
		public function get isFastMode():Boolean {
			return _isFastMode;
		}
		public function set isDirected(b:Boolean):void {
			_directional = b;
			_graphData.isDirected = _directional;
		}
		public function get isDirected():Boolean {
			return _directional;
		}
		public function get center():Point {
			return _center;
		}
		public function set center(p:Point):void {
			_center = p;
		}
		public function get mapPosition():Dictionary {
			return _mapPosition;
		}
		public function get edges():Array {
			return _graphData.edges;
		}
		public function get netRegion():Rectangle {
			return _netRegion;
		}
		public function set showCommunities(b:Boolean):void {
			if ( b == true ){
				if ( _layout != null ) {
					if ( _layout.finished == true || _layout.isRun == false)
						_showCommunities = b;
				}
				else
					_showCommunities = b;
					
			}
			else
				_showCommunities = b;
		}
		public function get showCommunities():Boolean {
			return	_showCommunities;
		}
		public function set showPath(b:Boolean):void {
			_showPath = b;
			if ( _showPath == true ){
				_path = _graphData.nodes;
				this.drawPath(_path);	
			}
			else {
				this._curPathIndex = 0;
				this.refreshPath(_path);
			}
		}
		public function get showPath():Boolean {
			return	_showPath;
		}
		public function set pathStyle(b:Boolean):void {
			_pathStyle = b;
		}
		public function get pathStyle():Boolean {
			return _pathStyle;
		}
		
		public function set showEdgeLabel(b:Boolean):void {
			_showEdgeLabel = b;
		}
		public function get showEdgeLabel():Boolean {
			return _showEdgeLabel;
		}
		public function set showEdgeDirection(b:Boolean):void {
			_showEdgeDirection = b;
		}
		public function get showEdgeDirection():Boolean {
			return _showEdgeDirection;
		}
		public function set nodeStyle(b:Boolean):void {
			if ( this._isFastMode == true )
				return ;
			_nodeStyle = b;
			var node:INode;
			for each (node in _graphData.nodes){
				node.visualNode.style = _nodeStyle;
			}
		}
		public function get nodeStyle():Boolean {
			return _nodeStyle;
		}

		public function set communitiesStyle(b:Boolean):void {
			if ( _communitiesStyle != b ){
				_communitiesStyle = b;
			}
		}
		public function get communitiesStyle():Boolean {
			return communitiesStyle;
		}
		public function set stretchStyle(b:Boolean):void {
			//_stretchStyle = b;
		}
		public function get stretchStyle():Boolean {
			//return _stretchStyle;
			return false;
		}
		public function set multiSelectStyle(b:Boolean):void {
			_multiSelectStyle = b;
			var node:INode;
			if ( _multiSelectStyle == true ){	//如果是多选模式，去掉拖拽画布的事件，加入多选开始的事件
				_graphCanvas.removeEventListener(MouseEvent.MOUSE_DOWN,mouseDown);
				_graphCanvas.addEventListener(MouseEvent.MOUSE_DOWN,multiSelectBegin);
			}
			else {	//如果不是多选模式，去掉多选开始的事件，加入拖拽画布的事件
				_graphCanvas.removeEventListener(MouseEvent.MOUSE_DOWN,multiSelectBegin);
				_graphCanvas.addEventListener(MouseEvent.MOUSE_DOWN,mouseDown);
				//FastMode
				if ( _isFastMode == false ){
					for each ( node in _graphData.nodes ) {
						if ( node.visualNode.select == true )
							node.visualNode.unSeleted();
					}
				}
				
			}
		}
		public function get multiSelectStyle():Boolean {
			return _multiSelectStyle;
		}
		public function get multiSelectResult():Array {
			_multiSelectResult = new Array;
			var node:INode;
			//FastMode
			for each ( node in _graphData.nodes ){
				if ( node.visualNode.select == true ){
					_multiSelectResult.push(node.id);
				}
			}
			return _multiSelectResult;
		}
		public function set rootChangeAction(b:Boolean):void {
			_rootChangeAction = b;
		}
		public function get rootChangeAction():Boolean {
			return _rootChangeAction;
		}
		public function set isLocalMagnify(b:Boolean):void {
			if ( _localMagnify == b )
				return ;
			_localMagnify = b;
			var node:INode;
			if ( _localMagnify == true ){
				for each ( node in _graphData.nodes ){
					_mapPositionFrom[node] = (_mapPosition[node] as Point).clone();
				}
				_graphCanvas.addEventListener(MouseEvent.MOUSE_MOVE, localMagnify);
				if ( _multiSelectStyle == true ){	//如果是多选模式，去掉拖拽画布的事件，加入多选开始的事件
					_graphCanvas.removeEventListener(MouseEvent.MOUSE_DOWN,multiSelectBegin);
				}
				else { //如果不是多选模式，去掉多选开始的事件，加入拖拽画布的事件
					_graphCanvas.removeEventListener(MouseEvent.MOUSE_DOWN,mouseDown);
				}
			}
			else {
				_graphCanvas.removeEventListener(MouseEvent.MOUSE_MOVE, localMagnify);
				if ( _multiSelectStyle == true ){	//如果是多选模式，去掉拖拽画布的事件，加入多选开始的事件
					_graphCanvas.addEventListener(MouseEvent.MOUSE_DOWN,multiSelectBegin);
				}
				else { //如果不是多选模式，去掉多选开始的事件，加入拖拽画布的事件
					_graphCanvas.addEventListener(MouseEvent.MOUSE_DOWN,mouseDown);
				}
				for each ( node in _graphData.nodes ){
					if ( (_mapPositionFrom[node] as Point )!= null )
						_mapPosition[node] = (_mapPositionFrom[node] as Point).clone();
				}
			}
		}
		public function get isLocalMagnify():Boolean {
			return _localMagnify;
		}
		private function localMagnify(evt:MouseEvent):void {
			var node:INode;
			var distance:Number = 50;
			var deltaD:Number;
			var mouseP:Point = new Point(evt.stageX,evt.stageY);
			var nodeP:Point = new Point;
			var nodeCurP:Point;
			var maxMagnify:int = 2;
			var position:Point;
			mouseP = _graphCanvas.globalToLocal(mouseP);
			for each ( node in _graphData.nodes ){
				nodeP.x = (_mapPositionFrom[node] as Point).x;
				nodeP.y = (_mapPositionFrom[node] as Point).y;
				nodeCurP = _mapPosition[node] as Point;
				deltaD = Point.distance(mouseP,nodeP);
				
				if ( deltaD < distance ){
					nodeCurP.x = (nodeP.x - mouseP.x)*maxMagnify + mouseP.x;
					nodeCurP.y = (nodeP.y - mouseP.y)*maxMagnify +mouseP.y;
					}
				else {
					nodeCurP.x = (nodeP.x - mouseP.x)*(maxMagnify-1+distance/deltaD) + mouseP.x;
					nodeCurP.y = (nodeP.y - mouseP.y)*(maxMagnify-1+distance/deltaD) +mouseP.y;
				}
			}
			drawNodes();
			drawEdges();
			drawCommunities();
		}
		public function getNodeById(id:String):Object {
			var node:INode;
			for each ( node in _graphData.nodes ){
				if ( node.id == id ){
					return node.dataObject;
					break;
				}
			}
			return null;
		}
		public function destroy():void {
			_layout.stop();
		}
		
		public function setLabelContent(propertyArr:Array):void {
			var node:INode;
			var nodeXML:XML;
			var attrName:String;
			var targetAttrName:String;
			var label:String = "";
			var attachedAttr:String = "";
			for each ( node in this._graphData.nodes ){
				label = "";
				attachedAttr = "";
				nodeXML = node.dataObject as XML;
				for each ( targetAttrName in propertyArr  ){
					for each ( var attribut:XML in nodeXML.attributes() ){
						attrName = attribut.name();
						if ( attrName == targetAttrName ){
							if ( attrName == "id" ){
								label = nodeXML.attribute(attrName);
							}
							else {
								attachedAttr += nodeXML.attribute(attrName) + ",";
							}
						}
					}
			    }
			    if ( attachedAttr != "" )
			    	node.label.text = label + "(" + attachedAttr.substr(0,attachedAttr.length-1) + ")";
			    else 
			    	node.label.text = label;
			    //node.label.invalidateDisplayList();

			    node.label.validateNow();
			}
			//this.refresh();
			this.drawNodeLabel();
		}
		
		public function setNodeWeightProperty(property:String):void {
			var node:INode;
			var nodeXML:XML;
			for each ( node in _graphData.nodes ){
				nodeXML = node.dataObject as XML;
				node.weight = 0;
				for each ( var attribut:XML in nodeXML.attributes() ){
					if ( attribut.name() == property ){
						node.weight = new Number(nodeXML.attribute(property));
					}
				}
			}
			_mapNodeColor = setNodeColor(_graphData.nodes);
			this.refresh();
		}
		
		protected function createForest(graphFullData:IGraphData, root:INode):void {
			if ( root == null ){
				if ( graphFullData.nodes.length != 0 ){
					root = graphFullData.nodes[0];
				}
				else
					return ;
			}
			var forest:Forest = new Forest(graphFullData, root);
			var trees:Array = forest.tree;
			var tempTree:Tree;
			var sortedTrees:Array = new Array;
			for ( var i:int = 0 ; i < trees.length - 1 ; i++ ){
				for ( var j:int = 0 ; j < trees.length - i - 1 ; j++ ){
					if ( (trees[j] as Tree).nodes.length < (trees[j+1] as Tree).nodes.length ) {
						tempTree = trees[j];
						trees[j] = trees[j+1];
						trees[j+1] = tempTree;
					}
				}
			}
/*  			var s:String = "";
			for ( var n:int = 0 ; n < trees.length ; n++ ){
				s += (trees[n] as Tree).nodes.length + ",";
			}
			Alert.show(s);  */
			var sortedTreesNodes:Array = new Array;
			for ( var n:int = 0 ; n < trees.length ; n++ ){
				sortedTreesNodes.push((trees[n] as Tree).nodes);
			}
			this._graphParts = createGraphParts(sortedTreesNodes);
		}
		
		private function createGraphParts(treesNodes:Array):Array {
			var graphParts:Array = new Array;
			var part:Array = new Array;;
			const MIN_SIZE:int = 150;
			for each ( var nodes:Array in treesNodes ){
				part = part.concat(nodes);
				if ( part.length > MIN_SIZE ) {
					graphParts.push(part);
					//Alert.show("part.length:"+part.length);
					part = new Array;
				}
			}
			if ( part.length != 0 ){
				graphParts.push(part);
				//Alert.show("part.length:"+part.length);
			}
/* 			var s:String = "";
			for ( var n:int = 0 ; n < graphParts.length ; n++ ){
				s += (graphParts[n] as Array).length + ",";
			}
			Alert.show(s); */
			return graphParts;
		}
		
		private function filterByGraphDataPart(nodes:Array):void {
			var nodeMap:Dictionary = new Dictionary;
			var newEdges:Array = new Array;
			var node:INode;
			var adjNode:INode;
			var fromNode:INode;
			var toNode:INode;
			var edge:IEdge;
			var tempArr:Array = new Array;
			this._graphData.nodes = nodes;
			var s:String = "";
			for each ( node in nodes ){
				//s += node.id+",";
				nodeMap[node.id] = true;
				nodeMap[node] = true;
			}
			//Alert.show(s);
			for each ( edge in this._graphFullData.edges ){
				fromNode = edge.fromNode;
				toNode = edge.toNode;
				if ( nodeMap[fromNode.id] == true && nodeMap[toNode.id] == true ){
					newEdges.push(edge);
					if ( nodeMap[edge.fromNode] == true && nodeMap[edge.toNode] == true ){
						
					}
					else {
						Alert.show("bug:"+edge.fromNode.id+" "+edge.toNode.id);
					}
				}
			}
			this._graphData.edges = newEdges;
			if ( this._graphData.nodes.length != 0 )
				_root = this._graphData.nodes[0];
			this.updateFilteredGraph();
			//Alert.show(s);
		}
	}
}