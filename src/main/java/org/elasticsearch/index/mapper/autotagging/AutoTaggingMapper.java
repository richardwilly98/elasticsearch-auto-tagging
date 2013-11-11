package org.elasticsearch.index.mapper.autotagging;

import static org.elasticsearch.index.mapper.MapperBuilders.stringField;
import static org.elasticsearch.index.mapper.core.TypeParsers.parsePathType;

import java.io.IOException;
import java.util.Map;

import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.mapper.ContentPath;
import org.elasticsearch.index.mapper.FieldMapperListener;
import org.elasticsearch.index.mapper.Mapper;
import org.elasticsearch.index.mapper.MapperParsingException;
import org.elasticsearch.index.mapper.MergeContext;
import org.elasticsearch.index.mapper.MergeMappingException;
import org.elasticsearch.index.mapper.ObjectMapperListener;
import org.elasticsearch.index.mapper.ParseContext;

public class AutoTaggingMapper implements Mapper {

	private static ESLogger logger = ESLoggerFactory
			.getLogger(AutoTaggingMapper.class.getName());

	public static final String CONTENT_TYPE = "auto-tagging";
	private final String name;
	private final ContentPath.Type pathType;
	private final Mapper contentMapper;

	public AutoTaggingMapper(String name, ContentPath.Type pathType,
			Mapper contentMapper) {
		this.name = name;
		this.pathType = pathType;
		this.contentMapper = contentMapper;
	}

	@Override
	public XContentBuilder toXContent(XContentBuilder builder, Params params)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void parse(ParseContext context) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void merge(Mapper mergeWith, MergeContext mergeContext)
			throws MergeMappingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void traverse(FieldMapperListener fieldMapperListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void traverse(ObjectMapperListener objectMapperListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	public static class Defaults {
		public static final ContentPath.Type PATH_TYPE = ContentPath.Type.FULL;
	}

	public static class Builder extends
			Mapper.Builder<Builder, AutoTaggingMapper> {

		private ContentPath.Type pathType = Defaults.PATH_TYPE;
		protected Builder builder;
		private Mapper.Builder contentBuilder;

		protected Builder(String name) {
			super(name);
			this.builder = this;
			this.contentBuilder = stringField(name);
		}

		public Builder pathType(ContentPath.Type pathType) {
			this.pathType = pathType;
			return this;
		}

		public String name() {
			return this.name;
		}

		@Override
		public AutoTaggingMapper build(BuilderContext context) {
			ContentPath.Type origPathType = context.path().pathType();
			context.path().pathType(pathType);

			// create the content mapper under the actual name
			Mapper contentMapper = contentBuilder.build(context);

			// create the DC one under the name
			context.path().add(name);
			context.path().remove();

			context.path().pathType(origPathType);
			return new AutoTaggingMapper(name, pathType, contentMapper);
		}
	}

	public static class TypeParser implements Mapper.TypeParser {

		@Override
		public Mapper.Builder parse(String name, Map<String, Object> node,
				ParserContext parserContext) throws MapperParsingException {
			AutoTaggingMapper.Builder builder = new AutoTaggingMapper.Builder(
					name);

			for (Map.Entry<String, Object> entry : node.entrySet()) {
				String fieldName = entry.getKey();
				Object fieldNode = entry.getValue();
				logger.debug("Field {} - {}", fieldName, fieldNode);
				if (fieldName.equals("path")) {
					builder.pathType(parsePathType(name, fieldNode.toString()));
				}
			}
			return builder;
		}

	}
}
