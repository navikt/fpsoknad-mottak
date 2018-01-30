package no.nav.foreldrepenger.oppslag.domain;

import java.util.Objects;

public final class Triple<T1, T2, T3> {

    private final Pair<T1, T2> firstTwo;
    private final T3 third;

    public static <T1, T2, T3> Triple<T1, T2, T3> of(T1 first, T2 second, T3 third) {
        return new Triple<T1, T2, T3>(first, second, third);
    }

    private Triple(T1 first, T2 second, T3 third) {
        this.firstTwo = Pair.of(first, second);
        this.third = third;
    }

    public T1 getFirst() {
        return firstTwo.getFirst();
    }

    public T2 getSecond() {
        return firstTwo.getSecond();
    }

    public T3 getThird() {
        return third;
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstTwo, third);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Triple<?, ?, ?> other = (Triple<?, ?, ?>) obj;
        return Objects.equals(this.firstTwo, other.firstTwo) && Objects.equals(this.third, other.third);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [first=" + firstTwo.getFirst() + ", second=" + firstTwo.getSecond()
                + ", third=" + third + "]";
    }

}
