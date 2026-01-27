package fr.profi.mgf;

public class MGFConstants {
  
  //Spectrum Annotation
  public static String ANNOTATION_SPECTRUM_ID="ID";
  public static String ANNOTATION_COMMENT="COMMENT";
  public static String ANNOTATION_CHARGE_STATES="CHARGE_STATES";
  
  //MGF Format tags
  public static String START_QUERY = "BEGIN IONS";
  public static String END_QUERY = "END IONS";
  public static String PARENT_MASS_I= "PEPMASS";
  public static String PARENT_CHARGE= "CHARGE";
  public static String TITLE= "TITLE";
  public static String RETENTION_TIME = "RTINSECONDS";
  public static String TOLERENCE= "TOL";
  public static String TOLERENCE_UNIT = "TOLU";
  public static String SEQUENCE = "SEQ";
  public static String COMPOSITION= "TOLU";
  public static String TAG= "TAG";
  public static String ERROR_TAG = "ETAG";
  public static String SCANS = "SCANS";
  public static String RAWSCANS = "RAWSCANS";
  
  
  public static String NEW_LINE= "\n";
  public static String VALUE_SEPARATOR= "=";
  public static String TITLE_SEPARATOR= ":";
  public static String START_COMMENT= "#";

  public static String[] MSMS_QUERY_TAGS = {
    START_QUERY,
    END_QUERY,
    PARENT_MASS_I,
    PARENT_CHARGE,
    TITLE,
    RETENTION_TIME,
    TOLERENCE,
    TOLERENCE_UNIT,
    SEQUENCE,
    COMPOSITION,
    TAG,
    ERROR_TAG,
    SCANS,
    RAWSCANS
  };
  
  
}
