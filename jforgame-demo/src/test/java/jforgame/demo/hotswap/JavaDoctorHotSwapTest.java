package jforgame.demo.hotswap;

import jforgame.demo.framework.doctor.HotswapManager;
import jforgame.hotswap.JavaDoctor;

public class JavaDoctorHotSwapTest {

	//	@Test
	public static void testHotswap()  {

	}

	public static void main(String[] args) {
		JavaDoctor.setAgentPath("D:\\jforgame-hotswap-agent.jar");
		new PlayerService().say("jforgame");
		HotswapManager.INSTANCE.reloadClass("hotswap");
		Person.sayHi();
		new PlayerService().say("jforgame");
	}

}
