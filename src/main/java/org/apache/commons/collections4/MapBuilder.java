package org.apache.commons.collections4;

import org.apache.commons.collections4.map.HashedMap;

import java.util.*;

/**
 * Defines an Helper Builder that generates a {@code Map}
 * A Builder class to help decide which type of map to use based on simple requirements.
 * Currently It takes care of only basic types of Maps and can be extended to different types of Maps available in the ecosystem.
 *
 * <pre>{@code
 * Map builderMap = new MapBuilder().setIterationOrder(MapBuilder.KeyOrder.INSERTION_ORDER).build();
 * builderMap.put("A", 1);
 * builderMap.put("X", 24);
 * builderMap.put("B", 2);
 * builderMap.put("Y", 26);
 * }</pre>
 *
 * @author Amita Pradhan
 */
public class MapBuilder {

    private Comparator comparator;
    private KeyOrder iterationOrder;
    private boolean synchronizedMap;
    private boolean immutable;
    private Map data;

    public MapBuilder() {
        comparator = null;
        iterationOrder = KeyOrder.RANDOM;
        synchronizedMap = false;
        immutable = false;
        data = null;
    }

    /*
     Sets the comparator to be used to decide the Iteration order in case of iterationOrder = COMPARATOR_ORDER;
     */
    public MapBuilder setComparator(Comparator comparator) {
        this.comparator = comparator;
        return this;
    }

    /*
    Sets the Iteration order to be used from [RANDOM, NATURAL_ORDER, INSERTION_ORDER, COMPARATOR_ORDER]
     */
    public MapBuilder setIterationOrder(KeyOrder iterationOrder) {
        this.iterationOrder = iterationOrder;
        return this;
    }

    /*
    Since most of the maps are not inherently thread safe , this option provides the option if the map has to be synchronised or not
     */
    public MapBuilder setSynchronizedMap(boolean synchronizedMap) {
        this.synchronizedMap = synchronizedMap;
        return this;
    }

    /*
    Option to create a immutable map from the provided data
     */
    public MapBuilder setImmutable(boolean immutable) {
        this.immutable = immutable;
        return this;
    }

    /*
    Populates the Map with some preexisting data. All the selected conditions will be automatically applied  to the existing data
     */
    public MapBuilder setData(Map data) {
        this.data = data;
        return this;
    }

    /*
    Builder Method which takes care of all the conditions and returns the required Map.
     */
    public Map build() {
        Map map;
        switch (iterationOrder) {
            case NATURAL_ORDER :
            case COMPARATOR_ORDER:
                map = new TreeMap(comparator);
                break;
            case INSERTION_ORDER :
                map = new LinkedHashMap();
                break;
            default:
                map = new HashedMap();
                break;
        }

        if(MapUtils.isNotEmpty(data)) {
            map.putAll(data);
        }

        if(synchronizedMap) {
            map = Collections.synchronizedMap(map);
        }

        if(immutable) {
            map = Collections.unmodifiableMap(map);
        }

        return map;
    }

    enum KeyOrder
    {
        RANDOM, NATURAL_ORDER, INSERTION_ORDER, COMPARATOR_ORDER;
    }
}
