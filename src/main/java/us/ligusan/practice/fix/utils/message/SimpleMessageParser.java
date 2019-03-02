package us.ligusan.practice.fix.utils.message;

import java.text.MessageFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import us.ligusan.practice.fix.utils.ParsePositionExt;

public class SimpleMessageParser implements MessageParser
{
    private char keyValueSeparator;
    private char pairSeparator;

    private Map<Integer, SimpleGroupDefinition> repeatingGroups;

    public SimpleMessageParser(final char pKeyValueSparator, final char pPairSeparator, final Map<Integer, SimpleGroupDefinition> pRepeatingGroupDefinitions)
    {
        keyValueSeparator = pKeyValueSparator;
        pairSeparator = pPairSeparator;

        repeatingGroups = pRepeatingGroupDefinitions;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this).appendSuper(super.toString()).append("keyValueSeparator", keyValueSeparator).append("pairSeparator", pairSeparator)
            .append("repeatingGroups", repeatingGroups).toString();
    }

    @Override
    public Message parse(final String pInput)
    {
        var lMessageMap = new LinkedHashMap<Integer, Object>();
        parse(pInput, new ParsePositionExt(), lMessageMap);
        return new SimpleMessage(lMessageMap);
    }

    protected int throwParseException(final ParsePosition pPosition, final int pErrorIndex, final String pErrorMessage)
    {
        pPosition.setErrorIndex(pErrorIndex);

        throw new RuntimeException(MessageFormat.format("{0} position={1}", pErrorMessage, pPosition));
    }

    protected int parseInt(final String pInput, final ParsePosition pPosition, final char pEndCharacter)
    {
        int ret = 0;

        int lStartIndex = pPosition.getIndex();

        // san - Feb 27, 2019 11:15:28 AM : we can use DecimalFormat here, but it takes String as an input
        for(int lIndex = lStartIndex; lIndex < pInput.length(); lIndex++)
        {
            char lChar = pInput.charAt(lIndex);
            if(lChar == pEndCharacter)
            {
                if(ret <= 0) throwParseException(pPosition, lStartIndex, "Expected to find positive integer!");

                pPosition.setIndex(lIndex + 1);
                return ret;
            }
            else if(lChar >= '0' && lChar <= '9') ret = ret * 10 + (lChar - '0');
            else throwParseException(pPosition, lIndex, "Expected digits only!");
        }

        return throwParseException(pPosition, lStartIndex, "Expected to find closing character!");
    }

    protected int parseKey(final String pInput, final ParsePositionExt pPosition)
    {
        // san - Feb 28, 2019 9:47:44 AM : check if key was already parsed
        int ret = pPosition.getKeyParsed();
        if(ret == 0) pPosition.setKeyParsed(ret = parseInt(pInput, pPosition, keyValueSeparator));
        return ret;
    }

    protected String parseStringValue(final String pInput, final ParsePositionExt pPosition)
    {
        int lCurrentIndex = pPosition.getIndex();

        int lSeparatorIndex = StringUtils.indexOf(pInput, pairSeparator, lCurrentIndex);
        if(lSeparatorIndex < 0) throwParseException(pPosition, lCurrentIndex, "Expected to find pairs separator!");

        pPosition.setIndex(lSeparatorIndex + 1);
        pPosition.setKeyParsed(0);

        return pInput.substring(lCurrentIndex, lSeparatorIndex).intern();
    }

    protected Message parseGroup(final String pInput, final ParsePositionExt pPosition, final SimpleGroupDefinition pGroupDefinition)
    {
        int lStartIndex = pPosition.getIndex();

        // san - Feb 28, 2019 9:47:44 AM : check if key was already parsed
        int lKey = parseKey(pInput, pPosition);
        if(lKey != pGroupDefinition.getFirstTag()) throwParseException(pPosition, lStartIndex, "Expected to find first key of the group!");

        var lGroupMap = new LinkedHashMap<Integer, Object>(pGroupDefinition.size());
        lGroupMap.put(lKey, parseStringValue(pInput, pPosition));

        lStartIndex = pPosition.getIndex();

        for(int lCurrentPosition; (lCurrentPosition = pPosition.getIndex()) < pInput.length();)
        {
            lKey = parseKey(pInput, pPosition);

            // san - Feb 28, 2019 9:40:15 AM : tag is not in the group
            if(!pGroupDefinition.contains(lKey)) break;

            if(lGroupMap.containsKey(lKey)) throwParseException(pPosition, lCurrentPosition, "Duplicate tag!");

            lGroupMap.put(lKey, parseStringValue(pInput, pPosition));
        }

        if(!pGroupDefinition.containsAllRequired(lGroupMap.keySet()))
            throwParseException(pPosition, lStartIndex, "Not all required tags are present!");

        return new SimpleMessage(lGroupMap);
    }

    protected int parseIntValue(final String pInput, final ParsePositionExt pPosition)
    {
        int ret = parseInt(pInput, pPosition, pairSeparator);
        pPosition.setKeyParsed(0);
        return ret;
    }

    protected void parse(final String pInput, final ParsePositionExt pPosition, final Map<Integer, Object> pMessageMap)
    {
        for(int lCurrentPosition; (lCurrentPosition = pPosition.getIndex()) < pInput.length();)
        {
            int lKey = parseKey(pInput, pPosition);
            
            if(pMessageMap.containsKey(lKey)) throwParseException(pPosition, lCurrentPosition, "Duplicate tag!");

            SimpleGroupDefinition lGroupDefinition = repeatingGroups == null ? null : repeatingGroups.get(lKey);
            // san - Feb 27, 2019 11:28:15 AM : regular tag
            if(lGroupDefinition == null) pMessageMap.put(lKey, parseStringValue(pInput, pPosition));
            // san - Feb 27, 2019 11:28:32 AM : repeating group
            else
            {
                int lRepeatingGroupSize = parseIntValue(pInput, pPosition);

                var lGroupsList = new ArrayList<Message>(lRepeatingGroupSize);
                for(int i = 0; i < lRepeatingGroupSize; i++)
                    lGroupsList.add(parseGroup(pInput, pPosition, lGroupDefinition));

                pMessageMap.put(lKey, lGroupsList);
            }
        }
    }
}
