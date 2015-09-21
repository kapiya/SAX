package net.seninp.jmotif.sax.discord;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import net.seninp.jmotif.sax.NumerosityReductionStrategy;
import net.seninp.jmotif.sax.TSProcessor;
import net.seninp.jmotif.sax.registry.LargeWindowAlgorithm;
import net.seninp.util.StackTrace;

public class TestDoubleLong {

  private static final String TEST_DATA_FNAME = "src/resources/test-data/ecg0606_1.csv";

  private static final int WIN_SIZE = 50;
  private static final int PAA_SIZE = 4;
  private static final int ALPHABET_SIZE = 3;

  private static final double NORM_THRESHOLD = 0.01;

  private static final int DISCORDS_TO_TEST = 5;

  private double[] series;

  @Before
  public void setUp() throws Exception {
    series = TSProcessor.readFileColumn(TEST_DATA_FNAME, 0, 0);
    double[] tmp = new double[series.length * 2];
    for (int i = 0; i < series.length; i++) {
      tmp[i] = series[i];
      tmp[i + series.length] = series[i];
    }
    series = tmp;
  }

  @Test
  public void test() {

    DiscordRecords discordsBruteForce = null;
    DiscordRecords discordsHash = null;

    try {

      discordsBruteForce = BruteForceDiscordImplementation.series2BruteForceDiscords(series,
          WIN_SIZE, DISCORDS_TO_TEST, new LargeWindowAlgorithm());
      for (DiscordRecord d : discordsBruteForce) {
        System.out.println("brute force discord " + d.toString());
      }

      discordsHash = HOTSAXImplementation.series2Discords(series, DISCORDS_TO_TEST, WIN_SIZE,
          PAA_SIZE, ALPHABET_SIZE, NumerosityReductionStrategy.NONE, NORM_THRESHOLD);
      for (DiscordRecord d : discordsHash) {
        System.out.println("hotsax hash discord " + d.toString());
      }

    }
    catch (Exception e) {
      fail("sholdn't throw an exception, exception thrown: \n" + StackTrace.toString(e));
      e.printStackTrace();
    }

    for (int i = 0; i < 1; i++) {

      Double d1 = discordsBruteForce.get(i).getNNDistance();
      Double d2 = discordsHash.get(i).getNNDistance();

      assertEquals(d1, d2);

    }

  }
}