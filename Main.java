package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;
import java.util.NoSuchElementException;

import static enigma.EnigmaException.error;

/**
 * Enigma simulator.
 *
 * @author Ho Jong Kang
 */
public final class Main {

    /**
     *  ArrayList of all rotors.
     */
    private List<Rotor> _allRotors = new ArrayList<>();
    /**
     * Alphabet used in this machine.
     */
    private Alphabet _alphabet;
    /**
     * Source of input messages.
     */
    private Scanner _input;
    /**
     * Source of machine configuration.
     */
    private Scanner _config;
    /**
     * File for encoded/decoded messages.
     */
    private PrintStream _output;

    /**
     * Check ARGS and open the necessary files (see comment on main).
     */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /**
     * Process a sequence of encryptions and decryptions, as
     * specified by ARGS, where 1 <= ARGS.length <= 3.
     * ARGS[0] is the name of a configuration file.
     * ARGS[1] is optional; when present, it names an input file
     * containing messages.  Otherwise, input comes from the standard
     * input.  ARGS[2] is optional; when present, it names an output
     * file for processed messages.  Otherwise, output goes to the
     * standard output. Exits normally if there are no errors in the input;
     * otherwise with code 1.
     */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /**
     * Sorting string. Source from:
     * https://www.geeksforgeeks.org/sort-a-string-in-java-2-different-ways/
     * @param inputString String that is given
     * @return returns new string that is sorted
     */
    private static String sortString(String inputString) {
        char[] temp = inputString.toCharArray();
        Arrays.sort(temp);
        return new String(temp);
    }

    /**
     * Return a Scanner reading from the file named NAME.
     */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /**
     * Return a PrintStream writing to the file named NAME.
     */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /**
     * Configure an Enigma machine from the contents of configuration
     * file _config and apply it to the messages in _input, sending the
     * results to _output.
     */
    private void process() {
        Machine enigma = readConfig();

        while (_input.hasNext()) {
            String next = _input.nextLine().toUpperCase();
            if (next.equals("")) {
                _output.println();
                continue;
            }
            if (!next.contains("*")) {
                throw new EnigmaException("No config");
            }
            setUp(enigma, next);

            if (_input.hasNextLine()) {
                next = (_input.nextLine()).toUpperCase();
            } else {
                break;
            }
            while (true) {
                if (next.contains("*")) {
                    break;
                }
                String result = enigma.convert(next.replaceAll(" ", ""));
                printMessageLine(result);
                if (_input.hasNext("\\s*[*].*")) {
                    break;
                }
                if (_input.hasNextLine()) {
                    next = (_input.nextLine()).toUpperCase();
                } else {
                    break;
                }

            }
        }

    }

    /**
     * Return an Enigma machine configured from the contents of configuration
     * file _config.
     */
    private Machine readConfig() {
        try {
            String range = _config.next();
            if (range.length() < 2) {
                throw new EnigmaException("Bad config");
            }
            if (range.charAt(1) == '-') {
                _alphabet = new CharacterRange(range.charAt(0),
                        range.charAt(2));
            } else {
                String sortedrange = sortString(range);
                _alphabet = new CharacterRangeExtra(range, sortedrange);
            }

            int numRotors = _config.nextInt();
            int pawls = _config.nextInt();
            while (_config.hasNext()) {
                _allRotors.add(readRotor());
            }
            return new Machine(_alphabet, numRotors, pawls, _allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /**
     * Return a rotor, reading its description from _config.
     */
    private Rotor readRotor() {
        try {
            String name = _config.next();
            String perm = "";
            String next = _config.next();
            char first = next.charAt(0);
            String notches = next.substring(1);
            while (_config.hasNext("\\s*[(].+[)]\\s*")) {
                next = _config.next().replaceAll("[)][(]", ") (");
                perm = perm + next + " ";
            }
            if (first == 'R') {
                return new Reflector(name, new Permutation(perm, _alphabet));
            } else if (first == 'N') {
                return new FixedRotor(name, new Permutation(perm, _alphabet));
            } else {
                return new MovingRotor(name,
                        new Permutation(perm, _alphabet), notches);
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /**
     * Set M according to the specification given on SETTINGS,
     * which must have the format specified in the assignment.
     */
    private void setUp(Machine M, String settings) {
        String[] rotors = new String[M.numRotors()];
        Scanner scan = new Scanner(settings);
        String temp = "";
        if (scan.hasNext()) {
            scan.next();
            for (int i = 0; i < rotors.length; i += 1) {
                rotors[i] = scan.next();
            }
            M.insertRotors(rotors);
            M.setRotors(scan.next());
            while (scan.hasNext()) {
                temp = temp + scan.next() + " ";
            }
            M.setPlugboard(new Permutation(temp, _alphabet));
        }
    }

    /**
     * Print MSG in groups of five (except that the last group may
     * have fewer letters).
     */
    private void printMessageLine(String msg) {
        int remainder = msg.length() % 5;
        for (int i = 0; i < msg.length() - remainder; i += 5) {
            _output.print(msg.substring(i, i + 5) + " ");
        }
        _output.println(msg.substring((msg.length() - remainder),
                msg.length()));
    }
}
