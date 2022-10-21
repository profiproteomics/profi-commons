package fr.profi.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CollectionUtilsTest {

    @Test
    public void testSlidingWindows() {

        List<Integer> values= getFake8ElementList();
        List<List<Integer>> result=  CollectionUtils.createSlidingWindow(values, 2).collect(Collectors.toList());
        Assert.assertNotNull(result);
        Assert.assertEquals(4, result.size());
        Assert.assertEquals(2, result.get(0).size());
        Assert.assertEquals(2, result.get(3).size());
        Assert.assertEquals(Integer.valueOf(8), result.get(3).get(1));

    }

    @Test
    public void testSlidingWindowsIrregular() {

        List<Integer> values= getFake8ElementList();
        List<List<Integer>> result=  CollectionUtils.createSlidingWindow(values, 3).collect(Collectors.toList());
        Assert.assertNotNull(result);
        Assert.assertEquals(3, result.size());
        Assert.assertEquals(3, result.get(0).size());
        Assert.assertEquals(2, result.get(2).size());
        Assert.assertEquals(Integer.valueOf(7), result.get(2).get(0));
    }

    @Test
    public void testOverlapSlidingWindows() {

        List<Integer> values= getFake8ElementList();
        List<List<Integer>> result=  CollectionUtils.createOverlapSlidingWindow(values, 3).collect(Collectors.toList());
        Assert.assertNotNull(result);
        Assert.assertEquals(6, result.size());
        Assert.assertEquals(3, result.get(0).size());
        Assert.assertEquals(3, result.get(5).size());
        Assert.assertEquals(Integer.valueOf(6), result.get(3).get(2));
        Assert.assertEquals(Integer.valueOf(6), result.get(5).get(0));
    }

    private List<Integer> getFake8ElementList(){
        List<Integer> numbers= new ArrayList<>();
        numbers.add(1);
        numbers.add(2);
        numbers.add(3);
        numbers.add(4);
        numbers.add(5);
        numbers.add(6);
        numbers.add(7);
        numbers.add(8);

        return numbers;
    }
}
