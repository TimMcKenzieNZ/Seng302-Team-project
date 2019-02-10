package seng302.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;

public class UserAttributeCollectionDeserializer extends
    StdDeserializer<UserAttributeCollection> {


  /**
   * Default constructor for the UserAttributeCollectionDeserializer class.
   */
  public UserAttributeCollectionDeserializer() {

    this(null);

  }


  /**
   * Constructor for UserAttributeCollectionDeserializer with a wildcard class as a parameter which
   * is passed to StdDeserializer.
   *
   * @param valueClass A wildcard class to be deserialized.
   */
  public UserAttributeCollectionDeserializer(Class<?> valueClass) {

    super(valueClass);

  }


  /**
   * @param jsonParser A the json parser to be used
   * @param context The context for which to deserialize
   * @return A new instance of a UserAttributeCollection
   * @throws IOException Signals if there is an error with the input
   * @throws JsonProcessingException If there was an error processing the input
   */
  @Override
  public UserAttributeCollection deserialize(JsonParser jsonParser, DeserializationContext context)
      throws IOException, JsonProcessingException {

    JsonNode node = jsonParser.getCodec().readTree(jsonParser);

    double height = node.get("height").asDouble();
    double weight = node.get("weight").asDouble();
    String bloodType = node.get("bloodType").textValue();
    boolean bodyMassIndexFlag = node.get("bodyMassIndexFlag").asBoolean();
    Boolean smoker = node.get("smoker").asBoolean();
    String bloodPressure = node.get("bloodPressure").textValue();
    double alcoholConsumption = node.get("alcoholConsumption").asDouble();
    String chronicDiseases = node.get("chronicDiseases").textValue();

    return new UserAttributeCollection(height, weight, bloodType, bodyMassIndexFlag, smoker,
        bloodPressure, alcoholConsumption, chronicDiseases);

  }


}
