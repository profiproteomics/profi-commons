package fr.profi.util.serialization

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility
import com.fasterxml.jackson.annotation._
import com.fasterxml.jackson.core.{JsonGenerator, Version}
import com.fasterxml.jackson.core.json.JsonWriteContext
import com.fasterxml.jackson.databind._
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.{ StdArraySerializers, StdScalarSerializer}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.ScalaObjectMapper
import org.msgpack.jackson.dataformat.MessagePackFactory

trait ObjectMapperContainer {
   def getObjectMapper(): ObjectMapper
}

trait CustomDoubleJacksonSerializer extends ObjectMapperContainer {
  val module = new SimpleModule("ProfiCustomDouble", Version.unknownVersion())
  module.addSerializer(classOf[java.lang.Double], new CustomDoubleSerializer)
  module.addSerializer(classOf[Array[Double]], new CustomArrayOfDoubleSerializer)
  this.getObjectMapper.registerModule(module)
}
  
trait ProfiSerialization[OT] {
  def serialize(value: Any): OT
  def serialize(value: Any, writer: java.io.Writer): Unit
  def deserialize[IT:Manifest](value: OT) : IT 

}

trait ProfiJacksonSerialization[OT] extends ProfiSerialization[OT] with ObjectMapperContainer {
  def getObjectMapper(): ObjectMapper
  
  protected def configureObjectMapper( mapper: ObjectMapper with ScalaObjectMapper ): Unit = {     
    // Configure serialization for visibility of data to be taken into account
    // http://stackoverflow.com/questions/7105745/how-to-specify-jackson-to-only-use-fields-preferably-globally
    // Note: the following settings breaks the compatibility with lazy fields
    // -> the fix consists in using the @JsonProperty annotation on serializable lazy fields
    mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE)
    mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY)
  }
}

/* Inspired from:
 * http://stackoverflow.com/questions/12591457/scala-2-10-json-serialization-and-deserialization
 * http://stackoverflow.com/questions/15887785/jackson-scala-module-serialize-enumeration-fails
 * http://stackoverflow.com/questions/19379967/strange-deserializing-problems-with-generic-types-using-scala-and-jackson-and-ja
 * http://stackoverflow.com/questions/16966743/small-example-of-jackson-scala-module
 * http://stackoverflow.com/questions/17135166/looking-for-a-good-example-of-polymorphic-serialization-deserialization-using-ja
*/
trait ProfiJsonSerialization extends ProfiJacksonSerialization[String] {
  
  // Configure object mapper used for serialization
  private val objectMapper = {
    val objMapper = new ObjectMapper() with ScalaObjectMapper
    this.configureObjectMapper( objMapper )
    objMapper
  }
  
  override protected def configureObjectMapper( mapper: ObjectMapper with ScalaObjectMapper ): Unit = {
    super.configureObjectMapper(mapper)
    
    // Configure the property naming strategy with the Proline convention
    //println("mapper conf called")
    mapper.setPropertyNamingStrategy(
      PropertyNamingStrategies.SNAKE_CASE
    )
    
    // Disable Exceptions for unknown properties
    // http://stackoverflow.com/questions/14343477/how-do-you-globally-set-jackson-to-ignore-unknown-properties-within-spring
    // Note: this is currently not taken into account by jerkson and json4s
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    
    // Configure serialization for null and empty exclusion
    // Note: with the NON_ABSENT filer, empty arrays are still serialized
    mapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
    /*mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
    mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)*/
  }
  
  def getObjectMapper() = objectMapper

  def serialize(value: Any): String = {
    objectMapper.writeValueAsString(value)
  }
  
  def serialize(value: Any, writer: java.io.Writer): Unit = {
    objectMapper.writeValue(writer, value)
  }
  
  //def deserialize[T](value: String )(implicit m: Manifest[T]): T = {
  def deserialize[T: Manifest](value: String ): T = {
    //objectMapper.readValue(value, typeReference[T])
    objectMapper.readValue(value, objectMapper.constructType[T] )
  }
  
}

trait ProfiJSMSerialization extends ProfiJsonSerialization {
  
  private val jsmObjectMapper = super.getObjectMapper()
  jsmObjectMapper.registerModule(DefaultScalaModule)
  
  override def getObjectMapper() = jsmObjectMapper
}

object ProfiJson extends ProfiJSMSerialization

/**
 * Trait for MessagePack serialization using Jackson data binding and Jackson ScalaModule
 */
trait ProfiMsgPackSerialization extends ProfiJacksonSerialization[Array[Byte]] {
  
  private val objectMapper = {
    val objMapper = new ObjectMapper(new MessagePackFactory()) with ScalaObjectMapper
    objMapper.registerModule(DefaultScalaModule)
    this.configureObjectMapper( objMapper )
    
    objMapper
  }
  
  def getObjectMapper() = objectMapper
  
  def serialize(value: Any): Array[Byte] = {
    objectMapper.writeValueAsBytes(value)
  }
  
  def serialize(value: Any, writer: java.io.Writer): Unit = {
    objectMapper.writeValue(writer, value)
  }
  
  def deserialize[T: Manifest](value: Array[Byte] ): T = {
    objectMapper.readValue(value, objectMapper.constructType[T] )
  }
}

object ProfiMsgPack extends ProfiMsgPackSerialization

private[this] object CustomDoubleSerializer {
  
  import java.text.{DecimalFormat, DecimalFormatSymbols}
  private val decimalSymbols = new DecimalFormatSymbols()
  decimalSymbols.setDecimalSeparator('.')
  decimalSymbols.setGroupingSeparator(0)
  
  def newDecimalFormat( template: String ): DecimalFormat = new DecimalFormat(template: String , decimalSymbols)
  
  val df = newDecimalFormat( "#.#######" )
  
  val instance = new CustomDoubleSerializer()
  
}

private[this] class CustomDoubleSerializer() extends StdScalarSerializer[java.lang.Double]( classOf[java.lang.Double] ) {

  /*override def serializeWithType( value: Double, jgen: JsonGenerator, provider: SerializerProvider, typeSer: TypeSerializer ) {
    this.serialize( value, jgen, provider )
  }*/
  
  override def serialize( value: java.lang.Double, jgen: JsonGenerator, provider: SerializerProvider ) {
    
    val outputCtx = jgen.getOutputContext.asInstanceOf[JsonWriteContext]
    outputCtx.writeValue()    
    outputCtx.inArray()
    
    var output = if ( outputCtx.inObject ) ":"
                 else if( outputCtx.inArray && outputCtx.getCurrentIndex > 0 ) ","
                 else  ""
    
    if ( java.lang.Double.isNaN(value) || java.lang.Double.isInfinite(value) ) {
      output += "null"
      //jgen.writeNumber( 0 ); // For lack of a better alternative in JSON
      //return;
    } else {
      output += CustomDoubleSerializer.df.format( value )
    }
    
    jgen.writeRaw( output )
  }

}

private[this] class CustomArrayOfDoubleSerializer() extends StdArraySerializers.DoubleArraySerializer {
  override def serializeContents( values: Array[Double], jgen: JsonGenerator, provider: SerializerProvider ) {
    for ( value <- values ) {
      CustomDoubleSerializer.instance.serialize(value,jgen,provider)
    }
  }
}
 
// Previous version (for jackson <= 2.3.5)
/*private[this] class CustomArrayOfDoubleSerializer() extends ArraySerializerBase[Array[Double]](classOf[Array[Double]], null) {
  
  override def _withValueTypeSerializer( vts: TypeSerializer): ContainerSerializer[_] = {
    this.asInstanceOf[ContainerSerializer[_]]
  }

  override def getContentType(): JavaType = {
    TypeFactory.defaultInstance().uncheckedSimpleType(java.lang.Double.TYPE)
  }
  
  override def getContentSerializer(): JsonSerializer[_] = null
  
  override def isEmpty( value: Array[Double] ): Boolean = {
    (value == null) || (value.length == 0)
  }
  
  override def hasSingleElement( value: Array[Double] ): Boolean = {
    (value.length == 1)
  }

  override def serializeContents( values: Array[Double], jgen: JsonGenerator, provider: SerializerProvider ) {
    for ( value <- values ) {
      CustomDoubleSerializer.instance.serialize(value,jgen,provider)
    }
  }

  override def getSchema( provider: SerializerProvider, typeHint: java.lang.reflect.Type): JsonNode = {
    val node = createSchemaNode("array", true)
    node.put("items", createSchemaNode("number"))
    node
  }
  
}*/

