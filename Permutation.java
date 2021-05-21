package enigma;
import java.util.ArrayList;
import java.util.List;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Ho Jong Kang
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        size = _alphabet.size();
        result = new ArrayList<Integer>(size());
        for (int i = 0; i < _alphabet.size(); i += 1) {
            result.add(i);
        }
        String extractedCycle = cycles.replace("(", "")
                .replace(")", "");
        for (String elem : extractedCycle.split(" ")) {
            addCycle(elem);
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        if (cycle.length() == 0) {
            return;
        }
        for (int i = 0; i < (cycle.length() - 1); i += 1) {
            result.set(_alphabet.toInt(cycle.charAt(i)),
                    _alphabet.toInt(cycle.charAt(i + 1)));
        }
        result.set(_alphabet.toInt(cycle.
                charAt(cycle.length() - 1)), _alphabet.toInt(cycle.charAt(0)));
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return size;
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        try {
            return result.get(wrap(p));
        } catch (IndexOutOfBoundsException e) {
            return p;
        }
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        try {
            return result.indexOf(wrap(c));
        } catch (EnigmaException e) {
            return c;
        }
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        return _alphabet.toChar(permute(_alphabet.toInt(p)));
    }

    /** Return the result of applying the inverse of this permutation to C. */
    int invert(char c) {
        try {
            int convert = _alphabet.toInt(c);
            return _alphabet.toChar(invert(convert));
        } catch (EnigmaException e) {
            return c;
        }

    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (int elem : result) {
            if (elem == result.indexOf(elem)) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** Size variable. */
    private int size;

    /** A list of integer that carries result. */
    private List<Integer> result;
}
