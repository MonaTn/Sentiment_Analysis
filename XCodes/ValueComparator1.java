package shared;

import java.util.Comparator;
import java.util.Map;

class ValueComparator1 implements Comparator<Object> {

    Map<Object, Double> map;
    
    public void ValueComparator(Map<Object, Double> map) {
        this.map = map;
    }
    public int compare(Object a, Object b) {
        if (map.get(a) >= map.get(b)) {
            return 1;
        } else {
            return -1;
        } 
    }
}