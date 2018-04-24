package com.kingston.jforgame.admin.servlet;

import java.io.File;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.kingston.jforgame.admin.utils.DbHelper;

/**
 * 各种初始化
 * @author kingston
 */
public class InitServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {
		//TODO 这里读取配置有点恶心，应该有优雅的方法
		String webInfoPath = getServletContext().getRealPath("/") + "WEB-INF";
		String databasePath = webInfoPath + File.separator +"classes"+File.separator+"proxool.xml";
		System.out.println(databasePath);
		DbHelper.init(databasePath);
	}

}
