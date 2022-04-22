package cz.mzk.solr.plugin.functions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import com.vividsolutions.jts.geom.Geometry;

/**
 * 
 * @author xrosecky
 */
public class BoundingBoxOverlapValueSourceTest {

	private static final double EPSILON = 1E-14;

	private Random random = new Random();

	@Test
	public void parsePolygon() {
		String polygonAsStr = "POLYGON((-6.152 40.547, 16.963 40.547, 16.963 51.847, -6.152 51.847, -6.152 40.547))";
		Geometry polygon = BoundingBoxOverlapValueSource.toGeometry(polygonAsStr);
		Assert.assertNotNull(polygon);
	}

	@Test
	public void parseEnvelope() {
		String polygonAsStr = "ENVELOPE(11.9, 19, 51.2, 48.5)";
		Geometry polygon = BoundingBoxOverlapValueSource.toGeometry(polygonAsStr);
		Assert.assertNotNull(polygon);
	}
	
	@Test
	public void getRatio() {
		String box1 = "ENVELOPE(18.0, 19.0, 53.0, 54.0)";
		String box2 = "ENVELOPE(18.0, 18.5, 53.0, 53.5)";
		String box3 = "ENVELOPE(-75.12, -65.85, -35.19, -42.82)";
		String box4 = "ENVELOPE(-180.0, 180, 90, -90)";
		Assert.assertEquals(1.0,
				BoundingBoxOverlapValueSource.getRatio(box1, box1), EPSILON);
		Assert.assertEquals(1.0,
				BoundingBoxOverlapValueSource.getRatio(box2, box2), EPSILON);
		Assert.assertEquals(0.21449322143318356,
				BoundingBoxOverlapValueSource.getRatio(box1, box2), EPSILON);
		Assert.assertEquals(0.21449322143318356,
				BoundingBoxOverlapValueSource.getRatio(box2, box1), EPSILON);
		Assert.assertEquals(0.0033103703703703724,
				BoundingBoxOverlapValueSource.getRatio(box3, box4), EPSILON);
	}

	//@Test
	public void performanceTest() {
		List<String> boxes = new ArrayList<String>();
		final int NUM_OF_BOXES = 150;
		for (int i = 0; i != NUM_OF_BOXES; i++) {
			boxes.add(getRandomBoundingBox());
		}
		for (String box1 : boxes) {
			for (String box2 : boxes) {
				double ratio = BoundingBoxOverlapValueSource.getRatio(box1,
						box2);
				Assert.assertTrue(ratio <= 1.0);
			}
		}
		Date start = new Date();
		for (String box1 : boxes) {
			for (String box2 : boxes) {
				double ratio = BoundingBoxOverlapValueSource.getRatio(box1,
						box2);
				Assert.assertTrue(ratio <= 1.0);
			}
		}
		Date end = new Date();
		System.out.println("Performance test has taken " + (end.getTime() - start.getTime()) + " ms.");
	}

	private String getRandomBoundingBox() {
		return String.format("ENVELOPE(%s, %s, %s, %s)", random.nextDouble(),
				random.nextDouble(), random.nextDouble(), random.nextDouble());
	}

}
