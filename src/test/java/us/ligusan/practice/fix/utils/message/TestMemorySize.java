package us.ligusan.practice.fix.utils.message;

import java.util.LinkedHashMap;
import org.github.jamm.MemoryMeter;

public class TestMemorySize
{
    public static int START_TAG = 100;
    public static int NUMBER_OF_FIELDS = 50;

    public static SimpleMessage newSimpleMessage()
    {
        LinkedHashMap<Integer, String> lLinkedHashMap = new LinkedHashMap<>();
        for(int i = 0; i < NUMBER_OF_FIELDS; i++)
        {
            int lTag = START_TAG + i;
            lLinkedHashMap.put(lTag, Integer.toString(lTag));
        }
        return new SimpleMessage(lLinkedHashMap);
    }

    public static MessageIntObjectMapImpl newMessageIntObjectMapImpl()
    {
        MessageIntObjectMapImpl ret = new MessageIntObjectMapImpl();
        for(int i = 0; i < NUMBER_OF_FIELDS; i++)
        {
            int lTag = START_TAG + i;
            ret.set(lTag, Integer.toString(lTag));
        }
        return ret;
    }
    
    protected void memoryTest()
    {
        MemoryMeter lMemoryMeter = new MemoryMeter();

        SimpleMessage lSimpleMessage = new SimpleMessage(new LinkedHashMap<>());
        MessageIntObjectMapImpl lMessageIntObjectMapImpl = new MessageIntObjectMapImpl();
        System.out.println("empty SimpleMessage -> " + lMemoryMeter.measure(lSimpleMessage) + ", " + lMemoryMeter.measureDeep(lSimpleMessage) + ", " + lMemoryMeter.countChildren(lSimpleMessage));
        System.out.println("empty MessageIntObjectMapImpl -> " + lMemoryMeter.measure(lMessageIntObjectMapImpl) + ", " + lMemoryMeter.measureDeep(lMessageIntObjectMapImpl) + ", " + lMemoryMeter.countChildren(lMessageIntObjectMapImpl));

        lSimpleMessage = newSimpleMessage();
        lMessageIntObjectMapImpl = newMessageIntObjectMapImpl();
        System.out.println(NUMBER_OF_FIELDS + " tags SimpleMessage -> " + lMemoryMeter.measure(lSimpleMessage) + ", " + lMemoryMeter.measureDeep(lSimpleMessage) + ", " + lMemoryMeter.countChildren(lSimpleMessage));
        System.out.println(NUMBER_OF_FIELDS + " tags MessageIntObjectMapImpl -> " + lMemoryMeter.measure(lMessageIntObjectMapImpl) + ", " + lMemoryMeter.measureDeep(lMessageIntObjectMapImpl) + ", " + lMemoryMeter.countChildren(lMessageIntObjectMapImpl));
    }

    public static void main(String... pArgs)
    {
        new TestMemorySize().memoryTest();
    }
}
