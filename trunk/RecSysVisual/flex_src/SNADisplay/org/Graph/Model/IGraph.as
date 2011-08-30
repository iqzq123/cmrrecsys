package SNADisplay.org.Graph.Model {
	import SNADisplay.org.Graph.Logical.*;
	import SNADisplay.org.LayoutAlgorithm.ILayout;
	
	import flash.geom.Point;
	import flash.geom.Rectangle;
	import flash.utils.Dictionary;
	
	import mx.containers.Canvas;

	public interface IGraph	{
		function set canvas(canvas:Canvas):void;
		function get canvas():Canvas;
		function get nodes():Array;
		function get edges():Array;
		function set layout(layout:ILayout):void;
		function get layout():ILayout;
		function set isFastMode(b:Boolean):void;
		function get isFastMode():Boolean;
		function set isDirected(b:Boolean):void;
		function get isDirected():Boolean;
		function get center():Point;
		function get mapPosition():Dictionary;
		function set center(p:Point):void;
		function get netRegion():Rectangle;
		function set showCommunities(b:Boolean):void;
		function get showCommunities():Boolean;
		function set showPath(b:Boolean):void;
		function get showPath():Boolean;
		function set showEdgeDirection(b:Boolean):void;
		function get showEdgeDirection():Boolean;
		function set showEdgeLabel(b:Boolean):void;
		function get showEdgeLabel():Boolean;
		function set nodeStyle(b:Boolean):void;
		function get nodeStyle():Boolean;
		function set pathStyle(b:Boolean):void;
		function get pathStyle():Boolean;
		
		function set communitiesStyle(b:Boolean):void;
		function get communitiesStyle():Boolean;
		function set stretchStyle(b:Boolean):void;
		function get stretchStyle():Boolean;
		function set multiSelectStyle(b:Boolean):void;
		function get multiSelectStyle():Boolean;
		function get multiSelectResult():Array;
		function set isLocalMagnify(b:Boolean):void;
		function get isLocalMagnify():Boolean;
		function get layoutName():String;
		function set rootChangeAction(b:Boolean):void;
		function get rootChangeAction():Boolean;
		function get mapNodeCommunity():Dictionary;
		
		function initGraphData(xmlData:XML, canvas:Canvas = null):void;
		function rotation(n:Number):void;	//旋转
		function copyGraphData():IGraphData;	//复制图数据
		function draw():void;	//绘制网络图
		function drawNodes():void;	//画节点
		function drawEdges():void;	//画边
		function calNetRegion():void;	//计算当期图的矩形范围
		function zoom(degree:Number):void 	//缩放
		function refresh():void;	//刷新
		function nodeIcon(image:Class):void;
		
		function stopLayout():void;
		function startLayout():void;
		function setBaseLayout():void;
		function setCircleLayout():void;
		function setConcentricRadialLayout():void;
		function setForceDirectedLayout():void;
		function setRadialTreeLayout():void;
		function setTreeLayout():void;
		function setTreeHorizonLayout():void;
		
		function setCliquePercolation():void;
		function setGN():void;
		function setFastGN():void;
		function setDefaultCommunities():void;
		
		function getNodeById(id:String):Object;
		function hasNode(id:String):Boolean;
		
		function destroy():void;
		function filter(minNodeW:Number = -1,maxNodeW:Number = -1,minEdgeW:Number = -1,maxEdgeW:Number = -1):void;
		function singleNodefilter(nodeId:String, depth:int, direction:Boolean = true):void;
		function removeNode(nodeId:String):Boolean;
		
		function setLabelContent(propertyArr:Array):void;
		function setNodeWeightProperty(property:String):void;

	}
}