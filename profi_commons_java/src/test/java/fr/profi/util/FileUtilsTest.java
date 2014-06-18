package fr.proline.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class FileUtilsTest {

    @Test
    public void testSplitFilePath() {
	final String linuxFile = "aa/bb//cc";
	String[] result = FileUtils.splitFilePath(linuxFile);
	assertEquals("Linux file", 3, result.length);

	final String windowsFile = "aa\\\\bb\\cc\\";
	result = FileUtils.splitFilePath(windowsFile);
	assertEquals("Windows file", 3, result.length);

	final String simpleFile = "fileName";
	result = FileUtils.splitFilePath(simpleFile);
	assertEquals("Simple file", 1, result.length);
    }

}
