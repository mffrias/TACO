package forArielGodio;
public class Perimeter
{   
	//@ requires 0 < x && x <= Short.MAX_VALUE;
	//@ ensures \result == 4*x;
	/*@ pure @*/ long Perimeter(short x)
	{
		long squarePerimeter = 4 / (long)x;//long squarePerimeter = 4 * (long)x;
		return squarePerimeter;
	}

	//@ requires 0 < x && x <= Integer.MAX_VALUE;
	//@ ensures \result == 5*x;
	/*@ pure @*/ long Perimeter(int x)
	{
		long pentagonPerimeter = 5 * (long)x;
		return pentagonPerimeter;
	}

	//@ requires 0 < x && 6*x <= Long.MAX_VALUE;
	//@ ensures \result == 6*x;
	/*@ pure @*/ long Perimeter(long x)
	{
		long hexagonalPerimeter = 6 * x;
		return hexagonalPerimeter;
	}

	//@ requires 0 < x && 0 < y && 2*x + 2*y <= Integer.MAX_VALUE;
	//@ ensures \result == 2*x + 2*y;
	/*@ pure @*/ long Perimeter(int x, int y)
	{
		long perimeterRectangle = 2*((long)x + (long)y);
		return perimeterRectangle;
	}

	//@ requires 0 < x && 0 < y && 0 < z && x + y + z <= Integer.MAX_VALUE;
	//@ ensures \result == x + y + z;
	/*@ pure @*/ long Perimeter(int x, int y, int z)
	{
		long trianglePerimeter = (long)x + (long)y + (long)z;
		return trianglePerimeter;
	}

	//@ requires 0 < w && 0 < x && 0 < y && 0 < z && w + x + y + z <= Integer.MAX_VALUE;
	//@ ensures \result == w + x + y + z;
	/*@ pure @*/ long Perimeter(int w, int x, int y, int z)
	{
		long trapeziumPerimeter = (long)w + (long)x + (long)y + (long)z;
		return trapeziumPerimeter;
	}
}