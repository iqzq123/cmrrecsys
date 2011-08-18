package SNADisplay.org.SNA.ConvexHull
{
	import SNADisplay.org.Graph.Model.IGraph;
	import SNADisplay.org.Graph.Logical.INode;
	
	import flash.geom.Point;
	import flash.utils.Dictionary;
	
	import mx.collections.ArrayCollection;
	import mx.collections.Sort;
	import mx.collections.SortField;
	
	public class ConvexHull
	{
		
		private var _clique:Array;
		private var _convexHull:Array;
		private var _sort:Sort;
		private var _startNode:INode;
		private var _col:ArrayCollection;
		private var _mapPosition:Dictionary;
		public function ConvexHull(graph:IGraph, clique:Array)
		{
			
			_clique = clique;
			_convexHull = new Array();
			_sort = new Sort();
			_col = new ArrayCollection();
			_mapPosition = graph.mapPosition;
			
		}
		public function find():Array {
			var node:INode;
			var sortedNodes:Array = new Array();
			
			if ( _clique.length <= 1 ){
				_convexHull = _clique;
				return _convexHull;
				
			}
			
			_startNode = _clique[0];
			//找到最左边的点
			for each ( node in _clique ){
				if ( ( _mapPosition[_startNode] as Point).x > ( _mapPosition[node] as Point).x )
					_startNode = node;
			}
			//trace("moset left:"+_startNode.id);
			//将剩余点加入到排序列表中
			for each ( node in _clique ){
				if ( _startNode !=  node )
					_col.addItem(node);
			}
			//排序余下的节点
			_sort.fields = [new SortField(null)];
			_sort.compareFunction = compareValues;
     		_col.sort = _sort;
       		_col.refresh();
       		if ( _startNode.id == "baselayouter" )
       			trace(_startNode.id+" "+( _mapPosition[_startNode] as Point));
       		//将_col中的有序节点导入到sortedNodes中
			for (var i:int = 0 ; i < _col.length ; i++ ){
				node = (_col.getItemAt(i) as INode);
				if ( _startNode.id == "baselayouter" ){
					trace(node.id);
					trace(( _mapPosition[node] as Point));
				}
				sortedNodes.push(node);
			}
			
			//凸包数组中加入最左端点
			_convexHull.push(_startNode);
			//排序队列中取出首节点加入到凸包中
			node = sortedNodes.shift();
			_convexHull.push(node);
			
			var targetNodePosition:Point;
			var lastNodePosition:Point;
			var secondLastNodePosition:Point;
			var x1:Number;
			var y1:Number;
			var x2:Number;
			var y2:Number;
			//倒数第一个节点和倒数第二的节点的坐标
			lastNodePosition = _mapPosition[ _convexHull[_convexHull.length - 1] ] as Point;
			secondLastNodePosition = _mapPosition[ _convexHull[_convexHull.length - 2] ] as Point;
			//已有凸包中倒数两个点的向量（x1,y1）
			x1 = lastNodePosition.x - secondLastNodePosition.x;
			y1 = lastNodePosition.y - secondLastNodePosition.y;
			for each ( node in sortedNodes ){
				//新节点和已有凸包最后一个节点的向量（x2,y2）
				targetNodePosition = _mapPosition[node] as Point;
				x2 = targetNodePosition.x - lastNodePosition.x;
				y2 = targetNodePosition.y - lastNodePosition.y;
				if ( node.id == "graphlayout_layout" ){
					trace("(x1,y1)="+x1+" "+y1);
					trace("(x2,y2)="+x2+" "+y2);
				}
				//如果向量(x2,y2)在（x1,y1）的右边，则弹出当前凸包中的最后一个节点
				while ( check(x1,y1,x2,y2) == 1 || check(x1,y1,x2,y2) == 0 ){
					_convexHull.pop();
					//如果弹出后凸包社团只剩下一个节点，那么跳出子循环
					if ( _convexHull.length == 1 ){
						break;
					}
					//更新凸包集合中的倒数第一个节点和倒数第二的节点的坐标
					lastNodePosition = _mapPosition[ _convexHull[_convexHull.length - 1] ] as Point;
					secondLastNodePosition = _mapPosition[ _convexHull[_convexHull.length - 2] ] as Point;
					//重新计算(x1,y1)和(x2,y2)
					x1 = lastNodePosition.x - secondLastNodePosition.x;
					y1 = lastNodePosition.y - secondLastNodePosition.y;
					x2 = targetNodePosition.x - lastNodePosition.x;
					y2 = targetNodePosition.y - lastNodePosition.y;
				}
				//对当前的node点考察完毕，加入到凸包集合中。注：现在Push进去的点不一定就是最终结果的点，只是符合当前状态
				_convexHull.push(node);
				//更新倒数第一个节点和倒数第二的节点的坐标
				lastNodePosition = _mapPosition[ _convexHull[_convexHull.length - 1] ] as Point;
				secondLastNodePosition = _mapPosition[ _convexHull[_convexHull.length - 2] ] as Point;
				//已有凸包中倒数两个点的向量（x1,y1）
				x1 = lastNodePosition.x - secondLastNodePosition.x;
				y1 = lastNodePosition.y - secondLastNodePosition.y;
			}
			return _convexHull;
		}
		//对节点序进行排序
		private function compareValues(a:INode, b:INode,fields:Array = null ):int{
			var result:int = 0;
			var aPoint:Point = _mapPosition[a] as Point;
			var bPoint:Point = _mapPosition[b] as Point;
			var startPoint:Point = _mapPosition[_startNode] as Point;
			var x1:Number;
			var y1:Number;
			var x2:Number;
			var y2:Number;
			var flag:Number;

			x1 = aPoint.x - startPoint.x;
			y1 = aPoint.y - startPoint.y;
			x2 = bPoint.x - startPoint.x;
			y2 = bPoint.y - startPoint.y;
			
			flag = x1*y2 - x2*y1;	//flag大于0,b点在oa点的左边，flag小于0,b点在0a点的右边
			if ( flag > 0 ) 	//a在b前
				result = 1;
			else if ( flag < 0 )	//a在b后
				result = -1;
			else { //flag==0的情况，oab在同一条直线上，或者oa重合,或者ob重合
				if ( x1 == 0 && y1 == 0 )	//如果a和原点o重合，那么a在b后
					result = -1;
				else if ( x2 == 0 && y2 == 0 )	//如果b和原电o重合，那么a在b前
					result = 1;
				else result = 0;		//a和b相等，相对位置不变
			}
			return result;	
		}
		
		private function check(x1:Number, y1:Number, x2:Number, y2:Number):int {
			var result:Number;
			result = x1*y2 -x2*y1;
			//左转
			if ( result > 0 )
				return 1;
			//两个点落在同一条直线上
			else if ( result == 0 ) 
				return 0;
			//右转
			else 
				return -1;
		}
	}
}