package org.openmicroscopy.shoola.env;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;

public class LookupNamesTest {

    @Test
    public void testUniqueness() throws Exception {
        HashSet test = new HashSet<String>();
        for (Field f : LookupNames.class.getFields()) {
            if (!Modifier.isStatic(f.getModifiers()))
                continue;
            if (!(f.get(null) instanceof String))
                continue;
            String value = (String)f.get(null);
            if (test.contains(value))
                Assert.fail("Field "+f.getName()+" value '"+value+"' is not unique!");
            else
                test.add(value);
        }
    }
}
