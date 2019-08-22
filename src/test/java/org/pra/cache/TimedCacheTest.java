package org.pra.cache;

import org.pra.cache.impl.TimedCache;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * @author Pradeep Jindal
 * Created by pjind5 on 04-Jul-17.
 */
public class TimedCacheTest {

    private static Cache getCacheInstance() {
        return new TimedCache<>();
    }

    @Test
    public void testPutAndGet() {
        String key = Integer.toString(new Random().nextInt());
        String value = Integer.toString(new Random().nextInt());
        String key2 = Integer.toString(new Random().nextInt());

        Cache<String, String> cache = new TimedCache<>();
        assertNull("key should not exist in cache", cache.getItem(key));
        cache.putItem(key, value);
        assertEquals("retrieving value from cache", value, cache.getItem(key));
        assertNull("should not exist", cache.getItem(key2));
    }

    @Test
    public void testEviction() {
        String key = Integer.toString(new Random().nextInt());
        String value = Integer.toString(new Random().nextInt());

        String key2 = Integer.toString(new Random().nextInt());
        String value2 = Integer.toString(new Random().nextInt());

        Cache<String, String> cache = new TimedCache<>(1000);
        assertEquals("initially size should be zero", 0, (int) cache.getSize());
        cache.putItem(key, value);
        assertEquals("now size should be one", 1, (int) cache.getSize());
        try {
            //System.out.println( " test going to sleep ");
            Thread.sleep(3000);
            //System.out.println( " test waking from sleep ");
        } catch (InterruptedException e) {
            //
        }
        assertEquals("cache should have been evicted", 0, (int) cache.getSize());
        //
        cache.putItem(key, value);
        assertEquals("size should remain one", 1, (int) cache.getSize());
        try {
            //System.out.println( " test going to sleep ");
            Thread.sleep(700);
            //System.out.println( " test waking from sleep ");
        } catch (InterruptedException e) {
            //
        }
        cache.putItem(key2, value2);
        assertEquals("size should remain one", 2, (int) cache.getSize());
        try {
            //System.out.println( " test going to sleep ");
            Thread.sleep(500);
            //System.out.println( " test waking from sleep ");
        } catch (InterruptedException e) {
            //
        }
        assertEquals("size should remain one", 1, (int) cache.getSize());
    }

    @Test
    public void testFullSizeOrDefaultSize() {

    }

    @Test
    public void testValueReplacementForSameKey() {
        String key = Integer.toString(new Random().nextInt());
        String value = Integer.toString(new Random().nextInt());

        String value2 = Integer.toString(new Random().nextInt());

        Cache<String, String> cache = new TimedCache<>(2);
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

        Cache<Object, Object> cache = new TimedCache<>(2);
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

        Cache<KeyClass, Object> cache = new TimedCache<>(2);
        cache.putItem(keyClass, value);
        assertEquals("object should be retrieved", value, cache.getItem(keyClass));
        assertNull("object should not be found", cache.getItem(keyClass2));

        // replacing value for same key
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

        Cache<KeyClass, ValueClass> cache = new TimedCache<>(2);
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

        Cache<FixedHashKeyClass, ValueClass> cache = new TimedCache<>(2);
        cache.putItem(keyClass, valueClass);
        assertEquals("object should be retrieved", valueClass, cache.getItem(keyClass));
        assertNull("object should not be found", cache.getItem(keyClass2));
        assertEquals("size should be one", 1, (int) cache.getSize());

        cache.putItem(keyClass2, valueClass2);
        assertEquals("size should be two", 2, (int) cache.getSize());
        assertEquals("retrieved object should match", valueClass, cache.getItem(keyClass));
        assertEquals("retrieved object should match", valueClass2, cache.getItem(keyClass2));

        cache.removeItem(keyClass);
        assertEquals("size should be one", 1, (int) cache.getSize());
        assertNull("key should not be present", cache.getItem(keyClass));
        assertEquals("retrieved object should match", valueClass2, cache.getItem(keyClass2));

        cache.removeItem(keyClass2);
        assertEquals("size should be zero", 0, (int) cache.getSize());
    }

    private class FixedHashKeyClass {
        final String key;
        final int hash;

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
            return Objects.equals(key, that.key);
        }

        @Override
        public int hashCode() {
            return 1;
        }
    }

    private class KeyClass {
        final String name;
        final int age;

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
            return Objects.equals(name, keyClass.name);
        }

        @Override
        public int hashCode() {
            int result = name == null ? 0 : name.hashCode();
            result = 31 * result + age;
            return result;
        }
    }

    private class ValueClass {
        final String valueString;
        final int valueInteger;

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
            return Objects.equals(valueString, that.valueString);
        }

        @Override
        public int hashCode() {
            int result = valueString != null ? valueString.hashCode() : 0;
            result = 31 * result + valueInteger;
            return result;
        }
    }
}
