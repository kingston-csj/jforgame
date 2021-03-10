package jforgame.merge;

import jforgame.merge.service.MergeController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MergeStartup {

    private static Logger logger = LoggerFactory.getLogger("MergeStartup");

    public static void main(String[] args) {
        try {
            new MergeController().doMerge();
        } catch (Exception e) {
            logger.error("", e);
        }
    }
}
