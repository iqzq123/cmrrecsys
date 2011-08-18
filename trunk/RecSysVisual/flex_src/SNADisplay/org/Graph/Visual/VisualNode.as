package SNADisplay.org.Graph.Visual
{
	import SNADisplay.org.Graph.Logical.INode;
	import SNADisplay.org.Source.EmbeddedIcons;
	import SNADisplay.org.utils.Colour;
	
	import mx.containers.Canvas;
	import mx.containers.VBox;
	import mx.controls.Image;
	import mx.controls.Label;
	import mx.core.UIComponent;
	
	public class VisualNode extends VBox
	{
		private const ICON_SIZE:int = 10;
		private var _nodeId:String; //节点id
		private var _node:INode;	//节点的逻辑数据结构
		private var _image:Image;	//节点的贴图

		private var _drawIcon:Canvas;
		private var _drawComp:UIComponent; //绘制节点图案的变量
		private var _style:Boolean; 	//false为绘制模式，true为贴图模式
		private var _iconSize:Number;   //绘制图案的尺寸，正方形边长
		private var _select:Boolean;	//是否选中
		private var _color:int;		//节点颜色
		private var _oldImage:Class;
		
		public function VisualNode()
		{
			super();
			_style = false;
			_select = false;
			_iconSize = ICON_SIZE;
			_color = Colour.GRAY;
		//	_label = new Label();
		
			this.mouseChildren = false;
			this.setStyle("horizontalAlign","center");

			if ( _style == false ){
				createDrawIcon();
				this.addChild(_drawIcon);
			}
			else {
				_image = new Image();
				_image.source = EmbeddedIcons.ball;
				_oldImage = EmbeddedIcons.ball;
				this.addChild(_image);
			}


		}
		
		public function refresh():void {
			this.removeAllChildren();
			if ( _style == false ){
				createDrawIcon();
				this.addChild(_drawIcon);
			}
			else {
				_image = new Image();
				_image.source = EmbeddedIcons.ball;
				_oldImage = EmbeddedIcons.ball;
				this.addChild(_image);
			}
		}
		public function get nodeId():String {
			return _nodeId;
		}
		
		public function set nodeId(id:String):void {
			_nodeId = id;
		}
		
		public function get node():INode {
			return _node;
			
		}
		
		public function set node(node:INode):void {
			_node = node;
			if ( _node != null )
				this.toolTip = _node.id+" hello";
		}
		
		public function get nodeIcon():UIComponent{//:Image {
			if ( _style == false )
				return _drawIcon as UIComponent;
			else 
				return _image as UIComponent;
		}
		public function set style(b:Boolean):void {
			//如果设置不改变原有值，直接返回，不做操作
			if ( _style == b )
				return;
			else
				_style = b;
			if ( b == false ){ //绘制节点图案
				this.removeAllChildren();
				_image = null;
				this.setStyle("horizontalAlign","center");
				createDrawIcon();
				this.addChild(_drawIcon);
			}
			else { //贴图片
				this.removeAllChildren();
				_drawIcon.removeAllChildren();
				_drawIcon = null;
				this.setStyle("horizontalAlign","center");
				_image = new Image();
				_image.source = EmbeddedIcons.ball;
				_oldImage = EmbeddedIcons.ball;
				this.addChild(_image);
			}
			this.validateNow();
		}
		//绘制节点图案
		private function createDrawIcon():void {
			_drawIcon = new Canvas();
			_drawIcon.width = _iconSize;
			_drawIcon.height = _iconSize;
			_drawComp = new UIComponent;
			_drawIcon.addChild(_drawComp);
			_drawComp.graphics.beginFill(_color,1);
			_drawComp.graphics.lineStyle(1,Colour.BLACK,1);
			_drawComp.graphics.drawCircle(_iconSize/2,_iconSize/2,_iconSize/2);
			_drawComp.graphics.endFill();
		}
		//选择节点的响应操作
		public function seleted():void {
			_select = true;
			if ( _style == true ){
				if ( _oldImage != EmbeddedIcons.star )
					_image.source = EmbeddedIcons.star;
				else
					_image.source = EmbeddedIcons.greenCube;
			}else {
				if ( _style == false ){
					//填充红色
					_drawComp.graphics.clear();
					_drawComp.graphics.beginFill(Colour.RED,1);
					_drawComp.graphics.lineStyle(1,Colour.BLACK,1);
					_drawComp.graphics.drawCircle(_iconSize/2,_iconSize/2,_iconSize/2);
					_drawComp.graphics.endFill();
				}
			}
		}
		
		//取消选择的响应函数
		public function unSeleted():void {
			_select = false;
			if ( _style == true )
				_image.source = _oldImage;
			else {
				if ( _style == false ){
					//填充灰色
					_drawComp.graphics.clear();
					_drawComp.graphics.beginFill(_color,1);
					_drawComp.graphics.lineStyle(1,Colour.BLACK,1);
					_drawComp.graphics.drawCircle(_iconSize/2,_iconSize/2,_iconSize/2);
					_drawComp.graphics.endFill();
				}
			}
		}
		//鼠标移动到节点上的响应
		public function mouseOver():void {
			if ( _select == false ){
				if ( _style == false ){
					//节点变大，填充红色
					_drawComp.graphics.clear();
					_drawComp.graphics.beginFill(_color,1);
					_drawComp.graphics.lineStyle(1,Colour.BLACK,1);
					_drawComp.graphics.drawCircle(_iconSize/2,_iconSize/2,_iconSize*1.2/2);
					_drawComp.graphics.endFill();
				}
			}
				
		}
		//鼠标移出节点上方的响应
		public function mouseOut():void {
			if ( _select == false ){
				if ( _style == false ){
					//节点回复到原来大小，填充灰色
					_drawComp.graphics.clear();
					_drawComp.graphics.beginFill(_color,1);
					_drawComp.graphics.lineStyle(1,Colour.BLACK,1);
					_drawComp.graphics.drawCircle(_iconSize/2,_iconSize/2,_iconSize/2);
					_drawComp.graphics.endFill();
				}	
			}	
		}
		public function set image(image:Class):void {
			if ( image != null ){
				_image.source = image;
				_oldImage = image;
			}
		}
		//返回节点被选取的状态
		public function get select():Boolean {
			return _select;
		}

		
		public function set color(color:int):void {
			_drawComp.graphics.clear();
			_color = color;
			_drawComp.graphics.beginFill(_color,1);
			_drawComp.graphics.lineStyle(1,Colour.BLACK,1);
			_drawComp.graphics.drawCircle(_iconSize/2,_iconSize/2,_iconSize/2);
			_drawComp.graphics.endFill();
		}
		
		public function colorDefault():void {
			_drawComp.graphics.clear();
			_color = Colour.GRAY;
			_drawComp.graphics.beginFill(_color,1);
			_drawComp.graphics.lineStyle(1,Colour.BLACK,1);
			_drawComp.graphics.drawCircle(_iconSize/2,_iconSize/2,_iconSize/2);
			_drawComp.graphics.endFill();
		}
		
		public function set size(s:int):void {
			if ( s <= 0 ){
				_iconSize = ICON_SIZE;
			}
			else {
				_iconSize = s;
			}
			refresh();
		}
		
	}
}