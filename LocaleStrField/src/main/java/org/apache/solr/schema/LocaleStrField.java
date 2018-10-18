package org.apache.solr.schema;

import java.util.Locale;
import java.util.Map;

import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldComparatorSource;
import org.apache.lucene.search.SortField;

public class LocaleStrField extends StrField {

	protected String localeStr;

	protected void init(IndexSchema schema, Map<String, String> args) {
		if (args != null) {
			localeStr = args.get("locale");
			args.remove("locale");
		}
		super.init(schema, args);
	}

	@Override
	public SortField getSortField(SchemaField field, boolean reverse) {
		if (localeStr == null) {
			return super.getSortField(field, reverse);
		} else {
			// need to do any validation?
			int sep = localeStr.indexOf("-");
			String lang = localeStr.substring(0, sep);
			String country = localeStr.substring(sep + 1);
			Locale locale = new Locale(lang, country);
			FieldComparatorSource comparator = new FieldComparatorSource() {

				@Override
				public FieldComparator<?> newComparator(String fieldname,
						int numHits, int sortPos, boolean reversed) {
					return null;
				}

			};
			return new SortField(field.getName(), comparator, reverse);
		}
	}

}
