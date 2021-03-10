package jforgame.server.game.database.server;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;


/**
 * 本服公共数据杂项记录
 * @author kinson
 */
@Entity
public class ServerRecord {

	/**
	 * 唯一主键
	 */
	@Id
	@Column
	private String key;
	@Column
	private String value;

	/**
	 * 注释说明
	 */
	@Column
	private String comment;

	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}

}
