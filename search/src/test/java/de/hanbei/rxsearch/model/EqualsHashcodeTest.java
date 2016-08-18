package de.hanbei.rxsearch.model;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public abstract class EqualsHashcodeTest<T> {

    private T equal;
    private T other;
    private T otherEqual;

    @Before
    public void setUp() throws Exception {
        this.equal = createEqual();
        this.otherEqual = createOtherEqual();
        this.other = createOther();
    }

    protected abstract T createEqual();

    protected abstract T createOther();

    protected abstract T createOtherEqual();

    @Test
    public void testEquals() throws Exception {
        assertThat(equal, is(equal));
        assertThat(equal, is(otherEqual));

        assertThat(equal, not(is(other)));
        assertThat(equal.equals(null), not(is(true)));
        assertThat(equal, not(is(new Object())));
    }

    @Test
    public void testHashCode() throws Exception {
        assertThat(equal.hashCode(), is(equal.hashCode()));
        assertThat(equal.hashCode(), is(otherEqual.hashCode()));
        assertThat(equal.hashCode(), not(is(other.hashCode())));
        assertThat(equal.hashCode(), not(is(new Object().hashCode())));
    }

}