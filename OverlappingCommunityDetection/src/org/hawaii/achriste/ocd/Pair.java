
public class Pair implements Comparable<Pair> {

    private Integer first;
    private Integer second;

    public Pair(int first, int second) {
        this.first  = first;
        this.second = second;
    }

    public String toString() {
        return "(" + first + ", " + second + ")";
    }

    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    @Override
    public int compareTo(Pair p) {
        if (first == p.getFirst() && second == p.getSecond()) {
            return 0;
        }
        if (first < p.getFirst()) {
            return -1;
        }
        if (first == p.getFirst() && second < p.getSecond()) {
            return -1;
        }
        return 1;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Pair) {
            Pair otherPair = (Pair) other;
            if (this == other) {
                return true;
            }
            if (first == otherPair.getFirst() && second == otherPair.getSecond()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hashFirst = first != null ? first.hashCode() : 0;
        int hashSecond = second != null ? second.hashCode() : 0;

        return (hashFirst + hashSecond) * hashSecond + hashFirst;
    }
}

