package com.esri.core.geometry;

import org.junit.Assert;
import org.junit.Test;

public class TestIntersectLineLineExact {
    @Test
    public void noIntersectionsWhenNonIntersectingLines() {
        Line line1 = new Line(0.0, 0.0, 2.0, 2.0);
        Line line2 = new Line(2.0, 0.0, 4.0, 2.0);
        Point2D[] intersectionPoints = new Point2D[2];
        double[] param1 = new double[2];
        double[] param2 = new double[2];
        int numIntersections = line1._intersectLineLineExact(line1, line2, intersectionPoints, param1, param2);
        Assert.assertEquals(0L, (long)numIntersections);
    }

    @Test
    public void oneIntersectionWhenSameStartPoint() {
        Line line1 = new Line(0.0, 0.0, 0.0, 2.0);
        Line line2 = new Line(0.0, 0.0, 2.0, 0.0);
        Point2D[] intersectionPoints = new Point2D[2];
        double[] param1 = new double[2];
        double[] param2 = new double[2];
        int numIntersections = line1._intersectLineLineExact(line1, line2, intersectionPoints, param1, param2);
        Assert.assertEquals(1, numIntersections);
    }

    @Test
    public void oneIntersectionWhenSameEndPoint() {
        Line line1 = new Line(0.0, 2.0, 0.0, 0.0);
        Line line2 = new Line(2.0, 0.0, 0.0, 0.0);
        Point2D[] intersectionPoints = new Point2D[2];
        double[] param1 = new double[2];
        double[] param2 = new double[2];
        int numIntersections = line1._intersectLineLineExact(line1, line2, intersectionPoints, param1, param2);
        Assert.assertEquals(1, numIntersections);
    }

    @Test
    public void twoIntersectionsWhenReverseStartAndEndPoints() {
        Line line1 = new Line(2.0, 2.0, 0.0, 0.0);
        Line line2 = new Line(0.0, 0.0, 2.0, 2.0);
        Point2D[] intersectionPoints = new Point2D[2];
        double[] param1 = new double[2];
        double[] param2 = new double[2];
        int numIntersections = line1._intersectLineLineExact(line1, line2, intersectionPoints, param1, param2);
        Assert.assertEquals(2, numIntersections);
    }

    @Test
    public void twoIntersectionsWhenSameLine() {
        Line line1 = new Line(0.0, 0.0, 1.0, 1.0);
        Line line2 = new Line(0.0, 0.0, 1.0, 1.0);
        Point2D[] intersectionPoints = new Point2D[2];
        double[] param1 = new double[2];
        double[] param2 = new double[2];
        int numIntersections = line1._intersectLineLineExact(line1, line2, intersectionPoints, param1, param2);
        Assert.assertEquals(2, numIntersections);
    }

    @Test
    public void twoIntersectionsWhenSameDegenerates() {
        Line line1 = new Line(0.0, 0.0, 0.0, 0.0);
        Line line2 = new Line(0.0, 0.0, 0.0, 0.0);
        Point2D[] intersectionPoints = new Point2D[2];
        double[] param1 = new double[2];
        double[] param2 = new double[2];
        int numIntersections = line1._intersectLineLineExact(line1, line2, intersectionPoints, param1, param2);
        Assert.assertEquals(2, numIntersections);
    }

    @Test
    public void twoIntersectionsWhenReverseSameLine() {
        Line line1 = new Line(0.0, 0.0, 1.0, 1.0);
        Line line2 = new Line(1.0, 1.0, 0.0, 0.0);
        Point2D[] intersectionPoints = new Point2D[2];
        double[] param1 = new double[2];
        double[] param2 = new double[2];
        int numIntersections = line1._intersectLineLineExact(line1, line2, intersectionPoints, param1, param2);
        Assert.assertEquals(2, numIntersections);
    }
}