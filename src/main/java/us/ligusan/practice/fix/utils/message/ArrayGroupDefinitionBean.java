package us.ligusan.practice.fix.utils.message;

import java.util.Collection;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Implementation of {@link SimpleGroupDefinition} based on arrays. Can be used with short groups.
 * 
 * @author Alexander Prishchepov
 *
 */
public class ArrayGroupDefinitionBean implements SimpleGroupDefinition
{
    private int firstTag;
    private int[] requiredTags;
    private int[] notRequiredTags;
    private int size;

    public ArrayGroupDefinitionBean(final int pFirstTag, final int[] pRequiredTags, final int[] pNotRequiredTags)
    {
        if(pRequiredTags != null) for(int i : pRequiredTags)
            if(i == pFirstTag) throw new IllegalArgumentException("First Tag is part of required tags!");
        if(pNotRequiredTags != null) for(int i : pNotRequiredTags)
            if(i == pFirstTag) throw new IllegalArgumentException("First Tag is part of not required tags!");

        firstTag = pFirstTag;
        requiredTags = pRequiredTags;
        notRequiredTags = pNotRequiredTags;

        size = 1;
        if(requiredTags != null) size += requiredTags.length;
        if(notRequiredTags != null) size += notRequiredTags.length;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this).appendSuper(super.toString()).append("firstTag", firstTag).append("requiredTags", requiredTags).append("notRequiredTags", notRequiredTags)
            .append("size", size).toString();
    }

    @Override
    public int size()
    {
        return size;
    }
    @Override
    public int getFirstTag()
    {
        return firstTag;
    }
    @Override
    public boolean contains(final int pTag)
    {
        if(requiredTags != null) for(int i : requiredTags)
            if(i == pTag) return true;
        if(notRequiredTags != null) for(int i : notRequiredTags)
            if(i == pTag) return true;
        return false;
    }
    @Override
    public boolean containsAllRequired(final Collection<Integer> pTags)
    {
        if(requiredTags != null) for(int i : requiredTags)
            if(!pTags.contains(i)) return false;

        return true;
    }
}
