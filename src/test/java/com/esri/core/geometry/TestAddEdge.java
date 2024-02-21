package com.esri.core.geometry;

import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertNotNull;


public class TestAddEdge {
    @Test
    public void addEdgex1x2outsideWidth() throws IllegalAccessException {
        SimpleRasterizer simpleRasterizer = new SimpleRasterizer();
        simpleRasterizer.setup(1, 60, null);
        simpleRasterizer.addEdge(2, 0, 3, 1 );
        Field field = null;
        try {
            field = SimpleRasterizer.class.getDeclaredField("ySortedEdges_");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        field.setAccessible(true); // Suppresses Java access checking

        assertNotNull(field.get(simpleRasterizer));
    }
    @Test
    public void addEdgeTooSmall() throws IllegalAccessException {
        SimpleRasterizer simpleRasterizer = new SimpleRasterizer();
        simpleRasterizer.setup(1, 60, null);
        simpleRasterizer.addEdge(-0x8fffff, 0, 3, 1 );
        Field field = null;
        try {
            field = SimpleRasterizer.class.getDeclaredField("ySortedEdges_");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        field.setAccessible(true); // Suppresses Java access checking
        assertNotNull(field.get(simpleRasterizer));
    }
}
