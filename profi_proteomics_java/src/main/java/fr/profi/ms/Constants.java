/*
 * Created on Mar 9, 2005
 *
 * $Id: Constants.java,v 1.1 2006/11/17 08:00:25 bruley Exp $
 */
package fr.profi.ms;

/**
 * 
 * 
 * @author CB205360
 */
public class Constants {

   /**
    * monoisotopic amino-masses
    */
   protected static final double[] AMINO_MASSES =
      {
         57.02147,
         71.03712,
         87.03203,
         97.05277,
         99.06842,
         101.04768,
         103.00919,
         113.08407,
         114.04293,
         115.02695,
         128.05858,
         128.09497,
         129.042650,
         131.04049,
         137.05891,
         147.06842,
         156.10112,
         163.06333,
         186.07932 };

   /**
    * hydrogen atom mass 
    */
   public static final double H = 1.007825;
   
   /**
    * Convert a MH+ mass into m/z mass format
    * 
    * @param mhMass MH+ mass to convert in m/z format
    * @param charge charge of the spectra to which belong the peak mass
    * @return the mass value in m/z format 
    */
   public static double convertMHToMz(double mhMass, int charge){
	   return (mhMass + (charge-1)*Constants.H) / charge;   // ParentMass in m/z format  
   }

   /**
    * Convert m/z mass format into MH+ mass 
    * 
    * @param mOfZMass m/z format to convert in MH+ mass 
    * @param charge charge of the spectra to which belong the peak mass
    * @return the MH+ mass
    */
   public static double convertMzToMH(double mOfZMass, int charge){
	   return mOfZMass * charge - (charge - 1) * Constants.H;	  
   }
}
