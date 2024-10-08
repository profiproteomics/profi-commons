package fr.profi.chemistry.model

import scala.collection.generic.ImmutableMapFactory
import scala.collection.mutable.HashMap
import scala.collection.mutable.LongMap
import scala.collection.mutable.MapBuilder

/**
 * @author David Bouyssie
 *
 */
trait IMolecularTable[M <: IMolecularEntity] {

  protected def buildMap(entities: Seq[M]): HashMap[String, M] = {
    val hmBuilder = HashMap.newBuilder[String, M]
    for (entity <- entities) hmBuilder += (entity.symbol -> entity)
    hmBuilder.result
  }
  
  def molecularEntities: List[M]
  def molecularEntityMap: HashMap[String, M]

  // TODO: move thse methods to CustomMolecularTable ?
  def hasMolecurlarEntity(symbol: String): Boolean = molecularEntityMap.contains(symbol)
  def getMolecurlarEntity(symbol: String): M = molecularEntityMap.apply(symbol)
  def getMolecurlarEntityOpt(symbol: String): Option[M] = molecularEntityMap.get(symbol)
  
  def toCustomTable(): CustomMolecularTable = new CustomMolecularTable(molecularEntities)

}

class CustomMolecularTable(val molecularEntities: List[IMolecularEntity]) extends IMolecularTable[IMolecularEntity] {
  
  def this(molecularTable: IMolecularTable[IMolecularEntity]) = {
    this(molecularTable.molecularEntities)
  }
  
  val molecularEntityMap: HashMap[String, IMolecularEntity] = this.buildMap(molecularEntities)
  
  def addMolecurlarEntity(symbol: String, entity: IMolecularEntity): Option[IMolecularEntity] = molecularEntityMap.put(symbol, entity)
}

object AtomTable {
  //def apply( atoms: Seq[Atom] ) = new AtomTable(this.buildMap(atoms))
  def apply(atoms: List[Atom]) = new AtomTable(atoms)
}

//class AtomTable private( protected val atomMap: HashMap[String,Atom] ) extends AtomTableLike
class AtomTable(val atoms: List[Atom]) extends AtomTableLike {
  require(
    atoms.length == atoms.map(_.symbol).distinct.length,
    "atoms contains duplicated entries"
  )
}

trait AtomTableLike extends IMolecularTable[Atom] {
  
  def molecularEntities: List[Atom] = atoms
  def molecularEntityMap: HashMap[String, Atom] = atomMap
  
  def atoms: List[Atom]
  protected lazy val atomMap: HashMap[String, Atom] = this.buildMap(atoms)

  private lazy val atomByProtonNumber = atomMap.map(tuple => tuple._2.protonNumber -> tuple._2).toMap

  def hasAtom(symbol: String): Boolean = atomMap.contains(symbol)
  def getAtom(symbol: String): Atom = atomMap.apply(symbol)
  def getAtomOpt(symbol: String): Option[Atom] = atomMap.get(symbol)

  def getAtomByProtonNumber(protonNumber: Short): Atom = atomByProtonNumber(protonNumber)
  def getAtomOptByByProtonNumber(protonNumber: Short): Option[Atom] = atomByProtonNumber.get(protonNumber)

}

object AminoAcidTable {
  //def apply( aaSeq: Seq[AminoAcidResidue] ) = new AminoAcidTable(this.buildMap(aaSeq))
  def apply(aminoAcids: List[AminoAcidResidue]) = new AminoAcidTable(aminoAcids)
}

//class AminoAcidTable private( protected val aaMap: HashMap[String,AminoAcidResidue] ) extends AminoAcidTableLike
class AminoAcidTable(val aminoAcids: List[AminoAcidResidue]) extends AminoAcidTableLike {
  require(
    aminoAcids.length == aminoAcids.map(_.symbol).distinct.length,
    "aminoAcids contains duplicated entries"
  )
}

trait AminoAcidTableLike extends IMolecularTable[AminoAcidResidue] {
  
  def molecularEntities: List[AminoAcidResidue] = aminoAcids
  def molecularEntityMap: HashMap[String, AminoAcidResidue] = aaMap

  def aminoAcids: List[AminoAcidResidue]
  protected lazy val aaMap: HashMap[String, AminoAcidResidue] = this.buildMap(aminoAcids)
  
  lazy val aaByCode1 = {
    val aaByLong = new LongMap[AminoAcidResidue]
     for (aa <- this.aminoAcids) {
      aaByLong.put(aa.code1.toInt, aa)
    }
    aaByLong
  }

  lazy val averageAAMass = this.aminoAcids.foldLeft(0.0)((s, a) => s + a.monoMass * a.occurrence)

  lazy val occurringAminoAcids = aminoAcids.filter(aa => aa.occurrence > 0)
  /*def this( aminoAcids: Seq[AminoAcidResidue] ) = {
    this {
      val hmBuilder = HashMap.newBuilder[String,AminoAcidResidue]
      for( aa <- aminoAcids) hmBuilder += (aa.symbol -> aa)      
      hmBuilder.result
    }
  }*/

  def hasAminoAcid(symbol: String): Boolean = aaMap.contains(symbol)
  def getAminoAcid(symbol: String): AminoAcidResidue = aaMap.apply(symbol)
  def getAminoAcidOpt(symbol: String): Option[AminoAcidResidue] = aaMap.get(symbol)

  def getAverageAtomComposition(atomTable: AtomTableLike): AtomComposition = {

    val averageAtomComp = new AtomComposition(new HashMap[Atom, Float])
    for (aa <- this.aminoAcids) {
      val aaAtomComp = aa.getAtomComposition(atomTable)
      aaAtomComp *= (aa.occurrence * 100)
      averageAtomComp += aaAtomComp
    }

    averageAtomComp
  }

  def getAbundanceRangeByAtom(atomTable: AtomTableLike): Map[Atom, (Float, Float)] = {

    val tmpAbRangeByAtom = new HashMap[Atom, Tuple2[Float, Float]]
    val atoms = atomTable.atoms

    for (aa <- this.occurringAminoAcids) {
      val abundanceMap = aa.getAtomComposition(atomTable).abundanceMap

      for (atom <- atoms) {
        val ab = abundanceMap.getOrElse(atom, 0f)
        var curRange = tmpAbRangeByAtom.getOrElseUpdate(atom, (Float.MaxValue, Float.MinValue))

        if (ab < curRange._1) curRange = curRange.copy(_1 = ab)
        if (ab > curRange._2) curRange = curRange.copy(_2 = ab)

        tmpAbRangeByAtom(atom) = curRange
      }
    }

    tmpAbRangeByAtom.toMap
  }

}

object BiomoleculeAtomTable extends AtomTableLike {
  
  /*val isotopeTable = Map(
    "H" -> Array( Isotope(1.0078250320710, 0.99988570),
                  Isotope(2.01410177784, 0.00011570),
                  Isotope(3.016049277725, 0.0)
                ),
    
    //"H+" -> Array( Isotope(1.00727646677, 1.0) ),
    //"e-" -> Array( Isotope(0.00054857990943, 1.0) ), 

    "C" -> Array( Isotope(12.0000000, 0.98938),
                  Isotope(13.0033548378, 0.01078),
                  Isotope(14.0032419894, 0.0)
                 ),

    "N" -> Array( Isotope(14.00307400486, 0.9963620),
                  Isotope(15.00010889827, 0.0036420)
                ),

    "O" -> Array( Isotope(15.9949146195616, 0.9975716),
                  Isotope(16.9991317012, 0.000381),
                  Isotope(17.99916107, 0.0020514)
                 ),
    
    "P" -> Array( Isotope(30.9737616320, 1.0000) ),

    "S" -> Array( Isotope(31.9720710015, 0.949926),
                  Isotope(32.9714587615, 0.00752),
                  Isotope(33.9678669012, 0.042524),
                  Isotope(35.9670807620, 0.00011)
                )
  )*/

  // Source : http://packages.python.org/pyteomics/_modules/pyteomics/mass.html
  val atoms = List(
    Atom(symbol = "H", name = "Hydrogen", atomicNumber = 1, isotopes = Array(
      Isotope(1, 1.00782503207, 0.999885f),
      Isotope(2, 2.0141017778, 0.000115f)
    //Isotope(3, 3.016049277725, 0.0f)
    )),
    // TODO: add the proton to the table ???
    /*Atom( symbol = "H+", name = "Proton", atomicNumber = 1, isotopes = Array(
      Isotope(1, 1.007276466812, 1f)
    )),*/
    Atom(symbol = "C", name = "Carbon", atomicNumber = 6, isotopes = Array(
      Isotope(12, 12.0000000, 0.9893f),
      Isotope(13, 13.0033548378, 0.0107f)
    //Isotope(14, 14.003241989, 0.0f)
    )),
    Atom(symbol = "N", name = "Nitrogen", atomicNumber = 7, isotopes = Array(
      Isotope(14, 14.0030740048, 0.99636f),
      Isotope(15, 15.0001088982, 0.00364f)
    )),
    Atom(symbol = "O", name = "Oxygen", atomicNumber = 8, isotopes = Array(
      Isotope(16, 15.99491461956, 0.99757f),
      Isotope(17, 16.99913170, 0.00038f),
      Isotope(18, 17.9991610, 0.00205f)
    )),
    Atom(symbol = "P", name = "Phosphorus", atomicNumber = 15, isotopes = Array(
      Isotope(31, 30.97376163, 1.0000f)
    )),
    Atom(symbol = "S", name = "Sulfur", atomicNumber = 16, isotopes = Array(
      Isotope(32, 31.97207100, 0.9499f),
      Isotope(33, 32.97145876, 0.0075f),
      Isotope(34, 33.96786690, 0.0425f),
      Isotope(35, 35.96708076, 0.0001f)
    )),
    Atom(symbol = "Se", name = "Selenium", atomicNumber = 34, isotopes = Array(
      Isotope(74, 73.92247642, 0.00894f)
    ))
  )

}

// Sources :
// * http://en.wikipedia.org/wiki/Proteinogenic_amino_acid
// * https://proteomicsresource.washington.edu/tools/masses.php
// * http://www.matrixscience.com/help/aa_help.html
object HumanAminoAcidTable extends AminoAcidTableLike {

  val aminoAcids = List(
    AminoAcidResidue(
      code1 = 'A',
      code3 = "Ala",
      name = "Alanine",
      formula = "C(3) H(5) O N",
      monoMass = 71.03711381,
      averageMass = 71.0779,
      occurrence = 0.078f,
      pKa1 = 2.35f,
      pKa2 = 9.87f,
      pI = 6.01f,
      codons = Array("GCA", "GCC", "GCG", "GCU")
    ),
    AminoAcidResidue(
      code1 = 'R',
      code3 = "Arg",
      name = "Arginine",
      formula = "C(6) H(12) O N(4)",
      monoMass = 156.1011111,
      averageMass = 156.18568,
      occurrence = 0.051f,
      pKa1 = 1.82f,
      pKa2 = 8.99f,
      pKa3 = 12.48f,
      pI = 10.76f,
      codons = Array("AGA", "AGG", "CGA", "CGC", "CGG", "CGU")
    ),
    AminoAcidResidue(
      code1 = 'N',
      code3 = "Asn",
      name = "Asparagine",
      formula = "C(4) H(6) O(2) N(2)",
      monoMass = 114.0429275,
      averageMass = 114.10264,
      occurrence = 0.043f,
      pKa1 = 2.14f,
      pKa2 = 8.72f,
      pI = 5.41f,
      codons = Array("AAC", "AAU")
    ),
    AminoAcidResidue(
      code1 = 'D',
      code3 = "Asp",
      name = "Aspartic acid",
      formula = "C(4) H(5) O(3) N",
      monoMass = 115.0269431,
      averageMass = 115.0874,
      occurrence = 0.053f,
      pKa1 = 1.99f,
      pKa2 = 9.9f,
      pKa3 = 3.9f,
      pI = 2.85f,
      codons = Array("GAC", "GAU")
    ),
    AminoAcidResidue(
      code1 = 'C',
      code3 = "Cys",
      name = "Cysteine",
      formula = "C(3) H(5) O N S",
      monoMass = 103.0091845,
      averageMass = 103.1429,
      occurrence = 0.019f,
      pKa1 = 1.92f,
      pKa2 = 10.7f,
      pKa3 = 8.18f,
      pI = 5.05f,
      codons = Array("UGC", "UGU")
    ),
    AminoAcidResidue(
      code1 = 'E',
      code3 = "Glu",
      name = "Glutamic acid",
      formula = "C(5) H(7) O(3) N",
      monoMass = 129.0425931,
      averageMass = 129.11398,
      occurrence = 0.063f,
      pKa1 = 2.1f,
      pKa2 = 9.47f,
      pKa3 = 4.07f,
      pI = 3.15f,
      codons = Array("GAA", "GAG")
    ),
    AminoAcidResidue(
      code1 = 'Q',
      code3 = "Gln",
      name = "Glutamine",
      formula = "C(5) H(8) O(2) N(2)",
      monoMass = 128.0585775,
      averageMass = 128.12922,
      occurrence = 0.042f,
      pKa1 = 2.17f,
      pKa2 = 9.13f,
      pI = 5.65f,
      codons = Array("CAA", "CAG")
    ),
    AminoAcidResidue(
      code1 = 'G',
      code3 = "Gly",
      name = "Glycine",
      formula = "C(2) H(3) O N",
      monoMass = 57.02146374,
      averageMass = 57.05132,
      occurrence = 0.072f,
      pKa1 = 2.35f,
      pKa2 = 9.78f,
      pI = 6.06f,
      codons = Array("GGA", "GGC", "GGG", "GGU")
    ),
    AminoAcidResidue(
      code1 = 'H',
      code3 = "His",
      name = "Histidine",
      formula = "C(6) H(7) O N(3)",
      monoMass = 137.0589119,
      averageMass = 137.13928,
      occurrence = 0.023f,
      pKa1 = 1.8f,
      pKa2 = 9.33f,
      pKa3 = 6.04f,
      pI = 7.6f,
      codons = Array("CAC", "CAU")
    ),
    AminoAcidResidue(
      code1 = 'I',
      code3 = "Ile",
      name = "Isoleucine",
      formula = "C(6) H(11) O N",
      monoMass = 113.084064,
      averageMass = 113.15764,
      occurrence = 0.053f,
      pKa1 = 2.32f,
      pKa2 = 9.76f,
      pI = 6.05f,
      codons = Array("AUA", "AUC", "AUU")
    ),
    AminoAcidResidue(
      code1 = 'L',
      code3 = "Leu",
      name = "Leucine",
      formula = "C(6) H(11) O N",
      monoMass = 113.084064,
      averageMass = 113.15764,
      occurrence = 0.091f,
      pKa1 = 2.33f,
      pKa2 = 9.74f,
      pI = 6.01f,
      codons = Array("CUA", "CUC", "CUG", "CUU", "UUA", "UUG")
    ),
    AminoAcidResidue(
      code1 = 'K',
      code3 = "Lys",
      name = "Lysine",
      formula = "C(6) H(12) O N(2)",
      monoMass = 128.0949631,
      averageMass = 128.17228,
      occurrence = 0.059f,
      pKa1 = 2.16f,
      pKa2 = 9.06f,
      pKa3 = 10.54f,
      pI = 9.6f,
      codons = Array("AAA", "AAG")
    ),
    AminoAcidResidue(
      code1 = 'M',
      code3 = "Met",
      name = "Methionine",
      formula = "C(5) H(9) O N S",
      monoMass = 131.0404846,
      averageMass = 131.19606,
      occurrence = 0.023f,
      pKa1 = 2.13f,
      pKa2 = 9.28f,
      pI = 5.74f,
      codons = Array("AUG")
    ),
    AminoAcidResidue(
      code1 = 'F',
      code3 = "Phe",
      name = "Phenylalanine",
      formula = "C(9) H(9) O N",
      monoMass = 147.0684139,
      averageMass = 147.17386,
      occurrence = 0.039f,
      pKa1 = 2.2f,
      pKa2 = 9.31f,
      pI = 5.49f,
      codons = Array("UUC", "UUU")
    ),
    AminoAcidResidue(
      code1 = 'P',
      code3 = "Pro",
      name = "Proline",
      formula = "C(5) H(7) O N",
      monoMass = 97.05276388,
      averageMass = 97.11518,
      occurrence = 0.052f,
      pKa1 = 1.95f,
      pKa2 = 10.64f,
      pI = 6.3f,
      codons = Array("CCA", "CCC", "CCG", "CCU")
    ),
    AminoAcidResidue(
      code1 = 'U',
      code3 = "Sec",
      name = "Selenocysteine",
      formula = "C(3) H(5) N O Se",
      monoMass = 150.9536353,
      averageMass = 150.0379,
      occurrence = 0f,
      pKa3 = 5.73f,
      pI = 5.47f,
      codons = Array("UGA")
    ),
    AminoAcidResidue(
      code1 = 'S',
      code3 = "Ser",
      name = "Serine",
      formula = "C(3) H(5) O(2) N",
      monoMass = 87.03202844,
      averageMass = 87.0773,
      occurrence = 0.068f,
      pKa1 = 2.19f,
      pKa2 = 9.21f,
      pKa3 = 5.68f,
      pI = 5.68f,
      codons = Array("AGC", "AGU", "UCA", "UCC", "UCG", "UCU")
    ),
    AminoAcidResidue(
      code1 = 'T',
      code3 = "Thr",
      name = "Threonine",
      formula = "C(4) H(7) O(2) N",
      monoMass = 101.0476785,
      averageMass = 101.10388,
      occurrence = 0.059f,
      pKa1 = 2.09f,
      pKa2 = 9.1f,
      pKa3 = 5.53f,
      pI = 5.6f,
      codons = Array("ACA", "ACC", "ACG", "ACU")
    ),
    AminoAcidResidue(
      code1 = 'W',
      code3 = "Trp",
      name = "Tryptophan",
      formula = "C(11) H(10) O N(2)",
      monoMass = 186.079313,
      averageMass = 186.2099,
      occurrence = 0.014f,
      pKa1 = 2.46f,
      pKa2 = 9.41f,
      pKa3 = 5.885f,
      pI = 5.89f,
      codons = Array("UGG")
    ),
    AminoAcidResidue(
      code1 = 'Y',
      code3 = "Tyr",
      name = "Tyrosine",
      formula = "C(9) H(9) O(2) N",
      monoMass = 163.0633286,
      averageMass = 163.17326,
      occurrence = 0.032f,
      pKa1 = 2.2f,
      pKa2 = 9.21f,
      pKa3 = 10.46f,
      pI = 5.64f,
      codons = Array("UAC", "UAU")
    ),
    AminoAcidResidue(
      code1 = 'V',
      code3 = "Val",
      name = "Valine",
      formula = "C(5) H(9) O N",
      monoMass = 99.06841395,
      averageMass = 99.13106,
      occurrence = 0.066f,
      pKa1 = 2.39f,
      pKa2 = 9.74f,
      pI = 6f,
      codons = Array("GUA", "GUC", "GUG", "GUU")
    )
  )
  
  

}

object ProteinogenicAminoAcidTable extends AminoAcidTableLike {
  
  val aminoAcids = HumanAminoAcidTable.aminoAcids ++ List(
    // massCalcObject.setSymbolModification('B', 114.53494)
    AminoAcidResidue(
      code1 = 'B',
      code3 = "Asx",
      name = "Asn or Asp",
      formula = "",
      monoMass = 114.5349353,
      averageMass = 114.59502
    ),
    // massCalcObject.setSymbolModification('J', 113.084064)
    AminoAcidResidue(
      code1 = 'J',
      code3 = "Xle",
      name = "Ile or Leu",
      formula = "C(6) H(11) O N",
      monoMass = 113.084064,
      averageMass = 113.15764
    ),
    // Was: massCalcObject.setSymbolModification('O', 255.158295) (237 + 18 ???)
    AminoAcidResidue(
      code1 = 'O',
      code3 = "Pyl",
      name = "Pyrrolysine",
      formula = "C(12) H(21) O(3) N(3)",
      monoMass = 237.1477266,
      averageMass = 237.298143,
      occurrence = 0f,
      codons = Array("UAG")
    ),
    // massCalcObject.setSymbolModification('X', 111.0)
    AminoAcidResidue(
      code1 = 'X',
      code3 = "Xaa",
      name = "Unknown",
      formula = "",
      monoMass = 111.0,
      averageMass = 111.0
    ),
    // massCalcObject.setSymbolModification('Z', 128.55059)
    AminoAcidResidue(
      code1 = 'Z',
      code3 = "Glx",
      name = "Glu or Gln",
      formula = "",
      monoMass = 128.5505853,
      averageMass = 128.6216
    )
  )
}

