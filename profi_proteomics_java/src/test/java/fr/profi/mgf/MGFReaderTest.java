package fr.profi.mgf;

import fr.profi.ms.model.MSMSSpectrum;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Before;
import java.io.File;
import java.util.List;

public class MGFReaderTest {

  private static final Logger log = LoggerFactory.getLogger(MGFReaderTest.class);
  File mgfFile;

  @Before
  public void setUp() {
    String srcFilename = "/Xpl1_002790_QX_small.mgf";
    mgfFile =new File(MGFReaderTest.class.getResource("/Xpl1_002790_QX_small.mgf").getFile());
  }

  @Test
  public void read() throws Exception {
    final MGFReader mgfReader = new MGFReader(mgfFile);
    final long start = System.currentTimeMillis();
    final List<MSMSSpectrum> spectrumList = mgfReader.readAllSpectrum();
    log.debug("Elapsed time : {} ms", System.currentTimeMillis() - start);
    log.debug("Number of spectra read : {}", spectrumList.size());
  }
}