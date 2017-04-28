package com.dongji.app.ui;

public class Point {

	public double x;
	public double y;
	
	Point(double xVal,double yVal)
	{
		x = xVal;
		y = yVal;
	}
	
	Point(final Point oldPoind)
	{
		x = oldPoind.x;
		y = oldPoind.y;
	}
	
	void move(double xDelta, double yDelta)
	{
		x += xDelta;
		y += yDelta;
	}
	
	double distance(final Point aPoint)
	{
		return Math.sqrt((x - aPoint.x)*(x - aPoint.x) + ( y -aPoint.y)* (y -aPoint.y));
	}
}
