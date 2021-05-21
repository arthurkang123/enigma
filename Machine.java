package enigma;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;

/**
 * Class that represents a complete enigma machine.
 *
 * @author Ho Jong Kang
 */
class Machine {

    /**
     * Common alphabet of my rotors.
     */
    private final Alphabet _alphabet;

    /**
     * Number of rotors.
     */
    private int _numRotors;

    /**
     * Number of pawls.
     */
    private int _pawls;

    /**
     * List of Rotors that keeps all rotors.
     */
    private List<Rotor> _allRotors;

    /**
     * List of Rotors that keeps my rotors.
     */
    private List<Rotor> _myRotors;

    /**
     * Plugboard being used.
     */
    private Permutation _plugboard;

    /**
     * A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     * and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     * available rotors.
     */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        this._numRotors = numRotors;
        this._pawls = pawls;
        this._myRotors = new ArrayList<>();
        this._allRotors = new ArrayList<>();
        this._allRotors.addAll(allRotors);
    }

    /**
     * Return the number of rotor slots I have.
     */
    int numRotors() {
        return _numRotors;
    }

    /**
     * Return the number pawls (and thus rotating rotors) I have.
     */
    int numPawls() {
        return _pawls;
    }

    /**
     * Set my rotor slots to the rotors named ROTORS from my set of
     * available rotors (ROTORS[0] names the reflector).
     * Initially, all rotors are set at their 0 setting.
     */
    void insertRotors(String[] rotors) {
        _myRotors.clear();
        for (String rotor : rotors) {
            for (Rotor availrotor : _allRotors) {
                if (rotor.equals(availrotor.name().toUpperCase())) {
                    if (_myRotors.contains(availrotor)) {
                        throw new EnigmaException("Duplicate rotor name");
                    }
                    _myRotors.add(availrotor);
                }
            }
        }
        if (_myRotors.size() != rotors.length) {
            throw new EnigmaException("Bad rotor name");
        }
    }

    /**
     * Set my rotors according to SETTING, which must be a string of
     * numRotors()-1 upper-case letters. The first letter refers to the
     * leftmost rotor setting (not counting the reflector).
     */
    void setRotors(String setting) {
        if (!_myRotors.get(0).reflecting()) {
            throw new EnigmaException("Reflector in wrong place");
        }
        for (int i = 1; i < _myRotors.size(); i += 1) {
            if (i < numRotors() - numPawls()) {
                if (_myRotors.get(i).rotates()) {
                    throw new EnigmaException("Wrong number of arguments");
                }
            } else {
                if (!_myRotors.get(i).rotates()) {
                    throw new EnigmaException("Wrong number of arguments");
                }
            }
            _myRotors.get(i).set(_alphabet.toInt(setting.charAt(i - 1)));
        }

    }

    /**
     * Set the plugboard to PLUGBOARD.
     */
    void setPlugboard(Permutation plugboard) {
        this._plugboard = plugboard;
    }

    /**
     * Returns the result of converting the input character C (as an
     * index in the range 0..alphabet size - 1), after first advancing
     * <p>
     * the machine.
     */
    int convert(int c) {
        Set<Rotor> move = new HashSet<>();
        for (int i = _numRotors - _pawls; i < _numRotors - 1; i += 1) {
            if (_myRotors.get(i + 1).atNotch()
                    || move.contains(_myRotors.get(i - 1))) {
                move.add(_myRotors.get(i));
                if (_myRotors.get(i).atNotch()) {
                    move.add(_myRotors.get(i - 1));
                }
            }
        }
        move.add(_myRotors.get(_numRotors - 1));

        for (Rotor elem : move) {
            elem.advance();
        }

        try {
            int result = _plugboard.permute(c);
            for (int i = _numRotors; i > 0; i -= 1) {
                Rotor forward = _myRotors.get(i - 1);
                result = forward.convertForward(result);
            }
            for (int j = 1; j < _myRotors.size(); j += 1) {
                Rotor backward = _myRotors.get(j);
                result = backward.convertBackward(result);
            }
            return _plugboard.permute(result);
        } catch (NullPointerException e) {
            int result = c;
            for (int i = _numRotors; i > 0; i -= 1) {
                Rotor forward = _myRotors.get(i - 1);
                result = forward.convertForward(result);
            }
            for (int j = 1; j < _numRotors; j += 1) {
                Rotor backward = _myRotors.get(j);
                result = backward.convertBackward(result);
            }
            return result;
        }

    }

    /**
     * Returns the encoding/decoding of MSG, updating the state of
     * the rotors accordingly.
     */
    String convert(String msg) {
        String result = "";
        for (int i = 0; i < msg.length(); i += 1) {
            char convert = _alphabet.
                    toChar(convert(_alphabet.toInt(msg.charAt(i))));
            result += convert;
        }
        return result;
    }
}
