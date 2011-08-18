package SNADisplay.org.Graph.Logical
{
	import SNADisplay.org.Graph.Visual.VisualNode;
	
	import mx.controls.Label;

	public interface INode
	{
		function get id():String;
		
		function set name(name:String):void;
		
		function get name():String;
		
		function get visualNode():VisualNode;
		
		function get label():Label;
		
		function get size():Number;
		
		function set size(n:Number):void;
		
		function addInEdge(n:INode):void;
		
		function addOutEdge(n:INode):void;
		
		function deleteInEdge(n:INode):void;
		
		function deleteOutEdge(n:INode):void;
		
		function deleteEdge(n:INode):void;
		
		function get inEdges():Array;
		
		function get outEdges():Array;
		
		function get edges():Array;
		
		function set dataObject(o:Object):void;
		
		function get dataObject():Object;
		
		function mouseOver():void;
		
		function mouseOut():void;
		
		function set weight(w:Number):void;
		
		function get weight():Number;
	}
}