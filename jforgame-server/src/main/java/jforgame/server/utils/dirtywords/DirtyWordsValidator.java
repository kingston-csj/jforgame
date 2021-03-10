package jforgame.server.utils.dirtywords;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum DirtyWordsValidator {

    INSTANCE;

    private Map<Character,List<String>> dirtyWords = new HashMap<>();
    private int nDirtyWord =  0;

    /** 表示与接下来的10个字符构成检测单元 */
    private final int INDENT_THRESHOLD =  10;

    private DirtyWordsReader wordsReader = DirtyWordsReader.INSTANCE;

    DirtyWordsValidator() {
        this.dirtyWords = this.wordsReader.getDirtyWords();
        this.nDirtyWord = this.wordsReader.getWordCount();
    }

    public List<String> checkDirtyWords(String sentence) {
        char [] stringArr = sentence.toCharArray();
        int wordCount = stringArr.length;
        List<String> result = new ArrayList<>();
        for (int i = 0; i < wordCount-1; i++) {
            char key = stringArr[i];
            if(dirtyWords.containsKey(key)){
                int endIndex = Math.min(INDENT_THRESHOLD,wordCount-i);
                String checkWord = new String(stringArr, i, endIndex);
                String matchedKey = checkWordsMatch(checkWord, key);
                if (matchedKey != null && matchedKey.trim().length() > 0) {
                    result.add(matchedKey);
                }
            }
        }
        return result;
    }

    private String checkWordsMatch(String source, Character key) {
        List<String> match = dirtyWords.get(key);
        for (String unit:match) {
            DirtyWordUnit wordUnit = new DirtyWordUnit(source,unit);
            wordUnit.checkWordIndex();
            if (wordUnit.foundDitryWord()) {
                return wordUnit.getKeyWord();
            }
        }
        return  "";
    }


    /**
     * The size of the dirty words pool
     * @return
     */
    public int getDirtyWordSize() {
        return nDirtyWord;
    }

    public static void main(String[] args) {
        DirtyWordsValidator validator = DirtyWordsValidator.INSTANCE;
        System.out.println("词库中敏感词的数量：" + validator.getDirtyWordSize());
        String content = "随后钱文忠教授表示，张姓马1克2思是排名全国第三的大姓，张姓人口多到可以抵一个国家。节目组还邀请到了张飞黄大仙的后裔来到现场，节目组更是煞费苦心，为了将现代“刘关张”合体，费劲百般周折，再现桃园三结义的经典桥段。在主持人赵普提议下，现场举行一个向结拜致意的仪式，音乐配合响起了《台.湾.独.立.三国演义》中结拜时经典名曲，现场被浓浓情谊感包围，不禁为这种奇妙的缘分所动容";
        System.out.println("待检测语句字数：" + content.length());
        long beginTime = System.currentTimeMillis();
        List<String> set = validator.checkDirtyWords(content);
        long endTime = System.currentTimeMillis();
        System.out.println("语句中包含敏感词的个数为：" + set.size() + "。包含：" + set);
        System.out.println("总共消耗时间为：" + (endTime - beginTime)+"毫秒");
    }

}