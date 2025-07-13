package jforgame.demo.game.database.user;

import jforgame.orm.converter.Convert;
import jforgame.orm.entity.BaseEntity;
import jforgame.orm.converter.JsonAttributeConverter;
import jforgame.demo.game.login.model.Platform;
import jforgame.demo.game.vip.model.VipRight;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * 玩家实体
 *
 */
@Entity(name = "playerent")
public class PlayerEnt extends BaseEntity<Long> {

    private static final long serialVersionUID = 8913056963732639062L;

    @Id
    @Column
    private Long id;

    @Column
    private long accountId;

    @Column
    private String name;

    /**
     * 职业
     */
    @Column
    private int job;

    @Column
    private int level;

    /**
     * 上一次每日重置的时间戳
     */
    @Column
    private long lastDailyReset;

    @Column
    @Convert(converter = JsonAttributeConverter.class)
    private VipRight vipRight;

    @Column(columnDefinition = "varchar(16)")
    private Platform platform;

    public PlayerEnt() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getJob() {
        return job;
    }

    public void setJob(int job) {
        this.job = job;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getLastDailyReset() {
        return lastDailyReset;
    }

    public void setLastDailyReset(long lastDailyReset) {
        this.lastDailyReset = lastDailyReset;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public VipRight getVipRight() {
        return vipRight;
    }

    public void setVipRight(VipRight vipRight) {
        this.vipRight = vipRight;
    }

    @Override
    protected void onAfterLoad() {
        // 为所有空值的引用属性自动初始化
        Arrays.stream(getClass().getDeclaredFields())
                .forEach(e -> {
                    e.setAccessible(true);
                    try {
                        // 过滤静态属性
                        if (Modifier.isFinal(e.getModifiers()) || Modifier.isStatic(e.getModifiers())) {
                            return;
                        }
                        // 过滤基本类型
                        if (e.getType().isPrimitive()) {
                            return;
                        }
                        // 过滤枚举类型
                        if (e.getType().isEnum()) {
                            return;
                        }
                        if (e.get(this) == null) {
                            @SuppressWarnings("all")
                            Object instance = e.getType().newInstance();
                            e.set(this, instance);
                        }
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                });
    }

    @Override
    protected void onBeforeSave() {
    }

}
