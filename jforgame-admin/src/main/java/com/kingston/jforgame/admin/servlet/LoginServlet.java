package com.kingston.jforgame.admin.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.kingston.jforgame.admin.utils.DbHelper;
import com.kingston.jforgame.admin.utils.HttpResult;
import com.kingston.jforgame.admin.utils.ResponseUtil;

@WebServlet(name = "login", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static ConcurrentMap<String, String> accounts;

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		String accountParam = request.getParameter("account");
		String psdParam = request.getParameter("password");
		if (validate(accountParam, psdParam)) {
			HttpSession session = request.getSession(false);
			if (session == null) {
				session = request.getSession();
			}
			session.setAttribute("account", accountParam);
			ResponseUtil.responseJson(HttpResult.valueOfSucc(), response);
		} else {
			ResponseUtil.responseJson(HttpResult.valueOfFailed(), response);
		}
	}

	private boolean validate(String account, String password) {
		if (accounts == null) {
			synchronized (LoginServlet.class) {
				 List<Map<String, String>> result = DbHelper.queryMapList(DbHelper.GM, "SELECT * FROM account");
				 for (Map<String, String> record:result) {
					 accounts = new ConcurrentHashMap<>();
					 accounts.put(record.get("account"), record.get("psd"));
				 }
			}
		}
		String realPassword = accounts.get(account);
		if (realPassword == null) {
			return false;
		}

		//TODO 密码加密
		return realPassword.equals(password);
	}
	
}
