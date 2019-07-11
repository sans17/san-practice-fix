package us.ligusan.practice.fix.utils.message;

import java.util.Random;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = {"-Xms1M", "-Xmx2G"})
public class MessageImplProfiling
{
    private SimpleMessage simpleMessage;
    private MessageIntObjectMapImpl messageIntObjectMapImpl;

    private Random random = new Random();

    @Setup(Level.Trial)
    public void setup()
    {
        simpleMessage = TestMemorySize.newSimpleMessage();
        messageIntObjectMapImpl = TestMemorySize.newMessageIntObjectMapImpl();
    }

    @Benchmark
    public void getSimple()
    {
        simpleMessage.get(TestMemorySize.START_TAG + random.nextInt(TestMemorySize.NUMBER_OF_FIELDS));
    }
    @Benchmark
    public void getIntObjectMapImpl()
    {
        messageIntObjectMapImpl.get(TestMemorySize.START_TAG + random.nextInt(TestMemorySize.NUMBER_OF_FIELDS));
    }

    public static void main(final String... pArgs) throws Exception
    {
        new Runner(new OptionsBuilder().include(MessageImplProfiling.class.getSimpleName()).build()).run();
    }
}
