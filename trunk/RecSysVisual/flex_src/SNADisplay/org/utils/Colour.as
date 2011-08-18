package SNADisplay.org.utils
{
	public class Colour
	{
		public static const BLUE 		: int = 0x0099FF;
		public static const RED			: int = 0xFF0000;
		public static const YELLOW 		: int = 0xFFFF00;
		public static const ORANGE 		: int = 0xFF6100;
		public static const GREEN 		: int = 0x32CD32;
		public static const PURPLE 		: int = 0xA020F0;
		public static const BLACK 		: int = 0x000000;
		public static const BROWN 		: int = 0xA0522D;
		public static const PINK 		: int = 0xFF00FF;
		public static const GRAY 		: int = 0xC0C0C0;
		public static const LIGHT_BLUE	: int = 0x87CEEB;
		public static const LIGHT_YELLOW: int = 0xFFFFCD;
		public static const LIGHT_GREEN : int = 0xBDFCC9;
		public static const LIGHT_PURPLE: int = 0xDDA0DD;
		public static const LIGHT_RED 	: int = 0xFFC0CB;
		public static const WHITE       : int = 0xFFFFFF;
		public static const MIDNIGHT_BLUE : int = 0x191970;
		public static const ORANGE_RED	: int = 0xFF4500;
		public static const DEEPSKY_BLUE: int = 0x00BFFF;
		public static const DARK_ORANGE : int = 0xFF8C00;
		public static function getColour(i:int):int{
			var n:int;
			n = i % 19;
			switch (n){
				case 0:
					return BLUE;
				case 1:
					return RED;
				case 2:
					return YELLOW;
				case 3:
					return ORANGE;
				case 4:
					return GREEN;
				case 5:
					return PURPLE;
				case 6:
					return BLACK;
				case 7:
					return BROWN;
				case 8:
					return PINK;
				case 9:
					return GRAY;
				case 10:
					return LIGHT_BLUE;
				case 11:
					return LIGHT_YELLOW;
				case 12:
					return LIGHT_GREEN;
				case 13:
					return LIGHT_PURPLE;
				case 14:
					return LIGHT_RED;
				case 15:
					return MIDNIGHT_BLUE;
				case 16:
					return ORANGE_RED;
				case 17:
					return DEEPSKY_BLUE;
				case 18:
					return DARK_ORANGE;
			}
			
			return BLUE;
		}
		public static function getColour2(i:Number):int{
			var color:int;
			var yellow:int = 0xFFFF00;
			var green:int = 0x00FF00;
			var greenBlue:int = 0x00CCFF;
			var blue:int = 0x0000FF;
			var range:int;
			var degree:int;
			color = yellow;
			range = (yellow - green) + (green - greenBlue) + (greenBlue - blue);
			if ( i < 0 ) 
				i = 0 ;
			if ( i > 1 )
				i = 1;
				
			degree = range*i;
			if ( degree <= (yellow - green) ){
				color -= degree;
			}
			else if ( degree > (yellow - green) && degree < (yellow - green) + (green - greenBlue) ){
				color = green - ( degree - (yellow - green) );
			}
			else if ( degree >= (yellow - green) + (green - greenBlue) && degree <= range ){
				color = greenBlue - ( range - degree );
			}
			return color;
		}
		
		public static function getWeightColour(n:int, defaultColor:int = GRAY):int {
			var color:int = defaultColor;
			if ( n < 0 || n > 510 )
				return color;
			n = 510 - n;
			if ( n <= 255 ){
				color = 0xFF0000 + n*0x000100;
			}
			else {
				color = 0xFFFF00 - (n-255)*0x010000;
			}
			return color;
		}

	}
}