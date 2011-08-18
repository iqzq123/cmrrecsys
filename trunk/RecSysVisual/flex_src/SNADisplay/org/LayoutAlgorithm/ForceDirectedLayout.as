package SNADisplay.org.LayoutAlgorithm
{
	import SNADisplay.org.Graph.Logical.*;
	import SNADisplay.org.Graph.Model.IGraph;
	import SNADisplay.org.utils.Geometry;
	
	import flash.events.TimerEvent;
	import flash.geom.Point;
	import flash.geom.Rectangle;
	import flash.utils.Dictionary;
	import flash.utils.Timer;
	
	import mx.containers.Canvas;
	public class ForceDirectedLayout extends BaseLayout implements ILayout
	{
		private const CLASS_NAME:String = "ForceDirectedLayout";
		private var _graph:IGraph;
		private var _aveEdgeLength:Number;	//平均边长
		private var _aveDistance:Number;	//平均节点间距
		private var _edges:Array;			//边
		private var _completed:Boolean;		//完成标记
		private var _mapForce:Dictionary;	//导引力的映射
		private var _mapPosition:Dictionary;	//位置映射
		private var _mapMass:Dictionary;	//质量映射
		private const FACTOR:Number = 5;	//参数
		private const S_FACTOR:Number = 1;	//边引力的参数
		private const G_FACTOR:Number = 1;	//质量引力的参数
		private const TIMER:int = 1;	//时间间隔参数
		private const AVE_EDGE_LENGTH:int = 50;	//默认的临界边长度
		private const AVE_DISTANCE:int = 40;	//默认的临界点间距
		private const REPULSION_AREA:Number = 2;	//斥力范围
		private var _maxDegree:int;				//记录网络中的最大度数
		private var _timer:Timer = null;	//计时器
		private var _maxMove:Number;		//最大移动距离
		private var _count:int;			//计数器
		//private var _isShowCommunities:Boolean;		//是否显示社团
		private var _minEnergy:Number;		//最小动能
		private var _lastEnergy:Number;		//上一次的动能（用于差值计算）
		
		public function ForceDirectedLayout()
		{
			super();
		}
		public function set graph(graph:IGraph):void {
			_graph = graph;
		}
		//初始化参数，启动计时器
		override public function placement():Dictionary {
			var node:INode;
			var force:Point;
			//初始化各个参数
			_edges = _graph.edges;
			_aveEdgeLength = AVE_EDGE_LENGTH;
			_aveDistance = AVE_DISTANCE;
			_completed = false;
			_mapForce = new Dictionary;
			_mapPosition = _graph.mapPosition;
			_mapMass = new Dictionary;

			_maxDegree = 0;
			_minEnergy = _graph.nodes.length/50;   //最小动能为节点数的50分之一
			_lastEnergy = 0;
			_count = 0;
			//初始化映射
			for each (node in _graph.nodes){
				force = new Point;
				force.x = 0;
				force.y = 0;
				_mapForce[node] = force;
				_mapMass[node] = node.inEdges.length + node.outEdges.length;
				//记录最大质量,即图中所有节点中的最大度数
				if ( _mapMass[node] > _maxDegree )
					_maxDegree = _mapMass[node];  
			}
			//随机布局，初始化
			radomLayout();
			//初始化计时器
			if ( _timer != null )
				_timer.stop();
			//新建计时器
			_timer = new Timer(TIMER,1);
			_timer.addEventListener(TimerEvent.TIMER, generation);
			//启动计时器
			_timer.start();
			return _mapPosition;
		}
		//随机生成布局，初始化
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
		//启发式算法的主要流程
		private function generation(event:TimerEvent = null):void {
			var node:INode;
			var position:Point;
			var moveX:Number;
			var moveY:Number;
			var energy:Number;
			var netRegion:Rectangle;
			var movePoint:Point = new Point;
			var zoomScale:Number;
			
			_maxMove = 0;
			//计算当期位置下的边引力和质量引力的参数
			calculateSpringForce();
			calculateGravitation();
			//初始化当前的动能
			energy = 0;
			//计算每个点的移动距离
			for each (node in _graph.nodes){
				position = new Point;
				position = ( _mapPosition[node] as Point ).clone();
				moveX = (_mapForce[node] as Point).x / _maxMove * FACTOR;  //进行修正
				moveY = (_mapForce[node] as Point).y / _maxMove * FACTOR;
				position.x += moveX;
				position.y += moveY;
				//累加能量，位移的标量和
				energy += Math.sqrt(Math.pow(moveX,2) + Math.pow(moveY,2));
				(_mapForce[node] as Point).x = 0;
				(_mapForce[node] as Point).y = 0;
				_mapPosition[node] = position;
			}
			//更新图的展示
			_graph.refresh();
			//如果能量低于下限，算法执行结束
			if ( energy < _minEnergy ){
				trace( "energy < _minEnergy" );
				_completed = true;
			}
			//如果前后两次的能量差值都小于一个范围，且保持了一段时间，则算法终止（防止波动）
			if ( Math.abs(_lastEnergy - energy) < 0.01 ){
				_count++;
				if ( _count > 50 ){
					trace( "count is out");
					_completed = true;
				}
			}
			else
				_count = 0;	//初始化计数器
			//trace("count="+_count+" abs="+Math.abs(_lastEnergy - energy)+" energy="+energy+" lastEnergy="+_lastEnergy);
			//保持当前的能量
			_lastEnergy = energy;
			if ( _completed == true ){  //如果算法结束
				_timer.stop();
				_graph.calNetRegion();
				//得到当前图像的面积尺寸，用矩形表示
				netRegion = _graph.netRegion;
				var can:Canvas = _graph.canvas;
				//计算当前图像和画布中心位置的变差
				movePoint.x = _graph.center.x - (netRegion.left + netRegion.right)/2;
			 	movePoint.y = _graph.center.y - (netRegion.top + netRegion.bottom)/2;
			 	//计算当前图像的尺寸和画布的可视面积的尺寸的比例缩放度
			 	zoomScale = Math.min( _graph.canvas.width*(1-2*padding)/netRegion.width , _graph.canvas.height*(1-2*padding)/netRegion.height );
				//调整当前图像，达到最好的展示效果
			 	for each (node in _graph.nodes){
					//trace("moveX="+((_mapForce[node] as Point).x * _f)+" moveY="+((_mapForce[node] as Point).y * _f));
					var point:Point = _mapPosition[node] as Point;
					point.offset( movePoint.x, movePoint.y);
					point.x = _graph.center.x + ( point.x - _graph.center.x )*zoomScale;
					point.y = _graph.center.y + ( point.y - _graph.center.y )*zoomScale;
				}
				//回复显示社团的模式状态
				//_graph.showCommunities = _isShowCommunities;
				_graph.refresh();	
			}else { //如果算法没有结束，则继续执行
				_timer = new Timer(TIMER,1);
				_timer.addEventListener(TimerEvent.TIMER, generation);
				_timer.start();
			}
		}
		//计算边连接的两点的扩张收缩关系
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
			for each ( edge in _edges ){
				nodeFrom = edge.fromNode;
				nodeTo = edge.toNode;
				pointFrom = (_mapPosition[nodeFrom] as Point);
				pointTo = (_mapPosition[nodeTo] as Point);
				angle = Geometry.getAngle( pointTo.x, pointTo.y, pointFrom);
				deltaLength = Math.sqrt( (pointFrom.x-pointTo.x)*(pointFrom.x-pointTo.x)+(pointFrom.y-pointTo.y)*(pointFrom.y-pointTo.y) ) - _aveEdgeLength;
				if ( _graph.showCommunities == true ){
					// 两个点不属于同一社团
					if ( _graph.mapNodeCommunity[nodeFrom] != _graph.mapNodeCommunity[nodeTo] || _graph.mapNodeCommunity[nodeFrom] == null || _graph.mapNodeCommunity[nodeTo] == null){
						//两点距离小于默认值
						if ( deltaLength < 0 ){
							deltaLength = deltaLength * 3;
						}
						else if ( _graph.showCommunities == true ){
							deltaLength = deltaLength / 2;
						}
					}
					else { // 两个点属于同一社团
						//两点距离大于默认值
						if ( deltaLength > 0 ){
							deltaLength = deltaLength * 4;
						}
					}
				}
				
				//mass为两个节点的质量积
				mass = _mapMass[nodeFrom]*_mapMass[nodeTo];
				(_mapForce[nodeFrom] as Point).x += deltaLength*Math.cos(angle)*mass/_maxDegree * S_FACTOR;
				(_mapForce[nodeFrom] as Point).y += deltaLength*Math.sin(angle)*mass/_maxDegree * S_FACTOR;
				if ( _maxMove < Math.max((_mapForce[nodeFrom] as Point).x, (_mapForce[nodeFrom] as Point).y )){
					_maxMove = Math.max((_mapForce[nodeFrom] as Point).x, (_mapForce[nodeFrom] as Point).y )
				}
				(_mapForce[nodeTo] as Point).x -= deltaLength*Math.cos(angle)*mass/_maxDegree * S_FACTOR;
				(_mapForce[nodeTo] as Point).y -= deltaLength*Math.sin(angle)*mass/_maxDegree * S_FACTOR;
				if ( _maxMove < Math.max((_mapForce[nodeFrom] as Point).x, (_mapForce[nodeFrom] as Point).y )){
					_maxMove = Math.max((_mapForce[nodeFrom] as Point).x, (_mapForce[nodeFrom] as Point).y )
				}
				
			}
		}
		
		//计算节点之间的质量引力关系
		private function calculateGravitation():void {
			var i:int;
			var j:int;
			var position:Point;
			var position2:Point;
			var distance:Number;
			var deltaDistance:Number;
			var mass:Number;
			var angle:Number;
			//复杂度为n!
			for ( i = 0 ; i < _graph.nodes.length ; i++ ){
				position = ( _mapPosition[_graph.nodes[i]] as Point );
				for ( j = i+1 ; j < _graph.nodes.length ; j++ ){
					position2 = ( _mapPosition[_graph.nodes[j]] as Point );
					distance = Point.distance(position,position2);
					angle = Geometry.getAngle( position2.x, position2.y, position);
					mass = _mapMass[_graph.nodes[i]]*_mapMass[_graph.nodes[j]];
					if ( mass == 0 )
						mass = 0.5;
					if ( distance < REPULSION_AREA*AVE_DISTANCE ){
						deltaDistance = REPULSION_AREA*AVE_DISTANCE - distance;		
						(_mapForce[_graph.nodes[i]] as Point).x -= deltaDistance*mass/_maxDegree*Math.cos(angle) * G_FACTOR;//*k
						(_mapForce[_graph.nodes[i]] as Point).y -= deltaDistance*mass/_maxDegree*Math.sin(angle) * G_FACTOR;//*k
						if ( _maxMove < Math.max((_mapForce[_graph.nodes[i]] as Point).x, (_mapForce[_graph.nodes[i]] as Point).y )){
							_maxMove = Math.max((_mapForce[_graph.nodes[i]] as Point).x, (_mapForce[_graph.nodes[i]] as Point).y )
						}
						(_mapForce[_graph.nodes[j]] as Point).x += deltaDistance*mass/_maxDegree*Math.cos(angle) * G_FACTOR;//*k
						(_mapForce[_graph.nodes[j]] as Point).y += deltaDistance*mass/_maxDegree*Math.sin(angle) * G_FACTOR;//*k
						if ( _maxMove < Math.max((_mapForce[_graph.nodes[j]] as Point).x, (_mapForce[_graph.nodes[j]] as Point).y )){
							_maxMove = Math.max((_mapForce[_graph.nodes[j]] as Point).x, (_mapForce[_graph.nodes[j]] as Point).y )
						}
					}
				}
			}
		}
		//停止算法
		override public function stop():void {
			if ( _timer != null ){
				_timer.stop();
			}
		}
		//启动算法
		override public function start():void {
			if ( _timer != null ){
				_completed = false;
				_timer.start();
			}
		}
		
		//返回是否执行结束。true为结束，false为没有结束
		override public function get finished():Boolean {
			return _completed;
		}
		
		//返回是否正在执行。true正在执行,false执行停止
		override public function get isRun():Boolean {
			if ( _timer == null || !_timer.running )
				return false;
			else 
				return false;
		}
		
		override public function get layoutName():String {
			return CLASS_NAME;
		}
	}
}