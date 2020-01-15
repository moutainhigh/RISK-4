package rms.alert.utils.geoboundary.beans;

import java.util.ArrayList;
import java.util.List;

public class Boundary {

	private List<MyPoint> boundary = new ArrayList<>();
	private double minX = -1, maxX = -1, minY = -1, maxY = -1;

	public Boundary() {
		super();
	}

	public List<MyPoint> getBoundary() {
		return boundary;
	}

	public void setBoundary(List<MyPoint> boundary) {
		this.boundary = boundary;
	}

	public void addMyPoint(MyPoint p) {
		boundary.add(p);
	}

	public void findMinMaxBoundary() {
		if (boundary.size() == 0) {
			return;
		}

		minX = boundary.get(0).getX();
		maxX = boundary.get(0).getX();
		minY = boundary.get(0).getY();
		maxY = boundary.get(0).getY();
		for (MyPoint pt : boundary) {
			minX = Math.min(pt.getX(), minX);
			maxX = Math.max(pt.getX(), maxX);
			minY = Math.min(pt.getY(), minY);
			maxY = Math.max(pt.getY(), maxY);
		}
	}

	public double getMinX() {
		return minX;
	}

	public double getMaxX() {
		return maxX;
	}

	public double getMinY() {
		return minY;
	}

	public double getMaxY() {
		return maxY;
	}

}
