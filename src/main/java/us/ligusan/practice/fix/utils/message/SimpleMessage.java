package us.ligusan.practice.fix.utils.message;

import java.util.Iterator;
import java.util.Map;

public class SimpleMessage implements Message
{
    /**
     * Map of tag to a {@link CharSequence} value or to a List of repeating groups {@link Message}s
     */
    private Map<Integer, ?> tagValueMap;

    public SimpleMessage(final Map<Integer, ?> pTagValueMap)
    {
        tagValueMap = pTagValueMap;
    }

    @Override
    public Iterator<Integer> iterator()
    {
        return tagValueMap.keySet().iterator();
    }

    @Override
    public boolean contains(int pTag)
    {
        return tagValueMap.containsKey(pTag);
    }
    @Override
    public Object get(final int pTag)
    {
        return tagValueMap.get(pTag);
    }

}
