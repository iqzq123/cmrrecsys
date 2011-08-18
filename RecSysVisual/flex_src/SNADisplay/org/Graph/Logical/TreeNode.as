package SNADisplay.org.Graph.Logical
{
	import SNADisplay.org.Graph.Logical.INode;
	public class TreeNode
	{
		public var node:INode;		//节点的逻辑结构
		public var level:int;		//节点的深度，根节点为0
		public var children:Array;
		public function TreeNode()
		{
			children = new Array;
		}

	}
}