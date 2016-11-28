package cz.mzk.solr.plugin.functions;

import java.util.List;

import org.apache.lucene.queries.function.ValueSource;
import org.apache.solr.search.FunctionQParser;
import org.apache.solr.search.SyntaxError;
import org.apache.solr.search.ValueSourceParser;

public class MarcValueSourceParser extends ValueSourceParser {

	@Override
	public ValueSource parse(FunctionQParser fqp) throws SyntaxError {
		List<ValueSource> sources = fqp.parseValueSourceList();
		return new MarcValueSource(sources.get(0));
	}

}
