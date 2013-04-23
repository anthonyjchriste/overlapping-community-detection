/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import edu.hawaii.achriste.ocdutils.Pair;

public class PairTest {
    @Test
    public void testEquals() {
        Pair<Integer, Integer> p1 = new Pair<>(1,2);
        assertTrue("reflexive", p1.equals(p1));
        
        Pair<Integer, Integer> p2 = new Pair<>(1, 2);
        assertTrue("symmetric", p1.equals(p2) && p2.equals(p1));
        
        Pair<Integer, Integer> p3 = new Pair<>(1, 2);
        assertTrue("transitive", p1.equals(p2) && p2.equals(p3) && p1.equals(p3));
        
        assertFalse("null", p1.equals(null));
    }
    
    @Test
    public void testHashCode() {
        Pair<Integer, Integer> p1 = new Pair<>(1, 2);
        Pair<Integer, Integer> p2 = new Pair<>(1, 2);
        
        assertTrue("consistency", p1.hashCode() == p1.hashCode());
        assertTrue("consistent with equals", p1.equals(p2) && p1.hashCode() == p2.hashCode());
    }
    
}