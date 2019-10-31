package fr.profi.ms.unimod

import java.io.InputStream

import javax.xml.bind.{JAXBContext, JAXBException, Unmarshaller}


/**
  * Utility class to unmarshal <code>Unimod</code> <em>root</em> element from an Unimod xml
  * <code>InputStream</code>.
  *
  * @author LMN
  *
  */
class UnimodUnmarshaller {

  /**
    * Unmarshals <code>Unimod</code> <em>root</em> element from an Unimod xml <code>InputStream</code>.
    *
    * @param is
    * InputStream containing valid Unimod xml data (from an Unimod xml file or resource). Must not
    * be <code>null</code> and should contain <code>&lt;umod:unimod&gt;</code> <em>root</em>
    * element.
    * @return The <code>Unimod</code> <em>root</em> element including all associated graph of sub-elements.
    * @throws JAXBException
    * In case of parse or unmarshalling error.
    */
  @throws(classOf[JAXBException])
  def unmarshal (is: InputStream) : Unimod = {

    if (is == null) {
      throw new IllegalArgumentException("InputStream is null")
    }

    /* Must use the package name of the Unimod Class (xml root element) */
    val jaxbContext: JAXBContext = JAXBContext.newInstance("fr.profi.ms.unimod")
    val unMarshaller: Unmarshaller = jaxbContext.createUnmarshaller

    val rootElement: Any = unMarshaller.unmarshal(is)

    if (rootElement.isInstanceOf[Unimod]) {
      return rootElement.asInstanceOf[Unimod]
    }
    else {
      throw new IllegalArgumentException("Unknown root element from given InputStream")
    }

  }
}
