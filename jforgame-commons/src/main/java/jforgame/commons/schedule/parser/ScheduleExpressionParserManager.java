package jforgame.commons.schedule.parser;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 调度表达式解析器管理器
 * 用于游戏业务的时间调度场景（活动开始/结束、任务触发等），管理多类型解析器链，分发解析请求。
 */
public class ScheduleExpressionParserManager {

    /**
     * 解析链,默认只有一个,就是quartz本身的Cron表达式解析器
     * 可根据需要,添加开服时间解析器,合服时间解析器等等
     */
    private static List<ScheduleExpressionParser> parserChain = new CopyOnWriteArrayList<>();

    static {
        // 默认解析器
        parserChain.add(new QuartzScheduleExpressionParser());
    }

    /**
     * 添加解析器到链表的最前面
     *
     * @param parser
     */
    public static void addParserBefore(ScheduleExpressionParser parser) {
        parserChain.add(0, parser);
    }

    /**
     * 添加解析器到链表的最后面
     *
     * @param parser
     */
    public static void addParserAfter(ScheduleExpressionParser parser) {
        parserChain.add(parser);
    }

    /**
     * 按照解析链,逐一解析表达式,如果表达式符合规则,则按当前节点解析器进行解析
     *
     * @param expression 表达式,可以是cron表达式,也可以是开服时间等自定义cron表达式
     * @param date       开始时间
     * @return 下次执行时间 如果解析失败,或者没有下次执行时间,返回null
     */
    public static Date getNextTriggerTimeAfter(String expression, Date date) {
        for (ScheduleExpressionParser parser : parserChain) {
            if (parser.isValidExpression(expression)) {
                return parser.getNextTriggerTimeAfter(expression, date);
            }
        }
        return null;
    }

    public static ScheduleExpressionParser getParser(String expression) {
        for (ScheduleExpressionParser parser : parserChain) {
            if (parser.isValidExpression(expression)) {
                return parser;
            }
        }
        return null;
    }

}
