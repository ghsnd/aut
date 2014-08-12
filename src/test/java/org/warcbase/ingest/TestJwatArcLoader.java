package org.warcbase.ingest;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.jwat.arc.ArcReader;
import org.jwat.arc.ArcReaderFactory;
import org.jwat.arc.ArcRecordBase;

import com.google.common.io.Files;
import com.google.common.io.Resources;

public class TestJwatArcLoader {
  private static final Log LOG = LogFactory.getLog(TestJwatArcLoader.class);
  private File tempDir;

  @Test
  public void testCountLinks() throws Exception {
    String arcTestDataFile = Resources.getResource("arc/example.arc.gz").getPath();
    
    ArcRecordBase record = null;
    InputStream in = new FileInputStream(new File(arcTestDataFile));
    ArcReader reader = ArcReaderFactory.getReader(in);
    int cnt = 0;
    while ((record = reader.getNextRecord()) != null) {
      // This is how you get out various fields.
      @SuppressWarnings("unused") String url = record.getUrlStr();
      @SuppressWarnings("unused") String date = record.getArchiveDateStr();
      @SuppressWarnings("unused") String content = "";
      String type = record.getContentTypeStr();

      if (type.toLowerCase().contains("text")) {
        content = new String(IOUtils.toByteArray(record.getPayloadContent()), Charset.forName("UTF-8"));
      }

      cnt++;
    }
    reader.close();
    in.close();

    LOG.info(cnt + " records read!");
    assertEquals(300, cnt);
  }

  @Before
  public void setUp() throws Exception {
    // create a random file location
    tempDir = Files.createTempDir();
    LOG.info("Output can be found in " + tempDir.getPath());
  }

  @After
  public void tearDown() throws Exception {
    // cleanup
    FileUtils.deleteDirectory(tempDir);
    LOG.info("Removing tmp files in " + tempDir.getPath());
  }
}
