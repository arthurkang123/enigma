package enigma;

import static enigma.EnigmaException.*;

/** An Alphabet consisting of the Unicode characters in a certain range in
 *  order.
 *  @author P. N. Hilfinger
 */
class CharacterRangeExtra extends Alphabet {

    /** An alphabet consisting of all characters between FIRST and LAST,
     *  inclusive.
     *  @param order non_sorted String
     *  @param sorted sorted String*/
    CharacterRangeExtra(String order, String sorted) {
        _order = order;
        _first = Character.toUpperCase(sorted.charAt(0));
        _last = Character.toUpperCase(sorted.charAt(sorted.length() - 1));
        if (_first > _last) {
            throw error("empty range of characters");
        }
    }

    @Override
    int size() {
        return _last - _first + 1;
    }

    @Override
    boolean contains(char ch) {
        return ch >= _first && ch <= _last;
    }

    @Override
    char toChar(int index) {
        return _order.charAt(index);
    }

    @Override
    int toInt(char ch) {
        if (!contains(ch)) {
            throw error("character out of range");
        }
        return _order.indexOf(ch);
    }

    /** Range of characters in this Alphabet. */
    private char _first, _last;

    /** String that keeps order. */
    private String _order;

}
