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

import static com.esri.core.geometry.TestClip.*;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;

public class TestSamK {
        // This static initialization ensures it's only done once.
        // static {
        // BranchCC.visitedBranch = new boolean[36];
        // }

        @Test
        public void testClipEmptyGeometry() {
                // added
                Geometry emptyGeom = new Polygon();
                assertTrue(emptyGeom.isEmpty());

                Geometry result = Clipper.clip(emptyGeom, new Envelope2D(0, 0, 10, 10), 0, 0);
                assertTrue(result.isEmpty());

                // assertTrue(BranchCC.visitedBranch[0]);
        }

        @Test
        public void testClipWithEmptyExtent() {
                // added
                Point pointGeom = new Point(5, 5);
                Envelope2D emptyExtent = new Envelope2D();

                Geometry result = Clipper.clip(pointGeom, emptyExtent, 0, 0);
                assertTrue(result.isEmpty());

                // assertTrue(BranchCC.visitedBranch[2]);
        }

        @Test
        public void testGeometryFullyInsideExtent() {
                // added
                Point pointGeom = new Point(5, 5);
                Envelope2D extent = new Envelope2D(0, 0, 10, 10);

                Geometry result = Clipper.clip(pointGeom, extent, 0, 0);
                assertFalse(result.isEmpty());

                // assertTrue(BranchCC.visitedBranch[10] || BranchCC.visitedBranch[5]);
        }

        @Test
        public void testEnvelopePartiallyIntersectingExtent() {
                // added
                // create an envelope that partially intersects the clipping extent
                Envelope envelopeGeom = new Envelope(5, 5, 15, 15); // This envelope extends beyond the extent
                Envelope2D extent = new Envelope2D(0, 0, 10, 10);

                Geometry result = Clipper.clip(envelopeGeom, extent, 0, 0);
                assertFalse("The clipped geometry should not be empty when the envelope partially intersects the extent",
                                result.isEmpty());

                // assertTrue("Branch 7 (Envelope type check) should be visited",
                // BranchCC.visitedBranch[7]);
                // assertTrue("Branch 8 (Envelope intersects extent) should be visited",
                // BranchCC.visitedBranch[8]);
                // assertFalse("Branch 9 (Envelope does not intersect extent) should not be
                // visited", BranchCC.visitedBranch[9]);
        }

}
