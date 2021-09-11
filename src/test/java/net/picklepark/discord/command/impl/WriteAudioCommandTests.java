package net.picklepark.discord.command.impl;

import net.picklepark.discord.command.audio.impl.WriteAudioCommand;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;

@RunWith(JUnit4.class)
public class WriteAudioCommandTests {

    @Test
    public void flattenPreservesData() {
        byte[] a = {1, 2};
        byte[] b = {3, 4};
        byte[] out = WriteAudioCommand.flatten(Arrays.asList(a, b));
        Assert.assertEquals(4, out.length);
        Assert.assertEquals(1, out[0]);
        Assert.assertEquals(2, out[1]);
        Assert.assertEquals(3, out[2]);
        Assert.assertEquals(4, out[3]);
    }

}
