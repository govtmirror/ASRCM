package gov.va.med.srcalc.domain.model;

import java.util.Objects;

import javax.persistence.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>A mathematical interval, that is, a set of real numbers between two real
 * numbers. Presents an immutable public API, but also supports package-private
 * reflection-based construction.</p>
 * 
 * <p>Composed of both a lower- and an upper-bound to support all possible
 * ranges, such as:</p>
 * <ul>
 * <li>(0.0, 1.0]</li>
 * <li>[1.0, 3.5)</li>
 * <li>(2.0, &infin;) - note that infinity is represented by +/- MAX</li>
 * </ul>
 * 
 * <p>NaN is not permitted.</p>
 * 
 * <p>Per Effective Java Item 17, this class is marked final because it was not
 * designed for inheritance.</p>
 */
@Embeddable  // An Embeddable instead of a full Entity because ranges do not
             // have their own lifecycle.
public final class NumericalRange implements Comparable<NumericalRange>
{
    /**
     * <p>The highest allowed range value.</p>
     * 
     * <p>We use 1 trillion because it far exceeds real-world use cases and is
     * easy to represent in SQL (unlike {@link Float#MAX_VALUE}).</p>
     */
    public static final float MAX = 1000000000000f;  // 1 trillion
    
    /**
     * The lowest allowed range value. Equal to -MAX.
     */
    public static final float MIN = -MAX;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(NumericalRange.class);

    private float fLowerBound;
    private boolean fLowerInclusive;
    private float fUpperBound;
    private boolean fUpperInclusive;
    
    /**
     * For reflection-based construction only. Business code should use
     * {@link #NumericalRange(float, boolean, float, boolean)}.
     */
    NumericalRange()
    {
        // Set invalid sentinel values to ensure clients call the setters.
        fLowerBound = Float.NaN;
        fUpperBound = Float.NaN;
    }
    
    /**
     * Constructs an instance.
     * @throws IllegalArgumentException if NaN is given for the lower or upper
     * bound.
     */
    public NumericalRange(
            float lowerBound,
            boolean lowerInclusive,
            float upperBound,
            boolean upperInclusive)
    {
        // Use bounds setters to validate the arguments
        setLowerBound(lowerBound);
        fLowerInclusive = lowerInclusive;
        setUpperBound(upperBound);
        fUpperInclusive = upperInclusive;
    }
    
    /**
     * Returns the lower bound of this range.
     */
    public float getLowerBound()
    {
        return fLowerBound;
    }
    
    /**
     * For reflection-based construction only. Sets the lower bound. Will limit
     * the given bound per the range specified in {@link #limit(float)}.
     * @throws IllegalArgumentException if NaN is given
     */
    void setLowerBound(final float lowerBound)
    {
        if (Float.isNaN(lowerBound))
        {
            throw new IllegalArgumentException("NaN is not a legal lower bound");
        }

        fLowerBound = limit(lowerBound);
    }
    
    /**
     * Returns if true if the lower bound is inclusive, false otherwise.
     */
    public boolean isLowerInclusive()
    {
        return fLowerInclusive;
    }
    
    /**
     * For reflection-based construction only.
     */
    void setLowerInclusive(boolean lowerInclusive)
    {
        fLowerInclusive = lowerInclusive;
    }
    
    /**
     * Returns the upper bound of this range.
     */
    public float getUpperBound()
    {
        return fUpperBound;
    }
    
    /**
     * For reflection-based construction only. Sets the upper bound. Will limit
     * the given bound per the range specified in {@link #limit(float)}.
     * @throws IllegalArgumentException if NaN is given
     */
    void setUpperBound(final float upperBound)
    {
        if (Float.isNaN(upperBound))
        {
            throw new IllegalArgumentException("NaN is not a legal upper bound");
        }

        fUpperBound = limit(upperBound);
    }
    
    /**
     * Returns true if the upper bound is inclusive, false otherwise.
     */
    public boolean isUpperInclusive()
    {
        return fUpperInclusive;
    }
    
    /**
     * For reflection-based construction only.
     */
    void setUpperInclusive(boolean upperInclusive)
    {
        fUpperInclusive = upperInclusive;
    }
    
    /**
     * Limits the given value to be within [-MIN,MAX]. If f > MAX, returns MAX.
     * If f < MIN, returns MIN.
     * @param f the value to limit
     * @return the limited value
     */
    public static float limit(final float f)
    {
        if (f > MAX)
        {
            LOGGER.debug("Limiting {} to max {}.", f, MAX);
            return MAX;
        }
        else if (f < MIN)
        {
            LOGGER.debug("Limiting {} to min {}.", f, MIN);
            return MIN;
        }
        else
        {
            return f;
        }
    }
    
    /**
     * Returns true if and only if the given value is above the lower bound
     * (considering whether the bound is inclusive).
     */
    protected boolean isValueWithinLower(final float value)
    {
        return isLowerInclusive() ?
                (value >= getLowerBound()) :
                (value > getLowerBound());
    }
    
    /**
     * Returns true if and only if the given value is below the upper bound
     * (considering whether the bound is inclusive).
     */
    protected boolean isValueWithinUpper(final float value)
    {
        return isUpperInclusive() ?
                (value <= getUpperBound()) :
                (value < getUpperBound());
    }
    
    /**
     * Returns true if {@code value} is within this numerical range, false otherwise.
     * @param value the value to test
     */
    public boolean isValueInRange(final float value)
    {
        return isValueWithinLower(value) && isValueWithinUpper(value);
    }
    
    /**
     * Checks the given value against this range. If the value is in the range,
     * returns the value. Otherwise, throws an {@link InvalidValueException}.
     * @return the valid value for convenience
     * @throws ValueTooLowException if the value is below the minimum
     * @throws ValueTooHighException if the value is above the minimum
     */
    public float checkValue(final float value)
            throws ValueTooLowException, ValueTooHighException
    {
        if (fLowerInclusive)
        {
            if (value < fLowerBound)
            {
                throw new ValueTooLowException(
                        ValueTooLowException.ERROR_CODE_INCLUSIVE,
                        "value must be greater than or equal to " + fLowerBound);
            }
        }
        else
        {
            if (value <= fLowerBound)
            {
                throw new ValueTooLowException(
                        ValueTooLowException.ERROR_CODE_EXCLUSIVE,
                        "value must be greater than " + fLowerBound);
            }
        }

        if (fUpperInclusive)
        {
            if (value > fUpperBound)
            {
                throw new ValueTooHighException(
                        ValueTooHighException.ERROR_CODE_INCLUSIVE,
                        "value must be less than or equal to " + fUpperBound);
            }
        }
        else
        {
            if (value >= fUpperBound)
            {
                throw new ValueTooHighException(
                        ValueTooHighException.ERROR_CODE_EXCLUSIVE,
                        "value must be less than " + fUpperBound);
            }
        }
        return value;
    }
    
    /**
     * Returns the interval in mathematical notation. (See class Javadocs.)
     */
    @Override
    public String toString()
    {
        return String.format(
                "%s%s, %s%s",
                (isLowerInclusive() ? "[" : "("),
                getLowerBound(),
                getUpperBound(),
                (isUpperInclusive() ? "]" : ")"));
    }
    
    /**
     * Returns true if the provided object is another NumericalRange with the
     * same bounds, considering inclusive/exclusive.
     */
    @Override
    public boolean equals(final Object obj)
    {
        if (obj instanceof NumericalRange)
        {
            final NumericalRange other = (NumericalRange)obj;
            return
                    new Float(this.fLowerBound).equals(other.fLowerBound) &&
                    this.fLowerInclusive == other.fLowerInclusive &&
                    new Float(this.fUpperBound).equals(other.fUpperBound) &&
                    this.fUpperInclusive == other.fUpperInclusive;
        }
        else
        {
            return false;
        }
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash(
                fLowerBound, fLowerInclusive, fUpperBound, fUpperInclusive);
    }
    
    /**
     * Orders {@link NumericalRange}s by their lower bounds, then upper bounds.
     */
    @Override
    public int compareTo(NumericalRange other)
    {
        // Note: casting NaN to int returns 0.
        final int lowerSignum =
                (int)Math.signum(this.fLowerBound - other.fLowerBound);
        if (lowerSignum != 0)
        {
            return lowerSignum;
        }
        else
        {
            return (int)Math.signum(this.fUpperBound - other.fUpperBound);
        }
    }
}
