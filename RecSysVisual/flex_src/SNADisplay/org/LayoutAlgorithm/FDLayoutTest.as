package SNADisplay.org.LayoutAlgorithm
{
	import flash.events.TimerEvent;
	import flash.geom.Point;
	import flash.geom.Rectangle;
	import flash.utils.Dictionary;
	import flash.utils.Timer;
	
	import SNADisplay.org.Graph.*;
	import SNADisplay.org.utils.Geometry;
	public class FDLayoutTest extends BaseLayout implements ILayout
	{
		private var _aveEdgeLength:Number;
		private var _aveDistance:Number;
		private var _edges:Array;
		private var _completed:Boolean;
		private var _mapForce:Dictionary;
		private var _mapPosition:Dictionary;
		private var _mapMass:Dictionary;
		private const _f:Number = 0.8;
		private var _k:Number = 0.5;
		private const FACTOR:Number = 0.1;
		private const TIMER:int = 10;
		private const AVE_EDGE_LENGTH:int = 100;
		private const AVE_DISTANCE:int = 80;
		private const REPULSION_AREA:Number = 2;
		private const SINGLE_STEP:Number = 0.1;
		private const MIN_ENERGY:Number = 1;
		private var _maxDegree:int;
		private var _timer:Timer = null;
		private var _maxMove:Number;
		public function FDLayoutTest()
		{
			super();
		}
		
		override public function placement():Dictionary {
			var node:INode;
			var force:Point;
			_edges = _graph.edges;
			
			//_aveEdgeLength = Math.min(_graph.canvas.width, _graph.canvas.height)*(1-2*padding) / ( (_graph.nodes.length+1)/2) ;
			_aveEdgeLength = AVE_EDGE_LENGTH;
			_aveDistance = AVE_DISTANCE;
			_completed = false;
			_mapForce = new Dictionary;
			_mapPosition = new Dictionary;//_graph.mapPosition;
			_mapMass = new Dictionary;
			//if ( !_mapPosition.isPrototypeOf(_graph.nodes[0]) )
			//radomLayout();
			_maxDegree = 0;
			for each (node in _graph.nodes){
				force = new Point;
				force.x = 0;
				force.y = 0;
				_mapForce[node] = force;
				_mapMass[node] = node.inEdges.length + node.outEdges.length;
				if ( _mapMass[node] > _maxDegree )
					_maxDegree = _mapMass[node];
			}
			if ( _timer != null )
				_timer.stop();
			_timer = new Timer(TIMER,1);
			_timer.addEventListener(TimerEvent.TIMER, generation);
			_timer.start();
			//trace("timer.start()");
			return _mapPosition;
		}
		
		private function radomLayout():void {
			var node:INode;
			var position:Point;
			for each (node in _graph.nodes){
				position = new Point;
				position.x = _graph.canvas.width*padding + Math.round(Math.random()*_graph.canvas.width*(1-2*padding));
				position.y = _graph.canvas.height*padding + Math.round(Math.random()*_graph.canvas.height*(1-2*padding));
				_mapPosition[node] = position;
			}
		}
		
		private function generation(event:TimerEvent = null):void {
			var node:INode;
			var position:Point;
			var energy:Number;
			var netRegion:Rectangle;
			var movePoint:Point = new Point;
			var zoomScale:Number;
			
			calculateSpringForce();
			//calculateGravitation();
			energy = 0;
			for each (node in _graph.nodes){
				position = new Point;
				position = ( _mapPosition[node] as Point ).clone();
				position.x += (_mapForce[node] as Point).x / _maxMove * 5;
				position.y += (_mapForce[node] as Point).y / _maxMove * 5;
				energy += Math.sqrt(Math.pow((_mapForce[node] as Point).x * FACTOR,2) + Math.pow((_mapForce[node] as Point).y * FACTOR,2));
				(_mapForce[node] as Point).x = 0;
				(_mapForce[node] as Point).y = 0;
				//trace("moveX="+((_mapForce[node] as Point).x * _f)+" moveY="+((_mapForce[node] as Point).y * _f));
				_mapPosition[node] = position;
			}
			_graph.refresh();
			
			trace("generation1: energy ="+energy);
			//SINGLE_STEP*_graph.nodes.length
			
			if ( energy < MIN_ENERGY ){
				/*
				netRegion = _graph.netRegion;
				movePoint.x = _graph.center.x - (netRegion.bottomRight.x + netRegion.topLeft.x)/2;
			 	movePoint.y = _graph.center.y - (netRegion.bottomRight.y + netRegion.topLeft.y)/2;
			 	for each (node in _graph.nodes){
					//trace("moveX="+((_mapForce[node] as Point).x * _f)+" moveY="+((_mapForce[node] as Point).y * _f));
					(_mapPosition[node] as Point).offset( movePoint.x, movePoint.y);
				}
				
				//trace("netRegion3"+netRegion+" bottomRight.y="+netRegion.bottomRight.y+" topLeft.y="+netRegion.topLeft.y);
				zoomScale = Math.min( _graph.canvas.width*(1-2*padding)/netRegion.width , _graph.canvas.height*(1-2*padding)/netRegion.height );
				
				trace("zoomScale="+zoomScale);
				_graph.zoomFit(zoomScale);
				_graph.refresh();	
				*/
				_completed = true;
			}
		
			if ( _completed == true ){
				trace("completed first step");
				_timer.stop();
				_completed = false;
				_timer = new Timer(TIMER,1);
				_timer.addEventListener(TimerEvent.TIMER, generation2);
				_timer.start();
			}else {
				_timer = new Timer(TIMER,1);
				_timer.addEventListener(TimerEvent.TIMER, generation);
				_timer.start();
			}
		}
		
		private function generation2(event:TimerEvent = null):void {
			var node:INode;
			var position:Point;
			var energy:Number;
			var netRegion:Rectangle;
			var movePoint:Point = new Point;
			var zoomScale:Number;
			
			calculateSpringForce();
			calculateGravitation();
			energy = 0;
			for each (node in _graph.nodes){
				position = new Point;
				position = ( _mapPosition[node] as Point ).clone();
				position.x += (_mapForce[node] as Point).x / _maxMove * 5;
				position.y += (_mapForce[node] as Point).y / _maxMove * 5;
				energy += Math.sqrt(Math.pow((_mapForce[node] as Point).x * FACTOR,2) + Math.pow((_mapForce[node] as Point).y * FACTOR,2));
				(_mapForce[node] as Point).x = 0;
				(_mapForce[node] as Point).y = 0;
				//trace("moveX="+((_mapForce[node] as Point).x * _f)+" moveY="+((_mapForce[node] as Point).y * _f));
				_mapPosition[node] = position;
			}
			_graph.refresh();
			
			trace("generation2: energy ="+energy);
			//SINGLE_STEP*_graph.nodes.length
			
			if ( energy < MIN_ENERGY ){
				
				netRegion = _graph.netRegion;
				movePoint.x = _graph.center.x - (netRegion.bottomRight.x + netRegion.topLeft.x)/2;
			 	movePoint.y = _graph.center.y - (netRegion.bottomRight.y + netRegion.topLeft.y)/2;
			 	for each (node in _graph.nodes){
					//trace("moveX="+((_mapForce[node] as Point).x * _f)+" moveY="+((_mapForce[node] as Point).y * _f));
					(_mapPosition[node] as Point).offset( movePoint.x, movePoint.y);
				}
				
				//trace("netRegion3"+netRegion+" bottomRight.y="+netRegion.bottomRight.y+" topLeft.y="+netRegion.topLeft.y);
				zoomScale = Math.min( _graph.canvas.width*(1-2*padding)/netRegion.width , _graph.canvas.height*(1-2*padding)/netRegion.height );
				
				trace("zoomScale="+zoomScale);
				_graph.zoomFit(zoomScale);
				_graph.refresh();	
			
				_completed = true;
			}
		
			if ( _completed == true )
				_timer.stop();
			else {
				_timer = new Timer(TIMER,1);
				_timer.addEventListener(TimerEvent.TIMER, generation2);
				_timer.start();
			}
		}
		
		private function calculateSpringForce():void {
			var edge:IEdge;
			var position:Point;
			var nodeFrom:INode;
			var nodeTo:INode;
			var pointFrom:Point;
			var pointTo:Point;
			var endgeLength:Number;
			var deltaLength:Number;
			var angle:Number;
			var mass:Number;
			_maxMove = 0;
			for each ( edge in _edges ){
				nodeFrom = edge.fromNode;
				nodeTo = edge.toNode;
				pointFrom = (_mapPosition[nodeFrom] as Point);
				pointTo = (_mapPosition[nodeTo] as Point);
				angle = Geometry.getAngle( pointTo.x, pointTo.y, pointFrom);
				deltaLength = Math.sqrt( (pointFrom.x-pointTo.x)*(pointFrom.x-pointTo.x)+(pointFrom.y-pointTo.y)*(pointFrom.y-pointTo.y) ) - _aveEdgeLength;
				
				mass = _mapMass[nodeFrom]*_mapMass[nodeTo];
				(_mapForce[nodeFrom] as Point).x += deltaLength*Math.cos(angle)*mass/_maxDegree* _f;
				(_mapForce[nodeFrom] as Point).y += deltaLength*Math.sin(angle)*mass/_maxDegree* _f;
				if ( _maxMove < Math.max((_mapForce[nodeFrom] as Point).x, (_mapForce[nodeFrom] as Point).y )){
					_maxMove = Math.max((_mapForce[nodeFrom] as Point).x, (_mapForce[nodeFrom] as Point).y )
				}
				(_mapForce[nodeTo] as Point).x -= deltaLength*Math.cos(angle)*mass/_maxDegree* _f;
				(_mapForce[nodeTo] as Point).y -= deltaLength*Math.sin(angle)*mass/_maxDegree* _f;
				if ( _maxMove < Math.max((_mapForce[nodeTo] as Point).x, (_mapForce[nodeTo] as Point).y )){
					_maxMove = Math.max((_mapForce[nodeTo] as Point).x, (_mapForce[nodeTo] as Point).y )
				}

			}
		}
		private function calculateGravitation():void {
			var i:int;
			var j:int;
			var position:Point;
			var position2:Point;
			var distance:Number;
			var deltaDistance:Number;
			var mass:Number;
			var angle:Number;
			_maxMove = 0;
			for ( i = 0 ; i < _graph.nodes.length ; i++ ){
				position = ( _mapPosition[_graph.nodes[i]] as Point );
				for ( j = i+1 ; j < _graph.nodes.length ; j++ ){
					position2 = ( _mapPosition[_graph.nodes[j]] as Point );
					distance = Point.distance(position,position2);
					angle = Geometry.getAngle( position2.x, position2.y, position);
					
					mass = _mapMass[_graph.nodes[i]]*_mapMass[_graph.nodes[j]];
					if ( distance < REPULSION_AREA*AVE_DISTANCE ){
						deltaDistance = REPULSION_AREA*AVE_DISTANCE - distance;
						(_mapForce[_graph.nodes[i]] as Point).x -= _k*deltaDistance*mass/_maxDegree*Math.cos(angle);
						(_mapForce[_graph.nodes[i]] as Point).y -= _k*deltaDistance*mass/_maxDegree*Math.sin(angle);
						if ( _maxMove < Math.max((_mapForce[_graph.nodes[i]] as Point).x, (_mapForce[_graph.nodes[i]] as Point).y )){
							_maxMove = Math.max((_mapForce[_graph.nodes[i]] as Point).x, (_mapForce[_graph.nodes[i]] as Point).y )
						}
						(_mapForce[_graph.nodes[j]] as Point).x += _k*deltaDistance*mass/_maxDegree*Math.cos(angle);
						(_mapForce[_graph.nodes[j]] as Point).y += _k*deltaDistance*mass/_maxDegree*Math.sin(angle);
						if ( _maxMove < Math.max((_mapForce[_graph.nodes[j]] as Point).x, (_mapForce[_graph.nodes[j]] as Point).y )){
							_maxMove = Math.max((_mapForce[_graph.nodes[j]] as Point).x, (_mapForce[_graph.nodes[j]] as Point).y )
						}
					}

				}
			}
		}
		override public function stop():void {
			_timer.stop();
		}
		
		override public function start():void {
			_timer.start();
		}
		
	}
}