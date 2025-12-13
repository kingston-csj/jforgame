package jforgame.commons.schedule.parser;

import java.util.Date;

/**
 * 时间表达式解析器
 * 背景：无论是spring表达式，还是cron表达式，均不支持自定义格式的表达式。
 * 使用该类，可以拓展游戏常用的自定义格式时间表达式，例如开服时间表达式、合服时间表达式等。
 * 统一管理游戏业务所有基于一次性/周期性表达式的时间解析器。
 * 主要是整合cron表达式，以及游戏业务自定义的表达式。
 */
public interface ScheduleExpressionParser {

    /**
     * 是否不本解析器允许的格式
     *
     * @param expression
     * @return
     */
    boolean isValidExpression(String expression);

    /**
     * 计算距离参考时间起的下一次日期
     *
     * @param expression cron表达式
     * @param date       参考日期
     * @return
     */
    Date getNextTriggerTimeAfter(String expression, Date date);

    /**
     * 是否周期性表达式
     *
     * @param expression cron表达式
     * @return
     */
    boolean isPeriodicExpression(String expression);
}
