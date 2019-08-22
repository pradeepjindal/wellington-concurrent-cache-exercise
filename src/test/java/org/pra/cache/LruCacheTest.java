package org.pra.cache;

import org.pra.cache.impl.LruCache;
import org.junit.Test;

import java.util.*;
import java.util.stream.IntStream;

import static org.junit.Assert.*;
/**
 * Created by pjind5 on 04-Jul-17.
 */
public class LruCacheTest {

    @Test
    public void testPutAndGet() {
        String key = Integer.toString(new Random().nextInt());
        String value = Integer.toString(new Random().nextInt());
        String key2 = Integer.toString(new Random().nextInt());

        Cache<String, String> cache = new LruCache<>();
        assertNull("key should not exist in cache", cache.getItem(key));
        cache.putItem(key, value);
        assertEquals("retrieving value from cache", value, cache.getItem(key));
        assertNull("should not exist", cache.getItem(key2));
    }

    @Test
    public void testSize() {
        String key = Integer.toString(new Random().nextInt());
        String value = Integer.toString(new Random().nextInt());

        String key2 = Integer.toString(new Random().nextInt());
        String value2 = Integer.toString(new Random().nextInt());

        Cache<String, String> cache = new LruCache<>(1);
        assertTrue("initially size should be zero", cache.getSize() == 0);
        cache.putItem(key, value);
        assertTrue("now size should be one", cache.getSize() == 1);
        cache.putItem(key2, value2);
        assertTrue("size should remain one", cache.getSize() == 1);
    }

    @Test
    public void testFullSizeOrDefaultSize() {

    }

    @Test
    public void testAutomaticEviction() {
        List<String> keys = new LinkedList();
        Map<String, String> data = new HashMap();
        IntStream.range(0,3).map( input -> {
            keys.add(input, Integer.toString(new Random().nextInt()));
            data.put(keys.get(input), Integer.toString(new Random().nextInt()));
            return input;
        }).count();

        Cache<String, String> cache = new LruCache<>(2);

        keys.stream().map( key -> {
            cache.putItem(key, data.get(key));
            return key;
        }).count();

        assertNull("first key shoud have evicted", cache.getItem(keys.get(0)));
        assertEquals("second key should be presented", data.get(keys.get(1)), cache.getItem(keys.get(1)));
        assertEquals("third key should be presented", data.get(keys.get(2)), cache.getItem(keys.get(2)));
    }

    @Test
    public void testValueReplacementForSameKey() {
        String key = Integer.toString(new Random().nextInt());
        String value = Integer.toString(new Random().nextInt());

        String value2 = Integer.toString(new Random().nextInt());

        Cache<String, String> cache = new LruCache<>(2);
        assertEquals("initially size should be zero", "0", cache.getSize().toString());
        cache.putItem(key, value);
        assertEquals("now size should be one", "1", cache.getSize().toString());
        assertEquals("object should be retrieved", value, cache.getItem(key));

        cache.putItem(key, value2);
        assertEquals("size should remain one", "1", cache.getSize().toString());
        assertNotEquals("size should not grow", "2", cache.getSize().toString());
        assertTrue("size should be less then two", cache.getSize() < 2);
        assertEquals("object should be replaced", value2, cache.getItem(key));
    }

    @Test
    public void testCasting() {
        String key = UUID.randomUUID().toString();
        String value = Integer.toString(new Random().nextInt());

        Cache<Object, Object> cache = new LruCache<>(2);
        assertEquals("initially size should be zero", "0", cache.getSize().toString());
        cache.putItem(key, value);
        assertEquals("object should be retrieved", value, cache.getItem(key));
    }

    @Test
    public void testCustomClassAsKey() {
        String key = UUID.randomUUID().toString();
        int intValue = new Random().nextInt();
        String value = Integer.toString(intValue);
        KeyClass keyClass = new KeyClass(key, intValue);

        String key2 = UUID.randomUUID().toString();
        int intValue2 = new Random().nextInt();
        String value2 = Integer.toString(intValue2);
        KeyClass keyClass2 = new KeyClass(key2, intValue2);

        Cache<KeyClass, Object> cache = new LruCache<>(2);
        cache.putItem(keyClass, value);
        assertEquals("object should be retrieved", value, cache.getItem(keyClass));
        assertNull("object should not be found", cache.getItem(keyClass2));

        // replaceing value for same key
        cache.putItem(keyClass, value2);
        assertNotEquals("object should not match", value, cache.getItem(keyClass2));
        assertEquals("object should match", value2, cache.getItem(keyClass));
    }

    @Test
    public void testCustomClassAsValue() {
        String key = UUID.randomUUID().toString();
        int intValue = new Random().nextInt();
        String value = Integer.toString(intValue);
        KeyClass keyClass = new KeyClass(key, intValue);
        ValueClass valueClass = new ValueClass(value, intValue);

        String key2 = UUID.randomUUID().toString();
        int intValue2 = new Random().nextInt();
        String value2 = Integer.toString(intValue2);
        KeyClass keyClass2 = new KeyClass(key2, intValue2);
        ValueClass valueClass2 = new ValueClass(value2, intValue2);

        Cache<KeyClass, ValueClass> cache = new LruCache<>(2);
        cache.putItem(keyClass, valueClass);
        assertEquals("object should be retrieved", valueClass, cache.getItem(keyClass));
        assertNull("object should not be found", cache.getItem(keyClass2));

        //storing same object with different key
        cache.putItem(keyClass2, valueClass);
        assertEquals("retrieved object should match", valueClass, cache.getItem(keyClass));
        assertEquals("retrieved object should match", valueClass, cache.getItem(keyClass2));
    }

    @Test
    public void testHashCollision() {
        String key = UUID.randomUUID().toString();
        int intValue = new Random().nextInt();
        String value = Integer.toString(intValue);
        FixedHashKeyClass keyClass = new FixedHashKeyClass(key, intValue);
        ValueClass valueClass = new ValueClass(value, intValue);

        String key2 = UUID.randomUUID().toString();
        int intValue2 = new Random().nextInt();
        String value2 = Integer.toString(intValue2);
        FixedHashKeyClass keyClass2 = new FixedHashKeyClass(key2, intValue2);
        ValueClass valueClass2 = new ValueClass(value2, intValue2);

        assertEquals("key hashCode should be equal", keyClass.hashCode(), keyClass2.hashCode());
        assertNotEquals("key value should not be equal", keyClass, keyClass2);
        assertNotEquals("value should not be equal", valueClass, valueClass2);

        Cache<FixedHashKeyClass, ValueClass> cache = new LruCache<>(2);
        cache.putItem(keyClass, valueClass);
        assertEquals("object should be retrieved", valueClass, cache.getItem(keyClass));
        assertNull("object should not be found", cache.getItem(keyClass2));
        assertTrue("size should be one", cache.getSize() == 1);

        cache.putItem(keyClass2, valueClass2);
        assertTrue("size should be two", cache.getSize() == 2);
        assertEquals("retrieved object should match", valueClass, cache.getItem(keyClass));
        assertEquals("retrieved object should match", valueClass2, cache.getItem(keyClass2));

        cache.removeItem(keyClass);
        assertTrue("size should be one", cache.getSize() == 1);
        assertNull("key shound not be present", cache.getItem(keyClass));
        assertEquals("retrieved object should match", valueClass2, cache.getItem(keyClass2));

        cache.removeItem(keyClass2);
        assertTrue("size should be zero", cache.getSize() == 0);
    }

    private class FixedHashKeyClass {
        String key;
        int hash;

        FixedHashKeyClass(String key, int hash) {
            this.key = key;
            this.hash = hash;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FixedHashKeyClass that = (FixedHashKeyClass) o;

            if (hash != that.hash) return false;
            return key != null ? key.equals(that.key) : that.key == null;
        }

        @Override
        public int hashCode() {
            return 1;
        }
    }

    private class KeyClass {
        String name;
        int age;

        KeyClass(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            KeyClass keyClass = (KeyClass) o;

            if (age != keyClass.age) return false;
            return name == null ? keyClass.name == null : name.equals(keyClass.name) ;
        }

        @Override
        public int hashCode() {
            int result = name == null ? 0 : name.hashCode();
            result = 31 * result + age;
            return result;
        }
    }

    private class ValueClass {
        String valueString;
        int valueInteger;

        ValueClass(String valueString, int valueInteger) {
            this.valueString = valueString;
            this.valueInteger = valueInteger;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ValueClass that = (ValueClass) o;

            if (valueInteger != that.valueInteger) return false;
            return valueString != null ? valueString.equals(that.valueString) : that.valueString == null;
        }

        @Override
        public int hashCode() {
            int result = valueString != null ? valueString.hashCode() : 0;
            result = 31 * result + valueInteger;
            return result;
        }
    }
}
