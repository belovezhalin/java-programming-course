package uj.wmii.pwj.map2d;

import java.util.Map;
import java.util.function.Function;
import java.util.Collections;
import java.util.HashMap;

public class Map2DImplementation<R, C, V> implements Map2D<R, C, V> {

    Map<R, Map<C, V>> Map2D;

    Map2DImplementation() {
        Map2D = new HashMap<>();
    }

    @Override
    public V put(R rowKey, C columnKey, V value) {
        if (rowKey == null || columnKey == null) throw new NullPointerException();
        Map<C, V> columnMap = Map2D.computeIfAbsent(rowKey, k -> new HashMap<>());
        return columnMap.put(columnKey, value);
    }

    @Override
    public V get(R rowKey, C columnKey) {
        Map<C, V> columnMap = Map2D.get(rowKey);
        if (columnMap != null) {
            return columnMap.get(columnKey);
        }
        return null;
    }

    @Override
    public V getOrDefault(R rowKey, C columnKey, V defaultValue) {
        Map<C, V> columnMap = Map2D.get(rowKey);
        if (columnMap != null) {
            return columnMap.get(columnKey);
        }
        return defaultValue;
    }

    @Override
    public V remove(R rowKey, C columnKey) {
        Map<C, V> columnMap = Map2D.get(rowKey);
        if (columnMap != null) {
            return columnMap.remove(columnKey);
        }
        return null;
    }

    @Override
    public boolean isEmpty() {
        return Map2D.isEmpty();
    }

    @Override
    public boolean nonEmpty() {
        return !Map2D.isEmpty();
    }

    @Override
    public int size() {
        int size = 0;
        for (Map<C, V> columnMap : Map2D.values()) {
            size += columnMap.size();
        }
        return size;
    }

    @Override
    public void clear() {
        Map2D.clear();
    }

    @Override
    public Map<C, V> rowView(R rowKey) {
        Map<C, V> rowMap = Map2D.get(rowKey);
        if (rowMap != null) {
            return Collections.unmodifiableMap(rowMap);
        }
        return Collections.emptyMap();
    }

    @Override
    public Map<R, V> columnView(C columnKey) {
        Map<R, V> columnMap = new HashMap<>();
        for (R key : Map2D.keySet()) {
            Map<C, V> valueMap = Map2D.get(key);
            if (valueMap.containsKey(columnKey)) {
                columnMap.put(key, valueMap.get(columnKey));
            }
        }
        return Collections.unmodifiableMap(columnMap);
    }

    @Override
    public boolean containsValue(V value) {
        for (Map<C, V> columnMap : Map2D.values()) {
            if (columnMap.containsValue(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsKey(R rowKey, C columnKey) {
        Map<C, V> columnMap = Map2D.get(rowKey);
        if (columnMap != null) {
            return columnMap.containsKey(columnKey);
        }
        return false;
    }

    @Override
    public boolean containsRow(R rowKey) {
        Map<C, V> rowMap = Map2D.get(rowKey);
        return rowMap != null && !rowMap.isEmpty();
    }

    @Override
    public boolean containsColumn(C columnKey) {
        for (Map<C, V> columnMap : Map2D.values()) {
            if (columnMap.containsKey(columnKey)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Map<R, Map<C, V>> rowMapView() {
        Map<R, Map<C, V>> newMap2D = new HashMap<>();
        for (R key : Map2D.keySet()) {
            Map<C, V> valueMap = Map.copyOf(Map2D.get(key));
            newMap2D.put(key, valueMap);
        }
        return Collections.unmodifiableMap(newMap2D);
    }


    @Override
    public Map<C, Map<R, V>> columnMapView() {
        Map<C, Map<R, V>> newColumnMap = new HashMap<>();
        for (R rowKey : Map2D.keySet()) {
            Map<C, V> columnMap = Map2D.get(rowKey);
            for (Map.Entry<C, V> columnEntry : columnMap.entrySet()) {
                C columnKey = columnEntry.getKey();
                V value = columnEntry.getValue();
                newColumnMap.computeIfAbsent(columnKey, k -> new HashMap<>()).put(rowKey, value);
            }
        }
        newColumnMap.replaceAll((key, map) -> Map.copyOf(map));
        return Collections.unmodifiableMap(newColumnMap);
    }

    @Override
    public Map2D<R, C, V> fillMapFromRow(Map<? super C, ? super V> target, R rowKey) {
        if (rowKey == null) {
            throw new NullPointerException();
        }
        Map<C, V> rowMap = Map2D.get(rowKey);
        if (rowMap != null) {
            target.putAll(rowMap);
        }
        return this;
    }

    @Override
    public Map2D<R, C, V> fillMapFromColumn(Map<? super R, ? super V> target, C columnKey) {
        if (columnKey == null) {
            throw new NullPointerException();
        }
        for (R rowKey : Map2D.keySet()) {
            Map<C, V> rowMap = Map2D.get(rowKey);
            if (rowMap != null && rowMap.containsKey(columnKey)) {
                target.put(rowKey, rowMap.get(columnKey));
            }
        }
        return this;
    }

    @Override
    public Map2D<R, C, V> putAll(Map2D<? extends R, ? extends C, ? extends V> source) {
        for (R key : source.rowMapView().keySet()) {
            Map<? extends C, ? extends V> valueMap = source.rowMapView().get(key);
            for (C key2 : valueMap.keySet()) {
                V value = valueMap.get(key2);
                this.put(key, key2, value);
            }
        }
        return this;
    }

    @Override
    public Map2D<R, C, V> putAllToRow(Map<? extends C, ? extends V> source, R rowKey) {
        for (C key : source.keySet()) {
            V value = source.get(key);
            this.put(rowKey, key, value);
        }
        return this;
    }

    @Override
    public Map2D<R, C, V> putAllToColumn(Map<? extends R, ? extends V> source, C columnKey) {
        for (R key : source.keySet()) {
            V value = source.get(key);
            this.put(key, columnKey, value);
        }
        return this;
    }

    @Override
    public <R2, C2, V2> Map2D<R2, C2, V2> copyWithConversion(
            Function<? super R, ? extends R2> rowFunction,
            Function<? super C, ? extends C2> columnFunction,
            Function<? super V, ? extends V2> valueFunction) {
        Map2DImplementation<R2, C2, V2> NewMap2D = new Map2DImplementation<>();
        for (R key : Map2D.keySet()) {
            Map<C, V> valueMap = Map2D.get(key);
            for (C key2 : valueMap.keySet()) {
                V value = valueMap.get(key2);
                NewMap2D.put(rowFunction.apply(key), columnFunction.apply(key2), valueFunction.apply(value));
            }
        }
        NewMap2D.Map2D = Collections.unmodifiableMap(NewMap2D.Map2D);
        return NewMap2D;
    }
}