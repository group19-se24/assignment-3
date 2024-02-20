/*
 Copyright 1995-2015 Esri

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

import java.util.HashSet;

class Clipper {
	Envelope2D m_extent;
	EditShape m_shape;
	int m_geometry;
	int m_vertices_on_extent_index;
	AttributeStreamOfInt32 m_vertices_on_extent;

	public static HashSet<Integer> coverageSplitSegments_ = new HashSet<>();
	public static HashSet<Integer> coverageClipPolyLine = new HashSet<>();
	public static HashSet<Integer> coverageClip = new HashSet<>();
	public static HashSet<Integer> coverageFixPaths = new HashSet<>();
	public static HashSet<Integer> coverageClipPolygon2 = new HashSet<>();

	int checkSegmentIntersection_(Envelope2D seg_env, int side,
			double clip_value) {
		switch (side) {
		case 0:
			if (seg_env.xmin < clip_value && seg_env.xmax <= clip_value) {
				return 0; // outside (or on the border)
			} else if (seg_env.xmin >= clip_value) {
				return 1;// inside
			} else
				return -1; // intersects
		case 1:
			if (seg_env.ymin < clip_value && seg_env.ymax <= clip_value) {
				return 0;
			} else if (seg_env.ymin >= clip_value) {
				return 1;
			} else
				return -1;
		case 2:
			if (seg_env.xmin >= clip_value && seg_env.xmax > clip_value) {
				return 0;
			} else if (seg_env.xmax <= clip_value) {
				return 1;
			} else
				return -1;
		case 3:
			if (seg_env.ymin >= clip_value && seg_env.ymax > clip_value) {
				return 0;
			} else if (seg_env.ymax <= clip_value) {
				return 1;
			} else
				return -1;
		}
		assert (false);// cannot be here
		return 0;
	}

	MultiPath clipMultiPath2_(MultiPath multi_path_in, double tolerance,
			double densify_dist) {
		boolean b_is_polygon = multi_path_in.getType() == Geometry.Type.Polygon;
		if (b_is_polygon)
			return clipPolygon2_((Polygon) multi_path_in, tolerance,
					densify_dist);
		else
			return clipPolyline_((Polyline) multi_path_in, tolerance);
	}

	MultiPath clipPolygon2_(Polygon polygon_in, double tolerance,
							double densify_dist) {


		// Branch #0 (Main default branch)
		coverageClipPolygon2.add(0);

		// If extent is degenerate, return 0.
		if (m_extent.getWidth() == 0 || m_extent.getHeight() == 0)
		{
			// Branch #1
			coverageClipPolygon2.add(1);
			return (MultiPath) polygon_in.createInstance();
		} else {
			// Branch #2
			coverageClipPolygon2.add(2);
		}

		Envelope2D orig_env2D = new Envelope2D();
		polygon_in.queryLooseEnvelope(orig_env2D);

		// m_shape = GCNEW Edit_shape();
		m_geometry = m_shape.addGeometry(polygon_in);

		// Forward decl for java port
		Envelope2D seg_env = new Envelope2D();
		Envelope2D sub_seg_env = new Envelope2D();
		Point2D pt_1 = new Point2D();
		Point2D pt_2 = new Point2D();
		double[] result_ordinates = new double[9];
		double[] parameters = new double[9];
		SegmentBuffer sub_segment_buffer = new SegmentBuffer();
		Line line = new Line();
		AttributeStreamOfInt32 delete_candidates = new AttributeStreamOfInt32(0);
		delete_candidates.reserve(Math.min(100, polygon_in.getPointCount()));
		// clip the polygon successively by each plane
		boolean b_all_outside = false;
		for (int iclip_plane = 0; !b_all_outside && iclip_plane < 4; iclip_plane++) {
			// Branch #3
			coverageClipPolygon2.add(3);
			boolean b_intersects_plane = false;
			boolean b_axis_x = (iclip_plane & 1) != 0;
			double clip_value = 0;
			switch (iclip_plane) {
				case 0:
					// Branch #4
					coverageClipPolygon2.add(4);
					clip_value = m_extent.xmin;
					b_intersects_plane = orig_env2D.xmin <= clip_value
							&& orig_env2D.xmax >= clip_value;
					assert (b_intersects_plane || clip_value < orig_env2D.xmin);
					break;
				case 1:
					// Branch #5
					coverageClipPolygon2.add(5);
					clip_value = m_extent.ymin;
					b_intersects_plane = orig_env2D.ymin <= clip_value
							&& orig_env2D.ymax >= clip_value;
					assert (b_intersects_plane || clip_value < orig_env2D.ymin);
					break;
				case 2:
					// Branch #6
					coverageClipPolygon2.add(6);
					clip_value = m_extent.xmax;
					b_intersects_plane = orig_env2D.xmin <= clip_value
							&& orig_env2D.xmax >= clip_value;
					assert (b_intersects_plane || clip_value > orig_env2D.xmax);
					break;
				case 3:
					// Branch #7
					coverageClipPolygon2.add(7);
					clip_value = m_extent.ymax;
					b_intersects_plane = orig_env2D.ymin <= clip_value
							&& orig_env2D.ymax >= clip_value;
					assert (b_intersects_plane || clip_value > orig_env2D.ymax);
					break;
				default:
					// Branch #8
					coverageClipPolygon2.add(8);
			}

			if (!b_intersects_plane) {
				// Branch #9
				coverageClipPolygon2.add(9);
				continue;// Optimize for common case when only few sides of the
				// clipper envelope intersect the geometry.
			} else {
				// Branch #10
				coverageClipPolygon2.add(10);
			}

			b_all_outside = true;
			for (int path = m_shape.getFirstPath(m_geometry); path != -1;) {
				// Branch #11
				coverageClipPolygon2.add(11);
				int inside = -1;
				int firstinside = -1;
				int first = m_shape.getFirstVertex(path);
				int vertex = first;
				do {
					Segment segment = m_shape.getSegment(vertex);
					if (segment == null) {
						// Branch #12
						coverageClipPolygon2.add(12);
						segment = line;
						m_shape.getXY(vertex, pt_1);
						segment.setStartXY(pt_1);
						m_shape.getXY(m_shape.getNextVertex(vertex), pt_2);
						segment.setEndXY(pt_2);
					} else {
						// Branch #13
						coverageClipPolygon2.add(13);
					}
					segment.queryEnvelope2D(seg_env);
					int seg_plane_intersection_status = checkSegmentIntersection_(
							seg_env, iclip_plane, clip_value);
					int split_count = 0;
					int next_vertex = -1;

					if (seg_plane_intersection_status == -1) // intersects plane
					{
						// Branch #14
						coverageClipPolygon2.add(14);
						int count = segment.intersectionWithAxis2D(b_axis_x,
								clip_value, result_ordinates, parameters);
						if (count > 0) {
							// Branch #15
							coverageClipPolygon2.add(15);
							split_count = m_shape.splitSegment(vertex,
									parameters, count);
						} else {
							// Branch #16
							coverageClipPolygon2.add(16);
							assert (count == 0);// might be -1 when the segment
							// is almost parallel to the
							// clip lane. Just to see this
							// happens.
							split_count = 0;
						}

						// add +1 to ensure we check the original segment if no
						// split produced due to degeneracy.
						// Also +1 is necessary to check the last segment of the
						// split
						split_count += 1;// split_count will never be 0 after
						// this if-block.

						int split_vert = vertex;
						int next_split_vert = m_shape.getNextVertex(split_vert);
						for (int i = 0; i < split_count; i++) {
							// Branch #17
							coverageClipPolygon2.add(17);
							m_shape.getXY(split_vert, pt_1);
							m_shape.getXY(next_split_vert, pt_2);

							Segment sub_seg = m_shape.getSegment(split_vert);
							if (sub_seg == null) {
								// Branch #18
								coverageClipPolygon2.add(18);
								sub_seg = line;
								sub_seg.setStartXY(pt_1);
								sub_seg.setEndXY(pt_2);
							} else {
								// Branch #19
								coverageClipPolygon2.add(19);
							}

							sub_seg.queryEnvelope2D(sub_seg_env);
							int sub_segment_plane_intersection_status = checkSegmentIntersection_(
									sub_seg_env, iclip_plane, clip_value);
							if (sub_segment_plane_intersection_status == -1) {
								// Branch #20
								coverageClipPolygon2.add(20);
								// subsegment is intertsecting the plane. We
								// need to snap one of the endpoints to ensure
								// no intersection.
								// TODO: ensure this works for curves. For
								// curves we have to adjust the curve shape.
								if (!b_axis_x) {
									// Branch #22
									coverageClipPolygon2.add(22);
									assert ((pt_1.x < clip_value && pt_2.x > clip_value) || (pt_1.x > clip_value && pt_2.x < clip_value));
									double d_1 = Math.abs(pt_1.x - clip_value);
									double d_2 = Math.abs(pt_2.x - clip_value);
									if (d_1 < d_2) {
										// Branch #23
										coverageClipPolygon2.add(23);
										pt_1.x = clip_value;
										m_shape.setXY(split_vert, pt_1);
									} else {
										// Branch #24
										coverageClipPolygon2.add(24);
										pt_2.x = clip_value;
										m_shape.setXY(next_split_vert, pt_2);
									}
								} else {
									// Branch #25
									coverageClipPolygon2.add(25);
									assert ((pt_1.y < clip_value && pt_2.y > clip_value) || (pt_1.y > clip_value && pt_2.y < clip_value));
									double d_1 = Math.abs(pt_1.y - clip_value);
									double d_2 = Math.abs(pt_2.y - clip_value);
									if (d_1 < d_2) {
										// Branch #26
										coverageClipPolygon2.add(26);
										pt_1.y = clip_value;
										m_shape.setXY(split_vert, pt_1);
									} else {
										// Branch #27
										coverageClipPolygon2.add(27);
										pt_2.y = clip_value;
										m_shape.setXY(next_split_vert, pt_2);
									}
								}

								// after the endpoint has been adjusted, recheck
								// the segment.
								sub_seg = m_shape.getSegment(split_vert);
								if (sub_seg == null) {
									// Branch #28
									coverageClipPolygon2.add(28);
									sub_seg = line;
									sub_seg.setStartXY(pt_1);
									sub_seg.setEndXY(pt_2);
								} else {
									// Branch #29
									coverageClipPolygon2.add(29);
								}
								sub_seg.queryEnvelope2D(sub_seg_env);
								sub_segment_plane_intersection_status = checkSegmentIntersection_(
										sub_seg_env, iclip_plane, clip_value);
							} else {
								// Branch #30
								coverageClipPolygon2.add(30);
							}

							assert (sub_segment_plane_intersection_status != -1);

							int old_inside = inside;
							inside = sub_segment_plane_intersection_status;
							if (firstinside == -1) {
								// Branch #31
								coverageClipPolygon2.add(31);
								firstinside = inside;
							} else {
								// Branch #32
								coverageClipPolygon2.add(32);
							}

							// add connections along the clipping plane line
							if (old_inside == 0 && inside == 1) {
								// Branch #33
								coverageClipPolygon2.add(33);
								// going from outside to inside. Do nothing
							} else if (old_inside == 1 && inside == 0) {
								// Branch #34
								coverageClipPolygon2.add(34);
								// going from inside to outside
							} else if (old_inside == 0 && inside == 0) {
								// Branch #35
								coverageClipPolygon2.add(35);
								// staying outside
								// remember the start point of the outside
								// segment to be deleted.
								delete_candidates.add(split_vert); // is a
								// candidate
								// to be
								// deleted
							} else {
								// Branch #36
								coverageClipPolygon2.add(36);
							}

							if (inside == 1) {
								// Branch #37
								coverageClipPolygon2.add(37);
								b_all_outside = false;
							} else {
								// Branch #38
								coverageClipPolygon2.add(38);
							}

							split_vert = next_split_vert;
							next_vertex = split_vert;
							next_split_vert = m_shape
									.getNextVertex(next_split_vert);
						}
					} else {
						// Branch #39
						coverageClipPolygon2.add(39);
					}

					if (split_count == 0) {
						// Branch #40
						coverageClipPolygon2.add(40);
						assert (seg_plane_intersection_status != -1);// cannot
						// happen.
						int old_inside = inside;
						inside = seg_plane_intersection_status;
						if (firstinside == -1) {
							// Branch #41
							coverageClipPolygon2.add(41);
							firstinside = inside;
						} else {
							// Branch #42
							coverageClipPolygon2.add(42);
						}

						if (old_inside == 0 && inside == 1) {
							// Branch #43
							coverageClipPolygon2.add(43);
							// going from outside to inside.
						} else if (old_inside == 1 && inside == 0) {
							// Branch #44
							coverageClipPolygon2.add(44);
							// going from inside to outside
						} else if (old_inside == 0 && inside == 0) {
							// Branch #45
							coverageClipPolygon2.add(45);
							// remember the start point of the outside segment
							// to be deleted.
							delete_candidates.add(vertex); // is a candidate to
							// be deleted
						}
						else {
							// Branch #46
							coverageClipPolygon2.add(46);
						}

						if (inside == 1) {
							// Branch #47
							coverageClipPolygon2.add(47);
							b_all_outside = false;
						} else {
							// Branch #48
							coverageClipPolygon2.add(48);
						}

						next_vertex = m_shape.getNextVertex(vertex);
					} else {
						// Branch #49
						coverageClipPolygon2.add(49);
					}
					vertex = next_vertex;
				} while (vertex != first);

				if (firstinside == 0 && inside == 0) {// first vertex need to be
					// deleted.
					// Branch #50
					coverageClipPolygon2.add(50);
					delete_candidates.add(first); // is a candidate to be
					// deleted
				} else {
					// Branch #51
					coverageClipPolygon2.add(51);
				}

				for (int i = 0, n = delete_candidates.size(); i < n; i++) {
					// Branch #52
					coverageClipPolygon2.add(52);
					int delete_vert = delete_candidates.get(i);
					m_shape.removeVertex(delete_vert, false);
				}
				delete_candidates.clear(false);
				if (m_shape.getPathSize(path) < 3) {
					// Branch #53
					coverageClipPolygon2.add(53);
					path = m_shape.removePath(path);
				} else {
					// Branch #54
					coverageClipPolygon2.add(54);
					path = m_shape.getNextPath(path);
				}
			}
		}

		if (b_all_outside) {
			// Branch #55
			coverageClipPolygon2.add(55);
			return (MultiPath) polygon_in.createInstance();
		} else {
			// Branch #56
			coverageClipPolygon2.add(56);
		}

		// After the clipping, we could have produced unwanted segment overlaps
		// along the clipping envelope boundary.
		// Detect and resolve that case if possible.
		resolveBoundaryOverlaps_();
		if (densify_dist > 0) {
			// Branch #57
			coverageClipPolygon2.add(57);
			densifyAlongClipExtent_(densify_dist);
		} else {
			// Branch #58
			coverageClipPolygon2.add(58);
		}
		// Branch #59 (Main default branch exit)
		coverageClipPolygon2.add(59);

		return (MultiPath) m_shape.getGeometry(m_geometry);
	}


	MultiPath clipPolyline_(Polyline polyline_in, double tolerance) {
		// Forward decl for java port
		Envelope2D seg_env = new Envelope2D();
		Envelope2D sub_seg_env = new Envelope2D();
		double[] result_ordinates = new double[9];
		double[] parameters = new double[9];
		SegmentBuffer sub_segment_buffer = new SegmentBuffer();
		MultiPath result_poly = polyline_in;
		Envelope2D orig_env2D = new Envelope2D();
		polyline_in.queryLooseEnvelope(orig_env2D);
		for (int iclip_plane = 0; iclip_plane < 4; iclip_plane++) {
			boolean b_intersects_plane = false;
			boolean b_axis_x = (iclip_plane & 1) != 0;
			double clip_value = 0;
			switch (iclip_plane) {
				case 0:
					coverageClipPolyLine.add(1);
					clip_value = m_extent.xmin;
					b_intersects_plane = orig_env2D.xmin <= clip_value
							&& orig_env2D.xmax >= clip_value;
					assert (b_intersects_plane || clip_value < orig_env2D.xmin);
					break;
				case 1:
					coverageClipPolyLine.add(2);
					clip_value = m_extent.ymin;
					b_intersects_plane = orig_env2D.ymin <= clip_value
							&& orig_env2D.ymax >= clip_value;
					assert (b_intersects_plane || clip_value < orig_env2D.ymin);
					break;
				case 2:
					coverageClipPolyLine.add(3);
					clip_value = m_extent.xmax;
					b_intersects_plane = orig_env2D.xmin <= clip_value
							&& orig_env2D.xmax >= clip_value;
					assert (b_intersects_plane || clip_value > orig_env2D.xmax);
					break;
				case 3:
					coverageClipPolyLine.add(4);
					clip_value = m_extent.ymax;
					b_intersects_plane = orig_env2D.ymin <= clip_value
							&& orig_env2D.ymax >= clip_value;
					assert (b_intersects_plane || clip_value > orig_env2D.ymax);
					break;
			}

			if (!b_intersects_plane) {
				coverageClipPolyLine.add(5);
				continue;// Optimize for common case when only few sides of the
				// clipper envelope intersect the geometry.
			}
			MultiPath src_poly = result_poly;
			result_poly = (MultiPath) polyline_in.createInstance();

			MultiPathImpl mp_impl_src = (MultiPathImpl) src_poly._getImpl();
			SegmentIteratorImpl seg_iter = mp_impl_src.querySegmentIterator();
			seg_iter.resetToFirstPath();
			Point2D pt_prev;
			Point2D pt = new Point2D();
			while (seg_iter.nextPath()) {
				coverageClipPolyLine.add(6);
				int inside = -1;
				boolean b_start_new_path = true;
				while (seg_iter.hasNextSegment()) {
					coverageClipPolyLine.add(7);
					Segment segment = seg_iter.nextSegment();
					segment.queryEnvelope2D(seg_env);
					int seg_plane_intersection_status = checkSegmentIntersection_(
							seg_env, iclip_plane, clip_value);
					if (seg_plane_intersection_status == -1) // intersects plane
					{
						coverageClipPolyLine.add(8);
						int count = segment.intersectionWithAxis2D(b_axis_x,
								clip_value, result_ordinates, parameters);
						if (count > 0) {
							coverageClipPolyLine.add(9);
							
							double t0 = 0.0;
							pt_prev = segment.getStartXY();
							for (int i = 0; i <= count; i++) {
								coverageClipPolyLine.add(10);
								
								double t = i < count ? parameters[i] : 1.0;
								if (t0 == t) {
									coverageClipPolyLine.add(11);

									continue;
								}
								segment.cut(t0, t, sub_segment_buffer);
								Segment sub_seg = sub_segment_buffer.get();
								sub_seg.setStartXY(pt_prev);
								if (i < count) {// snap to plane
									coverageClipPolyLine.add(12);
									
									if (b_axis_x) {
										coverageClipPolyLine.add(13);
										
										pt.x = result_ordinates[i];
										pt.y = clip_value;
									} else {
										coverageClipPolyLine.add(14);
										
										pt.x = clip_value;
										pt.y = result_ordinates[i];
									}
									sub_seg.setEndXY(pt);
								}

								sub_seg.queryEnvelope2D(sub_seg_env);
								int sub_segment_plane_intersection_status = checkSegmentIntersection_(
										sub_seg_env, iclip_plane, clip_value);

								if (sub_segment_plane_intersection_status == -1) {
									coverageClipPolyLine.add(15);
									
									// subsegment is intertsecting the plane. We
									// need to snap one of the endpoints to
									// ensure no intersection.
									// TODO: ensure this works for curves. For
									// curves we have to adjust the curve shape.
									Point2D pt_1 = sub_seg.getStartXY();
									Point2D pt_2 = sub_seg.getEndXY();
									if (!b_axis_x) {
										coverageClipPolyLine.add(16);
										
										assert ((pt_1.x < clip_value && pt_2.x > clip_value) || (pt_1.x > clip_value && pt_2.x < clip_value));
										double d_1 = Math.abs(pt_1.x
												- clip_value);
										double d_2 = Math.abs(pt_2.x
												- clip_value);
										if (d_1 < d_2) {
											coverageClipPolyLine.add(17);
											
											pt_1.x = clip_value;
											sub_seg.setStartXY(pt_1);
										} else {
											coverageClipPolyLine.add(18);
											
											pt_2.x = clip_value;
											sub_seg.setEndXY(pt_2);
										}
									} else {
										coverageClipPolyLine.add(19);
										
										assert ((pt_1.y < clip_value && pt_2.y > clip_value) || (pt_1.y > clip_value && pt_2.y < clip_value));
										double d_1 = Math.abs(pt_1.y
												- clip_value);
										double d_2 = Math.abs(pt_2.y
												- clip_value);
										if (d_1 < d_2) {
											coverageClipPolyLine.add(20);
											
											pt_1.y = clip_value;
											sub_seg.setStartXY(pt_1);
										} else {
											coverageClipPolyLine.add(21);
											
											pt_2.y = clip_value;
											sub_seg.setEndXY(pt_2);
										}
									}

									// after the endpoint has been adjusted,
									// recheck the segment.
									sub_seg.queryEnvelope2D(sub_seg_env);
									sub_segment_plane_intersection_status = checkSegmentIntersection_(
											sub_seg_env, iclip_plane,
											clip_value);
								}

								assert (sub_segment_plane_intersection_status != -1);

								pt_prev = sub_seg.getEndXY();
								t0 = t;

								inside = sub_segment_plane_intersection_status;
								if (inside == 1) {
									coverageClipPolyLine.add(22);
									
									result_poly.addSegment(sub_seg,
											b_start_new_path);
									b_start_new_path = false;
								} else {
									coverageClipPolyLine.add(23);

									b_start_new_path = true;
								}
							}
						}
					} else {
						coverageClipPolyLine.add(24);
						
						inside = seg_plane_intersection_status;
						if (inside == 1) {
							coverageClipPolyLine.add(25);
							
							result_poly.addSegment(segment, b_start_new_path);
							b_start_new_path = false;
						} else {
							coverageClipPolyLine.add(26);

							b_start_new_path = true;
						}
					}
				}
			}
		}
		return result_poly;
	}

	void resolveBoundaryOverlaps_() {
		m_vertices_on_extent_index = -1;
		splitSegments_(false, m_extent.xmin);
		splitSegments_(false, m_extent.xmax);
		splitSegments_(true, m_extent.ymin);
		splitSegments_(true, m_extent.ymax);

		m_vertices_on_extent.resize(0);
		m_vertices_on_extent.reserve(100);
		m_vertices_on_extent_index = m_shape.createUserIndex();

		Point2D pt = new Point2D();
		for (int path = m_shape.getFirstPath(m_geometry); path != -1; path = m_shape
				.getNextPath(path)) {
			int vertex = m_shape.getFirstVertex(path);
			for (int ivert = 0, nvert = m_shape.getPathSize(path); ivert < nvert; ivert++, vertex = m_shape
					.getNextVertex(vertex)) {
				m_shape.getXY(vertex, pt);
				if (m_extent.xmin == pt.x || m_extent.xmax == pt.x
						|| m_extent.ymin == pt.y || m_extent.ymax == pt.y) {
					m_shape.setUserIndex(vertex, m_vertices_on_extent_index,
							m_vertices_on_extent.size());
					m_vertices_on_extent.add(vertex);
				}
			}
		}
		// dbg_check_path_first_();
		resolveOverlaps_(false, m_extent.xmin);
		// dbg_check_path_first_();
		resolveOverlaps_(false, m_extent.xmax);
		// dbg_check_path_first_();
		resolveOverlaps_(true, m_extent.ymin);
		// dbg_check_path_first_();
		resolveOverlaps_(true, m_extent.ymax);
		fixPaths_();
	}

	void densifyAlongClipExtent_(double densify_dist) {
		assert (densify_dist > 0);
		Point2D pt_1 = new Point2D();
		Point2D pt_2 = new Point2D();
		double[] split_scalars = new double[2048];
		for (int path = m_shape.getFirstPath(m_geometry); path != -1; path = m_shape
				.getNextPath(path)) {
			int first_vertex = m_shape.getFirstVertex(path);
			int vertex = first_vertex;
			do {
				int next_vertex = m_shape.getNextVertex(vertex);
				m_shape.getXY(vertex, pt_1);
				int b_densify_x = -1;
				if (pt_1.x == m_extent.xmin) {
					m_shape.getXY(next_vertex, pt_2);
					if (pt_2.x == m_extent.xmin) {
						b_densify_x = 1;
					}
				} else if (pt_1.x == m_extent.xmax) {
					m_shape.getXY(next_vertex, pt_2);
					if (pt_2.x == m_extent.xmax) {
						b_densify_x = 1;
					}
				}

				if (pt_1.y == m_extent.ymin) {
					m_shape.getXY(next_vertex, pt_2);
					if (pt_2.y == m_extent.ymin) {
						b_densify_x = 0;
					}
				} else if (pt_1.y == m_extent.ymax) {
					m_shape.getXY(next_vertex, pt_2);
					if (pt_2.y == m_extent.ymax) {
						b_densify_x = 0;
					}
				}

				if (b_densify_x == -1) {
					vertex = next_vertex;
					continue;
				}

				double len = Point2D.distance(pt_1, pt_2);
				int num = (int) Math.min(Math.ceil(len / densify_dist), 2048.0);
				if (num <= 1) {
					vertex = next_vertex;
					continue;
				}

				for (int i = 1; i < num; i++) {
					split_scalars[i - 1] = (1.0 * i) / num;
				}

				int actual_splits = m_shape.splitSegment(vertex, split_scalars,
						num - 1);
				assert (actual_splits == num - 1);
				vertex = next_vertex;
			} while (vertex != first_vertex);
		}
	}
	
	void splitSegments_(boolean b_axis_x, double clip_value) {
		// After the clipping, we could have produced unwanted segment overlaps
		// along the clipping envelope boundary.
		// Detect and resolve that case if possible.
		int usage_index = m_shape.createUserIndex();
		Point2D pt = new Point2D();
		AttributeStreamOfInt32 sorted_vertices = new AttributeStreamOfInt32(0);
		sorted_vertices.reserve(100);
		for (int path = m_shape.getFirstPath(m_geometry); path != -1; path = m_shape
				.getNextPath(path)) {
			coverageSplitSegments_.add(1);
			int vertex = m_shape.getFirstVertex(path);
			for (int ivert = 0, nvert = m_shape.getPathSize(path); ivert < nvert; ivert++) {
				coverageSplitSegments_.add(2);
				int next_vertex = m_shape.getNextVertex(vertex);
				m_shape.getXY(vertex, pt);
				if (b_axis_x ? pt.y == clip_value : pt.x == clip_value) {
					coverageSplitSegments_.add(3);
					m_shape.getXY(next_vertex, pt);
					if (b_axis_x ? pt.y == clip_value : pt.x == clip_value) {
						coverageSplitSegments_.add(4);
						if (m_shape.getUserIndex(vertex, usage_index) != 1) {
							coverageSplitSegments_.add(5);
							sorted_vertices.add(vertex);
							m_shape.setUserIndex(vertex, usage_index, 1);
						}

						if (m_shape.getUserIndex(next_vertex, usage_index) != 1) {
							coverageSplitSegments_.add(6);
							sorted_vertices.add(next_vertex);
							m_shape.setUserIndex(next_vertex, usage_index, 1);
						}
					}
				}
				vertex = next_vertex;
			}
		}

		m_shape.removeUserIndex(usage_index);
		if (sorted_vertices.size() < 3) {
			coverageSplitSegments_.add(7);
			return;
		}

		sorted_vertices.Sort(0, sorted_vertices.size(),
				new ClipperVertexComparer(this));

		Point2D pt_tmp = new Point2D(); // forward declare for java port
										// optimization
		Point2D pt_0 = new Point2D();
		Point2D pt_1 = new Point2D();
		pt_0.setNaN();
		int index_0 = -1;
		AttributeStreamOfInt32 active_intervals = new AttributeStreamOfInt32(0);
		AttributeStreamOfInt32 new_active_intervals = new AttributeStreamOfInt32(
				0);

		int node1 = m_shape.createUserIndex();
		int node2 = m_shape.createUserIndex();
		for (int index = 0, n = sorted_vertices.size(); index < n; index++) {
			coverageSplitSegments_.add(8);
			int vert = sorted_vertices.get(index);
			m_shape.getXY(vert, pt);
			if (!pt.isEqual(pt_0)) {
				coverageSplitSegments_.add(9);
				if (index_0 == -1) {
					coverageSplitSegments_.add(10);
					index_0 = index;
					pt_0.setCoords(pt);
					continue;
				}
		          
				// add new intervals, that started at pt_0
				for (int i = index_0; i < index; i++) {
					coverageSplitSegments_.add(11);
					int v = sorted_vertices.get(i);
					int nextv = m_shape.getNextVertex(v);
					int prevv = m_shape.getPrevVertex(v);
					boolean bAdded = false;
					if (compareVertices_(v, nextv) < 0) {
						coverageSplitSegments_.add(12);
						m_shape.getXY(nextv, pt_tmp);
						if (b_axis_x ? pt_tmp.y == clip_value
								: pt_tmp.x == clip_value) {
							coverageSplitSegments_.add(13);
							active_intervals.add(v);
							bAdded = true;
							m_shape.setUserIndex(v, node2, 1);
						}
					}
					if (compareVertices_(v, prevv) < 0) {
						coverageSplitSegments_.add(14);
						m_shape.getXY(prevv, pt_tmp);
						if (b_axis_x ? pt_tmp.y == clip_value
								: pt_tmp.x == clip_value) {
							coverageSplitSegments_.add(15);
							if (!bAdded) {
								coverageSplitSegments_.add(16);
								active_intervals.add(v);
							}
							m_shape.setUserIndex(v, node1, 1);
						}
					}
				}

				// Split all active intervals at new point
				for (int ia = 0, na = active_intervals.size(); ia < na; ia++) {
					coverageSplitSegments_.add(17);
					int v = active_intervals.get(ia);
					int n_1 = m_shape.getUserIndex(v, node1);
					int n_2 = m_shape.getUserIndex(v, node2);
					if (n_1 == 1) {
						coverageSplitSegments_.add(18);
						int prevv = m_shape.getPrevVertex(v);
						m_shape.getXY(prevv, pt_1);
						double[] t = new double[1];
						t[0] = 0;
						if (!pt_1.isEqual(pt)) {// Split the active segment
							coverageSplitSegments_.add(19);
							double active_segment_length = Point2D
									.distance(pt_0, pt_1);
							t[0] = Point2D.distance(pt_1, pt)
									/ active_segment_length;
							assert (t[0] >= 0 && t[0] <= 1.0);
							if (t[0] == 0) {
								coverageSplitSegments_.add(20);
								t[0] = NumberUtils.doubleEps();// some
								// roundoff
								// issue.
								// split
								// anyway.
								}
							else if (t[0] == 1.0) {
								coverageSplitSegments_.add(21);
								t[0] = 1.0 - NumberUtils.doubleEps();// some
																		// roundoff
																		// issue.
																		// split
																		// anyway.
								assert (t[0] != 1.0);
							}

							int split_count = m_shape.splitSegment(prevv,
									t, 1);
							assert (split_count > 0);
							int v_1 = m_shape.getPrevVertex(v);
							m_shape.setXY(v_1, pt);
							new_active_intervals.add(v_1);
							m_shape.setUserIndex(v_1, node1, 1);
							m_shape.setUserIndex(v_1, node2, -1);
						} else {
							coverageSplitSegments_.add(22);
							// The active segment ends at the current point.
							// We skip it, and it goes away.
						}
					}
					if (n_2 == 1) {
						coverageSplitSegments_.add(23);
						int nextv = m_shape.getNextVertex(v);
						m_shape.getXY(nextv, pt_1);
						double[] t = new double[1];
						t[0] = 0;
						if (!pt_1.isEqual(pt)) {
							coverageSplitSegments_.add(24);
							double active_segment_length = Point2D
									.distance(pt_0, pt_1);
							t[0] = Point2D.distance(pt_0, pt)
									/ active_segment_length;
							assert (t[0] >= 0 && t[0] <= 1.0);
							if (t[0] == 0) {
								coverageSplitSegments_.add(25);
								t[0] = NumberUtils.doubleEps();// some
								// roundoff
								// issue.
								// split
								// anyway.

								}
							else if (t[0] == 1.0) {
								coverageSplitSegments_.add(26);
								t[0] = 1.0 - NumberUtils.doubleEps();// some
																		// roundoff
																		// issue.
																		// split
																		// anyway.
								assert (t[0] != 1.0);
							}

							int split_count = m_shape.splitSegment(v, t, 1);
							assert (split_count > 0);
							int v_1 = m_shape.getNextVertex(v);
							m_shape.setXY(v_1, pt);
							new_active_intervals.add(v_1);
							m_shape.setUserIndex(v_1, node1, -1);
							m_shape.setUserIndex(v_1, node2, 1);
						}
					}
				}

				AttributeStreamOfInt32 tmp = active_intervals;
				active_intervals = new_active_intervals;
				new_active_intervals = tmp;
				new_active_intervals.clear(false);

				index_0 = index;
				pt_0.setCoords(pt);
			}
		}

		m_shape.removeUserIndex(node1);
		m_shape.removeUserIndex(node2);
	}

	void resolveOverlaps_(boolean b_axis_x, double clip_value) {
		// Along the envelope boundary there could be overlapped segments.
		// Example, exterior ring with a hole is cut with a line, that
		// passes through the center of the hole.
		// Detect pairs of opposite overlapping segments and get rid of them
		Point2D pt = new Point2D();
		AttributeStreamOfInt32 sorted_vertices = new AttributeStreamOfInt32(0);
		sorted_vertices.reserve(100);
		int sorted_index = m_shape.createUserIndex();
		// DEBUGPRINTF(L"ee\n");
		for (int ivert = 0, nvert = m_vertices_on_extent.size(); ivert < nvert; ivert++) {
			int vertex = m_vertices_on_extent.get(ivert);
			if (vertex == -1)
				continue;

			int next_vertex = m_shape.getNextVertex(vertex);
			m_shape.getXY(vertex, pt);
			// DEBUGPRINTF(L"%f\t%f\n", pt.x, pt.y);
			if (b_axis_x ? pt.y == clip_value : pt.x == clip_value) {
				m_shape.getXY(next_vertex, pt);
				if (b_axis_x ? pt.y == clip_value : pt.x == clip_value) {
					assert (m_shape.getUserIndex(next_vertex,
							m_vertices_on_extent_index) != -1);
					if (m_shape.getUserIndex(vertex, sorted_index) != -2) {
						sorted_vertices.add(vertex);// remember the vertex. The
													// attached segment belongs
													// to the given clip plane.
						m_shape.setUserIndex(vertex, sorted_index, -2);
					}

					if (m_shape.getUserIndex(next_vertex, sorted_index) != -2) {
						sorted_vertices.add(next_vertex);
						m_shape.setUserIndex(next_vertex, sorted_index, -2);
					}
				}
			}
		}

		if (sorted_vertices.size() == 0) {
			m_shape.removeUserIndex(sorted_index);
			return;
		}

		sorted_vertices.Sort(0, sorted_vertices.size(),
				new ClipperVertexComparer(this));
		// std::sort(sorted_vertices.get_ptr(), sorted_vertices.get_ptr() +
		// sorted_vertices.size(), Clipper_vertex_comparer(this));

		// DEBUGPRINTF(L"**\n");
		for (int index = 0, n = sorted_vertices.size(); index < n; index++) {
			int vert = sorted_vertices.get(index);
			m_shape.setUserIndex(vert, sorted_index, index);
			// Point_2D pt;
			// m_shape.get_xy(vert, pt);
			// DEBUGPRINTF(L"%f\t%f\t%d\n", pt.x, pt.y, vert);
		}

		Point2D pt_tmp = new Point2D();
		Point2D pt_0 = new Point2D();
		pt_0.setNaN();
		int index_0 = -1;
		for (int index = 0, n = sorted_vertices.size(); index < n; index++) {
			int vert = sorted_vertices.get(index);
			if (vert == -1)
				continue;

			m_shape.getXY(vert, pt);
			if (!pt.isEqual(pt_0)) {
				if (index_0 != -1) {
					while (true) {
						boolean b_overlap_resolved = false;
						int index_to = index - index_0 > 1 ? index - 1 : index;
						for (int i = index_0; i < index_to; i++) {
							int v = sorted_vertices.get(i);
							if (v == -1)
								continue;
							int nextv = -1;
							int nv = m_shape.getNextVertex(v);
							if (compareVertices_(v, nv) < 0) {
								m_shape.getXY(nv, pt_tmp);
								if (b_axis_x ? pt_tmp.y == clip_value
										: pt_tmp.x == clip_value)
									nextv = nv;
							}
							int prevv = -1;
							int pv = m_shape.getPrevVertex(v);
							if (compareVertices_(v, pv) < 0) {
								m_shape.getXY(pv, pt_tmp);
								if (b_axis_x ? pt_tmp.y == clip_value
										: pt_tmp.x == clip_value)
									prevv = pv;
							}

							if (nextv != -1 && prevv != -1) {
								// we have a cusp here. remove the vertex.
								beforeRemoveVertex_(v, sorted_vertices,
										sorted_index);
								m_shape.removeVertex(v, false);
								beforeRemoveVertex_(nextv, sorted_vertices,
										sorted_index);
								m_shape.removeVertex(nextv, false);
								b_overlap_resolved = true;
								continue;
							}

							if (nextv == -1 && prevv == -1)
								continue;

							for (int j = i + 1; j < index; j++) {
								int v_1 = sorted_vertices.get(j);
								if (v_1 == -1)
									continue;
								int nv1 = m_shape.getNextVertex(v_1);
								int nextv1 = -1;
								if (compareVertices_(v_1, nv1) < 0) {
									m_shape.getXY(nv1, pt_tmp);
									if (b_axis_x ? pt_tmp.y == clip_value
											: pt_tmp.x == clip_value)
										nextv1 = nv1;
								}

								int pv1 = m_shape.getPrevVertex(v_1);
								int prevv_1 = -1;
								if (compareVertices_(v_1, pv1) < 0) {
									m_shape.getXY(pv1, pt_tmp);
									if (b_axis_x ? pt_tmp.y == clip_value
											: pt_tmp.x == clip_value)
										prevv_1 = pv1;
								}
								if (nextv1 != -1 && prevv_1 != -1) {
									// we have a cusp here. remove the vertex.
									beforeRemoveVertex_(v_1, sorted_vertices,
											sorted_index);
									m_shape.removeVertex(v_1, false);
									beforeRemoveVertex_(nextv1,
											sorted_vertices, sorted_index);
									m_shape.removeVertex(nextv1, false);
									b_overlap_resolved = true;
									break;
								}
								if (nextv != -1 && prevv_1 != -1) {
									removeOverlap_(sorted_vertices, v, nextv,
											v_1, prevv_1, sorted_index);
									b_overlap_resolved = true;
									break;
								} else if (prevv != -1 && nextv1 != -1) {
									removeOverlap_(sorted_vertices, v_1,
											nextv1, v, prevv, sorted_index);
									b_overlap_resolved = true;
									break;
								}
							}

							if (b_overlap_resolved)
								break;
						}

						if (!b_overlap_resolved)
							break;
					}
				}

				index_0 = index;
				pt_0.setCoords(pt);
			}
		}

		m_shape.removeUserIndex(sorted_index);
	}

	void beforeRemoveVertex_(int v_1, AttributeStreamOfInt32 sorted_vertices,
			int sorted_index) {
		int ind = m_shape.getUserIndex(v_1, sorted_index);
		sorted_vertices.set(ind, -1);
		ind = m_shape.getUserIndex(v_1, m_vertices_on_extent_index);
		m_vertices_on_extent.set(ind, -1);
		int path = m_shape.getPathFromVertex(v_1);
		if (path != -1) {
			int first = m_shape.getFirstVertex(path);
			if (first == v_1) {
				m_shape.setFirstVertex_(path, -1);
				m_shape.setLastVertex_(path, -1);
			}
		}
	}

	void removeOverlap_(AttributeStreamOfInt32 sorted_vertices, int v,
			int nextv, int v_1, int prevv_1, int sorted_index) {
		assert (m_shape.isEqualXY(v, v_1));
		assert (m_shape.isEqualXY(nextv, prevv_1));
		assert (m_shape.getNextVertex(v) == nextv);
		assert (m_shape.getNextVertex(prevv_1) == v_1);
		m_shape.setNextVertex_(v, v_1);
		m_shape.setPrevVertex_(v_1, v);
		m_shape.setPrevVertex_(nextv, prevv_1);
		m_shape.setNextVertex_(prevv_1, nextv);

		beforeRemoveVertex_(v_1, sorted_vertices, sorted_index);
		m_shape.removeVertexInternal_(v_1, false);
		beforeRemoveVertex_(prevv_1, sorted_vertices, sorted_index);
		m_shape.removeVertexInternal_(prevv_1, true);
	}

	void fixPaths_() {
		coverageFixPaths.add(0);
		for (int ivert = 0, nvert = m_vertices_on_extent.size(); ivert < nvert; ivert++) {

			coverageFixPaths.add(1);
			int vertex = m_vertices_on_extent.get(ivert);
			if (vertex != -1) {
				coverageFixPaths.add(2);
				m_shape.setPathToVertex_(vertex, -1);
			}
		}

		int path_count = 0;
		int geometry_size = 0;
		for (int path = m_shape.getFirstPath(m_geometry); path != -1;) {
			int first = m_shape.getFirstVertex(path);
			coverageFixPaths.add(3);
			if (first == -1 || path != m_shape.getPathFromVertex(first)) {
				// The path's first vertex has been deleted. Or the path first vertex is now part of another path. We have to delete such path object.
				coverageFixPaths.add(4);
				int p = path;
				path = m_shape.getNextPath(path);
				m_shape.setFirstVertex_(p, -1);
				m_shape.removePathOnly_(p);
				continue;
			}
			assert (path == m_shape.getPathFromVertex(first));
			int vertex = first;
			int path_size = 0;
			do {
				coverageFixPaths.add(5);
				m_shape.setPathToVertex_(vertex, path);
				path_size++;
				vertex = m_shape.getNextVertex(vertex);
			} while (vertex != first);

			if (path_size <= 2) {
				coverageFixPaths.add(6);
				int ind = m_shape.getUserIndex(first,
						m_vertices_on_extent_index);
				m_vertices_on_extent.set(ind, -1);
				int nv = m_shape.removeVertex(first, false);
				if (path_size == 2) {
					coverageFixPaths.add(7);
					ind = m_shape.getUserIndex(nv, m_vertices_on_extent_index);
					m_vertices_on_extent.set(ind, -1);
					m_shape.removeVertex(nv, false);
				}
				int p = path;
				path = m_shape.getNextPath(path);
				m_shape.setFirstVertex_(p, -1);
				m_shape.removePathOnly_(p);
				continue;
			}

			m_shape.setRingAreaValid_(path, false);
			m_shape.setLastVertex_(path, m_shape.getPrevVertex(first));
			m_shape.setPathSize_(path, path_size);
			geometry_size += path_size;
			path_count++;
			path = m_shape.getNextPath(path);
		}

		for (int ivert = 0, nvert = m_vertices_on_extent.size(); ivert < nvert; ivert++) {
			coverageFixPaths.add(8);
			int vertex = m_vertices_on_extent.get(ivert);
			if (vertex == -1) {
				coverageFixPaths.add(9);
				continue;
			}
			int path = m_shape.getPathFromVertex(vertex);
			if (path != -1) {
				coverageFixPaths.add(10);
				continue;
			}

			path = m_shape.insertPath(m_geometry, -1);
			int path_size = 0;
			int first = vertex;
			do {
				coverageFixPaths.add(11);
				m_shape.setPathToVertex_(vertex, path);
				path_size++;
				vertex = m_shape.getNextVertex(vertex);
			} while (vertex != first);

			if (path_size <= 2) {
				coverageFixPaths.add(12);
				int ind = m_shape.getUserIndex(first,
						m_vertices_on_extent_index);
				m_vertices_on_extent.set(ind, -1);
				int nv = m_shape.removeVertex(first, false);
				if (path_size == 2) {
					coverageFixPaths.add(13);
					ind = m_shape.getUserIndex(nv, m_vertices_on_extent_index);
					if (ind >= 0) {
						coverageFixPaths.add(14);
						m_vertices_on_extent.set(ind, -1);
					}
					else {
						coverageFixPaths.add(15);
						// this vertex is not on the extent.
					}
					m_shape.removeVertex(nv, false);
				}

				int p = path;
				path = m_shape.getNextPath(path);
				m_shape.setFirstVertex_(p, -1);
				m_shape.removePathOnly_(p);
				continue;
			}

			m_shape.setClosedPath(path, true);
			m_shape.setPathSize_(path, path_size);
			m_shape.setFirstVertex_(path, first);
			m_shape.setLastVertex_(path, m_shape.getPrevVertex(first));
			m_shape.setRingAreaValid_(path, false);
			geometry_size += path_size;
			path_count++;
		}

		m_shape.setGeometryPathCount_(m_geometry, path_count);
		m_shape.setGeometryVertexCount_(m_geometry, geometry_size);

		int total_point_count = 0;
		for (int geometry = m_shape.getFirstGeometry(); geometry != -1; geometry = m_shape
				.getNextGeometry(geometry)) {
			total_point_count += m_shape.getPointCount(geometry);
		}

		m_shape.setTotalPointCount_(total_point_count);
	}


	static Geometry clipMultiPath_(MultiPath multipath, Envelope2D extent,
			double tolerance, double densify_dist) {
		Clipper clipper = new Clipper(extent);
		return clipper.clipMultiPath2_(multipath, tolerance, densify_dist);
	}

	Clipper(Envelope2D extent) {
		m_extent = extent;
		m_shape = new EditShape();
		m_vertices_on_extent = new AttributeStreamOfInt32(0);
	}

	// static std::shared_ptr<Polygon> create_polygon_from_polyline(const
	// std::shared_ptr<Multi_path>& polyline, const Envelope_2D& env_2D, bool
	// add_envelope, double tolerance, double densify_dist, int
	// corner_is_inside);
	static Geometry clip(Geometry geometry, Envelope2D extent,
						 double tolerance, double densify_dist) {
		if (geometry.isEmpty()) {
			coverageClip.add(0);
			return geometry;
		}
		else{
			coverageClip.add(1);

		}

		if (extent.isEmpty()) {
			coverageClip.add(2) ;
			return geometry.createInstance(); // return an empty geometry
		}
		else{
			coverageClip.add(3) ;

		}

		int geomtype = geometry.getType().value();

		// Test firstly the simplest geometry types point and envelope.
		// After that we'll check the envelope intersection for the optimization
		if (geomtype == Geometry.Type.Point.value()) {
			coverageClip.add(4) ;
			Point2D pt = ((Point) geometry).getXY();
			if (extent.contains(pt)) {
				coverageClip.add(5) ;
				return geometry;
			}
			else {
				coverageClip.add(6) ;
				return geometry.createInstance(); // return an empty geometry
			}
		} else if (geomtype == Geometry.Type.Envelope.value()) {
			coverageClip.add(7) ;
			Envelope2D env = new Envelope2D();
			geometry.queryEnvelope2D(env);
			if (env.intersect(extent)) {
				coverageClip.add(8) ;

				Envelope result_env = new Envelope();
				geometry.copyTo(result_env);
				result_env.setEnvelope2D(env);
				return result_env;
			} else {
				coverageClip.add(9) ;
				return geometry.createInstance(); // return an empty geometry
			}
		}

		// Test the geometry envelope
		Envelope2D env_2D = new Envelope2D();
		geometry.queryLooseEnvelope2D(env_2D);
		if (extent.contains(env_2D)) {
			coverageClip.add(10) ;
			return geometry;// completely inside of bounds
		}
		else{
			coverageClip.add(11) ;
		}
		if (!extent.isIntersecting(env_2D)) {
			coverageClip.add(12) ;
			return geometry.createInstance();// outside of bounds. return empty
			// geometry.
		}
		else{
			coverageClip.add(13) ;
		}

		MultiVertexGeometryImpl impl = (MultiVertexGeometryImpl) geometry
				._getImpl();
		GeometryAccelerators accel = impl._getAccelerators();
		if (accel != null) {
			coverageClip.add(14) ;
			RasterizedGeometry2D rgeom = accel.getRasterizedGeometry();
			if (rgeom != null) {
				coverageClip.add(15) ;
				RasterizedGeometry2D.HitType hit = rgeom
						.queryEnvelopeInGeometry(extent);
				if (hit == RasterizedGeometry2D.HitType.Inside) {
					coverageClip.add(16) ;
					if (geomtype != Geometry.Type.Polygon.value()) {
						coverageClip.add(17) ;
						throw GeometryException.GeometryInternalError();
					}
					else{
						coverageClip.add(18) ;
					}

					Polygon poly = new Polygon(geometry.getDescription());
					poly.addEnvelope(extent, false);
					return poly;
				} else if (hit == RasterizedGeometry2D.HitType.Outside) {
					coverageClip.add(19) ;
					return geometry.createInstance();// outside of bounds.
					// return empty
					// geometry.
				}
			}
			else{
				coverageClip.add(20) ;
			}
		}
		else{
			coverageClip.add(21) ;
		}

		switch (geomtype) {

			case Geometry.GeometryType.MultiPoint: {
				coverageClip.add(22) ;
				MultiPoint multi_point = (MultiPoint) geometry;
				MultiPoint multi_point_out = null;
				int npoints = multi_point.getPointCount();
				AttributeStreamOfDbl xy = (AttributeStreamOfDbl) ((MultiPointImpl) multi_point
						._getImpl())
						.getAttributeStreamRef(VertexDescription.Semantics.POSITION);
				// create the new geometry only if there are points that has been
				// clipped out.
				// If all vertices are inside of the envelope, it returns the input
				// multipoint.
				int ipoints0 = 0;
				for (int ipoints = 0; ipoints < npoints; ipoints++) {
					coverageClip.add(23) ;
					Point2D pt = new Point2D();
					xy.read(2 * ipoints, pt);

					if (!extent.contains(pt)) {// vertex is outside of the envelope
						coverageClip.add(24) ;
						if (ipoints0 == 0) {
							coverageClip.add(25) ;
							multi_point_out = (MultiPoint) multi_point
									.createInstance();
						}
						else{
							coverageClip.add(26) ;
						}

						if (ipoints0 < ipoints) {
							coverageClip.add(27) ;
							multi_point_out.add(multi_point, ipoints0, ipoints);
						}
						else{
							coverageClip.add(28) ;
						}

						ipoints0 = ipoints + 1;// ipoints0 contains index of vertex
						// right after the last clipped out
						// vertex.
					}
					else{
						coverageClip.add(29) ;
					}
				}

				// add the rest of the batch to the result multipoint (only if
				// something has been already clipped out)
				if (ipoints0 > 0) {
					coverageClip.add(30) ;
					multi_point_out.add(multi_point, ipoints0, npoints);
				}
				else{
					coverageClip.add(31) ;

				}

				if (ipoints0 == 0) {
					coverageClip.add(32) ;
					return multi_point;// everything is inside, so return the input
					// geometry
				}
				else {
					coverageClip.add(33) ;
					return multi_point_out;// clipping has happend, return the
					// clipped geometry
				}
			}
			case Geometry.GeometryType.Polygon:
				coverageClip.add(34) ;
			case Geometry.GeometryType.Polyline:
				coverageClip.add(35) ;
				return clipMultiPath_((MultiPath) geometry, extent, tolerance,
						densify_dist);
			default:
				coverageClip.add(36) ;
				assert (false);
				throw GeometryException.GeometryInternalError();
		}
	}


	int compareVertices_(int v_1, int v_2) {
		Point2D pt_1 = new Point2D();
		m_shape.getXY(v_1, pt_1);
		Point2D pt_2 = new Point2D();
		m_shape.getXY(v_2, pt_2);
		int res = pt_1.compare(pt_2);
		return res;
	}

	static final class ClipperVertexComparer extends
			AttributeStreamOfInt32.IntComparator {
		Clipper m_clipper;

		ClipperVertexComparer(Clipper clipper) {
			m_clipper = clipper;
		}

		@Override
		public int compare(int v1, int v2) {
			return m_clipper.compareVertices_(v1, v2);
		}

	}
}
