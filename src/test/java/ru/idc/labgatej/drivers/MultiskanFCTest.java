package ru.idc.labgatej.drivers;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MultiskanFCTest extends TestCase {

	@Test
	public void test() {
		MultiskanFC ms = new MultiskanFC();
		Path file = Paths.get("src/test/java/ru/idc/labgatej/testData/multiskanFC3.xml");

		try {
			ms.parseFile(file);
		} catch (IOException e) {
			fail();
		}
	}
}
