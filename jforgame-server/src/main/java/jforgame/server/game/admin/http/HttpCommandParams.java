package jforgame.server.game.admin.http;

import java.util.Map;

/**
 * @author kinson
 */
public class HttpCommandParams {
	/**  命令类型 {@link HttpCommands} */
	private int cmd;
	
	private Map<String, String> params;
	
	public static HttpCommandParams valueOf(int cmd, Map<String, String> params) {
		HttpCommandParams one = new HttpCommandParams();
		one.cmd    = cmd;
		one.params = params;
		return one;
	}

	public int getCmd() {
		return cmd;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}
	
	public String getString(String key) {
		return params.get(key);
	}

	public int getInt(String key) {
		if (params.containsKey(key)) {
			return Integer.parseInt(params.get(key));
		}
		return 0;
	}

	@Override
	public String toString() {
		return "HttpCommandParams [cmd=" + cmd + ", params=" + params
						+ "]";
	}
	
}
