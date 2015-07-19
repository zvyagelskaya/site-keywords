package ru.zyzzy;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

/**
 *
 * @author yz
 */
public class SiteKeywords {
  
  static class ValueComparator implements Comparator<String> {
    Map<String, Integer> base;
    public ValueComparator(Map<String, Integer> base) {
      this.base = base;
    }

    @Override
    public int compare(String a, String b) {
      if (base.get(a) >= base.get(b)) {
        return -1;
      } else {
        return 1;
      } // returning 0 would merge keys
    }
  }
  
  public static boolean isStopword(List<String> stopwords, String s) {
    return stopwords.contains(s) || s.length() < 5;
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    List<String> stopwords = Arrays.asList(new String[]{"только", "чтобы", "очень", "когда", "можно", "просто"});
    Map<String, Integer> tokens = new HashMap<>();
    try {
      File root = new File(args[0]);
      File[] list = root.listFiles();
      if (list == null) throw new IllegalArgumentException("I expect to receive directory");

      for ( File f : list ) {
        if ( !f.isDirectory() ) {
          String text = "";
          try (FileInputStream fstream = new FileInputStream(f)) {
            try (DataInputStream in = new DataInputStream(fstream)) {
              BufferedReader br = new BufferedReader(new InputStreamReader(in));
              String strLine;
              while ((strLine = br.readLine()) != null)   {
                text += strLine;
              }
              in.close();
            } catch (Exception e) { // Catch exception if any
              System.err.println("Error: " + e.getMessage());
            }
            fstream.close();

            StringTokenizer st = new StringTokenizer(text, " ");
            while (st.hasMoreTokens()) {
              String token = st.nextToken().toLowerCase();
              if (!isStopword(stopwords, token)) {
                if (tokens.containsKey(token)) {
                  tokens.replace(token, tokens.get(token) + 1);
                } else { 
                  tokens.put(token, 1);
                }
              }
            }
          } catch (Exception e) { // Catch exception if any
            System.err.println("Error: " + e.getMessage());
          }
        }
      }
    } catch (Exception e) { // Catch exception if any
      System.err.println("Error: " + e.getMessage());
    }

    ValueComparator bvc =  new ValueComparator(tokens);
    TreeMap<String,Integer> sorted_map = new TreeMap<>(bvc);
    sorted_map.putAll(tokens);
    System.out.println(sorted_map);
  }
  
}
