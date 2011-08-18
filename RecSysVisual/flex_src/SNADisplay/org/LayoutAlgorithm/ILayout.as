package SNADisplay.org.LayoutAlgorithm
{
	import SNADisplay.org.Graph.Logical.*;
	import flash.utils.Dictionary;
	
	import mx.containers.Canvas;
	
	public interface ILayout
	{
		function init(graphData:IGraphData, canvas:Canvas):void;
		function placement():Dictionary;
		function set root(r:INode):void;
		function stop():void;
		function start():void;
		function get finished():Boolean;  //算法执行是否结束
		function get isRun():Boolean;	  //算法是否在执行
		function get layoutName():String; //返回布局算法的名称
	}
}