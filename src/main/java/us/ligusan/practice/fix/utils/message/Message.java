package us.ligusan.practice.fix.utils.message;

public interface Message extends Iterable<Integer>
{
    public boolean contains(int tag);
    public Object get(int tag);
}
