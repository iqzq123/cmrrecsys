package SNADisplay.org.SNA.CommunityDetection.CliquePercolation
{
	import SNADisplay.org.Graph.Logical.*;
	import SNADisplay.org.SNA.CommunityDetection.ICommunityDetection;
	
	import flash.utils.Dictionary;
	//派系过滤算法
	public class CliquePercolation implements ICommunityDetection
	{
		private var _graphData:IGraphData;	//保存网结构络数据的
		private var _cliques:Array;		//派系数组
		private var _newClique:Array;	//新的派系数组
		private var _mapNodeCliques:Dictionary;	//节点和派系的映射
		private const K_CLIQUE:int = 4;		//CP算法的参数K
		private const OVERLAPPED:int = 3;
		private var _matrix:Array;
		private var _matrix_percolation:Array;
		private var _newCommunity:Array;
		private var _communities:Array;
		private var _dic:Dictionary;
		public function CliquePercolation(graphData:IGraphData)
		{
			_graphData = graphData;
			_cliques = new Array();
			_mapNodeCliques = new Dictionary;
			_communities = new Array();
		}
		//社团发现的执行函数
		public function detection():Array {
			var nodes:Array = _graphData.nodes.concat();
			var node:INode;
			var neighborNode:INode;
			var arrayA:Array;
			var arrayB:Array;
			var arrayC:Array;
			var mapDictionary:Dictionary = new Dictionary;

			//遍历图中每一个顶点
			for each ( node in nodes ) {
				//trace("********************************************************");
				mapDictionary[node] = true;
				arrayA = new Array();
				arrayB = new Array();
				arrayC = new Array();
				arrayA.push(node);
				//将Node的邻接点加入到数组arrayB中
				for each ( neighborNode in node.edges ){
					if ( mapDictionary[neighborNode] == undefined )
						arrayB.push(neighborNode);
				}
				//如果Node存在邻接点，那么对node点进行分析,查找每个节点所属的完全图派系单元
				if ( arrayB.length > 0 ) {
					arrayC = arrayB.concat();
					findCliques(arrayA, arrayB, arrayC);
				}
				
				//trace("=========================================================");
			}
			
			var arr:Array;
			var i:int = 0;
			for each ( arr in _cliques ) {
				trace("== "+i+" == clique:");
				i++;
				for each ( node in arr ) {
					trace(node.id);
				}
			}
			//初始化派系矩阵
			prepareMatrix();
			//进行派系过滤
			percolation();
			giveResult();
			
			i = 0;
			for each ( arr in _communities ) {
				trace("== "+i+" == community:");
				i++;
				for each ( node in arr ) {
					trace(node.id);
				}
			}
			
			return _communities;
		}
		//arrA：已经构成完全图的点。arrB：arrA点集的邻接点集合。arrC：完全图生成的备选点。注：arrB 不一定等于 arrC
		private function findCliques(arrA:Array, arrB:Array, arrC:Array):void {
			var targetNode:INode;
			var node:INode;
			//初始数据
			var arrayA:Array = new Array();
			var arrayB:Array = new Array();
			var arrayC:Array = new Array();
			//分支一的拷贝数据
			var arrayA2:Array = new Array();
			var arrayB2:Array = new Array();
			var arrayC2:Array = new Array();
			//分支二的拷贝数据
			var arrayA3:Array = new Array();
			var arrayB3:Array = new Array();
			var arrayC3:Array = new Array();
			//分支二用到的数据
			var arrayLeft:Array = new Array();
		
			arrayA = arrA.concat();
			arrayB = arrB.concat();
			arrayC = arrC.concat();
			//从候选点集arrayC中弹出一个目标节点targetNode，同时在arrayB中将其删除
			targetNode = arrayC.shift();
			arrayB.splice(arrayB.indexOf(targetNode),1);
			//将targetNode加入到arrayA2中，作为当前已经获得完全图结果
			arrayA2 = arrayA.concat((targetNode));
			//在arrayB中找出targetnode的邻接节点,结果用arrayB2保存。
			arrayB2 = update(arrayB,targetNode);		//arrayB2为arrayA2的邻接节点集合
			
			if ( arrayB2.length == 0 ){	//arrayA2点集没有其他相关的邻接节点了。即最大完全图已被发现。
				//如果arrayA2的完全图规模满足最小派系的规模要求
				if ( arrayA2.length >= K_CLIQUE ){
					_newClique = new Array();
					_newClique = arrayA2.concat();
					addClique(arrayA[0],_newClique); //保存新派系
				}
			}
			else{	//arrayA2点集还有其他相关的邻接节点arrayB2。即还存在比arrayA2更大的完全图。（至少规模要多1个节点。）
				//拷贝arrayB2到arrayC2，arrayC2是下一次发现的候选点。分支一的邻接节点都是候选节点。
				arrayC2 = arrayB2.concat();
				//递归发现新派系
				if ( arrayA2.length + arrayC2.length >= K_CLIQUE )
					findCliques(arrayA2,arrayB2,arrayC2);
				
			}
			//对arrayC的抓取不放回的遍历，考虑完一个节点targetNode后，就不用考虑它和它完全图相关的其他节点了。
			//那些情况都在分支一中被考虑到了。
			//arrayLeft是不含targetNode的arrayB减去arrayB2后剩余的顶点集合。新的完全图肯定包含arrayLeft中的一个点。
			//arrayB2都是直接与targetNode点和arrayA中的点集的完全图相关点
			//arrayLeft是分支二的候选点集
			arrayLeft = subtract(arrayB, arrayB2);
			if ( arrayLeft.length > 0 ){	//如果arrayLeft不为空
				
				arrayA3 = arrayA.concat();	//arryA3没有加入targetNode
				arrayB3 = arrayB.concat();	//arryB已经除去了targetNode
				arrayC3 = arrayLeft.concat(); //候选点不包括和targetNode相关的点
				if ( arrayA3.length + arrayC3.length >= K_CLIQUE )
					findCliques(arrayA3,arrayB3,arrayC3);
			}
		}
		//在arrayB中找出targetnode的邻接节点
		private function update(arrayB:Array, targetnode:INode ):Array {
			var nodeA:INode;
			var nodeB:INode;
			var node:INode;
			var dic:Dictionary = new Dictionary;
			var result:Array = new Array();
			//目标点的邻居点都做上标记
			for each ( node in targetnode.edges ){
				dic[node] = true;
			}
			//遍历数组B,将属于目标点邻居点的点加到结果数组中。
			for each ( node in arrayB ){
				if ( dic[node] == true )
					result.push(node);
			}
			return result;
		}
		//求集合arrayA减去集合arrayB的差
		private function subtract(arrayA:Array, arrayB:Array ):Array {
			var node:INode;
			var dic:Dictionary = new Dictionary;
			var result:Array = new Array();
			//目标点的邻居点都做上标记
			for each ( node in arrayB ){
				dic[node] = true;
			}
			for each ( node in arrayA ){
				if ( dic[node] == undefined )
					result.push(node);
			}

			return result;
		}
		//保存新发现的派系
		private function addClique(targetNode:INode, clique:Array):void {
			var tempClique:Array;
			var node:INode;
			var dic:Dictionary;
			var existed:Boolean = false;

			//如果映射字典变量元素为空，则初始化
			if ( _mapNodeCliques[targetNode] == undefined ) {
				_mapNodeCliques[targetNode] = new Array();
			}
			//去重。每考察完一个目标点，会标记为已考察，在以后的计算中，将不会引入改目标点进行考察。但是，会生成包含该目标点的
			//完全图的子图。比如之前生成了完全图（0，1,2,3,4）,之后虽然不会再考察点0，但是会产生完全图（1,2,3,4）,此时需要
			//将类似完全图（1,2,3,4）这样的情况给去掉，不去保存
			for each ( tempClique in (_mapNodeCliques[targetNode] as Array) ){
				if ( tempClique.length > clique.length ){
					existed = true;
					dic = new Dictionary;
					for each ( node in tempClique ){
						dic[node] = true;
					}
					for each ( node in clique ){
						if ( dic[node] == undefined ){
							existed = false;
							continue;
						}
					}
					if ( existed == true ){
						return ;
					}
				}
			}
			//如果此完全图是新图，则进行存储
			if ( existed == false ){
				_cliques.push(_newClique);
				
				for each ( node in _newClique ) {
					if ( _mapNodeCliques[node] == undefined ) {
						_mapNodeCliques[node] = new Array();
					}
					(_mapNodeCliques[node] as Array).push(clique);
				}
				return ;
			}
		}
		
		//将size个已经发现的派系（完全图）建立size*size的矩阵，只存储右上角部分。
		//元素ij(i != j)表示派系i和派系j之间重合点的个数如果等于k-1则赋值为1，否则为0
		//对角线上的元素等于派系的标号
		private function prepareMatrix():void {
			var size:int = _cliques.length;
			var i:int;
			var j:int;
			_matrix = new Array(size*(size+1)/2);
			for ( i = 0 ; i < size ; i ++ ) {
				for ( j = i ; j < size; j ++ ) {
					if ( i == j ) {
						_matrix[countSeq(i,j,size)] = i;//对角线的点存储派系的标号
					}
					else {
						//如果两个完全同联通，则赋值为1，否则为0
						if ( overlapped((_cliques[i] as Array), (_cliques[j] as Array)) == K_CLIQUE-1 )
							_matrix[countSeq(i,j,size)] = 1;
						else
							_matrix[countSeq(i,j,size)] = 0;
					}
				}
			}
		}
		//将矩阵坐标变化成数组坐标的变换函数
		private function countSeq(n:int, m:int, size:int):int{
			if ( n > m ) {
				var i:int;
				i = n;
				n = m;
				m = i;
			}
			return (2*size+1-n)*n/2+m-n;
		}
		//两个社团彼此重叠的点的个数
		private function overlapped(cliqueA:Array, cliqueB:Array):int {
			var dic:Dictionary = new Dictionary;
			var node:INode;
			var result:int = 0;
			for each ( node in cliqueA ){
				dic[node] = true;
			}
			for each ( node in cliqueB ){
				if ( dic[node] != undefined ){
					result++;
				}
			}
			return result;
		}
		//将联通的派系聚合在一起
		private function percolation():void {
			var size:int = _cliques.length;
			var i:int;
			var j:int;
			for ( i = 0 ; i < size ; i ++ ) {
				for ( j = i+1 ; j < size; j ++ ) {
					if ( _matrix[countSeq(i,j,size)] == 1 ){	//如果为1，表示j和i相联通
						//矩阵上元素ii和元素jj都更新为两者最小值
						if ( _matrix[countSeq(j,j,size)] < _matrix[countSeq(i,i,size)] )
							_matrix[countSeq(i,i,size)] = _matrix[countSeq(j,j,size)];
						else
							_matrix[countSeq(j,j,size)] = _matrix[countSeq(i,i,size)];
					}
					
				}
			}
		}
		//得出社团划分结果
		private function giveResult():void {
			var communityCliqueIndex:Array;
			var community:Array;
			var size:int = _cliques.length;
			var i:int;
			var j:int;
			_dic = new Dictionary;
			for ( i = 0 ; i < size ; i ++ ){
				if ( _matrix[countSeq(i,i,size)] == i ){ //如果元素ii为i，表示它是社团中序号最小的派系
					communityCliqueIndex = new Array;	//创建新的社团
					communityCliqueIndex.push(i);		//加入当前的派系序号
					//加入之后的所有和i同属一个社团的的派系序号
					for ( j = i+1 ; j < size ; j++ ){
						if (  _matrix[countSeq(j,j,size)] == i )
							communityCliqueIndex.push(j);
					}
					//将派系序号组生成为社团节点集合
					community = buildCommunity(communityCliqueIndex);
					_communities.push(community);
				}
			} 
		}
		//将派系序号组生成为社团节点集合
		private function buildCommunity(communityCliqueIndex:Array):Array{
			var community:Array = new Array;
			var mapNode:Dictionary = new Dictionary;
			var node:INode;
			var index:int;
			for each ( index in communityCliqueIndex ){
				for each ( node in _cliques[index] ){
					if ( mapNode[node] == undefined ){	//如果没有出现过，则添加的社团中
						community.push(node);	
						mapNode[node] == true;	//标记为已经出现过
					}
				}
			}
			return community;
		}
		

		
	}
}