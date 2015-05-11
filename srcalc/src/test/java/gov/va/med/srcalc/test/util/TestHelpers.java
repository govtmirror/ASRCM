package gov.va.med.srcalc.test.util;

import static org.junit.Assert.*;

import com.google.common.base.Strings;

/**
 * Helper methods useful in automated tests.
 */
public class TestHelpers
{
    /**
     * Verifies the contract of {@link Comparable#compareTo(Object)} given three
     * objects of the same type.
     * @param lesser <code>lesser</code> &lt; <code>middle</code> &lt; <code>greater</code>
     * @param middle see above
     * @param greater see above
     */
    public static <T extends Comparable<T>> void verifyCompareToContract(
            T lesser, T middle, T greater)
    {
        assertEquals("lesser should equal itself", 0, lesser.compareTo(lesser));
        assertTrue("lesser should be less than middle", lesser.compareTo(greater) < 0);
        assertTrue("lesser should be less than greater", lesser.compareTo(middle) < 0);

        assertEquals("middle should equal itself", 0, middle.compareTo(middle));
        assertTrue("middle should be greater than lesser", middle.compareTo(lesser) > 0);
        assertTrue("middle should be less than greater", middle.compareTo(greater) < 0);

        assertEquals("greater should equal itself", 0, greater.compareTo(greater));
        assertTrue("greater should be greater than lesser", greater.compareTo(lesser) > 0);
        assertTrue("greater should be greater than middle", greater.compareTo(middle) > 0);
        
    }
    
    /**
     * Returns a String with exactly the given length. Its contents are
     * otherwise arbitrary
     * @param length the length of the returned string
     */
    public static String stringOfLength(final int length)
    {
        return Strings.padEnd("", length, 'X');
    }
}
