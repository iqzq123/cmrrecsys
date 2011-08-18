package SNADisplay.org.SNA.CommunityDetection.Default
{
	import SNADisplay.org.Graph.Logical.IGraphData;
	import SNADisplay.org.Graph.Logical.INode;
	import SNADisplay.org.SNA.CommunityDetection.ICommunityDetection;
	
	import flash.utils.Dictionary;
	public class DefaultCommunities implements ICommunityDetection
	{
		private var _graphData:IGraphData;	//网络的数据结构
		private var _xmlData:XML;		//网络的xml原始结构
		private var _communities:Array;	//社团结构
		private var _communityMap:Array;	//社团id和社团数组映射关系
		private var _dic:Dictionary;	//哈希表
		private var _keys:Object = {};	//排序用的临时对象
		public function DefaultCommunities(graphData:IGraphData)
		{
			_graphData = graphData;
			_xmlData = graphData.xmlData;
			_communities = new Array;
			_communityMap = new Array;
			_dic = new Dictionary;
		}
		public function detection():Array{
			var xnode:XML;
			var node:INode;
			var nodeId:String;
			var s:String;
			var communityId:Array = new Array;
			for each(xnode in _xmlData.descendants("Node")) {
				_keys = new Array;	//初始化排序用的临时变量
				nodeId = xnode.@id;
				s = xnode.@communityid;
				communityId = s.split(','); 	//获得当前点所属的社团id
				communityId = communityId.filter(removedDuplicates); //过滤，去重复，去空值
				cluster(nodeId, communityId);	//将当前点加入到对应的社团中
			}
			return _communities;
		}
		private function cluster(id:String, comIdArr:Array):void{
			var comId:String;
			var community:Array;
			for each ( comId in comIdArr ){
				if ( comId == "" ){	//如果社团id为空，跳过
					continue;
				}
				if ( _dic[comId] == undefined ){	//如果是第一次遇到的社团id,建立相应的社团数组
					community = new Array;
					_dic[comId] = community;	//将id与社团数组相映射
					community.push(_graphData.getNodeById(id));
					_communities.push(community);	//将当前节点加入到社团数组中
				}
				else {
					community = _dic[comId] as Array;	//找到此与社团id对应的社团数组
					community.push(_graphData.getNodeById(id));	//将当前节点加入到社团数组中
				}
			}
		}
		private function removedDuplicates(item:Object, idx:uint, arr:Array):Boolean {   
             if ( _keys.hasOwnProperty(item) || item=="" ){   //重复或者为空的id进行消去
                 return false;   
             } 
             else {   
                 _keys[item] = item;   
                 return true;   
             }   

        }  

	}
}