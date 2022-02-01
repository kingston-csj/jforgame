package jforgame.server.game.database.user;

import jforgame.server.db.BaseEntity;

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
    public Comparable getId() {
        return null;
    }
}
