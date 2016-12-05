package cz.mzk.solr.plugin.functions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.StrDocValues;
import org.apache.lucene.queries.function.valuesource.FieldCacheSource;
import org.apache.lucene.queries.function.valuesource.LiteralValueSource;
import org.marc4j.MarcPermissiveStreamReader;
import org.marc4j.MarcReader;
import org.marc4j.MarcXmlWriter;
import org.marc4j.marc.Record;

public class MarcValueSource extends ValueSource {

	private final String field;

	public MarcValueSource(ValueSource data) {
		if (data instanceof FieldCacheSource) {
			this.field = ((FieldCacheSource) data).getField();
		} else if (data instanceof LiteralValueSource) {
			this.field = ((LiteralValueSource) data).getValue();
		} else {
			throw new IllegalArgumentException(data.getClass().getCanonicalName());
		}
	}

	@Override
	public FunctionValues getValues(final Map context,
			final LeafReaderContext reader)
			throws IOException {
		return new StrDocValues(this) {

			@Override
			public String strVal(int docId) {
				try {
					Document document = reader.reader().document(docId);
					String[] values = document.getValues(MarcValueSource.this.field);
					return (values != null && values.length > 0) ? this.asXml(values[0]) : null;
				} catch (IOException ioe) {
					throw new RuntimeException(ioe);
				}
			}

			private String asXml(String marc) {
				ByteArrayInputStream bis = new ByteArrayInputStream(marc.getBytes(StandardCharsets.UTF_8));
				MarcReader reader = new MarcPermissiveStreamReader(bis, true, true, "UTF-8");
				Record record = reader.next();
				OutputStream stream = new ByteArrayOutputStream();
				MarcXmlWriter writer = new MarcXmlWriter(stream, "UTF-8");
				writer.write(record);
				writer.close();
				return stream.toString();
			}

		};
	}

	@Override
	public String description() {
		return "marc";
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MarcValueSource)) {
			return false;
		}
		MarcValueSource other = (MarcValueSource) obj; 
		return Objects.equals(this.field, other.field);
	}

	@Override
	public int hashCode() {
		return this.field.hashCode();
	}

}
