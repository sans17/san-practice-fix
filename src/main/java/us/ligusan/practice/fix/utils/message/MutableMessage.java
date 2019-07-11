package us.ligusan.practice.fix.utils.message;

public interface MutableMessage extends Message
{
    public void remove(int tag);
    public void set(int tag, Object value);
}
