package us.ligusan.practice.fix.utils.message;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import java.util.Map;
import org.junit.jupiter.api.Test;
import us.ligusan.practice.fix.utils.ParsePositionExt;

public class SimpleMessageParserTest
{
    //    @BeforeAll
    //    static void setUpBeforeClass() throws Exception
    //    {}
    //
    //    @AfterAll
    //    static void tearDownAfterClass() throws Exception
    //    {}
    //
    //    @BeforeEach
    //    void setUp() throws Exception
    //    {}
    //
    //    @AfterEach
    //    void tearDown() throws Exception
    //    {}

    @Test
    final void testParseInt()
    {
        var lParserUnderTest = new SimpleMessageParser('=', '|', null);

        try
        {
            assertEquals(100, lParserUnderTest.parseInt("100=", new ParsePositionExt(), '='));
        }
        catch(Throwable t)
        {
            fail(t);
        }

        assertThrows(RuntimeException.class, () -> lParserUnderTest.parseInt("=", new ParsePositionExt(), '='));

        assertThrows(RuntimeException.class, () -> lParserUnderTest.parseInt("-10=", new ParsePositionExt(), '='));
    }

    @Test
    final void testParseKey()
    {
        var lParserUnderTest = new SimpleMessageParser('=', '|', null);

        try
        {
            assertEquals(100, lParserUnderTest.parseKey("100=", new ParsePositionExt()));
        }
        catch(Throwable t)
        {
            fail(t);
        }

        assertThrows(RuntimeException.class, () -> lParserUnderTest.parseKey("=", new ParsePositionExt()));
        assertThrows(RuntimeException.class, () -> lParserUnderTest.parseKey("100|", new ParsePositionExt()));
        assertThrows(RuntimeException.class, () -> lParserUnderTest.parseKey("-10=", new ParsePositionExt()));
    }

    @Test
    final void testGivenParse()
    {
        // san - Feb 28, 2019 10:59:18 AM : first given test
        SimpleMessageParser lNoGroupsParser = new SimpleMessageParser('=', '|', null);
        try
        {
            lNoGroupsParser.parse("8=345|9=12|55=IBM|40=P|");
        }
        catch(Throwable t)
        {
            fail(t);
        }
        
        // san - Feb 28, 2019 10:59:29 AM : second given test
        String lMessageString = "8=345|9=12|55=IBM|40=P|269=2|277=12|283=5|456=7|277=1|231=56|456=7|44=12|";
        assertThrows(RuntimeException.class, () -> lNoGroupsParser.parse(lMessageString));
        try
        {
            new SimpleMessageParser('=', '|', Map.of(269, new ArrayGroupDefinitionBean(277, new int[] {456}, new int[] {231, 283}))).parse(lMessageString);
        }
        catch(Throwable t)
        {
            fail(t);
        }

        // san - Feb 28, 2019 10:59:45 AM : third given test
        SimpleMessageParser lParserUnderTest = new SimpleMessageParser('=', '|',
            Map.of(269, new ArrayGroupDefinitionBean(277, new int[] {456}, new int[] {231, 283}), 123, new ArrayGroupDefinitionBean(786, new int[] {398}, new int[] {567, 496})));
        try
        {
            lParserUnderTest.parse("8=345|9=12|55=IBM|40=P|269=2|277=12|283=5|456=7|277=1|231=56|456=7|44=12|");
        }
        catch(Throwable t)
        {
            fail(t);
        }
        try
        {
            lParserUnderTest.parse("8=345|9=12|55=IBM|40=P|269=2|277=12|283=5|456=7|277=1|231=56|456=7|123=2|786=9|398=ABC|786=QAS|567=12|496=SDF|398=12|44=12|");
        }
        catch(Throwable t)
        {
            fail(t);
        }
        assertThrows(RuntimeException.class, () -> lParserUnderTest.parse("8=345|9=12|55=IBM|40=P|269=2|277=12|283=5|456=7|277=1|231=56|44=12|"));
    }

    @Test
    final void testParse()
    {
        // san - Feb 28, 2019 10:59:45 AM : short group
        SimpleMessageParser lParserUnderTest = new SimpleMessageParser('=', '|', Map.of(78, new ArrayGroupDefinitionBean(79, null, new int[] {80})));

        // san - Mar 2, 2019 4:13:22 PM : missing final pairs separator 1
        assertThrows(RuntimeException.class, () -> lParserUnderTest.parse("8=A|9=B"));

        // san - Mar 2, 2019 4:13:22 PM : missing pairs separator in the middle - this one correctly parses
        //        assertThrows(RuntimeException.class, () -> lParserUnderTest.parse("8=A9=B|"));

        // san - Mar 2, 2019 4:15:45 PM : missing tag value separator
        assertThrows(RuntimeException.class, () -> lParserUnderTest.parse("8=A|9B|"));
        // san - Mar 2, 2019 4:15:45 PM : duplicate tag
        assertThrows(RuntimeException.class, () -> lParserUnderTest.parse("8=A|8=B|"));
        // san - Mar 2, 2019 4:15:45 PM : empty repeating group
        assertThrows(RuntimeException.class, () -> lParserUnderTest.parse("8=A|9=B|78=0|"));
        // san - Mar 2, 2019 4:15:45 PM : missing group 1
        assertThrows(RuntimeException.class, () -> lParserUnderTest.parse("8=A|9=B|78=1|"));
        // san - Mar 2, 2019 4:15:45 PM : missing final pairs separator
        assertThrows(RuntimeException.class, () -> lParserUnderTest.parse("8=A|9=B|78=1|79=C"));
        // san - Mar 2, 2019 4:15:45 PM : missing group 2
        assertThrows(RuntimeException.class, () -> lParserUnderTest.parse("8=A|9=B|78=2|79=C|"));
        // san - Mar 2, 2019 4:15:45 PM : wrong sequence in group
        assertThrows(RuntimeException.class, () -> lParserUnderTest.parse("8=A|9=B|78=1|80=C|79=D|"));
        // san - Mar 2, 2019 4:15:45 PM : wrong tag in group
        assertThrows(RuntimeException.class, () -> lParserUnderTest.parse("8=A|9=B|78=2|79=D|80=E|81=F|79=G|"));
        // san - Mar 2, 2019 4:23:46 PM : short group
        try
        {
            lParserUnderTest.parse("8=A|9=B|78=2|79=D|80=E|79=G|");
        }
        catch(Throwable t)
        {
            fail(t);
        }
    }
}
