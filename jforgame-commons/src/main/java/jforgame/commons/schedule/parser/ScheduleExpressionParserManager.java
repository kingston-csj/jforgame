package jforgame.commons.schedule.parser;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Schedule expression parser manager
 * Used for game business time scheduling scenarios (activity start/end, task trigger, etc.), manages multi-type parser chains and dispatches parsing requests.
 */
public class ScheduleExpressionParserManager {

    /**
     * Parser chain, default has only one, which is the quartz Cron expression parser itself
     * Can add server opening time parser, server merging time parser, etc. as needed
     */
    private static List<ScheduleExpressionParser> parserChain = new CopyOnWriteArrayList<>();

    static {
        // Default parser
        parserChain.add(new QuartzScheduleExpressionParser());
    }

    /**
     * Adds a parser to the front of the chain
     *
     * @param parser the parser to add
     */
    public static void addParserBefore(ScheduleExpressionParser parser) {
        parserChain.add(0, parser);
    }

    /**
     * Adds a parser to the end of the chain
     *
     * @param parser the parser to add
     */
    public static void addParserAfter(ScheduleExpressionParser parser) {
        parserChain.add(parser);
    }

    /**
     * Parses the expression one by one according to the parser chain, if the expression matches the rule, it will be parsed by the current node parser
     *
     * @param expression the expression, can be a cron expression or a custom cron expression such as server opening time
     * @param date       the start time
     * @return the next execution time, returns null if parsing fails or there is no next execution time
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
