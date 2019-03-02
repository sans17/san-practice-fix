package us.ligusan.practice.fix.utils.message;

import java.util.Collection;

public interface SimpleGroupDefinition
{
    int size();

    int getFirstTag();

    boolean contains(int tag);
    boolean containsAllRequired(Collection<Integer> tags);
}
