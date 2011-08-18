package SNADisplay.org.Graph.Logical
{
	import SNADisplay.org.Graph.Visual.VisualEdge;
	public interface IEdge
	{
		function get id():String;
		function get fromNode():INode;
		function get toNode():INode;
		function get label():String;
		function get visualEdge():VisualEdge;
		function set visualEdge(ve:VisualEdge):void;
		function set label(s:String):void;
		function get weight():Number;
		function set weight(s:Number):void;
	}
}