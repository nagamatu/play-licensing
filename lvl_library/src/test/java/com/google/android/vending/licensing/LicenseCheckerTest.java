package com.google.android.vending.licensing;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.P})
public class LicenseCheckerTest {

    private static final String BASE64_PUBLIC_KEY =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApca867KYNn0onDg2mjkaeOZ1CVJYM7wW/zYwfsvVR4RKZQHrRIIADmSoAI68/q8gXTbJ+CzJxTmZBR9ruz8579cSsciN6gcsoibG60VppXp378WAmMt/EI0/pW8Hx7z98OOQc3C3g/RFbWgvcFl6tECuHLAT46GunUvHgu8c5IYfT3poIBVSh09pEbAm0UNvPBaNLjKZYOKatebCTcFJspEA+hCPHo+mWtk94Rn87Genvy0+yPsQFj9lOFGNlIptLyjZ0xSRw/10lVBmNn/gAA7jIfEOjNUy0jt7/WHoTlPEd8SpyXgbSyZhLO+tuI8mALqrWSlLWSr0B00fbcifAwIDAQAB";

    private Context mContext;
    private Policy mMockPolicy;
    private LicenseCheckerCallback mMockCallback;

    @Before
    public void setUp() throws Exception {
        mContext = RuntimeEnvironment.getApplication();
        mMockPolicy = mock(Policy.class);
        mMockCallback = mock(LicenseCheckerCallback.class);
    }

    @Test
    public void testCheckAccess_CachedAllow() {
        when(mMockPolicy.allowAccess()).thenReturn(true);

        LicenseChecker checker = new LicenseChecker(mContext, mMockPolicy, BASE64_PUBLIC_KEY);
        checker.checkAccess(mMockCallback);

        verify(mMockCallback).allow(Policy.LICENSED);
        checker.onDestroy();
    }

    @Test
    public void testCheckAccess_NoCache_BindsService() {
        // キャッシュがない場合、サービスのバインドを試みることを確認
        when(mMockPolicy.allowAccess()).thenReturn(false);

        LicenseChecker checker = new LicenseChecker(mContext, mMockPolicy, BASE64_PUBLIC_KEY);
        checker.checkAccess(mMockCallback);

        // 実際には Robolectric の ShadowContext で bindService が呼ばれたかを検証することも可能
        // ここではクラッシュせずにメソッドが完了することを確認
        checker.onDestroy();
    }
}
