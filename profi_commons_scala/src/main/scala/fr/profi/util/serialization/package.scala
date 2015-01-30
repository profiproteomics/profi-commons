package fr.profi.util

import java.lang.reflect.{Type, ParameterizedType}
import com.fasterxml.jackson.annotation._
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.json.JsonWriteContext
import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind._
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.jsontype.TypeSerializer
import com.fasterxml.jackson.databind.ser.std.ArraySerializerBase
import com.fasterxml.jackson.databind.ser.std.NonTypedScalarSerializerBase
import com.fasterxml.jackson.databind.ser.ContainerSerializer
import com.fasterxml.jackson.databind.`type`.TypeFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.JsonScalaEnumeration
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper


package object serialization {
  
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
    def serialize(value: Any, writer: java.io.Writer)
    def deserialize[IT:Manifest](value: OT) : IT 

  }
  
  /* Inspired from:
   * http://stackoverflow.com/questions/12591457/scala-2-10-json-serialization-and-deserialization
   * http://stackoverflow.com/questions/15887785/jackson-scala-module-serialize-enumeration-fails
   * http://stackoverflow.com/questions/19379967/strange-deserializing-problems-with-generic-types-using-scala-and-jackson-and-ja
   * http://stackoverflow.com/questions/16966743/small-example-of-jackson-scala-module
   * http://stackoverflow.com/questions/17135166/looking-for-a-good-example-of-polymorphic-serialization-deserialization-using-ja
  */
  trait ProfiJsonSerialization extends ProfiSerialization[String] with ObjectMapperContainer {
    
    def getObjectMapper() = new ObjectMapper() with ScalaObjectMapper
    
    // Configure object mapper used for serialization
    private lazy val objectMapper = this._configureObjectMapper( this.getObjectMapper() )
    
    // Register some modules
    /*val module = new SimpleModule("ProlineJSONSerializer")
    module.addSerializer(classOf[Throwable], new ThrowableSerializer)
    module.addSerializer(classOf[ObjectId], new ObjectIdSerializer)
    module.addDeserializer(classOf[ObjectId], new ObjectIdDeserializer)
    objectMapper.registerModule(module)
    */
  
    private def _configureObjectMapper( mapper: ObjectMapper with ScalaObjectMapper ): ObjectMapper with ScalaObjectMapper = {
      // Configure the property naming strategy with the Proline convention
      //println("mapper conf called")
      mapper.setPropertyNamingStrategy(
        PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES
      )
      
      // Disable Exceptions for unknown properties
      // http://stackoverflow.com/questions/14343477/how-do-you-globally-set-jackson-to-ignore-unknown-properties-within-spring
      // Note: this is currently not taken into account by jerkson and json4s
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      
      // Configure serialization null and empty exclusion
      mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
      mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
      
      // Configure serialization visibility
      // http://stackoverflow.com/questions/7105745/how-to-specify-jackson-to-only-use-fields-preferably-globally
      // Note: the following settings breaks the compatibility with lazy fields
      // -> the fix consists in using the @JsonProperty annotation on serializable lazy fields
      mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE)
      mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY)
      
      mapper
    }
  
    def serialize(value: Any): String = {
      objectMapper.writeValueAsString(value)
    }
    
    def serialize(value: Any, writer: java.io.Writer) = {
      objectMapper.writeValue(writer, value)
    }
    
    //def deserialize[T](value: String )(implicit m: Manifest[T]): T = {
    def deserialize[T: Manifest](value: String ): T = {
      //objectMapper.readValue(value, typeReference[T])
      objectMapper.readValue(value, objectMapper.constructType[T] )
    }

    //import scala.reflect.api._
    //import scala.reflect.classTag
    //import scala.reflect.runtime.universe.typeOf
    //import scala.reflect.runtime.universe.typeTag
    
    // Old code snippet used for Jackson Scala Module deserialization (prior 2.4.0 version)
    /*private [this] def typeReference[T: Manifest] = new TypeReference[T] {
      override def getType = typeFromManifest(manifest[T])
    }
    
    private [this] def typeFromManifest(m: Manifest[_]): Type = {
      val runtimeClass = m.runtimeClass
      if (m.typeArguments.isEmpty || runtimeClass.isArray() ) { runtimeClass } // was m.erasure in Scala 2.9
      else new ParameterizedType {
        def getRawType = runtimeClass  // was m.erasure in Scala 2.9
        def getActualTypeArguments = m.typeArguments.map(typeFromManifest).toArray
        def getOwnerType = null
      }
    }*/
    
  }
  
  trait ProfiJSMSerialization extends ProfiJsonSerialization {
    
    private val jsmObjectMapper = super.getObjectMapper()
    jsmObjectMapper.registerModule(DefaultScalaModule)
    
    override def getObjectMapper() = jsmObjectMapper
  }
  
  object ProfiJson extends ProfiJSMSerialization // with ProfiJson4sSerialization
  
  /*import org.json4s._
  import org.json4s.jackson.JsonMethods
  import org.json4s.jackson.JsonMethods._
  import org.json4s.jackson.Serialization
  import org.json4s.jackson.JValueSerializer
    
  // Tests have been done with json4s, works well but jSM is prefered for now
  trait ProfiJson4sSerialization extends JsonMethods with ProfiJsonSerialization {
    
    override def deserialize[T: Manifest ](value: String): T = {
        
      try {
        implicit val formats = DefaultFormats + new OptionJNullSerializer()
        val json = parse(value).camelizeKeys
        json.extract[T]
      } catch {
        case e: Exception => super.deserialize(value)
      }

    }
    
    //note: first lambda is deserializer second lambda is serializer                                                                                     
    class OptionJNullSerializer extends CustomSerializer[Option[_]](format => ({ case JNull => None }, { case None => JNull } )) 

    def serializeType[T <: AnyRef](value: T): String = {
      implicit val formats = DefaultFormats + new OptionJNullSerializer()

      val jvalue = Extraction.decompose(value).snakizeKeys
      val filtered = jvalue.filterField{ case (s, ast) => ast != JNull }
     
      mapper.writeValueAsString(new JObject(filtered))
    }
  }*/
  
  private[this] object CustomDoubleSerializer {
    
    import java.text.{DecimalFormat,DecimalFormatSymbols}
    private val decimalSymbols = new DecimalFormatSymbols()
    decimalSymbols.setDecimalSeparator('.')
    decimalSymbols.setGroupingSeparator('\0')
    
    def newDecimalFormat( template: String ): DecimalFormat = new DecimalFormat(template: String , decimalSymbols)
    
    val df = newDecimalFormat( "#.#######" )
    
    val instance = new CustomDoubleSerializer()
    
  }
  
  private[this] class CustomDoubleSerializer() extends NonTypedScalarSerializerBase[java.lang.Double]( classOf[java.lang.Double] ) {
   
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
  
  private[this] class CustomArrayOfDoubleSerializer() extends ArraySerializerBase[Array[Double]](classOf[Array[Double]], null) {
    
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
    
  }

}