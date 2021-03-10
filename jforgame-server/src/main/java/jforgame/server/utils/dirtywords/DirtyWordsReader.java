package jforgame.server.utils.dirtywords;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public enum DirtyWordsReader {

	/** 枚举单例 */
	INSTANCE;

	private String ENCODING = "UTF-8";
	private final Map<Character,List<String>> dirtyWords = new HashMap<>();
	private int wordCount =  0;
	private final String FILE_PATH = "configs/SensitiveWord.txt";

	DirtyWordsReader(){
		Set<String> words = readSensitiveWordPool();
		this.initWordsStore(words);
	}

	private Set<String> readSensitiveWordPool() {
		Set<String> words = new HashSet<>();
		File file = new File(FILE_PATH);
		try (InputStreamReader read = new InputStreamReader(new FileInputStream(file),ENCODING)) {
			if (file.isFile() && file.exists()) {
				BufferedReader bufferedReader = new BufferedReader(read);
				String word;
				while ((word = bufferedReader.readLine()) != null) {
					if(isEmpty(word)){
						continue;
					}
					wordCount++;
					words.add(word.trim());
				}
			}else{
				throw new FileNotFoundException("dirty words file is empty");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return words;
	}

	private void initWordsStore(Set<String> words) {
		for (String word:words) {
			if (isEmpty(word)) {
				continue;
			}
			char first = word.charAt(0);
			List<String> sameFirst = dirtyWords.get(first);
			if (sameFirst == null) {
				sameFirst = new ArrayList<>();
				dirtyWords.put(first,sameFirst);
			}
			sameFirst.add(word);
		}
	}

	private boolean isEmpty(String word){
		return word == null || word.trim().length() <= 0;

	}

	public Map<Character, List<String>> getDirtyWords() {
		return dirtyWords;
	}

	public int getWordCount(){
		return wordCount;
	}

}