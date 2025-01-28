package fr.profi.mgf;

import fr.profi.ms.model.MSMSSpectrum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.MessageFormat;
import java.util.*;


public class MGFReader implements Iterator<MSMSSpectrum> {

  private static final Logger logger = LoggerFactory.getLogger(MGFReader.class);

  private int currentLineNumber = 0;
	private String currentLine;
  private static BufferedReader bufferedReader;


  public MGFReader(File file) throws IOException {
    this(new FileReader(file));
  }

  public MGFReader(Reader reader) throws IOException {
    bufferedReader = new BufferedReader(reader);
    moveToNextSpectrumLine();
  }

  public void close() throws IOException {
    bufferedReader.close();
  }

  /**
   * Reads the MS/MS spectra contained in the specified file
   *
   * @return the list of MS/MS spectra.
   * @throws InvalidMGFFormatException is thrown any an IO error or a format exception occur.
   */
  public List<MSMSSpectrum> readAllSpectrum() throws InvalidMGFFormatException {

    List<MSMSSpectrum> spectrumList = new ArrayList<>();

    try {
      while(hasNext()) {
        spectrumList.add(readSpectrum());
        moveToNextSpectrumLine();
      }

    } catch (IOException ioe) {
      throw new InvalidMGFFormatException(ioe.getMessage());
    }
    return spectrumList;
  }

  public boolean hasNext() {
    return (currentLine != null) && !currentLine.isEmpty() && currentLine.startsWith(MGFConstants.START_QUERY);
  }

  @Override
  public MSMSSpectrum next() {
    MSMSSpectrum spectrum;
    try {
      spectrum = readSpectrum();
      moveToNextSpectrumLine();
      return spectrum;
    } catch (InvalidMGFFormatException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void moveToNextSpectrumLine() throws IOException {
    currentLine = nextLine();
    while (currentLine != null) {
      if (currentLine.startsWith(MGFConstants.START_QUERY)) {
        break;
      } else if (currentLine.startsWith(MGFConstants.START_COMMENT)) {
        this.notifyMGFComment(this, currentLine.substring(1));
      }
      currentLine = nextLine();
    }
  }

  private String nextLine() throws IOException {
    String line = bufferedReader.readLine();
    if (line != null) {
      line = line.trim();
      currentLineNumber++;
      if (line.isEmpty())
        return nextLine();
    }
    return line;
  }

  /**
   * Read a spectra description from lines, starting at index i. The new
   * spectra will be added to spectrum list. The index of the end of the
   * spectra description will be returned.
   *
   * @return the {@link MSMSSpectrum} read
   * @throws InvalidMGFFormatException if the format does not follow the MGF format
   */
  private MSMSSpectrum readSpectrum() throws InvalidMGFFormatException, IOException {

    double parentMass = -1;
    double parentIntensity = 0;
    int parentCharge = 0;
    double parentRetTime = 0;
    String title = null;
    String scans = null;
    String rawScans = null;
    String charges = null;

    List<double[]> allPeaks = new ArrayList<>();

    currentLine = nextLine();

    // Go through spectra description until END_QUERY found.
    boolean exitWithNoEnd = false;
    while ((currentLine != null) && !currentLine.startsWith(MGFConstants.END_QUERY)) {

      // Tag Values if any
      String value = null;

      // ** Get Tag and Value strings
      String tag = currentLine;
      int separatorIndex = currentLine.indexOf(MGFConstants.VALUE_SEPARATOR);
      if (separatorIndex > 1) {
        tag = currentLine.substring(0, separatorIndex);
        value = currentLine.substring(separatorIndex + 1);
      }

      // Test if current line is a TAG/VALUE line or a peak line
      List<String> l = Arrays.asList(MGFConstants.MSMS_QUERY_TAGS);
      if (l.contains(tag)) {
        // logger.debug("TAG = "+tag);
        // ******************************
        // ******* MSMS ion search TAG line

        // ---- PARENT MASS AND INTENSITY TAG
        if (MGFConstants.PARENT_MASS_I.equals(tag)) {
          double[] values = getMassAndIntensity(value);
          parentMass = values[0];
          parentIntensity = values[1];
        } // END PARENT MASS AND INTENSITY TAG

        // ---- PARENT CHARGE TAG
        if (MGFConstants.PARENT_CHARGE.equals(tag)) {
          if (value != null) {
            parentCharge = getCharge(value);
            charges = value;
          }
        }// END PARENT CHARGE TAG

        // ---- TITLE TAG
        if (MGFConstants.TITLE.equals(tag)) {
          if (value != null) {
            title = value;
          }
        }// END TITLE TAG

        // ---- RET_TIME TAG
        if (MGFConstants.RETENTION_TIME.equals(tag)) {
          parentRetTime = getRetentionTime(value);
        }// END RET_TIME TAG

        // ---- SCANS TAG
        if (MGFConstants.SCANS.equals(tag)) {
          if (value != null) {
            scans = value;
          }
        }// END RET_TIME TAG

        if (MGFConstants.RAWSCANS.equals(tag)) {
          if (value != null) {
            rawScans = value;
          }
        }// END RET_TIME TAG

        // --- OTHERS TAGS are not read !

      } else {
        // ******************************
        // ******* MSMS peaks value line
        double[] peak = getPeakValue(currentLine);
        allPeaks.add(peak);
      }

      // Go to nextLine
      currentLine = nextLine();
      if (currentLine == null) {
        exitWithNoEnd = true;
        break;
      }

    } // END Go through all lines

    // Spectra description was not complete !
    if (exitWithNoEnd) {
      Object[] args = {currentLineNumber};
      String msg = MessageFormat.format("No END ION found for query [line:{0}]", args);
      throw new InvalidMGFFormatException(msg);
    }

    // Mandatory Peptide Mass value was not specified
    if (parentMass == -1) {
      Object[] args = {currentLineNumber};
      String msg = MessageFormat.format("No mass specified for query [line:{0}]", args);
      throw new InvalidMGFFormatException(msg);
    }

    // Create new Spectra
    // logger.debug("Create spectrum ");
    MSMSSpectrum spectra = new MSMSSpectrum(parentMass, parentIntensity, parentCharge, parentRetTime);
    // logger.debug(" add Annotation title "+title);
    if (title != null) {
      spectra.setAnnotation(MGFConstants.TITLE, title);
    }
    if (scans != null)
      spectra.setAnnotation(MGFConstants.SCANS, scans);
    if (rawScans != null)
      spectra.setAnnotation(MGFConstants.RAWSCANS, rawScans);
    if (charges != null)
      spectra.setAnnotation(MGFConstants.ANNOTATION_CHARGE_STATES, charges);
    // logger.debug("Add peaks, nbr = "+allPeaks.size());
    for (double[] aPeak : allPeaks) {
      spectra.addPeak(aPeak[0], aPeak[1]);
    }

    return spectra;
  }

  private double[] getPeakValue(String line) throws InvalidMGFFormatException {
    double[] values = new double[2];
    StringTokenizer tokenizer = new StringTokenizer(line);
    if (tokenizer.countTokens() != 2) {
      Object[] args = {currentLineNumber};
      String msg = MessageFormat.format("Mass and intensity values must be specified for each peak or end of spectrum not detected [line:{0}]", args);
      throw new InvalidMGFFormatException(msg);
    }

    try {
      values[0] = Double.parseDouble(tokenizer.nextToken());
      values[1] = Double.parseDouble(tokenizer.nextToken());
    } catch (NumberFormatException nfe) {
      Object[] args = {currentLineNumber};
      String msg = MessageFormat.format("Mass and intensity values must be specified for each peak or end of spectrum not detected [line:{0}]", args);
      throw new InvalidMGFFormatException(msg);
    }

    return values;
  }

  private double getRetentionTime(String value) {
    double retTime = 0;
    if (value != null) {
      try {
        retTime = Double.parseDouble(value);
      } catch (NumberFormatException nfe) {

        // try a[[-b][,c[-d]]] format
        List<String> values = new ArrayList<>();
        List<Double> result = new ArrayList<>();

        int commaIndex = value.indexOf(",");
        if (commaIndex != -1) {
          values.add(value.substring(0, commaIndex));
          values.add(value.substring(commaIndex + 1));
        } else
          values.add(value);

        for (String nextValue : values) {
          int dashIndex = nextValue.indexOf("-");
          if (dashIndex != -1) { // Split value
            try {
              String firstDbl = nextValue.substring(0, dashIndex);
              double first = Double.parseDouble(firstDbl);
              String secDbl = nextValue.substring(dashIndex + 1);
              double sec = Double.parseDouble(secDbl);

              double avg = (first + sec) / 2.0;
              result.add(avg);
            } catch (NumberFormatException nfe1) {
              // Try with next value !
            }
          } else { // Only one value
            try {
              double dbl = Double.parseDouble(nextValue);
              result.add(dbl);
            } catch (NumberFormatException nfe1) {
              // Try with next value !
            }
          }
        }

        if (!result.isEmpty()) {
          int nbrDbl = 0;
          for (; nbrDbl < result.size(); nbrDbl++) {
            retTime = retTime + result.get(nbrDbl);
          }

          retTime = retTime / nbrDbl;

          MessageFormat numberFormat = new MessageFormat("{0,number,0.0000} ", Locale.ENGLISH);
          Object[] args = {retTime};
          String retTimeStr = numberFormat.format(args);
          retTime = Double.parseDouble(retTimeStr);

        } else {
          String msg = MessageFormat.format("Invalid {0} specified for query [line:{1}]", "retention time", currentLineNumber);
          logger.warn(msg);
        }
      }
    }
    return retTime;
  }

  private double[] getMassAndIntensity(String value) throws InvalidMGFFormatException {
    double[] ret = new double[2];

    if (value == null) {
      String msg = MessageFormat.format("No mass specified for query [line:{0}]", currentLineNumber);
      throw new InvalidMGFFormatException(msg);
    }

    StringTokenizer tokenizer = new StringTokenizer(value);
    String firstVal = null;
    if (tokenizer.hasMoreTokens()) {
      firstVal = tokenizer.nextToken();
    }

    try {
      ret[0] = Double.parseDouble(firstVal);
    } catch (NumberFormatException nfe) {
      String msg = MessageFormat.format("Invalid {0} specified for query [line:{1}]", "mass", currentLineNumber);
      throw new InvalidMGFFormatException(msg);
    }

    if (tokenizer.hasMoreTokens()) {
      String iVal = tokenizer.nextToken();
      try {
        ret[1] = Double.parseDouble(iVal);
      } catch (NumberFormatException nfe) {
        String msg = MessageFormat.format("Invalid {0} specified for query [line:{1}]", "intensity", currentLineNumber);
        logger.warn(msg);
      }
    }

    return ret;
  }

  private int getCharge(String value) {
    int charge = 0;
    if (value == null)
      return charge;

    if (value.contains("+"))
      value = value.substring(0, value.indexOf("+"));
    // VD TODO : Case or 'Charge = 2+ and 3+' !!!!
    try {
      charge = Integer.parseInt(value);
    } catch (NumberFormatException nfe) {
      String msg = MessageFormat.format("Invalid {0} specified for query [line:{1}]", "charge", currentLineNumber);
      logger.warn(msg);
    }
    return charge;
  }

  public void notifySpectrumEnd(Object source, MSMSSpectrum spectrum) {
	}

  public void notifyMGFComment(Object source, String text) {
//    logger.info("{} : {}", currentLineNumber, text);
  }

}
