package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import java.util.HashMap;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Ho Jong Kang
 */
public class FixedRotorTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Rotor rotor;
    private String alpha = UPPER_STRING;

    /** Set the rotor to the one with given NAME and permutation as
     *  specified by the NAME entry in ROTORS, with given NOTCHES. */
    private void setRotor(String name, HashMap<String, String> rotors) {
        rotor = new FixedRotor(name, new Permutation(rotors.get(name), UPPER));
    }

    /* ***** TESTS ***** */

    @Test
    public void testSetting() {
        setRotor("I", NAVALA);
        assertEquals(0, rotor.setting());
    }

    @Test
    public void testAdvance() {
        setRotor("I", NAVALA);
        rotor.advance();
        assertEquals(0, rotor.setting());
    }

    @Test
    public void testNotReflecting() {
        setRotor("I", NAVALA);
        assertFalse(rotor.reflecting());
    }

    @Test
    public void testConvertBackward() {
        setRotor("I", NAVALA);
        assertEquals(22, rotor.convertBackward(1));
    }



}
