package com.dongji.app.ui;

public class Line { 
Point start; // Start point of line 
Point end; // End point of line 
// Create a line from two points 
Line(final Point start, final Point end) { 
this.start = new Point(start); 
this.end = new Point(end); 
 } 
// Create a line from two coordinate pairs 
 public Line(double xStart, double yStart, double xEnd, double yEnd) { 
 start = new Point(xStart, yStart); // Create the start point 
 end = new Point(xEnd, yEnd); // Create the end point 
 } 
 // Calculate the length of a line 
 double length() { 
 return start.distance(end); // Use the method from the Point class 
 } 
 // Convert a line to a string 
 public String toString() { 
 return "(" + start+ "):(" + end + ")"; // As "(start):(end)" 
 } // that is, "(x1, y1):(x2, y2)"
 
public Point intersects(final Line line1) { 
Point localPoint = new Point(0, 0); 
double num = (end.y - start.y)*(start.x - line1.start.x) - 
(end.x - start.x)*(start.y - line1.start.y); 
double denom = (end.y - start.y)*(line1.end.x - line1.start.x) - 
(end.x - start.x)*(line1.end.y - line1.start.y); 
localPoint.x = line1.start.x + (line1.end.x - line1.start.x)*num/denom; 
localPoint.y = line1.start.y + (line1.end.y - line1.start.y)*num/denom; 
return localPoint; 
}

}

