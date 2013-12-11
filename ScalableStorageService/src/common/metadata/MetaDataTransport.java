package common.metadata;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class MetaDataTransport {
	
	public static String marshalMetaData(MetaData metaData) {
		Gson gson = new Gson();
		return gson.toJson(metaData);
	}
	
	public static MetaData unmarshalMetaData(String jsonMetaData) {
		Gson gson = new Gson();
		try {
			return gson.fromJson(jsonMetaData, MetaData.class);
		} catch (JsonSyntaxException exc) {
			exc.printStackTrace();
			// Unable to decode the string into a metadata instance
			return null;
		}
	}

}
