package com.jndi.jti.utils;

import static java.lang.String.format;

import java.util.TimeZone;

import org.json.JSONException;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonUtils {

	private static ObjectMapper MAPPER = createNewObjectMapper(true);
	private static ObjectMapper MAPPER_NON_EMPTY = createNewObjectMapper(false);
	public static ObjectMapper getObjectMapper() {
		return MAPPER;
	}
	public static <T> String toJson(final T object, ObjectMapper mapper) {
		try {
			return mapper.writeValueAsString(object);
		} catch (final JsonProcessingException e) {
			throw new JSONException(format("Unable to write JSON object '%s'", object.getClass().getSimpleName()));
		}
	}

	public static ObjectMapper createNewObjectMapper(boolean includeType) {
		final ObjectMapper MAPPER = new ObjectMapper();
		if (includeType) {
			MAPPER.setSerializationInclusion(JsonInclude.Include.ALWAYS);
		} else {
			MAPPER.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
		}
		MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		MAPPER.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);

		MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		MAPPER.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
		return MAPPER;
	}

	public static ObjectMapper getObjectMapperNonEmpty() {
		return MAPPER_NON_EMPTY;
	}
}
