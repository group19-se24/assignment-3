/*
 Copyright 1995-2017 Esri

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 For additional information, contact:
 Environmental Systems Research Institute, Inc.
 Attn: Contracts Dept
 380 New York Street
 Redlands, California, USA 92373

 email: contracts@esri.com
 */

package com.esri.core.geometry;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.Arrays;

public class TestSamS extends TestCase {


    final static int coverageSplitSegments_Goal = 26;
	static int testCase = 0;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}


	static Polygon makePolygon() {
		Polygon poly = new Polygon();
		poly.startPath(0, 0);
		poly.lineTo(10, 10);
		poly.lineTo(20, 0);

		return poly;
	}


	// Requirement: a normalized distance between two points that is 0
	@Test
	public void testEnvelopesAlmostIntersectingGeneratesThreePointsInDoubleMin() {
		OperatorFactoryLocal engine = OperatorFactoryLocal.getInstance();
		OperatorClip clipOp = (OperatorClip) engine.getOperator(Operator.Type.Clip);
		SpatialReference spatialRef = SpatialReference.create(3857);

		// Cursor implementation
		Polygon p2 = new Polygon();
		p2.addEnvelope(new Envelope2D(0, 0, 50, 100), false);
		p2.addEnvelope(new Envelope2D(Double.MIN_VALUE, 5, 0, 95), true);

		Polygon clippedPoly = (Polygon) clipOp.execute(p2,
				new Envelope2D(-10, -10, 110, 45), spatialRef, null);

		int counter = 0;
		for (Point2D point : clippedPoly.getCoordinates2D()) {
			if (point.x == Double.MIN_VALUE) counter++;
		}
		assertEquals(3, counter);
	}

	// Requirement: Having a polygon that does not contain any points
	@Test
	public void testIsPointInPolygonEmptyPolygon() {
		Polygon p = new Polygon();
		assertEquals(0, PointInPolygonHelper.isPointInPolygon(p, 1,1,0));
	}

	// Requirement: Having a point that is not inside the polygon
	@Test
	public void testIsPointInPolygonPointOutsidePolygon() {
		Polygon p = makePolygon();
		assertEquals(0, PointInPolygonHelper.isPointInPolygon(p, -10,-10,0));
	}

	// Requirement: Having a point that is inside the polygon
	@Test
	public void testIsPointInPolygonPointOutsidePolygonHighTolerance() {
		Polygon p = makePolygon();
		assertEquals(1, PointInPolygonHelper.isPointInPolygon(p, 1,1,0));
	}
}
