package com.lithium3141.OpenWarp.test.util;

import org.junit.Assert;
import org.junit.Test;

import com.lithium3141.OpenWarp.util.StringUtil;


public class StringUtilTest {
    
    @Test
    public void testTrimArrayLeftEmpty() {
        Assert.assertArrayEquals(new String[] { }, StringUtil.trimArrayLeft(new String[] { }, new String[] { }));
    }
    
    @Test
    public void testTrimArrayLeft_empty_bonus() {
        Assert.assertArrayEquals(new String[] { }, StringUtil.trimArrayLeft(new String[] { }, new String[] { "trim" }));
        Assert.assertArrayEquals(new String[] { }, StringUtil.trimArrayLeft(new String[] { }, new String[] { "trim", "bonus" }));
    }
    
    @Test
    public void testTrimArrayLeft_nonempty_empty() {
        Assert.assertArrayEquals(new String[] { "test" }, StringUtil.trimArrayLeft(new String[] { "test" }, new String[] { }));
        Assert.assertArrayEquals(new String[] { "test", "trim" }, StringUtil.trimArrayLeft(new String[] { "test", "trim" }, new String[] { }));
    }
    
    @Test
    public void testTrimArrayLeft_nonempty_nonempty() {
        Assert.assertArrayEquals(new String[] { }, StringUtil.trimArrayLeft(new String[] { "test" }, new String[] { "test" }));
        Assert.assertArrayEquals(new String[] { "trim" }, StringUtil.trimArrayLeft(new String[] { "test", "trim" }, new String[] { "test" }));
    }
    
}
