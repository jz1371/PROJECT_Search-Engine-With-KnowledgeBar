package edu.nyu.cs.cs2580;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Spelling {

  public final Map<String, Integer> nWords = new HashMap<String, Integer>();

  public Spelling(String file) throws IOException {
    BufferedReader in = new BufferedReader(new FileReader(file));
    Pattern p = Pattern.compile("\\w+");
    for (String temp = ""; temp != null; temp = in.readLine()) {
      Matcher m = p.matcher(temp.toLowerCase());
      while (m.find())
        nWords.put((temp = m.group()),
            nWords.containsKey(temp) ? nWords.get(temp) + 1 : 1);
    }
    in.close();
  }

  private final List<String> edits(String word) {
    List<String> result = new ArrayList<String>();
    // deletes
    for (int i = 0; i < word.length(); ++i)
      result.add(word.substring(0, i) + word.substring(i + 1));
    // transpose
    for (int i = 0; i < word.length() - 1; ++i)
      result.add(word.substring(0, i) + word.substring(i + 1, i + 2)
          + word.substring(i, i + 1) + word.substring(i + 2));
    // replace
    for (int i = 0; i < word.length(); ++i)
      for (char c = 'a'; c <= 'z'; ++c)
        result.add(word.substring(0, i) + String.valueOf(c)
            + word.substring(i + 1));
    // insert
    for (int i = 0; i <= word.length(); ++i)
      for (char c = 'a'; c <= 'z'; ++c)
        result.add(word.substring(0, i) + String.valueOf(c)
            + word.substring(i));
    return result;
  }

  public final String correct(String word) {
    if (nWords.containsKey(word))
      return word;
    List<String> list = edits(word);
    Map<Integer, String> candidates = new HashMap<Integer, String>();
    for (String s : list)
      if (nWords.containsKey(s))
        candidates.put(nWords.get(s), s);
    if (candidates.size() > 0)
      return candidates.get(Collections.max(candidates.keySet()));
    for (String s : list)
      for (String w : edits(s))
        if (nWords.containsKey(w))
          candidates.put(nWords.get(w), w);
    return candidates.size() > 0 ? candidates.get(Collections.max(candidates
        .keySet())) : word;
  }

  public static void main(String args[]) throws IOException {
    Spelling spel = new Spelling("data/SpellTrainer/big.txt");
    long start = System.nanoTime();
    System.out.println(spel.correct("exceptiosnfsasd"));
    System.out.println(System.nanoTime() - start);
  }

}