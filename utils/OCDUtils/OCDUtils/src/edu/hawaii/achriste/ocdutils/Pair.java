package edu.hawaii.achriste.ocdutils;


public class Pair <F, S> {
    private F first;
    private S second;
    
    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }
    
    public F getFirst() {
        return this.first;
    }
    
    public S getSecond() {
        return this.second;
    }
    
    public void setFirst(F first) {
        this.first = first;
    }
    
    public void setSecond(S second) {
        this.second = second;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + first.hashCode();
        result = 31 * result + second.hashCode();
        return result;
    }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        
        if(!(o instanceof Pair)) {
            return false;
        }
        
        Pair pair = (Pair) o;
        
        return this.first.equals(pair.getFirst()) && this.second.equals(pair.getSecond());
    }
    
    @Override
    public String toString() {
        return String.format("%s %s", this.first.toString(), this.second.toString());
    }
}
