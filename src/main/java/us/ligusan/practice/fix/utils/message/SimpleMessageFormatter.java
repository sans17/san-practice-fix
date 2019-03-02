package us.ligusan.practice.fix.utils.message;

import java.text.MessageFormat;
import java.util.List;

public class SimpleMessageFormatter
{
    public StringBuilder format(final Message pMessage)
    {
        StringBuilder ret = new StringBuilder("Tag\tValue\n");
        for(Integer lTag : pMessage)
        {
            Object lValue = pMessage.get(lTag);
            if(lValue instanceof String) ret.append(lTag).append('\t').append(lValue).append('\n');
            else if(lValue instanceof List)
            {
                List<?> lList = (List)lValue;

                ret.append("Start Repeating Group\n");
                ret.append(lTag).append('\t').append(lList.size()).append('\n');
                int i = 1;
                for(Object lObject : lList)
                {
                    ret.append("Group ").append(i++).append('\n');
                    if(lObject instanceof Message) ret.append(format((Message)lObject));
                    else new RuntimeException(MessageFormat.format("Unsupported list element! object={0}", lObject));
                }
                ret.append("End Repeating Group\n");
            }
            else throw new RuntimeException(MessageFormat.format("Unsupported value! value={0}", lValue));
        }
        return ret;
    }
}
