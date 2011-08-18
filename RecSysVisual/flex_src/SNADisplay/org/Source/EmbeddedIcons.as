package SNADisplay.org.Source
{
	public class EmbeddedIcons
	{
		[Bindable]
		[Embed(source="images/ball.png")]
 		static public var ball:Class;
 		[Bindable]
 		[Embed(source="images/bars.png")]
 		static public var bars:Class;
 		[Bindable]
		[Embed(source="images/bulb.png")]
 		static public var bulb:Class;
 		[Bindable]
 		[Embed(source="images/budget.png")]
 		static public var budget:Class;
 		[Bindable]
 		[Embed(source="images/star.png")]
 		static public var star:Class;
 		[Bindable]
 		[Embed(source="images/clock.png")]
 		static public var clock:Class;
 		[Bindable]
 		[Embed(source="images/colorSquare.png")]
 		static public var colorSquare:Class;
 		[Bindable]
 		[Embed(source="images/excalmatry.png")]
 		static public var excalmatry:Class;
 		[Bindable]
 		[Embed(source="images/female.png")]
 		static public var female:Class;
 		[Bindable]
 		[Embed(source="images/file.png")]
 		static public var file:Class;
 		[Bindable]
 		[Embed(source="images/greenCube.png")]
 		static public var greenCube:Class;
 		[Bindable]
 		[Embed(source="images/group.png")]
 		static public var group:Class;
 		[Bindable]
 		[Embed(source="images/closeHand.png")]
 		static public var closeHand:Class;
 		[Bindable]
 		[Embed(source="images/leaf.png")]
 		static public var leaf:Class;
 		[Bindable]
 		[Embed(source="images/male.png")]
 		static public var male:Class;
 		[Bindable]
 		[Embed(source="images/note.png")]
 		static public var note:Class;
 		[Bindable]
 		[Embed(source="images/openHand.png")]
 		static public var openHand:Class;
 		[Bindable]
 		[Embed(source="images/org.png")]
 		static public var org:Class;
 		[Bindable]
 		[Embed(source="images/smileFace.png")]
 		static public var smileFace:Class;
 		[Bindable]
 		[Embed(source="images/blueCube.gif")]
 		static public var blueCube:Class;
 		
 		public static function getIcon(label:String):Class{
 			switch ( label ){
 				case "ball":
 					return ball;
 				case "bars":
 					return bars;
 				case "bulb":
 					return bulb;
 				case "budget":
 					return budget;
 				case "star":
 					return star;
 				case "clock":
 					return clock;
 				case "colorSquare":
 					return colorSquare;
 				case "excalmatry":
 					return excalmatry;
 				case "female":
 					return female;
 				case "file":
 					return file;
 				case "greenCube":
 					return greenCube;
 				case "group":
 					return group;
 				case "closeHand":
 					return closeHand;
 				case "leaf":
 					return leaf;
 				case "male":
 					return male;
 				case "note":
 					return note;
 				case "openHand":
 					return openHand;
 				case "org":
 					return org;
 				case "smileFace":
 					return smileFace;
 				case "blueCube":
 					return blueCube;
 				default :
 					return null;
 			}
 		}
	}
}