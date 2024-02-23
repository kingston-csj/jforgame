package jforgame.demo.hotswap;

import jforgame.demo.doctor.HotswapManager;
import jforgame.demo.doctor.Person;
import org.junit.Test;

public class InstrumentHotSwapTest {

	@Test
	public void testHotswap() throws Exception {
		System.out.println(new Person().toString());
		HotswapManager.INSTANCE.reloadClass("hotswap");
		System.out.println(new Person().toString());
	}

}
