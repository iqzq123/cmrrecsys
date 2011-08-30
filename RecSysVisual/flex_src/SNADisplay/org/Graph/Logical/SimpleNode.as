package SNADisplay.org.Graph.Logical
{
	import SNADisplay.org.Graph.Visual.VisualNode;
	
	import mx.controls.Label;
	public class SimpleNode implements INode
	{
		private var _id:String;
		private var _name:String;		
		private var _inEdges:Array;
		private var _outEdges:Array;	
		private var _dataObject:Object;
		private var _label:Label;
		private var _size:Number;
		private var _weight:Number;
		public function SimpleNode(id:String,name:String,o:Object = null)
		{
			_id = id;
			_name = name;
			_size = 10;
			_label = new Label;
			
			if ( name != null && name != "" )
				_label.text = name;
			else
				_label.text = id; 
			
			//_label.text = id+"("+ (o as XML).attribute("clickNum").toString()+","+ (o as XML).attribute("time").toString()+")";
			_inEdges = new Array;
			_outEdges = new Array;
			_dataObject = o;
			_weight = 1;
		}
		//加入入度节点
		public function addInEdge(n:INode):void {
		
			_inEdges.push(n);
		}
		//加入出度节点
		public function addOutEdge(n:INode):void {
			
			_outEdges.push(n);
		}
		//删除入度边
		public function deleteInEdge(n:INode):void {
			var i:int;
			for ( i = 0 ; i < _inEdges.length ; i++ ){
				if ( _inEdges[i] == n )
					_inEdges.splice(i,1);
			}
		}
		//删除出度边
		public function deleteOutEdge(n:INode):void {
			var i:int;
			for ( i = 0 ; i < _outEdges.length ; i++ ){
				if ( _outEdges[i] == n )
					_outEdges.splice(i,1);
			}
		}
		//删除边
		public function deleteEdge(n:INode):void {
			var i:int;
			for ( i = 0 ; i < _outEdges.length ; i++ ){
				if ( _outEdges[i] == n )
					_outEdges.splice(i,1);
			}
			for ( i = 0 ; i < _inEdges.length ; i++ ){
				if ( _inEdges[i] == n )
					_inEdges.splice(i,1);
			}
		}
		
		public function get id():String {
			return _id;
		}
		
		public function get name():String {
			return _name;
		}
		
		public function set name(name:String):void {
			_name = name;
			_label.text = name;
		}
		
		
		public function get visualNode():VisualNode {
			return null;
		}
		
		public function get inEdges():Array {
			return _inEdges;
		}
		
		public function set inEdges(inEdges:Array):void {
			_inEdges = inEdges;
		}
		
		public function get outEdges():Array {
			return _outEdges;
		}
		
		public function set outEdges(outEdges:Array):void {
			_outEdges = outEdges;
		}
		
		public function get edges():Array {
			var edges:Array;
			edges = _inEdges.concat(_outEdges);
			return edges;
		}
		
		public function set dataObject(o:Object):void {
			_dataObject = o;
		}
		
		public function get dataObject():Object {
			return _dataObject;
		}
		
		public function get label():Label {
			return _label;
		}
		
		public function get size():Number {
			return _size;
		}
		
		public function set size(n:Number):void {
			_size = n;
		}
		
		public function mouseOver():void {
			//_visualNode.mouseOver();
		}
		
		public function mouseOut():void {
			//.mouseOut();
		}
		
		public function set weight(w:Number):void {
			_weight = w;
		}
		
		public function get weight():Number {
			return _weight;
		}
	}
}