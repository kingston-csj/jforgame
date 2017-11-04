package com.kingston.hotswap;

import org.junit.Test;

import com.kingston.doctor.HotswapManager;
import com.kingston.doctor.Person;

public class TestInstrumentHotswap {

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

		Thread.sleep(3000); //wait
		HotswapManager.INSTANCE.reloadClass("Person");
		Thread.sleep(3000); //wait

	}

}
