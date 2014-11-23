package edu.nyu.cs.cs2580;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;

public class PseudoRelevanceFeedback {
  
  private Indexer _indexer;
  private int _numterms;
  private Vector<ScoredDocument> _docs;
  private Query _query;
  private Set<String> stopWords;
  
  class FrequentTerm implements Comparable<FrequentTerm>{
    private String _term;
    private double _probability;
    
    FrequentTerm(String term, double prob){ 
      _term = term;
      _probability = prob;
    }
    
    public String toString() {
      return _term + "\t" + _probability;
    }
    
    @Override
    public int compareTo(FrequentTerm o) {
      if (this._probability == o._probability) {
        return 0;
      }
      return (this._probability > o._probability) ? 1 : -1;
    }
  }
  
  public PseudoRelevanceFeedback(Vector<ScoredDocument> docs, Indexer indexer, int numterms, Query query){
    _docs = docs;
    _indexer = indexer;
    _numterms = numterms;
    _query = query;
    stopWords = new HashSet<String>();
    stopWords.add("the");
    stopWords.add("of");
    stopWords.add("and");
    stopWords.add("in");
    stopWords.add("&");
    stopWords.add("to");
    stopWords.add("^");
    stopWords.add("is");
    stopWords.add("for");
    stopWords.add("on");
    stopWords.add("as");
    stopWords.add("by");
    stopWords.add("was");
    stopWords.add("with");
    stopWords.add("from");
    stopWords.add("that");
    stopWords.add("at");
    stopWords.add("it");
    stopWords.add("are");
    stopWords.add("this");
    stopWords.add("[edit]");
    stopWords.add("retrieved");
    stopWords.add("or");
    stopWords.add("-");
    stopWords.add("�C");
    stopWords.add("an");
    stopWords.add("be");
    stopWords.add("which");
    stopWords.add("his");
    stopWords.add("also");
    stopWords.add("has");
    stopWords.add("not");
    stopWords.add("were");
    stopWords.add("he");
    stopWords.add("have");
    stopWords.add("a");
    stopWords.add("their");
    stopWords.add("had");
    stopWords.add("by");
    stopWords.add("been");
    stopWords.add("can");
    stopWords.add("you");
    stopWords.add("she");
    stopWords.add("other");
    stopWords.add("its");
    stopWords.add("about");
    stopWords.add("her");
    stopWords.add("there");
    stopWords.add("no");
    stopWords.add("they");
  }
  
  public StringBuffer compute(){
    Set<String> termSet = new HashSet<String>();
    List<Integer> docidList = new ArrayList<Integer>();
    Queue<FrequentTerm> rankQueue = new PriorityQueue<FrequentTerm>();
    Vector<String> queryTerms = ((QueryPhrase) _query).getTermVector();
    int totalTerms = 0;
    for(ScoredDocument doc : _docs){
      int docid = doc.getDocid();
      docidList.add(docid);
      Document document =  _indexer.getDoc(docid);
      Map<String, Integer> termMap = _indexer.getDocTermMap(docid);
      for(String term : termList){
        if(!termSet.contains(term) && !queryTerms.contains(term) && !stopWords.contains(term)){
          termSet.add(term);
        }
      }
      totalTerms += ((DocumentIndexed)document).getLength();
    }
    Collections.sort(docidList);
    for(String term : termSet){
      double prob = 0;
      for(int id : docidList){
        prob += _indexer.documentTermFrequency(term, id);
      }
      rankQueue.add(new FrequentTerm(term, prob/totalTerms));
      if (rankQueue.size() > _numterms) {
        rankQueue.poll();
      }
    }
    List<FrequentTerm> results = new ArrayList<FrequentTerm>();
    FrequentTerm frequentTerm = null;
    while ((frequentTerm = rankQueue.poll()) != null) {
      results.add(frequentTerm);
    }
    Collections.sort(results, Collections.reverseOrder());
    normalize(results);
    StringBuffer response = new StringBuffer();
    for(FrequentTerm fterm : results){
      response.append(fterm.toString());
      response.append('\n');
    }
    return response;
  }
  
  private void normalize(List<FrequentTerm> terms){
    double normFactor = 0;
    for(FrequentTerm fterm : terms){
      normFactor += fterm._probability ;
    }
    for(FrequentTerm fterm : terms){
      fterm._probability = fterm._probability / normFactor;
    }
  }
}