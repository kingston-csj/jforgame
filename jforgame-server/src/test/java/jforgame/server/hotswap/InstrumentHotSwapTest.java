package jforgame.server.hotswap;

import jforgame.server.doctor.HotswapManager;
import jforgame.server.doctor.Person;
import org.junit.Test;

public class InstrumentHotSwapTest {

	@Test
	public void testHotswap() throws Exception {
		System.out.println(new Person().toString());
		HotswapManager.INSTANCE.reloadClass("hotswap");
		System.out.println(new Person().toString());
	}

}
