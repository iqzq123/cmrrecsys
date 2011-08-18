package SNADisplay.org.Graph.Logical
{
	import SNADisplay.org.Graph.Visual.VisualEdge;
	
	public class Edge implements IEdge
	{
		private var _id:String;
		private var _fromNode:INode; 
		private var _toNode:INode; 
		private var _visualEdge:VisualEdge;
		private var _label:String;
		private var _weight:Number;
		
		public function Edge(id:String, fromNode:INode, toNode:INode, label:String = null)
		{
			_id = id;
			_fromNode = fromNode;
			_toNode = toNode;
			if ( label == null )
				_label = id.toString();
		}
		
		public function get id():String {
			return _id;
		}
		public function get fromNode():INode {
			return _fromNode;
		}
		public function get toNode():INode {
			return _toNode;
		}
		public function set visualEdge(va:VisualEdge):void {
			_visualEdge = va;
			_visualEdge.id = "edge"+_id;
		}
		public function get visualEdge():VisualEdge{
			return _visualEdge;
		}
		
		public function get label():String{
			return _label;
		}
		
		public function set label(s:String):void {
			_label = s;
		}
		
		public function get weight():Number{
			return _weight;
		}
		
		public function set weight(w:Number):void {
			_weight = w;
		}

	}
}