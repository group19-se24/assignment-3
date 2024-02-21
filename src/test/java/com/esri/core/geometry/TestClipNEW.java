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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;

import static com.esri.core.geometry.TestClip.*;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;



import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLOutput;

public class TestClipNEW{
    // This static initialization ensures it's only done once.
    //static {
    //    BranchCC.visitedBranch = new boolean[36];
    //}

    @BeforeClass
    public static void beforeClass() {
        BranchCC.visitedBranch = new boolean[37];
        // Optional: Reset or prepare anything before each test
    }

    @AfterClass
    public static void afterClass() throws Exception {
        System.out.println("HEJ");
        int visitedBranchesCount = 0;
        try (FileWriter writer = new FileWriter("branch_coverage_report_1_After4AddedTests.txt", true)) { // true to append
            for (int i = 0; i < BranchCC.visitedBranch.length; i++) {
                if (BranchCC.visitedBranch[i]) {
                    writer.write("Branch " + i + " was executed.\n");
                    visitedBranchesCount++;
                } else {
                    writer.write("Branch " + i + " was not executed.\n");
                }
            }
            double coveragePercentage = ((double) visitedBranchesCount / BranchCC.visitedBranch.length) * 100;
            writer.write(String.format("Branch Coverage: %.2f%%\n", coveragePercentage));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testClipEmptyGeometry() {
        //added
        // Requirement: Clipping an empty geometry should return an empty geometry
        Geometry emptyGeom = new Polygon();
        assertTrue(emptyGeom.isEmpty());

        Geometry result = Clipper.clip(emptyGeom, new Envelope2D(0, 0, 10, 10), 0, 0);
        assertTrue(result.isEmpty());
        // Validates that the clipping operation correctly handles empty input geometries.
        //assertTrue(BranchCC.visitedBranch[0]);

    }

    @Test
    public void testClipWithEmptyExtent() {
        //added
        // Requirement: Clipping any geometry with an empty extent should result in an empty geometry
        Point pointGeom = new Point(5, 5);
        Envelope2D emptyExtent = new Envelope2D();

        Geometry result = Clipper.clip(pointGeom, emptyExtent, 0, 0);
        assertTrue(result.isEmpty());
        // Validates that an empty clipping extent results in no output geometry

        //assertTrue(BranchCC.visitedBranch[2]);
    }

    @Test
    public void testGeometryFullyInsideExtent() {
        //added
        // Requirement: A geometry fully inside the clipping extent should remain unchanged after clipping
        Point pointGeom = new Point(5, 5);
        Envelope2D extent = new Envelope2D(0, 0, 10, 10);

        Geometry result = Clipper.clip(pointGeom, extent, 0, 0);
        assertFalse(result.isEmpty());
        // Validates/verifies that geometry completely within the extent are preserved by the clipping process

        //assertTrue(BranchCC.visitedBranch[10] || BranchCC.visitedBranch[5]);
    }

    @Test
    public void testEnvelopePartiallyIntersectingExtent() {
        //added
        //create an envelope that partially intersects the clipping extent
        // Requirement: A envelope partially intersecting the clipping extent should be clipped by its intersection area.

        Envelope envelopeGeom = new Envelope(5, 5, 15, 15); // This envelope extends beyond the extent
        Envelope2D extent = new Envelope2D(0, 0, 10, 10);

        Geometry result = Clipper.clip(envelopeGeom, extent, 0, 0);
        assertFalse("The clipped geometry should not be empty when the envelope partially intersects the extent", result.isEmpty());

        // validates/checks that envelopes that only partially intersect the clipping extent dont give nothing back.

        //assertTrue("Branch 7 (Envelope type check) should be visited", BranchCC.visitedBranch[7]);
        //assertTrue("Branch 8 (Envelope intersects extent) should be visited", BranchCC.visitedBranch[8]);
        //assertFalse("Branch 9 (Envelope does not intersect extent) should not be visited", BranchCC.visitedBranch[9]);
    }












    @Test
    public void testClipGeometries() {
        //System.out.println("WHHHAT");
        // RandomTest();
        OperatorFactoryLocal engine = OperatorFactoryLocal.getInstance();
        OperatorClip clipOp = (OperatorClip) engine
                .getOperator(Operator.Type.Clip);

        Polygon polygon = makePolygon();
        SimpleGeometryCursor polygonCurs = new SimpleGeometryCursor(polygon);

        Polyline polyline = makePolyline();

        SimpleGeometryCursor polylineCurs = new SimpleGeometryCursor(polyline);

        MultiPoint multipoint = makeMultiPoint();
        SimpleGeometryCursor multipointCurs = new SimpleGeometryCursor(
                multipoint);

        Point point = makePoint();
        SimpleGeometryCursor pointCurs = new SimpleGeometryCursor(point);

        SpatialReference spatialRef = SpatialReference.create(3857);

        Envelope2D envelope = new Envelope2D();
        envelope.xmin = 0;
        envelope.xmax = 20;
        envelope.ymin = 5;
        envelope.ymax = 15;

        // Cursor implementation
        GeometryCursor clipPolygonCurs = clipOp.execute(polygonCurs, envelope,
                spatialRef, null);
        Polygon clippedPolygon = (Polygon) clipPolygonCurs.next();
        double area = clippedPolygon.calculateArea2D();
        assertTrue(Math.abs(area - 25) < 0.00001);

        // Single Geometry implementation
        clippedPolygon = (Polygon) clipOp.execute(polygon, envelope,
                spatialRef, null);
        area = clippedPolygon.calculateArea2D();
        assertTrue(Math.abs(area - 25) < 0.00001);

        // Cursor implementation
        GeometryCursor clipPolylineCurs = clipOp.execute(polylineCurs,
                envelope, spatialRef, null);
        Polyline clippedPolyline = (Polyline) clipPolylineCurs.next();
        double length = clippedPolyline.calculateLength2D();
        assertTrue(Math.abs(length - 10 * Math.sqrt(2.0)) < 1e-10);

        // Single Geometry implementation
        clippedPolyline = (Polyline) clipOp.execute(polyline, envelope,
                spatialRef, null);
        length = clippedPolyline.calculateLength2D();
        assertTrue(Math.abs(length - 10 * Math.sqrt(2.0)) < 1e-10);

        // Cursor implementation
        GeometryCursor clipMulti_pointCurs = clipOp.execute(multipointCurs,
                envelope, spatialRef, null);
        MultiPoint clipped_multi_point = (MultiPoint) clipMulti_pointCurs
                .next();
        int pointCount = clipped_multi_point.getPointCount();
        assertTrue(pointCount == 2);

        // Cursor implementation
        GeometryCursor clipPointCurs = clipOp.execute(pointCurs, envelope,
                spatialRef, null);
        Point clippedPoint = (Point) clipPointCurs.next();
        assertTrue(clippedPoint != null);

        // RandomTest();

        Polyline _poly = new Polyline();
        _poly.startPath(2, 2);
        _poly.lineTo(0, 0);

        Envelope2D _env = new Envelope2D();
        _env.setCoords(2, 1, 5, 3);

        Polyline _clippedPolyline = (Polyline) clipOp.execute(_poly, _env,
                spatialRef, null);
        assertTrue(_clippedPolyline.isEmpty());

        {
            Polygon poly = new Polygon();
            poly.addEnvelope(new Envelope2D(0, 0, 100, 100), false);
            poly.addEnvelope(new Envelope2D(5, 5, 95, 95), true);

            Polygon clippedPoly = (Polygon) clipOp.execute(poly,
                    new Envelope2D(-10, -10, 110, 50), spatialRef, null);
            assertTrue(clippedPoly.getPathCount() == 1);
            assertTrue(clippedPoly.getPointCount() == 8);
        }
    }

    static Polygon makePolygon() {
        Polygon poly = new Polygon();
        poly.startPath(0, 0);
        poly.lineTo(10, 10);
        poly.lineTo(20, 0);

        return poly;
    }

    static Polyline makePolyline() {
        Polyline poly = new Polyline();
        poly.startPath(0, 0);
        poly.lineTo(10, 10);
        poly.lineTo(20, 0);

        return poly;
    }

    @Test
    public void testGetXCorrectCR185697() {
        OperatorFactoryLocal engine = OperatorFactoryLocal.getInstance();
        OperatorClip clipOp = (OperatorClip) engine
                .getOperator(Operator.Type.Clip);

        Polyline polylineCR = makePolylineCR();
        @SuppressWarnings("unused")
        SimpleGeometryCursor polylineCursCR = new SimpleGeometryCursor(
                polylineCR);

        SpatialReference gcsWGS84 = SpatialReference.create(4326);

        Envelope2D envelopeCR = new Envelope2D();
        envelopeCR.xmin = -180;
        envelopeCR.xmax = 180;
        envelopeCR.ymin = -90;
        envelopeCR.ymax = 90;
        // CR
        Polyline clippedPolylineCR = (Polyline) clipOp.execute(polylineCR,
                envelopeCR, gcsWGS84, null);
        Point pointResult = new Point();
        clippedPolylineCR.getPointByVal(0, pointResult);
        assertTrue(pointResult.getX() == -180);
        clippedPolylineCR.getPointByVal(1, pointResult);
        assertTrue(pointResult.getX() == -90);
        clippedPolylineCR.getPointByVal(2, pointResult);
        assertTrue(pointResult.getX() == 0);
        clippedPolylineCR.getPointByVal(3, pointResult);
        assertTrue(pointResult.getX() == 100);
        clippedPolylineCR.getPointByVal(4, pointResult);
        assertTrue(pointResult.getX() == 170);
        clippedPolylineCR.getPointByVal(5, pointResult);
        assertTrue(pointResult.getX() == 180);
    }

    @Test
    public void testArcObjectsFailureCR196492() {
        OperatorFactoryLocal engine = OperatorFactoryLocal.getInstance();
        OperatorClip clipOp = (OperatorClip) engine
                .getOperator(Operator.Type.Clip);

        Polygon polygon = new Polygon();
        polygon.addEnvelope(new Envelope2D(0, 0, 600, 600), false);
        polygon.startPath(30, 300);
        polygon.lineTo(20, 310);
        polygon.lineTo(10, 300);

        SpatialReference gcsWGS84 = SpatialReference.create(4326);

        Envelope2D envelopeCR = new Envelope2D(10, 10, 500, 500);

        Polygon clippedPolygon = (Polygon) clipOp.execute(polygon, envelopeCR,
                gcsWGS84, null);
        assertTrue(clippedPolygon.getPointCount() == 7);
        // ((MultiPathImpl::SPtr)clippedPolygon._GetImpl()).SaveToTextFileDbg("c:\\temp\\test_ArcObjects_failure_CR196492.txt");
    }

    static Polyline makePolylineCR() {
        Polyline polyline = new Polyline();

        polyline.startPath(-200, -90);
        polyline.lineTo(-180, -85);
        polyline.lineTo(-90, -70);
        polyline.lineTo(0, 0);
        polyline.lineTo(100, 25);
        polyline.lineTo(170, 45);
        polyline.lineTo(225, 65);

        return polyline;
    }

    static MultiPoint makeMultiPoint() {
        MultiPoint mpoint = new MultiPoint();

        Point2D pt1 = new Point2D();
        pt1.x = 10;
        pt1.y = 10;

        Point2D pt2 = new Point2D();
        pt2.x = 15;
        pt2.y = 10;

        Point2D pt3 = new Point2D();
        pt3.x = 10;
        pt3.y = 20;

        mpoint.add(pt1.x, pt1.y);
        mpoint.add(pt2.x, pt2.y);
        mpoint.add(pt3.x, pt3.y);

        return mpoint;
    }

    static Point makePoint() {
        Point point = new Point();

        Point2D pt = new Point2D();
        pt.setCoords(10, 20);
        point.setXY(pt);

        return point;
    }


    @Test
    public void testClipOfCoinciding() {
        OperatorFactoryLocal engine = OperatorFactoryLocal.getInstance();
        OperatorClip clipOp = (OperatorClip) engine
                .getOperator(Operator.Type.Clip);

        Polygon polygon = new Polygon();
        Envelope2D envelopeCR = new Envelope2D();
        envelopeCR.xmin = -180;
        envelopeCR.xmax = 180;
        envelopeCR.ymin = -90;
        envelopeCR.ymax = 90;

        polygon.addEnvelope(envelopeCR, false);

        SpatialReference gcsWGS84 = SpatialReference.create(4326);
        // CR
        Polygon clippedPolygon = (Polygon) clipOp.execute(polygon, envelopeCR,
                gcsWGS84, null);
        assertTrue(clippedPolygon.getPathCount() == 1);
        assertTrue(clippedPolygon.getPointCount() == 4);

        OperatorDensifyByLength densifyOp = (OperatorDensifyByLength) engine
                .getOperator(Operator.Type.DensifyByLength);
        polygon.setEmpty();
        polygon.addEnvelope(envelopeCR, false);
        polygon = (Polygon) densifyOp.execute(polygon, 1, null);

        int pc = polygon.getPointCount();
        int pathc = polygon.getPathCount();
        assertTrue(pc == 1080);
        assertTrue(pathc == 1);
        clippedPolygon = (Polygon) clipOp.execute(polygon, envelopeCR,
                gcsWGS84, null);
        int _pathc = clippedPolygon.getPathCount();
        int _pc = clippedPolygon.getPointCount();
        assertTrue(_pathc == 1);
        assertTrue(_pc == pc);
    }

    @Test
    public void testClipAttributes() {
        OperatorFactoryLocal engine = OperatorFactoryLocal.getInstance();
        OperatorClip clipOp = (OperatorClip) engine
                .getOperator(Operator.Type.Clip);
        {
            Polygon polygon = new Polygon();
            polygon.addAttribute(VertexDescription.Semantics.M);

            polygon.startPath(0, 0);
            polygon.lineTo(30, 30);
            polygon.lineTo(60, 0);

            polygon.setAttribute(VertexDescription.Semantics.M, 0, 0, 0);
            polygon.setAttribute(VertexDescription.Semantics.M, 1, 0, 60);
            polygon.setAttribute(VertexDescription.Semantics.M, 2, 0, 120);

            Envelope2D clipper = new Envelope2D();
            clipper.setCoords(10, 0, 50, 20);
            Polygon clippedPolygon = (Polygon) clipOp.execute(polygon, clipper,
                    SpatialReference.create(4326), null);

            assertTrue(clippedPolygon.getAttributeAsDbl(
                    VertexDescription.Semantics.M, 0, 0) == 100);
            assertTrue(clippedPolygon.getAttributeAsDbl(
                    VertexDescription.Semantics.M, 1, 0) == 19.999999999999996); // 20.0
            assertTrue(clippedPolygon.getAttributeAsDbl(
                    VertexDescription.Semantics.M, 2, 0) == 20);
            assertTrue(clippedPolygon.getAttributeAsDbl(
                    VertexDescription.Semantics.M, 3, 0) == 40);
            assertTrue(clippedPolygon.getAttributeAsDbl(
                    VertexDescription.Semantics.M, 4, 0) == 80);
            assertTrue(clippedPolygon.getAttributeAsDbl(
                    VertexDescription.Semantics.M, 5, 0) == 100);
        }

        {
            Polygon polygon = new Polygon();
            polygon.addAttribute(VertexDescription.Semantics.M);

            polygon.startPath(0, 0);
            polygon.lineTo(0, 40);
            polygon.lineTo(20, 40);
            polygon.lineTo(20, 0);

            polygon.setAttribute(VertexDescription.Semantics.M, 0, 0, 0);
            polygon.setAttribute(VertexDescription.Semantics.M, 1, 0, 60);
            polygon.setAttribute(VertexDescription.Semantics.M, 2, 0, 120);
            polygon.setAttribute(VertexDescription.Semantics.M, 3, 0, 180);

            Envelope2D clipper = new Envelope2D();
            clipper.setCoords(0, 10, 20, 20);
            Polygon clippedPolygon = (Polygon) clipOp.execute(polygon, clipper,
                    SpatialReference.create(4326), null);

            assertTrue(clippedPolygon.getAttributeAsDbl(
                    VertexDescription.Semantics.M, 0, 0) == 15);
            assertTrue(clippedPolygon.getAttributeAsDbl(
                    VertexDescription.Semantics.M, 1, 0) == 30);
            assertTrue(clippedPolygon.getAttributeAsDbl(
                    VertexDescription.Semantics.M, 2, 0) == 150);
            assertTrue(clippedPolygon.getAttributeAsDbl(
                    VertexDescription.Semantics.M, 3, 0) == 165);
        }
    }
    @Test
    public void testClipIssue258243() {
        Polygon poly1 = new Polygon();
        poly1.startPath(21.476191371901479, 41.267022001907215);
        poly1.lineTo(59.669186665158051, 36.62700518555863);
        poly1.lineTo(20.498578117352313, 30.363180148246094);
        poly1.lineTo(18.342565836615044, 46.303295352085627);
        poly1.lineTo(17.869569458621626, 23.886816966894159);
        poly1.lineTo(19.835465558090434, 20);
        poly1.lineTo(18.83911285048551, 43.515995498114791);
        poly1.lineTo(20.864485260298004, 20.235921201027757);
        poly1.lineTo(18.976127544787012, 20);
        poly1.lineTo(34.290201277718218, 61.801369014954794);
        poly1.lineTo(20.734727419368866, 20);
        poly1.lineTo(18.545865698148113, 20);
        poly1.lineTo(19.730260558565515, 20);
        poly1.lineTo(19.924806216827005, 23.780315893949187);
        poly1.lineTo(21.675168105421452, 36.699924873001258);
        poly1.lineTo(22.500527828912158, 43.703424859922983);
        poly1.lineTo(42.009527116514818, 36.995486982256089);
        poly1.lineTo(24.469729873835782, 58.365871758247039);
        poly1.lineTo(24.573736036545878, 36.268390409195824);
        poly1.lineTo(22.726502169802746, 20);
        poly1.lineTo(23.925834885228145, 20);
        poly1.lineTo(25.495346880936729, 20);
        poly1.lineTo(23.320941499288317, 20);
        poly1.lineTo(24.05655665646276, 28.659578774758632);
        poly1.lineTo(23.205940789341135, 38.491506888710504);
        poly1.lineTo(21.472847203385509, 53.057228182018044);
        poly1.lineTo(25.04257681654104, 20);
        poly1.lineTo(25.880572351149542, 25.16102863979474);
        poly1.lineTo(26.756283333879658, 20);
        poly1.lineTo(21.476191371901479, 41.267022001907215);
        Envelope2D env = new Envelope2D();
        env.setCoords(24.269517325186033, 19.999998900000001,
                57.305574253225409, 61.801370114954793);

        try {
            Geometry output_geom = OperatorClip.local().execute(poly1, env,
                    SpatialReference.create(4326), null);
            Envelope envPoly = new Envelope();
            poly1.queryEnvelope(envPoly);
            Envelope e = new Envelope(env);
            e.intersect(envPoly);
            Envelope clippedEnv = new Envelope();
            output_geom.queryEnvelope(clippedEnv);
            assertTrue(Math.abs(clippedEnv.getXMin() - e.getXMin()) < 1e-10 &&
                    Math.abs(clippedEnv.getYMin() - e.getYMin()) < 1e-10 &&
                    Math.abs(clippedEnv.getXMax() - e.getXMax()) < 1e-10 &&
                    Math.abs(clippedEnv.getYMax() - e.getYMax()) < 1e-10);
        } catch (Exception e) {
            assertTrue(false);
        }

    }

}
