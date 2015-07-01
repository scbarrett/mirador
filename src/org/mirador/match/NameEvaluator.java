/* --------------------------------------------------------------------------+
   NameEvaluator.java - Matching strategy that measures the name similarity of
     model elements.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   Mirador supports several model comparison modules for schema matching.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.match;
import ca.dsrg.mirador.model.EcoreExtra;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Evaluator of model element name similarity.
 *
 * @since   v0.18 - Mar 6, 2010
 * @author  Stephen Barrett
 */
public class NameEvaluator extends SimilarityEvaluator {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**                                                                     DOCDO: Provide constructor overview.
   *
   */
  public NameEvaluator() {
  }


  public NameEvaluator(float weight) {
    super(weight);
    label_ = "by Name";
    spin_tool_tip_ =
      "Set weight of the by Name strategy in similarity calculation.";
    radio_tool_tip_ =
      "Automatically match elements based on name similarity.";
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ operations
  /**
   * Evaluates lexical similarity of names from one model element to another. As
   * measures are not always symmetrical, a direction of evaluation is implied
   * by the arguments. The measure is a normalized representation of similarity.
   *
   * Similarity is based on letter pairs within the words that make up a name.
   * For model element names, which are typically expressed in camel case, the
   * words are separated by capital letters, or "humps".
   *
   * @param  from_element  Model element <i>from</i> which to measure.
   * @param  to_element  Model element <i>to</i> which to measure.
   * @return  Normalized similarity: 0.0 = no similarity, 1.0 = identical
   * (Not good for one or two character names.)
   */
  @Override public float evaluate(EcoreExtra from_element,
      EcoreExtra to_element) { // TODO:3 Replace with Simmetrics.
    // Obtain adjacent character pairs for element name to measure from.
    List<String> fm_pairs = pairNameCharacters(from_element.getName());
    if (fm_pairs.isEmpty() && from_element.getName() != null)
      fm_pairs.add(from_element.getName());

    // Obtain adjacent character pairs for element name to measure to.
    List<String> to_pairs = pairNameCharacters(to_element.getName());
    if (to_pairs.isEmpty() && to_element.getName() != null)
      to_pairs.add(to_element.getName());


    int pair_ct = fm_pairs.size() + to_pairs.size();  // Total pairs in name.
    int match_ct = 0;  // Number of name pairs that match.

    // Tally pairs in the "to" list that match those in the "from" list.
    for (String fm_pair : fm_pairs) {
      for (Iterator<String> to_it = to_pairs.iterator(); to_it.hasNext();) {
        String to_pair = to_it.next();

        if (fm_pair.equals(to_pair)) { // Pairs are identical.
          ++match_ct;      // Increment count of matched pairs, and
          to_it.remove();  //   remove the "to" pair from further consideration.
          break;
        }
      }
    }

    // Similarity measure as a ratio of matched pairs to total pairs.
    return (pair_ct > 0) ? (2.0f * match_ct) / pair_ct : 0.0f;
  }


  /**
   * Collects all pairs of adjacent characters found in an element name.
   *
   * @param  name  Element name to obtain character pairs for.
   * @return  List of all word character pairs in the name.
   */
  private List<String> pairNameCharacters(String name) {
    List<String> name_pairs = new ArrayList<String>();
    List<String> words = tokenizeName(name);  // Break out camel cased "words".

    // Extract all adjacent character pairs from each word of element name.
    for (String word : words) {
      List<String> word_pairs = pairWordCharacters(word);

      // Add word character pairs to name's collection of all pairs.
      for (String pair : word_pairs)
        name_pairs.add(pair);
    }

    return name_pairs;
  }


  /**
   * Collects all pairs of adjacent characters found in a word.
   *
   * @param  word  Word of element name to obtain character pairs for.
   * @return  List of all character pairs in the word.
   */
  private List<String> pairWordCharacters(String word) {
    List<String> word_pairs = new ArrayList<String>();

    // Extract all adjacent character pairs from word.
    for (int i = 0; i < word.length() - 1; ++i) {
      word_pairs.add(word.substring(i, i + 2));  // Save word character pair.
    }

    return word_pairs;
  }


  /**
   * Break camel cased element name into "words" as delimited by the "humps"
   * of the name.
   *
   * @param  name  Element name to tokenize.
   * @return  List of words in the name.
   */
  private List<String> tokenizeName(String name) {
    List<String> words = new ArrayList<String>();
    StringBuilder name_buf = new StringBuilder(name);
    StringBuilder word_buf = null;
    char ch;
    boolean old_case = false;  // false = lower case, true = upper case.
    boolean new_case = false;  // false = lower case, true = upper case.

    // Eat away at name to build up words character by character.
    while (name_buf.length() > 0) {
      ch = name_buf.charAt(0);  // Get character at front of name.
      new_case = ('A' <= ch && ch <= 'Z');

      // Test word boundaries.
      if (word_buf == null) { // At start of new word.
        word_buf = new StringBuilder();  // Make area for collecting characters.
      }
      else if (new_case != old_case){
        if (new_case == true) { // Change to upper case.
          words.add(word_buf.toString());  // Add finished word to list.
          word_buf = null;  // Invalidate word buffer.
          continue;  // Start fetching next word.
        }
        else if (word_buf.length() > 1){ // Change to lower case.
          char last_ch = word_buf.charAt(word_buf.length() - 1);
          words.add((word_buf.deleteCharAt(word_buf.length() - 1)).toString());
          word_buf = new StringBuilder();
          word_buf.append(last_ch);
          continue;  // Start fetching next word.
        }
      }

      name_buf.deleteCharAt(0);  // Remove character from front of name,
      word_buf.append(ch);       //   and store it in current word buffer.
      old_case = new_case;
    }

    //char end_ch = word_buf.charAt(word_buf.length() - 1);

//    while (name_buf.length() > 0) {
//      ch = name_buf.charAt(0);  // Get character at front of name.
//
//      // Test word boundaries.
//      if (word_buf == null) { // At start of new word.
//        word_buf = new StringBuilder();  // Make area for collecting characters.
//      }
//      else if ('A' <= ch && ch <= 'Z') { // Just past end of last word.
//        words.add(word_buf.toString());  // Add finished word to list.
//        word_buf = null;  // Invalidate word buffer,
//        continue;         //   and start fetching next word.
//      }
//
//      name_buf.deleteCharAt(0);  // Remove character from front of name,
//      word_buf.append(ch);       //   and store it in current word buffer.
//    }

    if (word_buf != null)  // One last word buffer.
      words.add(word_buf.toString());  // Append to word list.

    return words;
  }
}
