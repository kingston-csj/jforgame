package com.kingston.monitor.jmx;

public interface ControllerMBean {
	
	/** 
     * 统计在线玩家总数 
     * @return 
     */  
    int getOnlinePlayerSum();  
      
    /** 
     * 统计内存使用情况 
     * @return 
     */  
    String getMemoryInfo();  
    
    /**
     * 执行JavaScript代码
     * @param jsCode
     * @return
     */
    String execJavascript(String jsCode);
      

}
