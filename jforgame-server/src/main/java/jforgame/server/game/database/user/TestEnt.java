package jforgame.server.game.database.user;

import jforgame.server.db.BaseEntity;
import jforgame.socket.actor.MailBox;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "testent")
public class TestEnt extends BaseEntity {

    @Id
    @Column
    private Long id;

    @Column
    private long accountId;

    @Column
    private long level;


    @Override
    public MailBox mailBox() {
        return null;
    }

    @Override
    public Comparable getId() {
        return null;
    }
}
