package us.ligusan.practice.fix.utils.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/*
 * Simple map implementation takes memory.
 * Let's try arrays.
 *
 */
public class MessageIntObjectMapImpl implements MutableMessage
{
    private int[] tags;
    private ArrayList<Object> values;

    public MessageIntObjectMapImpl()
    {
        values = new ArrayList<>();
    }

    protected int getIndex(final int pTag)
    {
        int lSize = values.size();
        return lSize > 0 ? Arrays.binarySearch(tags, 0, lSize, pTag) : -1;
    }

    @Override
    public boolean contains(final int pTag)
    {
        return getIndex(pTag) >= 0;
    }

    @Override
    public Object get(final int pTag)
    {
        int lIndex = getIndex(pTag);
        return lIndex >= 0 ? values.get(lIndex) : null;
    }

    @Override
    public Iterator<Integer> iterator()
    {
        return new Iterator<Integer>()
            {
                private int index;

                @Override
                public boolean hasNext()
                {
                    return index < values.size();
                }

                @Override
                public Integer next()
                {
                    return tags[index++];
                }
            };
    }

    @Override
    public void remove(final int pTag)
    {
        int lIndex = getIndex(pTag);
        if(lIndex > 0)
        {
            int lSize = values.size();
            if(lIndex < lSize - 1) System.arraycopy(tags, lIndex + 1, tags, lIndex, lSize - 1 - lIndex);

            values.remove(lIndex);
        }
    }

    @Override
    public void set(final int pTag, final Object pValue)
    {
        int lIndex = getIndex(pTag);
        if(lIndex > 0) values.set(lIndex, pValue);
        else
        {
            // aprishchepov - Jun 25, 2019 5:55:33 PM : insertion index
            lIndex = -++lIndex;

            // aprishchepov - Jun 26, 2019 11:35:18 AM : where we copy to (might be the same as where we copy from)
            int[] lNewTags = tags;
            int lLength = tags == null ? 0 : tags.length;
            int lSize = values.size();
            // aprishchepov - Jun 26, 2019 9:21:32 AM : need to increase keys size
            if(lLength <= lSize)
            {
                // aprishchepov - Jun 25, 2019 5:30:02 PM : increase by 10? in ArrayList they use: oldCapacity + (oldCapacity >> 1)
                // aprishchepov - Jun 26, 2019 9:32:11 AM : assigning new keys
                lNewTags = new int[lLength + 10];

                // aprishchepov - Jun 26, 2019 9:27:48 AM : #1 copy left side
                if(lIndex > 0) System.arraycopy(tags, 0, lNewTags, 0, lIndex);
            }
            // aprishchepov - Jun 26, 2019 9:28:13 AM : #2 copy right side (in case it is the same array)
            if(lIndex < lSize) System.arraycopy(tags, lIndex, lNewTags, lIndex + 1, lSize - lIndex);
            // aprishchepov - Jun 26, 2019 9:29:21 AM : #3 setting new key
            lNewTags[lIndex] = pTag;

            tags = lNewTags;
            // aprishchepov - Jun 26, 2019 9:20:28 AM : adding value
            values.add(lIndex, pValue);
        }
    }
}
