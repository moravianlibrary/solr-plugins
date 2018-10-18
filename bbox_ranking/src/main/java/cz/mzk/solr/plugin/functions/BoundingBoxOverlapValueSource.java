package cz.mzk.solr.plugin.functions;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;

/**
 * 
 * @author xrosecky
 */
public class BoundingBoxOverlapValueSource extends ValueSource {

	protected final ValueSource first;
	protected final ValueSource second;

	public BoundingBoxOverlapValueSource(ValueSource first, ValueSource second) {
		this.first = first;
		this.second = second;
	}

	@Override
	public FunctionValues getValues(Map context,
			LeafReaderContext reader)
			throws IOException {

		final FunctionValues firstVal = first.getValues(context, reader);

		final FunctionValues secondVal = second.getValues(context, reader);

		return new FunctionValues() {

			@Override
			public String toString(int doc) throws IOException {
				String firstAsStr = firstVal.strVal(doc);
				String secondAsStr = secondVal.strVal(doc);
				return firstAsStr + ":" + secondAsStr;
			}

			@Override
			public double doubleVal(int doc) throws IOException {
				String firstAsStr = firstVal.strVal(doc);
				String secondAsStr = secondVal.strVal(doc);
				if (firstAsStr == null || secondAsStr == null) {
					return 0.0;
				}
				double result = BoundingBoxOverlapValueSource.getRatio(
						firstAsStr, secondAsStr);
				return result;
			}

			@Override
			public float floatVal(int doc) throws IOException {
				return (float) doubleVal(doc);
			}

		};
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof BoundingBoxOverlapValueSource) {
			BoundingBoxOverlapValueSource other = (BoundingBoxOverlapValueSource) o;
			return (first.equals(other.first) && second.equals(other.second));
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return first.hashCode() + second.hashCode();
	}

	@Override
	public String description() {
		return "BoundingBoxOverlapValueSource";
	}

	public static double getRatio(String firstAsStr, String secondAsStr) {
		Geometry first = toPolygon(firstAsStr);
		Geometry second = toPolygon(secondAsStr);
		double areaOfFirst = first.getArea();
		double areaOfSecond = second.getArea();
		double commonArea = first.intersection(second).getArea();
		return (commonArea / (areaOfFirst + areaOfSecond - commonArea));
	}

	public static Polygon toPolygon(String str) {
		String[] pointsAsString = str.split(" ");
		float[] pointsAsFloat = new float[pointsAsString.length];
		for (int i = 0; i != pointsAsString.length; i++) {
			pointsAsFloat[i] = Float.parseFloat(pointsAsString[i]);
		}
		GeometryFactory gf = new GeometryFactory();
		List<Coordinate> points = new ArrayList<Coordinate>();
		points.add(new Coordinate(pointsAsFloat[0], pointsAsFloat[1]));
		points.add(new Coordinate(pointsAsFloat[2], pointsAsFloat[1]));
		points.add(new Coordinate(pointsAsFloat[2], pointsAsFloat[3]));
		points.add(new Coordinate(pointsAsFloat[0], pointsAsFloat[3]));
		points.add(new Coordinate(pointsAsFloat[0], pointsAsFloat[1]));
		final Polygon polygon = gf.createPolygon(
				new LinearRing(new CoordinateArraySequence(points
						.toArray(new Coordinate[points.size()])), gf), null);
		return polygon;
	}

}
