package fr.profi.util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadLoggerTest {

    private static final Logger LOG = LoggerFactory.getLogger(ThreadLoggerTest.class);

    @Test
    public void testThreadLoggerLogger() {
	final Runnable target = new InnerTest();

	final Thread thr = new Thread(target, "Thread-testThreadLogger");
	thr.setPriority(Thread.NORM_PRIORITY);
	thr.setUncaughtExceptionHandler(new ThreadLogger(LOG));

	thr.start();

	try {
	    thr.join();

	    LOG.debug("Thread teminated");
	} catch (InterruptedException intEx) {
	    LOG.warn("Thread.join() interrupted", intEx);
	}

    }

    private class InnerTest implements Runnable {

	public void run() {
	    LOG.debug("Entering thread loop");

	    for (int i = 0; i < 10; ++i) {
		/* Will generate a divide by zero ArithmeticException */
		final int value = 1 / (i - 5);
		LOG.trace("Value: {}", value);
	    }

	}

    }

}
