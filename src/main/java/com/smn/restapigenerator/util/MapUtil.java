package com.smn.restapigenerator.util;

import java.util.Map;

public class MapUtil {
    
    /**
     * Dumps the contents of a Map to a string representation.
     * Recursively handles nested maps with proper indentation.
     * 
     * @param map the Map to dump
     * @return string representation of the map contents
     */
    public static String dumpMap(Map<?, ?> map) {
        if (map == null) {
            return "null";
        }
        if (map.isEmpty()) {
            return "{}";
        }
        
        StringBuilder sb = new StringBuilder();
        dumpMap(map, sb, 0);
        return sb.toString();
    }
    
     /**
     * Dumps the contents of a Map to a compact single-line string representation.
     * Recursively handles nested maps without formatting.
     * 
     * @param map the Map to dump
     * @return compact string representation of the map contents
     */
    public static String dumpMapCompact(Map<?, ?> map) {
        if (map == null) {
            return "null";
        }
        if (map.isEmpty()) {
            return "{}";
        }
        
        StringBuilder sb = new StringBuilder();
        dumpMapCompact(map, sb);
        return sb.toString();
    }
    
     /**
     * Recursively dumps the contents of a Map with specified indentation.
     * 
     * @param map the Map to dump
     * @param sb the StringBuilder to append to
     * @param indentLevel the current indentation level
     */
    private static void dumpMap(Map<?, ?> map, StringBuilder sb, int indentLevel) {
        if (map == null) {
            sb.append("null");
            return;
        }
        
        if (map.isEmpty()) {
            sb.append("{}");
            return;
        }
        
        sb.append("{\n");
        
        int entryCount = 0;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            // Add indentation
            for (int i = 0; i <= indentLevel; i++) {
                sb.append("  ");
            }
            
            // Add key
            Object key = entry.getKey();
            sb.append(formatValue(key)).append(" = ");
            
            // Add value (with recursive handling for Maps)
            Object value = entry.getValue();
            if (value instanceof Map) {
                dumpMap((Map<?, ?>) value, sb, indentLevel + 1);
            } else {
                sb.append(formatValue(value));
            }
            
            // Add comma if not the last entry
            entryCount++;
            if (entryCount < map.size()) {
                sb.append(",");
            }
            sb.append("\n");
        }
        
        // Add closing brace with proper indentation
        for (int i = 0; i < indentLevel; i++) {
            sb.append("  ");
        }
        sb.append("}");
    }
    
    /**
     * Formats a value for display, handling null values and strings.
     * 
     * @param value the value to format
     * @return formatted string representation
     */
    private static String formatValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String) {
            return "\"" + value + "\"";
        }
        return value.toString();
    }
    
    /**
     * Recursively dumps the contents of a Map in compact format.
     * 
     * @param map the Map to dump
     * @param sb the StringBuilder to append to
     */
    private static void dumpMapCompact(Map<?, ?> map, StringBuilder sb) {
        if (map == null) {
            sb.append("null");
            return;
        }
        
        if (map.isEmpty()) {
            sb.append("{}");
            return;
        }
        
        sb.append("{");
        
        int entryCount = 0;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object key = entry.getKey();
            sb.append(formatValue(key)).append("=");
            
            Object value = entry.getValue();
            if (value instanceof Map) {
                dumpMapCompact((Map<?, ?>) value, sb);
            } else {
                sb.append(formatValue(value));
            }
            
            entryCount++;
            if (entryCount < map.size()) {
                sb.append(", ");
            }
        }
        
        sb.append("}");
    }
    
}
