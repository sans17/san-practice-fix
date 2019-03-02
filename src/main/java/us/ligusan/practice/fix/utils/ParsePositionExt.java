package us.ligusan.practice.fix.utils;

import java.text.ParsePosition;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ParsePositionExt extends ParsePosition
{
    private int keyParsed;

    public ParsePositionExt()
    {
        super(0);
    }

    public int getKeyParsed()
    {
        return keyParsed;
    }
    public void setKeyParsed(final int pKeyParsed)
    {
        keyParsed = pKeyParsed;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this).appendSuper(super.toString()).append("keyParsed", keyParsed).toString();
    }
}
