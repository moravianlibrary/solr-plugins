package cz.mzk.solr.plugin.functions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import com.vividsolutions.jts.geom.Polygon;

/**
 * 
 * @author xrosecky
 */
public class BoundingBoxOverlapValueSourceTest {

	private static final double EPSILON = 1E-14;

	private Random random = new Random();

	@Test
	public void parsePolygon() {
		String polygonAsStr = "18.0 53.0 19.0 54.0";
		Polygon polygon = BoundingBoxOverlapValueSource.toPolygon(polygonAsStr);
		Assert.assertNotNull(polygon);
	}

	@Test
	public void getRatio() {
		String box1 = "18.0 53.0 19.0 54.0";
		String box2 = "18.0 53.0 18.5 53.5";
		String box3 = "-75.12 -42.82 -65.85 -35.19";
		String box4 = "-180.0 -90 180 90";
		Assert.assertEquals(1.0,
				BoundingBoxOverlapValueSource.getRatio(box1, box1), EPSILON);
		Assert.assertEquals(1.0,
				BoundingBoxOverlapValueSource.getRatio(box2, box2), EPSILON);
		Assert.assertEquals(0.25,
				BoundingBoxOverlapValueSource.getRatio(box1, box2), EPSILON);
		Assert.assertEquals(0.25,
				BoundingBoxOverlapValueSource.getRatio(box2, box1), EPSILON);
		Assert.assertEquals(0.0010915145447578664,
				BoundingBoxOverlapValueSource.getRatio(box3, box4), EPSILON);
	}

	@Test
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
		return String.format("%s %s %s %s", random.nextDouble(),
				random.nextDouble(), random.nextDouble(), random.nextDouble());
	}

}
