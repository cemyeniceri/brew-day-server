package org.gsu.brewday.unit.config;

import org.gsu.brewday.config.UuidIdentifierGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;

public class UuidIdentifierGeneratorTest {

    private Serializable uuid1;
    private Serializable uuid2;

    @Before
    public void setUp() throws Exception {
        UuidIdentifierGenerator uuidIdentifierGenerator = new UuidIdentifierGenerator();
        uuid1 = uuidIdentifierGenerator.generate(null, null);
        uuid2 = uuidIdentifierGenerator.generate(null, null);
    }

    @Test
    public void generate() throws Exception {
        Assert.assertNotNull(uuid1);
        Assert.assertNotNull(uuid2);
        Assert.assertFalse(uuid1.toString().isEmpty());
        Assert.assertFalse(uuid2.toString().isEmpty());
        Assert.assertFalse(uuid1.toString().contains("-"));
        Assert.assertFalse(uuid2.toString().contains("-"));
        Assert.assertNotEquals(uuid1, uuid2);
    }

}