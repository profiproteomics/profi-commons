package fr.profi.chemistry

import com.typesafe.scalalogging.StrictLogging
import org.junit._
import fr.profi.chemistry.model._

class IsoelectricPointComputerTest extends StrictLogging {
  
  @Test
  def computePI() = {
    
    val sequenceAndPIList = Array(
      ("STELLIR",6.22f),
      ("FTAVQALSVIESSK",6.22f),
      ("FAROUGIA",9.99f),
      // IL32_HUMAN
      ("MCFPKVLSDDMKKLKARMVMLLPTSAQGLGAWVSACDTEDTVGHLGPWRDKDPALWCQLC"+
       "LSSQHQAIERFYDKMQNAESGRGQVMSSLAELEDDFKEGYLETVAAYYEEQHPELTPLLE"+
       "KERDGLRCRGNRSPVPDVEDPATEEPGESFCDKVMRWFQAMLQRLQTWWHGVLAWVKEKV"+
       "VALVHAVQALWKQFQSFCCSLSELFMSSFQSYGAPRGDKEELTPQKCSEPQSSK",5.26f)
    )
    
    val piComputer = new fr.profi.chemistry.algo.IsoelectricPointComputer(ProteinogenicAminoAcidTable)
    
    for ((sequence,expectedPI) <- sequenceAndPIList) {
      logger.info("AA sequence = " + sequence)
  
      val pI = piComputer.computePI(sequence)
      logger.info("Computed pI = " + pI)
     
      Assert.assertEquals(expectedPI, pI, 0.01)
    }

  }
  

}