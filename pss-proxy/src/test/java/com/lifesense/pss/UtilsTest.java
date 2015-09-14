package com.lifesense.pss;

import org.junit.Test;


public class UtilsTest {

	@Test
	public void testg(){
		String s = "zookeeper://123.59.78.95:2181";
		System.out.println(s.replaceAll("^zookeeper://", ""));
	}
}
