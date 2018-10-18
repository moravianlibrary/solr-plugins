package cz.mzk.solr.plugin.functions;

import java.util.List;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.solr.search.FunctionQParser;
import org.apache.solr.search.SyntaxError;
import org.apache.solr.search.ValueSourceParser;

/**
 * 
 * @author xrosecky
 */
public class BoundingBoxOverlapValueSourceParser extends ValueSourceParser {

	@Override
	public ValueSource parse(FunctionQParser fp) throws SyntaxError {
		List<ValueSource> sources = fp.parseValueSourceList();
		return new BoundingBoxOverlapValueSource(sources.get(0), sources.get(1));
	}

}
