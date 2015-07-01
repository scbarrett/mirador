/* --------------------------------------------------------------------------+
   InvocationParser.java - Argument and option handler for a program that is
     invoked from the command line.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   Common parser for command line invocations. Command arguments and options
   are routed to their own hash maps for later interpretation.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Verifies and parses command line arguments and options submitted to the
 * program. Displays usage information if requested, or if the invocation
 * command is found wanting.<p>
 *
 * As with standard UNIX conventions, options are asserted before any arguments.
 * The option switch is a single dash '-', and options may be grouped.
 * Additional parameters or details should immediately follow their associated
 * switch or argument.<p>
 *
 * Invocation syntax is expected to conform to one of the following patterns,
 * where <i>name</i> is the name of either a class file or a jar file.<pre>
 *   From class:  java name [-options] required_args [optional_args]
 *   From a jar:  java -jar name.jar [-options] required_args [optional_args]</pre>
 *
 * @since   v0.19 - Mar 12, 2010
 * @author  Stephen Barrett
 */
public class InvocationParser {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**
   * Constructs a verifier and parser to operate on the passed command strings.
   *
   * @param  args  Command line arguments and options as strings.
   * @throws  ParseException if an error is encountered, or help is requested.
   * @throws IOException
   */
  public InvocationParser(String[] args) throws ParseException, IOException {
    options_.put("config", "mirador.cfg");  // Default configuration file.
    parseCommandLine(args);
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  /**
   * Gives the value of a passed argument by its human readable name, which
   * doubles as the key for its map entry. An argument's value consists of
   * parameters passed in with it at program invocation time, <i>and</i> any
   * other information that may become associated with it as the program runs.
   *
   * @param  key  Name of argument to check for.
   * @return  Details associated with the argument, null if not passed in.
   */
  public String argumentValue(String key) {
    return arguments_.get(key);
  }


  public void argumentValue(String key, String value) {
    arguments_.put(key, value);
  }


  /**
   * Gives the value of a passed option by its human readable name, which
   * doubles as the key for its map entry. An option's value consists of
   * parameters passed in with it at program invocation time, <i>and</i> any
   * other information that may become associated with it as the program runs.
   *
   * @param  key  Name of option to check for.
   * @return  Details associated with the option, null if not passed in.
   */
  public String optionValue(String key) {
    return options_.get(key);
  }


  public void optionValue(String key, String value) {
    options_.put(key, value);
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ predicates
  /**
   * Checks passed arguments for presence of a particular argument by its human
   * readable name, which doubles as the key for its map entry.
   *
   * @param  to_test  Name of argument to check for.
   * @return  true = argument was passed in, false = argument was not passed in
   */
  public boolean isArgumentPassed(String to_test) {
    return arguments_.containsKey(to_test);
  }


  /**
   * Checks passed options for presence of a particular option by its human
   * readable name, which doubles as the key for its map entry.
   *
   * @param  to_test  Name of option to check for.
   * @return  true = option was passed in, false = option was not passed in
   */
  public boolean isOptionPassed(String to_test) {
    return options_.containsKey(to_test);
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ operations
  /**
   * Sends basic syntax for program invocation to the console.
   */
  static public void displayUsage() {
    System.out.println("\nUsage: java Mirador [OPTION]"
        + " [BASE_MODEL] LEFT_MODEL RIGHT_MODEL");

    System.out.println("Note: BASE_MODEL is required for three-way merging.");

    System.out.println("\nGeneral options:");
    System.out.println(" -?        display this help and exit");
    System.out.println(" -v        print version information and exit");
    System.out.println(" -c=FILE   specify configuration file"
        + " - default=mirador.cfg");
    System.out.println(" -d        enable printing of debug messages");

    System.out.println("\nElement matching:");
    System.out.println(" -ml       select left as master side");
    System.out.println(" -mr       select right as master side");
    System.out.println(" -mt=VAL   set element matching threshold");
    System.out.println(" -mp=FILE  specify previous match file");
    System.out.println(" -md[=VAL] use 'by dependency' matching startegy");
    System.out.println(" -me[=VAL] use 'by ECL' matching startegy");
    System.out.println(" -mh[=VAL] use 'by history' matching startegy");
    System.out.println(" -mi[=VAL] use 'by ID' matching startegy");
    System.out.println(" -mn[=VAL] use 'by name' matching startegy");
    System.out.println(" -ms[=VAL] use 'by structure' matching startegy");
    System.out.println(" -mt=VAL   set element matching threshold");
    System.out.println(" -u1=FILE  use specified user matching strategy #1");
    System.out.println(" -u2=FILE  use specified user matching strategy #2");
    System.out.println(" -tm=FILE  specify element match decision table");

    System.out.println("\nModel merging:");
    System.out.println(" -2\t   perform two-way merge");
    System.out.println(" -3\t   perform three-way merge - default");
    System.out.println(" -a\t   run in automatic mode");
    System.out.println(" -i\t   run in interactive mode - default");
    System.out.println(" -tc=FILE  specify conflict detection decision table");
    System.out.println(" -tr=FILE  specify conflict resolution decision table");
    System.out.println();
  }


  /**
   * Sends program version information to the console.
   */
  static public void displayVersion() {
    System.out.println("\nMirador Model Merger v" + Constants.VERSION
        + "  -  " + Constants.ORGANIZATION + "\n(" + Constants.CONTACT + ")\n");
  }


  public void loadConfigFile() throws ParseException, IOException {
    String name = options_.get("config_file");
    options_.remove("config_file");
    File file = new File(name);

    if (file.canRead()) {
      List<String> words = new ArrayList<String>();
      FileReader fin = new FileReader(file);

      int chi = fin.read();
      StringBuffer word = new StringBuffer();
      boolean in_comment = false;

      while (chi != -1) {
        Character ch = (char) chi;
        chi = fin.read();

        if (Character.isWhitespace(ch)) {
          if (ch == 0x0A)
            in_comment = false;

          if (word.length() > 0) {
            words.add(word.toString());
            word.delete(0, word.length());
          }

          continue;
        }

        if (ch == '/' && chi == '/')
          in_comment = true;

        if (in_comment) {
          int len = word.length();
          if (len > 0 && word.charAt(len - 1) == '/')
            word.deleteCharAt(len - 1);

          continue;
        }

        word.append(ch);
      }

      if (word.length() > 0)
        words.add(word.toString());

      String[] args = words.toArray(new String[] {});
      parseCommandLine(args);
    }
    else
      throw new ParseException("Unable to read from file " + file, 0);

  }


  /**
   * Breaks out program arguments and their details from the passed command line
   * strings array. Tokens are stored in the argument map data structure.
   *
   * @param  args  Command line arguments and options as strings.
   * @param  first_arg  Position of first non-option in string array.
   * @throws  ParseException if wrong number of arguments are present.
   */
  public void parseArguments(String[] args, int first_arg)
      throws ParseException {
    // --------------------------------------------------------------------
    // Parse arguments.
    int arg_idx = first_arg;
    int arg_sz = args.length - first_arg;

    // Guard against missing arguments.
    if (isOptionPassed("2-way")) {
      if (arg_sz >= 2) {
        arguments_.put("base_model", null);
        arguments_.put("left_model", args[arg_idx++]);
        arguments_.put("right_model", args[arg_idx++]);
      }
      else if (arguments_.size() < 2)
        throw new ParseException("Two-way merging requires two models", 0);
    }
    else {
      if (arg_sz >= 3) {
        arguments_.put("base_model", args[arg_idx++]);
        arguments_.put("left_model", args[arg_idx++]);
        arguments_.put("right_model", args[arg_idx++]);
      }
      else if (arguments_.size() < 3)
        throw new ParseException("Three-way merging requires three models", 0);
    }




//    try
//    {
//      if (new Double(cutoff) > 1)  // Should be a double <= 1.
//      {
//        throw new ParseException("\"" + cutoff
//          + "\" must be less than or equal to 1.", arg_num);
//      }
//    }
//    catch (NumberFormatException e)
//    { // Argument is not a double. Display message and/or help.
//      throw new ParseException("\"" + cutoff
//        + "\" must be a double.", arg_num);
//    }
//
//    getArguments().put("cutoff", cutoff);  // Add value to map.
//
//
//    // Guard against no more arguments.
//    if (args.length <= arg_num)
//      getArguments().put("runs", "20");  // Add default value to map.
//    else
//    { // Determine intervals.
//      String runs = args[arg_num++];
//
//      try
//      {
//        new Integer(runs);  // Should be an integer.
//      }
//      catch (NumberFormatException e)
//      { // Argument is not an integer. Display message and/or help.
//        throw new ParseException("\"" + runs
//          + "\" must be an integer.", arg_num);
//      }
//
//      getArguments().put("runs", runs);  // Add value to map.
//    }
//
//
//    // Guard against no more arguments.
//    if (args.length <= arg_num)
//      getArguments().put("intervals", "1000");  // Add default value to map.
//    else
//    { // Determine intervals.
//      String intervals = args[arg_num++];
//
//      try
//      {
//        new Integer(intervals);  // Should be an integer.
//      }
//      catch (NumberFormatException e)
//      { // Argument is not an integer. Display message and/or help.
//        throw new ParseException("\"" + intervals
//          + "\" must be an integer.", arg_num);
//      }
//
//      getArguments().put("intervals", intervals);  // Add value to map.
//    }
  }


  public void parseCommandLine(String[] args) throws ParseException,
      IOException {
    try {
      int first_arg = parseOptions(args);  // Get leading options.
      parseArguments(args, first_arg);  // Examine arguments that remain.
    }
    catch (ParseException ex) {
      // Command line parsing ended prematurely. Display message and/or help.
      switch (ex.getErrorOffset()) {
        case -1:
          displayUsage();
        break;

        case -2:
          displayVersion();
        break;

        case -3:
          System.err.println("\n\t!!! Parsing error " + ex.getErrorOffset()
              + ": " + ex.getMessage() + " !!!\n");
        break;

        default:
          System.err.println("\n\t!!! Parsing error: "
              + ex.getMessage() + " !!!");
          displayUsage();
      }
      throw ex;
    }
  }


  /**
   * Breaks out program options and their details from the passed command line
   * strings array. Tokens are stored in the option map data structure.
   *
   * @param  args  Command line arguments and options as strings.
   * @throws  ParseException if an error is encountered, or help is requested.
   * @throws IOException
   */
  public int parseOptions(String[] args) throws ParseException, IOException {
    int first_arg = 0;  // First non-option.

    // Examine leading command strings for single character options.
    for (String arg : args) {
      if (arg.charAt(0) == '-') { // Option switch character found.
        ++first_arg;  // Mark start of arguments.

        // Examine characters of command string: store option, or show help.
        int len = arg.length();
        String rhs;
        int pos;
        char ch;

        for (int j = 1; j < len; ++j) {
          switch (arg.charAt(j)) {
            case '?':  // Help requested.
              throw new ParseException("Usage information requested", -1);

            case '2':
              options_.put("2-way", "true");
            break;

            case '3':
              options_.remove("2-way");
            break;

            case 'a':
              options_.put("auto_mode", "true");
            break;

            case 'c':
              pos = arg.indexOf('=', 1);
              if (pos >= 1 && len - (pos + 1) > 0) {
                rhs = arg.substring(pos + 1).trim();
                options_.put("config_file", rhs);
                loadConfigFile();
                j = len;
              }
              else
                throw new ParseException("Incorrect syntax for option -c", 0);
            break;

            case 'd':  // Debug mode console output.
              options_.put("debug_mode", "true");
            break;

            case 'i':
              options_.remove("auto_mode");
            break;

            case 'm':
              pos = arg.indexOf('=', 1);
              if (pos >= 1 && len - (pos + 1) > 0)
                rhs = arg.substring(pos + 1).trim();
              else
                rhs = null;

              ch = arg.charAt(++j);
              switch (ch) {
                case 'd':
                  options_.put("by_dependency", rhs);
                break;

                case 'e':
                  options_.put("by_ecl", rhs);
                break;

                case 'h':
                  options_.put("by_history", rhs);
                break;

                case 'i':
                  options_.put("by_id", rhs);
                break;

                case 'l':
                  options_.put("master_side", "left");
                break;

                case 'n':
                  options_.put("by_name", rhs);
                break;

                case 's':
                  options_.put("by_structure", rhs);
                break;

                case 'p':
                  options_.put("previous_file", rhs);
                break;

                case 'r':
                  options_.put("master_side", "right");
                break;

                case 't':
                  options_.put("match_threshold", rhs);
                break;

                default:
                  throw new ParseException("Incorrect syntax for option -m", 0);
              }

              j = len;
            break;

            case 't':
              pos = arg.indexOf('=', 1);
              if (pos >= 1 && len - (pos + 1) > 0)
                rhs = arg.substring(pos + 1).trim();
              else
                rhs = null;

              ch = arg.charAt(++j);
              switch (ch) {
                case 'c':
                  options_.put("conflict_table", rhs);
                break;

                case 'm':
                  options_.put("match_table", rhs);
                break;

                case 'r':
                  options_.put("resolve_table", rhs);
                break;

                default:
                  throw new ParseException("Incorrect syntax for option -t", 0);
              }

              j = len;
            break;

            case 'u':
              pos = arg.indexOf('=', 1);
              if (pos >= 1 && len - (pos + 1) > 0)
                rhs = arg.substring(pos + 1).trim();
              else
                rhs = null;

              ch = arg.charAt(++j);
              switch (ch) {
                case '1':
                  options_.put("by_user1", rhs);
                break;

                case '2':
                  options_.put("by_user2", rhs);
                break;

                default:
                  throw new ParseException("Incorrect syntax for option -u", 0);
              }

              j = len;
            break;

            case 'v':  // Version information requested.
              throw new ParseException("Version information requested", -2);

            default:  // "Uncaught" options fall through to here.
              throw new ParseException("Unknown option '" + arg.charAt(j)
                  + "'", 0);
          }
        }
      }
    }

    return first_arg;
  }


  // Instance data ----------------------------------------------------------
  private Map<String, String> arguments_ = new HashMap<String, String>();
  private Map<String, String> options_ = new HashMap<String, String>();
  // End instance data ------------------------------------------------------
}
