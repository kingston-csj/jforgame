package jforgame.server.hotswap;

import org.junit.Test;

import jforgame.server.doctor.HotswapManager;
import jforgame.server.doctor.Person;

public class InstrumentHotSwapTest {

	@Test
	public void testHotswap() throws Exception {

		//only a person instance in memory
		final Person p = new Person();
		new Thread(
				() -> {
					try{
						while (true) {
							Thread.sleep(1000);
							System.err.println(p);
						}
					}catch(Exception e){

					}
				}
				).start();

		Thread.sleep(3000);
		HotswapManager.INSTANCE.reloadClass("target");
		Thread.sleep(3000);

	}

}
