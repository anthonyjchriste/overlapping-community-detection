package edu.hawaii.achriste.ocdutils;

/**
 * This class represents a generic pair.
 * @author Anthony Christe
 * @param <F> The first item in the pair.
 * @param <S> The second item in the pair.
 */
public class Pair <F, S> {
    /**
     * First item stored in the pair.
     */
    private F first;
    
    /**
     * Second item stored in the pair.
     */
    private S second;
    
    /**
     * Creates a new pair object.
     * @param first The first item stored in the pair.
     * @param second  The second item stored in the pair.
     */
    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }
    
    /**
     * Return the first item stored in the pair.
     * @return The first item stored in the pair.
     */
    public F getFirst() {
        return this.first;
    }
    
    /**
     * Return the second item stored in the pair.
     * @return The second item stored in the pair.
     */
    public S getSecond() {
        return this.second;
    }
    
    /**
     * Set the first item stored in the pair.
     * @param first The first item stored in the pair.
     */
    public void setFirst(F first) {
        this.first = first;
    }
    
    /**
     * Set the second item stored in the pair.
     * @param second The second item stored in the pair.
     */
    public void setSecond(S second) {
        this.second = second;
    }
    
    /**
     * Generates a hashCode for each instance of this class.
     * @return A unique hashCode for each instance of this class.
     */
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + first.hashCode();
        result = 31 * result + second.hashCode();
        return result;
    }
    
    /**
     * Determines if this pair is equal to another pair.
     * @param o The other Pair.
     * @return true if this pair is equal to another pair, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        
        if(!(o instanceof Pair)) {
            return false;
        }
        
        Pair<?, ?> pair = (Pair) o;
        
        return this.first.equals(pair.getFirst()) && this.second.equals(pair.getSecond());
    }
    
    /**
     * A String representation of a Pair in the form of "first second".
     * @return A String representation of a Pair in the form of "first second".
     */
    @Override
    public String toString() {
        return String.format("%s %s", this.first.toString(), this.second.toString());
    }
}
