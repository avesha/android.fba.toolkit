package ru.profi1c.engine.map.mapsforge;

import android.test.ActivityInstrumentationTestCase2;

import com.sample.map.mapsforge.TAMapsforgeRouteMap;


public class TAMapsforgeRouteMapTest extends ActivityInstrumentationTestCase2<TAMapsforgeRouteMap> {

    private TAMapsforgeRouteMap activity;

    public TAMapsforgeRouteMapTest() {
        super(TAMapsforgeRouteMap.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        activity = getActivity();
    }

    public void testPreconditions() {
        assertNotNull("TAMapsforgeRouteMap is null", activity);
    }

    public void testExistMapFile() throws Exception {
        assertTrue(activity.getMapFile().exists());
    }

    public void testMapView() throws Exception {
        assertNotNull(activity.getMapView());
    }

    public void testRoute() {
        final int count = activity.DUMMY_ROUTE_ITEMS_COUNT;
        for(int i = 0; i< count; i++){
            RouteItem<?> item = activity.getRouteOverlayItem(i);
            assertNotNull(item);
            activity.setCenterRouteItem(i);
        }
    }

    public void testNavigate() {
        activity.setCenterRouteItem(0);
        activity.navigateRoute(1);
        assertEquals(1, activity.getCurrentRouteItemIndex());
        activity.navigateRoute(-1);
        assertEquals(0, activity.getCurrentRouteItemIndex());
        activity.navigateRoute(2);
        assertEquals(2, activity.getCurrentRouteItemIndex());
    }
}