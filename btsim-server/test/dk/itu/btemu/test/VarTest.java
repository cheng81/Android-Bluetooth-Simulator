package dk.itu.btemu.test;

import java.util.UUID;

import junit.framework.TestCase;

public class VarTest extends TestCase {

	public void testString() {
		String s = "foobarbaz";
		System.out.println(s.charAt(s.length()-1));
	}
	public void testUUID() {
		System.out.println(UUID.randomUUID().toString());
	}
}
