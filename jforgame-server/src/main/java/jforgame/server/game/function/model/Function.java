package jforgame.server.game.function.model;

import java.util.HashSet;
import java.util.Set;

public class Function {

	private Set<Integer> funcs = new HashSet<>();

	public Set<Integer> getFuncs() {
		return funcs;
	}

	public void setFuncs(Set<Integer> funcs) {
		this.funcs = funcs;
	}

	public void open(int funcId) {
		this.funcs.add(funcId);
	}

	public void close(int funcId) {
		this.funcs.remove(funcId);
	}

	public boolean isOpened(int funcId) {
		return this.funcs.contains(funcId);
	}

}
