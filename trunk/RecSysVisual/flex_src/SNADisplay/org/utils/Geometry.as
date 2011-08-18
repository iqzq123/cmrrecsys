package SNADisplay.org.utils
{
	import flash.geom.Point;

	public class Geometry
	{
		public static function getAngle(x:Number, y:Number, p:Point):Number {
			var point:Point = p;
			var angle:Number;
			if ( x == point.x ){
				if ( y < point.y )
					return Math.PI*1.5;
				else if ( y > point.y )
					return Math.PI/2;
				else 
					return 0;
			}
			else {
				angle = Math.atan((y-point.y)/(x-point.x));
				if ( y < point.y && angle > 0 )
					return angle+Math.PI;
				if ( y > point.y && angle < 0 )
					return angle+Math.PI;
				if ( x < point.x && angle == 0 )
					return Math.PI;
				if ( y < point.y && angle < 0 )
					return angle+2*Math.PI;
			}
			return angle;
		}
		
		public static function arrowPosition(fromX:Number, fromY:Number, toX:Number, toY:Number):Array {
			var arr:Array = new Array;
			var point1:Point = new Point;
			var point2:Point = new Point;
			const LENGTH:Number = 10;
			var angle:Number = Math.atan2(toY-fromY, toX-fromX);
			var pi:Number = Math.PI;
			var angle1:Number;
			var angle2:Number;
			var arrowAngle:Number = pi/8;
			angle = (angle+pi*2)%(pi*2);
			if ( angle >= 0 && angle <= pi ){
				angle1 = angle + pi - arrowAngle;
				angle2 = angle1 + arrowAngle*2;
			}
			else if ( angle > pi && angle < pi*2 ){
				angle1 = angle - pi - arrowAngle;
				angle2 = angle1 + arrowAngle*2;
			}
			point1.x = LENGTH*Math.cos(angle1) + toX;
			point1.y = LENGTH*Math.sin(angle1) + toY;
			point2.x = LENGTH*Math.cos(angle2) + toX;
			point2.y = LENGTH*Math.sin(angle2) + toY;

			arr.push(point1);
			arr.push(point2);
			return arr;
		}
		//top-left bottom-left bottom-right top-right
		public static function line2rectangle(pointFrom:Point, potinTo:Point, radius:Number):Array{
			var results:Array = new Array;
			var angle:Number = Math.atan2(potinTo.y-pointFrom.y, potinTo.x-pointFrom.x);
			var point:Point;
			var pi:Number = Math.PI;
			
			point = new Point;
			point = Point.polar(radius, angle + pi/2);
			point.offset(pointFrom.x, pointFrom.y);
			results.push(point);
			
			point = new Point;
			point = Point.polar(radius, angle - pi/2);
			point.offset(pointFrom.x, pointFrom.y);
			results.push(point);
			
			point = new Point;
			point = Point.polar(radius, angle - pi/2);
			point.offset(potinTo.x, potinTo.y);
			results.push(point);
			point = new Point;
			point = Point.polar(radius, angle + pi/2);
			point.offset(potinTo.x, potinTo.y);
			results.push(point);
			return results;
		}
		
		public static function getControlPoint(center:Point, fromPoint:Point, toPoint:Point):Point {
			var controlPoint:Point = new Point;
			var angle1:Number = Math.atan2(fromPoint.y - center.y, fromPoint.x - center.x);
			var angle2:Number = Math.atan2(toPoint.y - center.y, toPoint.x - center.x);
			var angle:Number;
			var radius:Number = Point.distance(fromPoint, center);;
			var length:Number;
			var pi:Number = Math.PI;
			angle = (angle1 - angle2 + 2*pi)%(2*pi);
			if ( angle > pi ){
				//throw Error("in getControlPoint the angle between 2 anchors is no less than pi");
				//return null;
				controlPoint = Point.interpolate(fromPoint,toPoint,0.5);
			}
			else if ( angle < pi ){
				length = radius / Math.cos(angle/2);
				if ( length > radius*3 )
					length = radius*3;
				controlPoint = Point.polar(length, angle2+angle/2);
				controlPoint.offset(center.x, center.y);
			}
			else if( angle == pi ){
				length = 2*radius;
				controlPoint = Point.polar(length, angle2+pi/2);
				controlPoint.offset(center.x, center.y);
			}
			return controlPoint;
		}
	}
}