package fr.profi.util;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

// get from https://stackoverflow.com/questions/34158634/how-to-transform-a-java-stream-into-a-sliding-window
public final class CollectionUtils {

    /**
     * Create sublists of specified size with no overlap, from input list.
     *
     * @param list : source list to split
     * @param size : size of created sublist
     * @return List of sub list created from list with specified size, without any overlap
     */
    public static <T> Stream<List<T>> createSlidingWindow(List<T> list, int size) {
        return IntStream.range(0, list.size())
                .mapToObj(i -> list.subList(
                        Math.min(list.size(), size * i),
                        Math.min(list.size(), size + (size * i))))
                .filter(l -> !l.isEmpty());
    }

    public static <T> Stream<List<T>> createOverlapSlidingWindow(List<T> list, int size) {
        if(size > list.size())
            return Stream.empty();
        return IntStream.range(0, list.size()-size+1)
                .mapToObj(start -> list.subList(start, start+size));
    }


}
