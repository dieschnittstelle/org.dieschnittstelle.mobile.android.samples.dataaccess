package org.dieschnittstelle.mobile.android.dataaccess.model.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.dieschnittstelle.mobile.android.dataaccess.model.DataItem;

public class JsonIO {

	/**
	 * ObjectMapper is able to read json objects from input streams and write json objects to output streams
	 */
	private static final ObjectMapper MAPPER = new ObjectMapper();

	/**
	 * JsonFactory is able to create json nodes and to provide json generators from output streams
	 */
	private static final JsonFactory JSONFACTORY = new JsonFactory(MAPPER);

	public static JsonNode readJsonNodeFromInputStream(InputStream is)
			throws JsonParseException, JsonMappingException, IOException {

		// read a json node from the input stream
		return MAPPER.readValue(is, JsonNode.class);
	}

	public static void writeJsonNodeToOutputStream(JsonNode node,
			OutputStream os) throws IOException {
		
		// obtain a json generator for the output stream
		JsonGenerator generator = JSONFACTORY.createJsonGenerator(os,
				JsonEncoding.UTF8);

		// write the object to the stream, using the generator
		generator.writeObject(node);
	}

	/**
	 * this is for merely converting a json array node to a list of data items
	 * @param arrayNode
	 * @return
	 */
	public static List<DataItem> createDataItemListFromArrayNode(
			ArrayNode arrayNode) {

		List<DataItem> itemlist = new ArrayList<DataItem>();

		for (int i = 0; i < arrayNode.size(); i++) {
			itemlist.add(createDataItemFromObjectNode((ObjectNode) arrayNode
					.get(i)));
		}

		return itemlist;
	}

	/**
	 * this takes a json object nodes and created a DataItem using its attribute values
	 * @param objectNode
	 * @return
	 */
	public static DataItem createDataItemFromObjectNode(ObjectNode objectNode) {

		return new DataItem(objectNode.get("id").getLongValue(),
				objectNode.get("name").getTextValue(), objectNode.get(
						"description").getTextValue());

	}

	/**
	 * this, reversely, takes a DataItem and creates a json object from it
	 * @param item
	 * @return
	 */
	public static ObjectNode createObjectNodeFromDataItem(DataItem item) {

		// JsonNodeFactory offers creation methods for each type of json node
		ObjectNode objectNode = JsonNodeFactory.instance.objectNode();

		objectNode.put("id", item.getId());
		objectNode.put("name", item.getName());
		objectNode.put("description", item.getDescription());

		return objectNode;

	}

}
