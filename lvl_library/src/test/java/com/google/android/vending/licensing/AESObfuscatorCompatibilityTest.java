package com.google.android.vending.licensing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import android.os.Build;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;

import java.util.Arrays;
import java.util.Collection;

@RunWith(ParameterizedRobolectricTestRunner.class)
public class AESObfuscatorCompatibilityTest {

    private static final byte[] SALT = new byte[]{
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20
    };
    private static final String APPLICATION_ID = "com.example.app";
    private static final String DEVICE_ID = "unique_device_id";
    private static final String TEST_DATA = "Sensitive License Information 12345";
    private static final String TEST_KEY = "licensing_key";

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private final int sdkLevel;

    public AESObfuscatorCompatibilityTest(int sdkLevel) {
        this.sdkLevel = sdkLevel;
    }

    @ParameterizedRobolectricTestRunner.Parameters(name = "SDK Level: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {Build.VERSION_CODES.O},      // 26
                {Build.VERSION_CODES.P},      // 28
                {Build.VERSION_CODES.Q},      // 29
                {Build.VERSION_CODES.R},      // 30
                {Build.VERSION_CODES.S},      // 31
                {Build.VERSION_CODES.TIRAMISU}, // 33
                {Build.VERSION_CODES.UPSIDE_DOWN_CAKE} // 34
        });
    }

    @Test
    public void testObfuscateUnobfuscate_Success() throws Exception {
        AESObfuscator obfuscator = new AESObfuscator(SALT, APPLICATION_ID, DEVICE_ID);
        
        String obfuscated = obfuscator.obfuscate(TEST_DATA, TEST_KEY);
        assertNotEquals(TEST_DATA, obfuscated);
        
        String unobfuscated = obfuscator.unobfuscate(obfuscated, TEST_KEY);
        assertEquals(TEST_DATA, unobfuscated);
    }

    @Test(expected = ValidationException.class)
    public void testUnobfuscate_WithWrongKey_ThrowsException() throws Exception {
        AESObfuscator obfuscator = new AESObfuscator(SALT, APPLICATION_ID, DEVICE_ID);
        String obfuscated = obfuscator.obfuscate(TEST_DATA, TEST_KEY);
        
        obfuscator.unobfuscate(obfuscated, "wrong_key");
    }

    @Test(expected = ValidationException.class)
    public void testUnobfuscate_WithWrongDevice_ThrowsException() throws Exception {
        AESObfuscator obfuscator = new AESObfuscator(SALT, APPLICATION_ID, DEVICE_ID);
        String obfuscated = obfuscator.obfuscate(TEST_DATA, TEST_KEY);
        
        AESObfuscator differentDeviceObfuscator = new AESObfuscator(SALT, APPLICATION_ID, "other_device");
        
        differentDeviceObfuscator.unobfuscate(obfuscated, TEST_KEY);
    }
}
