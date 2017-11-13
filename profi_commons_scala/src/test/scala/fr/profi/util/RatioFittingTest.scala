package fr.profi.util

import org.junit.Assert._
import org.junit.Test
import org.scalatest.junit.AssertionsForJUnit

import fr.profi.util.math._

@Test
class RatioFittingTest extends AssertionsForJUnit {
  
       @Test
  def summarizeCBy {
        
    val matrix = Array(
      Array(Float.NaN   ,Float.NaN,3363.449951f,Float.NaN,4048.209473f,8135.1875f,67534.57031f,135369.2188f,371872.2188f,739425.625f ,1185.227295f,Float.NaN,Float.NaN,Float.NaN,5591.858887f,15660.9502f,66991.46875f,171569.125f ,440577f     ,920273.875f ,Float.NaN,Float.NaN   ,4702.259766f,6555.999512f,5565.342285f,20784.26758f,73084.47656f,168318.2344f,452562.4375f,975477f     ,Float.NaN,Float.NaN,3954.012451f,4004.141357f,6415.609375f,15904.85938f,70523.82031f,151799f     ,360053.5938f,760401f),
      Array(1435.074707f,Float.NaN,344.5174866f,Float.NaN,Float.NaN   ,Float.NaN ,883.0036011f,3277.200195f,7209.469727f,15732.92383f,Float.NaN   ,Float.NaN,Float.NaN,Float.NaN,Float.NaN   ,Float.NaN  ,904.1730957f,Float.NaN   ,7112.368164f,13947.44238f,Float.NaN,252.5355225f,Float.NaN   ,Float.NaN   ,Float.NaN   ,946.0653076f,940.7069092f,3670.187012f,7771.291992f,14737.60938f,Float.NaN,Float.NaN,Float.NaN   ,Float.NaN   ,0.815979004f,632.1899414f,Float.NaN   ,2248.929199f,5382.649902f,11419.53906f),
      Array(Float.NaN   ,Float.NaN,Float.NaN   ,Float.NaN,Float.NaN   ,Float.NaN ,5168.999512f,14289.15137f,33031.47266f,58896.28125f,Float.NaN   ,Float.NaN,Float.NaN,Float.NaN,Float.NaN   ,Float.NaN  ,4823.464355f,14161.42578f,35102.55469f,69109.08594f,Float.NaN,Float.NaN   ,Float.NaN   ,Float.NaN   ,Float.NaN   ,Float.NaN   ,6178.142578f,17820.57031f,44293.32031f,83401.98438f,Float.NaN,Float.NaN,Float.NaN   ,Float.NaN   ,Float.NaN   ,Float.NaN   ,5267.399414f,13935.02441f,32856.23828f,65789.28906f),
      Array(Float.NaN   ,Float.NaN,192.802063f ,Float.NaN,Float.NaN   ,Float.NaN ,Float.NaN   ,7267.249023f,21651.70898f,61014.47266f,Float.NaN   ,Float.NaN,Float.NaN,Float.NaN,Float.NaN   ,Float.NaN  ,Float.NaN   ,5434.973633f,29762.81641f,69735.07813f,Float.NaN,2394.83252f ,Float.NaN   ,Float.NaN   ,Float.NaN   ,Float.NaN   ,Float.NaN   ,11155.42773f,29782.76953f,82598.30469f,Float.NaN,Float.NaN,Float.NaN   ,Float.NaN   ,Float.NaN   ,Float.NaN   ,Float.NaN   ,7757.658203f,27527.72461f,59408.21484f)
    )
  
       val abundances = RatioFitting.fitWithCountPredicate(matrix, 3)
       println("RatioFitting Summarizer = "+abundances.mkString(","))
    }
  
}
