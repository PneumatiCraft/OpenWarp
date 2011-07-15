package com.lithium3141.OpenWarp.test.util;

import java.util.ArrayList;
import java.util.List;

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
    public void testTrimArrayLeft_nonempty_single() {
        Assert.assertArrayEquals(new String[] { }, StringUtil.trimArrayLeft(new String[] { "test" }, new String[] { "test" }));
        Assert.assertArrayEquals(new String[] { "trim" }, StringUtil.trimArrayLeft(new String[] { "test", "trim" }, new String[] { "test" }));
    }
    
    @Test
    public void testTrimListLeft() {
        List<String> value = new ArrayList<String>() {{ add("a"); add("b"); add("c"); add("d"); }};
        List<String> trim = new ArrayList<String>() {{ add("a"); add("b"); }};
        
        Assert.assertEquals(new ArrayList<String>() {{ add("c"); add("d"); }}, StringUtil.trimListLeft(value, trim));
    }
    
}
