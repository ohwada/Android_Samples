/**
 *  Kryo Sample
 *  2018-05-01 K.OHWADA
 */

package jp.ohwada.android.kryosample;

import android.os.Build;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.util.Log;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import junit.framework.Assert;

//import org.junit.Before;
//import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 *  class KryoTest
* original https://github.com/keiji/serializer_benchmarks/tree/kryo
 */
public class KryoTest {

    private static final String TAG = KryoTest.class.getSimpleName();

    private static final int LIMIT = 30;
    private static final int EPOCH = 5;

    private static final String LF = "\n";

    private final Random rand = new Random();
    private final List<SampleData> userList = new ArrayList<>();

    // @Before
    public void prepare() throws Exception {

        for (int i = 0; i < LIMIT; i++) {
            SampleData data1 = generateSample(i);
            userList.add(data1);
        }
    }

    @NonNull
    private SampleData generateSample(long id) {
        SampleData data1 = new SampleData();
        data1.setId(id);
        data1.setName("user " + id);
        data1.setAge(rand.nextInt(50));
        data1.setGender(rand.nextBoolean() ? SampleData.Gender.Female : SampleData.Gender.Male);
        data1.setMegane(rand.nextBoolean());
        return data1;
    }

    // @Test
    // public void test() throws Exception {
    public String test() throws Exception {

        String msg = "";
        for (int i = 0; i < EPOCH; i++) {
                    Result result = onshotTest();
                    msg += result.toString() + LF;
        }
        return msg;
    }


    //private void onshotTest() throws Exception {
     private  Result onshotTest() throws Exception {
        Result result = serializeDeserialize();
        Log.d(TAG, result.toString());

        for (int i = 0; i < userList.size(); i++) {
            Assert.assertTrue(userList.get(i).equals(result.serializedList.get(i)));
        }
        return result;
    }


    private Result serializeDeserialize() throws Exception {

        // serialize
        Kryo kryo = new Kryo();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = new Output(baos);
        long start = Debug.threadCpuTimeNanos();
        kryo.writeClassAndObject(output, userList);
        output.flush();
        byte[] serializedData = baos.toByteArray();
        long serializeDuration = Debug.threadCpuTimeNanos() - start;

        long serializedSize = serializedData.length;

        // deserialize
        ByteArrayInputStream bais = new ByteArrayInputStream(serializedData);
        Input input = new Input(bais);
        start = Debug.threadCpuTimeNanos();
        List<SampleData> list = (List<SampleData>) kryo.readClassAndObject(input);
        long deserializeDuration = Debug.threadCpuTimeNanos() - start;

        return new Result(list, serializeDuration, serializedSize, deserializeDuration);
    }

    private class Result {
        public final List<SampleData> serializedList;
        public final long serializeDuration;
        public final long serializedSize;
        public final long deserializeDuration;


        private Result(List<SampleData> serializedList,
                       long serializeDuration,
                       long serializedSize,
                       long deserializeDuration) {
            this.serializedList = serializedList;
            this.serializeDuration = serializeDuration;
            this.serializedSize = serializedSize;
            this.deserializeDuration = deserializeDuration;
        }

        @Override
        public String toString() {
            return String.format(Locale.getDefault(),
                    "Device: " + Build.DEVICE + "\n" +
                            "serialize:   duration -> %dns\n" +
                            "             size -> %dbytes\n" +
                            "deserialize: duration -> %dns",
                    serializeDuration, serializedSize, deserializeDuration);
        }
    }
}
