package cz.mzk.solr.plugin.functions;

import com.vividsolutions.jts.geom.Geometry;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;

/**
 * 
 * @author xrosecky
 */
public class BoundingBoxOverlapValueSource extends ValueSource {

	private static final Pattern ENVELOPE_PATTERN = Pattern.compile("ENVELOPE\\((.*),(.*),(.*),(.*)\\)");

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
			public String toString(int doc) {
				String firstAsStr = firstVal.strVal(doc);
				String secondAsStr = secondVal.strVal(doc);
				return firstAsStr + ":" + secondAsStr;
			}

			@Override
			public double doubleVal(int doc) {
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
			public float floatVal(int doc) {
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
		Geometry first = toGeometry(firstAsStr);
		Geometry second = toGeometry(secondAsStr);
		double areaOfFirst = first.getArea();
		double areaOfSecond = second.getArea();
		double commonArea = first.intersection(second).getArea();
		return (commonArea / (areaOfFirst + areaOfSecond - commonArea));
	}

	public static Geometry toGeometry(String str) {
		Matcher matcher = ENVELOPE_PATTERN.matcher(str);
		if (matcher.matches()) {
			String p1 = matcher.group(1).trim();
			String p2 = matcher.group(2).trim();
			String p3 = matcher.group(3).trim();
			String p4 = matcher.group(4).trim();
			// ENVELOPE(p1, p2, 51.2, 48.5)
			// "POLYGON((p1 p4, p2 p4, p2 p3, p1 p3, p1 p4))"
			str = String.format("POLYGON((%s %s, %s %s, %s %s, %s %s, %s %s))", p1, p4, p2, p4, p2, p3, p4, p3, p1, p4);
			System.out.println(str);
		}
		WKTReader reader = new WKTReader();
		try {
			return reader.read(str);
		} catch (ParseException pe) {
			throw new IllegalArgumentException(
					String.format("Invalid geometry: %s", str), pe);
		}
	}

}
