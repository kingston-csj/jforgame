package jforgame.demo.hotswap;

import jforgame.demo.doctor.HotswapManager;
import org.junit.Test;

public class JavaDoctorHotSwapTest {

	@Test
	public void testHotswap() throws Exception {
		new PlayerService().say("jforgame");
		HotswapManager.INSTANCE.reloadClass("hotswap");
//		Person.sayHi();
		new PlayerService().say("jforgame");
	}

}
